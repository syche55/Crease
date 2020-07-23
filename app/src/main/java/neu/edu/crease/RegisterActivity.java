package neu.edu.crease;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import neu.edu.crease.Model.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText username_register, password_register, email_register;
    private Button register;
    private TextView login_text;
    private ImageView back;

    private FirebaseAuth auth;
    private DatabaseReference reference;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // init fields

        username_register = findViewById(R.id.username_register);
        email_register = findViewById(R.id.email_register);
        password_register = findViewById(R.id.password_register);
        register = findViewById(R.id.login_button);
        login_text = findViewById(R.id.login_text);

        auth = FirebaseAuth.getInstance();

        // when user click the login text, just direct them to login page
        login_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        // when user click the register button
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show the waiting message
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please wait...");
                pd.show();

                // get the info from user typing
                String str_username = username_register.getText().toString().trim();
                String str_email = email_register.getText().toString().trim();
                String str_password = password_register.getText().toString().trim();

                // if some info not correct
                if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
                    Toast.makeText(RegisterActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    if(pd.isShowing()){
                        pd.dismiss();
                    }
                }
                else if (str_password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password must have at least 6 characters!", Toast.LENGTH_LONG).show();
                    if(pd.isShowing()){
                        pd.dismiss();
                    }
                }
                else {
                    // all info correct, just go to register
                    Log.e("register user: ", "enter into register method");
                    register(str_username, str_email, str_password);
                }
            }
        });

        back = findViewById(R.id.register_back_to_main);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // register process
    private void register(final String username, String email, final String password) {
        // create user for firebase auth using email and password
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e("create user: ", "task success");
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            // create the user for firebase database (different from firebase auth)
                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                            Log.d("Register", "User id for new registered user is " + userid);

//                            HashMap<String, Object> map = new HashMap<>();
//                            map.put("id", userid);
//                            map.put("username", username.toLowerCase());

                            // set the fields for current user, and redirect to start page (code below could be replaced by the commented part)
                            User newUser = new User(userid, username);
                            Log.e("enter: ", "into firebase database user create");
                            reference.setValue(newUser);
//                            reference.push().setValue(newUser);
                            pd.dismiss();
                            Intent intent = new Intent(RegisterActivity.this, StartActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

//                            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    Log.e("create user: ", "reference set value");
//                                    if (task.isSuccessful()) {
//                                        Log.e("create user: ", "enter into new intent");
//                                        pd.dismiss();
//                                        Intent intent = new Intent(RegisterActivity.this, StartActivity.class);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(intent);
//                                    }
//                                }
//                            });
                        }
                        else {
                            // register failed
                            pd.dismiss();
                            Log.e("failed: ", "onComplete: Failed=" + task.getException().getMessage());
                            Toast.makeText(RegisterActivity.this, "Please try another email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}