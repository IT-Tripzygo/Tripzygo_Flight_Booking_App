package in.tripzygo.tripzygoflightbookingapp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.tripzygo.tripzygoflightbookingapp.Modals.FlightDetails;
import in.tripzygo.tripzygoflightbookingapp.Tools.Sort;

public class InternationalReturnFlightsAdapter extends RecyclerView.Adapter<InternationalReturnFlightsAdapter.ViewHolder> {
    List<FlightDetails> flightDetails;
    Context context;
    StorageReference storageReference;
    JsonObject paxInFo, jsonObject;
    String type = "International";
    FragmentManager fragmentManager;


    public InternationalReturnFlightsAdapter(List<FlightDetails> flightDetails, Context context, JsonObject paxInFo, JsonObject jsonObject, FragmentManager fragmentManager) {
        this.flightDetails = flightDetails;
        this.context = context;
        this.paxInFo = paxInFo;
        this.jsonObject = jsonObject;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.international_return_flight_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.expandableRelativeLayout.collapse();
        FlightDetails flightDetail = flightDetails.get(position);
        FlightDetails returnFlightDetails = new FlightDetails();
        Gson gson = new Gson();
        JsonArray sI = gson.fromJson(flightDetail.getSI(), new TypeToken<JsonArray>() {
        }.getType());
        String depCity = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().get("fromCityOrAirport").getAsJsonObject().get("code").getAsString();
        String arrCity = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().get("toCityOrAirport").getAsJsonObject().get("code").getAsString();
        String retDepCity = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(1).getAsJsonObject().get("fromCityOrAirport").getAsJsonObject().get("code").getAsString();
        String retArrCity = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(1).getAsJsonObject().get("toCityOrAirport").getAsJsonObject().get("code").getAsString();
        if (sI.size() < 3) {
            JsonObject jsonObject2 = sI.get(0).getAsJsonObject();
            if (jsonObject2.get("stops").getAsInt() == 0) {
                flightDetail.setTotalStops("non-stop");
                flightDetail.setTotalTime(sI.get(0).getAsJsonObject().get("duration").getAsInt());
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String departure = sI.get(0).getAsJsonObject().get("dt").getAsString();
                String arrival = sI.get(0).getAsJsonObject().get("at").getAsString();
                try {
                    Date dof = df.parse(departure);
                    Date aol = df.parse(arrival);
                    String dTime = df1.format(dof);
                    String aTime = df1.format(aol);
//                    System.out.println("aTime = " + aTime + " dTime = " + dTime);
                    flightDetail.setDepartureTime(dTime);
                    flightDetail.setArrivalTime(aTime);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String departureOfFirst = sI.get(0).getAsJsonObject().get("dt").getAsString();
                String arrivalOfLast = sI.get(sI.size() - 1).getAsJsonObject().get("at").getAsString();
                try {
                    Date dof = df.parse(departureOfFirst);
                    Date aol = df.parse(arrivalOfLast);
                    String dTime = df1.format(dof);
                    String aTime = df1.format(aol);
                    System.out.println("aTime = " + aTime + " dTime = " + dTime);
                    flightDetail.setDepartureTime(dTime);
                    flightDetail.setArrivalTime(aTime);
                    long d = dof.getTime();
                    long a = aol.getTime();
                    long t = a - d;
                    long Seconds_difference
                            = (t
                            / (1000 * 60));

                    long Minutes_difference
                            = (t
                            / (1000 * 60))
                            % 60;

                    long Hours_difference
                            = (t
                            / (1000 * 60 * 60))
                            % 24;

//                    System.out.println("departureOfFirst = " + departureOfFirst);
//                    System.out.println("arrivalOfLast = " + arrivalOfLast);
//                    System.out.println(Hours_difference + " h " + Minutes_difference + " m");
//                    System.out.println("Seconds_difference = " + Seconds_difference);
                    flightDetail.setTotalTime(Math.toIntExact(Seconds_difference));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                flightDetail.setTotalStops(sI.get(sI.size() - 1).getAsJsonObject().get("sN").getAsInt() + " Stop");
            }
            holder.totalStops.setText(flightDetail.getTotalStops());
            holder.departureTime.setText(flightDetail.getDepartureTime());
            holder.arrivalTime.setText(flightDetail.getArrivalTime());
            int timeInMinutes = flightDetail.getTotalTime();
            long hours = timeInMinutes / 60;
            long minutes = timeInMinutes % 60;
            holder.totalTime.setText(hours + "h " + minutes + "m");
            JsonObject jsonObject1 = sI.get(sI.size() - 1).getAsJsonObject();
            JsonArray returnsI = new JsonArray();
            returnsI.add(jsonObject1);
            sI.remove(jsonObject1);
            flightDetail.setSI(sI.toString());
            returnFlightDetails.setSI(returnsI.toString());
            returnFlightDetails.setAirlineImage(returnsI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("code").getAsString());
            if (jsonObject1.get("stops").getAsInt() == 0) {
                flightDetail.setTotalStops("non-stop");
                flightDetail.setTotalTime(returnsI.get(0).getAsJsonObject().get("duration").getAsInt());
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String departure = returnsI.get(0).getAsJsonObject().get("dt").getAsString();
                String arrival = returnsI.get(0).getAsJsonObject().get("at").getAsString();
                try {
                    Date dof = df.parse(departure);
                    Date aol = df.parse(arrival);
                    String dTime = df1.format(dof);
                    String aTime = df1.format(aol);
//                    System.out.println("aTime = " + aTime + " dTime = " + dTime);
                    flightDetail.setDepartureTime(dTime);
                    flightDetail.setArrivalTime(aTime);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String departureOfFirst = returnsI.get(0).getAsJsonObject().get("dt").getAsString();
                String arrivalOfLast = returnsI.get(returnsI.size() - 1).getAsJsonObject().get("at").getAsString();
                try {
                    Date dof = df.parse(departureOfFirst);
                    Date aol = df.parse(arrivalOfLast);
                    String dTime = df1.format(dof);
                    String aTime = df1.format(aol);
//                    System.out.println("aTime = " + aTime + " dTime = " + dTime);
                    flightDetail.setDepartureTime(dTime);
                    flightDetail.setArrivalTime(aTime);
                    long d = dof.getTime();
                    long a = aol.getTime();
                    long t = a - d;
                    long Seconds_difference
                            = (t
                            / (1000 * 60));

                    long Minutes_difference
                            = (t
                            / (1000 * 60))
                            % 60;

                    long Hours_difference
                            = (t
                            / (1000 * 60 * 60))
                            % 24;

//                    System.out.println("departureOfFirst = " + departureOfFirst);
//                    System.out.println("arrivalOfLast = " + arrivalOfLast);
//                    System.out.println(Hours_difference + " h " + Minutes_difference + " m");
//                    System.out.println("Seconds_difference = " + Seconds_difference);
                    flightDetail.setTotalTime(Math.toIntExact(Seconds_difference));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                flightDetail.setTotalStops(sI.get(sI.size() - 1).getAsJsonObject().get("sN").getAsInt() + " Stop");
            }
            holder.returnTotalStops.setText(flightDetail.getTotalStops());
            holder.returnDepartureTime.setText(flightDetail.getDepartureTime());
            holder.returnArrivalTime.setText(flightDetail.getArrivalTime());
            int timeInMinutesRet = flightDetail.getTotalTime();
            long hoursRet = timeInMinutesRet / 60;
            long minutesRet = timeInMinutesRet % 60;
            holder.returnTotalTime.setText(hoursRet + "h " + minutesRet + "m");
        } else {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
            SimpleDateFormat df1 = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String departure = sI.get(0).getAsJsonObject().get("dt").getAsString();
            String arrival = null;
            JsonArray fsI = new JsonArray();
            int j = 0;
            for (int i = 0; i < sI.size(); i++) {
                JsonObject flightObject1 = sI.get(i).getAsJsonObject();
                fsI.add(flightObject1);
                if (flightObject1.getAsJsonObject("aa").get("cityCode").getAsString().matches(arrCity)) {
                    arrival = flightObject1.get("at").getAsString();
                    try {
                        Date dof = df.parse(departure);
                        Date aol = df.parse(arrival);
                        String dTime = df1.format(dof);
                        String aTime = df1.format(aol);
//                        System.out.println("aTime = " + aTime + " dTime = " + dTime);
                        holder.departureTime.setText(dTime);
                        holder.arrivalTime.setText(aTime);
                        long d = dof.getTime();
                        long a = aol.getTime();
                        long t = a - d;
                        long Seconds_difference
                                = (t
                                / (1000 * 60));

                        long Minutes_difference
                                = (t
                                / (1000 * 60))
                                % 60;

                        long Hours_difference
                                = (t
                                / (1000 * 60 * 60))
                                % 24;
//                        System.out.println(Hours_difference + " h " + Minutes_difference + " m");
//                        System.out.println("Seconds_difference = " + Seconds_difference);
                        int timeInMinutes = Math.toIntExact(Seconds_difference);
                        long hours = timeInMinutes / 60;
                        long minutes = timeInMinutes % 60;
                        holder.totalTime.setText(hours + "h " + minutes + "m");

                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    if (flightObject1.get("sN").getAsInt() == 0) {
                        holder.totalStops.setText("non stop");

                    } else {
                        holder.totalStops.setText(flightObject1.get("sN") + " stop");
                    }
                    j = i + 1;
                    break;
                }

            }

            String retdeparture = sI.get(j).getAsJsonObject().get("dt").getAsString();

            String retarrival = null;
            JsonArray returnsI = new JsonArray();
            int length = sI.size();
            for (int i = j; i < sI.size(); i++) {
                JsonObject flightObject1 = sI.get(i).getAsJsonObject();
                JsonObject jsonObject1 = sI.get(i).getAsJsonObject();
                returnsI.add(jsonObject1);
                if (flightObject1.getAsJsonObject("aa").get("cityCode").getAsString().matches(retArrCity)) {
                    retarrival = flightObject1.get("at").getAsString();
                    try {
                        Date dof = df.parse(retdeparture);
                        Date aol = df.parse(retarrival);
                        String dTime = df1.format(dof);
                        String aTime = df1.format(aol);
//                        System.out.println("aTime = " + aTime + " dTime = " + dTime);
                        holder.returnDepartureTime.setText(dTime);
                        holder.returnArrivalTime.setText(aTime);
                        long d = dof.getTime();
                        long a = aol.getTime();
                        long t = a - d;
                        long Seconds_difference
                                = (t
                                / (1000 * 60));
//                        System.out.println(Hours_difference + " h " + Minutes_difference + " m");
//                        System.out.println("Seconds_difference = " + Seconds_difference);
                        int timeInMinutes = Math.toIntExact(Seconds_difference);
                        long hours = timeInMinutes / 60;
                        long minutes = timeInMinutes % 60;
                        holder.returnTotalTime.setText(hours + "h " + minutes + "m");
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    if (flightObject1.get("sN").getAsInt() == 0) {
                        holder.returnTotalStops.setText("non stop");
                    } else {
                        holder.returnTotalStops.setText(flightObject1.get("sN") + " stop");
                    }
                }
            }

            flightDetail.setSI(fsI.toString());
            returnFlightDetails.setSI(returnsI.toString());
            returnFlightDetails.setAirlineImage(returnsI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("code").getAsString());
            System.out.println("gson.toJson(sI) = " + gson.toJson(sI));
            System.out.println("gson.toJson(returnsI) = " + gson.toJson(returnsI));

        }
        holder.departureCity.setText(depCity);
        holder.arrivalCity.setText(arrCity);
        holder.returnDepartureCity.setText(retDepCity);
        holder.returnArrivalCity.setText(retArrCity);
        flightDetail.setAirlineName(sI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("name").getAsString());
        flightDetail.setAirlineImage(sI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("code").getAsString());
        holder.airlineName.setText(flightDetail.getAirlineName());
        Locale locale = new Locale.Builder().setLanguage("en").setRegion("IN").build();
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        int d = Integer.parseInt(flightDetail.getTotalPrice());
        String currency = formatter.format(d);
        int centsIndex = currency.lastIndexOf(".00");
        if (centsIndex != -1) {
            currency = currency.substring(0, centsIndex);
        }
        holder.totalPrice.setText(currency);
        JsonArray totalPriceList = gson.fromJson(flightDetail.getTotalPriceList(), new TypeToken<JsonArray>() {
        }.getType());
        List<JsonObject> jsonObjects = new ArrayList<>();
        for (JsonElement jsonElement : totalPriceList) {
            JsonObject jsonObject2 = jsonElement.getAsJsonObject();
            jsonObjects.add(jsonObject2);
        }
        jsonObjects.sort(new Sort());
        String pId1 = jsonObjects.get(0).get("id").getAsString();
        if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").has("rT")) {
            ColorStateList colorStateList;
            if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 0) {
                holder.FareType.setText("NR");
                colorStateList = ColorStateList.valueOf(context.getResources().getColor(R.color.red));
                holder.FareType.setBackgroundTintList(colorStateList);
            } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 1) {
                holder.FareType.setText("RF");
                colorStateList = ColorStateList.valueOf(context.getResources().getColor(R.color.forestGreen));
                holder.FareType.setBackgroundTintList(colorStateList);
            } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 2) {
                holder.FareType.setText("PR");
                colorStateList = ColorStateList.valueOf(context.getResources().getColor(R.color.green));
                holder.FareType.setBackgroundTintList(colorStateList);
            }
        }
        if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString().equalsIgnoreCase("ECONOMY")) {
            holder.classType.setText("EC");
        } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString().equalsIgnoreCase("Premium Economy")) {
            holder.classType.setText("PE");
        } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString().equalsIgnoreCase("Business")) {
            holder.classType.setText("BS");
        } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString().equalsIgnoreCase("First")) {
            holder.classType.setText("FC");
        }
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference storageReference1 = storageReference.child("AirlineLogos").child(flightDetail.getAirlineImage() + ".png");
        storageReference1.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(context).load(uri).into(holder.airlineImage);
        }).addOnFailureListener(e -> {
            System.out.println("storageReference1 = " + storageReference1);
            System.out.println("e.getMessage() = " + e.getMessage());
        });
        holder.itemView.setOnClickListener(view -> {
            if (holder.expandableRelativeLayout.isExpanded()) {
                holder.expandableRelativeLayout.collapse();
                holder.viewFares.setText("View Fares");
                holder.FareType.setVisibility(View.VISIBLE);
                holder.classType.setVisibility(View.VISIBLE);
            } else {
                holder.expandableRelativeLayout.expand();
                holder.viewFares.setText("Hide Fares");
                holder.FareType.setVisibility(View.GONE);
                holder.classType.setVisibility(View.GONE);
            }
         /*   List<String> stringList = new ArrayList<>();
            stringList.add(pId1);
            Bundle bundle = new Bundle();
            bundle.putString("paxInfo", String.valueOf(paxInFo));
            bundle.putString("priceIds", String.valueOf(stringList));
            bundle.putString("type",type);
            bundle.putSerializable("flightDetails", flightDetail);
            bundle.putSerializable("returnFlightDetails", returnFlightDetails);
            context.startActivity(new Intent(context, ReturnFlightCheckoutActivity.class).putExtra("bundle", bundle));
            */
        });
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
//        System.out.println("jsonObjects = " + jsonObjects);
        FlightDetails flightDetail1 = flightDetails.get(position);
