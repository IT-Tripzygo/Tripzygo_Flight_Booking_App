package in.tripzygo.tripzygoflightbookingapp;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import in.tripzygo.tripzygoflightbookingapp.Modals.FlightDetails;

public class SortByArrivalHighToLow implements Comparator<FlightDetails> {
    // Used for sorting in ascending order of
    // roll number
    @Override
    public int compare(FlightDetails a, FlightDetails b) {
        Gson gson=new Gson();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
        JsonArray sI = gson.fromJson(a.getSI(), new TypeToken<JsonArray>() {
        }.getType());
        JsonArray sI2 = gson.fromJson(b.getSI(), new TypeToken<JsonArray>() {
        }.getType());
        String departureOfFirst = sI.get(sI.size()-1).getAsJsonObject().get("at").getAsString();
        String departureOfSecond = sI2.get(sI2.size()-1).getAsJsonObject().get("at").getAsString();
        Date date = new Date();
        Date date1 = new Date();
        try {
            date = df.parse(departureOfFirst);
            date1 = df.parse(departureOfSecond);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date1.compareTo(date);
    }
}
