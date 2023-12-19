package in.tripzygo.tripzygoflightbookingapp.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kizitonwose.calendar.view.CalendarView;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.tripzygo.tripzygoflightbookingapp.ArrivalCityAdapter;
import in.tripzygo.tripzygoflightbookingapp.FlightListActivity;
import in.tripzygo.tripzygoflightbookingapp.Modals.AirportCode;
import in.tripzygo.tripzygoflightbookingapp.R;
import in.tripzygo.tripzygoflightbookingapp.ReturnFlightsActivity;
import in.tripzygo.tripzygoflightbookingapp.SearchAdapter;
import in.tripzygo.tripzygoflightbookingapp.SliderAdapter;
import in.tripzygo.tripzygoflightbookingapp.Tools.SharedPreference;

public class HomeFragment extends Fragment {
    TextView fromTextView, fromCityTextView, toTextView, toCityTextView, whenTextView, whenReturnTextView, textAddReturn;
    ConstraintLayout TC, DepartureDate, returnTripDateContainer;
    TextView personTextView, classTextView;
    Button search;
    RadioGroup radioGroup;
    RadioButton oneWayRadioButton, returnRadioButton;
    List<AirportCode> airportCodes = new ArrayList<>();
    DatePickerDialog.OnDateSetListener dateSetListener;
    public static CalendarView calendarView;
    public static LocalDate localDate;
    String Adult = "1", Children = "0", Infant = "0", Class = "ECONOMY", fromAirport = "DEL", toAirport = "BOM", travelDate = "", returnTravelDate, type = "Domestic", fromAirportCity = "Delhi", toAirportCity = "Mumbai";
    boolean oneWay = true;
    LinearLayout passenger;
    ImageView swapImageView;
    boolean isDialogOpen = false;
    SliderView sliderView;
    List<String> sliderItems = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        sliderView = view.findViewById(R.id.imageSlider);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.SLIDE); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.parseColor("#000000"));
        sliderView.setIndicatorUnselectedColor(Color.WHITE);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();
        fromTextView = view.findViewById(R.id.fromText);
        fromCityTextView = view.findViewById(R.id.firstCity);
        toTextView = view.findViewById(R.id.toText);
        toCityTextView = view.findViewById(R.id.secondCity);
        swapImageView = view.findViewById(R.id.exchange);
        whenTextView = view.findViewById(R.id.DepartureDate);
        whenReturnTextView = view.findViewById(R.id.returnDate);
        textAddReturn = view.findViewById(R.id.textAddReturn);
        TC = view.findViewById(R.id.travellersAndClassContainer);
        DepartureDate = view.findViewById(R.id.departureDateSelection);
        returnTripDateContainer = view.findViewById(R.id.returnDateContainer);
        personTextView = view.findViewById(R.id.travellerCount);
        classTextView = view.findViewById(R.id.classLevel);
        search = view.findViewById(R.id.search);
        passenger = view.findViewById(R.id.passenger);
        radioGroup = view.findViewById(R.id.radio_type);
        oneWayRadioButton = view.findViewById(R.id.oneWay);
        returnRadioButton = view.findViewById(R.id.returnway);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yy");
        travelDate = sdf1.format(calendar.getTime());
        whenTextView.setText(sdf.format(calendar.getTime()));
        sliderItems.add("android.resource://in.tripzygo.tripzygoflightbookingapp/drawable/pleasant");
        sliderItems.add("android.resource://in.tripzygo.tripzygoflightbookingapp/drawable/go_travel");
        sliderItems.add("android.resource://in.tripzygo.tripzygoflightbookingapp/drawable/world");
        SliderAdapter adapter = new SliderAdapter(getContext(), sliderItems);
        sliderView.setSliderAdapter(adapter);
        adapter.notifyDataSetChanged();
        textAddReturn.setOnClickListener(view1 -> {
            oneWay = false;
            returnRadioButton.setChecked(true);
            returnRadioButton.setTextAppearance(R.style.RadioBold);
            oneWayRadioButton.setTextAppearance(R.style.RadioNormal);
            textAddReturn.setVisibility(View.GONE);
            returnTripDateContainer.setVisibility(View.VISIBLE);
            try {
                Date date = sdf1.parse(travelDate);
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(date);
                calendar1.add(Calendar.DAY_OF_MONTH, 1);
                returnTravelDate = sdf1.format(calendar1.getTime());
                whenReturnTextView.setText(sdf.format(calendar1.getTime()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            switch (radioGroup1.getCheckedRadioButtonId()) {
                case R.id.oneWay:
                    oneWay = true;
                    oneWayRadioButton.setTextAppearance(R.style.RadioBold);
                    returnRadioButton.setTextAppearance(R.style.RadioNormal);
//                    oneWayRadioButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.plane_solid, 0, 0, 0);
//                    returnRadioButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.plane_solid_white, 0, 0, 0);
//                    returnRadioButton.setTextColor(Color.WHITE);
                    returnTripDateContainer.setVisibility(View.GONE);
                    textAddReturn.setVisibility(View.VISIBLE);
                    break;
                case R.id.returnway:
                    oneWay = false;
                    returnRadioButton.setTextAppearance(R.style.RadioBold);
                    oneWayRadioButton.setTextAppearance(R.style.RadioNormal);
//                    oneWayRadioButton.setTextColor(Color.WHITE);
//                    oneWayRadioButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.plane_solid_white, 0, 0, 0);
//                    returnRadioButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.plane_solid, 0, 0, 0);
//                    returnRadioButton.setTextColor(Color.BLACK);
                    returnTripDateContainer.setVisibility(View.VISIBLE);
                    textAddReturn.setVisibility(View.GONE);
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.add(Calendar.DAY_OF_MONTH, 1);
                    returnTravelDate = sdf1.format(calendar1.getTime());
                    whenReturnTextView.setText(sdf.format(calendar1.getTime()));
                    break;
            }
        });
        fromTextView.setOnClickListener(view1 -> {
            if (!isDialogOpen) {
                isDialogOpen = true;
                Gson gson = new Gson();
                String json = null;
                try {
                    InputStream is = getContext().getAssets().open("airportcodes.json");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    json = new String(buffer, StandardCharsets.UTF_8);
                    airportCodes = gson.fromJson(json, new TypeToken<List<AirportCode>>() {
                    }.getType());

                    SharedPreference.storeList(getContext(), "NameList", "Names", airportCodes);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                List<AirportCode> popularAirportCodeList = new ArrayList<>();
                List<AirportCode> airportCodeList = SharedPreference.loadList(getContext(), "NameList", "Names");
                View view2 = getLayoutInflater().inflate(R.layout.search, null);
                ImageView imgToolBack = view2.findViewById(R.id.img_tool_back);
                final EditText edtToolSearch = view2.findViewById(R.id.edt_tool_search);
                ImageView imgToolMic = view2.findViewById(R.id.img_tool_mic);
                final ListView listSearch = view2.findViewById(R.id.list_search);
                final ListView popularList_search = view2.findViewById(R.id.popularList_search);
                final TextView txtEmpty = view2.findViewById(R.id.txt_empty);
                final TextView popularCityText = view2.findViewById(R.id.popularCities);
                final TextView allCityText = view2.findViewById(R.id.textView6);
                for (AirportCode airportCode : airportCodeList) {
                    if (airportCode.isPopular()) {
                        popularAirportCodeList.add(airportCode);
                    }
                }

                final Dialog toolbarSearchDialog = new Dialog(getContext(), R.style.MaterialSearch);
                toolbarSearchDialog.setContentView(view2);
                toolbarSearchDialog.setCancelable(false);
                toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
                toolbarSearchDialog.show();

                toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                airportCodeList = (airportCodeList != null && airportCodeList.size() > 0) ? airportCodeList : new ArrayList<>();
                popularList_search.setVisibility(View.VISIBLE);
                listSearch.setVisibility(View.GONE);
                final SearchAdapter searchAdapter = new SearchAdapter(getContext(), airportCodeList, false);
                final SearchAdapter popularSearchAdapter1 = new SearchAdapter(getContext(), popularAirportCodeList, false);
                popularList_search.setAdapter(popularSearchAdapter1);
                listSearch.setAdapter(searchAdapter);
                listSearch.setOnItemClickListener((adapterView, view4, i, l) -> {
                    AirportCode fromAirportCode = (AirportCode) adapterView.getItemAtPosition(i);
                    if (fromAirportCode.getCountry().equalsIgnoreCase("India")) {
                        type = "Domestic";
                    } else {
                        type = "International";
                    }
                    fromAirportCity = fromAirportCode.getCity();
                    fromCityTextView.setText(fromAirportCity);
                    fromTextView.setText(fromAirportCode.getCitycode());
                    fromAirport = fromAirportCode.getCitycode();
                    listSearch.setVisibility(View.GONE);
                    toolbarSearchDialog.dismiss();
                    isDialogOpen = false;
                });
                popularList_search.setOnItemClickListener((adapterView, view4, i, l) -> {
                    AirportCode fromAirportCode = (AirportCode) adapterView.getItemAtPosition(i);
                    if (fromAirportCode.getCountry().equalsIgnoreCase("India")) {
                        type = "Domestic";
                    } else {
                        type = "International";
                    }
                    fromAirportCity = fromAirportCode.getCity();
                    fromCityTextView.setText(fromAirportCity);
                    fromTextView.setText(fromAirportCode.getCitycode());
                    fromAirport = fromAirportCode.getCitycode();
                    popularList_search.setVisibility(View.GONE);
                    toolbarSearchDialog.dismiss();
                    isDialogOpen = false;
                });
                imgToolBack.setOnClickListener(view3 -> {
                    toolbarSearchDialog.dismiss();
                    isDialogOpen = false;
                });
                imgToolMic.setOnClickListener(view3 -> {
                    edtToolSearch.setText("");
                });
                edtToolSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        Gson gson = new Gson();
                        airportCodes = SharedPreference.loadList(getContext(), "NameList", "Names");
                        searchAdapter.updateList(airportCodes, true);
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        Gson gson = new Gson();
                        List<AirportCode> filterList = new ArrayList<>();
                        String json = null;
                        boolean isNodata = false;
                        if (charSequence.length() > 0) {
                            for (AirportCode airportCode : airportCodes) {
                                if (airportCode.getCity().toLowerCase().startsWith(String.valueOf(charSequence)) || airportCode.getCode().toLowerCase().startsWith(String.valueOf(charSequence))) {
                                    filterList.add(airportCode);
                                    allCityText.setVisibility(View.VISIBLE);
                                    listSearch.setVisibility(View.VISIBLE);
                                    popularList_search.setVisibility(View.GONE);
                                    popularCityText.setVisibility(View.GONE);
                                    searchAdapter.updateList(filterList, true);
                                    isNodata = true;
                                }
                            }
                            if (!isNodata) {
                                listSearch.setVisibility(View.GONE);
                                popularList_search.setVisibility(View.GONE);
                                popularCityText.setVisibility(View.GONE);
                                txtEmpty.setVisibility(View.VISIBLE);
                                txtEmpty.setText("No data found");
                            }
                        } else {
                            listSearch.setVisibility(View.GONE);
                            popularList_search.setVisibility(View.GONE);
                            popularCityText.setVisibility(View.GONE);
                            txtEmpty.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

            }
        });
        toTextView.setOnClickListener(view2 -> {
            if (!isDialogOpen) {
                isDialogOpen = true;
                Gson gson = new Gson();
                String json = null;
                try {
                    InputStream is = getContext().getAssets().open("airportcodes.json");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    json = new String(buffer, StandardCharsets.UTF_8);
                    airportCodes = gson.fromJson(json, new TypeToken<List<AirportCode>>() {
                    }.getType());
                    List<AirportCode> popularAirportCodeList = new ArrayList<>();
                    View view3 = getLayoutInflater().inflate(R.layout.search, null);
                    SharedPreference.storeList(getContext(), "NameList", "Names", airportCodes);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }


                List<AirportCode> popularAirportCodeList = new ArrayList<>();
                List<AirportCode> airportCodeList = SharedPreference.loadList(getContext(), "NameList", "Names");
                View view3 = getLayoutInflater().inflate(R.layout.search, null);
                ImageView imgToolBack = view3.findViewById(R.id.img_tool_back);
                final EditText edtToolSearch = view3.findViewById(R.id.edt_tool_search);
                ImageView imgToolMic = view3.findViewById(R.id.img_tool_mic);
                final ListView listSearch = view3.findViewById(R.id.list_search);
                final ListView popularList_search = view3.findViewById(R.id.popularList_search);
                final TextView txtEmpty = view3.findViewById(R.id.txt_empty);
                final TextView popularCityText = view3.findViewById(R.id.popularCities);
                final TextView allCityText = view3.findViewById(R.id.textView6);
                for (AirportCode airportCode : airportCodeList) {
                    if (airportCode.isPopular()) {
                        popularAirportCodeList.add(airportCode);
                    }
                }

                final Dialog toolbarSearchDialog = new Dialog(getContext(), R.style.MaterialSearch);
                toolbarSearchDialog.setContentView(view3);
                toolbarSearchDialog.setCancelable(false);
                toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
                toolbarSearchDialog.show();

                toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                airportCodeList = (airportCodeList != null && airportCodeList.size() > 0) ? airportCodeList : new ArrayList<>();
                popularList_search.setVisibility(View.VISIBLE);
                listSearch.setVisibility(View.GONE);
                allCityText.setVisibility(View.GONE);
                final SearchAdapter searchAdapter = new SearchAdapter(getContext(), airportCodeList, false);
                final SearchAdapter popularSearchAdapter1 = new SearchAdapter(getContext(), popularAirportCodeList, false);
                popularList_search.setAdapter(popularSearchAdapter1);
                listSearch.setAdapter(searchAdapter);
                listSearch.setOnItemClickListener((adapterView, view4, i, l) -> {
                    AirportCode fromAirportCode = (AirportCode) adapterView.getItemAtPosition(i);
                    if (!type.matches("International")) {
                        if (fromAirportCode.getCountry().equalsIgnoreCase("India")) {
                            type = "Domestic";
                        } else {
                            type = "International";
                        }
                    }
                    toAirportCity = fromAirportCode.getCity();
                    toCityTextView.setText(toAirportCity);
                    toTextView.setText(fromAirportCode.getCitycode());
                    toAirport = fromAirportCode.getCitycode();
                    listSearch.setVisibility(View.GONE);
                    toolbarSearchDialog.dismiss();
                    isDialogOpen = false;
                });
                popularList_search.setOnItemClickListener((adapterView, view4, i, l) -> {
                    AirportCode fromAirportCode = (AirportCode) adapterView.getItemAtPosition(i);
                    if (fromAirportCode.getCountry().equalsIgnoreCase("India")) {
                        type = "Domestic";
                    } else {
                        type = "International";
                    }
                    toAirportCity = fromAirportCode.getCity();
                    toCityTextView.setText(toAirportCity);
                    toTextView.setText(fromAirportCode.getCitycode());
                    toAirport = fromAirportCode.getCitycode();
                    listSearch.setVisibility(View.GONE);
                    toolbarSearchDialog.dismiss();
                    isDialogOpen = false;
                });
                imgToolBack.setOnClickListener(view4 -> {
                    toolbarSearchDialog.dismiss();
                    isDialogOpen = false;
                });
                imgToolMic.setOnClickListener(view4 -> {
                    edtToolSearch.setText("");
                });
                edtToolSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        Gson gson = new Gson();
                        airportCodes = SharedPreference.loadList(getContext(), "NameList", "Names");
                        searchAdapter.updateList(airportCodes, true);
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        Gson gson = new Gson();
                        List<AirportCode> filterList = new ArrayList<>();
                        String json = null;
                        boolean isNodata = false;
                        if (charSequence.length() > 0) {
                            for (AirportCode airportCode : airportCodes) {
                                if (airportCode.getCity().toLowerCase().startsWith(String.valueOf(charSequence)) || airportCode.getCode().toLowerCase().startsWith(String.valueOf(charSequence))) {
                                    filterList.add(airportCode);
                                    allCityText.setVisibility(View.VISIBLE);
                                    listSearch.setVisibility(View.VISIBLE);
                                    popularList_search.setVisibility(View.GONE);
                                    popularCityText.setVisibility(View.GONE);
                                    searchAdapter.updateList(filterList, true);
                                    isNodata = true;
                                }
                            }
                            if (!isNodata) {
                                listSearch.setVisibility(View.GONE);
                                popularList_search.setVisibility(View.GONE);
                                popularCityText.setVisibility(View.GONE);
                                txtEmpty.setVisibility(View.VISIBLE);
                                txtEmpty.setText("No data found");
                            }
                        } else {
                            listSearch.setVisibility(View.GONE);
                            popularList_search.setVisibility(View.GONE);
                            popularCityText.setVisibility(View.GONE);
                            txtEmpty.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }
        });
        swapImageView.setOnClickListener(view1 -> {
            String fromCity = fromCityTextView.getText().toString();
            String toCity = toCityTextView.getText().toString();
            String from = fromTextView.getText().toString();
            String to = toTextView.getText().toString();
            fromTextView.setText(to);
            fromCityTextView.setText(toCity);
            toTextView.setText(from);
            toCityTextView.setText(fromCity);
            String temp = fromAirport;
            fromAirport = toAirport;
            toAirport = temp;
            String temp1 = fromAirportCity;
            fromAirportCity = toAirportCity;
            toAirportCity = temp1;
//            Toast.makeText(getContext(), fromAirport + "-" + toAirport, Toast.LENGTH_SHORT).show();
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM");
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                String Date = sdf.format(calendar.getTime());
                travelDate = sdf1.format(calendar.getTime());
                whenTextView.setText(Date);
            }
        };
        whenTextView.setOnClickListener(view2 -> {
            if (!isDialogOpen) {
                isDialogOpen = true;
                try {
                    setupRangePickerDialog(oneWay);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
//            View view1 = getLayoutInflater().inflate(R.layout.calendar, null);
//            calendarView = view1.findViewById(R.id.calendarView);
//            Calendar calendar = Calendar.getInstance();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                YearMonth yearMonth = YearMonth.now();
//                localDate = LocalDate.now();
//                YearMonth end = yearMonth.plusMonths(12);
//                List<DayOfWeek> daysOfWeek = Arrays.asList(DayOfWeek.values());
//                ViewGroup titlesContainer = view1.findViewById(R.id.titlesContainer);
//                for (int i = 0; i < titlesContainer.getChildCount(); i++) {
//                    View child = titlesContainer.getChildAt(i);
//                    if (child instanceof TextView) {
//                        TextView textView = (TextView) child;
//                        DayOfWeek dayOfWeek = daysOfWeek.get(i);
//                        String title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault());
//                        textView.setText(title);
//                    }
//                }
//
//                calendarView.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthViewContainer>() {
//                    @NonNull
//                    @Override
//                    public MonthViewContainer create(@NonNull View view) {
//                        return new MonthViewContainer(view);
//                    }
//
//                    @Override
//                    public void bind(@NonNull MonthViewContainer container, CalendarMonth calendarMonth) {
//                        container.monthContainer.setText(calendarMonth.getYearMonth().getMonth() + " " + calendarMonth.getYearMonth().getYear());
//                    }
//                });
//                calendarView.setDayBinder(new MonthDayBinder<DayViewContainer>() {
//                    @NonNull
//                    @Override
//                    public DayViewContainer create(@NonNull View view) {
//                        return new DayViewContainer(view);
//                    }
//
//                    @Override
//                    public void bind(@NonNull DayViewContainer container, CalendarDay calendarDay) {
//                        container.day = calendarDay;
//                        CalendarDay day = calendarDay;
//                        TextView textView = container.calendarDayTextView;
//                        textView.setText(String.valueOf(day.getDate().getDayOfMonth()));
//                        if (day.getPosition() == DayPosition.MonthDate) {
//                            // Show the month dates. Remember that views are reused!
//                            textView.setVisibility(View.VISIBLE);
//                            container.calendarDayPriceTextView.setVisibility(View.VISIBLE);
//
//
//                            if (day.getDate().equals(selectedDate)) {
//                                // If this is the selected date, show a round background and change the text color.
//                                textView.setTextColor(Color.WHITE);
//                                container.calendarDayPriceTextView.setTextColor(Color.WHITE);
//                                textView.setBackgroundResource(R.drawable.refs);
//                                container.calendarDayPriceTextView.setBackgroundResource(R.drawable.refs);
//                            } else {
//                                // If this is NOT the selected date, remove the background and reset the text color.
//                                textView.setTextColor(Color.BLACK);
//                                container.calendarDayPriceTextView.setTextColor(Color.BLACK);
//                                textView.setBackground(null);
//                                container.calendarDayPriceTextView.setBackground(null);
//                                if (day.getDate().isBefore(localDate)) {
//                                    textView.setTextColor(Color.LTGRAY);
//                                    container.calendarDayPriceTextView.setTextColor(Color.LTGRAY);
//                                    textView.setBackground(null);
//                                    container.calendarDayPriceTextView.setBackground(null);
//                                }
//                            }
//                            if (day.getDate().isEqual(localDate)) {
//                                selectedDate = localDate;
//                                textView.setTextColor(Color.WHITE);
//                                container.calendarDayPriceTextView.setTextColor(Color.WHITE);
//                                textView.setBackgroundResource(R.drawable.refs);
//                                container.calendarDayPriceTextView.setBackgroundResource(R.drawable.refs);
//                            }
//                        } else {
//                            // Hide in and out dates
//                            textView.setVisibility(View.INVISIBLE);
//                            container.calendarDayPriceTextView.setVisibility(View.INVISIBLE);
//                        }
//
//                        container.calendarDayPriceTextView.setText("--");
//                    }
//                });
//                DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
//                calendarView.setup(yearMonth, end, dayOfWeek);
//                calendarView.setOrientation(LinearLayout.VERTICAL);
//                calendarView.setScrollPaged(false);
//                calendarView.setMonthScrollListener(new Function1<CalendarMonth, Unit>() {
//                    @Override
//                    public Unit invoke(CalendarMonth calendarMonth) {
//                        return Unit.INSTANCE;
//                    }
//                });
//
//            }
//
//            final Dialog toolbarSearchDialog = new Dialog(MainActivity.this, R.style.MaterialSearch);
//            toolbarSearchDialog.setContentView(view1);
//            toolbarSearchDialog.setCancelable(true);
//            toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
//            toolbarSearchDialog.show();
////            toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        });
        whenReturnTextView.setOnClickListener(view1 -> {
            if (!isDialogOpen) {
                isDialogOpen = true;
                try {
                    setupRangePickerDialog(oneWay);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
/*
        passenger.setOnClickListener(view2 -> {
            View view1 = getLayoutInflater().inflate(R.layout.personlayout, null);
            RadioGroup adultGroup, childrenGroup, infantGroup, classGroup;
            Button done = view1.findViewById(R.id.done);
            done.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.selcted_level));
            RadioButton economyRadioButton, premiumEconomyRadioButton, businessRadioButton,
                    firstClassRadioButton, oneAdultRadioButton, twoAdultRadioButton, threeAdultRadioButton,
                    fourAdultRadioButton, fiveAdultRadioButton, sixAdultRadioButton, sevenAdultRadioButton,
                    eightAdultRadioButton, nineAdultRadioButton, oneChildrenRadioButton, twoChildrenRadioButton,
                    threeChildrenRadioButton, fourChildrenRadioButton, fiveChildrenRadioButton, sixChildrenRadioButton,
                    sevenChildrenRadioButton, zeroChildrenRadioButton, oneInfantRadioButton, twoInfantRadioButton, threeInfantRadioButton,
                    fourInfantRadioButton, fiveInfantRadioButton, sixInfantRadioButton, sevenInfantRadioButton, zeroInfantRadioButton;
            adultGroup = view1.findViewById(R.id.radioGroup_adults);
            oneAdultRadioButton = view1.findViewById(R.id.radioButton_adults_1);
            twoAdultRadioButton = view1.findViewById(R.id.radioButton_adults_2);
            threeAdultRadioButton = view1.findViewById(R.id.radioButton_adults_3);
            fourAdultRadioButton = view1.findViewById(R.id.radioButton_adults_4);
            fiveAdultRadioButton = view1.findViewById(R.id.radioButton_adults_5);
            sixAdultRadioButton = view1.findViewById(R.id.radioButton_adults_6);
            sevenAdultRadioButton = view1.findViewById(R.id.radioButton_adults_7);
            eightAdultRadioButton = view1.findViewById(R.id.radioButton_adults_8);
            nineAdultRadioButton = view1.findViewById(R.id.radioButton_adults_9);
            childrenGroup = view1.findViewById(R.id.radioGroup_child);
            oneChildrenRadioButton = view1.findViewById(R.id.radioButton_child_1);
            twoChildrenRadioButton = view1.findViewById(R.id.radioButton_child_2);
            threeChildrenRadioButton = view1.findViewById(R.id.radioButton_child_3);
            fourChildrenRadioButton = view1.findViewById(R.id.radioButton_child_4);
            fiveChildrenRadioButton = view1.findViewById(R.id.radioButton_child_5);
            sixChildrenRadioButton = view1.findViewById(R.id.radioButton_child_6);
            sevenChildrenRadioButton = view1.findViewById(R.id.radioButton_child_7);
            zeroChildrenRadioButton = view1.findViewById(R.id.radioButton_child_0);
            infantGroup = view1.findViewById(R.id.radioGroup_infants);
            zeroInfantRadioButton = view1.findViewById(R.id.radioButton_infants_0);
            oneInfantRadioButton = view1.findViewById(R.id.radioButton_infants_1);
            twoInfantRadioButton = view1.findViewById(R.id.radioButton_infants_2);
            threeInfantRadioButton = view1.findViewById(R.id.radioButton_infants_3);
            fourInfantRadioButton = view1.findViewById(R.id.radioButton_infants_4);
            fiveInfantRadioButton = view1.findViewById(R.id.radioButton_infants_5);
            sixInfantRadioButton = view1.findViewById(R.id.radioButton_infants_6);
            sevenInfantRadioButton = view1.findViewById(R.id.radioButton_infants_7);
            classGroup = view1.findViewById(R.id.radio_class);
            economyRadioButton = view1.findViewById(R.id.economy);
            premiumEconomyRadioButton = view1.findViewById(R.id.premium_economy);
            businessRadioButton = view1.findViewById(R.id.business);
            firstClassRadioButton = view1.findViewById(R.id.firstclass);
            adultGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.radioButton_adults_1:
                            oneAdultRadioButton.setTextColor(Color.WHITE);
                            twoAdultRadioButton.setTextColor(Color.BLACK);
                            threeAdultRadioButton.setTextColor(Color.BLACK);
                            fourAdultRadioButton.setTextColor(Color.BLACK);
                            fiveAdultRadioButton.setTextColor(Color.BLACK);
                            sixAdultRadioButton.setTextColor(Color.BLACK);
                            sevenAdultRadioButton.setTextColor(Color.BLACK);
                            eightAdultRadioButton.setTextColor(Color.BLACK);
                            nineAdultRadioButton.setTextColor(Color.BLACK);
                            Adult = oneAdultRadioButton.getText().toString();
                            break;
                        case R.id.radioButton_adults_2:
                            twoAdultRadioButton.setTextColor(Color.WHITE);
                            oneAdultRadioButton.setTextColor(Color.BLACK);
                            threeAdultRadioButton.setTextColor(Color.BLACK);
                            fourAdultRadioButton.setTextColor(Color.BLACK);
                            fiveAdultRadioButton.setTextColor(Color.BLACK);
                            sixAdultRadioButton.setTextColor(Color.BLACK);
                            sevenAdultRadioButton.setTextColor(Color.BLACK);
                            eightAdultRadioButton.setTextColor(Color.BLACK);
                            nineAdultRadioButton.setTextColor(Color.BLACK);
                            twoChildrenRadioButton.setEnabled(true);
                            twoChildrenRadioButton.setTextColor(Color.BLACK);
                            twoInfantRadioButton.setEnabled(true);
                            twoInfantRadioButton.setTextColor(Color.BLACK);
                            Adult = twoAdultRadioButton.getText().toString();
                            break;

                        case R.id.radioButton_adults_3:
                            threeAdultRadioButton.setTextColor(Color.WHITE);
                            oneAdultRadioButton.setTextColor(Color.BLACK);
                            twoAdultRadioButton.setTextColor(Color.BLACK);
                            fourAdultRadioButton.setTextColor(Color.BLACK);
                            fiveAdultRadioButton.setTextColor(Color.BLACK);
                            sixAdultRadioButton.setTextColor(Color.BLACK);
                            sevenAdultRadioButton.setTextColor(Color.BLACK);
                            eightAdultRadioButton.setTextColor(Color.BLACK);
                            nineAdultRadioButton.setTextColor(Color.BLACK);
                            threeChildrenRadioButton.setSaveEnabled(true);
                            threeInfantRadioButton.setSaveEnabled(true);
                            Adult = threeAdultRadioButton.getText().toString();
                            break;

                        case R.id.radioButton_adults_4:
                            fourAdultRadioButton.setTextColor(Color.WHITE);
                            oneAdultRadioButton.setTextColor(Color.BLACK);
                            twoAdultRadioButton.setTextColor(Color.BLACK);
                            threeAdultRadioButton.setTextColor(Color.BLACK);
                            fiveAdultRadioButton.setTextColor(Color.BLACK);
                            sixAdultRadioButton.setTextColor(Color.BLACK);
                            sevenAdultRadioButton.setTextColor(Color.BLACK);
                            eightAdultRadioButton.setTextColor(Color.BLACK);
                            nineAdultRadioButton.setTextColor(Color.BLACK);
                            fourChildrenRadioButton.setSaveEnabled(true);
                            fourInfantRadioButton.setSaveEnabled(true);
                            Adult = fourAdultRadioButton.getText().toString();
                            break;

                        case R.id.radioButton_adults_5:
                            fiveAdultRadioButton.setTextColor(Color.WHITE);
                            oneAdultRadioButton.setTextColor(Color.BLACK);
                            twoAdultRadioButton.setTextColor(Color.BLACK);
                            threeAdultRadioButton.setTextColor(Color.BLACK);
                            fourAdultRadioButton.setTextColor(Color.BLACK);
                            sixAdultRadioButton.setTextColor(Color.BLACK);
                            sevenAdultRadioButton.setTextColor(Color.BLACK);
                            eightAdultRadioButton.setTextColor(Color.BLACK);
                            nineAdultRadioButton.setTextColor(Color.BLACK);
                            Adult = fiveAdultRadioButton.getText().toString();
                            break;

                        case R.id.radioButton_adults_6:
                            sixAdultRadioButton.setTextColor(Color.WHITE);
                            oneAdultRadioButton.setTextColor(Color.BLACK);
                            twoAdultRadioButton.setTextColor(Color.BLACK);
                            threeAdultRadioButton.setTextColor(Color.BLACK);
                            fourAdultRadioButton.setTextColor(Color.BLACK);
                            fiveAdultRadioButton.setTextColor(Color.BLACK);
                            sevenAdultRadioButton.setTextColor(Color.BLACK);
                            eightAdultRadioButton.setTextColor(Color.BLACK);
                            nineAdultRadioButton.setTextColor(Color.BLACK);
                            fourChildrenRadioButton.setSaveEnabled(false);
                            fourInfantRadioButton.setSaveEnabled(false);
                            Adult = sixAdultRadioButton.getText().toString();
                            break;

                        case R.id.radioButton_adults_7:
                            sevenAdultRadioButton.setTextColor(Color.WHITE);
                            oneAdultRadioButton.setTextColor(Color.BLACK);
                            twoAdultRadioButton.setTextColor(Color.BLACK);
                            threeAdultRadioButton.setTextColor(Color.BLACK);
                            fourAdultRadioButton.setTextColor(Color.BLACK);
                            fiveAdultRadioButton.setTextColor(Color.BLACK);
                            sixAdultRadioButton.setTextColor(Color.BLACK);
                            eightAdultRadioButton.setTextColor(Color.BLACK);
                            nineAdultRadioButton.setTextColor(Color.BLACK);
                            threeChildrenRadioButton.setSaveEnabled(false);
                            threeInfantRadioButton.setSaveEnabled(false);
                            Adult = sevenAdultRadioButton.getText().toString();
                            break;

                        case R.id.radioButton_adults_8:
                            eightAdultRadioButton.setTextColor(Color.WHITE);
                            oneAdultRadioButton.setTextColor(Color.BLACK);
                            twoAdultRadioButton.setTextColor(Color.BLACK);
                            threeAdultRadioButton.setTextColor(Color.BLACK);
                            fourAdultRadioButton.setTextColor(Color.BLACK);
                            fiveAdultRadioButton.setTextColor(Color.BLACK);
                            sixAdultRadioButton.setTextColor(Color.BLACK);
                            sevenAdultRadioButton.setTextColor(Color.BLACK);
                            nineAdultRadioButton.setTextColor(Color.BLACK);
                            twoChildrenRadioButton.setSaveEnabled(false);
                            twoInfantRadioButton.setSaveEnabled(false);
                            Adult = eightAdultRadioButton.getText().toString();
                            break;

                        case R.id.radioButton_adults_9:
                            nineAdultRadioButton.setTextColor(Color.WHITE);
                            oneAdultRadioButton.setTextColor(Color.BLACK);
                            twoAdultRadioButton.setTextColor(Color.BLACK);
                            threeAdultRadioButton.setTextColor(Color.BLACK);
                            fourAdultRadioButton.setTextColor(Color.BLACK);
                            fiveAdultRadioButton.setTextColor(Color.BLACK);
                            sixAdultRadioButton.setTextColor(Color.BLACK);
                            sevenAdultRadioButton.setTextColor(Color.BLACK);
                            eightAdultRadioButton.setTextColor(Color.BLACK);
                            oneChildrenRadioButton.setSaveEnabled(false);
                            oneInfantRadioButton.setSaveEnabled(false);
                            Adult = nineAdultRadioButton.getText().toString();
                            break;
                    }
                }
            });
            childrenGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.radioButton_child_0:
                            zeroChildrenRadioButton.setTextColor(Color.WHITE);
                            oneChildrenRadioButton.setTextColor(Color.BLACK);
                            twoChildrenRadioButton.setTextColor(Color.BLACK);
                            threeChildrenRadioButton.setTextColor(Color.BLACK);
                            fourChildrenRadioButton.setTextColor(Color.BLACK);
                            fiveChildrenRadioButton.setTextColor(Color.BLACK);
                            sixChildrenRadioButton.setTextColor(Color.BLACK);
                            sevenChildrenRadioButton.setTextColor(Color.BLACK);
                            Children = zeroChildrenRadioButton.getText().toString();
                            break;
                        case R.id.radioButton_child_1:
                            zeroChildrenRadioButton.setTextColor(Color.BLACK);
                            oneChildrenRadioButton.setTextColor(Color.WHITE);
                            twoChildrenRadioButton.setTextColor(Color.BLACK);
                            threeChildrenRadioButton.setTextColor(Color.BLACK);
                            fourChildrenRadioButton.setTextColor(Color.BLACK);
                            fiveChildrenRadioButton.setTextColor(Color.BLACK);
                            sixChildrenRadioButton.setTextColor(Color.BLACK);
                            sevenChildrenRadioButton.setTextColor(Color.BLACK);
                            Children = oneChildrenRadioButton.getText().toString();
                            break;

                        case R.id.radioButton_child_2:
                            zeroChildrenRadioButton.setTextColor(Color.BLACK);
                            oneChildrenRadioButton.setTextColor(Color.BLACK);
                            twoChildrenRadioButton.setTextColor(Color.WHITE);
                            threeChildrenRadioButton.setTextColor(Color.BLACK);
                            fourChildrenRadioButton.setTextColor(Color.BLACK);
                            fiveChildrenRadioButton.setTextColor(Color.BLACK);
                            sixChildrenRadioButton.setTextColor(Color.BLACK);
                            sevenChildrenRadioButton.setTextColor(Color.BLACK);
                            Children = twoChildrenRadioButton.getText().toString();
                            break;

                        case R.id.radioButton_child_3:
                            zeroChildrenRadioButton.setTextColor(Color.BLACK);
                            oneChildrenRadioButton.setTextColor(Color.BLACK);
                            twoChildrenRadioButton.setTextColor(Color.BLACK);
                            threeChildrenRadioButton.setTextColor(Color.WHITE);
                            fourChildrenRadioButton.setTextColor(Color.BLACK);
                            fiveChildrenRadioButton.setTextColor(Color.BLACK);
                            sixChildrenRadioButton.setTextColor(Color.BLACK);
                            sevenChildrenRadioButton.setTextColor(Color.BLACK);
                            Children = threeChildrenRadioButton.getText().toString();
                            break;

                        case R.id.radioButton_child_4:
                            zeroChildrenRadioButton.setTextColor(Color.BLACK);
                            oneChildrenRadioButton.setTextColor(Color.BLACK);
                            twoChildrenRadioButton.setTextColor(Color.BLACK);
                            threeChildrenRadioButton.setTextColor(Color.BLACK);
                            fourChildrenRadioButton.setTextColor(Color.WHITE);
                            fiveChildrenRadioButton.setTextColor(Color.BLACK);
                            sixChildrenRadioButton.setTextColor(Color.BLACK);
                            sevenChildrenRadioButton.setTextColor(Color.BLACK);
                            Children = fourChildrenRadioButton.getText().toString();
                            break;

                        case R.id.radioButton_child_5:
                            zeroChildrenRadioButton.setTextColor(Color.BLACK);
                            oneChildrenRadioButton.setTextColor(Color.BLACK);
                            twoChildrenRadioButton.setTextColor(Color.BLACK);
                            threeChildrenRadioButton.setTextColor(Color.BLACK);
                            fourChildrenRadioButton.setTextColor(Color.BLACK);
                            fiveChildrenRadioButton.setTextColor(Color.WHITE);
                            sixChildrenRadioButton.setTextColor(Color.BLACK);
                            sevenChildrenRadioButton.setTextColor(Color.BLACK);
                            Children = fiveChildrenRadioButton.getText().toString();
                            break;

                        case R.id.radioButton_child_6:
                            zeroChildrenRadioButton.setTextColor(Color.BLACK);
                            oneChildrenRadioButton.setTextColor(Color.BLACK);
                            twoChildrenRadioButton.setTextColor(Color.BLACK);
                            threeChildrenRadioButton.setTextColor(Color.BLACK);
                            fourChildrenRadioButton.setTextColor(Color.BLACK);
                            fiveChildrenRadioButton.setTextColor(Color.BLACK);
                            sixChildrenRadioButton.setTextColor(Color.WHITE);
                            sevenChildrenRadioButton.setTextColor(Color.BLACK);
                            Children = sixChildrenRadioButton.getText().toString();
                            break;

                        case R.id.radioButton_child_7:
                            zeroChildrenRadioButton.setTextColor(Color.BLACK);
                            oneChildrenRadioButton.setTextColor(Color.BLACK);
                            twoChildrenRadioButton.setTextColor(Color.BLACK);
                            threeChildrenRadioButton.setTextColor(Color.BLACK);
                            fourChildrenRadioButton.setTextColor(Color.BLACK);
                            fiveChildrenRadioButton.setTextColor(Color.BLACK);
                            sixChildrenRadioButton.setTextColor(Color.BLACK);
                            sevenChildrenRadioButton.setTextColor(Color.WHITE);
                            Children = sevenChildrenRadioButton.getText().toString();
                            break;
                    }
                }
            });
            infantGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.radioButton_infants_0:
                            zeroInfantRadioButton.setTextColor(Color.WHITE);
                            oneInfantRadioButton.setTextColor(Color.BLACK);
                            twoInfantRadioButton.setTextColor(Color.BLACK);
                            threeInfantRadioButton.setTextColor(Color.BLACK);
                            fourInfantRadioButton.setTextColor(Color.BLACK);
                            fiveInfantRadioButton.setTextColor(Color.BLACK);
                            sixInfantRadioButton.setTextColor(Color.BLACK);
                            sevenInfantRadioButton.setTextColor(Color.BLACK);
                            Infant = zeroInfantRadioButton.getText().toString();
                            break;
                        case R.id.radioButton_infants_1:
                            zeroInfantRadioButton.setTextColor(Color.BLACK);
                            oneInfantRadioButton.setTextColor(Color.WHITE);
                            twoInfantRadioButton.setTextColor(Color.BLACK);
                            threeInfantRadioButton.setTextColor(Color.BLACK);
                            fourInfantRadioButton.setTextColor(Color.BLACK);
                            fiveInfantRadioButton.setTextColor(Color.BLACK);
                            sixInfantRadioButton.setTextColor(Color.BLACK);
                            sevenInfantRadioButton.setTextColor(Color.BLACK);
                            Infant = oneInfantRadioButton.getText().toString();
                            break;

                        case R.id.radioButton_infants_2:
                            zeroInfantRadioButton.setTextColor(Color.BLACK);
                            oneInfantRadioButton.setTextColor(Color.BLACK);
                            twoInfantRadioButton.setTextColor(Color.WHITE);
                            threeInfantRadioButton.setTextColor(Color.BLACK);
                            fourInfantRadioButton.setTextColor(Color.BLACK);
                            fiveInfantRadioButton.setTextColor(Color.BLACK);
                            sixInfantRadioButton.setTextColor(Color.BLACK);
                            sevenInfantRadioButton.setTextColor(Color.BLACK);
                            Infant = twoInfantRadioButton.getText().toString();
                            break;

                        case R.id.radioButton_infants_3:
                            zeroInfantRadioButton.setTextColor(Color.BLACK);
                            oneInfantRadioButton.setTextColor(Color.BLACK);
                            twoInfantRadioButton.setTextColor(Color.BLACK);
                            threeInfantRadioButton.setTextColor(Color.WHITE);
                            fourInfantRadioButton.setTextColor(Color.BLACK);
                            fiveInfantRadioButton.setTextColor(Color.BLACK);
                            sixInfantRadioButton.setTextColor(Color.BLACK);
                            sevenInfantRadioButton.setTextColor(Color.BLACK);
                            Infant = threeInfantRadioButton.getText().toString();
                            break;

                        case R.id.radioButton_infants_4:
                            zeroInfantRadioButton.setTextColor(Color.BLACK);
                            oneInfantRadioButton.setTextColor(Color.BLACK);
                            twoInfantRadioButton.setTextColor(Color.BLACK);
                            threeInfantRadioButton.setTextColor(Color.BLACK);
                            fourInfantRadioButton.setTextColor(Color.WHITE);
                            fiveInfantRadioButton.setTextColor(Color.BLACK);
                            sixInfantRadioButton.setTextColor(Color.BLACK);
                            sevenInfantRadioButton.setTextColor(Color.BLACK);
                            Infant = fourInfantRadioButton.getText().toString();
                            break;

                        case R.id.radioButton_infants_5:
                            zeroInfantRadioButton.setTextColor(Color.BLACK);
                            oneInfantRadioButton.setTextColor(Color.BLACK);
                            twoInfantRadioButton.setTextColor(Color.BLACK);
                            threeInfantRadioButton.setTextColor(Color.BLACK);
                            fourInfantRadioButton.setTextColor(Color.BLACK);
                            fiveInfantRadioButton.setTextColor(Color.WHITE);
                            sixInfantRadioButton.setTextColor(Color.BLACK);
                            sevenInfantRadioButton.setTextColor(Color.BLACK);
                            Infant = fiveInfantRadioButton.getText().toString();
                            break;

                        case R.id.radioButton_infants_6:
                            zeroInfantRadioButton.setTextColor(Color.BLACK);
                            oneInfantRadioButton.setTextColor(Color.BLACK);
                            twoInfantRadioButton.setTextColor(Color.BLACK);
                            threeInfantRadioButton.setTextColor(Color.BLACK);
                            fourInfantRadioButton.setTextColor(Color.BLACK);
                            fiveInfantRadioButton.setTextColor(Color.BLACK);
                            sixInfantRadioButton.setTextColor(Color.WHITE);
                            sevenInfantRadioButton.setTextColor(Color.BLACK);
                            Infant = sixInfantRadioButton.getText().toString();
                            break;

                        case R.id.radioButton_infants_7:
                            zeroInfantRadioButton.setTextColor(Color.BLACK);
                            oneInfantRadioButton.setTextColor(Color.BLACK);
                            twoInfantRadioButton.setTextColor(Color.BLACK);
                            threeInfantRadioButton.setTextColor(Color.BLACK);
                            fourInfantRadioButton.setTextColor(Color.BLACK);
                            fiveInfantRadioButton.setTextColor(Color.BLACK);
                            sixInfantRadioButton.setTextColor(Color.BLACK);
                            sevenInfantRadioButton.setTextColor(Color.WHITE);
                            Infant = sevenInfantRadioButton.getText().toString();
                            break;
                    }

                }
            });
            classGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.economy:
                            economyRadioButton.setTextColor(Color.WHITE);
                            premiumEconomyRadioButton.setTextColor(Color.BLACK);
                            businessRadioButton.setTextColor(Color.BLACK);
                            firstClassRadioButton.setTextColor(Color.BLACK);
                            Class = economyRadioButton.getText().toString().toUpperCase();
                            break;

                        case R.id.premium_economy:
                            economyRadioButton.setTextColor(Color.BLACK);
                            premiumEconomyRadioButton.setTextColor(Color.WHITE);
                            businessRadioButton.setTextColor(Color.BLACK);
                            firstClassRadioButton.setTextColor(Color.BLACK);

                            Class = premiumEconomyRadioButton.getText().toString().toUpperCase();
                            break;
                        case R.id.business:
                            economyRadioButton.setTextColor(Color.BLACK);
                            premiumEconomyRadioButton.setTextColor(Color.BLACK);
                            businessRadioButton.setTextColor(Color.WHITE);
                            firstClassRadioButton.setTextColor(Color.BLACK);
                            Class = businessRadioButton.getText().toString().toUpperCase();
                            break;
                        case R.id.firstclass:
                            economyRadioButton.setTextColor(Color.BLACK);
                            premiumEconomyRadioButton.setTextColor(Color.BLACK);
                            businessRadioButton.setTextColor(Color.BLACK);
                            firstClassRadioButton.setTextColor(Color.WHITE);
                            Class = firstClassRadioButton.getText().toString().toUpperCase();
                            break;
                    }
                }
            });
            final Dialog toolbarSearchDialog = new Dialog(getContext(), R.style.MaterialSearch);
            toolbarSearchDialog.setContentView(view1);
            toolbarSearchDialog.setCancelable(true);
            toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
            toolbarSearchDialog.show();
            toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            done.setOnClickListener(view3 -> {
                int adult = Integer.parseInt(Adult);
                int child = Integer.parseInt(Children);
                int infant = Integer.parseInt(Infant);
                int total = adult + child + infant;
                personTextView.setText(total + " Traveller");
                classTextView.setText(Class);
                toolbarSearchDialog.dismiss();
            });
        });
*/
        TC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isDialogOpen) {
                    isDialogOpen = true;
                    View v = View.inflate(getContext(), R.layout.travellers_and_class_layout, null);
                    showDialogTC(v);
                }
            }
        });
        search.setOnClickListener(view2 -> {
            String fa = fromAirport;
            String ta = toAirport;
            String td = travelDate;
            if (fa.equals("")) {
                Toast.makeText(getContext(), "Please Select Departure Airport", Toast.LENGTH_LONG).show();
            } else if (ta.equals("")) {
                Toast.makeText(getContext(), "Please Select Arrival Airport", Toast.LENGTH_LONG).show();
            } else if (td.equals("")) {
                Toast.makeText(getContext(), "Please Select Dates", Toast.LENGTH_LONG).show();
            } else {
                JSONObject jsonObject = new JSONObject();
                JSONObject searchQuery = new JSONObject();
                JSONObject paxInfo = new JSONObject();
                JSONArray routeInfos = new JSONArray();
                JSONObject fromCityOrAirport = new JSONObject();
                JSONObject toCityOrAirport = new JSONObject();
                if (oneWay) {
                    JSONObject searchModifiers = new JSONObject();

                    try {
                        searchQuery.put("cabinClass", Class);
                        paxInfo.put("ADULT", Adult);
                        paxInfo.put("CHILD", Children);
                        paxInfo.put("INFANT", Infant);
                        fromCityOrAirport.put("code", fromAirport);
                        toCityOrAirport.put("code", toAirport);
                        routeInfos.put(0, new JSONObject().put("fromCityOrAirport", fromCityOrAirport).put("toCityOrAirport", toCityOrAirport).put("travelDate", travelDate));
                        searchQuery.put("paxInfo", paxInfo);
                        searchQuery.put("routeInfos", routeInfos);
                        jsonObject.put("searchQuery", searchQuery);
                        Gson gson = new Gson();
                        JsonObject jsonObject1 = gson.fromJson(String.valueOf(jsonObject), new TypeToken<JsonObject>() {
                        }.getType());
                        System.out.println("gson.toJson(jsonObject) = " + gson.toJson(jsonObject));
                        System.out.println("gson.toJson(jsonObject) = " + gson.toJson(jsonObject1));


                        startActivity(new Intent(getContext(), FlightListActivity.class).putExtra("json", String.valueOf(jsonObject1)).putExtra("type", type).putExtra("fromAirportCity", fromAirportCity).putExtra("toAirportCity", toAirportCity));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        searchQuery.put("cabinClass", Class);
                        paxInfo.put("ADULT", Adult);
                        paxInfo.put("CHILD", Children);
                        paxInfo.put("INFANT", Infant);
                        fromCityOrAirport.put("code", fromAirport);
                        toCityOrAirport.put("code", toAirport);
                        routeInfos.put(0, new JSONObject().put("fromCityOrAirport", fromCityOrAirport).put("toCityOrAirport", toCityOrAirport).put("travelDate", travelDate));
                        routeInfos.put(1, new JSONObject().put("fromCityOrAirport", toCityOrAirport).put("toCityOrAirport", fromCityOrAirport).put("travelDate", returnTravelDate));
                        searchQuery.put("paxInfo", paxInfo);
                        searchQuery.put("routeInfos", routeInfos);
                        jsonObject.put("searchQuery", searchQuery);
                        Gson gson = new Gson();
                        JsonObject jsonObject1 = gson.fromJson(String.valueOf(jsonObject), new TypeToken<JsonObject>() {
                        }.getType());
                        System.out.println("gson.toJson(jsonObject) = " + gson.toJson(jsonObject));
                        System.out.println("gson.toJson(jsonObject) = " + gson.toJson(jsonObject1));
                        if (type.equalsIgnoreCase("Domestic")) {
                            startActivity(new Intent(getContext(), ReturnFlightsActivity.class).putExtra("json", String.valueOf(jsonObject1)).putExtra("type", type).putExtra("fromAirportCity", fromAirportCity).putExtra("toAirportCity", toAirportCity));
                        } else if (type.equalsIgnoreCase("International")) {
                            startActivity(new Intent(getContext(), InternationalReturnFragment.class).putExtra("json", String.valueOf(jsonObject1)).putExtra("type", type).putExtra("fromAirportCity", fromAirportCity).putExtra("toAirportCity", toAirportCity));
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        return view;
    }

    private void showDialogTC(View v) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(v);

        ImageView closeDialog = dialog.findViewById(R.id.closeDialog);
        Button done = dialog.findViewById(R.id.DoneTC);

        TextView AdultAdd, CAdd, IAdd, AdultReduce, CReduce, IReduce;
        TextView AdultCount, ChildrenCount, InfantCount;

        AdultAdd = dialog.findViewById(R.id.AaddBTN);
        AdultReduce = dialog.findViewById(R.id.ASubBTN);
        AdultCount = dialog.findViewById(R.id.AdultCount);
        CAdd = dialog.findViewById(R.id.CAddBTN);
        CReduce = dialog.findViewById(R.id.CSubBTN);
        ChildrenCount = dialog.findViewById(R.id.ChildrenCount);
        IAdd = dialog.findViewById(R.id.IAddBTN);
        IReduce = dialog.findViewById(R.id.ISubBTN);
        InfantCount = dialog.findViewById(R.id.InfantsCount);

        // CLASS
        RadioButton eco, priEco, Business, firstClassRadioButton;
        eco = dialog.findViewById(R.id.economyClass);
        priEco = dialog.findViewById(R.id.premiumEconomy);
        Business = dialog.findViewById(R.id.business);
        firstClassRadioButton = dialog.findViewById(R.id.firstclass);

        RadioGroup classT = dialog.findViewById(R.id.ClassRadioG);
        eco.setChecked(true);
        classTextView.setText(eco.getText().toString());
        Class = "ECONOMY";
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


        final int[] adult = {Integer.parseInt(AdultCount.getText().toString())};
        final int[] children = {Integer.parseInt(ChildrenCount.getText().toString())};
        final int[] infant = {Integer.parseInt(InfantCount.getText().toString())};

        classT.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.economyClass:
                        Class = "ECONOMY";
                        classTextView.setText(eco.getText().toString());
                        break;

                    case R.id.premiumEconomy:
                        Class = "PREMIUM_ECONOMY";
                        classTextView.setText(priEco.getText().toString());
                        break;
                    case R.id.business:
                        Class = "BUSINESS";
                        classTextView.setText(Business.getText().toString());
                        break;
                    case R.id.firstclass:
                        Class = "FIRST";
                        classTextView.setText(firstClassRadioButton.getText().toString());
                        break;
                }
            }
        });

        AdultAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adult[0] = adult[0] + 1;
                int childAdultCombine = 9 - children[0];

                if (adult[0] > 9 || adult[0] > childAdultCombine) {
                    AdultAdd.setEnabled(false);
                    CAdd.setEnabled(false);
                } else if (infant[0] < adult[0]) {
                    IAdd.setEnabled(true);
                }
                AdultReduce.setEnabled(true);
                AdultCount.setText(String.valueOf(adult[0]));
            }
        });
        CAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                children[0] = children[0] + 1;
                int childAdultCombine2 = 9 - adult[0];

                if (children[0] >= childAdultCombine2) {
                    CAdd.setEnabled(false);
                } else if (children[0] > 0) {
                    CReduce.setEnabled(true);
                } else {
                    CAdd.setEnabled(true);
                }
                ChildrenCount.setText(String.valueOf(children[0]));
            }
        });
        IAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infant[0] = infant[0] + 1;
                Toast.makeText(getContext(), String.valueOf(adult[0]), Toast.LENGTH_SHORT).show();
                if (infant[0] == adult[0]) {
                    IAdd.setEnabled(false);
                    IReduce.setEnabled(true);
                } else {
                    IReduce.setEnabled(true);
                    IAdd.setEnabled(true);
                }
                InfantCount.setText(String.valueOf(infant[0]));
            }
        });

        AdultReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int childAdultCombine2 = 9 - children[0];
                adult[0] = adult[0] - 1;

                if (adult[0] <= infant[0]) {
                    infant[0] = adult[0];
                    if (adult[0] <= 1) {
                        AdultReduce.setEnabled(false);
                    }
                    InfantCount.setText(String.valueOf(infant[0]));
                    IAdd.setEnabled(false);
                } else if (infant[0] > 0) {
                    IReduce.setEnabled(true);
                } else if (adult[0] < childAdultCombine2) {
                    AdultAdd.setEnabled(true);
                    CAdd.setEnabled(true);
                }

                AdultCount.setText(String.valueOf(adult[0]));
            }
        });
        CReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                children[0] = children[0] - 1;
                int childAdultCombine2 = 9 - adult[0];
                if (children[0] == 0) {
                    CReduce.setEnabled(false);
                } else if (children[0] < childAdultCombine2) {
                    CAdd.setEnabled(true);
                    AdultAdd.setEnabled(true);
                } else {
                    CReduce.setEnabled(true);
                }
                ChildrenCount.setText(String.valueOf(children[0]));

            }
        });
        IReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infant[0] = infant[0] - 1;
                if (infant[0] < 1) {
                    IReduce.setEnabled(false);
                    IAdd.setEnabled(true);
                } else if (infant[0] <= adult[0]) {
                    IAdd.setEnabled(true);
                } else {
                    IReduce.setEnabled(true);
                }
                InfantCount.setText(String.valueOf(infant[0]));
            }
        });

        if (adult[0] == 1) {
            AdultAdd.setEnabled(true);
        } else if (children[0] == 0) {
            CAdd.setEnabled(true);
        } else if (infant[0] < adult[0]) {
            IAdd.setEnabled(true);
        }


