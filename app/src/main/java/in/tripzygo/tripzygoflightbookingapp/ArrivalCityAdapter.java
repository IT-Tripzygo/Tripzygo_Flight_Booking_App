package in.tripzygo.tripzygoflightbookingapp;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.tripzygo.tripzygoflightbookingapp.Modals.AirportCode;

public class ArrivalCityAdapter extends RecyclerView.Adapter<ArrivalCityAdapter.MyViewHolder> {

    List<AirportCode> city;
    Context c;
    Dialog dialog;
    TextView code,cityTextView;
    String fromAirport,type;
    RecyclerView recyclerView;

    public ArrivalCityAdapter(List<AirportCode> city, Context c, Dialog dialog, TextView cityTextView,TextView code, String fromAirport,  String type,RecyclerView recyclerView) {
        this.city = city;
        this.c = c;
        this.dialog = dialog;
        this.code = code;
        this.cityTextView=cityTextView;
        this.fromAirport = fromAirport;
        this.recyclerView = recyclerView;
        this.type=type;
    }

    public ArrivalCityAdapter(Context c, List<AirportCode> city) {
        this.city = city;
        this.c = c;
    }
    public void updateList(List<AirportCode> filterList) {
        this.city = filterList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_with_airport_item_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.city.setText(city.get(position).getCity());
        holder.cityCode.setText(city.get(position).getCitycode());
        holder.country.setText(city.get(position).getCountry());
        holder.airport.setText(city.get(position).getName());
        holder.itemView.setOnClickListener(view -> {
            AirportCode fromAirportCode = city.get(position);
            if (fromAirportCode.getCountry().equalsIgnoreCase("India")) {
                type = "Domestic";
            } else {
                type = "International";
            }
            code.setText(fromAirportCode.getCitycode());
            cityTextView.setText(fromAirportCode.getCity());
            fromAirport = fromAirportCode.getCitycode();
            recyclerView.setVisibility(View.GONE);
            dialog.dismiss();
        });
    }

    @Override
    public int getItemCount() {
        return city.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView city;
        TextView cityCode;
        TextView country;
        TextView airport;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            city = itemView.findViewById(R.id.city);
            cityCode = itemView.findViewById(R.id.cityCode);
            country = itemView.findViewById(R.id.country);
            airport = itemView.findViewById(R.id.Airport);


        }
    }
}
