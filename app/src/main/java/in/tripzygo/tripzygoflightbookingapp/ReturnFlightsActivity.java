package in.tripzygo.tripzygoflightbookingapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.tripzygo.tripzygoflightbookingapp.Modals.FlightDetails;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.ApiInterface;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.Retrofitt;
import in.tripzygo.tripzygoflightbookingapp.Tools.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReturnFlightsActivity extends AppCompatActivity implements ReturnFlightsAdapter.AdapterCallback {
    RecyclerView oneWayRecyclerView, returnWayRecyclerView;
    TextView TotalPriceTextView, DepartureAirportTextReturn, ArrivalAirportTextReturn, datesText, passengerText;
    Button bookNowButton;
    MaterialToolbar materialToolbar;
    private Integer selectedItem1;
    private Integer selectedItem2;
    int sum = 0;
    JsonObject paxInfo;
    ShimmerFrameLayout mShimmerViewContainerOneway,mShimmerViewContainerReturn;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_flights);
        oneWayRecyclerView = findViewById(R.id.onewayRecycler);
        materialToolbar = findViewById(R.id.toolbar_Return);
        DepartureAirportTextReturn = findViewById(R.id.DepartureAirportTextReturn);
        ArrivalAirportTextReturn = findViewById(R.id.ArrivalAirportTextReturn);
        datesText = findViewById(R.id.datesText);
        passengerText = findViewById(R.id.passengerText);
        returnWayRecyclerView = findViewById(R.id.returnWayRecycler);
        TotalPriceTextView = findViewById(R.id.price_flightTotal);
        bookNowButton = findViewById(R.id.bookNowButtonReturn);
        mShimmerViewContainerOneway=findViewById(R.id.shimmer_view_containerOneway);
        mShimmerViewContainerReturn=findViewById(R.id.shimmer_view_containerReturn);
        mShimmerViewContainerOneway.startShimmerAnimation();
        mShimmerViewContainerReturn.startShimmerAnimation();
        oneWayRecyclerView.setLayoutManager(new LinearLayoutManager(ReturnFlightsActivity.this));
        returnWayRecyclerView.setLayoutManager(new LinearLayoutManager(ReturnFlightsActivity.this));
        setSupportActionBar(materialToolbar);
        materialToolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
        });
        String json = getIntent().getStringExtra("json");
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, new TypeToken<JsonObject>() {
        }.getType());
        String dep = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().getAsJsonObject("fromCityOrAirport").get("code").getAsString();
        String arr = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().getAsJsonObject("toCityOrAirport").get("code").getAsString();
        String depDate = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().get("travelDate").getAsString();
        String arrDate = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(1).getAsJsonObject().get("travelDate").getAsString();
        String Adult = jsonObject.getAsJsonObject("searchQuery").getAsJsonObject("paxInfo").get("ADULT").getAsString();
        String Children = jsonObject.getAsJsonObject("searchQuery").getAsJsonObject("paxInfo").get("CHILD").getAsString();
        String Infant = jsonObject.getAsJsonObject("searchQuery").getAsJsonObject("paxInfo").get("INFANT").getAsString();
        paxInfo = jsonObject.getAsJsonObject("searchQuery").getAsJsonObject("paxInfo");
        int adult = Integer.parseInt(Adult);
        int child = Integer.parseInt(Children);
        int infant = Integer.parseInt(Infant);
        int total = adult + child + infant;
        DepartureAirportTextReturn.setText(dep);
        ArrivalAirportTextReturn.setText(arr);
        passengerText.setText(total + " Traveller");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
        try {
            Date date = sdf1.parse(depDate);
            Date date1 = sdf1.parse(arrDate);
            String d = sdf.format(date);
            String a = sdf.format(date1);
            datesText.setText(d + " - " + a);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


        getFlights(jsonObject,mShimmerViewContainerOneway,mShimmerViewContainerReturn,oneWayRecyclerView, returnWayRecyclerView, this);
    }

    public static void getFlights(JsonObject jsonObject,ShimmerFrameLayout mShimmerViewContainerOneway,ShimmerFrameLayout mShimmerViewContainerReturn, RecyclerView oneWayRecyclerView, RecyclerView returnWayRecyclerView, ReturnFlightsAdapter.AdapterCallback onItemClickListener) {
        Gson gson = new Gson();
        ApiInterface apiInterface = Retrofitt.initretro().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getReturnFlights(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject object = response.body();
                    System.out.println("gson.toJson(object) = " + gson.toJson(object));
                    System.out.println("gson.toJson(object) = " + gson.toJson(object.getAsJsonObject("searchResult").getAsJsonObject("tripInfos")));
                    if (object != null) {
                        List<FlightDetails> flightDetailsList = new ArrayList<>();
                        List<FlightDetails> returnFlightDetailsList = new ArrayList<>();
                        JsonArray RETURN = object.getAsJsonObject("searchResult").getAsJsonObject("tripInfos").getAsJsonArray("RETURN");
                        for (JsonElement js : RETURN) {
                            JsonObject jsonObject1 = js.getAsJsonObject();
                            FlightDetails flightDetails = new FlightDetails();
                            JsonArray sI = jsonObject1.get("sI").getAsJsonArray();
                            flightDetails.setSI(sI.toString());
                            if (sI.size() == 1) {
                                flightDetails.setTotalStops("non-stop");
                                flightDetails.setTotalTime(sI.get(0).getAsJsonObject().get("duration").getAsInt());
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                                SimpleDateFormat df1 = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                String departure = sI.get(0).getAsJsonObject().get("dt").getAsString();
                                String arrival = sI.get(0).getAsJsonObject().get("at").getAsString();
                                try {
                                    Date dof = df.parse(departure);
                                    Date aol = df.parse(arrival);
                                    String dTime = df1.format(dof);
                                    String aTime = df1.format(aol);
                                    System.out.println("aTime = " + aTime + " dTime = " + dTime);
                                    flightDetails.setDepartureTime(dTime);
                                    flightDetails.setArrivalTime(aTime);
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
                                    flightDetails.setDepartureTime(dTime);
                                    flightDetails.setArrivalTime(aTime);
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

                                    System.out.println("departureOfFirst = " + departureOfFirst);
                                    System.out.println("arrivalOfLast = " + arrivalOfLast);
                                    System.out.println(Hours_difference + " h " + Minutes_difference + " m");
                                    System.out.println("Seconds_difference = " + Seconds_difference);
                                    flightDetails.setTotalTime(Math.toIntExact(Seconds_difference));
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                flightDetails.setTotalStops(sI.get(sI.size() - 1).getAsJsonObject().get("sN").getAsInt() + " Stop");
                            }
                            flightDetails.setAirlineName(sI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("name").getAsString());
                            flightDetails.setAirlineImage(sI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("code").getAsString());
                            JsonArray totalPriceList = jsonObject1.get("totalPriceList").getAsJsonArray();
                            flightDetails.setTotalPriceList(totalPriceList.toString());
                            List<Integer> ints = new ArrayList<>();
                            System.out.println("gson.toJson(jsonObject1) = " + gson.toJson(jsonObject1));
                            System.out.println("gson.toJson(totalPriceList) = " + gson.toJson(totalPriceList));
                            for (JsonElement jsonElement : totalPriceList) {
                                JsonObject jsonObject2 = jsonElement.getAsJsonObject();
                                if (!jsonObject2.get("fareIdentifier").getAsString().matches("SPECIAL_RETURN")) {
                                    int price = jsonObject2.getAsJsonObject("fd").getAsJsonObject("ADULT").getAsJsonObject("fC").get("TF").getAsInt();
                                    ints.add(price);
                                } else {
                                    System.out.println("jsonObject2.get(\"fareIdentifier\").getAsString() = " + gson.toJson(jsonObject2));
                                }

                            }
                            System.out.println("ints = " + ints);
                            int temp;
                            for (int i = 0; i < ints.size(); i++) {
                                for (int j = i + 1; j < ints.size(); j++) {
                                    if (ints.get(i) > ints.get(j)) {
                                        temp = ints.get(i);
                                        ints.set(i, ints.get(j));
                                        ints.set(j, temp);
                                    }
                                }
                            }
                            System.out.println("ints = " + ints);
                            if (ints.size() > 0) {
                                int lowPrice = ints.get(0);
                                flightDetails.setTotalPrice(String.valueOf(lowPrice));
                                returnFlightDetailsList.add(flightDetails);
                            }
                            System.out.println("gson.toJson(flightDetails) = " + gson.toJson(flightDetails));
                        }
                        returnFlightDetailsList.sort(new Sortbyroll());
                        System.out.println("gson.toJson(returnFlightDetailsList) = " + gson.toJson(returnFlightDetailsList));
                        ReturnFlightsAdapter returnFlightsAdapter = new ReturnFlightsAdapter(returnFlightDetailsList, returnWayRecyclerView.getContext(), onItemClickListener);
                        returnWayRecyclerView.setAdapter(returnFlightsAdapter);
                        returnFlightsAdapter.notifyDataSetChanged();
                        mShimmerViewContainerReturn.stopShimmerAnimation();
                        mShimmerViewContainerReturn.setVisibility(View.GONE);
//                        int p2 = Integer.parseInt(returnFlightsAdapter.getSelected().getTotalPrice());
                        JsonArray ONWARD = object.getAsJsonObject("searchResult").getAsJsonObject("tripInfos").getAsJsonArray("ONWARD");
                        for (JsonElement js : ONWARD) {
                            JsonObject jsonObject1 = js.getAsJsonObject();
                            FlightDetails flightDetails = new FlightDetails();
                            JsonArray sI = jsonObject1.get("sI").getAsJsonArray();
                            flightDetails.setSI(sI.toString());
                            if (sI.size() == 1) {
                                flightDetails.setTotalStops("non-stop");
                                flightDetails.setTotalTime(sI.get(0).getAsJsonObject().get("duration").getAsInt());
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                                SimpleDateFormat df1 = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                String departure = sI.get(0).getAsJsonObject().get("dt").getAsString();
                                String arrival = sI.get(0).getAsJsonObject().get("at").getAsString();
                                try {
                                    Date dof = df.parse(departure);
                                    Date aol = df.parse(arrival);
                                    String dTime = df1.format(dof);
                                    String aTime = df1.format(aol);
                                    System.out.println("aTime = " + aTime + " dTime = " + dTime);
                                    flightDetails.setDepartureTime(dTime);
                                    flightDetails.setArrivalTime(aTime);
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
                                    flightDetails.setDepartureTime(dTime);
                                    flightDetails.setArrivalTime(aTime);
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

                                    System.out.println("departureOfFirst = " + departureOfFirst);
                                    System.out.println("arrivalOfLast = " + arrivalOfLast);
                                    System.out.println(Hours_difference + " h " + Minutes_difference + " m");
                                    System.out.println("Seconds_difference = " + Seconds_difference);
                                    flightDetails.setTotalTime(Math.toIntExact(Seconds_difference));
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                flightDetails.setTotalStops(sI.get(sI.size() - 1).getAsJsonObject().get("sN").getAsInt() + " Stop");
                            }
                            flightDetails.setAirlineName(sI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("name").getAsString());
                            flightDetails.setAirlineImage(sI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("code").getAsString());
                            JsonArray totalPriceList = jsonObject1.get("totalPriceList").getAsJsonArray();
                            flightDetails.setTotalPriceList(totalPriceList.toString());
                            List<Integer> ints = new ArrayList<>();
                            for (JsonElement jsonElement : totalPriceList) {
                                JsonObject jsonObject2 = jsonElement.getAsJsonObject();
                                if (!jsonObject2.get("fareIdentifier").getAsString().matches("SPECIAL_RETURN")) {
                                    int price = jsonObject2.getAsJsonObject("fd").getAsJsonObject("ADULT").getAsJsonObject("fC").get("TF").getAsInt();
                                    ints.add(price);
                                } else {
                                    System.out.println("jsonObject2.get(\"fareIdentifier\").getAsString() = " + gson.toJson(jsonObject2));
                                }

                            }
                            System.out.println("ints = " + ints);
                            int temp;
                            for (int i = 0; i < ints.size(); i++) {
                                for (int j = i + 1; j < ints.size(); j++) {
                                    if (ints.get(i) > ints.get(j)) {
                                        temp = ints.get(i);
                                        ints.set(i, ints.get(j));
                                        ints.set(j, temp);
                                    }
                                }
                            }
                            System.out.println("ints = " + ints);
                            if (ints.size() > 0) {
                                int lowPrice = ints.get(0);
                                flightDetails.setTotalPrice(String.valueOf(lowPrice));
                                flightDetailsList.add(flightDetails);
                            }
                            System.out.println("gson.toJson(flightDetails) = " + gson.toJson(flightDetails));
                        }
                        flightDetailsList.sort(new Sortbyroll());
                        System.out.println("gson.toJson(flightDetailsList) = " + gson.toJson(flightDetailsList));
                        ReturnFlightsAdapter flightsAdapter = new ReturnFlightsAdapter(flightDetailsList, oneWayRecyclerView.getContext(), onItemClickListener);
                        oneWayRecyclerView.setAdapter(flightsAdapter);
                        flightsAdapter.notifyDataSetChanged();
                        mShimmerViewContainerOneway.stopShimmerAnimation();
                        mShimmerViewContainerOneway.setVisibility(View.GONE);
                        onItemClickListener.updateSum();
//                        int p1 = Integer.parseInt(flightsAdapter.getSelected().getTotalPrice());
//                        int total = p1 + p2;
//                        System.out.println("p1 = " + p1);
//                        System.out.println("p2 = " + p2);
//                        System.out.println("total = " + total);
//                        textView.setText("₹ " + total);

                    }
                } else {
                    System.out.println("response = " + response);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println("t = " + t.toString());
            }
        });
    }


    @Override
    public void onItemClicked(int value) {
        sum += value;
        TotalPriceTextView.setText("₹ " + sum);
    }

    public void updateSum() {
        ReturnFlightsAdapter adapter1 = (ReturnFlightsAdapter) oneWayRecyclerView.getAdapter();
        ReturnFlightsAdapter adapter2 = (ReturnFlightsAdapter) returnWayRecyclerView.getAdapter();
        System.out.println("adapter1.getSelectedItem() = " + adapter1.getSelectedItem());
        System.out.println("adapter2.getSelectedItem() = " + adapter2.getSelectedItem());
        int selectedValue1 = adapter1.getSelectedItem();
        int selectedValue2 = adapter2.getSelectedItem();
        FlightDetails flightDetails = adapter1.getSelectedItemPriceId();
        FlightDetails flightDetails1 = adapter2.getSelectedItemPriceId();
        Gson gson = new Gson();
        System.out.println("gson = " + gson.toJson(flightDetails1));
        JsonArray totalPriceList = gson.fromJson(flightDetails.getTotalPriceList(), new TypeToken<JsonArray>() {
        }.getType());
        JsonArray totalPriceList1 = gson.fromJson(flightDetails1.getTotalPriceList(), new TypeToken<JsonArray>() {
        }.getType());
        List<JsonObject> jsonObjects = new ArrayList<>();
        for (JsonElement jsonElement : totalPriceList) {
            JsonObject jsonObject2 = jsonElement.getAsJsonObject();
            if (!jsonObject2.get("fareIdentifier").getAsString().matches("SPECIAL_RETURN")) {
                jsonObjects.add(jsonObject2);
            }
        }
        jsonObjects.sort(new Sort());
        List<JsonObject> jsonObjects1 = new ArrayList<>();
        for (JsonElement jsonElement : totalPriceList1) {
            JsonObject jsonObject2 = jsonElement.getAsJsonObject();
            if (!jsonObject2.get("fareIdentifier").getAsString().matches("SPECIAL_RETURN")) {
                jsonObjects1.add(jsonObject2);
            }

        }
        jsonObjects1.sort(new Sort());
        String pId1 = jsonObjects.get(0).get("id").getAsString();
        String pId2 = jsonObjects1.get(0).get("id").getAsString();
        List<String> stringList = new ArrayList<>();
        stringList.add(pId1);
        stringList.add(pId2);
        String[] strings = new String[]{pId1, pId2};
        bookNowButton.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("paxInfo", String.valueOf(paxInfo));
            bundle.putString("priceIds", String.valueOf(stringList));
            bundle.putSerializable("flightDetails", flightDetails);
            bundle.putSerializable("returnFlightDetails", flightDetails1);
            startActivity(new Intent(ReturnFlightsActivity.this, ReturnFlightCheckoutActivity.class).putExtra("bundle", bundle));
        });
        sum = selectedValue1 + selectedValue2;
        TotalPriceTextView.setText("₹ " + sum);
    }
}