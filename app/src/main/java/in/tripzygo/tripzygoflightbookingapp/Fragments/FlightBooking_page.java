package in.tripzygo.tripzygoflightbookingapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;
import com.kizitonwose.calendar.view.ViewContainer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import in.tripzygo.tripzygoflightbookingapp.ArrivalCityAdapter;
import in.tripzygo.tripzygoflightbookingapp.DayViewContainer1;
import in.tripzygo.tripzygoflightbookingapp.Modals.AirportCode;
import in.tripzygo.tripzygoflightbookingapp.R;
import in.tripzygo.tripzygoflightbookingapp.Tools.SharedPreference;

public class FlightBooking_page extends Fragment {

    // Add return
    TextView returnTitle, returnDate, returnDay;

    FrameLayout fm;

    // CLASS & TRAVELLERS
    TextView classLevel, travellersNumberText, departureDate, departureDay;

    RadioButton oneWay, roundTrip;
    RecyclerView arrivalCity;
    Button searchFlightBTN;
    ConstraintLayout TC, DepartureDate, returnTripDateContainer;
    ArrayList<Integer> AdultsCounts = null;
    ArrayList<Integer> childrenCounts = null;
    ArrayList<Integer> infantsCounts = null;
    ArrayList<String> COUNT = new ArrayList<>();
    List<AirportCode> airportCodes = new ArrayList<>();

//    ArrayList<TravellersDetailsList> travellersDetailsLists = new ArrayList<TravellersDetailsList>();
//    ArrayList<city> cities = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView firstCityCode = view.findViewById(R.id.fromText);
        TextView firstCityName = view.findViewById(R.id.firstCity);
        TextView secondCityCode = view.findViewById(R.id.toText);
        TextView secondCityCodeName = view.findViewById(R.id.secondCity);

        final DayViewContainer1[] selectCurrentView = new DayViewContainer1[2];

        ImageView swapImg = view.findViewById(R.id.exchange);
        searchFlightBTN = view.findViewById(R.id.SearchFlightBTN);
        TC = view.findViewById(R.id.travellersAndClassContainer);
        DepartureDate = view.findViewById(R.id.departureDateSelection);

        oneWay = view.findViewById(R.id.oneWay);
        returnTripDateContainer = view.findViewById(R.id.returnDateContainer);
        roundTrip = view.findViewById(R.id.returnway);
        arrivalCity = view.findViewById(R.id.arrivalCityList);

        travellersNumberText = view.findViewById(R.id.travellerCount);
        returnTitle = view.findViewById(R.id.textAddReturn);
        departureDate = view.findViewById(R.id.DepartureDate);
        returnDate = view.findViewById(R.id.returnDate);
        returnTripDateContainer = view.findViewById(R.id.returnDateContainer);
        roundTrip = view.findViewById(R.id.returnway);
        returnTripDateContainer = view.findViewById(R.id.returnDateContainer);
        classLevel = view.findViewById(R.id.classLevel);

        oneWay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    oneWayTicket();
                }
            }
        });
        roundTrip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    roundTrip();
                }
            }
        });

        //    DEPARTURE CITY ----- SELECT
        firstCityCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = View.inflate(getContext(), R.layout.arrival_city, null);
                showDialog(v);
            }
        });

        //    ARRIVAL CITY ----- SELECT
        secondCityCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = View.inflate(getContext(), R.layout.departure_city, null);
                showDialog(v);
            }
        });

        //    TRAVELLERS AND CLASS ----- SELECT
        TC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = View.inflate(getContext(), R.layout.travellers_and_class_layout, null);
                showDialogTC(v);
            }
        });


        // SWAP CITIES IMAGE

        swapImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String acCode = firstCityCode.getText().toString();
                String dcCode = secondCityCode.getText().toString();
                String ac = firstCityName.getText().toString();
                String dc = secondCityCodeName.getText().toString();

                firstCityCode.setText(dcCode);
                firstCityName.setText(dc);

                secondCityCode.setText(acCode);
                secondCityCodeName.setText(ac);
            }
        });


        LocalDate arrivalDateUpdating, departureDateUpdating;

        // DEPARTURE DATE ------ SELECTION
        DepartureDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = View.inflate(getContext(), R.layout.calendar, null);

