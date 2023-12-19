package in.tripzygo.tripzygoflightbookingapp.Fragments;

import android.annotation.SuppressLint;
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

public class CancellationFragment extends Fragment {

    JsonObject Cancellation;
    String Dep, Arr;
    TextView AirlineFeeTextView, TaxesTextView, policyInfoTextView, Departure, Arrival;

    public CancellationFragment() {
        // Required empty public constructor
    }

    public CancellationFragment(JsonObject cancellation) {
        Cancellation = cancellation;
    }

    public CancellationFragment(JsonObject cancellation, String dep, String arr) {
        Cancellation = cancellation;
        Dep = dep;
        Arr = arr;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cancelled_booking, container, false);
        AirlineFeeTextView = view.findViewById(R.id.AirlineFeeCancellation);
        TaxesTextView = view.findViewById(R.id.TaxesCancellation);
        policyInfoTextView = view.findViewById(R.id.policyInfoCancellation);
        Departure = view.findViewById(R.id.DepartureCityTextCancellation);
        Arrival = view.findViewById(R.id.ArrivalCityTextCancellation);
        Departure.setText(Dep);
        Arrival.setText(Arr);
        System.out.println("Cancellation = " + Cancellation);
        if (Cancellation.equals(new JsonObject())) {
            policyInfoTextView.setText("NA");
            AirlineFeeTextView.setText("NA");
            TaxesTextView.setText("NA");
        } else {
            if (Cancellation.has("DEFAULT")) {
                if (Cancellation.getAsJsonObject("DEFAULT").has("policyInfo")) {
                    policyInfoTextView.setText(Cancellation.getAsJsonObject("DEFAULT").get("policyInfo").getAsString());
                }
                int acf = Cancellation.getAsJsonObject("DEFAULT").getAsJsonObject("fcs").get("ACF").getAsInt();
                int acft = Cancellation.getAsJsonObject("DEFAULT").getAsJsonObject("fcs").get("ACFT").getAsInt();
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
            } else if (Cancellation.has("AFTER_DEPARTURE") || Cancellation.has("BEFORE_DEPARTURE")) {
                AirlineFeeTextView.setText("NA");
                TaxesTextView.setText("NA");
                String AFTER_DEPARTURE = "AFTER_DEPARTURE :- " + Cancellation.getAsJsonObject("AFTER_DEPARTURE").get("policyInfo").getAsString();
                String BEFORE_DEPARTURE = "BEFORE_DEPARTURE :- " + Cancellation.getAsJsonObject("BEFORE_DEPARTURE").get("policyInfo").getAsString() + " ₹" + Cancellation.getAsJsonObject("BEFORE_DEPARTURE").get("amount").getAsString();
                policyInfoTextView.setText(AFTER_DEPARTURE + "\n" + BEFORE_DEPARTURE);
            }

        }
        return view;
    }
}