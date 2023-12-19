package in.tripzygo.tripzygoflightbookingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.tripzygo.tripzygoflightbookingapp.Modals.Traveller;

public class TravellersCountDetails_Adapter extends RecyclerView.Adapter<TravellersCountDetails_Adapter.MyViewHolder> {

    int adult, children, infant;
    //    ArrayList<String> adult;
    Context ctx;
    List<Traveller> travellerList;
    String type;

    public TravellersCountDetails_Adapter(int adult, int children, int infant, Context ctx, List<Traveller> travellerList,String type) {
        this.adult = adult;
        this.children = children;
        this.infant = infant;
        this.ctx = ctx;
        this.travellerList=travellerList;
        this.type=type;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.traveller_details_adapter, parent, false);
        MyViewHolder view = new MyViewHolder(v);
        return view;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.adult.setLayoutManager(new LinearLayoutManager(ctx.getApplicationContext()));
        AdultAdapter adultAdapter = new AdultAdapter(adult, ctx,travellerList,type);
        holder.adult.setAdapter(adultAdapter);
        travellerList.addAll(adultAdapter.getSelectedTravellerList());

        holder.children.setLayoutManager(new LinearLayoutManager(ctx.getApplicationContext()));
        ChildrenAdapter childrenAdapter = new ChildrenAdapter(children, ctx,travellerList,type);
        holder.children.setAdapter(childrenAdapter);
        travellerList.addAll(childrenAdapter.getSelectedTravellerList());

        holder.infant.setLayoutManager(new LinearLayoutManager(ctx.getApplicationContext()));
        InfantAdapter infantAdapter = new InfantAdapter(infant, ctx,travellerList,type);
        holder.infant.setAdapter(infantAdapter);
        travellerList.addAll(infantAdapter.getSelectedTravellerList());

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RecyclerView adult, children, infant;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            adult = itemView.findViewById(R.id.AdultsRecycler);
            children = itemView.findViewById(R.id.ChildrenRecycler);
            infant = itemView.findViewById(R.id.InfantsRecycler);
        }
    }
    public List<Traveller> getSelectedTravellerList() {
        return travellerList;
    }

}