//                calendarDeparture(v, departureDate, departureDay);
            }
        });

        // ARRIVAL DATE ------ SELECTION
        returnTripDateContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = View.inflate(getContext(), R.layout.calendar, null);
//                selectCurrentView[1] = calendarArrival(v, returnDate, returnDay);
            }
        });

        returnTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = View.inflate(getContext(), R.layout.calendar, null);

//                calendarArrival(v, returnDate, returnDay);
                roundTrip();
            }
        });


        // ARRIVAL CITY --------- RECYCLER VIEW

//        cities.add(new city("New Delhi", "India", "Indira Gandhi international Airport", "DEL"));
//        cities.add(new city("Mumbai", "India", "Chhatrapati Shivaji Maharaj International Airport", "BOM"));
//        cities.add(new city("Banglore", "India", "Kempegowda International Airport", "BLR"));
//        cities.add(new city("Kolkata", "India", "Netaji Subhash Chandra Bose international Airport", "DEL"));
//        cities.add(new city("New Delhi", "India", "Indira Gandhi international Airport", "DEL"));
//        cities.add(new city("Mumbai", "India", "Chhatrapati Shivaji Maharaj International Airport", "BOM"));
//        cities.add(new city("Banglore", "India", "Kempegowda International Airport", "BLR"));
//        cities.add(new city("Kolkata", "India", "Netaji Subhash Chandra Bose international Airport", "DEL"));
//        cities.add(new city("New Delhi", "India", "Indira Gandhi international Airport", "DEL"));
//        cities.add(new city("Mumbai", "India", "Chhatrapati Shivaji Maharaj International Airport", "BOM"));
//        cities.add(new city("Banglore", "India", "Kempegowda International Airport", "BLR"));
//        cities.add(new city("Kolkata", "India", "Netaji Subhash Chandra Bose international Airport", "DEL"));


        // SEARCH FLIGHT
        searchFlightBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (COUNT.size() == 0) {
                    COUNT.add(0, "1");
                }
//                Intent intent = new Intent(getApplicationContext(), TravellersDetails.class);
//                intent.putStringArrayListExtra("TC", COUNT);
//                toast(""+COUNT.size());
//                startActivity(intent);
            }
        });
        return view;
    }

    private void calendarDeparture(View v, TextView Date, TextView Day) {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(v);

        ImageView close = dialog.findViewById(R.id.closeDialog);
        Button doneCalendar = dialog.findViewById(R.id.doneCalendar);
        LinearLayout btnContainer = dialog.findViewById(R.id.buttonContainer);
        View view = dialog.findViewById(R.id.viewDIV1);
        TextView title = dialog.findViewById(R.id.cityTitle);

        title.setText("Select Departure city");
        btnContainer.setVisibility(View.GONE);
        view.setVisibility(View.GONE);

        LocalDate selectedDate;
        CalendarView calendarView;

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        String[] monthName = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};

        calendarView = dialog.findViewById(R.id.calenderView);
        selectedDate = LocalDate.now();

        YearMonth currentMonth = YearMonth.from(LocalDate.now());
        YearMonth startMonth = currentMonth.minusMonths(0);
        YearMonth endMonth = currentMonth.plusMonths(12);

        Log.e("CURR-MONTH", "" + currentMonth);
        Log.e("START-MONTH", "" + startMonth);
        Log.e("END-MONTH", "" + endMonth);

        calendarView.setup(startMonth, endMonth, getFirstDayOfWeekFromLocal());
        calendarView.scrollToMonth(currentMonth);

        calendarView.setOrientation(RecyclerView.VERTICAL);
        calendarView.setMonthHeaderResource(R.layout.title_calendar);

        final String[] weekDayOfMonth = new String[1];
        final String[] DateOfMonth = new String[1];

        calendarView.setDayBinder(new MonthDayBinder<DayViewContainer1>() {
            DayViewContainer1 selectedToday;

            @NonNull
            @Override
            public DayViewContainer1 create(@NonNull View view) {
                return new DayViewContainer1(view);
            }

            @Override
            public void bind(@NonNull DayViewContainer1 container, CalendarDay calendarDay) {

                container.textView.setText(String.valueOf(calendarDay.getDate().getDayOfMonth()));

                log(String.valueOf(selectedDate));

                if (calendarDay.getPosition() == DayPosition.MonthDate) {
                    container.textView.setVisibility(View.VISIBLE);
                    if (calendarDay.getDate().isEqual(selectedDate)) {
                        selectedToday = container;
                        DateEnable(selectedToday);
                    } else if (calendarDay.getDate().isBefore(selectedDate)) {
                        container.textView.setTextColor(Color.LTGRAY);
                        container.textView.setEnabled(false);
                    } else {
                        container.textView.setTextColor(Color.BLACK);
                        container.textView.setBackground(null);
                    }
                } else {
                    container.textView.setVisibility(View.INVISIBLE);
                }


                container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedToday.textView.setTextColor(Color.BLACK);
                        selectedToday.textView.setBackground(null);

                        int date = Integer.parseInt(calendarDay.getDate().format(DateTimeFormatter.ofPattern("dd")));
                        int month = Integer.parseInt(calendarDay.getDate().format(DateTimeFormatter.ofPattern("MM")));
                        int year = Integer.parseInt(calendarDay.getDate().format(DateTimeFormatter.ofPattern("yy")));
                        weekDayOfMonth[0] = calendarDay.getDate().format(DateTimeFormatter.ofPattern("E"));
                        DateOfMonth[0] = ", " + date + " " + monthName[month - 1] + " '" + year;

                        selectedToday = container;
                        selectedToday.textView.setBackgroundResource(R.drawable.background_test);
                        selectedToday.textView.setTextColor(Color.WHITE);
                    }
                });
