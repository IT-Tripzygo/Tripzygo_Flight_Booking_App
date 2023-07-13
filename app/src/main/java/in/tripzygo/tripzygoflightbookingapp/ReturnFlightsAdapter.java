package in.tripzygo.tripzygoflightbookingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.StorageReference;

import java.util.List;

import in.tripzygo.tripzygoflightbookingapp.Modals.FlightDetails;

public class ReturnFlightsAdapter extends RecyclerView.Adapter<ReturnFlightsAdapter.ViewHolder> {
    List<FlightDetails> flightDetails;
    Context context;
    StorageReference storageReference;
    int checkedPosition = 0;
    private AdapterCallback listener;
    private int selectedItemIndex = 0;

    public interface AdapterCallback {
        void onItemClicked(int value);

        void updateSum();
    }

    // Define the callback interface

    public ReturnFlightsAdapter(List<FlightDetails> flightDetails, Context context, AdapterCallback listener) {
        this.flightDetails = flightDetails;
        this.context = context;
        this.listener = listener;
    }

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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView airlineName, departureTime, arrivalTime, totalPrice, totalStops, totalTime;
        ImageView airlineImage;
        RelativeLayout relativeLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            airlineName = itemView.findViewById(R.id.airline_name);
            airlineImage = itemView.findViewById(R.id.airlineImage);
            departureTime = itemView.findViewById(R.id.DepartureText);
            arrivalTime = itemView.findViewById(R.id.ArrivalText);
            totalPrice = itemView.findViewById(R.id.price);
            totalStops = itemView.findViewById(R.id.stop);
            totalTime = itemView.findViewById(R.id.time);
            relativeLayout = itemView.findViewById(R.id.returnRelative);

        }

        void bind(FlightDetails flightDetail) {
            if (selectedItemIndex == -1) {
                relativeLayout.setBackgroundColor(context.getColor(R.color.white));
            } else {
                if (selectedItemIndex == getAdapterPosition()) {
                    relativeLayout.setBackgroundColor(context.getColor(R.color.lavender));
                } else {
                    relativeLayout.setBackgroundColor(context.getColor(R.color.white));
                }
            }
            airlineName.setText(flightDetail.getAirlineName());
            totalPrice.setText("₹" + flightDetail.getTotalPrice());
            totalStops.setText(flightDetail.getTotalStops());
            departureTime.setText(flightDetail.getDepartureTime());
            arrivalTime.setText(flightDetail.getArrivalTime());
            int timeInMinutes = flightDetail.getTotalTime();
            long hours = timeInMinutes / 60;
            long minutes = timeInMinutes % 60;
            totalTime.setText(hours + "h " + minutes + "m");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
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
                listener.updateSum();
            }
        }
    }

    public FlightDetails getSelected() {
        if (selectedItemIndex != -1) {
            return flightDetails.get(checkedPosition);
        }
        return null;
    }

    public FlightDetails getSelectedItemPriceId() {
        if (selectedItemIndex >= 0 && selectedItemIndex < flightDetails.size()) {
            return flightDetails.get(selectedItemIndex );
        }
        return null;
    }

    public int getSelectedItem() {
        if (selectedItemIndex >= 0 && selectedItemIndex < flightDetails.size()) {
            return Integer.parseInt(flightDetails.get(selectedItemIndex).getTotalPrice());
        }
        return 0;
    }

    public void selectItem(int position) {
        selectedItemIndex = position;
        notifyDataSetChanged();
    }
}
