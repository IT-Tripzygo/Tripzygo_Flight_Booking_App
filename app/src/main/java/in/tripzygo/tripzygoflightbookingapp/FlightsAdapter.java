package in.tripzygo.tripzygoflightbookingapp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.tripzygo.tripzygoflightbookingapp.Modals.FlightDetails;
import in.tripzygo.tripzygoflightbookingapp.Tools.Sort;

public class FlightsAdapter extends RecyclerView.Adapter<FlightsAdapter.ViewHolder> {
    List<FlightDetails> flightDetails;
    Context context;
    StorageReference storageReference;
    String type;
    JsonObject paxInFo;
    FragmentManager fragmentManager;

    public FlightsAdapter(String type, List<FlightDetails> flightDetails, Context context, JsonObject paxInFo) {
        this.flightDetails = flightDetails;
        this.context = context;
        this.paxInFo = paxInFo;
        this.type = type;
    }

    public FlightsAdapter(String type, List<FlightDetails> flightDetails, Context context, JsonObject paxInFo, FragmentManager fragmentManager) {
        this.flightDetails = flightDetails;
        this.context = context;
        this.type = type;
        this.paxInFo = paxInFo;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.flight_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.expandableRelativeLayout.collapse();
        FlightDetails flightDetail = flightDetails.get(position);
        holder.airlineName.setText(flightDetail.getAirlineName());
        holder.totalStops.setText(flightDetail.getTotalStops());
        holder.departureTime.setText(flightDetail.getDepartureTime());
        holder.arrivalTime.setText(flightDetail.getArrivalTime());
        int timeInMinutes = flightDetail.getTotalTime();
        long hours = timeInMinutes / 60;
        long minutes = timeInMinutes % 60;
        holder.totalTime.setText(hours + "h " + minutes + "m");
        Gson gson = new Gson();
        JsonArray sI = gson.fromJson(flightDetail.getSI(), new TypeToken<JsonArray>() {
        }.getType());
        String departureOfFirst = sI.get(0).getAsJsonObject().getAsJsonObject("da").get("cityCode").getAsString();
        String arrivalOfLast = sI.get(sI.size() - 1).getAsJsonObject().getAsJsonObject("aa").get("cityCode").getAsString();
        holder.departureCity.setText(departureOfFirst);
        holder.arrivalCity.setText(arrivalOfLast);
        JsonArray totalPriceList = gson.fromJson(flightDetail.getTotalPriceList(), new TypeToken<JsonArray>() {
        }.getType());
        List<JsonObject> jsonObjects = new ArrayList<>();
        for (JsonElement jsonElement : totalPriceList) {
            JsonObject jsonObject2 = jsonElement.getAsJsonObject();
            jsonObjects.add(jsonObject2);
        }
        jsonObjects.sort(new Sort());
        Locale locale = new Locale.Builder().setLanguage("en").setRegion("IN").build();
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        int d = jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").getAsJsonObject("fC").get("TF").getAsInt();
        String currency = formatter.format(d);
        int centsIndex = currency.lastIndexOf(".00");
        if (centsIndex != -1) {
            currency = currency.substring(0, centsIndex);
        }
        holder.totalPrice.setText(currency);
        if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").has("rT")) {
            ColorStateList colorStateList;
            if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 0) {
                holder.FareType.setText("NR");
                colorStateList = ColorStateList.valueOf(context.getResources().getColor(R.color.red));
                holder.FareType.setBackgroundTintList(colorStateList);
            } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 1) {
                holder.FareType.setText("RF");
                colorStateList = ColorStateList.valueOf(context.getResources().getColor(R.color.forestGreen));
                holder.FareType.setBackgroundTintList(colorStateList);
            } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 2) {
                holder.FareType.setText("PR");
                colorStateList = ColorStateList.valueOf(context.getResources().getColor(R.color.green));
                holder.FareType.setBackgroundTintList(colorStateList);
            }
        }
        if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString().equalsIgnoreCase("ECONOMY")) {
            holder.classType.setText("EC");
        } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString().equalsIgnoreCase("Premium Economy")) {
            holder.classType.setText("PE");
        } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString().equalsIgnoreCase("Business") ){
            holder.classType.setText("BS");
        } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString().equalsIgnoreCase("First") ){
            holder.classType.setText("FC");
        }
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference storageReference1 = storageReference.child("AirlineLogos").child(flightDetail.getAirlineImage() + ".png");
        storageReference1.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(context).load(uri).into(holder.airlineImage);
        }).addOnFailureListener(e -> {
            System.out.println("storageReference1 = " + storageReference1);
            System.out.println("e.getMessage() = " + e.getMessage());
        });
        holder.itemView.setOnClickListener(view -> {
            if (holder.expandableRelativeLayout.isExpanded()) {
                holder.expandableRelativeLayout.collapse();
                holder.viewFares.setText("View Fares");
                holder.FareType.setVisibility(View.VISIBLE);
                holder.classType.setVisibility(View.VISIBLE);

            } else {
                holder.expandableRelativeLayout.expand();
                holder.viewFares.setText("Hide Fares");
                holder.FareType.setVisibility(View.GONE);
                holder.classType.setVisibility(View.GONE);

            }
        });
        holder.viewFares.setOnClickListener(view -> {



        });
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        System.out.println("jsonObjects = " + jsonObjects);;
        FareAdapter fareAdapter=new FareAdapter(jsonObjects,context,flightDetail,fragmentManager,type,paxInFo,false);
        holder.recyclerView.setAdapter(fareAdapter);
        System.out.println("holder = " + holder.recyclerView.getAdapter().getItemCount());
        fareAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return flightDetails.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView airlineName, departureTime,departureCity, arrivalTime,arrivalCity, totalPrice, totalStops, totalTime, FareType,classType,viewFares;
        ImageView airlineImage;
        ExpandableRelativeLayout expandableRelativeLayout;
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            airlineName = itemView.findViewById(R.id.airline_name);
            airlineImage = itemView.findViewById(R.id.airlineImage);
            departureTime = itemView.findViewById(R.id.DepartureText);
            departureCity = itemView.findViewById(R.id.DepartureCityText);
            arrivalTime = itemView.findViewById(R.id.ArrivalText);
            arrivalCity = itemView.findViewById(R.id.ArrivalCityText);
            totalPrice = itemView.findViewById(R.id.price);
            totalStops = itemView.findViewById(R.id.stop);
            totalTime = itemView.findViewById(R.id.time);
            FareType = itemView.findViewById(R.id.FareType);
            classType = itemView.findViewById(R.id.classType);
            viewFares = itemView.findViewById(R.id.viewFares);
            expandableRelativeLayout=itemView.findViewById(R.id.expandableLayout_flight);
            recyclerView=itemView.findViewById(R.id.fareRecycler);
        }
    }
}
