package neu.edu.crease.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import neu.edu.crease.Model.Post;
import neu.edu.crease.Model.User;
import neu.edu.crease.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public Context mContext;
    public List<Post> mPost;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // get current post
        final Post post = mPost.get(position);

        Glide.with(mContext).load(post.getPostImage()).into(holder.postImage);

        if(post.getPostContent().equals("")){
            holder.description.setVisibility(View.GONE);
        }else{
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getPostContent());
        }

        holder.bookName.setText(post.getPostTitle());
        holder.time.setText(post.getPostTime());

        // display publisher information
        publisherInfo(holder.imageProfile, holder.username, holder.publisher, post.getPostPublisher());

        // check like status
        isLiked(post.getPostID(), holder.like);
        nrLikes(holder.likes, post.getPostID());

        // click like button
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("likeOnClick", "clicked");
                if (holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostID())
                            .child(firebaseUser.getUid()).setValue(true);
                            updateUserBeingLiked(post);
                } else{
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostID())
                            .child(firebaseUser.getUid()).removeValue();
                            updateUserBeingLikedCancelled(post);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageProfile, postImage, like, comment, save;
        public TextView username, bookName, likes, publisher, description, comments, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.imageProfile);
            postImage = itemView.findViewById(R.id.postImage);
            bookName = itemView.findViewById(R.id.bookName);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            username = itemView.findViewById(R.id.username);
            likes = itemView.findViewById(R.id.likes);
            publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
//            comments = itemView.findViewById(R.id.comments);
            time = itemView.findViewById(R.id.time);
        }
    }

    private void isLiked(String postid, final ImageView imageView){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_liked_green);
                    imageView.setTag("liked");
                } else{
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag(("like"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void nrLikes (final TextView likes, final String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void publisherInfo(final ImageView imageProfile, final  TextView username, final TextView publisher, final String userId){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                Glide.with(mContext).load(user.getUserProfileImage()).into(imageProfile);
                username.setText(user.getUserName());
                publisher.setText(user.getUserName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // post liked, userBeingLiked count + 1
    public void updateUserBeingLiked(Post post){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(post.getPostPublisher()).child("userBeingLiked");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               Integer prevCount = snapshot.getValue(Integer.class);
               // in case data change will call this listener again
               reference.removeEventListener(this);
               reference.setValue(prevCount+1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // post unliked, userBeingLiked count -1
    public void updateUserBeingLikedCancelled(Post post){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(post.getPostPublisher()).child("userBeingLiked");
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
