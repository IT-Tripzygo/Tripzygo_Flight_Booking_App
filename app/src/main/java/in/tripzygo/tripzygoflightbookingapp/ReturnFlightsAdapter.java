package in.tripzygo.tripzygoflightbookingapp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class ReturnFlightsAdapter extends RecyclerView.Adapter<ReturnFlightsAdapter.ViewHolder> {

    List<FlightDetails> flightDetails;
    Context context;
    StorageReference storageReference;
    int checkedPosition = 0;
    String type;
    FragmentManager fragmentManager;
    private int selectedItemIndex = 0;
    JsonObject paxInFo;
    String id;
    FlightDetails firstFlightDetails;

    public ReturnFlightsAdapter(List<FlightDetails> flightDetails,FlightDetails firstFlightDetails, Context context, String type, FragmentManager fragmentManager,JsonObject paxInFo,String id) {
        this.flightDetails = flightDetails;
        this.context = context;
        this.type = type;
        this.fragmentManager = fragmentManager;
        this.paxInFo=paxInFo;
        this.id=id;
        this.firstFlightDetails=firstFlightDetails;
    }

    // Define the callback interface

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.return_flight_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FlightDetails flightDetail = flightDetails.get(position);
        holder.bind(flightDetail);
    }

    @Override
    public int getItemCount() {
        return flightDetails.size();
    }


    public FlightDetails getSelectedItemPriceId() {
        if (selectedItemIndex >= 0 && selectedItemIndex < flightDetails.size()) {
            return flightDetails.get(selectedItemIndex);
        }
        return null;
    }

    public int getSelectedItem() {
        if (selectedItemIndex >= 0 && selectedItemIndex < flightDetails.size()) {
            return Integer.parseInt(flightDetails.get(selectedItemIndex).getTotalPrice());
        }
        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView airlineName, departureTime,departureCity, arrivalTime,arrivalCity, totalPrice, totalStops, totalTime, classType, viewFares, FareType;
        ImageView airlineImage;
        RelativeLayout relativeLayout;
        ExpandableRelativeLayout expandableRelativeLayout;
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            airlineName = itemView.findViewById(R.id.airline_name);
            airlineImage = itemView.findViewById(R.id.airlineImage);
            departureTime = itemView.findViewById(R.id.DepartureText);
            departureCity = itemView.findViewById(R.id.DepartureCityText);
            arrivalCity = itemView.findViewById(R.id.ArrivalCityText);
            arrivalTime = itemView.findViewById(R.id.ArrivalText);
            totalPrice = itemView.findViewById(R.id.price);
            totalStops = itemView.findViewById(R.id.stop);
            totalTime = itemView.findViewById(R.id.time);
            relativeLayout = itemView.findViewById(R.id.returnRelative);
            FareType = itemView.findViewById(R.id.FareType);
            viewFares = itemView.findViewById(R.id.viewFares);
            expandableRelativeLayout = itemView.findViewById(R.id.expandableLayout_flight);
            recyclerView = itemView.findViewById(R.id.fareRecycler);
            classType = itemView.findViewById(R.id.classType);

        }

        void bind(FlightDetails flightDetail) {
            expandableRelativeLayout.collapse();
          /*  if (selectedItemIndex == -1) {
                relativeLayout.setBackgroundColor(context.getColor(R.color.white));
            } else {
                if (selectedItemIndex == getAdapterPosition()) {
                    relativeLayout.setBackgroundColor(context.getColor(R.color.lavender));
                } else {
                    relativeLayout.setBackgroundColor(context.getColor(R.color.white));
                }
            }*/
            storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference storageReference1 = storageReference.child("AirlineLogos").child(flightDetail.getAirlineImage() + ".png");
            storageReference1.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(context).load(uri).into(airlineImage);
            }).addOnFailureListener(e -> {
                System.out.println("storageReference1 = " + storageReference1);
                System.out.println("e.getMessage() = " + e.getMessage());
            });
            airlineName.setText(flightDetail.getAirlineName());
            Gson gson = new Gson();
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
            totalPrice.setText(currency);
            totalStops.setText(flightDetail.getTotalStops());
            departureTime.setText(flightDetail.getDepartureTime());
            arrivalTime.setText(flightDetail.getArrivalTime());
            int timeInMinutes = flightDetail.getTotalTime();
            long hours = timeInMinutes / 60;
            long minutes = timeInMinutes % 60;
            totalTime.setText(hours + "h " + minutes + "m");
            JsonArray sI = gson.fromJson(flightDetail.getSI(), new TypeToken<JsonArray>() {
            }.getType());
            String departureOfFirst = sI.get(0).getAsJsonObject().getAsJsonObject("da").get("cityCode").getAsString();
            String arrivalOfLast = sI.get(sI.size() - 1).getAsJsonObject().getAsJsonObject("aa").get("cityCode").getAsString();
            departureCity.setText(departureOfFirst);
            arrivalCity.setText(arrivalOfLast);
            itemView.setOnClickListener(this);
            if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").has("rT")) {
                ColorStateList colorStateList;
                if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 0) {
                    FareType.setText("NR");
                    colorStateList = ColorStateList.valueOf(context.getResources().getColor(R.color.red));
                    FareType.setBackgroundTintList(colorStateList);
                } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 1) {
                    FareType.setText("RF");
                    colorStateList = ColorStateList.valueOf(context.getResources().getColor(R.color.forestGreen));
                    FareType.setBackgroundTintList(colorStateList);
                } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 2) {
                    FareType.setText("PR");
                    colorStateList = ColorStateList.valueOf(context.getResources().getColor(R.color.green));
                    FareType.setBackgroundTintList(colorStateList);
                }
            }
            if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString().equalsIgnoreCase("ECONOMY")) {
                classType.setText("EC");
            } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString().equalsIgnoreCase("Premium Economy")) {
                classType.setText("PE");
            } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString().equalsIgnoreCase("Business") ){
                classType.setText("BS");
            } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString().equalsIgnoreCase("First") ){
                classType.setText("FC");
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            System.out.println("jsonObjects = " + jsonObjects);
            System.out.println("flightDetail1 = " + gson.toJson(flightDetail));
            FareAdapter fareAdapter = new FareAdapter(jsonObjects, context,flightDetail,firstFlightDetails, fragmentManager, type, paxInFo,true,id);
            recyclerView.setAdapter(fareAdapter);
            fareAdapter.notifyDataSetChanged();
        }

        @Override
        public void onClick(View view) {
            if (expandableRelativeLayout.isExpanded()) {
                expandableRelativeLayout.collapse();
                viewFares.setText("View Fares");
                FareType.setVisibility(View.VISIBLE);
                classType.setVisibility(View.VISIBLE);

            } else {
                expandableRelativeLayout.expand();
                viewFares.setText("Hide Fares");
                FareType.setVisibility(View.GONE);
                classType.setVisibility(View.GONE);

            }
            /*
            relativeLayout.setBackgroundColor(context.getColor(R.color.lavender));
            if (selectedItemIndex != getAdapterPosition()) {
                notifyItemChanged(selectedItemIndex);
                selectedItemIndex = getAdapterPosition();
            }
            int position = getAdapterPosition();
            // Ensure the position is valid
            if (position != RecyclerView.NO_POSITION) {
                // Retrieve the corresponding data item
                String price = flightDetails.get(position).getTotalPrice();
                System.out.println("price = " + price);
                // Trigger the onItemClick callback with the item data
            }
            selectedItemIndex = position;
            notifyDataSetChanged();

            // Call updateSum() method of the activity
            if (listener != null) {
                listener.onItemClicked(Integer.parseInt(flightDetails.get(position).getTotalPrice()));
                listener.updateSum(type);
            }

            */
        }
    }
}
