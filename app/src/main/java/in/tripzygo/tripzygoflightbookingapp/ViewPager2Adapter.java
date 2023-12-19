package in.tripzygo.tripzygoflightbookingapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import in.tripzygo.tripzygoflightbookingapp.Fragments.PastBookingFragment;
import in.tripzygo.tripzygoflightbookingapp.Fragments.UpcomingBookingFragment;
import in.tripzygo.tripzygoflightbookingapp.Modals.Booking;

public class ViewPager2Adapter extends FragmentStateAdapter {
    int mNumOfTabs;
    Context context;
    List<Booking> upcomingBookings,pastBookings;

    public ViewPager2Adapter(@NonNull FragmentManager fragment, int NumOfTabs, Lifecycle lifecycle, Context context) {
        super(fragment, lifecycle);
        this.mNumOfTabs = NumOfTabs;
        this.context = context;
    }

    public ViewPager2Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, int mNumOfTabs, Context context, List<Booking> upcomingBookings, List<Booking> pastBookings) {
        super(fragmentManager, lifecycle);
        this.mNumOfTabs = mNumOfTabs;
        this.context = context;
        this.upcomingBookings = upcomingBookings;
        this.pastBookings = pastBookings;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                UpcomingBookingFragment upcomingBookingFragment = new UpcomingBookingFragment(upcomingBookings);
                return upcomingBookingFragment;
            case 1:
                PastBookingFragment pastBookingFragment = new PastBookingFragment(pastBookings);
                return pastBookingFragment;
            default:
                UpcomingBookingFragment upcomingBookingFragment1 = new UpcomingBookingFragment(upcomingBookings);
                return upcomingBookingFragment1;
        }
    }

    @Override
    public int getItemCount() {
        return mNumOfTabs;
    }
}
