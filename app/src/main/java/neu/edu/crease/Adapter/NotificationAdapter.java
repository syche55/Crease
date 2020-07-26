package neu.edu.crease.Adapter;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import neu.edu.crease.Model.Post;
import neu.edu.crease.Model.User;
import neu.edu.crease.R;
import neu.edu.crease.ui.postDetail.PostDetailFragment;
import neu.edu.crease.ui.profile.ProfileFragment;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    private Context mContext;
    private List<neu.edu.crease.Model.Notification> mNotification;

    public NotificationAdapter(Context mContext, List<neu.edu.crease.Model.Notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);

        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final neu.edu.crease.Model.Notification notification = mNotification.get(position);
        holder.comment_notification.setText(notification.getComment_text());
        getUserInfo(holder.image_profile_notification, holder.username_notification, notification.getUserID());

        if (notification.getIsPost()){
            holder.post_image_notification.setVisibility(View.VISIBLE);
            getPostImage(holder.post_image_notification, notification.getPostID());
        } else {
            holder.post_image_notification.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Is this a post?", notification.getIsPost()+" "+notification.getComment_text());
                if (notification.getIsPost()){
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("postID", notification.getPostID());
                    Log.e("notification.getPostID", notification.getPostID());
                    editor.apply();

//                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container, new PostDetailFragment()).commit();

                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container,
                            new PostDetailFragment()).addToBackStack(null).commit();

                } else {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", notification.getUserID());
                    editor.apply();

                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).addToBackStack(null).commit();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView image_profile_notification, post_image_notification;
        public TextView username_notification, comment_notification;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile_notification=itemView.findViewById(R.id.image_profile_notification);
            post_image_notification=itemView.findViewById(R.id.post_image_notification);
            username_notification=itemView.findViewById(R.id.username_notification);
            comment_notification=itemView.findViewById(R.id.comment_notification);

        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherID){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getProfileImage()).into(imageView);
                username.setText((user.getUserName()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostImage(final ImageView imageView, String postID){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                Glide.with(mContext).load(post.getPostImage()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
