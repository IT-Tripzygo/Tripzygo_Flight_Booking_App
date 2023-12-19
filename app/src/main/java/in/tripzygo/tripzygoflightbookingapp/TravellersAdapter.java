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

public class TravellersAdapter extends RecyclerView.Adapter<TravellersAdapter.MyViewHolder>{
    JsonArray travellersInfo;
    Context ctx;
    String string;

    public TravellersAdapter(Context context, JsonArray TV,String string) {
        this.ctx = context;
        this.travellersInfo = TV;
        this.string =string;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.travellers_info, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        JsonObject travellersObject = travellersInfo.get(position).getAsJsonObject();
        JsonObject pnrDetail = travellersObject.getAsJsonObject("pnrDetails");

        String Name = travellersObject.get("ti").getAsString()+" "+travellersObject.get("fN").getAsString()+" "+travellersObject.get("lN").getAsString();

        holder.name.setText((position+1)+". "+Name);
        holder.PNR.setText(pnrDetail.get(string).getAsString());
    }

    @Override
    public int getItemCount() {
        return travellersInfo.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name, PNR, E_Ticket_Number, SeatNo;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.passengerName);
            PNR = itemView.findViewById(R.id.passengerPNR);
            E_Ticket_Number = itemView.findViewById(R.id.passengerTicketNo);
            SeatNo = itemView.findViewById(R.id.passengerSeat);

        }
    }
}
