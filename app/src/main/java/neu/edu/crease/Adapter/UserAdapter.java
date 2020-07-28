package neu.edu.crease.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import neu.edu.crease.Model.User;
import neu.edu.crease.R;
import neu.edu.crease.SearchUserActivity;
import neu.edu.crease.StartActivity;
import neu.edu.crease.ui.profile.ProfileFragment;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context mContext;
    private List<User> mUsers;
    private boolean isFragment;

    private FirebaseUser firebaseUser;



    public UserAdapter(Context mContext, List<User> mUsers, boolean isFragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = mUsers.get(position);
        holder.btn_follow.setVisibility(View.VISIBLE);
        holder.username_display.setText(user.getUserName());
        holder.user_self_description.setText(user.getUserSelfDescription());

        Glide.with(mContext).load(user.getProfileImage()).into(holder.user_profile_image);
        isFollowing(user.getUserID(), holder.btn_follow);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // when click a user, go to his / her profile
                // we do not redirect here, because now we're in the search activity, and redirect will let the fragment contain
                // the search activity container; but all fragments should be under the start activity container;
                // so we first redirect to the start activity, and then open the user profile there
                if(isFragment){
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", user.getUserID());
                    editor.apply();
                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container,
                            new ProfileFragment()).addToBackStack(null).commit();
                }else{
                    Intent intent = new Intent(mContext, StartActivity.class);
                    intent.putExtra("publisherID", user.getUserID());
                    mContext.startActivity(intent);
                }

            }
        });

        // only show follow button if the profile is not the current sign on user's
        if (user.getUserID().equals(firebaseUser.getUid())){
            holder.btn_follow.setVisibility(View.GONE);
        }

        holder.btn_follow.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (holder.btn_follow.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(user.getUserID()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUserID())
                            .child("Followers").child(firebaseUser.getUid()).setValue(true);
                    addNotifications(user.getUserID());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(user.getUserID()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUserID())
                            .child("Followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });
    }

    // display following button
    private void isFollowing(final String userid, final Button btn_follow){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener(){
            @SuppressLint("SetTextI18n")
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                if (dataSnapshot.child(userid).exists()){
                    btn_follow.setText("following");
                } else {
                    btn_follow.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // add following notification to database
    private void addNotifications(String userID){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userID);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userID", firebaseUser.getUid());
        hashMap.put("comment_text", "started following you");
        hashMap.put("postID", "");
        hashMap.put("isPost", false);

        reference.push().setValue(hashMap);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView user_profile_image;
        public TextView username_display;
        public TextView user_self_description;
        public Button btn_follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_profile_image = itemView.findViewById(R.id.user_profileImage);
            username_display = itemView.findViewById(R.id.username_display);
            user_self_description = itemView.findViewById(R.id.user_self_description);
            btn_follow = itemView.findViewById(R.id.btn_follow);
        }
    }
}