//                DateEnable(selectCurrentView);
            }
        });

        calendarView.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthViewContainer>() {
            @NonNull
            @Override
            public MonthViewContainer create(@NonNull View view) {
                return new MonthViewContainer(view);
            }

            @Override
            public void bind(@NonNull MonthViewContainer container, CalendarMonth calendarMonth) {
                int month = Integer.parseInt(calendarMonth.getYearMonth().format(DateTimeFormatter.ofPattern("MM")));
                String year = calendarMonth.getYearMonth().format(DateTimeFormatter.ofPattern("yyyy"));
                String[] monthName = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

                container.titlesMonthContainer.setText(monthName[(month - 1) % 12] + " " + year);

            }
        });

        doneCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toast("DONE");
                Day.setText(weekDayOfMonth[0]);
                Date.setText(DateOfMonth[0]);
                dialog.cancel();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogC(dialog);
            }
        });

    }

    private void DateEnable(DayViewContainer1 container) {
        container.textView.setBackgroundResource(R.drawable.background_test);
        container.textView.setTextColor(Color.WHITE);
    }

    private DayViewContainer1 calendarArrival(View v, TextView Date, TextView Day) {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(v);

        ImageView close = dialog.findViewById(R.id.closeDialog);
        Button doneCalendar = dialog.findViewById(R.id.doneCalendar);
        LinearLayout btnContainer = dialog.findViewById(R.id.buttonContainer);
        View view = dialog.findViewById(R.id.viewDIV1);
        Button arrivalBTN, departureBTN;
        TextView title = dialog.findViewById(R.id.cityTitle);

        title.setText("Select Arrival city");
        btnContainer.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
        title.setPadding(0, 0, 0, 20);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


        LocalDate selectedDate;
        CalendarView calendarView;

        String[] week = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        String[] monthName = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};

        calendarView = dialog.findViewById(R.id.calenderView);
        selectedDate = LocalDate.now();


        YearMonth currentMonth = YearMonth.from(LocalDate.now());
        YearMonth startMonth = currentMonth.minusMonths(0);
        YearMonth endMonth = currentMonth.plusMonths(12);


        Log.e("CURR-MONTH", "" + currentMonth);
        Log.e("START-MONTH", "" + startMonth);
        Log.e("END-MONTH", "" + endMonth);

        calendarView.setup(startMonth, endMonth, getFirstDayOfWeekFromLocal());
        calendarView.scrollToMonth(currentMonth);

        calendarView.setOrientation(RecyclerView.VERTICAL);
        calendarView.setMonthHeaderResource(R.layout.title_calendar);

        final String[] weekDayOfMonth = new String[1];
        final String[] DateOfMonth = new String[1];
        final DayViewContainer1[] selectCurrentView = new DayViewContainer1[2];

        calendarView.setDayBinder(new MonthDayBinder<DayViewContainer1>() {

            @NonNull
            @Override
            public DayViewContainer1 create(@NonNull View view) {
                return new DayViewContainer1(view);
            }

            @Override
            public void bind(@NonNull DayViewContainer1 container, CalendarDay calendarDay) {

                container.textView.setText(String.valueOf(calendarDay.getDate().getDayOfMonth()));

                log(String.valueOf(selectedDate));

//                if(calendarDay.getPosition() == DayPosition.MonthDate){
//                    container.textView.setVisibility(View.VISIBLE);
//                    if(calendarDay.getDate().isEqual(selectedDate)){
//                        selectCurrentView[0] = container;
//                        selectCurrentView[1] = container;
//                        DateEnable(selectCurrentView[0]);
//                    } else if(calendarDay.getDate().isBefore(selectedDate)){
//                        container.textView.setTextColor(Color.LTGRAY);
//                        container.textView.setEnabled(false);
//                    }
//                    else{
//                        container.textView.setTextColor(Color.BLACK);
//                        container.textView.setBackground(null);
//                    }
//                } else{
//                    container.textView.setVisibility(View.INVISIBLE);
//                }
                if (calendarDay.getPosition() == DayPosition.MonthDate) {
                    container.textView.setVisibility(View.VISIBLE);
                    if (calendarDay.getDate().isEqual(selectedDate) && selectCurrentView[1] == null) {
                        selectCurrentView[0] = container;
                        selectCurrentView[1] = container;
                        DateEnable(selectCurrentView[0]);
                    } else if (calendarDay.getDate().isBefore(selectedDate)) {
                        container.textView.setTextColor(Color.LTGRAY);
                        container.textView.setEnabled(false);
                    } else {
                        DateEnable(selectCurrentView[1]);
                        container.textView.setTextColor(Color.BLACK);
                        container.textView.setBackground(null);
                    }
                } else {
                    container.textView.setVisibility(View.INVISIBLE);
                }

                container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectCurrentView[1].textView.setTextColor(Color.BLACK);
                        selectCurrentView[1].textView.setBackground(null);
                        if (selectCurrentView[0] != null) {
                            DateEnable(selectCurrentView[0]);
                        }

                        Log.e("NON_TOUCHED", selectCurrentView[0].textView.getText() + "");
                        Log.e("DISELECTED_DATE", selectCurrentView[1].textView.getText() + "");


                        int date = Integer.parseInt(calendarDay.getDate().format(DateTimeFormatter.ofPattern("dd")));
                        int month = Integer.parseInt(calendarDay.getDate().format(DateTimeFormatter.ofPattern("MM")));
                        int year = Integer.parseInt(calendarDay.getDate().format(DateTimeFormatter.ofPattern("yy")));
                        weekDayOfMonth[0] = calendarDay.getDate().format(DateTimeFormatter.ofPattern("E"));
                        DateOfMonth[0] = ", " + date + " " + monthName[month - 1] + " '" + year;

                        selectCurrentView[1] = container;
                        selectCurrentView[1].textView.setBackgroundResource(R.drawable.background_test);
                        selectCurrentView[1].textView.setTextColor(Color.WHITE);
                        Log.e("SELECTED_DATE", selectCurrentView[1].textView.getText() + "");
                    }
                });

