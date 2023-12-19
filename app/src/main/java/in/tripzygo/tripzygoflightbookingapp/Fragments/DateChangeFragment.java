package in.tripzygo.tripzygoflightbookingapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;

import java.text.NumberFormat;
import java.util.Locale;

import in.tripzygo.tripzygoflightbookingapp.R;

public class DateChangeFragment extends Fragment {

    JsonObject DateChange;
    TextView AirlineFeeTextView, TaxesTextView, policyInfoTextView, Departure, Arrival;
    String Dep, Arr;

    public DateChangeFragment() {
        // Required empty public constructor
    }

    public DateChangeFragment(JsonObject dateChange) {
        DateChange = dateChange;
    }

    public DateChangeFragment(JsonObject dateChange, String dep, String arr) {
        DateChange = dateChange;
        Dep = dep;
        Arr = arr;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_failed_booking, container, false);
        AirlineFeeTextView = view.findViewById(R.id.AirlineFeeDateChange);
        TaxesTextView = view.findViewById(R.id.TaxesDateChange);
        policyInfoTextView = view.findViewById(R.id.policyInfoDateChange);
        Departure = view.findViewById(R.id.DepartureCityTextDateChange);
        Arrival = view.findViewById(R.id.ArrivalCityTextDateChange);
        Departure.setText(Dep);
        Arrival.setText(Arr);
        System.out.println("DateChange = " + DateChange);
        if (DateChange.equals(new JsonObject())) {
            policyInfoTextView.setText("NA");
            AirlineFeeTextView.setText("NA");
            TaxesTextView.setText("NA");
        } else {
            if (DateChange.has("DEFAULT")) {
            if (DateChange.getAsJsonObject("DEFAULT").has("policyInfo")) {
                policyInfoTextView.setText(DateChange.getAsJsonObject("DEFAULT").get("policyInfo").getAsString());
            }
            int acf = DateChange.getAsJsonObject("DEFAULT").getAsJsonObject("fcs").get("ARF").getAsInt();
            int acft = DateChange.getAsJsonObject("DEFAULT").getAsJsonObject("fcs").get("ARFT").getAsInt();
            Locale locale = new Locale.Builder().setLanguage("en").setRegion("IN").build();
            NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
            String basecurrency = formatter.format(acf);
            String taxcurrency = formatter.format(acft);
            int basecentsIndex = basecurrency.lastIndexOf(".00");
            if (basecentsIndex != -1) {
                basecurrency = basecurrency.substring(0, basecentsIndex);
            }
            int taxcentsIndex = taxcurrency.lastIndexOf(".00");
            if (taxcentsIndex != -1) {
                taxcurrency = taxcurrency.substring(0, taxcentsIndex);
            }
            AirlineFeeTextView.setText(basecurrency);
            TaxesTextView.setText(taxcurrency);
        } else if (DateChange.has("AFTER_DEPARTURE") || DateChange.has("BEFORE_DEPARTURE")) {
                AirlineFeeTextView.setText("NA");
                TaxesTextView.setText("NA");
                String AFTER_DEPARTURE = "AFTER_DEPARTURE :- " + DateChange.getAsJsonObject("AFTER_DEPARTURE").get("policyInfo").getAsString();
                String BEFORE_DEPARTURE = "BEFORE_DEPARTURE :- " + DateChange.getAsJsonObject("BEFORE_DEPARTURE").get("policyInfo").getAsString() + " ₹" + DateChange.getAsJsonObject("BEFORE_DEPARTURE").get("amount").getAsString();
                policyInfoTextView.setText(AFTER_DEPARTURE + "\n" + BEFORE_DEPARTURE);
            }
        }
        return view;
    }
}