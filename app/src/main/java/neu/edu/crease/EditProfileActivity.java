package neu.edu.crease;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import neu.edu.crease.Model.User;

public class EditProfileActivity extends AppCompatActivity {

    private CircleImageView profile_image_edit;
    private FirebaseUser currentUser;
    private Uri profile_uri_edit;
    private static final int GALLERY_PERMISSION_CODE = 1003;
    private static final int IMAGE_PICK_CODE = 1002;
    private EditText self_intro_edit;
    private EditText username_edit;
    private String self_intro_edited;
    private String username_edited;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profile_image_edit = findViewById(R.id.profile_image_edit);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        username_edit = findViewById(R.id.username_edit);
        self_intro_edit = findViewById(R.id.self_intro_edit);
        TextView save_edit = findViewById(R.id.save_edit);
        ImageView close_edit = findViewById(R.id.close_edit);
        Button upload_profile_image_edit = findViewById(R.id.upload_profile_image_edit);
        Button random_profile_image_edit = findViewById(R.id.random_profile_image_edit);

        // get user from database using profile id
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                // display the username and user profile image
                assert user != null;
                Glide.with(EditProfileActivity.this).load(Uri.parse(user.getProfileImage())).into(profile_image_edit);
                username_edit.setText(user.getUserName());
                if (!user.getUserSelfDescription().equals("")) {
                    self_intro_edit.setText(user.getUserSelfDescription());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        close_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        random_profile_image_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri loremPicsum = Uri.parse("https://picsum.photos/150/?random&t=" + new Date().getTime());
                Glide.with(v).load(loremPicsum).into(profile_image_edit);
                profile_uri_edit = loremPicsum;
            }
        });

        upload_profile_image_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if not have permission
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED) {
                    // request the permission
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, GALLERY_PERMISSION_CODE);
                }
                else {
                    // already got permission
                    pickImageFromGallery();
                }
            }
        });

        save_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                self_intro_edited = self_intro_edit.getText().toString();
                username_edited = username_edit.getText().toString();
                if (profile_image_edit != null){
                    updateProfile(profile_uri_edit, self_intro_edited, username_edited);
                } else {
                    Toast.makeText(EditProfileActivity.this, "Please choose a profile image before continue", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void pickImageFromGallery() {
        // picking image intent
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    public void updateProfile(final Uri profile_uri_edit, final String self_intro_edited, final String username_edited){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUser.getUid());
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://crease-reading-170ad.appspot.com");

        final StorageReference profileImagesRef = storageRef.child("profileImage/"+reference.getKey()+".jpg");

        profile_image_edit.setDrawingCacheEnabled(true);
        profile_image_edit.buildDrawingCache();
        Bitmap bitmap = profile_image_edit.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageTask uploadTask = profileImagesRef.putBytes(data);
        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isComplete()){
                    throw task.getException();
                }
                return profileImagesRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    String myUri = downloadUri.toString();

                    HashMap<String, Object> hashMap=new HashMap<>();
                    hashMap.put("profileImage", myUri);
                    hashMap.put("userSelfDescription", self_intro_edited);
                    hashMap.put("userName", username_edited);

                    reference.updateChildren(hashMap);

                } else {
                    Toast.makeText(EditProfileActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // handling permission result: granted or denied
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // granted
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImageFromGallery();
        }
        // denied
        else {
            Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
        }
    }

    // called when got the image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            profile_uri_edit = data.getData();
            profile_image_edit.setImageURI(profile_uri_edit);
        }

    }
}
