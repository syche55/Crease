package neu.edu.crease;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import neu.edu.crease.ui.add.AddFragment;
import neu.edu.crease.ui.explore.ExploreFragment;
import neu.edu.crease.ui.favorite.FavoriteFragment;
import neu.edu.crease.ui.home.HomeFragment;
import neu.edu.crease.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class StartActivity extends AppCompatActivity {

    BottomNavigationView navView;
    Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Initialize And Assign Variable
        navView = (BottomNavigationView) findViewById(R.id.nav_view);
        // Set Listener
        navView.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();
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
                        case R.id.navigation_favorite:
                            selectedFragment = new FavoriteFragment();
                            break;
                        case R.id.navigation_profile:
//                            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
//                            editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
//                            editor.apply();
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