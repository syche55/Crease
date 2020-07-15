package neu.edu.crease;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import neu.edu.crease.Model.Post;

public class PostActivity extends AppCompatActivity {
    private Uri imageUri;
    private String myUri = "";
    private StorageTask uploadTask;
    private StorageReference storageReference;

    private ImageView edit_post_photo_add, edit_post_cancel, edit_post_reminder;
    private EditText edit_post_enter_title, edit_post_description;
    private Button edit_post_submit_button;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_post);

        edit_post_photo_add = findViewById(R.id.edit_post_photo_add);
        edit_post_enter_title=findViewById(R.id.edit_post_enter_title);
        edit_post_description=findViewById(R.id.edit_post_description);
        edit_post_cancel=findViewById(R.id.edit_post_cancel);
        //edit_post_reminder=findViewById(R.id.edit_post_reminder);
        edit_post_submit_button = findViewById(R.id.edit_post_submit_button);


        storageReference = FirebaseStorage.getInstance().getReference("posts");

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("imagePath")) {
            Log.e("get image Uri ", ""+Uri.parse(extras.getString("imagePath")));
            imageUri= Uri.parse(extras.getString("imagePath"));
            edit_post_photo_add.setImageURI(imageUri);
        }

        edit_post_cancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, StartActivity.class));
                finish();
            }
        });


        edit_post_submit_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                uploadPost();
            }
        });

       // CropImage.activity().setAspectRatio(1,1).start(PostActivity.this);
    }

    private void uploadPost(){
        // could add ProgressBar here TODO
        if (imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getMimeTypeFromUrl(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isComplete()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        myUri = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                        String postID = reference.push().getKey();

//                        Post newPost = new Post(postID, FirebaseAuth.getInstance().getCurrentUser().getUid(),
//                                myUri, edit_post_enter_title.getText().toString(), edit_post_description.getText().toString());

                        Post newPost = new Post(postID, "testUser",
                                myUri, edit_post_enter_title.getText().toString(), edit_post_description.getText().toString());

                        reference.child(postID).setValue(newPost);

                        startActivity(new Intent(PostActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(PostActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(PostActivity.this, "Image not selected!", Toast.LENGTH_SHORT).show();
        }


    }


    private String getMimeTypeFromUrl(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}
