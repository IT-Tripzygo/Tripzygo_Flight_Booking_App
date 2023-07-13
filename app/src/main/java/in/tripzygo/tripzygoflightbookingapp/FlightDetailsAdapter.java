package in.tripzygo.tripzygoflightbookingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.tripzygo.tripzygoflightbookingapp.Modals.FlightDetails;
import in.tripzygo.tripzygoflightbookingapp.Tools.Sort;

public class FlightDetailsAdapter extends RecyclerView.Adapter<FlightDetailsAdapter.ViewHolder> {
    FlightDetails flightDetails;
    Context context;
    int size;

    public FlightDetailsAdapter(FlightDetails flightDetails, Context context, int size) {
        this.flightDetails = flightDetails;
        this.context = context;
        this.size = size;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.flight_detail_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Gson gson = new Gson();
        JsonArray sI = gson.fromJson(flightDetails.getSI(), new TypeToken<JsonArray>() {
        }.getType());
        JsonObject jsonObject = sI.get(position).getAsJsonObject();
        JsonArray totalPriceList = gson.fromJson(flightDetails.getTotalPriceList(), new TypeToken<JsonArray>() {
        }.getType());
        holder.airline_name.setText(jsonObject.getAsJsonObject("fD").getAsJsonObject("aI").get("name").getAsString() + " " + jsonObject.getAsJsonObject("fD").get("fN").getAsString());
        holder.DepartureCityCodeText.setText(jsonObject.getAsJsonObject("da").get("cityCode").getAsString());
        holder.DepartureAirportText.setText(jsonObject.getAsJsonObject("da").get("name").getAsString());
        if (jsonObject.getAsJsonObject("da").has("terminal")) {
            holder.DepartureTerminalText.setText(jsonObject.getAsJsonObject("da").get("terminal").getAsString());
        } else {
            holder.DepartureTerminalText.setText("-");
        }
        holder.ArrivalCityCodeText.setText(jsonObject.getAsJsonObject("aa").get("cityCode").getAsString());
        holder.ArrivalAirportText.setText(jsonObject.getAsJsonObject("aa").get("name").getAsString());
        if (jsonObject.getAsJsonObject("aa").has("terminal")) {
            holder.ArrivalTerminalText.setText(jsonObject.getAsJsonObject("aa").get("terminal").getAsString());
        } else {
            holder.ArrivalTerminalText.setText("-");
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
        SimpleDateFormat df1 = new SimpleDateFormat("E, dd MMM", Locale.getDefault());
        SimpleDateFormat df2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String departure = jsonObject.get("dt").getAsString();
        String arrival = jsonObject.get("at").getAsString();
        try {
            Date dof = df.parse(departure);
            Date aol = df.parse(arrival);
            String daTime = df2.format(dof);
            String aaTime = df2.format(aol);
            long d = dof.getTime();
            long a = aol.getTime();
            long t = a - d;
            long Seconds_difference = (t / (1000 * 60));

            long Minutes_difference = (t / (1000 * 60)) % 60;

            long Hours_difference = (t / (1000 * 60 * 60)) % 24;

            System.out.println(Hours_difference + " h " + Minutes_difference + " m");

            System.out.println("Seconds_difference = " + Seconds_difference);
            holder.timesTextView.setText(Hours_difference + "h " + Minutes_difference + "m");
            String dTime = df1.format(dof);
            String aTime = df1.format(aol);
            System.out.println("aTime = " + aTime + " dTime = " + dTime);
            holder.DepartureDateText.setText(dTime);
            holder.DepartureTimeText.setText(daTime);
            holder.ArrivalTimeText.setText(aaTime);
            holder.ArrivalDateText.setText(aTime);
            List<JsonObject> jsonObjects = new ArrayList<>();
            for (JsonElement jsonElement : totalPriceList) {
                JsonObject jsonObject2 = jsonElement.getAsJsonObject();
                jsonObjects.add(jsonObject2);
            }
            jsonObjects.sort(new Sort());
            if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").getAsJsonObject("bI").has("cB") || jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").getAsJsonObject("bI").has("iB")) {
                holder.cabin_baggage.setText("Cabin : " + jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").getAsJsonObject("bI").get("cB").getAsString());
                holder.checkIn_baggage.setText("Check-in : " + jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").getAsJsonObject("bI").get("iB").getAsString());
            } else {
                holder.cabin_baggage.setText("Cabin : -");
                holder.checkIn_baggage.setText("Check-in : -");
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (size == 1) {
            holder.layoverTextView.setVisibility(View.GONE);
        } else {
            System.out.println("size = " + size);
            System.out.println("jsonObject.get(\"cT\") = " + sI.get(0).getAsJsonObject().get("cT").getAsInt());
            if (jsonObject.has("cT")) {
                String city = jsonObject.getAsJsonObject("aa").get("city").getAsString();
                int minutes = jsonObject.get("cT").getAsInt();

                long Minutes_differences = minutes % 60;
                long Hours_differences = (minutes / 60) % 24;
                holder.layoverTextView.setText("Flight change in " + city + ". Layover of " + Hours_differences + "h " + Minutes_differences + "m");
            } else {
                holder.layoverTextView.setVisibility(View.GONE);
            }


        }

    }

    @Override
    public int getItemCount() {
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView airline_name, DepartureCityCodeText, DepartureTimeText, ArrivalTimeText, ArrivalCityCodeText, DepartureDateText,
                ArrivalDateText, DepartureAirportText, ArrivalAirportText, DepartureTerminalText, ArrivalTerminalText, cabin_baggage, checkIn_baggage, layoverTextView, timesTextView;
        ImageView airlineImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            airline_name = itemView.findViewById(R.id.airline_name_checkout);
            DepartureCityCodeText = itemView.findViewById(R.id.DepartureCityCodeText);
            DepartureTimeText = itemView.findViewById(R.id.DepartureTimeText);
            ArrivalTimeText = itemView.findViewById(R.id.ArrivalTimeText);
            ArrivalCityCodeText = itemView.findViewById(R.id.ArrivalCityCodeText);
            DepartureDateText = itemView.findViewById(R.id.DepartureDateText);
            ArrivalDateText = itemView.findViewById(R.id.ArrivalDateText);
            DepartureAirportText = itemView.findViewById(R.id.DepartureAirportText);
            ArrivalAirportText = itemView.findViewById(R.id.ArrivalAirportText);
            DepartureTerminalText = itemView.findViewById(R.id.DepartureTerminalText);
            ArrivalTerminalText = itemView.findViewById(R.id.ArrivalTerminalText);
            cabin_baggage = itemView.findViewById(R.id.cabin_baggage);
            checkIn_baggage = itemView.findViewById(R.id.checkIn_baggage);
            layoverTextView = itemView.findViewById(R.id.layoverText);
            airlineImage = itemView.findViewById(R.id.airlineImage);
            timesTextView = itemView.findViewById(R.id.timeText_Flight);
        }
    }
}
