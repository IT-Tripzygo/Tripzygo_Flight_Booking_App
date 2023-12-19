package in.tripzygo.tripzygoflightbookingapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.tripzygo.tripzygoflightbookingapp.Modals.FlightDetails;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.ApiInterface;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.Retrofitt;
import in.tripzygo.tripzygoflightbookingapp.Tools.Sort;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReturnFlightCheckoutActivity extends AppCompatActivity {
    FlightDetails flightDetails, returnFlightDetails;
    RecyclerView flightRecyclerView, returnFlightRecyclerView;
    TextView DepartureCityText, ArrivalCityText, classText, stopText, timeText, FareType, BaseFare, Taxes, ConvenienceFees, FareTypeText,
            NetPayable, price_flight, ConvenienceFeesText, noOfPassenger, returnDepartureCityText, returnArrivalCityText, returnClassText, returnStopText, returnTimeText;
    Button bookNow;
    String id, id2, bookingId, segmentId;
    List<String> stringList = new ArrayList<>();
    int finalAmount;
    JsonArray tripInfos;
    RelativeLayout relativeLayout;
    ShimmerFrameLayout shimmerFrameLayout;
    MaterialToolbar materialToolbar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_flight_checkout);
        materialToolbar = findViewById(R.id.toolbar_checkOut);
        Bundle bundle = getIntent().getBundleExtra("bundle");
        String ids = bundle.getString("priceIds");
        String pa = bundle.getString("paxInfo");
        System.out.println(pa);
        flightDetails = (FlightDetails) bundle.get("flightDetails");
        returnFlightDetails = (FlightDetails) bundle.get("returnFlightDetails");
        Gson gson = new Gson();
        System.out.println(gson.toJson(bundle));
        stringList = gson.fromJson(ids, new TypeToken<List<String>>() {
        }.getType());
        JsonObject pax = gson.fromJson(pa, new TypeToken<JsonObject>() {
        }.getType());
        JsonArray sI = gson.fromJson(flightDetails.getSI(), new TypeToken<JsonArray>() {
        }.getType());
        JsonArray totalPriceList = gson.fromJson(flightDetails.getTotalPriceList(), new TypeToken<JsonArray>() {
        }.getType());
        materialToolbar.setNavigationOnClickListener(view -> onBackPressed());
        flightRecyclerView = findViewById(R.id.flightDetailRecycler);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_containerReturnCheckout);
        shimmerFrameLayout.startShimmerAnimation();
        flightRecyclerView.setLayoutManager(new LinearLayoutManager(ReturnFlightCheckoutActivity.this));
        DepartureCityText = findViewById(R.id.DepartureCityText);
        ArrivalCityText = findViewById(R.id.ArrivalCityText);
        classText = findViewById(R.id.classText);
        stopText = findViewById(R.id.stopText);
        timeText = findViewById(R.id.timeText);
        FareTypeText = findViewById(R.id.FareTypeText);
        FareType = findViewById(R.id.FareType);
        BaseFare = findViewById(R.id.BaseFare);
        Taxes = findViewById(R.id.Taxes);
        ConvenienceFees = findViewById(R.id.ConvenienceFees);
        ConvenienceFeesText = findViewById(R.id.ConvenienceFeesText);
        NetPayable = findViewById(R.id.NetPayable);
        price_flight = findViewById(R.id.price_flight);
        bookNow = findViewById(R.id.bookNowButton);
        noOfPassenger = findViewById(R.id.noOfPassenger);
        stopText.setText(flightDetails.getTotalStops());
        FlightDetailsAdapter flightDetailsAdapter = new FlightDetailsAdapter(flightDetails, ReturnFlightCheckoutActivity.this, sI.size());
        flightRecyclerView.setAdapter(flightDetailsAdapter);
        DepartureCityText.setText(sI.get(0).getAsJsonObject().getAsJsonObject("da").get("city").getAsString());
        if (sI.size() == 1) {
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
                long Seconds_difference = (t / (1000 * 60));

                long Minutes_difference = (t / (1000 * 60)) % 60;

                long Hours_difference = (t / (1000 * 60 * 60)) % 24;

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
                long Seconds_difference = (t / (1000 * 60));

                long Minutes_difference = (t / (1000 * 60)) % 60;

                long Hours_difference = (t / (1000 * 60 * 60)) % 24;

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
            JsonObject jsonObject2 = jsonElement.getAsJsonObject();
            if (!jsonObject2.get("fareIdentifier").getAsString().matches("SPECIAL_RETURN")) {
                jsonObjects.add(jsonObject2);
            }
        }
        jsonObjects.sort(new Sort());
        classText.setText(jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString());
        if (returnFlightDetails != null) {
            JsonArray returnsI = gson.fromJson(returnFlightDetails.getSI(), new TypeToken<JsonArray>() {
            }.getType());
            JsonArray returnTotalPriceList = gson.fromJson(returnFlightDetails.getTotalPriceList(), new TypeToken<JsonArray>() {
            }.getType());
            returnFlightRecyclerView = findViewById(R.id.returnflightDetailRecycler);
            relativeLayout = findViewById(R.id.relative_return);
            relativeLayout.setVisibility(View.VISIBLE);
            returnFlightRecyclerView.setLayoutManager(new LinearLayoutManager(ReturnFlightCheckoutActivity.this));
            returnDepartureCityText = findViewById(R.id.returnDepartureCityText);
            returnArrivalCityText = findViewById(R.id.returnArrivalCityText);
            returnClassText = findViewById(R.id.returnclassText);
            returnStopText = findViewById(R.id.returnstopText);
            returnTimeText = findViewById(R.id.returntimeText);
            returnStopText.setText(returnFlightDetails.getTotalStops());
            FlightDetailsAdapter returnFlightDetailsAdapter = new FlightDetailsAdapter(returnFlightDetails, ReturnFlightCheckoutActivity.this, returnsI.size());
            returnFlightRecyclerView.setAdapter(returnFlightDetailsAdapter);
            returnDepartureCityText.setText(returnsI.get(0).getAsJsonObject().getAsJsonObject("da").get("city").getAsString());
            if (returnsI.size() == 1) {
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
            if (returnTotalPriceList != null) {
                for (JsonElement jsonElement : returnTotalPriceList) {
                    JsonObject jsonObject2 = jsonElement.getAsJsonObject();
                    if (!jsonObject2.get("fareIdentifier").getAsString().matches("SPECIAL_RETURN")) {
                        returnjsonObjects.add(jsonObject2);
                    }
                }
                returnjsonObjects.sort(new Sort());
                id2 = returnjsonObjects.get(0).getAsJsonObject().get("id").getAsString();
                returnClassText.setText(returnjsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString());
                if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").has("rT") &&
                        returnjsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").has("rT")) {
                    if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 0 && returnjsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 0) {
                        FareType.setText("Non Refundable");
                    } else if (jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 1 && returnjsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 1) {
                        FareType.setText("Refundable");
                    } else {
                        FareType.setText("Partial Refundable");
                    }
                } else {
                    FareType.setVisibility(View.GONE);
                    FareTypeText.setVisibility(View.GONE);
                }
            }
        }
        ConvenienceFees.setVisibility(View.GONE);
        ConvenienceFeesText.setVisibility(View.GONE);
        id = jsonObjects.get(0).getAsJsonObject().get("id").getAsString();
        String Adult = pax.get("ADULT").getAsString();
        String Children = pax.get("CHILD").getAsString();
        String Infant = pax.get("INFANT").getAsString();
        int adult = Integer.parseInt(Adult);
        int child = Integer.parseInt(Children);
        int infant = Integer.parseInt(Infant);
        int total = adult + child + infant;
        if (total > 1) {
            noOfPassenger.setText(" (For " + total + " Passengers)");
        } else {
            noOfPassenger.setText(" (For " + total + " Passenger)");
        }
        JSONObject jsonObject = new JSONObject();
        JSONArray priceIds = new JSONArray();
        try {
            priceIds.put(id);
            if (returnFlightDetails.getTotalPriceList() != null) {
                priceIds.put(id2);
            }
            jsonObject.put("priceIds", priceIds);
            System.out.println("gson priceIds= " + gson.toJson(priceIds));
            System.out.println("gson priceIds= " + gson.toJson(jsonObject));
            JsonObject jsonObject1 = gson.fromJson(String.valueOf(jsonObject), new TypeToken<JsonObject>() {
            }.getType());
            System.out.println("gson priceIds= " + gson.toJson(jsonObject1));
            ApiInterface apiInterface = Retrofitt.initretro().create(ApiInterface.class);
            Call<JsonObject> call = apiInterface.getReview(jsonObject1);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonObject object = response.body();
                        if (object != null) {
                            bookingId = object.get("bookingId").getAsString();
                            tripInfos = object.getAsJsonArray("tripInfos");
                            segmentId = object.getAsJsonArray("tripInfos").get(0).getAsJsonObject().getAsJsonArray("sI").get(0).getAsJsonObject().get("id").getAsString();
                            System.out.println("bookingId = " + bookingId);
                            JsonObject totalFareDetail = object.getAsJsonObject("totalPriceInfo").getAsJsonObject("totalFareDetail");
                            finalAmount = totalFareDetail.getAsJsonObject("fC").get("TF").getAsInt();
                            String BaseFareString=String.valueOf(totalFareDetail.getAsJsonObject("fC").get("BF").getAsInt());
                            String TaxesString=String.valueOf(totalFareDetail.getAsJsonObject("fC").get("TAF").getAsInt());
                            Locale locale = new Locale.Builder().setLanguage("en").setRegion("IN").build();
                            NumberFormat formatter= NumberFormat.getCurrencyInstance(locale);
                            String basecurrency=formatter.format(totalFareDetail.getAsJsonObject("fC").get("BF").getAsInt());
                            String taxcurrency=formatter.format(totalFareDetail.getAsJsonObject("fC").get("TAF").getAsInt());
                            String totalcurrency=formatter.format(totalFareDetail.getAsJsonObject("fC").get("TF").getAsInt());
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
                            price_flight.setText(totalcurrency);
                            shimmerFrameLayout.stopShimmerAnimation();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            bookNow.setOnClickListener(view -> {
                                System.out.println("gson = " + gson.toJson(returnFlightDetails));
                                Intent intent = new Intent(ReturnFlightCheckoutActivity.this, PaymentActivity.class)
                                        .putExtra("bundle", bundle)
                                        .putExtra("tripInfos", String.valueOf(tripInfos))
                                        .putExtra("sI", String.valueOf(sI))
                                        .putExtra("totalPriceList", String.valueOf(totalPriceList))
                                        .putExtra("bookingId", bookingId)
                                        .putExtra("segmentKey", segmentId)
                                        .putExtra("finalAmount", finalAmount)
                                        .putExtra("BaseFare", BaseFareString)
                                        .putExtra("Taxes", TaxesString)
                                        .putExtra("paxInfo", String.valueOf(pax));
                                if (!(returnFlightDetails == null)) {
                                    intent.putExtra("returnsI", returnFlightDetails.getSI());
                                    intent.putExtra("returntotalPriceList", returnFlightDetails.getTotalPriceList());
                                }
                                startActivity(intent);

                            });
                        }
                    } else {
                        System.out.println("response review = " + response);
                        JsonObject object = response.body();
                        ResponseBody responseBody = response.errorBody();
                        if (responseBody != null) {
                            System.out.println("responseBody = " + gson.toJson(responseBody));
//                            System.out.println("message = " + object.getAsJsonArray("errors").get(0).getAsJsonObject().get("message").getAsString());
//                            System.out.println("details = " + object.getAsJsonArray("errors").get(0).getAsJsonObject().get("details").getAsString());
                        }

                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    System.out.println("t = " + t.getMessage());
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
}