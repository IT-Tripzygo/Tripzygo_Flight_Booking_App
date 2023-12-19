package in.tripzygo.tripzygoflightbookingapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import in.tripzygo.tripzygoflightbookingapp.Adapter;
import in.tripzygo.tripzygoflightbookingapp.FlightsAdapter;
import in.tripzygo.tripzygoflightbookingapp.InternationalReturnFlightsAdapter;
import in.tripzygo.tripzygoflightbookingapp.Modals.Filters;
import in.tripzygo.tripzygoflightbookingapp.Modals.FlightDetails;
import in.tripzygo.tripzygoflightbookingapp.Modals.Modelclass;
import in.tripzygo.tripzygoflightbookingapp.R;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.ApiInterface;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.Retrofitt;
import in.tripzygo.tripzygoflightbookingapp.SortByPrice;
import it.mirko.rangeseekbar.OnRangeSeekBarListener;
import it.mirko.rangeseekbar.RangeSeekBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterDialog extends BottomSheetDialogFragment implements OnRangeSeekBarListener {
    Context context;
    TextView rate1, rate2, earlyMorningTime, morningTime, afternoonTime, nightTime, filters, departureText;
    SwitchCompat refundableSwitch, allSwitch;

    CheckBox nonstopCheckBox, oneStopCheckBox, earlyMorning, morning, afternoon, night;
    RecyclerView recyclerView, flightRecyclerView;
    RecyclerView returnWayRecyclerView;
    RecyclerView returnWayInternationalRecyclerView;
    ShimmerFrameLayout mShimmerViewContainerReturn;
    ShimmerFrameLayout mShimmerViewContainerReturnInternational;
    ArrayList<Modelclass> modelclassArrayList = new ArrayList<>();
    List<FlightDetails> flightDetailsList = new ArrayList<>();
    List<FlightDetails> flightDetailsList1 = new ArrayList<>();
    List<FlightDetails> returnFlightDetailsList = new ArrayList<>();
    JsonObject paxInfo;
    Button applyFilter, resetFilter;
    Filters filter;
    String type;
    boolean isDirectFlight = true, isConnectingFlight = true;
    JsonObject jsonObject;
    ShimmerFrameLayout mShimmerViewContainer;
    boolean isReturn;
    String Date;
    LinearLayout domesticLinearLayout, domesticShimmerLinearLayout;
    RelativeLayout relativeLayout;
    FragmentManager fragmentManager;
    private RangeSeekBar rangeSeekBar;
    private TextView startText;
    private TextView endText;


    public FilterDialog(String type, String date, JsonObject jsonObject, Context context, RecyclerView flightRecyclerView, List<FlightDetails> flightDetailsList, JsonObject paxInfo, Filters filter, ShimmerFrameLayout mShimmerViewContainer, boolean isReturn, FragmentManager fragmentManager) {
        this.context = context;
        this.flightRecyclerView = flightRecyclerView;
        this.flightDetailsList = flightDetailsList;
        this.paxInfo = paxInfo;
        this.type = type;
        this.filter = filter;
        this.mShimmerViewContainer = mShimmerViewContainer;
        this.jsonObject = jsonObject;
        this.isReturn = isReturn;
        this.Date = date;
        this.fragmentManager = fragmentManager;
    }

    public FilterDialog(Context context, RecyclerView flightRecyclerView, RecyclerView returnWayRecyclerView, RecyclerView returnWayInternationalRecyclerView, ShimmerFrameLayout mShimmerViewContainerReturnInternational, ShimmerFrameLayout mShimmerViewContainerReturn, ShimmerFrameLayout mShimmerViewContainer, JsonObject paxInfo, Filters filter, String type, JsonObject jsonObject, boolean isReturn, LinearLayout domesticLinearLayout, LinearLayout domesticShimmerLinearLayout, RelativeLayout relativeLayout, List<FlightDetails> flightDetailsList, List<FlightDetails> returnFlightDetailsList,FragmentManager fragmentManager) {
        this.context = context;
        this.flightRecyclerView = flightRecyclerView;
        this.returnWayRecyclerView = returnWayRecyclerView;
        this.mShimmerViewContainer = mShimmerViewContainer;
        this.mShimmerViewContainerReturn = mShimmerViewContainerReturn;
        this.filter = filter;
        this.type = type;
        this.jsonObject = jsonObject;
        this.isReturn = isReturn;
        this.paxInfo = paxInfo;
        this.domesticLinearLayout = domesticLinearLayout;
        this.domesticShimmerLinearLayout = domesticShimmerLinearLayout;
        this.relativeLayout = relativeLayout;
        this.flightDetailsList = flightDetailsList;
        this.returnFlightDetailsList = returnFlightDetailsList;
        this.returnWayInternationalRecyclerView = returnWayInternationalRecyclerView;
        this.mShimmerViewContainerReturnInternational = mShimmerViewContainerReturnInternational;
        this.fragmentManager=fragmentManager;
    }

    public FilterDialog(Context context, RecyclerView returnWayInternationalRecyclerView, ShimmerFrameLayout mShimmerViewContainerReturnInternational, JsonObject paxInfo, Filters filter, String type, JsonObject jsonObject, boolean isReturn, LinearLayout domesticLinearLayout, LinearLayout domesticShimmerLinearLayout, RelativeLayout relativeLayout, List<FlightDetails> flightDetailsList,FragmentManager fragmentManager) {
        this.context = context;
        this.returnWayInternationalRecyclerView = returnWayInternationalRecyclerView;
        this.mShimmerViewContainerReturnInternational = mShimmerViewContainerReturnInternational;
        this.paxInfo = paxInfo;
        this.filter = filter;
        this.type = type;
        this.jsonObject = jsonObject;
        this.isReturn = isReturn;
        this.domesticLinearLayout = domesticLinearLayout;
        this.domesticShimmerLinearLayout = domesticShimmerLinearLayout;
        this.relativeLayout = relativeLayout;
        this.flightDetailsList = flightDetailsList;
        this.fragmentManager=fragmentManager;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        BottomSheetBehavior<android.widget.FrameLayout> bottomSheetBehavior = dialog.getBehavior();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        // Do something with your dialog like setContentView() or whatever
        return dialog;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.filterscreen, null);
        dialog.setContentView(view);

    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.filterscreen, container,
                false);
        Gson gson = new Gson();
        applyFilter = view.findViewById(R.id.applyFilter);
        resetFilter = view.findViewById(R.id.resetFilter);
        departureText = view.findViewById(R.id.departure);
        rate1 = view.findViewById(R.id.start);
        rate2 = view.findViewById(R.id.end);
        filters = view.findViewById(R.id.filters);
        earlyMorningTime = view.findViewById(R.id.earlyMorningTime);
        morningTime = view.findViewById(R.id.morningTime);
        afternoonTime = view.findViewById(R.id.afternoonTime);
        nightTime = view.findViewById(R.id.nightTime);
        refundableSwitch = view.findViewById(R.id.refundablebtn);
        allSwitch = view.findViewById(R.id.allbtn);
        nonstopCheckBox = view.findViewById(R.id.check1);
        oneStopCheckBox = view.findViewById(R.id.check2);
        earlyMorning = view.findViewById(R.id.earlyMorning);
        morning = view.findViewById(R.id.morning);
        afternoon = view.findViewById(R.id.afternoon);
        night = view.findViewById(R.id.night);
        String depDate12 = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().get("travelDate").getAsString();
        System.out.println("depDate = " + depDate12);
        recyclerView = view.findViewById(R.id.recyclerview);
        rangeSeekBar = view.findViewById(R.id.rangeSeekBar);
        if (filter != null) {
            nonstopCheckBox.setChecked(filter.isNonstop());
            oneStopCheckBox.setChecked(filter.isOneStop());
            if (filter.isNonstop()) {
                isDirectFlight = true;
                isConnectingFlight = false;
            } else if (filter.isOneStop()) {
                isDirectFlight = false;
                isConnectingFlight = true;
            }
        }
        nonstopCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                filter.setNonstop(b);
                if (b) {
                    isDirectFlight = true;
                    isConnectingFlight = false;
                } else {
                    isConnectingFlight = true;
                    isDirectFlight = true;
                }
            }
        });
        oneStopCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                filter.setOneStop(b);
                if (b) {
                    isDirectFlight = false;
                    isConnectingFlight = true;
                } else {
                    isConnectingFlight = true;
                    isDirectFlight = true;
                }
            }
        });
        JsonArray sI = gson.fromJson(flightDetailsList.get(0).getSI(), new TypeToken<JsonArray>() {
        }.getType());
        departureText.setText("Departure from " + sI.get(0).getAsJsonObject().getAsJsonObject("da").get("city").getAsString());
        for (FlightDetails flightDetails : flightDetailsList) {
            modelclassArrayList.add(new Modelclass(flightDetails.getAirlineImage(), flightDetails.getAirlineName(), flightDetails.getTotalPrice(), false));
        }
        List<String> uniqueNamesList = getUniqueNamesFromModalClassList1((ArrayList<FlightDetails>) flightDetailsList);

        System.out.println("gson.toJson(uniqueNamesList) = " + gson.toJson(uniqueNamesList));
        List<String> uniqueNamesFromList2 = new ArrayList<>();
        for (Modelclass modalClass : modelclassArrayList) {
            if (!uniqueNamesFromList2.contains(modalClass.getName())) {
                uniqueNamesFromList2.add(modalClass.getName());
            }
        }
        Set<String> uniqueNames = new HashSet<>();
        List<Modelclass> filteredList = new ArrayList<>();
        for (Modelclass item : modelclassArrayList) {
            if (uniqueNames.add(item.getName())) {
                // If the name is not already in the Set, add the item to the filtered list
                filteredList.add(item);
            }
        }
        JSONArray preferredAirline = new JSONArray();
        System.out.println("gson.toJson(modelclassArrayList) = " + gson.toJson(modelclassArrayList));
        System.out.println("gson.toJson(filteredList) = " + gson.toJson(filteredList));
        Adapter adapter = new Adapter(filteredList, flightDetailsList, preferredAirline, context);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        setupViews(Integer.parseInt(flightDetailsList.get(0).getTotalPrice()), Integer.parseInt(flightDetailsList.get(flightDetailsList.size() - 1).getTotalPrice()));

        allSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    adapter.setAllChecked();
                } else {
                    adapter.unselectAll();
                }
            }
        });

        filters.setOnClickListener(view1 -> {
            flightDetailsList1.clear();
            getDialog().dismiss();

        });
        resetFilter.setOnClickListener(view1 -> {
            filter = new Filters();
            oneStopCheckBox.setChecked(false);
            nonstopCheckBox.setChecked(false);
            adapter.unselectAll();
        });
        applyFilter.setOnClickListener(view1 -> {

            getDialog().dismiss();
            System.out.println("gson = " + gson.toJson(jsonObject));
            JSONObject jsonObject1 = gson.fromJson(gson.toJson(jsonObject), new TypeToken<JSONObject>() {
            }.getType());
            JSONObject searchQuery = new JSONObject();
            String cabinClass = jsonObject.getAsJsonObject("searchQuery").get("cabinClass").getAsString();
            JSONObject paxInfos = new JSONObject();
            String Adult = jsonObject.getAsJsonObject("searchQuery").getAsJsonObject("paxInfo").get("ADULT").getAsString();
            String Children = jsonObject.getAsJsonObject("searchQuery").getAsJsonObject("paxInfo").get("CHILD").getAsString();
            String Infant = jsonObject.getAsJsonObject("searchQuery").getAsJsonObject("paxInfo").get("INFANT").getAsString();
            JSONArray routeInfos = new JSONArray();
            JSONObject fromCityOrAirport = new JSONObject();
            JSONObject toCityOrAirport = new JSONObject();
            String dep = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().getAsJsonObject("fromCityOrAirport").get("code").getAsString();
            String arr = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().getAsJsonObject("toCityOrAirport").get("code").getAsString();
            String depDate = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().get("travelDate").getAsString();
            System.out.println("depDate = " + depDate);
            System.out.println("jsonObject1 = " + jsonObject1);
            if (!isReturn) {
                try {
                    mShimmerViewContainer.setVisibility(View.VISIBLE);
                    mShimmerViewContainer.startShimmerAnimation();
                    paxInfos.put("ADULT", Adult);
                    paxInfos.put("CHILD", Children);
                    paxInfos.put("INFANT", Infant);
                    fromCityOrAirport.put("code", dep);
                    toCityOrAirport.put("code", arr);
                    routeInfos.put(0, new JSONObject()
                            .put("fromCityOrAirport", fromCityOrAirport)
                            .put("toCityOrAirport", toCityOrAirport)
                            .put("travelDate", Date)
                    );
                    searchQuery.put("cabinClass", cabinClass);
                    searchQuery.put("paxInfo", paxInfos);
                    searchQuery.put("routeInfos", routeInfos);
                    JSONArray jsonArray = adapter.getFlightDetailsList1();
                    searchQuery.put("preferredAirline", jsonArray);
                    JSONObject searchModifiers = new JSONObject();
                    searchModifiers.put("isDirectFlight", isDirectFlight);
                    searchModifiers.put("isConnectingFlight", isConnectingFlight);
                    searchQuery.put("searchModifiers", searchModifiers);
                    jsonObject1.put("searchQuery", searchQuery);
                    System.out.println("jsonObject1 = " + jsonObject1);
                    jsonObject = gson.fromJson(String.valueOf(jsonObject1), new TypeToken<JsonObject>() {
                    }.getType());
                    System.out.println("gson = " + gson.toJson(jsonObject));
                    ApiInterface apiInterface = Retrofitt.initretro().create(ApiInterface.class);
                    Call<JsonObject> call = apiInterface.getFlights(jsonObject);
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.isSuccessful()) {
                                JsonObject object = response.body();
                                if (object != null) {
                                    List<FlightDetails> flightDetailsList = new ArrayList<>();
                                    JsonArray jsonArray = object.getAsJsonObject("searchResult").getAsJsonObject("tripInfos").getAsJsonArray("ONWARD");
                                    for (JsonElement js : jsonArray) {
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
                                            int price = jsonObject2.getAsJsonObject("fd").getAsJsonObject("ADULT").getAsJsonObject("fC").get("TF").getAsInt();
                                            ints.add(price);
                                        }
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
                                        int lowPrice = ints.get(0);
                                        flightDetails.setTotalPrice(String.valueOf(lowPrice));
                                        flightDetailsList.add(flightDetails);
                                        System.out.println("gson.toJson(flightDetails) = " + gson.toJson(flightDetails));
                                    }
                                    flightDetailsList.sort(new SortByPrice());

                                    flightRecyclerView.setLayoutManager(new LinearLayoutManager(flightRecyclerView.getContext()));
                                    System.out.println("gson.toJson(flightDetailsList) = " + gson.toJson(flightDetailsList));
                                    FlightsAdapter flightsAdapter = new FlightsAdapter(type, flightDetailsList, flightRecyclerView.getContext(), paxInfo, fragmentManager);
                                    flightRecyclerView.setAdapter(flightsAdapter);
                                    mShimmerViewContainer.stopShimmerAnimation();
                                    mShimmerViewContainer.setVisibility(View.GONE);

                                }
                            } else {
                                System.out.println("response = " + response);
                                System.out.println("gson.toJson(jsonObject) filter = " + gson.toJson(jsonObject));
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            System.out.println("t = " + t);
                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            } else {
                if (type.equalsIgnoreCase("Domestic")) {
                    domesticLinearLayout.setVisibility(View.VISIBLE);
                    domesticShimmerLinearLayout.setVisibility(View.VISIBLE);
                    relativeLayout.setVisibility(View.VISIBLE);
                    returnWayInternationalRecyclerView.setVisibility(View.GONE);
                    mShimmerViewContainerReturnInternational.setVisibility(View.GONE);
                    mShimmerViewContainer.setVisibility(View.VISIBLE);
                    mShimmerViewContainer.startShimmerAnimation();
                    mShimmerViewContainerReturn.setVisibility(View.VISIBLE);
                    mShimmerViewContainerReturn.startShimmerAnimation();
                } else {
                    domesticLinearLayout.setVisibility(View.GONE);
                    domesticShimmerLinearLayout.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.GONE);
                    returnWayInternationalRecyclerView.setVisibility(View.VISIBLE);
                    mShimmerViewContainerReturnInternational.setVisibility(View.VISIBLE);
                    mShimmerViewContainerReturnInternational.startShimmerAnimation();
                }
                try {

                    String depDate1 = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(1).getAsJsonObject().get("travelDate").getAsString();
                    paxInfos.put("ADULT", Adult);
                    paxInfos.put("CHILD", Children);
                    paxInfos.put("INFANT", Infant);
                    fromCityOrAirport.put("code", dep);
                    toCityOrAirport.put("code", arr);
                    routeInfos.put(0, new JSONObject().put("fromCityOrAirport", fromCityOrAirport).put("toCityOrAirport", toCityOrAirport).put("travelDate", depDate));
                    routeInfos.put(1, new JSONObject().put("fromCityOrAirport", toCityOrAirport).put("toCityOrAirport", fromCityOrAirport).put("travelDate", depDate1));
                    searchQuery.put("cabinClass", cabinClass);
                    searchQuery.put("paxInfo", paxInfos);
                    searchQuery.put("routeInfos", routeInfos);
                    searchQuery.put("preferredAirline", preferredAirline);
                    JSONObject searchModifiers = new JSONObject();
                    searchModifiers.put("isDirectFlight", isDirectFlight);
                    searchModifiers.put("isConnectingFlight", isConnectingFlight);
                    searchQuery.put("searchModifiers", searchModifiers);
                    jsonObject1.put("searchQuery", searchQuery);
                    System.out.println("jsonObject1 = " + jsonObject1);
                    jsonObject = gson.fromJson(String.valueOf(jsonObject1), new TypeToken<JsonObject>() {
                    }.getType());
                    System.out.println("gson = " + gson.toJson(jsonObject));
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
                                       /* ReturnFlightsAdapter returnFlightsAdapter = new ReturnFlightsAdapter(returnFlightDetailsList, returnWayRecyclerView.getContext(), onItemClickListener, type, getParentFragmentManager(), paxInfo);
                                        returnWayRecyclerView.setAdapter(returnFlightsAdapter);
                                        returnFlightsAdapter.notifyDataSetChanged();
                                        mShimmerViewContainerReturn.stopShimmerAnimation();
                                        mShimmerViewContainerReturn.setVisibility(View.GONE);*/
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
                             /*           ReturnFlightsAdapter flightsAdapter = new ReturnFlightsAdapter(flightDetailsList, flightRecyclerView.getContext(), onItemClickListener, type, getParentFragmentManager(), paxInfo);
                                        flightRecyclerView.setAdapter(flightsAdapter);
                                        flightsAdapter.notifyDataSetChanged();*/
                                        mShimmerViewContainer.stopShimmerAnimation();
                                        mShimmerViewContainer.setVisibility(View.GONE);
                                    } else {
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
                                        InternationalReturnFlightsAdapter internationalReturnFlightsAdapter = new InternationalReturnFlightsAdapter(flightDetailsList, returnWayInternationalRecyclerView.getContext(), paxInfo, jsonObject, getParentFragmentManager());
                                        returnWayInternationalRecyclerView.setLayoutManager(new LinearLayoutManager(returnWayInternationalRecyclerView.getContext()));
                                        returnWayInternationalRecyclerView.setAdapter(internationalReturnFlightsAdapter);
                                        mShimmerViewContainerReturnInternational.stopShimmerAnimation();
                                        mShimmerViewContainerReturnInternational.setVisibility(View.GONE);
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
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return view;


    }

    private List<String> getUniqueNamesFromModalClassList1(ArrayList<FlightDetails> list) {
        List<String> uniqueNames = new ArrayList<>();
        for (FlightDetails modalClass : list) {
            if (!uniqueNames.contains(modalClass.getAirlineName())) {
                uniqueNames.add(modalClass.getAirlineName());
            }
        }
        return uniqueNames;
    }

    private void setupViews(int min, int max) {
        rangeSeekBar.setStartProgress(min); // default is 0
        rangeSeekBar.setEndProgress(max); // default is 50
        rangeSeekBar.setMinDifference(1500); // default is 20
        rangeSeekBar.setMax(max);
        rate1.setText("₹ " + rangeSeekBar.getStartProgress());
        rate2.setText("₹ " + rangeSeekBar.getEndProgress());
        rangeSeekBar.setOnRangeSeekBarListener(this);
    }

    @Override
    public void onRangeValues(RangeSeekBar rangeSeekBar, int start, int end) {
        rate1.setText("₹ " + start); // example using start value
        rate2.setText("₹ " + end);
        for (FlightDetails flightDetails : flightDetailsList) {
            int p = Integer.parseInt(flightDetails.getTotalPrice());
            if (start < p && end > p) {
                flightDetailsList1.add(flightDetails);
            } else {
                flightDetailsList1.remove(flightDetails);
            }
        }


    }

}
