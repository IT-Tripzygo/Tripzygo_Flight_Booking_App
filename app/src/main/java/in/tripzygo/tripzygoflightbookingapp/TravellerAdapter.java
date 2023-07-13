package in.tripzygo.tripzygoflightbookingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

import in.tripzygo.tripzygoflightbookingapp.Modals.Traveller;

public class TravellerAdapter extends RecyclerView.Adapter<TravellerAdapter.ViewHolder> {
    List<Traveller> travellerList, selectedTravellerList;
    Context context;
    private int selectedItemIndex = 0;
    public TravellerAdapter(List<Traveller> travellerList, List<Traveller> selectedTravellerList, Context context) {
        this.travellerList = travellerList;
        this.selectedTravellerList = selectedTravellerList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.travellerlayout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Traveller traveller = travellerList.get(position);
        holder.radioButton.setText(traveller.getTitle() + ". " + traveller.getFirstName() + " " + traveller.getLastName());
        holder.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    selectedTravellerList.add(traveller);
                    Gson gson= new Gson();
                    System.out.println(" = " + gson.toJson(selectedTravellerList));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return travellerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radio_passenger);

        }
    }

    public List<Traveller> getSelectedTravellerList() {
        return selectedTravellerList;
    }

}
