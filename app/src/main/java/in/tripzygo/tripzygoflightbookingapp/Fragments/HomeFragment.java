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

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kizitonwose.calendar.view.CalendarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.tripzygo.tripzygoflightbookingapp.FlightListActivity;
import in.tripzygo.tripzygoflightbookingapp.Modals.AirportCode;
import in.tripzygo.tripzygoflightbookingapp.R;
import in.tripzygo.tripzygoflightbookingapp.ReturnFlightsActivity;
import in.tripzygo.tripzygoflightbookingapp.SearchAdapter;
import in.tripzygo.tripzygoflightbookingapp.Tools.SharedPreference;

public class HomeFragment extends Fragment {
    TextView fromTextView, toTextView, whenTextView;
    TextView personTextView, classTextView;
    Button search;
    RadioGroup radioGroup;
    RadioButton oneWayRadioButton, returnRadioButton;
    List<AirportCode> airportCodes = new ArrayList<>();
    DatePickerDialog.OnDateSetListener dateSetListener;
    public static CalendarView calendarView;
    public static LocalDate localDate;
    String Adult = "1", Children = "0", Infant = "0", Class = "ECONOMY", fromAirport = "", toAirport = "", travelDate = "", returnTravelDate;
    boolean oneWay = true;
    LinearLayout passenger;

