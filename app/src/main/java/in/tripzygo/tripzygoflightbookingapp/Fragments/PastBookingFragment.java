package in.tripzygo.tripzygoflightbookingapp.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.tripzygo.tripzygoflightbookingapp.BookingAdapter;
import in.tripzygo.tripzygoflightbookingapp.Modals.Booking;
import in.tripzygo.tripzygoflightbookingapp.R;

public class PastBookingFragment extends Fragment {
    List<Booking> bookingList;
    RecyclerView recyclerView;

    public PastBookingFragment(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    public PastBookingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_past_booking, container, false);
        recyclerView=view.findViewById(R.id.pastRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        BookingAdapter bookingAdapter=new BookingAdapter(bookingList,getContext());
        recyclerView.setAdapter(bookingAdapter);
        return view;
    }
}