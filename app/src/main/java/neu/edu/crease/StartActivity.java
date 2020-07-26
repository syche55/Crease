package neu.edu.crease;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import neu.edu.crease.ui.explore.ExploreFragment;
import neu.edu.crease.ui.notification.NotificationFragment;
import neu.edu.crease.ui.home.HomeFragment;
import neu.edu.crease.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

public class StartActivity extends AppCompatActivity {

    BottomNavigationView navView;
    Fragment selectedFragment = null;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);



        // Initialize And Assign Variable
        navView = (BottomNavigationView) findViewById(R.id.nav_view);
        // Set Listener
        navView.setOnNavigationItemSelectedListener(navListener);

        // comment
        Bundle intent =  getIntent().getExtras();
        if(intent != null){
            String publisher = intent.getString("publisherID");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new ProfileFragment()).commit();
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();
        }

        // if we come from the searching activity, then just redirect to the target user profile
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log.e("", "enter the  start activity to profile fragment part!");
            String value = extras.getString("key");
            //The key argument here must match that used in the other activity
            mContext = StartActivity.this;
            SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
            editor.putString("profileid", value);
            boolean successPut = editor.commit();
            Log.e("holder setOnClickListener ", String.valueOf(successPut));
            ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    new ProfileFragment()).addToBackStack(null).commit();
        }
        // if we first come in, then just load the home fragment
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();
        }


    }



    // Set bottom nav bar
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.navigation_explore:
                            selectedFragment = new ExploreFragment();
                            break;
                        case R.id.navigation_add:
                            selectedFragment = null;
                            startActivity(new Intent(StartActivity.this, TakePhotoActivity.class));
                            //selectedFragment = new AddFragment();

                            break;
                        case R.id.navigation_notification:
                            selectedFragment = new NotificationFragment();

                            break;
                        case R.id.navigation_profile:
                            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                            editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();
                            selectedFragment = new ProfileFragment();

                            break;
                        case R.id.navigation_home:
                            selectedFragment = new HomeFragment();

                            break;
                    }
                    if(selectedFragment != null){
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                selectedFragment).commit();
                    }
                    return true;
                }
            };


}



