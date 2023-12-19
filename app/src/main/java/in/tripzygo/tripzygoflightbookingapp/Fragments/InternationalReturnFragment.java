package in.tripzygo.tripzygoflightbookingapp.Fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
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

import in.tripzygo.tripzygoflightbookingapp.InternationalReturnFlightsAdapter;
import in.tripzygo.tripzygoflightbookingapp.Modals.FlightDetails;
import in.tripzygo.tripzygoflightbookingapp.R;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.ApiInterface;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.Retrofitt;
import in.tripzygo.tripzygoflightbookingapp.SortByPrice;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InternationalReturnFragment extends AppCompatActivity {
    TextView DepartureAirportTextReturn, ArrivalAirportTextReturn, datesText, passengerText;

    RecyclerView internationalReturnWayRecyclerView;
    ShimmerFrameLayout mShimmerViewContainerReturnInternational;
    MaterialToolbar materialToolbar;
    JsonObject paxInfo;
    ImageView filterImageView;
    String type;

    public static void getFlights(JsonObject paxInfo, String type, JsonObject jsonObject, ShimmerFrameLayout mShimmerViewContainerReturnInternational, RecyclerView returnWayInternationalRecyclerView, FragmentManager fragmentManager) {
        Gson gson = new Gson();
        ApiInterface apiInterface = Retrofitt.initretro().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getReturnFlights(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject object = response.body();
                    if (object != null) {
                        List<FlightDetails> flightDetailsList = new ArrayList<>();
                        List<FlightDetails> returnFlightDetailsList = new ArrayList<>();
                        System.out.println("gson.toJson(object) = " + gson.toJson(object));
                        System.out.println("object.getAsJsonObject(\"searchResult\").getAsJsonObject(\"tripInfos\") = " + object.getAsJsonObject("searchResult").getAsJsonObject("tripInfos"));
                        JsonArray COMBO = object.getAsJsonObject("searchResult").getAsJsonObject("tripInfos").getAsJsonArray("COMBO");
                        for (JsonElement js : COMBO) {
                            JsonObject jsonObject1 = js.getAsJsonObject();
                            JsonArray flightsI = jsonObject1.getAsJsonArray("sI");
                            String depCity = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().get("fromCityOrAirport").getAsJsonObject().get("code").getAsString();
                            String arrCity = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().get("toCityOrAirport").getAsJsonObject().get("code").getAsString();
                            String retDepCity = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(1).getAsJsonObject().get("fromCityOrAirport").getAsJsonObject().get("code").getAsString();
                            String retArrCity = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(1).getAsJsonObject().get("toCityOrAirport").getAsJsonObject().get("code").getAsString();
                            List<JsonObject> depJsonObjects = new ArrayList<>();
                            List<JsonObject> arrJsonObjects = new ArrayList<>();
                            if (flightsI.size() > 2) {
                                int j = 0;
                                for (int i = 0; i < flightsI.size(); i++) {
                                    JsonObject flightObject1 = flightsI.get(i).getAsJsonObject();
                                    if (flightObject1.getAsJsonObject("aa").get("code").getAsString().matches(arrCity)) {

                                    }

                                }
                                for (int i = j; i < flightsI.size(); i++) {
                                    JsonObject flightObject1 = flightsI.get(i).getAsJsonObject();
                                    arrJsonObjects.add(flightObject1);
                                    if (flightObject1.getAsJsonObject("aa").get("code").getAsString().matches(retArrCity)) {
                                        break;
                                    }
                                }
                            }
                            FlightDetails flightDetails = new FlightDetails();
                            flightDetails.setAirlineName(flightsI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("name").getAsString());
                            flightDetails.setAirlineImage(flightsI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("code").getAsString());
                            flightDetails.setSI(flightsI.toString());
                            JsonArray totalPriceList = jsonObject1.get("totalPriceList").getAsJsonArray();
                            flightDetails.setTotalPriceList(totalPriceList.toString());
                            List<Integer> ints = new ArrayList<>();
                            for (JsonElement jsonElement : totalPriceList) {
                                JsonObject jsonObject3 = jsonElement.getAsJsonObject();
                                if (!jsonObject3.get("fareIdentifier").getAsString().matches("SPECIAL_RETURN")) {
                                    int price = jsonObject3.getAsJsonObject("fd").getAsJsonObject("ADULT").getAsJsonObject("fC").get("TF").getAsInt();
                                    ints.add(price);
                                } else {
                                    System.out.println("jsonObject2.get(\"fareIdentifier\").getAsString() = " + gson.toJson(jsonObject3));
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
                        }
                        flightDetailsList.sort(new SortByPrice());
                        InternationalReturnFlightsAdapter internationalReturnFlightsAdapter = new InternationalReturnFlightsAdapter(flightDetailsList, returnWayInternationalRecyclerView.getContext(), paxInfo, jsonObject, fragmentManager);
                        System.out.println(internationalReturnFlightsAdapter.getItemCount());
                        System.out.println("flightDetailsList = " + flightDetailsList.size());
                        returnWayInternationalRecyclerView.setAdapter(internationalReturnFlightsAdapter);
                        mShimmerViewContainerReturnInternational.stopShimmerAnimation();
                        mShimmerViewContainerReturnInternational.setVisibility(View.GONE);


                    } else {
                        System.out.println("re = " + response);
                    }
                } else {
                    System.out.println("response = " + response);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println("t = " + t);
            }
        });
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_international_return);
        materialToolbar = findViewById(R.id.toolbar_ReturnInternational);
        DepartureAirportTextReturn = findViewById(R.id.DepartureAirportTextReturn);
        ArrivalAirportTextReturn = findViewById(R.id.ArrivalAirportTextReturn);
        datesText = findViewById(R.id.datesText);
        filterImageView = findViewById(R.id.edit);
        passengerText = findViewById(R.id.passengerText);
        materialToolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
        });
        internationalReturnWayRecyclerView = findViewById(R.id.internationalReturnWayRecycler);
        mShimmerViewContainerReturnInternational = findViewById(R.id.shimmer_view_containerReturnInternational);
        mShimmerViewContainerReturnInternational.startShimmerAnimation();
        internationalReturnWayRecyclerView.setLayoutManager(new LinearLayoutManager(InternationalReturnFragment.this));
        String json = getIntent().getStringExtra("json");
        type = getIntent().getStringExtra("type");
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, new TypeToken<JsonObject>() {
        }.getType());
        paxInfo = jsonObject.getAsJsonObject("searchQuery").getAsJsonObject("paxInfo");
        String toAirportCity = getIntent().getStringExtra("toAirportCity");
        String fromAirportCity = getIntent().getStringExtra("fromAirportCity");
        String dep = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().getAsJsonObject("fromCityOrAirport").get("code").getAsString();
        String arr = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().getAsJsonObject("toCityOrAirport").get("code").getAsString();
        String depDate = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().get("travelDate").getAsString();
        String arrDate = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(1).getAsJsonObject().get("travelDate").getAsString();
        String Adult = jsonObject.getAsJsonObject("searchQuery").getAsJsonObject("paxInfo").get("ADULT").getAsString();
        String Children = jsonObject.getAsJsonObject("searchQuery").getAsJsonObject("paxInfo").get("CHILD").getAsString();
        String Infant = jsonObject.getAsJsonObject("searchQuery").getAsJsonObject("paxInfo").get("INFANT").getAsString();
        int adult = Integer.parseInt(Adult);
        int child = Integer.parseInt(Children);
        int infant = Integer.parseInt(Infant);
        int total = adult + child + infant;
        DepartureAirportTextReturn.setText(fromAirportCity);
        ArrivalAirportTextReturn.setText(toAirportCity);
        passengerText.setText(total + " Traveller");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yy", Locale.getDefault());
        try {
            Date date = sdf1.parse(depDate);
            Date date1 = sdf1.parse(arrDate);
            String d = sdf.format(date);
            String a = sdf.format(date1);
            datesText.setText(d + " - " + a);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        filterImageView.setOnClickListener(view -> {
            super.onBackPressed();
        });
        System.out.println("paxInfo = " + paxInfo);
        System.out.println("type = " + type);
        System.out.println("jsonObject = " + jsonObject);
        getFlights(paxInfo, type, jsonObject, mShimmerViewContainerReturnInternational, internationalReturnWayRecyclerView, getSupportFragmentManager());
    }
}