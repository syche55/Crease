package neu.edu.crease.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import neu.edu.crease.CommentActivity;
import neu.edu.crease.Model.Post;
import neu.edu.crease.Model.User;
import neu.edu.crease.R;
import neu.edu.crease.ui.postDetail.PostDetailFragment;
import neu.edu.crease.ui.profile.ProfileFragment;

import java.util.List;
import java.util.UUID;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public Context mContext;
    public List<Post> mPost;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @Override
    public long getItemId(int position) {
        return UUID.nameUUIDFromBytes(mPost.get(position).getPostID().getBytes()).getMostSignificantBits();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Log.e("BindView", "Binding " + position + "th post with title " + mPost.get(position).getPostTitle());
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
        publisherInfo(holder.imageProfile, holder.username, post.getPostPublisher(), position);

        // check like status
        isLiked(post.getPostID(), holder.like);
        postLikesDisplay(holder.likes, post.getPostID());

        // get comments
        getComments(post.getPostID(), holder.comments);

        // click save button
        isSaved(post.getPostID(), holder.save);

        // if user clicks the profile image of a post, then direct to the publisher's profile
        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPostPublisher());
                editor.apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new ProfileFragment()).addToBackStack(null).commit();
            }
        });

        // if user clicks the publisher useername of a post, then direct to the publisher's profile
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPostPublisher());
                editor.apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new ProfileFragment()).addToBackStack(null).commit();
            }
        });

//        // if user clicks the publisher of a post, then direct to the publisher's profile
//        holder.publisher.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
//                editor.putString("profileid", post.getPostPublisher());
//                editor.apply();
//
//                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container,
//                        new ProfileFragment()).addToBackStack(null).commit();
//            }
//        });

        // if user clicks the image of a post, then direct to the detail of that post
        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("", "you clicked the post image in post adapter!");
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postID", post.getPostID());
                boolean successPut = editor.commit();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new PostDetailFragment()).addToBackStack(null).commit();
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.save.getTag().equals("save")){
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

        // click comment button
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postID", post.getPostID());
                intent.putExtra("publisherID", post.getPostPublisher());
                mContext.startActivity(intent);
            }
        });

        // click comments text - see more comments or send comments
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postID", post.getPostID());
                intent.putExtra("publisherID", post.getPostPublisher());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageProfile, postImage, like, comment, save;
        public TextView username, bookName, likes, description, comments, time;

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
//            publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
            comments = itemView.findViewById(R.id.comments);
            time = itemView.findViewById(R.id.time);
        }
    }

    // comments
    private void getComments(String postID, final TextView comments){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postID);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // set to invisible later
                comments.setText("View all " + snapshot.getChildrenCount() + " comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    private void postLikesDisplay(final TextView likes, final String postid){
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
    private void publisherInfo(final ImageView imageProfile, final  TextView username, final String userId, final int position){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                Glide.with(mContext).load(user.getProfileImage()).into(imageProfile);
                username.setText(user.getUserName());
//                publisher.setText(user.getUserName());
                Log.e("Publisher:", "postion: " + position + " user: " + user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isSaved(final String postid, final ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.ic_saved);
                    imageView.setTag("saved");
                } else{
                    imageView.setImageResource(R.drawable.ic_save);
                    imageView.setTag("save");
                }
                reference.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // post liked, userBeingLiked count + 1
    public void updateUserBeingLiked(Post post){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(post.getPostPublisher()).child("userBeingLiked");
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
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(post.getPostPublisher()).child("userBeingLiked");
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
