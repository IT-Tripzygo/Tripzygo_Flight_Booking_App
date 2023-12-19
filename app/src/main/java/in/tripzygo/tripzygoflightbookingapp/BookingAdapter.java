package in.tripzygo.tripzygoflightbookingapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.tripzygo.tripzygoflightbookingapp.Modals.Booking;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {
    List<Booking> bookingList;
    Context context;

    public BookingAdapter(List<Booking> bookingList, Context context) {
        this.bookingList = bookingList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        String depDate = booking.getDeparture();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
        SimpleDateFormat df1 = new SimpleDateFormat("E, dd MMM", Locale.getDefault());
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(booking.getTripInfos(), new TypeToken<JsonArray>() {
        }.getType());
        if (jsonArray.size() == 1) {
            JsonArray sI = jsonArray.get(0).getAsJsonObject().getAsJsonArray("sI");
            if (sI.size() == 1) {
                holder.booking_destination.setText("Trip to " + sI.get(0).getAsJsonObject().getAsJsonObject("aa").get("city").getAsString());
                holder.bookingArrivalCityCodeText.setText(sI.get(0).getAsJsonObject().getAsJsonObject("aa").get("cityCode").getAsString());
                holder.imageView.setImageDrawable(context.getDrawable(R.drawable.baseline_flight_24));
                holder.imageView.setRotation(90);
                holder.airlineName.setText(sI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("name").getAsString() + " " + sI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("code").getAsString() + " " + sI.get(0).getAsJsonObject().getAsJsonObject("fD").get("fN").getAsString());
                try {
                    Date dof = df.parse(depDate);
                    Date now = Calendar.getInstance().getTime();
                    if (dof.before(now)) {
                        holder.completedText.setVisibility(View.VISIBLE);
                    }
                    String dTime = df1.format(dof);
                    holder.bookingArrivalDateText.setText(dTime);

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else if (sI.size() > 1) {
                holder.bookingArrivalCityCodeText.setText(sI.get(sI.size() - 1).getAsJsonObject().getAsJsonObject("aa").get("cityCode").getAsString());
                holder.booking_destination.setText("Trip to " + sI.get(sI.size() - 1).getAsJsonObject().getAsJsonObject("aa").get("city").getAsString());
                holder.imageView.setImageDrawable(context.getDrawable(R.drawable.baseline_flight_24));
                holder.imageView.setRotation(90);
                holder.airlineName.setText(sI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("name").getAsString()
                        + " " + sI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("code").getAsString()
                        + " " + sI.get(0).getAsJsonObject().getAsJsonObject("fD").get("fN").getAsString()
                        + "," + sI.get(sI.size() - 1).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("name").getAsString()
                        + " " + sI.get(sI.size() - 1).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("code").getAsString()
                        + " " + sI.get(sI.size() - 1).getAsJsonObject().getAsJsonObject("fD").get("fN").getAsString());
                try {
                    Date dof = df.parse(depDate);
                    Date now = Calendar.getInstance().getTime();
                    if (dof.before(now)) {
                        holder.completedText.setVisibility(View.VISIBLE);
                    }
                    String dTime = df1.format(dof);
                    holder.bookingArrivalDateText.setText(dTime);

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            holder.bookingDepartureCityCodeText.setText(sI.get(0).getAsJsonObject().getAsJsonObject("da").get("cityCode").getAsString());
        } else if (jsonArray.size() > 1) {
            JsonArray sI = jsonArray.get(0).getAsJsonObject().getAsJsonArray("sI");
            JsonArray returnsI = jsonArray.get(jsonArray.size() - 1).getAsJsonObject().getAsJsonArray("sI");
            if (returnsI.size() == 1) {
                holder.bookingArrivalCityCodeText.setText(returnsI.get(0).getAsJsonObject().getAsJsonObject("aa").get("cityCode").getAsString());
                holder.booking_destination.setText("Trip to " + returnsI.get(0).getAsJsonObject().getAsJsonObject("aa").get("city").getAsString());
                holder.imageView.setImageDrawable(context.getDrawable(R.drawable.arrow_right_arrow_left_solid_black));
                holder.airlineName.setText(returnsI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("name").getAsString() + " " + returnsI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("code").getAsString() + " " + returnsI.get(0).getAsJsonObject().getAsJsonObject("fD").get("fN").getAsString());
                try {
                    Date dof = df.parse(depDate);
                    Date now = Calendar.getInstance().getTime();
                    if (dof.before(now)) {
                        holder.completedText.setVisibility(View.VISIBLE);
                    }
                    String dTime = df1.format(dof);
                    holder.bookingArrivalDateText.setText(dTime);

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else if (returnsI.size() > 1) {
                System.out.println("sI = " + returnsI.size());
                System.out.println("sI = " + returnsI.size());
                holder.bookingArrivalCityCodeText.setText(returnsI.get(returnsI.size() - 1).getAsJsonObject().getAsJsonObject("aa").get("cityCode").getAsString());
                System.out.println("sI.get(sI.size() - 1).getAsJsonObject().getAsJsonObject(\"aa\").get(\"city\").getAsString() = " + returnsI.get(returnsI.size() - 1).getAsJsonObject().getAsJsonObject("aa").get("city").getAsString());
                holder.booking_destination.setText("Trip to " + returnsI.get(returnsI.size() - 1).getAsJsonObject().getAsJsonObject("aa").get("city").getAsString());
                holder.imageView.setImageDrawable(context.getDrawable(R.drawable.arrow_right_arrow_left_solid_black));
                holder.airlineName.setText(sI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("name").getAsString()
                        + " " + sI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("code").getAsString()
                        + " " + sI.get(0).getAsJsonObject().getAsJsonObject("fD").get("fN").getAsString()
                        + "," + returnsI.get(returnsI.size() - 1).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("name").getAsString()
                        + " " + returnsI.get(returnsI.size() - 1).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("code").getAsString()
                        + " " + returnsI.get(returnsI.size() - 1).getAsJsonObject().getAsJsonObject("fD").get("fN").getAsString());
                try {
                    Date dof = df.parse(depDate);
                    String adate = returnsI.get(returnsI.size() - 1).getAsJsonObject().get("at").getAsString();
                    Date aol = df.parse(adate);
                    Date now = Calendar.getInstance().getTime();
                    if (dof.before(now)) {
                        holder.completedText.setVisibility(View.VISIBLE);
                    }
                    String dTime = df1.format(dof);
                    String aTime = df1.format(aol);
                    holder.bookingArrivalDateText.setText(dTime + " - " + aTime);

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            holder.bookingDepartureCityCodeText.setText(returnsI.get(0).getAsJsonObject().getAsJsonObject("da").get("cityCode").getAsString());
        }
        holder.itemView.setOnClickListener(view -> {
            context.startActivity(new Intent(context, DisplayBookingActivity.class).putExtra("booking", String.valueOf(gson.toJson(booking))));
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView airlineName, booking_destination, bookingDepartureCityCodeText, bookingArrivalCityCodeText, bookingArrivalDateText, completedText;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            airlineName = itemView.findViewById(R.id.booking_airline_name);
            completedText = itemView.findViewById(R.id.completedText);
            booking_destination = itemView.findViewById(R.id.booking_destination);
            bookingDepartureCityCodeText = itemView.findViewById(R.id.bookingDepartureCityCodeText);
            bookingArrivalCityCodeText = itemView.findViewById(R.id.bookingArrivalCityCodeText);
            bookingArrivalDateText = itemView.findViewById(R.id.bookingArrivalDateText);
            imageView = itemView.findViewById(R.id.bookingAirplane);

        }
    }

}
