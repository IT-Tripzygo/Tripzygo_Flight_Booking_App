package in.tripzygo.tripzygoflightbookingapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.tripzygo.tripzygoflightbookingapp.Modals.Booking;
import in.tripzygo.tripzygoflightbookingapp.Modals.FlightDetails;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.ApiInterface;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.Retrofitt;
import in.tripzygo.tripzygoflightbookingapp.Tools.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisplayBookingActivity extends AppCompatActivity {
    TextView DepartureCityText, ArrivalCityText, classText, stopText, timeText, ChangeText, priceFLightPayment, returnDepartureCityText, returnArrivalCityText, returnClassText, returnStopText, FareType, BaseFare, Taxes, ConvenienceFees, FareTypeText,
            NetPayable, returnTimeText;
    RecyclerView flightRecyclerView, returnFlightRecyclerView, passengerRecyclerBooking, returnPassengerRecyclerBooking;
    Booking booking;
    String bookingId;
    boolean isCompleted = false;
    MaterialToolbar materialToolbar;
    RelativeLayout eTicketRelativeLayout, invoiceRelativeLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_booking);
        eTicketRelativeLayout = findViewById(R.id.eTicketBtn);
        invoiceRelativeLayout = findViewById(R.id.invoiceBtn);
        DepartureCityText = findViewById(R.id.DepartureCityText);
        ArrivalCityText = findViewById(R.id.ArrivalCityText);
        classText = findViewById(R.id.classText);
        stopText = findViewById(R.id.stopText);
        timeText = findViewById(R.id.timeText);
        FareType = findViewById(R.id.FareType);
        FareTypeText = findViewById(R.id.FareTypeText);
        BaseFare = findViewById(R.id.BaseFare);
        Taxes = findViewById(R.id.Taxes);
        NetPayable = findViewById(R.id.NetPayable);
        materialToolbar = findViewById(R.id.toolbarDisplay);
        materialToolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
        });
        String book = getIntent().getStringExtra("booking");
        Gson gson = new Gson();
        System.out.println("book = " + book);
        booking = gson.fromJson(book, new TypeToken<Booking>() {
        }.getType());
        bookingId = booking.getBookingId();
        flightRecyclerView = findViewById(R.id.flightDetailRecycler);
        flightRecyclerView.setLayoutManager(new LinearLayoutManager(DisplayBookingActivity.this));
        String tripInfo = booking.getTripInfos();
        String depDate = booking.getDeparture();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
        SimpleDateFormat df1 = new SimpleDateFormat("E, dd MMM", Locale.getDefault());
        try {
            Date dof = df.parse(depDate);
            Date now = Calendar.getInstance().getTime();
            if (dof.before(now)) {
                isCompleted = true;
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        JsonArray jsonArray = gson.fromJson(tripInfo, new TypeToken<JsonArray>() {
        }.getType());

        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        JsonArray sI = jsonObject.getAsJsonArray("sI");
        JsonArray totalPriceList = jsonObject.getAsJsonArray("totalPriceList");
        FlightDetails flightDetails = new FlightDetails();
        flightDetails.setAirlineImage(jsonObject.getAsJsonArray("sI").get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("code").getAsString());
        flightDetails.setSI(String.valueOf(jsonObject.getAsJsonArray("sI")));
        flightDetails.setTotalPriceList(String.valueOf(jsonObject.getAsJsonArray("totalPriceList")));
        ApiInterface apiInterface = Retrofitt.initretro().create(ApiInterface.class);
        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("bookingId", bookingId);
        Call<JsonObject> call = apiInterface.retrieveBooking(jsonObject1);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject2 = response.body();
                    JsonObject totalFareDetail = jsonObject2.getAsJsonObject("itemInfos").getAsJsonObject("AIR").getAsJsonObject("totalPriceInfo").getAsJsonObject("totalFareDetail");
                    Locale locale = new Locale.Builder().setLanguage("en").setRegion("IN").build();
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
                    String basecurrency = formatter.format(totalFareDetail.getAsJsonObject("fC").get("BF").getAsInt());
                    String taxcurrency = formatter.format(totalFareDetail.getAsJsonObject("fC").get("TAF").getAsInt());
                    String totalcurrency = formatter.format(totalFareDetail.getAsJsonObject("fC").get("TF").getAsInt());
                    int basecentsIndex = basecurrency.lastIndexOf(".00");
                    int taxcentsIndex = taxcurrency.lastIndexOf(".00");
                    int totalcentsIndex = totalcurrency.lastIndexOf(".00");
                    if (taxcentsIndex != -1) {
                        taxcurrency = taxcurrency.substring(0, taxcentsIndex);
                    }
                    if (totalcentsIndex != -1) {
                        totalcurrency = totalcurrency.substring(0, totalcentsIndex);
                    }
                    if (basecentsIndex != -1) {
                        basecurrency = basecurrency.substring(0, basecentsIndex);
                    }
                    BaseFare.setText(basecurrency);
                    Taxes.setText(taxcurrency);
                    NetPayable.setText(totalcurrency);
                    JsonArray travellerInfos = jsonObject2.getAsJsonObject("itemInfos").getAsJsonObject("AIR").getAsJsonArray("travellerInfos");
                    BookedFlightDetailsAdapter flightDetailsAdapter = new BookedFlightDetailsAdapter(flightDetails, DisplayBookingActivity.this, jsonObject.getAsJsonArray("sI").size(), isCompleted, travellerInfos);
                    flightRecyclerView.setAdapter(flightDetailsAdapter);
                    DepartureCityText.setText(sI.get(0).getAsJsonObject().getAsJsonObject("da").get("city").getAsString());
                    eTicketRelativeLayout.setOnClickListener(view -> {
                        JsonObject item = jsonObject2.getAsJsonObject("itemInfos");
                        startActivity(new Intent(DisplayBookingActivity.this, EticketGenerator.class).putExtra("data", item.toString()).putExtra("bookingId", bookingId));
                    });
                    invoiceRelativeLayout.setOnClickListener(view -> {
                        JsonObject item = jsonObject2.getAsJsonObject("itemInfos");
                        startActivity(new Intent(DisplayBookingActivity.this, InvoiceActivity.class).putExtra("data", item.toString()).putExtra("bookingId", bookingId));
                    });
                    if (sI.size() == 1) {
                        stopText.setText("non stop");
                        ArrivalCityText.setText(sI.get(0).getAsJsonObject().getAsJsonObject("aa").get("city").getAsString());
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                        SimpleDateFormat df1 = new SimpleDateFormat("E, dd MMM", Locale.getDefault());
                        String departure = sI.get(0).getAsJsonObject().get("dt").getAsString();
                        String arrival = sI.get(0).getAsJsonObject().get("at").getAsString();
                        try {
                            Date dof = df.parse(departure);
                            Date aol = df.parse(arrival);
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

                            System.out.println(Hours_difference + " h " + Minutes_difference + " m");

                            System.out.println("Seconds_difference = " + Seconds_difference);
                            timeText.setText(Hours_difference + "h " + Minutes_difference + "m");
                            String dTime = df1.format(dof);
                            String aTime = df1.format(aol);
                            System.out.println("aTime = " + aTime + " dTime = " + dTime);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        stopText.setText(sI.size() - 1 + " stop");
                        ArrivalCityText.setText(sI.get(sI.size() - 1).getAsJsonObject().getAsJsonObject("aa").get("city").getAsString());
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                        SimpleDateFormat df1 = new SimpleDateFormat("E, dd MMM", Locale.getDefault());
                        String departureOfFirst = sI.get(0).getAsJsonObject().get("dt").getAsString();
                        String arrivalOfLast = sI.get(sI.size() - 1).getAsJsonObject().get("at").getAsString();
                        try {
                            Date dof = df.parse(departureOfFirst);
                            Date aol = df.parse(arrivalOfLast);
                            String dTime = df1.format(dof);
                            String aTime = df1.format(aol);
                            System.out.println("aTime = " + aTime + " dTime = " + dTime);
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
                            timeText.setText(Hours_difference + "h " + Minutes_difference + "m");
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    List<JsonObject> jsonObjects = new ArrayList<>();
                    for (JsonElement jsonElement : totalPriceList) {
                        JsonObject jsonObject3 = jsonElement.getAsJsonObject();
                        if (!jsonObject3.get("fareIdentifier").getAsString().matches("SPECIAL_RETURN")) {
                            jsonObjects.add(jsonObject3);
                        }
                    }
                    jsonObjects.sort(new Sort());
                    if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").has("rT")) {
                        if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 0) {
                            FareType.setText("Non Refundable");
                        } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 1) {
                            FareType.setText("Refundable");
                        } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 2) {
                            FareType.setText("Partial Refundable");
                        }
                    } else {
                        FareType.setVisibility(View.GONE);
                        FareTypeText.setVisibility(View.GONE);
                    }
                    System.out.println("gson.toJson(jsonObjects) = " + gson.toJson(jsonObjects));
                    classText.setText(jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString());
                    if (jsonArray.size() > 1) {
                        returnFlightRecyclerView = findViewById(R.id.returnflightDetailRecycler);
                        returnFlightRecyclerView.setLayoutManager(new LinearLayoutManager(DisplayBookingActivity.this));
                        returnDepartureCityText = findViewById(R.id.returnDepartureCityText);
                        returnArrivalCityText = findViewById(R.id.returnArrivalCityText);
                        returnClassText = findViewById(R.id.returnclassText);
                        returnStopText = findViewById(R.id.returnstopText);
                        returnTimeText = findViewById(R.id.returntimeText);
                        JsonObject returnJsonObject = jsonArray.get(1).getAsJsonObject();
                        JsonArray returnsI = returnJsonObject.getAsJsonArray("sI");
                        JsonArray returntotalPriceList = returnJsonObject.getAsJsonArray("totalPriceList");
                        System.out.println("gson.toJson(returnJsonObject) = " + gson.toJson(returnJsonObject));
                        System.out.println("gson.toJson(jsonArray) = " + gson.toJson(jsonArray));
                        returnDepartureCityText.setText(returnsI.get(0).getAsJsonObject().getAsJsonObject("da").get("city").getAsString());
                        if (returnsI.size() == 1) {
                            returnStopText.setText("non-stop");
                            returnArrivalCityText.setText(returnsI.get(0).getAsJsonObject().getAsJsonObject("aa").get("city").getAsString());
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                            SimpleDateFormat df1 = new SimpleDateFormat("E, dd MMM", Locale.getDefault());
                            String departure = returnsI.get(0).getAsJsonObject().get("dt").getAsString();
                            String arrival = returnsI.get(0).getAsJsonObject().get("at").getAsString();
                            try {
                                Date dof = df.parse(departure);
                                Date aol = df.parse(arrival);
                                long d = dof.getTime();
                                long a = aol.getTime();
                                long t = a - d;
                                long Seconds_difference = (t / (1000 * 60));

                                long Minutes_difference = (t / (1000 * 60)) % 60;

                                long Hours_difference = (t / (1000 * 60 * 60)) % 24;

                                System.out.println(Hours_difference + " h " + Minutes_difference + " m");

                                System.out.println("Seconds_difference = " + Seconds_difference);
                                returnTimeText.setText(Hours_difference + "h " + Minutes_difference + "m");
                                String dTime = df1.format(dof);
                                String aTime = df1.format(aol);
                                System.out.println("aTime = " + aTime + " dTime = " + dTime);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            returnStopText.setText(returnsI.size() - 1 + " stop");
                            returnArrivalCityText.setText(returnsI.get(returnsI.size() - 1).getAsJsonObject().getAsJsonObject("aa").get("city").getAsString());
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                            SimpleDateFormat df1 = new SimpleDateFormat("E, dd MMM", Locale.getDefault());
                            String departureOfFirst = returnsI.get(0).getAsJsonObject().get("dt").getAsString();
                            String arrivalOfLast = returnsI.get(returnsI.size() - 1).getAsJsonObject().get("at").getAsString();
                            try {
                                Date dof = df.parse(departureOfFirst);
                                Date aol = df.parse(arrivalOfLast);
                                String dTime = df1.format(dof);
                                String aTime = df1.format(aol);
                                System.out.println("aTime = " + aTime + " dTime = " + dTime);
                                long d = dof.getTime();
                                long a = aol.getTime();
                                long t = a - d;
                                long Seconds_difference = (t / (1000 * 60));

                                long Minutes_difference = (t / (1000 * 60)) % 60;

                                long Hours_difference = (t / (1000 * 60 * 60)) % 24;

                                System.out.println("departureOfFirst = " + departureOfFirst);
                                System.out.println("arrivalOfLast = " + arrivalOfLast);
                                System.out.println(Hours_difference + " h " + Minutes_difference + " m");

                                System.out.println("Seconds_difference = " + Seconds_difference);
                                returnTimeText.setText(Hours_difference + "h " + Minutes_difference + "m");
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        List<JsonObject> returnjsonObjects = new ArrayList<>();
                        for (JsonElement jsonElement : returntotalPriceList) {
                            JsonObject jsonObject3 = jsonElement.getAsJsonObject();
                            if (!jsonObject3.get("fareIdentifier").getAsString().matches("SPECIAL_RETURN")) {
                                returnjsonObjects.add(jsonObject3);
                            }
                        }
                        returnjsonObjects.sort(new Sort());
                        returnClassText.setText(returnjsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString());
                        FlightDetails returnFlightDetails = new FlightDetails();
                        returnFlightDetails.setSI(String.valueOf(returnJsonObject.getAsJsonArray("sI")));
                        returnFlightDetails.setTotalPriceList(String.valueOf(returnJsonObject.getAsJsonArray("totalPriceList")));
                        BookedFlightDetailsAdapter bookedFlightDetailsAdapter = new BookedFlightDetailsAdapter(returnFlightDetails, DisplayBookingActivity.this, returnJsonObject.getAsJsonArray("sI").size(), isCompleted, travellerInfos);
                        returnFlightRecyclerView.setAdapter(bookedFlightDetailsAdapter);
                        if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").has("rT") || returnjsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").has("rT")) {
                            if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 0 || returnjsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 0) {
                                FareType.setText("Non Refundable");
                            } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 1 || returnjsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 1) {
                                FareType.setText("Refundable");
                            } else {
                                FareType.setText("Partial Refundable");
                            }
                        } else {
                            FareType.setVisibility(View.GONE);
                            FareTypeText.setVisibility(View.GONE);
                        }
                    } else {
                        RelativeLayout relativeLayout = findViewById(R.id.returnFLightLayoutBookingDisplay);
                        relativeLayout.setVisibility(View.GONE);
                    }

                } else {
                    System.out.println("response = " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println("t.getMessage() = " + t.getMessage());
                System.out.println(t.getCause());
                t.printStackTrace();
            }
        });
    }
}