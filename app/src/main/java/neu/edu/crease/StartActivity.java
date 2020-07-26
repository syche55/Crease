package neu.edu.crease;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import neu.edu.crease.Model.Notification;
import neu.edu.crease.ui.explore.ExploreFragment;
import neu.edu.crease.ui.notification.NotificationFragment;
import neu.edu.crease.ui.home.HomeFragment;
import neu.edu.crease.ui.profile.ProfileFragment;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.annotation.SuppressLint;
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
import java.util.Collections;

public class StartActivity extends AppCompatActivity {

    BottomNavigationView navView;
    Fragment selectedFragment = null;
    Context mContext;
    Long notificationCounter;

    com.google.android.material.bottomnavigation.BottomNavigationItemView notificationItem;

    BottomNavigationMenuView bottomNavigationMenuView;
    View v;
    QBadgeView qb;

    Long countChildrenOnFirstLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        notificationCounter=0L;

        notificationItem = (BottomNavigationItemView) findViewById(R.id.navigation_notification);

        // Initialize And Assign Variable
        navView = (BottomNavigationView) findViewById(R.id.nav_view);
        // Set Listener
        navView.setOnNavigationItemSelectedListener(navListener);


        bottomNavigationMenuView = (BottomNavigationMenuView) navView.getChildAt(0); // number of menu from left
        v = bottomNavigationMenuView.getChildAt(3);

        qb = new QBadgeView(StartActivity.this);
        qb.bindTarget(v).setBadgeNumber(0);


        final Menu menu = navView.getMenu();

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

        // activity - fragment: set in
//        // if we come from the searching activity, then just redirect to the target user profile
//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            Log.e("", "enter the  start activity to profile fragment part!");
//            String value = extras.getString("key");
//            //The key argument here must match that used in the other activity
//            mContext = StartActivity.this;
//            SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
//            editor.putString("profileid", value);
//            boolean successPut = editor.commit();
//            Log.e("holder setOnClickListener ", String.valueOf(successPut));
//            ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container,
//                    new ProfileFragment()).addToBackStack(null).commit();
//        }
//        // if we first come in, then just load the home fragment
//        else {
//            getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();
//        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                countChildrenOnFirstLogin = snapshot.getChildrenCount();
//                notificationCounter = countChildrenOnFirstLogin;
                reference.removeEventListener(this);

                reference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        notificationCounter ++;
                        if (notificationCounter > countChildrenOnFirstLogin){
                            qb.bindTarget(v).setBadgeText("!").setBadgeBackground(getDrawable(R.drawable.ic_notification_green)).setBadgeTextColor(-1);

                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }


                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




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
                            //BottomMenuHelper.removeBadge(StartActivity.this, notificationItem);
                            qb.bindTarget(v).hide(false);


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