//                DateEnable(selectCurrentView);
            }
        });
//        log(selectCurrentView[0]+"");

        calendarView.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthViewContainer>() {
            @NonNull
            @Override
            public MonthViewContainer create(@NonNull View view) {
                return new MonthViewContainer(view);
            }

            @Override
            public void bind(@NonNull MonthViewContainer container, CalendarMonth calendarMonth) {
                int month = Integer.parseInt(calendarMonth.getYearMonth().format(DateTimeFormatter.ofPattern("MM")));
                String year = calendarMonth.getYearMonth().format(DateTimeFormatter.ofPattern("yyyy"));
                String[] monthName = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

                container.titlesMonthContainer.setText(monthName[(month - 1) % 12] + " " + year);

            }
        });

        doneCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toast("DONE");
                Day.setText(weekDayOfMonth[0]);
                Date.setText(DateOfMonth[0]);
                dialog.cancel();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogC(dialog);
            }
        });

        return selectCurrentView[1];
    }

    private LocalDate calenderCreate(CalendarView calendarView, LocalDate selectedDate) {
        YearMonth currentMonth = YearMonth.from(LocalDate.now());
        YearMonth startMonth = currentMonth.minusMonths(0);
        YearMonth endMonth = currentMonth.plusMonths(12);

        String[] monthName = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};


        Log.e("CURR-MONTH", "" + currentMonth);
        Log.e("START-MONTH", "" + startMonth);
        Log.e("END-MONTH", "" + endMonth);

        calendarView.setup(startMonth, endMonth, getFirstDayOfWeekFromLocal());
        calendarView.scrollToMonth(currentMonth);

        calendarView.setOrientation(RecyclerView.VERTICAL);
        calendarView.setMonthHeaderResource(R.layout.title_calendar);

        final String[] weekDayOfMonth = new String[1];
        final String[] DateOfMonth = new String[1];

        calendarView.setDayBinder(new MonthDayBinder<DayViewContainer1>() {
            DayViewContainer1 selectedToday;

            @NonNull
            @Override
            public DayViewContainer1 create(@NonNull View view) {
                return new DayViewContainer1(view);
            }

            @Override
            public void bind(@NonNull DayViewContainer1 container, CalendarDay calendarDay) {

                container.textView.setText(String.valueOf(calendarDay.getDate().getDayOfMonth()));

                log(String.valueOf(selectedDate));

                if (calendarDay.getPosition() == DayPosition.MonthDate) {
                    container.textView.setVisibility(View.VISIBLE);
                    if (calendarDay.getDate().isEqual(selectedDate)) {
                        selectedToday = container;
                        DateEnable(selectedToday);
                    } else if (calendarDay.getDate().isBefore(selectedDate)) {
                        container.textView.setTextColor(Color.LTGRAY);
                        container.textView.setEnabled(false);
                    } else {
                        container.textView.setTextColor(Color.BLACK);
                        container.textView.setBackground(null);
                    }
                } else {
                    container.textView.setVisibility(View.INVISIBLE);
                }


                container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedToday.textView.setTextColor(Color.BLACK);
                        selectedToday.textView.setBackground(null);

                        int date = Integer.parseInt(calendarDay.getDate().format(DateTimeFormatter.ofPattern("dd")));
                        int month = Integer.parseInt(calendarDay.getDate().format(DateTimeFormatter.ofPattern("MM")));
                        int year = Integer.parseInt(calendarDay.getDate().format(DateTimeFormatter.ofPattern("yy")));
                        weekDayOfMonth[0] = calendarDay.getDate().format(DateTimeFormatter.ofPattern("E"));
                        DateOfMonth[0] = ", " + date + " " + monthName[month - 1] + " '" + year;

                        selectedToday = container;
                        selectedToday.textView.setBackgroundResource(R.drawable.background_test);
                        selectedToday.textView.setTextColor(Color.WHITE);
                        Toast.makeText(getContext(), "" + container, Toast.LENGTH_SHORT).show();

                    }
                });
