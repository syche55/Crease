package neu.edu.crease;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.RemoteMessage;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StartActivity extends AppCompatActivity {

    BottomNavigationView navView;
    Fragment selectedFragment = null;
    Context mContext;
    Intent notificationIntent;
    Long notificationCounter;
    boolean stop = false;
    ActivityManager activityManager;

    public static boolean stop_checked;

    public static String PACKAGE_NAME;

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
        PACKAGE_NAME = getApplicationContext().getPackageName();

        notificationItem = (BottomNavigationItemView) findViewById(R.id.navigation_notification);

        // Initialize And Assign Variable
        navView = (BottomNavigationView) findViewById(R.id.nav_view);
        // Set Listener
        navView.setOnNavigationItemSelectedListener(navListener);


        bottomNavigationMenuView = (BottomNavigationMenuView) navView.getChildAt(0); // number of menu from left
        v = bottomNavigationMenuView.getChildAt(3);

        qb = new QBadgeView(StartActivity.this);
        qb.bindTarget(v).setBadgeNumber(0);

        stop_checked = false;

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

        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        new Thread(new AppStatus()).start();

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
                            Log.i( "onChildAdded: ", notificationCounter.toString());
                            if (!appOnForeground()){
                                notificationPhone();
                            }
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

//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w("TokenFailed", "getInstanceId failed", task.getException());
//                            return;
//                        }
//
//                        // Get new Instance ID token
//                        String token = task.getResult().getToken();
//
////                        // Log and toast
////                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d("Token is", token);
//                        Toast.makeText(StartActivity.this, token, Toast.LENGTH_SHORT).show();
//                    }
//                });



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

                            break;
                        case R.id.navigation_notification:
                            selectedFragment = new NotificationFragment();
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



    String CHANNEL_ID = "n";
    NotificationManager manager;

    private void notificationPhone(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // activity manager for app running in background check
            assert manager != null;
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        Context context = getApplicationContext();
        Intent intent = new Intent(context, StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        builder.setContentTitle("Crease")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(StartActivity.this
                        .getResources(),R.drawable.ic_tempura))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentText("You have a new notification, check it out!")
                .setContentIntent(pIntent);

        manager.notify(999, builder.build());

    }

    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            notificationPhone() ;
        }
    }



    // app running in background status check
    private class AppStatus implements Runnable {
        @Override
        public void run() {
            stop = false;
            while (!stop) {
                try {
                    if (appOnForeground()) {
                        // do nothing
                    } else {
                        // do nothing
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private boolean appOnForeground() {
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(PACKAGE_NAME) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
}



