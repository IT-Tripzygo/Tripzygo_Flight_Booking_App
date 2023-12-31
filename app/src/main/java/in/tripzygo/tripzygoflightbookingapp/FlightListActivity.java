package in.tripzygo.tripzygoflightbookingapp;

import static in.tripzygo.tripzygoflightbookingapp.DayViewContainer.selectedDate;
import static in.tripzygo.tripzygoflightbookingapp.FlightListActivity.filters;
import static in.tripzygo.tripzygoflightbookingapp.FlightListActivity.fragmentManager;
import static in.tripzygo.tripzygoflightbookingapp.FlightListActivity.getFlights;
import static in.tripzygo.tripzygoflightbookingapp.FlightListActivity.weekCalendarView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kizitonwose.calendar.core.Week;
import com.kizitonwose.calendar.core.WeekDay;
import com.kizitonwose.calendar.core.WeekDayPosition;
import com.kizitonwose.calendar.view.DaySize;
import com.kizitonwose.calendar.view.ViewContainer;
import com.kizitonwose.calendar.view.WeekCalendarView;
import com.kizitonwose.calendar.view.WeekDayBinder;
import com.kizitonwose.calendar.view.WeekHeaderFooterBinder;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.tripzygo.tripzygoflightbookingapp.Modals.Filters;
import in.tripzygo.tripzygoflightbookingapp.Modals.FlightDetails;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.ApiInterface;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.Retrofitt;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FlightListActivity extends AppCompatActivity {
    public static WeekCalendarView weekCalendarView;
    public static LocalDate localDate, endDate, startDate;
    public static RecyclerView recyclerView;
    public static ShimmerFrameLayout mShimmerViewContainer;
    public static TextView priceSort, depSort, arrSort;
    public static FloatingActionButton floatingActionButton;
    public static FragmentManager fragmentManager;
    public static Filters filters = new Filters();
    public static boolean lowABoolean = true, depLow = true, arrLow = true;
    static int adult, child, infant;
    MaterialToolbar materialToolbar;
    LinearLayout passenger;
    ImageView imageView;
    TextView DepartureAirportTextOneWay, ArrivalAirportTextOneWay, datesTextOneWay, passengerTextOneWay;
    String type;

    public static void getFlights(String type, JsonObject jsonObject, RecyclerView recyclerView, JsonObject paxInfo, ShimmerFrameLayout mShimmerViewContainer, FloatingActionButton floatingActionButton, FragmentManager fragmentManager, Filters filters, TextView priceSort, TextView depSort, TextView arrSort, boolean priceLow, boolean depLow, boolean arrLow) {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        Gson gson = new Gson();
        System.out.println("gson.toJson(jsonObject) = " + gson.toJson(jsonObject));
        String depDate12 = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().get("travelDate").getAsString();
        ApiInterface apiInterface = Retrofitt.initretro().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getFlights(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject object = response.body();
                    if (object != null) {
                        System.out.println(gson.toJson(object));
                        List<FlightDetails> flightDetailsList = new ArrayList<>();
                        if (object.getAsJsonObject("searchResult").has("tripInfos")) {
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
                                        flightDetails.setDepartureTime(dTime);
                                        flightDetails.setArrivalTime(aTime);
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }
                                } else {
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                                    SimpleDateFormat df1 = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                    int duration = 0;
                                    for (JsonElement jsonElement : sI) {
                                        JsonObject jsonObject2 = jsonElement.getAsJsonObject();
                                        if (jsonObject2.has("cT")) {
                                            duration += jsonObject2.get("duration").getAsInt() + jsonObject2.get("cT").getAsInt();
                                        } else {
                                            duration += jsonObject2.get("duration").getAsInt();
                                        }
                                    }
                                    flightDetails.setTotalTime(duration);
                                    String departureOfFirst = sI.get(0).getAsJsonObject().get("dt").getAsString();
                                    String arrivalOfLast = sI.get(sI.size() - 1).getAsJsonObject().get("at").getAsString();
                                    try {
                                        Date dof = df.parse(departureOfFirst);
                                        Date aol = df.parse(arrivalOfLast);
                                        String dTime = df1.format(dof);
                                        String aTime = df1.format(aol);
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
                            }
                            flightDetailsList.sort(new SortByPrice());
                            System.out.println("depDate12 fl = " + depDate12);
                            final boolean[] d = {!depLow};
                            depSort.setOnClickListener(view -> {
                                if (d[0]) {
                                    depSort.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.arrow_up_long_solid, 0);
                                    flightDetailsList.sort(new SortByDepartureHighToLow());
                                    d[0] = false;
                                } else {
                                    depSort.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.arrow_down_long_solid, 0);
                                    flightDetailsList.sort(new SortByDeparture());
                                    d[0] = true;
                                }
                                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                                FlightsAdapter flightsAdapter = new FlightsAdapter(type, flightDetailsList, recyclerView.getContext(), paxInfo, fragmentManager);
                                recyclerView.setAdapter(flightsAdapter);
                            });
                            final boolean[] a = {!arrLow};
                            arrSort.setOnClickListener(view -> {
                                if (a[0]) {
                                    arrSort.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.arrow_up_long_solid, 0);
                                    flightDetailsList.sort(new SortByArrivalHighToLow());
                                    a[0] = false;
                                } else {
                                    arrSort.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.arrow_down_long_solid, 0);
                                    flightDetailsList.sort(new SortByArrival());
                                    a[0] = true;
                                }
                                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                                FlightsAdapter flightsAdapter = new FlightsAdapter(type, flightDetailsList, recyclerView.getContext(), paxInfo, fragmentManager);
                                recyclerView.setAdapter(flightsAdapter);
                            });
                            final boolean[] b = {!priceLow};
                            priceSort.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (b[0]) {
                                        priceSort.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.arrow_up_long_solid, 0);
                                        flightDetailsList.sort(new SortByPriceHighToLow());

                                        b[0] = false;
                                    } else {
                                        priceSort.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.arrow_down_long_solid, 0);
                                        flightDetailsList.sort(new SortByPrice());
                                        b[0] = true;
                                    }
                                    recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                                    FlightsAdapter flightsAdapter = new FlightsAdapter(type, flightDetailsList, recyclerView.getContext(), paxInfo, fragmentManager);
                                    recyclerView.setAdapter(flightsAdapter);
                                }
                            });
                            floatingActionButton.setOnClickListener(view -> {
                                FilterActivity filterDialog = new FilterActivity(flightDetailsList);
                                filterDialog.setCancelable(false);
                                filterDialog.show(fragmentManager, "filters");
                            });
                            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                            FlightsAdapter flightsAdapter = new FlightsAdapter(type, flightDetailsList, recyclerView.getContext(), paxInfo, fragmentManager);
                            recyclerView.setAdapter(flightsAdapter);
                            mShimmerViewContainer.stopShimmerAnimation();
                            mShimmerViewContainer.setVisibility(View.GONE);

                        } else {
                            mShimmerViewContainer.stopShimmerAnimation();
                            mShimmerViewContainer.setVisibility(View.GONE);
                        }
                    }
                } else {
                    System.out.println("response = " + response);
                    System.out.println("response = " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println("t = " + t);
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_list);
        materialToolbar = findViewById(R.id.toolbarOneway);
        DepartureAirportTextOneWay = findViewById(R.id.DepartureAirportTextOneWay);
        ArrivalAirportTextOneWay = findViewById(R.id.ArrivalAirportTextOneWay);
        datesTextOneWay = findViewById(R.id.datesTextOneWay);
        passenger = findViewById(R.id.passengerOneWay);
        imageView = findViewById(R.id.editSearch);
        priceSort = findViewById(R.id.priceSort);
        depSort = findViewById(R.id.departureSort);
        arrSort = findViewById(R.id.arrivalSort);
        passengerTextOneWay = findViewById(R.id.passengerTextOneWay);
        materialToolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
        });
        floatingActionButton = findViewById(R.id.floating);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.setVisibility(View.GONE);
        String json = getIntent().getStringExtra("json");
        type = getIntent().getStringExtra("type");
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, new TypeToken<JsonObject>() {
        }.getType());
        String toAirportCity = getIntent().getStringExtra("toAirportCity");
        String fromAirportCity = getIntent().getStringExtra("fromAirportCity");
        String dep = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().getAsJsonObject("fromCityOrAirport").get("code").getAsString();
        String arr = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().getAsJsonObject("toCityOrAirport").get("code").getAsString();
        String depDate = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().get("travelDate").getAsString();
        String Adult = jsonObject.getAsJsonObject("searchQuery").getAsJsonObject("paxInfo").get("ADULT").getAsString();
        String Children = jsonObject.getAsJsonObject("searchQuery").getAsJsonObject("paxInfo").get("CHILD").getAsString();
        String Infant = jsonObject.getAsJsonObject("searchQuery").getAsJsonObject("paxInfo").get("INFANT").getAsString();
        JsonObject paxInfo = jsonObject.getAsJsonObject("searchQuery").getAsJsonObject("paxInfo");
        imageView.setOnClickListener(view -> {
            super.onBackPressed();
        });
        adult = Integer.parseInt(Adult);
        child = Integer.parseInt(Children);
        infant = Integer.parseInt(Infant);
        int total = adult + child + infant;
        DepartureAirportTextOneWay.setText(fromAirportCity);
        ArrivalAirportTextOneWay.setText(toAirportCity);
        passengerTextOneWay.setText(total + " Traveller");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yy", Locale.getDefault());
        try {
            Date date = sdf1.parse(depDate);
            String d = sdf.format(date);
            datesTextOneWay.setText(d);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        weekCalendarView = findViewById(R.id.weekCalendarView);
        recyclerView = findViewById(R.id.flightsRecycler);
        weekCalendarView.setDaySize(DaySize.Square);
        Calendar calendar = Calendar.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            YearMonth yearMonth = YearMonth.now();
            localDate = LocalDate.now();
            String string = jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().get("travelDate").getAsString();
            LocalDate localDate1 = LocalDate.parse(string);
            System.out.println("localDate1 = " + localDate1);
            selectedDate = localDate1;
            if (selectedDate.isEqual(localDate)) {
                startDate = localDate;
            } else {
                startDate = localDate1.minusDays(15);
            }

            endDate = localDate1.plusDays(15);
            System.out.println("endDate = " + endDate);
            System.out.println("localDate = " + localDate.getDayOfMonth() + 15);
            weekCalendarView.setWeekHeaderBinder(new WeekHeaderFooterBinder<WeekViewContainer>() {
                @NonNull
                @Override
                public WeekViewContainer create(@NonNull View view) {
                    return new WeekViewContainer(view);
                }

                @Override
                public void bind(@NonNull WeekViewContainer container, Week week) {
                    System.out.println("week = " + Arrays.asList(week.getDays()));
                    LinearLayout linearLayout = container.titlesContainer;
                    for (int i = 0; i < linearLayout.getChildCount(); i++) {
                        View view = linearLayout.getChildAt(i);
                        if (view instanceof TextView) {
                            TextView textView = (TextView) view;
                            String title = week.getDays().get(i).toString();
                            System.out.println("title = " + title);
                            textView.setText(title);
                        }
                    }
                }
            });
            weekCalendarView.setWeekFooterBinder(new WeekHeaderFooterBinder<ViewContainer>() {
                @NonNull
                @Override
                public ViewContainer create(@NonNull View view) {
                    return new WeekViewContainer(view);
                }

                @Override
                public void bind(@NonNull ViewContainer container, Week week) {

                }
            });
            weekCalendarView.setDayBinder(new WeekDayBinder<DayViewContainer>() {
                @NonNull
                @Override
                public DayViewContainer create(@NonNull View view) {
                    return new DayViewContainer(view, jsonObject, type);
                }

                @Override
                public void bind(@NonNull DayViewContainer container, WeekDay weekDay) {
                    container.day = weekDay;
                    WeekDay day = weekDay;
                    TextView textView = container.calendarDayTextView;
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM");
                    DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String tDate = day.getDate().format(dateTimeFormatter1);
                    jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().addProperty("travelDate", tDate);
                    getLowestFlightRate(jsonObject, container.calendarDayPriceTextView, container.shimmerFrameLayout);
                    System.out.println("gson = " + gson.toJson(jsonObject));
                    textView.setText(day.getDate().format(dateTimeFormatter));
                    container.calendarDayWeekTextView.setText(weekDay.getDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
                    if (day.getPosition() == WeekDayPosition.RangeDate) {
                        // Show the month dates. Remember that views are reused!

                        textView.setVisibility(View.VISIBLE);
                        container.calendarDayPriceTextView.setVisibility(View.VISIBLE);
                        container.calendarDayWeekTextView.setVisibility(View.VISIBLE);

                        if (day.getDate().equals(selectedDate)) {
                            // If this is the selected date, show a round background and change the text color.
                            textView.setTextColor(Color.WHITE);
                            container.calendarDayPriceTextView.setTextColor(Color.WHITE);
                            container.calendarDayWeekTextView.setTextColor(Color.WHITE);
                            textView.setBackgroundResource(R.color.blackLight);
                            container.calendarDayPriceTextView.setBackgroundResource(R.color.blackLight);
                            container.calendarDayWeekTextView.setBackgroundResource(R.color.blackLight);
                        } else {
                            // If this is NOT the selected date, remove the background and reset the text color.
                            textView.setTextColor(Color.BLACK);
                            container.calendarDayPriceTextView.setTextColor(Color.BLACK);
                            container.calendarDayWeekTextView.setTextColor(Color.BLACK);
                            container.calendarDayWeekTextView.setBackground(null);
                            textView.setBackground(null);
                            container.calendarDayPriceTextView.setBackground(null);
                            if (day.getDate().isBefore(localDate)) {
                                textView.setTextColor(Color.LTGRAY);
                                container.calendarDayPriceTextView.setTextColor(Color.LTGRAY);
                                container.calendarDayWeekTextView.setTextColor(Color.LTGRAY);
                                container.calendarDayWeekTextView.setBackground(null);
                                textView.setBackground(null);
                                container.calendarDayPriceTextView.setBackground(null);
                            }
                        }
                    } else {
                        // Hide in and out dates
                        textView.setVisibility(View.INVISIBLE);
                        container.calendarDayPriceTextView.setVisibility(View.INVISIBLE);
                        container.calendarDayWeekTextView.setVisibility(View.INVISIBLE);
                    }

                }
            });

            DayOfWeek dayOfWeek = startDate.getDayOfWeek();
            weekCalendarView.setup(startDate, endDate, dayOfWeek);
            weekCalendarView.setScrollPaged(false);
//            weekCalendarView.scrollToDate(selectedDate);
            weekCalendarView.scrollToWeek(selectedDate);
            weekCalendarView.setWeekScrollListener(new Function1<Week, Unit>() {
                @Override
                public Unit invoke(Week week) {
                    return Unit.INSTANCE;
                }
            });


        }
        fragmentManager = getSupportFragmentManager();
        getFlights(type, jsonObject, recyclerView, paxInfo, mShimmerViewContainer, floatingActionButton, fragmentManager, filters, priceSort, depSort, arrSort, lowABoolean, depLow, arrLow);
    }

    public void getLowestFlightRate(JsonObject jsonObject, TextView textView, ShimmerFrameLayout shimmerFrameLayout) {
        final String[] Fare = {null};
        Gson gson = new Gson();
        ApiInterface apiInterface = Retrofitt.initretro().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getFlights(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject object = response.body();
                    if (object != null) {
                        System.out.println(gson.toJson(object));
                        List<FlightDetails> flightDetailsList = new ArrayList<>();
                        if (object.getAsJsonObject("searchResult").has("tripInfos")) {
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
                            }
                            flightDetailsList.sort(new SortByPrice());
                            Fare[0] = flightDetailsList.get(0).getTotalPrice();
                            Locale locale = new Locale.Builder().setLanguage("en").setRegion("IN").build();
                            NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
                            String basecurrency = formatter.format(Integer.parseInt(Fare[0]));
                            int basecentsIndex = basecurrency.lastIndexOf(".00");
                            if (basecentsIndex != -1) {
                                basecurrency = basecurrency.substring(0, basecentsIndex);
                            }
                            System.out.printf("Fare[0] = %s%n", Fare[0]);
                            textView.setText(basecurrency);
                            shimmerFrameLayout.stopShimmerAnimation();
                            shimmerFrameLayout.setVisibility(View.GONE);

//                        recyclerView.setLayoutManager(new LinearLayoutManager(FlightListActivity.this));
//                        System.out.println("gson.toJson(flightDetailsList) = " + gson.toJson(flightDetailsList));
//                        FlightsAdapter flightsAdapter = new FlightsAdapter(flightDetailsList, FlightListActivity.this);
//                        recyclerView.setAdapter(flightsAdapter);
                        } else {
                            textView.setText("-");
                            shimmerFrameLayout.stopShimmerAnimation();
                            shimmerFrameLayout.setVisibility(View.GONE);
                        }
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

}

class DayViewContainer extends ViewContainer {
    public static LocalDate currentSelection;
    public static LocalDate selectedDate = null;
    TextView calendarDayTextView, calendarDayPriceTextView, calendarDayWeekTextView;
    ShimmerFrameLayout shimmerFrameLayout;
    WeekDay day;

    public DayViewContainer(@NonNull View view, JsonObject jsonObject, String type) {
        super(view);
        this.calendarDayTextView = view.findViewById(R.id.calendarDayText);
        this.calendarDayPriceTextView = view.findViewById(R.id.calendarDayPriceText);
        this.calendarDayWeekTextView = view.findViewById(R.id.calendarDayWeekText);
        this.shimmerFrameLayout = view.findViewById(R.id.shimmer_view_containerCalendar);
        this.shimmerFrameLayout.startShimmerAnimation();
        view.setOnClickListener(view1 -> {
            if (day.getPosition() == WeekDayPosition.RangeDate) {
                // Keep a reference to any previous selection
                // in case we overwrite it and need to reload it.

                currentSelection = selectedDate;

                if (currentSelection != null && currentSelection.equals(day.getDate())) {
                    // If the user clicks the same date, clear selection.
                    selectedDate = null;
                    // Reload this date so the dayBinder is called
                    // and we can REMOVE the selection background.
                    weekCalendarView.notifyDateChanged(currentSelection);
                } else {

                    selectedDate = day.getDate();
                    DateTimeFormatter dateTimeFormatter1 = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        dateTimeFormatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        String tDate = day.getDate().format(dateTimeFormatter1);
                        jsonObject.getAsJsonObject("searchQuery").getAsJsonArray("routeInfos").get(0).getAsJsonObject().addProperty("travelDate", tDate);
                        JsonObject paxInfo = jsonObject.getAsJsonObject("searchQuery").getAsJsonObject("paxInfo");
                        getFlights(type, jsonObject, FlightListActivity.recyclerView, paxInfo, FlightListActivity.mShimmerViewContainer, FlightListActivity.floatingActionButton, fragmentManager, filters, FlightListActivity.priceSort, FlightListActivity.depSort, FlightListActivity.arrSort, FlightListActivity.lowABoolean, FlightListActivity.depLow, FlightListActivity.arrLow);
                    }

                    System.out.println("selectedDate = " + selectedDate);
                    // Reload the newly selected date so the dayBinder is
                    // called and we can ADD the selection background.
                    weekCalendarView.notifyDateChanged(day.getDate());

                    if (currentSelection != null) {
                        // We need to also reload the previously selected
                        // date so we can REMOVE the selection background.
                        weekCalendarView.notifyDateChanged(currentSelection);
                    }
                }
            }
        });
    }

    public TextView getCalendarDayTextView() {
        return calendarDayTextView;
    }

    public TextView getCalendarDayPriceTextView() {
        return calendarDayPriceTextView;
    }

}

class WeekViewContainer extends ViewContainer {

    LinearLayout titlesContainer;

    public WeekViewContainer(@NonNull View view) {
        super(view);
        this.titlesContainer = (LinearLayout) view;
    }
}