//                toast(""+container);
//                DateEnable(selectCurrentView);
            }
        });

        calendarView.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthViewContainer>() {
            @NonNull
            @Override
            public MonthViewContainer create(@NonNull View view) {
                return new MonthViewContainer(view);
            }

            @Override
            public void bind(@NonNull MonthViewContainer container, CalendarMonth calendarMonth) {
                int month = Integer.parseInt(calendarMonth.getYearMonth().format(DateTimeFormatter.ofPattern("MM")));
                String year = calendarMonth.getYearMonth().format(DateTimeFormatter.ofPattern("yyyy"));
                String[] monthName = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

                container.titlesMonthContainer.setText(monthName[(month - 1) % 12] + " " + year);

            }
        });

        return selectedDate;
    }

    private void oneWayTicket() {
        returnTripDateContainer.setVisibility(View.GONE);
        returnTitle.setVisibility(View.VISIBLE);
    }

    private void roundTrip() {

        roundTrip.setChecked(true);
        returnTripDateContainer.setVisibility(View.VISIBLE);
        returnTitle.setVisibility(View.GONE);
    }

    private void showDialog(View v) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(v);

        TextView arrivalTitle = dialog.findViewById(R.id.arrivalCityTitle);
        ImageView closeDialog = dialog.findViewById(R.id.closeDialog);
        RecyclerView arrivalCity = dialog.findViewById(R.id.arrivalCityList);

        arrivalTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Arrival cities", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        arrivalCity.setLayoutManager(new LinearLayoutManager(arrivalCity.getContext()));
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
        List<AirportCode> exerciseStored = SharedPreference.loadList(getContext(), "NameList", "Names");
        exerciseStored = (exerciseStored != null && exerciseStored.size() > 0) ? exerciseStored : new ArrayList<>();
        ArrivalCityAdapter adapter = new ArrivalCityAdapter(getContext(), exerciseStored);
        arrivalCity.setAdapter(adapter);


        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }

    private void showDialogTC(View v) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(v);

        ImageView closeDialog = dialog.findViewById(R.id.closeDialog);
        Button done = dialog.findViewById(R.id.DoneTC);

        Button AdultAdd, CAdd, IAdd, AdultReduce, CReduce, IReduce;
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
        RadioButton eco, priEco, Business;
        eco = dialog.findViewById(R.id.economyClass);
        priEco = dialog.findViewById(R.id.premiumEconomy);
        Business = dialog.findViewById(R.id.business);

        RadioGroup classT = dialog.findViewById(R.id.ClassRadioG);
        eco.setChecked(true);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


        final int[] adult = {Integer.parseInt(AdultCount.getText().toString())};
        final int[] children = {Integer.parseInt(ChildrenCount.getText().toString())};
        final int[] infant = {Integer.parseInt(InfantCount.getText().toString())};


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
                COUNT.add(AdultCount.getText().toString());
                COUNT.add(ChildrenCount.getText().toString());
                COUNT.add(InfantCount.getText().toString());
