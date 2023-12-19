package in.tripzygo.tripzygoflightbookingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class PassengerBookingAdapter extends RecyclerView.Adapter<PassengerBookingAdapter.ViewHolder> {
    JsonArray jsonArray;
    Context context;
    String string;

    public PassengerBookingAdapter(JsonArray jsonArray, Context context, String string) {
        this.jsonArray = jsonArray;
        this.context = context;
        this.string = string;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.passengerbookinglayout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JsonObject jsonObject=jsonArray.get(position).getAsJsonObject();
        System.out.println("string = " + string);
        holder.passengerNameTextView.setText(jsonObject.get("fN").getAsString()+" "+jsonObject.get("lN").getAsString());
        holder.pnrTextView.setText(jsonObject.get("pnrDetails").getAsJsonObject().get(string).getAsString()+" -");

    }

    @Override
    public int getItemCount() {
        return jsonArray.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView passengerNameTextView,pnrTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            passengerNameTextView=itemView.findViewById(R.id.passengerNameTextBooking);
            pnrTextView=itemView.findViewById(R.id.pnrBooking);
        }
    }
}
