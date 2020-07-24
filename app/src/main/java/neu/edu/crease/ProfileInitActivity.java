package neu.edu.crease;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class ProfileInitActivity extends AppCompatActivity {
    private Button random_profile_image;
    private Button upload_profile_image;
    private ImageView profile_image_init;
    private EditText self_intro_input;
    private Button continue_button;
    private Uri profile_uri;
    private static final int GALLERY_PERMISSION_CODE = 1003;
    private String self_intro;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_init);

        upload_profile_image = findViewById(R.id.upload_profile_image);
        random_profile_image = findViewById(R.id.random_profile_image);
        profile_image_init = findViewById(R.id.profile_image_init);
        self_intro_input = findViewById(R.id.self_intro_input);
        continue_button = findViewById(R.id.continue_button);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                self_intro = self_intro_input.getText().toString();
                updateProfile(profile_uri,self_intro);
                Intent intent = new Intent(ProfileInitActivity.this, StartActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        random_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri loremPicsum = Uri.parse("https://picsum.photos/150/?random&t=" + new Date().getTime());
                Glide.with(v).load(loremPicsum).into(profile_image_init);
                profile_uri = loremPicsum;
            }

        });

//        upload_profile_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // if not have permission
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
//                            PackageManager.PERMISSION_DENIED) {
//                        // request the permission
//                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
//                        requestPermissions(permissions, GALLERY_PERMISSION_CODE);
//                    }
//                    else {
//                        // already got permission
//                        pickImageFromGallery();
//                    }
//                }
//                else {
//                    pickImageFromGallery();
//                }
//            }
//        });



    }

//    private void pickImageFromGallery() {
//        // picking image intent
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        startActivityForResult(intent, IMAGE_PICK_CODE);
//    }

    public void updateProfile(Uri profile_uri, String self_intro){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUser.getUid());

        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("profileImage", profile_uri.toString());
        hashMap.put("userSelfDescription", self_intro);

        reference.updateChildren(hashMap);
    }

}
