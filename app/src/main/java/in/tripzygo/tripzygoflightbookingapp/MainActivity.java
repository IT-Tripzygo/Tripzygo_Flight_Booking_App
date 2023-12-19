package in.tripzygo.tripzygoflightbookingapp;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import in.tripzygo.tripzygoflightbookingapp.Fragments.HomeFragment;
import in.tripzygo.tripzygoflightbookingapp.Fragments.MyBookingFragment;
import in.tripzygo.tripzygoflightbookingapp.Fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    public static BottomNavigationView bottomNavigationView;
   public static MaterialToolbar materialToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        materialToolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        loadFragment(new HomeFragment());
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.navigation_booking:
                        selectedFragment = new MyBookingFragment();
                        break;
                    case R.id.navigation_profile:
                        selectedFragment = new ProfileFragment();
                        break;
                }
                loadFragment(selectedFragment);

                return true;
            }
        });


    }

    public  void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();
        if (fragment.getClass().getSimpleName().matches(HomeFragment.class.getSimpleName())) {
            materialToolbar.setTitle("Book Flights");
        } else if (fragment.getClass().getSimpleName().matches(MyBookingFragment.class.getSimpleName())) {
            materialToolbar.setTitle("My Bookings");
        } else if (fragment.getClass().getSimpleName().matches(ProfileFragment.class.getSimpleName())) {
            materialToolbar.setTitle("Profile");
        }
    }

}
