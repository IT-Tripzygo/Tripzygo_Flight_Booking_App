package in.tripzygo.tripzygoflightbookingapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.tripzygo.tripzygoflightbookingapp.Modals.Booking;
import in.tripzygo.tripzygoflightbookingapp.Modals.User;
import in.tripzygo.tripzygoflightbookingapp.R;
import in.tripzygo.tripzygoflightbookingapp.Tools.SharedPreference;
import in.tripzygo.tripzygoflightbookingapp.ViewPager2Adapter;


public class MyBookingFragment extends Fragment {
    DatabaseReference databaseReference;
    User user;

    public MyBookingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_booking, container, false);
        TabLayout tabLayout = view.findViewById(R.id.tablay);
        ViewPager2 viewPager2 = view.findViewById(R.id.pager);
        user = SharedPreference.loadUser(getContext());
        List<Booking> upcomingBookings = new ArrayList<>();
        List<Booking> pastBookings = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance("https://flightbookingapp-307f0-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Bookings");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Booking booking = dataSnapshot.getValue(Booking.class);
                    if (booking.getPhoneNo().substring(1).matches(user.getPhone_no().substring(1))) {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                        try {
                            Date dof = df.parse(booking.getDeparture());
                            Date now = Calendar.getInstance().getTime();
                            if (dof.after(now)) {
                                upcomingBookings.add(booking);
                            } else if (dof.before(now)) {
                                pastBookings.add(booking);
                            }
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                    tabLayout.addTab(tabLayout.newTab().setText("Upcoming"));
                    tabLayout.addTab(tabLayout.newTab().setText("Past"));
                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                    ArrayList<String> strings = new ArrayList<>();
                    strings.add("Upcoming");
                    strings.add("Past");
                    ViewPager2Adapter viewPagerAdapter = new ViewPager2Adapter(getChildFragmentManager(), getLifecycle(), tabLayout.getTabCount(), getContext(), upcomingBookings, pastBookings);
                    viewPager2.setAdapter(viewPagerAdapter);
                    TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
                        @Override
                        public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                            System.out.println("position = " + position);
                            System.out.println("strings = " + strings);
                            tab.setText(strings.get(position));
                        }
                    });
                    tabLayoutMediator.attach();
                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            viewPager2.setCurrentItem(tab.getPosition());
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {

                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("error.getDetails() = " + error.getDetails());
            }
        });

        return view;
    }
}