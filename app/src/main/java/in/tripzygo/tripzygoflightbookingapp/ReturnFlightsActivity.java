package in.tripzygo.tripzygoflightbookingapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import in.tripzygo.tripzygoflightbookingapp.Fragments.DomesticOnwardFragment;

public class ReturnFlightsActivity extends AppCompatActivity {
    TextView DepartureAirportTextReturn, ArrivalAirportTextReturn, datesText, passengerText;
    MaterialToolbar materialToolbar;
    JsonObject paxInfo;
    ImageView filterImageView;
    FloatingActionButton floatingActionButton;

/*    public static void getFlights(JsonObject paxInfo,
                                  String type,
                                  JsonObject jsonObject,
                                  ShimmerFrameLayout mShimmerViewContainerOneway,
                                  ShimmerFrameLayout mShimmerViewContainerReturn,
                                  ShimmerFrameLayout mShimmerViewContainerReturnInternational,
                                  RecyclerView oneWayRecyclerView,
                                  RecyclerView returnWayRecyclerView,
                                  RecyclerView returnWayInternationalRecyclerView,
                                  LinearLayout domesticLinearLayout,
                                  LinearLayout domesticShimmerLinearLayout,
                                  ReturnFlightsOnwardAdapter.AdapterCallback onItemClickListener,
                                  FloatingActionButton floatingActionButton,
                                  FragmentManager fragmentManager,
                                  Filters filters, RelativeLayout relativeLayout) {
        Gson gson = new Gson();
        ApiInterface apiInterface = Retrofitt.initretro().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getReturnFlights(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject object = response.body();
//                    System.out.println("gson.toJson(object) = " + gson.toJson(object));
//                    System.out.println("gson.toJson(object) = " + gson.toJson(object.getAsJsonObject("searchResult").getAsJsonObject("tripInfos")));
                    if (object != null) {
                        List<FlightDetails> flightDetailsList = new ArrayList<>();
                        List<FlightDetails> returnFlightDetailsList = new ArrayList<>();
                        if (type.equalsIgnoreCase("Domestic")) {
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

                            returnFlightDetailsList.sort(new SortByPrice());
                            System.out.println("gson.toJson(returnFlightDetailsList) = " + gson.toJson(returnFlightDetailsList));
                            ReturnFlightsOnwardAdapter returnFlightsAdapter = new ReturnFlightsOnwardAdapter(returnFlightDetailsList, returnWayRecyclerView.getContext(), onItemClickListener, type, fragmentManager, paxInfo);
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
                            flightDetailsList.sort(new SortByPrice());
                            System.out.println("gson.toJson(flightDetailsList) = " + gson.toJson(flightDetailsList));
                            ReturnFlightsOnwardAdapter flightsAdapter = new ReturnFlightsOnwardAdapter(flightDetailsList, oneWayRecyclerView.getContext(), onItemClickListener, type, fragmentManager, paxInfo);
                            oneWayRecyclerView.setAdapter(flightsAdapter);
                            flightsAdapter.notifyDataSetChanged();
                            mShimmerViewContainerOneway.stopShimmerAnimation();
                            mShimmerViewContainerOneway.setVisibility(View.GONE);

                            floatingActionButton.setOnClickListener(view -> {
                                FilterDialog filterDialog = new FilterDialog(oneWayRecyclerView.getContext(), oneWayRecyclerView, returnWayRecyclerView, onItemClickListener, returnWayInternationalRecyclerView, mShimmerViewContainerReturnInternational, mShimmerViewContainerReturn, mShimmerViewContainerOneway, paxInfo, filters, type, jsonObject, true, domesticLinearLayout, domesticShimmerLinearLayout, relativeLayout, flightDetailsList, returnFlightDetailsList, fragmentManager);
                                filterDialog.setCancelable(false);
                                filterDialog.show(fragmentManager, "filters");
                            });
                        } else {
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
                            returnWayInternationalRecyclerView.setLayoutManager(new LinearLayoutManager(returnWayInternationalRecyclerView.getContext()));
                            returnWayInternationalRecyclerView.setAdapter(internationalReturnFlightsAdapter);
                            mShimmerViewContainerReturnInternational.stopShimmerAnimation();
                            mShimmerViewContainerReturnInternational.setVisibility(View.GONE);
                            floatingActionButton.setOnClickListener(view -> {
                                FilterDialog filterDialog = new FilterDialog(returnWayInternationalRecyclerView.getContext(), returnWayInternationalRecyclerView, mShimmerViewContainerReturnInternational, paxInfo, filters, type, jsonObject, true, domesticLinearLayout, domesticShimmerLinearLayout, relativeLayout, flightDetailsList, fragmentManager);
                                filterDialog.setCancelable(false);
                                filterDialog.show(fragmentManager, "filters");
                            });
//                            System.out.println("gson.toJson(flightDetailsList) = " + gson.toJson(flightDetailsList));
//                            ReturnFlightsAdapter flightsAdapter = new ReturnFlightsAdapter(flightDetailsList, oneWayRecyclerView.getContext(), onItemClickListener);
//                            oneWayRecyclerView.setAdapter(flightsAdapter);
//                            flightsAdapter.notifyDataSetChanged();
//                            mShimmerViewContainerOneway.stopShimmerAnimation();
//                            mShimmerViewContainerOneway.setVisibility(View.GONE);
//                            returnFlightDetailsList.sort(new Sortbyroll());
//                            System.out.println("gson.toJson(returnFlightDetailsList) = " + gson.toJson(returnFlightDetailsList));
//                            ReturnFlightsAdapter returnFlightsAdapter = new ReturnFlightsAdapter(returnFlightDetailsList, returnWayRecyclerView.getContext(), onItemClickListener);
//                            returnWayRecyclerView.setAdapter(returnFlightsAdapter);
//                            returnFlightsAdapter.notifyDataSetChanged();
//                            mShimmerViewContainerReturn.stopShimmerAnimation();
//                            mShimmerViewContainerReturn.setVisibility(View.GONE);
//                            onItemClickListener.updateSum();
                            System.out.println("type = " + type);
                        }
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
    }*/

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_flights);
        materialToolbar = findViewById(R.id.toolbar_Return);
        DepartureAirportTextReturn = findViewById(R.id.DepartureAirportTextReturn);
        ArrivalAirportTextReturn = findViewById(R.id.ArrivalAirportTextReturn);
        datesText = findViewById(R.id.datesText);
        floatingActionButton = findViewById(R.id.floatingReturn);
        filterImageView = findViewById(R.id.edit);
        passengerText = findViewById(R.id.passengerText);
        materialToolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
        });
        String json = getIntent().getStringExtra("json");
        String type = getIntent().getStringExtra("type");
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

        assert type != null;
        if (type.equalsIgnoreCase("Domestic")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_return, new DomesticOnwardFragment(paxInfo, jsonObject, type, getSupportFragmentManager())).addToBackStack("yes").commit();
        }

    }

  /*  @Override
    public void onItemClicked(int value) {
        sum += value;
        TotalPriceTextView.setText("₹ " + sum);
    }

    public void updateSum(String type) {
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
            bundle.putString("type", type);
            bundle.putSerializable("flightDetails", flightDetails);
            bundle.putSerializable("returnFlightDetails", flightDetails1);
            startActivity(new Intent(ReturnFlightsActivity.this, ReturnFlightCheckoutActivity.class).putExtra("bundle", bundle));
        });
        sum = selectedValue1 + selectedValue2;
        Locale locale = new Locale.Builder().setLanguage("en").setRegion("IN").build();
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        String currency = formatter.format(sum);
        int centsIndex = currency.lastIndexOf(".00");
        if (centsIndex != -1) {
            currency = currency.substring(0, centsIndex);
        }
        TotalPriceTextView.setText(currency);
    }*/
}