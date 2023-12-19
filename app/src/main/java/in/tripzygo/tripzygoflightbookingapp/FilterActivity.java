package in.tripzygo.tripzygoflightbookingapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import in.tripzygo.tripzygoflightbookingapp.Modals.FlightDetails;
import it.mirko.rangeseekbar.RangeSeekBar;


public class FilterActivity extends BottomSheetDialogFragment {
    RecyclerView filterRV, filterValuesRV;
    RangeSeekBar priceSeekBar;
    LinearLayout filterValuesContainer;
    TextView rate1, rate2;
    RelativeLayout relativeLayout;
    List<FlightDetails> flightDetailsList=new ArrayList<>();

    public FilterActivity(List<FlightDetails> flightDetailsList) {
        this.flightDetailsList = flightDetailsList;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        BottomSheetBehavior<FrameLayout> bottomSheetBehavior = dialog.getBehavior();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        // Do something with your dialog like setContentView() or whatever
        return dialog;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.activity_filter, null);
        dialog.setContentView(view);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_filter, container,
                false);
        initControls(view);
        return view;
    }

    private void initControls(View view) {
        filterValuesContainer = view.findViewById(R.id.filterValuesContainer);
        filterRV = filterValuesContainer.findViewById(R.id.filterRV);
        relativeLayout = filterValuesContainer.findViewById(R.id.relative2);
        priceSeekBar = filterValuesContainer.findViewById(R.id.rangeSeekBar);
        filterValuesRV = filterValuesContainer.findViewById(R.id.filterValuesRV);
        rate1 = view.findViewById(R.id.start);
        rate2 = view.findViewById(R.id.end);
        filterRV.setLayoutManager(new LinearLayoutManager(getContext()));
        filterValuesRV.setLayoutManager(new LinearLayoutManager(getContext()));
        int min=Integer.parseInt(flightDetailsList.get(0).getTotalPrice());
        int max=Integer.parseInt(flightDetailsList.get(flightDetailsList.size() - 1).getTotalPrice());
        priceSeekBar.setStartProgress(min); // default is 0
        priceSeekBar.setEndProgress(max); // default is 50
        priceSeekBar.setMinDifference(1500); // default is 20
        priceSeekBar.setMax(max);
        rate1.setText("₹ " + priceSeekBar.getStartProgress());
        rate2.setText("₹ " + priceSeekBar.getEndProgress());

        List<String> stops = Arrays.asList("Non Stop", "1+ Stop");
        if (!Preferences.filters.containsKey(Filter.INDEX_STOPS)) {
            Preferences.filters.put(Filter.INDEX_STOPS, new Filter("Stops", stops, new ArrayList()));
        }
        List<String> departureTimes = Arrays.asList("Early Morning (12:00 am to 06:00 am)", "Morning (06:00 am to 12:00 pm)", "Noon (12:00 pm to 06:00 pm)", "Night (06:00 pm to 11:59 pm)");
        if (!Preferences.filters.containsKey(Filter.INDEX_DEPARTURE_TIME)) {
            Preferences.filters.put(Filter.INDEX_DEPARTURE_TIME, new Filter("Departure Time", departureTimes, new ArrayList()));
        }
        List<String> arrivalTimes = Arrays.asList("Early Morning (12:00 am to 06:00 am)", "Morning (06:00 am to 12:00 pm)", "Noon (12:00 pm to 06:00 pm)", "Night (06:00 pm to 11:59 pm)");
        if (!Preferences.filters.containsKey(Filter.INDEX_ARRIVAL_TIME)) {
            Preferences.filters.put(Filter.INDEX_ARRIVAL_TIME, new Filter("Arrival Time", arrivalTimes, new ArrayList()));
        }
        List<String> prices = Arrays.asList();
        if (!Preferences.filters.containsKey(Filter.INDEX_PRICE)) {
            Preferences.filters.put(Filter.INDEX_PRICE, new Filter("Price", prices, new ArrayList()));
        }
        List<String> airlineNames = Arrays.asList("Early Morning (12:00 am to 06:00 am)", "Morning (06:00 am to 12:00 pm)", "Noon (12:00 pm to 06:00 pm)", "Night (06:00 pm to 11:59 pm)");
        if (!Preferences.filters.containsKey(Filter.INDEX_AIRLINES)) {
            Preferences.filters.put(Filter.INDEX_AIRLINES, new Filter("Airlines", airlineNames, new ArrayList()));
        }
        FilterAdapter filterAdapter = new FilterAdapter(Preferences.filters, filterValuesRV, filterRV, relativeLayout,priceSeekBar, filterValuesContainer);
        filterRV.setAdapter(filterAdapter);

        Button clearB = view.findViewById(R.id.clearB);
        clearB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.filters.get(Filter.INDEX_STOPS).setSelected(new ArrayList());
                Preferences.filters.get(Filter.INDEX_DEPARTURE_TIME).setSelected(new ArrayList());
                Preferences.filters.get(Filter.INDEX_ARRIVAL_TIME).setSelected(new ArrayList());
                /*startActivity(new Intent(FilterActivity.this, ViewItemsActivity.class));
                finish();*/
            }
        });

        Button applyB = view.findViewById(R.id.applyB);
        applyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                /*startActivity(new Intent(FilterActivity.this, ViewItemsActivity.class));
                finish();*/
            }
        });
    }

    private void showFilterValuesRV() {
        filterValuesContainer.removeAllViews();
        filterValuesContainer.addView(filterValuesRV);
        filterValuesRV.setVisibility(View.VISIBLE);
        priceSeekBar.setVisibility(View.GONE);
    }

    private void showSeekBar() {
        filterValuesContainer.removeAllViews();
        filterValuesContainer.addView(priceSeekBar);
        priceSeekBar.setVisibility(View.VISIBLE);
        filterValuesRV.setVisibility(View.GONE);
        // You can customize SeekBar attributes here
    }
}