package neu.edu.crease;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import neu.edu.crease.Adapter.TakePhotoAdapter;
import neu.edu.crease.ScrollActivity.ScrollLayoutManager;

import static neu.edu.crease.StartActivity.STOP_CHECKED;

public class TakePhotoActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int GALLERY_PERMISSION_CODE = 1003;
    private static final int IMAGE_CAPTURE_CODE = 1;
    private static final int IMAGE_PICK_CODE = 1002;
    private ImageView mimageView;
    private Uri image_uri;
    private Button mTakePhotoOk;
    private Dialog take_photo_tip_dialog;
    private Button rotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        // rotate image button
        rotate = findViewById(R.id.rotate);

        take_photo_tip_dialog = new Dialog(this);
        ImageView take_photo_tip = findViewById(R.id.take_photo_tip);

        RecyclerView recyclerView = findViewById(R.id.recycleView);
        recyclerView.setAdapter(new TakePhotoAdapter(TakePhotoActivity.this));
        recyclerView.setLayoutManager(new ScrollLayoutManager(TakePhotoActivity.this));

        recyclerView.smoothScrollToPosition(Integer.MAX_VALUE / 2);

        mimageView = findViewById(R.id.image_view);
        Button mCaptureButton = findViewById(R.id.capture_image_btn);
        Button mGetFromGalleryButton = findViewById(R.id.get_from_gallery_btn);
        mTakePhotoOk = findViewById(R.id.take_photo_ok);
        mTakePhotoOk.setVisibility(View.GONE);
        rotate.setVisibility(View.GONE);

        // if no show next time is not checked / first time open take photo
        if (!STOP_CHECKED){
            showDialog();
        }

        // when user chooses to take photo and click that button
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if not have permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
                else {
                    openCamera();
                }
            }
        });

        // when user chooses to get from gallery and click that button
        mGetFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if not have permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED) {
                        // request the permission
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions, GALLERY_PERMISSION_CODE);
                    }
                    else {
                        // already got permission
                        pickImageFromGallery();
                    }
                }
                else {
                    pickImageFromGallery();
                }
            }
        });

        // continue with the chosen photo
        mTakePhotoOk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (image_uri !=null){
                    connectToPostActivity(image_uri);
                }
            }


        });

        // user can click on the tip button to see the tip dialog again
        take_photo_tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    // when user choose to rotate the image
    public void clickRotate(View view) {
        try {
            // get the image
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri);
            // rotate
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            // create new image
            Bitmap resizedBitMap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                    matrix, true);
            if (resizedBitMap != bitmap && bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }

            // convert back to uri
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            resizedBitMap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), resizedBitMap, "Title", null);
            image_uri = Uri.parse(path);

            // display
            mimageView.setImageURI(image_uri);
        }
        catch (Exception e)
        {
            //handle exception
        }
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

        if (resultCode == RESULT_OK){
            if(requestCode == IMAGE_CAPTURE_CODE){
                mimageView.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CODE) {
                image_uri = data.getData();
                mimageView.setImageURI(image_uri);
            }
            mTakePhotoOk.setVisibility(View.VISIBLE);
            rotate.setVisibility(View.VISIBLE);
        }

    }

    // parse image to PostActivity
    private void connectToPostActivity(Uri imageUri){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClass(TakePhotoActivity.this,  PostActivity.class);
            intent.putExtra("imagePath", imageUri.toString());
            startActivity(intent);
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    // show tip dialog
    public void showDialog(){
        take_photo_tip_dialog.setContentView(R.layout.dialog_take_photo_tip);
        take_photo_tip_dialog.setTitle("Some tips");

        Button tip_no_more_reminder = take_photo_tip_dialog.findViewById(R.id.tip_no_more_reminder);
        Button tip_close = (Button) take_photo_tip_dialog.findViewById(R.id.tip_close);
        tip_close.setEnabled(true);

        if (STOP_CHECKED){
            tip_no_more_reminder.setVisibility(View.GONE);
        }
        tip_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                take_photo_tip_dialog.cancel();
            }
        });
        // check no more reminder
        tip_no_more_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                STOP_CHECKED = true;
                take_photo_tip_dialog.dismiss();
            }
        });

        Objects.requireNonNull(take_photo_tip_dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        take_photo_tip_dialog.show();
    }
}