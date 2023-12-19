package in.tripzygo.tripzygoflightbookingapp;

import java.util.Comparator;

import in.tripzygo.tripzygoflightbookingapp.Modals.FlightDetails;

public class SortByPrice implements Comparator<FlightDetails> {
    // Used for sorting in ascending order of
    // roll number
    @Override
    public int compare(FlightDetails a, FlightDetails b) {
        return Integer.parseInt(a.getTotalPrice()) - Integer.parseInt(b.getTotalPrice());
    }
}
