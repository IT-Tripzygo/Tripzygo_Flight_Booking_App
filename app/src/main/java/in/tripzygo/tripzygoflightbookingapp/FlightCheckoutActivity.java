package in.tripzygo.tripzygoflightbookingapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import in.tripzygo.tripzygoflightbookingapp.Fragments.PolicyDialog;
import in.tripzygo.tripzygoflightbookingapp.Modals.FlightDetails;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.ApiInterface;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.Retrofitt;
import in.tripzygo.tripzygoflightbookingapp.Tools.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FlightCheckoutActivity extends AppCompatActivity {
    FlightDetails flightDetails;
    ImageView airlineImage;
    RecyclerView flightRecyclerView;
    TextView DepartureCityText, ArrivalCityText, classText, stopText, timeText, FareType, BaseFare, Taxes, ConvenienceFees,
            NetPayable, price_flight, ConvenienceFeesText, noOfPassenger, FareTypeText, cancellationTextView;
    Button bookNow;
    String id, bookingId, segmentId;
    JsonArray tripInfos;
    int finalAmount;
    MaterialToolbar materialToolbar;
    ShimmerFrameLayout shimmerFrameLayout;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_checkout);
        Bundle bundle = getIntent().getBundleExtra("bundle");
        String pa = bundle.getString("paxInfo");
        id=bundle.getString("id");
        flightDetails = (FlightDetails) bundle.get("flightDetails");
        Gson gson = new Gson();
        JsonObject pax = gson.fromJson(pa, new TypeToken<JsonObject>() {
        }.getType());
        JsonArray sI = gson.fromJson(flightDetails.getSI(), new TypeToken<JsonArray>() {
        }.getType());
        JsonArray totalPriceList = gson.fromJson(flightDetails.getTotalPriceList(), new TypeToken<JsonArray>() {
        }.getType());
        flightRecyclerView = findViewById(R.id.flightDetailRecycler);
        materialToolbar = findViewById(R.id.toolbar_review);
        materialToolbar.setNavigationOnClickListener(view -> {
            super.onBackPressed();
        });
        materialToolbar.setTitle("Flight Review Details");
        materialToolbar.setTitleTextColor(Color.WHITE);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_containerCheckout);
        shimmerFrameLayout.startShimmerAnimation();
        flightRecyclerView.setLayoutManager(new LinearLayoutManager(FlightCheckoutActivity.this));
        DepartureCityText = findViewById(R.id.DepartureCityText);
        ArrivalCityText = findViewById(R.id.ArrivalCityText);
        classText = findViewById(R.id.classText);
        stopText = findViewById(R.id.stopText);
        timeText = findViewById(R.id.timeText);
        FareType = findViewById(R.id.FareType);
        FareTypeText = findViewById(R.id.FareTypeText);
        cancellationTextView = findViewById(R.id.cancellation);
        BaseFare = findViewById(R.id.BaseFare);
        Taxes = findViewById(R.id.Taxes);
        ConvenienceFees = findViewById(R.id.ConvenienceFees);
        ConvenienceFeesText = findViewById(R.id.ConvenienceFeesText);
        NetPayable = findViewById(R.id.NetPayable);
        price_flight = findViewById(R.id.price_flight);
        bookNow = findViewById(R.id.bookNowButton);
        noOfPassenger = findViewById(R.id.noOfPassenger);
        stopText.setText(flightDetails.getTotalStops());
        FlightDetailsAdapter flightDetailsAdapter = new FlightDetailsAdapter(flightDetails, FlightCheckoutActivity.this, sI.size());
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
            JsonObject jsonObject3 = jsonObject2.getAsJsonObject("fd").getAsJsonObject("ADULT").getAsJsonObject("fC");
            jsonObjects.add(jsonObject2);
        }
        jsonObjects.sort(new Sort());
        classText.setText(jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString());
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
        System.out.println("jsonObjects.get(0).getAsJsonObject(\"fd\").getAsJsonObject(\"ADULT\").getAsJsonObject(\"fC\").get(\"BF\").getAsInt() = " + jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").getAsJsonObject("fC").get("BF").getAsInt());
        ConvenienceFees.setVisibility(View.GONE);
        ConvenienceFeesText.setVisibility(View.GONE);
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

//        int price = jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").getAsJsonObject("fC").get("TF").getAsInt();
//        price = price * adult;
//        if (jsonObjects.get(0).getAsJsonObject("fd").has("INFANT") && jsonObjects.get(0).getAsJsonObject("fd").has("CHILD")) {
//            price += jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("CHILD").getAsJsonObject("fC").get("TF").getAsInt();
//            price = price * child;
//            price += jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("INFANT").getAsJsonObject("fC").get("TF").getAsInt();
//            price = price * infant;
//        } else if (jsonObjects.get(0).getAsJsonObject("fd").has("INFANT")) {
//            price += jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("INFANT").getAsJsonObject("fC").get("TF").getAsInt();
//            price = price * infant;
//        } else if (jsonObjects.get(0).getAsJsonObject("fd").has("CHILD")) {
//            price += jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("CHILD").getAsJsonObject("fC").get("TF").getAsInt();
//            price = price * child;
//        }
        JSONObject jsonObject = new JSONObject();
        JSONArray priceIds = new JSONArray();
        try {
            priceIds.put(id);
            jsonObject.put("priceIds", priceIds);
            JsonObject jsonObject1 = gson.fromJson(String.valueOf(jsonObject), new TypeToken<JsonObject>() {
            }.getType());
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
                            String BaseFareString = String.valueOf(totalFareDetail.getAsJsonObject("fC").get("BF").getAsInt());
                            String TaxesString = String.valueOf(totalFareDetail.getAsJsonObject("fC").get("TAF").getAsInt());
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
                            price_flight.setText(totalcurrency);
                            shimmerFrameLayout.stopShimmerAnimation();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            cancellationTextView.setOnClickListener(view -> {
                                String s = sI.get(0).getAsJsonObject().getAsJsonObject("da").get("cityCode").getAsString() + "-" + sI.get(sI.size() - 1).getAsJsonObject().getAsJsonObject("aa").get("cityCode").getAsString();
                                String D=sI.get(0).getAsJsonObject().getAsJsonObject("da").get("city").getAsString();
                                String A= sI.get(sI.size() - 1).getAsJsonObject().getAsJsonObject("aa").get("city").getAsString();
                                PolicyDialog dialog = new PolicyDialog(FlightCheckoutActivity.this, id, s,D,A);
                                dialog.setCancelable(false);
                                dialog.show(getSupportFragmentManager(), "show_description");
                            });
                            bookNow.setOnClickListener(view -> {
                                startActivity(new Intent(FlightCheckoutActivity.this, PaymentActivity.class)
                                        .putExtra("bundle", bundle)
                                        .putExtra("tripInfos", String.valueOf(tripInfos))
                                        .putExtra("sI", String.valueOf(sI))
                                        .putExtra("totalPriceList", String.valueOf(totalPriceList))
                                        .putExtra("bookingId", bookingId)
                                        .putExtra("segmentKey", segmentId)
                                        .putExtra("finalAmount", finalAmount)
                                        .putExtra("BaseFare", BaseFareString)
                                        .putExtra("Taxes", TaxesString)
                                        .putExtra("paxInfo", String.valueOf(pax)));

                            });
                        }
                    } else {
                        System.out.println("response review = " + response.message());
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