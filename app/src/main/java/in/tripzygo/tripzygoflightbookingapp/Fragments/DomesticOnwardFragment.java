package in.tripzygo.tripzygoflightbookingapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.tripzygo.tripzygoflightbookingapp.Modals.FlightDetails;
import in.tripzygo.tripzygoflightbookingapp.R;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.ApiInterface;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.Retrofitt;
import in.tripzygo.tripzygoflightbookingapp.ReturnFlightsOnwardAdapter;
import in.tripzygo.tripzygoflightbookingapp.SortByPrice;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DomesticOnwardFragment extends Fragment {


    RecyclerView oneWayRecyclerView;
    ShimmerFrameLayout mShimmerViewContainerOneway;
    JsonObject paxInfo, jsonObject;
    String type;
    FragmentManager fragmentManager;
    TextView departingTextView;



    public DomesticOnwardFragment(JsonObject paxInfo, JsonObject jsonObject, String type, FragmentManager fragmentManager) {
        this.paxInfo = paxInfo;
        this.jsonObject = jsonObject;
        this.type = type;
        this.fragmentManager = fragmentManager;

    }

    public static void getFlights(JsonObject paxInfo,
                                  String type,
                                  JsonObject jsonObject,
                                  ShimmerFrameLayout mShimmerViewContainerOneway,
                                  RecyclerView oneWayRecyclerView,
                                  FragmentManager fragmentManager) {
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

                        JsonArray RETURN = object.getAsJsonObject("searchResult").getAsJsonObject("tripInfos").getAsJsonArray("RETURN");

                        System.out.println("gson.toJson(returnFlightDetailsList) = " + gson.toJson(returnFlightDetailsList));
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
                        ReturnFlightsOnwardAdapter flightsAdapter = new ReturnFlightsOnwardAdapter(flightDetailsList, RETURN, oneWayRecyclerView.getContext(), type, fragmentManager, paxInfo,jsonObject);
                        oneWayRecyclerView.setAdapter(flightsAdapter);
                        flightsAdapter.notifyDataSetChanged();
                        mShimmerViewContainerOneway.stopShimmerAnimation();
                        mShimmerViewContainerOneway.setVisibility(View.GONE);

                           /* floatingActionButton.setOnClickListener(view -> {
                                FilterDialog filterDialog = new FilterDialog(oneWayRecyclerView.getContext(), oneWayRecyclerView, returnWayRecyclerView, onItemClickListener, returnWayInternationalRecyclerView, mShimmerViewContainerReturnInternational, mShimmerViewContainerReturn, mShimmerViewContainerOneway, paxInfo, filters, type, jsonObject, true, domesticLinearLayout, domesticShimmerLinearLayout, relativeLayout, flightDetailsList, returnFlightDetailsList, fragmentManager);
                                filterDialog.setCancelable(false);
                                filterDialog.show(fragmentManager, "filters");
                            });*/

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_domestic_onward, container, false);
        departingTextView=view.findViewById(R.id.departingTextView);
        oneWayRecyclerView = view.findViewById(R.id.onewayRecycler);
        oneWayRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mShimmerViewContainerOneway = view.findViewById(R.id.shimmer_view_containerOneway);
        mShimmerViewContainerOneway.startShimmerAnimation();
        String depDate = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().get("travelDate").getAsString();
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM", Locale.getDefault());
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf1.parse(depDate);
            String d = sdf.format(date);
            departingTextView.setText("Departing Flight ("+d+")");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        getFlights(paxInfo,
                type,
                jsonObject,
                mShimmerViewContainerOneway,
                oneWayRecyclerView,
                fragmentManager
        );

        return view;
    }
}