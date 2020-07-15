package neu.edu.crease;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TakePhotoActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int GALLERY_PERMISSION_CODE = 1003;
    private static final int IMAGE_CAPTURE_CODE = 1;
    private static final int IMAGE_PICK_CODE = 1002;
    private Button mCaptureButton;
    private Button mGetFromGalleryButton;
    private ImageView mimageView;
    private Uri image_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        mimageView = findViewById(R.id.image_view);
        mCaptureButton = findViewById(R.id.capture_image_btn);
        mGetFromGalleryButton = findViewById(R.id.get_from_gallery_btn);

        // when user choose to take photo and click that button
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if not have permission
                if (checkSelfPermission(Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                PackageManager.PERMISSION_DENIED) {
                    // request the permission
                    String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, PERMISSION_CODE);
                }
                else {
                    // already got permission
                    openCamera();
                }
            }
        });

        // when user choose to get from gallery and click that button
        mGetFromGalleryButton.setOnClickListener(new View.OnClickListener() {
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
    }

    // when user get the permission, now come to pick from gallery
    private void pickImageFromGallery() {
        // picking image intent
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    // when user get the permission, now come to take photo
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("FAIL", "failed to create image file");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                image_uri = FileProvider.getUriForFile(this,
                        "neu.edu.crease.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
                startActivityForResult(takePictureIntent, IMAGE_CAPTURE_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    // handling permission result: granted or denied
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            // if get permission of taking photo
            case PERMISSION_CODE: {
                // granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                }
                // denied
                else {
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
            }

            // if get permission of picking from gallery
            case GALLERY_PERMISSION_CODE: {
                // granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery();
                }
                // denied
                else {
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // called when got the image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("In onActivityResult","");
        if (resultCode == RESULT_OK && requestCode == IMAGE_CAPTURE_CODE) {
            Log.d("set image uri", image_uri.toString());
            mimageView.setImageURI(image_uri);
        }

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            mimageView.setImageURI(data.getData());
        }
    }


}