//        Toast.makeText(getContext(),""+adult[0], Toast.LENGTH_SHORT).show();


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Adult = AdultCount.getText().toString();
                Children = ChildrenCount.getText().toString();
                Infant = InfantCount.getText().toString();
                int adult = Integer.parseInt(Adult);
                int child = Integer.parseInt(Children);
                int infant = Integer.parseInt(Infant);
                int total = adult + child + infant;
                if (total > 1) {
                    personTextView.setText(total + " Travellers");
                } else {
                    personTextView.setText(total + " Traveller");
                }
                System.out.println("Class = " + Class);
//                classTextView.setText(Class);
//                travellersDetailsLists.add(new TravellersDetailsList(AdultsCounts, childrenCounts, infantsCounts));
//                AdultsCounts.add(Integer.parseInt(AdultCount.getText().toString()));
//                childrenCounts.add((Integer.parseInt(ChildrenCount.getText().toString())));
//                infantsCounts.add((Integer.parseInt(InfantCount.getText().toString())));
                dialog.cancel();
                isDialogOpen = false;
            }
        });
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                isDialogOpen = false;
            }
        });
    }

    private void showDialog(View v, List<AirportCode> airportCodeList, List<AirportCode> popularAirportCodeList, TextView fromTextView, TextView fromCityTextView, String fromAirport) {

        Gson gson = new Gson();
        ImageView imgToolBack = v.findViewById(R.id.img_tool_back);
        final EditText edtToolSearch = v.findViewById(R.id.edt_tool_search);
        ImageView imgToolMic = v.findViewById(R.id.img_tool_mic);
        final ListView listSearch = v.findViewById(R.id.list_search);
        final RecyclerView popularList_search = v.findViewById(R.id.popularList_search);
        final TextView txtEmpty = v.findViewById(R.id.txt_empty);
        for (AirportCode airportCode : airportCodeList) {
            if (airportCode.isPopular()) {
                popularAirportCodeList.add(airportCode);
            }
        }

        final Dialog toolbarSearchDialog = new Dialog(getContext(), R.style.MaterialSearch);
        toolbarSearchDialog.setContentView(v);
        toolbarSearchDialog.setCancelable(true);
        toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
        toolbarSearchDialog.show();

        toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        airportCodeList = (airportCodeList != null && airportCodeList.size() > 0) ? airportCodeList : new ArrayList<>();
        System.out.println("gson = " + gson.toJson(airportCodeList));
        System.out.println("gson = " + gson.toJson(popularAirportCodeList));
        ArrivalCityAdapter popularArrivalCityAdapter = new ArrivalCityAdapter(popularAirportCodeList, getContext(), toolbarSearchDialog, fromCityTextView, fromTextView, fromAirport, type, popularList_search);
        popularList_search.setVisibility(View.VISIBLE);
        listSearch.setVisibility(View.VISIBLE);
        final SearchAdapter searchAdapter = new SearchAdapter(getContext(), airportCodeList, false);

        popularList_search.setLayoutManager(new LinearLayoutManager(getContext()));
        popularList_search.setAdapter(popularArrivalCityAdapter);
        listSearch.setAdapter(searchAdapter);
        listSearch.setOnItemClickListener((adapterView, view4, i, l) -> {
            AirportCode fromAirportCode = (AirportCode) adapterView.getItemAtPosition(i);
            if (!type.matches("International")) {
                if (fromAirportCode.getCountry().equalsIgnoreCase("India")) {
                    type = "Domestic";
                } else {
                    type = "International";
                }
            }
            fromTextView.setText(fromAirportCode.getCity());
            listSearch.setVisibility(View.GONE);
            toolbarSearchDialog.dismiss();
        });
        imgToolBack.setOnClickListener(view3 -> {
            toolbarSearchDialog.dismiss();
        });
        imgToolMic.setOnClickListener(view3 -> {
            edtToolSearch.setText("");
        });
        edtToolSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                listSearch.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                airportCodes = SharedPreference.loadList(getContext(), "NameList", "Names");
                System.out.println("gson name = " + gson.toJson(airportCodes));
                System.out.println("nameListWith = " + airportCodes.size());
                listSearch.setVisibility(View.VISIBLE);
                searchAdapter.updateList(airportCodes, true);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Gson gson = new Gson();
                List<AirportCode> filterList = new ArrayList<>();
                String json = null;
                boolean isNodata = false;
                if (charSequence.length() > 0) {
                    for (AirportCode airportCode : airportCodes) {
                        if (airportCode.getCity().toLowerCase().startsWith(String.valueOf(charSequence)) || airportCode.getCode().toLowerCase().startsWith(String.valueOf(charSequence))) {
                            System.out.println("airportCode = " + airportCode.getCode());
                            System.out.println("airportCode.getCity() = " + airportCode.getCity());
                            System.out.println("airportCode.getName() = " + airportCode.getName());
                            filterList.add(airportCode);
                            listSearch.setVisibility(View.VISIBLE);
                            popularList_search.setVisibility(View.GONE);
                            searchAdapter.updateList(filterList, true);
                            isNodata = true;
                        }
                    }
                    if (!isNodata) {
                        listSearch.setVisibility(View.GONE);
                        popularList_search.setVisibility(View.GONE);
                        txtEmpty.setVisibility(View.VISIBLE);
                        txtEmpty.setText("No data found");
                    }
                } else {
                    listSearch.setVisibility(View.GONE);
                    popularList_search.setVisibility(View.GONE);
                    txtEmpty.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void setupRangePickerDialog(boolean oneWay) throws ParseException {
        if (oneWay) {
            MaterialDatePicker.Builder<Long> builder =
                    MaterialDatePicker.Builder.datePicker();
            builder.setCalendarConstraints(limitRange().build());
            builder.setSelection(Calendar.getInstance().getTimeInMillis());
            MaterialDatePicker<Long> picker = builder.build();
            picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                @Override
                public void onPositiveButtonClick(Long selection) {
                    Date date = new Date(selection);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yy");
                    travelDate = sdf1.format(date);
                    whenTextView.setText(sdf.format(date));
                    isDialogOpen = false;
                }
            });
            picker.addOnCancelListener(dialogInterface -> {
                isDialogOpen = false;
            });
            picker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isDialogOpen = false;
                }
            });
            picker.addOnDismissListener(dialogInterface -> {
                isDialogOpen = false;
            });
            picker.show(getChildFragmentManager(), picker.toString());
        } else {
            MaterialDatePicker.Builder<Pair<Long, Long>> builderRange =
                    MaterialDatePicker.Builder.dateRangePicker();
            builderRange.setCalendarConstraints(limitRange().build());
          /*  SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf1.parse(travelDate);
            Date date1 = sdf1.parse(returnTravelDate);
            System.out.println("date1 = " + date1);
            System.out.println("date1 = " + date1.getTime());
            System.out.println("date = " + date);
            System.out.println("date = " + date.getTime());
            Pair<Long, Long> selection =new Pair<>(date.getTime(),date1.getTime());
            System.out.println("selection = " + selection);*/

            MaterialDatePicker<Pair<Long, Long>> pickerRange = builderRange.build();
            pickerRange.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                @Override
                public void onPositiveButtonClick(Pair<Long, Long> selection) {
                    System.out.println("selection.first.toString() = " + selection.first.toString());
                    System.out.println("selection.second.toString() = " + selection.second.toString());
                    Date date1 = new Date(selection.first);
                    Date date2 = new Date(selection.second);
                    System.out.println("date = " + date1);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat sdf2 = new SimpleDateFormat("E, dd MMM yy");
                    System.out.println("sdf1.format(date) = " + sdf1.format(date1));
                    travelDate = sdf1.format(date1);
                    returnTravelDate = sdf1.format(date2);
                    String date = sdf2.format(date1);
                    String retDate = sdf2.format(date2);
                    whenTextView.setText(date);
                    whenReturnTextView.setText(retDate);
                    isDialogOpen = false;
                }
            });
            pickerRange.show(getChildFragmentManager(), pickerRange.toString());
        }
    }

    private CalendarConstraints.Builder limitRange() {
        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();

        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();

        int year = 2023;
        int startMonth = 05;
        int startDate = 17;

        int endMonth = 07;
        int endDate = 20;

        calendarStart.set(Calendar.getInstance(Locale.getDefault()).get(Calendar.YEAR), Calendar.getInstance(Locale.getDefault()).get(Calendar.MONTH), Calendar.getInstance(Locale.getDefault()).get(Calendar.DAY_OF_MONTH) - 1);
        calendarEnd.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 6, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        long minDate = calendarStart.getTimeInMillis();
        long maxDate = calendarEnd.getTimeInMillis();
        System.out.println("maxDate = " + calendarEnd.getTime());
        System.out.println("minDate = " + calendarStart.getTime());
        constraintsBuilderRange.setStart(minDate);
        constraintsBuilderRange.setEnd(maxDate);

        constraintsBuilderRange.setValidator(new RangeValidator(minDate, maxDate));

        return constraintsBuilderRange;
    }

    public static class RangeValidator implements CalendarConstraints.DateValidator {
        private final long minDate;
        private final long maxDate;

        public RangeValidator(long minDate, long maxDate) {
            this.minDate = minDate;
            this.maxDate = maxDate;
        }

        protected RangeValidator(Parcel in) {
            minDate = in.readLong();
            maxDate = in.readLong();
        }

        @Override
        public boolean isValid(long date) {
            return !(minDate > date || maxDate < date);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(minDate);
            dest.writeLong(maxDate);
        }

        public static final Parcelable.Creator<RangeValidator> CREATOR =
                new Parcelable.Creator<RangeValidator>() {
                    @Override
                    public RangeValidator createFromParcel(Parcel in) {
                        return new RangeValidator(in);
                    }

                    @Override
                    public RangeValidator[] newArray(int size) {
                        return new RangeValidator[size];
                    }
                };
    }
}