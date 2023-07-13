package in.tripzygo.tripzygoflightbookingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.tripzygo.tripzygoflightbookingapp.Modals.Traveller;

public class SelectedPassengerAdapter extends RecyclerView.Adapter<SelectedPassengerAdapter.ViewHolder> {
    List<Traveller> travellerList;
    Context context;

    public SelectedPassengerAdapter(List<Traveller> travellerList, Context context) {
        this.travellerList = travellerList;

        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.passesnger_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Traveller traveller = travellerList.get(position);
        holder.passengerNameTextView.setText(traveller.getTitle() + ". " + traveller.getFirstName() + " " + traveller.getLastName());

    }

    @Override
    public int getItemCount() {
        return travellerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView passengerNameTextView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            passengerNameTextView = itemView.findViewById(R.id.passengerNameText);

        }
    }

}
