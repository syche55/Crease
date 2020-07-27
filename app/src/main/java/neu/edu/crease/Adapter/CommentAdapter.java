package neu.edu.crease.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import neu.edu.crease.Model.Comment;
import neu.edu.crease.Model.User;
import neu.edu.crease.R;
import neu.edu.crease.StartActivity;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private Context mContext;
    private List<Comment> mComment;
    private FirebaseUser firebaseUser;
    private String postID;

    public CommentAdapter(Context mContext, List<Comment> mComment, String postID) {
        this.mContext = mContext;
        this.mComment = mComment;
        this.postID = postID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment = mComment.get(position);

        holder.comment.setText(comment.getComment());
        // MATCH Comment model - Otherwise null string comment.getPublisherID()
        Log.e( "onBindViewHolder: comment", comment + " " + comment.getComment() + " " +comment.getPublisherID());
        getUserInfo(holder.imageProfile, holder.username, comment.getPublisherID());

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, StartActivity.class);
                intent.putExtra("publisherID", comment.getPublisherID());
                mContext.startActivity(intent);
            }
        });

        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, StartActivity.class);
                intent.putExtra("publisherID", comment.getPublisherID());
                mContext.startActivity(intent);
            }
        });

//        // if the user want to delete the comment
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                // if the user is the comment publisher
//                if (comment.getPublisherID().equals(firebaseUser.getUid())) {
//                    // make an alert message
//                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
//                    alertDialog.setTitle("Do you want to delete?");
//                    // if user choose no, then do not delete
//                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//                    // if user choose yes, then delete
//                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    FirebaseDatabase.getInstance().getReference("Comments").child(postID)
//                                            .child(comment.getCommentID())
//                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()) {
//                                                Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                    });
//                                    dialog.dismiss();
//                                }
//                            });
//                    alertDialog.show();
//                }
//                return true;
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageProfile;
        public TextView username, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.imageProfile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);

        }
    }

    private void getUserInfo(final ImageView imageProfile, final TextView username, final String publisherID){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                Glide.with(mContext).load(user.getProfileImage()).into(imageProfile);
                username.setText(user.getUserName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