//        System.out.println("flightDetail1 = " + gson.toJson(flightDetail1));
        InternationalReturnFareAdapter fareAdapter = new InternationalReturnFareAdapter(jsonObjects, context, flightDetail1, flightDetail, returnFlightDetails, fragmentManager, type, paxInFo);
        holder.recyclerView.setAdapter(fareAdapter);
    }

    @Override
    public int getItemCount() {
        return flightDetails.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView airlineName, departureTime, arrivalTime, totalPrice, totalStops, totalTime, FareType, departureCity, arrivalCity, returnDepartureCity, returnArrivalCity, returnDepartureTime, returnArrivalTime, returnTotalStops, returnTotalTime, classType, viewFares;
        ImageView airlineImage;

        ExpandableRelativeLayout expandableRelativeLayout;
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            airlineName = itemView.findViewById(R.id.airline_nameInternational);
            airlineImage = itemView.findViewById(R.id.airlineImageInternational);
            departureTime = itemView.findViewById(R.id.DepartureTextInternational);
            arrivalTime = itemView.findViewById(R.id.ArrivalTextInternational);
            departureCity = itemView.findViewById(R.id.DepartureCityTextInternational);
            arrivalCity = itemView.findViewById(R.id.ArrivalCityTextInternational);
            returnDepartureCity = itemView.findViewById(R.id.returnDepartureCityTextInternational);
            returnArrivalCity = itemView.findViewById(R.id.returnArrivalCityTextInternational);
            returnArrivalTime = itemView.findViewById(R.id.returnArrivalText);
            returnDepartureTime = itemView.findViewById(R.id.returnDepartureText);
            totalPrice = itemView.findViewById(R.id.priceInternational);
            totalStops = itemView.findViewById(R.id.stopInternational);
            returnTotalStops = itemView.findViewById(R.id.returnstop);
            returnTotalTime = itemView.findViewById(R.id.returntime);
            totalTime = itemView.findViewById(R.id.timeInternational);
            FareType = itemView.findViewById(R.id.FareTypeInternational);
            viewFares = itemView.findViewById(R.id.viewFaresInternational);
            expandableRelativeLayout = itemView.findViewById(R.id.expandableLayout_flightInternational);
            recyclerView = itemView.findViewById(R.id.fareRecyclerInternational);
            classType = itemView.findViewById(R.id.classTypeInternational);

        }
    }
}
