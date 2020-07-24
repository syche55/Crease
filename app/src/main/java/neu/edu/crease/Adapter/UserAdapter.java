package neu.edu.crease.Adapter;

import android.content.Context;
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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import neu.edu.crease.Model.User;
import neu.edu.crease.R;
import neu.edu.crease.ui.profile.ProfileFragment;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context mContext;
    private List<User> mUsers;

    private FirebaseUser firebaseUser;



    public UserAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers=mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = mUsers.get(position);
        holder.btn_follow.setVisibility(View.VISIBLE);
        holder.username_display.setText(user.getUserName());
        holder.user_self_description.setText(user.getUserSelfDescription());
//        Log.e("what is null", Uri.parse(user.getProfileImage()).toString());
        Glide.with(mContext).load(user.getProfileImage()).into(holder.user_profile_image);
        isFollowing(user.getUserID(), holder.btn_follow);

//        Log.e("mUsers.getUserID", user.getUserID());
//        Log.e("getUID", firebaseUser.getUid());
        if (user.getUserID().equals(firebaseUser.getUid())){
            holder.btn_follow.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", user.getUserID());
                boolean successPut = editor.commit();
                Log.e("holder setOnClickListener ", String.valueOf(successPut));

                // when click a user, go to his / her profile

                // create a frame layout
                FrameLayout fragmentLayout = new FrameLayout(mContext);

                // set the layout params to fill the activity
                fragmentLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                // set an id to the layout
                fragmentLayout.setId(R.id.fragmentLayout); // some positive integer
                // set the layout as Activity content
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.setContentView(fragmentLayout);
                // Finally , add the fragment
                ProfileFragment newFragment = new ProfileFragment();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentLayout, newFragment).commit();  // 1000 - is the id set for the container layout


            }
        });

        holder.btn_follow.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (holder.btn_follow.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(user.getUserID()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUserID())
                            .child("Followers").child(firebaseUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(user.getUserID()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUserID())
                            .child("Followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

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

    private void isFollowing(final String userid, final Button btn_follow){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener(){
            public void onDataChange(DataSnapshot dataSnapshot){
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
}
