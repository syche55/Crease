package neu.edu.crease.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import neu.edu.crease.Model.Post;
import neu.edu.crease.Model.User;
import neu.edu.crease.R;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ViewHolder>{
    public Context mContext;
    public List<Post> mPost;

    private FirebaseUser firebaseUser;

    public ExploreAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.explore_item, parent, false);
        return new ExploreAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPost.get(position);

        Glide.with(mContext).load(post.getPostImage()).into(holder.postImage);

        if(post.getPostContent().equals("")){
            holder.description.setVisibility(View.GONE);
        }else{
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getPostContent());
        }

        holder.bookName.setText(post.getPostTitle());
        // Update - set to # of being saved
        if(post.getPostBeingSaved().equals(0)){
            holder.save.setText("");
        }else{
            holder.save.setText(String.valueOf(post.getPostBeingSaved()));
        }

        publisherInfo(holder.imageProfile, holder.username, post.getPostPublisher());

        isSaved(post.getPostID(), holder.saveBtn, holder.saveIcon);
        holder.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.saveBtn.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostID()).setValue(true);
                    updatePostBeingSaved(post);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostID()).removeValue();
                    updatePostBeingSavedCancelled(post);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageProfile, postImage, saveIcon;
        public TextView username, bookName, description, save, saveContext;
        public LinearLayout saveBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.explore_user_profile_image);
            username = itemView.findViewById(R.id.explore_username);

            postImage = itemView.findViewById(R.id.explore_post_img);
            bookName = itemView.findViewById(R.id.explore_book_title);
            description = itemView.findViewById(R.id.explore_post_context);
            saveContext = itemView.findViewById(R.id.explore_save_context);
            saveIcon = itemView.findViewById(R.id.explore_save_icon);
            saveBtn = itemView.findViewById(R.id.explore_save_button);
            save = itemView.findViewById(R.id.explore_save_count);
        }
    }

    private void publisherInfo(final ImageView imageProfile, final  TextView username, final String userId){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                Glide.with(mContext).load(user.getUserProfileImage()).into(imageProfile);
                username.setText(user.getUserName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isSaved(final String postid, final LinearLayout linearLayout, final ImageView saveIcon) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postid).exists()){
                    saveIcon.setImageResource(R.drawable.ic_saved);
                    //linearLayout.setBackgroundResource(R.drawable.explore_saved_button);
                    linearLayout.setTag("saved");
                } else{
                    saveIcon.setImageResource(R.drawable.ic_save);
                    //linearLayout.setBackgroundResource(R.drawable.explore_save_button);
                    linearLayout.setTag("save");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void updatePostBeingSaved(Post post){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts")
                .child(post.getPostID()).child("postBeingSaved");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer prevCount = snapshot.getValue(Integer.class);
                reference.removeEventListener(this);
                reference.setValue(prevCount+1);
                Log.e("prevCount", prevCount+"");
                Log.e("currentCount", ""+snapshot.getValue(Integer.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void updatePostBeingSavedCancelled(Post post){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts")
                .child(post.getPostID()).child("postBeingSaved");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer prevCount = snapshot.getValue(Integer.class);
                reference.removeEventListener(this);
                reference.setValue(prevCount-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
