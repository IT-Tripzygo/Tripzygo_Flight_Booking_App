package in.tripzygo.tripzygoflightbookingapp.Fragments;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
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
import in.tripzygo.tripzygoflightbookingapp.R;
import in.tripzygo.tripzygoflightbookingapp.ReturnFlightsAdapter;
import in.tripzygo.tripzygoflightbookingapp.SortByPrice;
import in.tripzygo.tripzygoflightbookingapp.Tools.Sort;

public class DomesticReturnFragment extends Fragment {
    RecyclerView returnWayRecyclerView;
    ShimmerFrameLayout mShimmerViewContainerReturn;
    JsonObject paxInfo,jsonObject;
    String type, id;
    FragmentManager fragmentManager;
    JsonArray RETURN;
    FlightDetails flightDetails;
    TextView airlineName, departureTime, departureCity, arrivalTime, arrivalCity, totalPrice, totalStops, totalTime, FareType, classType, viewFares, departingTextView, returningTextView;
    ImageView airlineImage;


    public DomesticReturnFragment(JsonObject paxInfo, String type, String id, FragmentManager fragmentManager, JsonArray RETURN, FlightDetails flightDetails,JsonObject jsonObject) {
        this.paxInfo = paxInfo;
        this.type = type;
        this.id = id;
        this.fragmentManager = fragmentManager;
        this.RETURN = RETURN;
        this.flightDetails = flightDetails;
        this.jsonObject=jsonObject;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_domestic_return, container, false);
        returnWayRecyclerView = view.findViewById(R.id.returnWayRecycler);
        returnWayRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mShimmerViewContainerReturn = view.findViewById(R.id.shimmer_view_containerReturn);
        mShimmerViewContainerReturn.startShimmerAnimation();
        airlineName = view.findViewById(R.id.airline_name);
        airlineImage = view.findViewById(R.id.airlineImage);
        departureTime = view.findViewById(R.id.DepartureText);
        departureCity = view.findViewById(R.id.DepartureCityText);
        arrivalTime = view.findViewById(R.id.ArrivalText);
        arrivalCity = view.findViewById(R.id.ArrivalCityText);
        totalPrice = view.findViewById(R.id.price);
        totalStops = view.findViewById(R.id.stop);
        totalTime = view.findViewById(R.id.time);
        FareType = view.findViewById(R.id.FareType);
        classType = view.findViewById(R.id.classType);
        departingTextView = view.findViewById(R.id.departingTextView);
        returningTextView = view.findViewById(R.id.returningTextView);
        airlineName.setText(flightDetails.getAirlineName());
        totalStops.setText(flightDetails.getTotalStops());
        departureTime.setText(flightDetails.getDepartureTime());
        arrivalTime.setText(flightDetails.getArrivalTime());
        int timeInMinutes = flightDetails.getTotalTime();
        long hours = timeInMinutes / 60;
        long minutes = timeInMinutes % 60;
        totalTime.setText(hours + "h " + minutes + "m");
        String depDate = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().get("travelDate").getAsString();
        String arrDate = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(1).getAsJsonObject().get("travelDate").getAsString();
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM", Locale.getDefault());
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf1.parse(depDate);
            Date date1 = sdf1.parse(arrDate);
            String d = sdf.format(date);
            String a = sdf.format(date1);
            departingTextView.setText("Departing Flight ("+d+")");
            returningTextView.setText("Returning Flight ("+a+")");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Gson gson = new Gson();
        JsonArray f1sI = gson.fromJson(flightDetails.getSI(), new TypeToken<JsonArray>() {
        }.getType());
        String f1departureOfFirst = f1sI.get(0).getAsJsonObject().getAsJsonObject("da").get("cityCode").getAsString();
        String f1arrivalOfLast = f1sI.get(f1sI.size() - 1).getAsJsonObject().getAsJsonObject("aa").get("cityCode").getAsString();
        departureCity.setText(f1departureOfFirst);
        arrivalCity.setText(f1arrivalOfLast);
        JsonArray f1totalPriceList = gson.fromJson(flightDetails.getTotalPriceList(), new TypeToken<JsonArray>() {
        }.getType());
        List<JsonObject> jsonObjects = new ArrayList<>();
        for (JsonElement jsonElement : f1totalPriceList) {
            JsonObject jsonObject2 = jsonElement.getAsJsonObject();
            jsonObjects.add(jsonObject2);
        }
        jsonObjects.sort(new Sort());
        Locale locale = new Locale.Builder().setLanguage("en").setRegion("IN").build();
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        int tf = jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").getAsJsonObject("fC").get("TF").getAsInt();
        String currency = formatter.format(tf);
        int centsIndex = currency.lastIndexOf(".00");
        if (centsIndex != -1) {
            currency = currency.substring(0, centsIndex);
        }
        totalPrice.setText(currency);
        if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").has("rT")) {
            ColorStateList colorStateList;
            if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 0) {
                FareType.setText("NR");
                colorStateList = ColorStateList.valueOf(getContext().getResources().getColor(R.color.red));
                FareType.setBackgroundTintList(colorStateList);
            } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 1) {
                FareType.setText("RF");
                colorStateList = ColorStateList.valueOf(getContext().getResources().getColor(R.color.forestGreen));
                FareType.setBackgroundTintList(colorStateList);
            } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 2) {
                FareType.setText("PR");
                colorStateList = ColorStateList.valueOf(getContext().getResources().getColor(R.color.green));
                FareType.setBackgroundTintList(colorStateList);
            }
        }
        if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString().equalsIgnoreCase("ECONOMY")) {
            classType.setText("EC");
        } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString().equalsIgnoreCase("Premium Economy")) {
            classType.setText("PE");
        } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString().equalsIgnoreCase("Business")) {
            classType.setText("BS");
        } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString().equalsIgnoreCase("First")) {
            classType.setText("FC");
        }
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference storageReference1 = storageReference.child("AirlineLogos").child(flightDetails.getAirlineImage() + ".png");
        storageReference1.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(getContext()).load(uri).into(airlineImage);
        }).addOnFailureListener(e -> {
            System.out.println("storageReference1 = " + storageReference1);
            System.out.println("e.getMessage() = " + e.getMessage());
        });
        List<FlightDetails> returnFlightDetailsList = new ArrayList<>();

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
        ReturnFlightsAdapter returnFlightsAdapter = new ReturnFlightsAdapter(returnFlightDetailsList, flightDetails, returnWayRecyclerView.getContext(), type, fragmentManager, paxInfo, id);
        returnWayRecyclerView.setAdapter(returnFlightsAdapter);
        mShimmerViewContainerReturn.stopShimmerAnimation();
        mShimmerViewContainerReturn.setVisibility(View.GONE);
        return view;
    }
}