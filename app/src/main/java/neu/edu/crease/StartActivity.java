package neu.edu.crease;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import neu.edu.crease.ui.explore.ExploreFragment;
import neu.edu.crease.ui.notification.NotificationFragment;
import neu.edu.crease.ui.home.HomeFragment;
import neu.edu.crease.ui.profile.ProfileFragment;
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

import com.google.firebase.messaging.RemoteMessage;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class StartActivity extends AppCompatActivity {

    BottomNavigationView navView;
    Fragment selectedFragment = null;

    Long notificationCounter;
    boolean stop = false;
    ActivityManager activityManager;
    public static String PACKAGE_NAME;

    // if take photo tip dialog has been checked as no show next time
    public static boolean STOP_CHECKED;

    // notification
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

        navView = (BottomNavigationView) findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navListener);

        // notification
        bottomNavigationMenuView = (BottomNavigationMenuView) navView.getChildAt(0);
        v = bottomNavigationMenuView.getChildAt(3);
        qb = new QBadgeView(StartActivity.this);
        qb.bindTarget(v).setBadgeNumber(0);

        STOP_CHECKED = false;

        // comment
        Bundle intent =  getIntent().getExtras();
        if(intent != null){
            String publisher = intent.getString("publisherID");
            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new ProfileFragment()).commit();
            String noti = intent.getString("menuFragment");
            if(noti != null && noti.equals("notificationMenuItem")){
                getSupportFragmentManager().beginTransaction().replace(R.id.container,new NotificationFragment()).commit();
            }
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();
        }

        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        new Thread(new AppStatus()).start();

        // check new notification while remember previous notification count
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                countChildrenOnFirstLogin = snapshot.getChildrenCount();
                reference.removeEventListener(this);
                reference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        notificationCounter ++;
                        if (notificationCounter > countChildrenOnFirstLogin){
                            qb.bindTarget(v).setBadgeText("!").setBadgeBackground(getDrawable(R.drawable.ic_notification_green)).setBadgeTextColor(-1);

                            // only show notification push when app in background
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
                            // when check notification, no new notification badge show
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

    // push notification
    private void notificationPhone(){
        CharSequence appName = getString(R.string.app_name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, appName, NotificationManager.IMPORTANCE_HIGH);
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // activity manager for app running in background check
            assert manager != null;
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        Context context = getApplicationContext();
        Intent intent = new Intent(context, StartActivity.class);
        intent.putExtra("menuFragment", "notificationMenuItem");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // click notification, direct to app
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        builder.setContentTitle(appName)
                .setSmallIcon(R.drawable.ic_notifications_green)
                .setLargeIcon(BitmapFactory.decodeResource(StartActivity.this
                        .getResources(),R.drawable.ic_icon))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentText("You have a new notification, check it out!")
                .setContentIntent(pIntent);

        manager.notify(999, builder.build());

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



