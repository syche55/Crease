package neu.edu.crease;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import neu.edu.crease.Adapter.CommentAdapter;
import neu.edu.crease.Model.Comment;
import neu.edu.crease.Model.User;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    public EditText addComment;
    public ImageView imageProfile;
    public TextView post;

    public String postID;
    public String publisherID;

    public FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        postID = intent.getExtras().getString("postID");
        publisherID = intent.getExtras().getString("publisherID");

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList, postID);
        recyclerView.setAdapter(commentAdapter);

        addComment = findViewById(R.id.addComment);
        imageProfile = findViewById(R.id.imageProfile);
        post = findViewById(R.id.post);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addComment.getText().toString().equals("")){
                    Toast.makeText(CommentActivity.this, "Can't send empty comment", Toast.LENGTH_SHORT).show();
                } else{
                    addComment();
                }
            }
        });

        // get cur user's profile image
        getProfileImage();
        // load comments
        readComment();

    }

    // add comment - match with this post
    private void addComment(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postID);

        String commentID = reference.push().getKey();

        HashMap<String, Object> map = new HashMap<>();
        map.put("comment", addComment.getText().toString());
        map.put("publisherID", firebaseUser.getUid());
        map.put("commentID", commentID);

        reference.child(commentID).setValue(map);
        addNotifications();
        addComment.setText("");
    }

    // if others post comments, send notification to post publisher
    private void addNotifications(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherID);
        if (!firebaseUser.getUid().equals(publisherID) ){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userID", firebaseUser.getUid());
        hashMap.put("comment_text", "commented: " + addComment.getText().toString());
        hashMap.put("postID", postID);
        hashMap.put("isPost", true);

        reference.push().setValue(hashMap);}
    }

    //get current user's profile image
    private void getProfileImage(){
        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                Glide.with(getApplicationContext()).load(user.getProfileImage()).into(imageProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // load this post's comments from db
    private void readComment(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for(DataSnapshot datasnapshot: snapshot.getChildren()){
                    Comment comment = datasnapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