    public HomeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fromTextView = view.findViewById(R.id.fromText);
        toTextView = view.findViewById(R.id.toText);
        whenTextView = view.findViewById(R.id.calendarText);
        personTextView = view.findViewById(R.id.personText);
        classTextView = view.findViewById(R.id.classText);
        search = view.findViewById(R.id.search);
        passenger = view.findViewById(R.id.passenger);
        radioGroup = view.findViewById(R.id.radio_type);
        oneWayRadioButton = view.findViewById(R.id.oneway);
        returnRadioButton = view.findViewById(R.id.returnway);
        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            switch (radioGroup1.getCheckedRadioButtonId()) {
                case R.id.oneway:
                    oneWay = true;
                    oneWayRadioButton.setTextColor(Color.BLACK);
                    oneWayRadioButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.plane_solid, 0, 0, 0);
                    returnRadioButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.plane_solid_white, 0, 0, 0);
                    returnRadioButton.setTextColor(Color.WHITE);
                    break;
                case R.id.returnway:
                    oneWay = false;
                    oneWayRadioButton.setTextColor(Color.WHITE);
                    oneWayRadioButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.plane_solid_white, 0, 0, 0);
                    returnRadioButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.plane_solid, 0, 0, 0);
                    returnRadioButton.setTextColor(Color.BLACK);
                    break;
            }
        });
        fromTextView.setOnClickListener(view1 -> {
            Gson gson = new Gson();
            String json = null;
            try {
                InputStream is = getContext().getAssets().open("airportcodes.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
                airportCodes = gson.fromJson(json, new TypeToken<List<AirportCode>>() {
                }.getType());
                SharedPreference.storeList(getContext(), "NameList", "Names", airportCodes);
                final SearchAdapter searchAdapter = new SearchAdapter(getContext(), airportCodes, false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            List<AirportCode> exerciseStored = SharedPreference.loadList(getContext(), "NameList", "Names");

            View view2 = getLayoutInflater().inflate(R.layout.search, null);
            ImageView imgToolBack = view2.findViewById(R.id.img_tool_back);
            final EditText edtToolSearch = view2.findViewById(R.id.edt_tool_search);
            ImageView imgToolMic = view2.findViewById(R.id.img_tool_mic);
            final ListView listSearch = view2.findViewById(R.id.list_search);
            final TextView txtEmpty = view2.findViewById(R.id.txt_empty);


            final Dialog toolbarSearchDialog = new Dialog(getContext(), R.style.MaterialSearch);
            toolbarSearchDialog.setContentView(view2);
            toolbarSearchDialog.setCancelable(true);
            toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
            toolbarSearchDialog.show();

            toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            exerciseStored = (exerciseStored != null && exerciseStored.size() > 0) ? exerciseStored : new ArrayList<>();
            final SearchAdapter searchAdapter = new SearchAdapter(getContext(), exerciseStored, false);
            listSearch.setVisibility(View.VISIBLE);
            listSearch.setAdapter(searchAdapter);
            listSearch.setOnItemClickListener((adapterView, view3, i, l) -> {
                AirportCode fromAirportCode = (AirportCode) adapterView.getItemAtPosition(i);
                fromTextView.setText(fromAirportCode.getCity());
                fromAirport = fromAirportCode.getCode();
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
                                searchAdapter.updateList(filterList, true);
                                isNodata = true;
                            }
                        }
                        if (!isNodata) {
                            listSearch.setVisibility(View.GONE);
                            txtEmpty.setVisibility(View.VISIBLE);
                            txtEmpty.setText("No data found");
                        }
                    } else {
                        listSearch.setVisibility(View.GONE);
                        txtEmpty.setVisibility(View.GONE);
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        });
        toTextView.setOnClickListener(view2 -> {
            Gson gson = new Gson();
            String json = null;
            try {
                InputStream is = getContext().getAssets().open("airportcodes.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
                airportCodes = gson.fromJson(json, new TypeToken<List<AirportCode>>() {
                }.getType());
                SharedPreference.storeList(getContext(), "NameList", "Names", airportCodes);
                final SearchAdapter searchAdapter = new SearchAdapter(getContext(), airportCodes, false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            List<AirportCode> exerciseStored = SharedPreference.loadList(getContext(), "NameList", "Names");

            View view3 = getLayoutInflater().inflate(R.layout.search, null);
            ImageView imgToolBack = view3.findViewById(R.id.img_tool_back);
            final EditText edtToolSearch = view3.findViewById(R.id.edt_tool_search);
            ImageView imgToolMic = view3.findViewById(R.id.img_tool_mic);
            final ListView listSearch = view3.findViewById(R.id.list_search);
            final TextView txtEmpty = view3.findViewById(R.id.txt_empty);


            final Dialog toolbarSearchDialog = new Dialog(getContext(), R.style.MaterialSearch);
            toolbarSearchDialog.setContentView(view3);
            toolbarSearchDialog.setCancelable(true);
            toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
            toolbarSearchDialog.show();

            toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            exerciseStored = (exerciseStored != null && exerciseStored.size() > 0) ? exerciseStored : new ArrayList<>();
            final SearchAdapter searchAdapter = new SearchAdapter(getContext(), exerciseStored, false);
            listSearch.setVisibility(View.VISIBLE);
            listSearch.setAdapter(searchAdapter);
            listSearch.setOnItemClickListener((adapterView, view4, i, l) -> {
                AirportCode fromAirportCode = (AirportCode) adapterView.getItemAtPosition(i);
                toTextView.setText(fromAirportCode.getCity());
                toAirport = fromAirportCode.getCode();
                listSearch.setVisibility(View.GONE);
                toolbarSearchDialog.dismiss();
            });
            imgToolBack.setOnClickListener(view4 -> {
                toolbarSearchDialog.dismiss();
            });
            imgToolMic.setOnClickListener(view4 -> {
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
                                searchAdapter.updateList(filterList, true);
                                isNodata = true;
                            }
                        }
                        if (!isNodata) {
                            listSearch.setVisibility(View.GONE);
                            txtEmpty.setVisibility(View.VISIBLE);
                            txtEmpty.setText("No data found");
                        }
                    } else {
                        listSearch.setVisibility(View.GONE);
                        txtEmpty.setVisibility(View.GONE);
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

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
            setupRangePickerDialog(oneWay);
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


                        startActivity(new Intent(getContext(), FlightListActivity.class).putExtra("json", String.valueOf(jsonObject1)));
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


                        startActivity(new Intent(getContext(), ReturnFlightsActivity.class).putExtra("json", String.valueOf(jsonObject1)));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        return view;
    }

    private void setupRangePickerDialog(boolean oneWay) {
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
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
                    travelDate = sdf1.format(date);
                    whenTextView.setText(sdf.format(date));
                }
            });
            picker.show(getChildFragmentManager(), picker.toString());
        } else {
            MaterialDatePicker.Builder<Pair<Long, Long>> builderRange =
                    MaterialDatePicker.Builder.dateRangePicker();
            builderRange.setCalendarConstraints(limitRange().build());
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
                    SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM");
                    System.out.println("sdf1.format(date) = " + sdf1.format(date1));
                    travelDate = sdf1.format(date1);
                    returnTravelDate = sdf1.format(date2);
                    String date = sdf2.format(date1) + " - " + sdf2.format(date2);
                    whenTextView.setText(date);
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
        private long minDate;
        private long maxDate;

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