//                travellersDetailsLists.add(new TravellersDetailsList(AdultsCounts, childrenCounts, infantsCounts));
//                AdultsCounts.add(Integer.parseInt(AdultCount.getText().toString()));
//                childrenCounts.add((Integer.parseInt(ChildrenCount.getText().toString())));
//                infantsCounts.add((Integer.parseInt(InfantCount.getText().toString())));
                int travellers = (Integer.parseInt(AdultCount.getText().toString())) + (Integer.parseInt(ChildrenCount.getText().toString())) + (Integer.parseInt(InfantCount.getText().toString()));
                travellers(travellers);
                selectedClass(eco, priEco, Business);
                dialogC(dialog);
            }
        });
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogC(dialog);
            }
        });
    }

    private void selectedClass(RadioButton eco, RadioButton priEco, RadioButton business) {
        if (eco.isChecked()) {
            classLevel.setText(" Economy");
        } else if (priEco.isChecked()) {
            classLevel.setText(" Premium Economy");
        } else if (business.isChecked()) {
            classLevel.setText(" Business");
        } else {
            classLevel.setText("");
            Toast.makeText(getContext(), "Select Class", Toast.LENGTH_SHORT).show();
        }
    }

    private void travellers(int numberOfTravellers) {
        travellersNumberText.setText(numberOfTravellers + " Travellers");
    }

    private void dialogC(@NonNull Dialog dialog) {
        dialog.cancel();
    }

    private void toast(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }


    /////////////<<<<<<<<<<<<<<-------- FOR CALENDAR ---------------------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    private void log(String s) {
        Log.e("TAG-AAKANSHA", s);
    }

    private DayOfWeek getFirstDayOfWeekFromLocal() {
        return DayOfWeek.SUNDAY;
    }
//    private DayOfWeek[] daysOfWeek = new DayOfWeek[]{
//            DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
//            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
//    };

    static class MonthViewContainer extends ViewContainer {
        TextView titlesMonthContainer;

        public MonthViewContainer(View view) {
            super(view);
            titlesMonthContainer = view.findViewById(R.id.titleMonth);
        }
    }

}