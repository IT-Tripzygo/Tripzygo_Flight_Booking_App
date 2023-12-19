package in.tripzygo.tripzygoflightbookingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class AdapterSI2 extends RecyclerView.Adapter<AdapterSI2.MyViewHolder> {

    JsonArray sI;
    JsonArray travellersInfo;
    Context ctx;

    public AdapterSI2(Context context, JsonArray sI, JsonArray travellersInfo) {
        this.ctx = context;
        this.sI = sI;
        this.travellersInfo = travellersInfo;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_layout, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        JsonObject firstSI = sI.get(position).getAsJsonObject();
        JsonObject fD = firstSI.getAsJsonObject("fD");
        JsonObject AI = fD.getAsJsonObject("aI");

        JsonObject DA = firstSI.getAsJsonObject("da");
        JsonObject AA = firstSI.getAsJsonObject("aa");

//        String idN = firstSI.get("id").getAsString();
//        holder.id.setText(firstSI.get("id").getAsString());

        String name = fD.getAsJsonObject("aI").get("name").getAsString();
        String fN = fD.get("fN").getAsString();
        String eT = fD.get("eT").getAsString();

        String codeAIR = eT + "-" + fN;

        holder.cityFrom.setText(DA.get("city").getAsString());
        holder.cityTo.setText(AA.get("city").getAsString());
        holder.AirportDeparture.setText(DA.get("name").getAsString());
        holder.AirportArrival.setText(AA.get("name").getAsString());

        holder.airlineCode.setText(codeAIR);
        holder.airlineName.setText(name);

        holder.cityDeparture.setText(DA.get("city").getAsString());
        holder.cityArrival.setText(AA.get("city").getAsString());
        holder.cityCodeDep.setText(DA.get("cityCode").getAsString());
        holder.cityCodeArr.setText(AA.get("cityCode").getAsString());

        int duration = firstSI.get("duration").getAsInt();
        int hours = duration / 60;
        int min = duration % 60;
        String totalTime = "  " + hours + "h " + min + "m";
        holder.totalTimeCenterFloating.setText(totalTime);
        holder.totalTime.setText(totalTime);


        // DURATION----------------------------

        String dt = firstSI.get("dt").getAsString();
        String at = firstSI.get("at").getAsString();

        int lengthDa = dt.length();
        int lengthAa = at.length();
        String timeDA = dt.substring(lengthDa - 5, lengthDa);
        String timAA = at.substring(lengthAa - 5, lengthAa);
        holder.timeDeparture.setText(timeDA);
        holder.timeArrival.setText(timAA);

        String[] month = {"Jan", "Feb", "March", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int yearD = Integer.parseInt(dt.substring(lengthDa - 16, lengthDa - 12));
        int monthD = Integer.parseInt(dt.substring(lengthDa - 11, lengthDa - 9));
        int dayD = Integer.parseInt(dt.substring(lengthDa - 8, lengthDa - 6));

        int yearA = Integer.parseInt(dt.substring(lengthAa - 16, lengthAa - 12));
        int monthA = Integer.parseInt(dt.substring(lengthAa - 11, lengthAa - 9));
        int dayA = Integer.parseInt(dt.substring(lengthAa - 8, lengthAa - 6));

        holder.dateDeparture.setText(dayD + " " + month[monthD - 1] + " '" + yearD % 2000);
        holder.dateArrival.setText(dayA + " " + month[monthA - 1] + " '" + yearA % 2000);
        holder.calendarMonth.setText(month[monthD - 1]);
        holder.calendarDate.setText("" + dayD);

//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        Adapter adapter1 = new Adapter(MainActivity.this, jsonElements, travellersInfo);
//        recyclerView.setAdapter(adapter1);

        holder.travellers.setLayoutManager(new LinearLayoutManager(ctx.getApplicationContext()));
        String str = DA.get("code").getAsString() + "-" + AA.get("code").getAsString();
        TravellersAdapter adapter1 = new TravellersAdapter(ctx, travellersInfo, str);
        holder.travellers.setAdapter(adapter1);
        String Image = sI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("code").getAsString();
        StorageReference storageReference1 = FirebaseStorage.getInstance().getReference().child("AirlineLogos").child(Image + ".png");
        storageReference1.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(ctx).load(uri).into(holder.airlineLogoOneWay);
        }).addOnFailureListener(e -> {
            System.out.println("storageReference1 = " + storageReference1);
            System.out.println("e.getMessage() = " + e.getMessage());
        });

    }

    @Override
    public int getItemCount() {
        return sI.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView cityFrom, cityTo, id, airlineName, airlineCode, ClassLevel, stops, totalTime, totalTimeCenterFloating, cityCodeDep, cityCodeArr;
        TextView dateDeparture, dateArrival, cityDeparture, cityArrival, timeDeparture, timeArrival, AirportDeparture, AirportArrival;
        TextView calendarMonth, calendarDate, calendarDay;
        RecyclerView travellers;
        ImageView airlineLogoOneWay;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cityFrom = itemView.findViewById(R.id.cityFrom);
            cityTo = itemView.findViewById(R.id.cityTo);
            cityCodeDep = itemView.findViewById(R.id.cityCodeFrom);
            cityCodeArr = itemView.findViewById(R.id.cityCodeTO);
            stops = itemView.findViewById(R.id.stops);
            totalTime = itemView.findViewById(R.id.totalTime);
            airlineCode = itemView.findViewById(R.id.airlineCode);
            airlineName = itemView.findViewById(R.id.airlineName);
            ClassLevel = itemView.findViewById(R.id.ClassLevel);
            totalTimeCenterFloating = itemView.findViewById(R.id.totalTimeFloatingCenter);
            dateDeparture = itemView.findViewById(R.id.allDateFrom);
            dateArrival = itemView.findViewById(R.id.allDateTo);
            cityDeparture = itemView.findViewById(R.id.cityDeparture);
            cityArrival = itemView.findViewById(R.id.cityArrival);
            timeDeparture = itemView.findViewById(R.id.cityTimeFrom);
            timeArrival = itemView.findViewById(R.id.cityTimeTo);
            AirportDeparture = itemView.findViewById(R.id.departureAirport);
            AirportArrival = itemView.findViewById(R.id.arrivingAirport);

            calendarDate = itemView.findViewById(R.id.calendarDate);
            calendarDay = itemView.findViewById(R.id.calendarDay);
            calendarMonth = itemView.findViewById(R.id.calendarMonth);
            airlineLogoOneWay = itemView.findViewById(R.id.airlineLogoOneWay);

            travellers = itemView.findViewById(R.id.travellersInfoRecycler);

        }
    }
}
