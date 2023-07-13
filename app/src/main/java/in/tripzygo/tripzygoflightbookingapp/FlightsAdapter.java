package in.tripzygo.tripzygoflightbookingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.JsonObject;

import java.util.List;

import in.tripzygo.tripzygoflightbookingapp.Modals.FlightDetails;

public class FlightsAdapter extends RecyclerView.Adapter<FlightsAdapter.ViewHolder> {
    List<FlightDetails> flightDetails;
    Context context;
    StorageReference storageReference;

    JsonObject paxInFo;

    public FlightsAdapter(List<FlightDetails> flightDetails, Context context, JsonObject paxInFo) {
        this.flightDetails = flightDetails;
        this.context = context;
        this.paxInFo = paxInFo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.flight_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FlightDetails flightDetail = flightDetails.get(position);
        holder.airlineName.setText(flightDetail.getAirlineName());
        holder.totalPrice.setText("₹" + flightDetail.getTotalPrice());
        holder.totalStops.setText(flightDetail.getTotalStops());
        holder.departureTime.setText(flightDetail.getDepartureTime());
        holder.arrivalTime.setText(flightDetail.getArrivalTime());
        int timeInMinutes = flightDetail.getTotalTime();
        long hours = timeInMinutes / 60;
        long minutes = timeInMinutes % 60;
        holder.totalTime.setText(hours + "h " + minutes + "m");
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference storageReference1 = storageReference.child("AirlineLogos").child(flightDetail.getAirlineImage() + ".png");
        storageReference1.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(context).load(uri).into(holder.airlineImage);
        }).addOnFailureListener(e -> {
            System.out.println("e.getMessage() = " + e.getMessage());
        });
        holder.itemView.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("paxInfo", String.valueOf(paxInFo));
            bundle.putSerializable("flightDetails", flightDetail);
            context.startActivity(new Intent(context, FlightCheckoutActivity.class).putExtra("bundle", bundle));
        });
    }

    @Override
    public int getItemCount() {
        return flightDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView airlineName, departureTime, arrivalTime, totalPrice, totalStops, totalTime;
        ImageView airlineImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            airlineName = itemView.findViewById(R.id.airline_name);
            airlineImage = itemView.findViewById(R.id.airlineImage);
            departureTime = itemView.findViewById(R.id.DepartureText);
            arrivalTime = itemView.findViewById(R.id.ArrivalText);
            totalPrice = itemView.findViewById(R.id.price);
            totalStops = itemView.findViewById(R.id.stop);
            totalTime = itemView.findViewById(R.id.time);

        }
    }
}
