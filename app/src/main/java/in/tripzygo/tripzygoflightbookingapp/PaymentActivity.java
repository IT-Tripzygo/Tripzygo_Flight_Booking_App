package in.tripzygo.tripzygoflightbookingapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.easebuzz.payment.kit.PWECouponsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import in.tripzygo.tripzygoflightbookingapp.Modals.FlightDetails;
import in.tripzygo.tripzygoflightbookingapp.Modals.Traveller;
import in.tripzygo.tripzygoflightbookingapp.Modals.User;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.ApiInterface;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.NullOnEmptyConverterFactory;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.Retrofitt;
import in.tripzygo.tripzygoflightbookingapp.Tools.SharedPreference;
import in.tripzygo.tripzygoflightbookingapp.Tools.Sort;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentActivity extends AppCompatActivity {
    private static int counter = 0;
    final List<Traveller>[] selectedTravellers = new List[]{new ArrayList<>()};
    Button button;
    String data;
    DatabaseReference reference;
    TextView oneWayDepartureCityText, oneWayArrivalCityText, priceFLightPayment, returnDepartureCityText, returnArrivalCityText,
            airline_name_oneWay, airline_name_return, returnDepartureTimeText, oneWayDepartureTimeText, returnArrivalTimeText,
            oneWayArrivalTimeText, oneWayDepartureDateText, returnDepartureDateText,
            oneWayArrivalDateText, returnArrivalDateText, BaseFare, Taxes, NetPayable;
    ImageView airlineImageOneWay, airlineImageReturn;
    CountryCodePicker codePicker;
    EditText phoneEditText, emailTextInputLayout, addressEditText, stateEditText, pinCodeEditText, countryEditText, cityEditText;
    CheckBox gstCheckBox;
    RelativeLayout gstRelativeLayout;
    RecyclerView travellerRecyclerView;
    FlightDetails flightDetails, returnFlightDetails;
    int finalAmount;
    boolean isChecked = false;
    JsonArray tripInfos;
    String type;
    MaterialToolbar materialToolbar;
    DatabaseReference userReference;
    private ActivityResultLauncher<Intent> pweActivityResultLauncher;

    public static String generateTransactionId() {
        // Increment the counter and use it as the transaction ID
        counter++;
        int paddedCounter = 1000000 + counter; // Add 10000 to ensure a 4-digit number
        String transactionId = String.valueOf(paddedCounter);
        return transactionId.substring(1); // Extract the last 4 digits
    }

    public static String encryptThisString(String input) {
        try {
            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() <= 32) {
                hashtext = "0" + hashtext;
                System.out.println("hashtext = " + hashtext);
            }

            // return the HashText
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        button = findViewById(R.id.checkout);
        priceFLightPayment = findViewById(R.id.price_flight_payment);
        oneWayDepartureCityText = findViewById(R.id.cityCodeDeparture);
        oneWayArrivalCityText = findViewById(R.id.cityCodeArrival);
        emailTextInputLayout = findViewById(R.id.EmailId_TravellerDetails);
        codePicker = findViewById(R.id.country_code);
        phoneEditText = findViewById(R.id.payment_phone);
        addressEditText = findViewById(R.id.AddressLineInBillingDetails);
        stateEditText = findViewById(R.id.StateInBillingDetails);
        countryEditText = findViewById(R.id.CountryInBillingDetails);
        pinCodeEditText = findViewById(R.id.PinCodeInBillingDetails);
        cityEditText = findViewById(R.id.CityInBillingDetails);
        airline_name_oneWay = findViewById(R.id.airline_nameOneway);
        oneWayDepartureTimeText = findViewById(R.id.timeToDeparture);
        oneWayArrivalTimeText = findViewById(R.id.timeToArrival);
        oneWayDepartureDateText = findViewById(R.id.dateToDeparture);
        oneWayArrivalDateText = findViewById(R.id.dateToArrival);
        airlineImageOneWay = findViewById(R.id.flagImg);
        travellerRecyclerView = findViewById(R.id.listOfTravellers);
        materialToolbar = findViewById(R.id.toolbar_payment);
        gstCheckBox = findViewById(R.id.GSTcheckBox);
        gstRelativeLayout = findViewById(R.id.GstContainer);
        materialToolbar.setNavigationOnClickListener(view -> {
            super.onBackPressed();
        });
        materialToolbar.setTitle("Traveller Details");
        materialToolbar.setTitleTextColor(Color.WHITE);
        BaseFare = findViewById(R.id.bareFaceAmount);
        Taxes = findViewById(R.id.TaxesAndFeesAmount);
        NetPayable = findViewById(R.id.totalAmount);
        Gson gson = new Gson();
        String si = getIntent().getStringExtra("sI");
        String tpi = getIntent().getStringExtra("tripInfos");
        String returnSI = getIntent().getStringExtra("returnsI");
        String totalPrice = getIntent().getStringExtra("totalPriceList");
        String returnTotalPrice = getIntent().getStringExtra("returntotalPriceList");
        Bundle bundle = getIntent().getBundleExtra("bundle");
        String bookingId = getIntent().getStringExtra("bookingId");
        String segmentKey = getIntent().getStringExtra("segmentKey");
        finalAmount = getIntent().getIntExtra("finalAmount", 0);
        String base = getIntent().getStringExtra("BaseFare");
        String taxes = getIntent().getStringExtra("Taxes");
        Locale locale = new Locale.Builder().setLanguage("en").setRegion("IN").build();
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        String currency = formatter.format(finalAmount);

        assert base != null;
        String basecurrency = formatter.format(Integer.parseInt(base));
        assert taxes != null;
        String taxcurrency = formatter.format(Integer.parseInt(taxes));
        int centsIndex = currency.lastIndexOf(".00");
        if (centsIndex != -1) {
            currency = currency.substring(0, centsIndex);
        }
        int basecentsIndex = basecurrency.lastIndexOf(".00");
        if (basecentsIndex != -1) {
            basecurrency = basecurrency.substring(0, basecentsIndex);
        }
        int taxcentsIndex = taxcurrency.lastIndexOf(".00");
        if (taxcentsIndex != -1) {
            taxcurrency = taxcurrency.substring(0, taxcentsIndex);
        }
        priceFLightPayment.setText(currency);
        NetPayable.setText(currency);
        BaseFare.setText(basecurrency);
        Taxes.setText(taxcurrency);
        String pa = getIntent().getStringExtra("paxInfo");
        assert bundle != null;
        flightDetails = (FlightDetails) bundle.get("flightDetails");
        type = bundle.getString("type");
        System.out.println(gson.toJson(bundle));
        System.out.println("type = " + type);
        returnFlightDetails = (FlightDetails) bundle.get("returnFlightDetails");
        JsonArray sI = gson.fromJson(si, new TypeToken<JsonArray>() {
        }.getType());
        JsonArray returnsI = gson.fromJson(returnSI, new TypeToken<JsonArray>() {
        }.getType());
        JsonObject pax = gson.fromJson(pa, new TypeToken<JsonObject>() {
        }.getType());
        JsonArray totalPriceList = gson.fromJson(totalPrice, new TypeToken<JsonArray>() {
        }.getType());
        tripInfos = gson.fromJson(tpi, new TypeToken<JsonArray>() {
        }.getType());
        JsonArray returnTotalPriceList = gson.fromJson(returnTotalPrice, new TypeToken<JsonArray>() {
        }.getType());
        User user = SharedPreference.loadUser(PaymentActivity.this);
        emailTextInputLayout.setText(user.getEmail_id());
        phoneEditText.setText(String.valueOf(user.getMobileNo()));
        codePicker.setCountryForPhoneCode(Integer.parseInt(user.getCode()));
        assert sI != null;
        airline_name_oneWay.setText(sI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("name").getAsString());
        oneWayDepartureCityText.setText(sI.get(0).getAsJsonObject().getAsJsonObject("da").get("cityCode").getAsString());
        int adult, children, infant;
        assert pax != null;
        adult = Integer.parseInt(pax.get("ADULT").getAsString());
        children = Integer.parseInt(pax.get("CHILD").getAsString());
        infant = Integer.parseInt(pax.get("INFANT").getAsString());
        List<Traveller> travellerList = new ArrayList<>();
        TravellersCountDetails_Adapter travellersCountDetailsAdapter = new TravellersCountDetails_Adapter(adult, children, infant, PaymentActivity.this, selectedTravellers[0], type);
        travellerRecyclerView.setLayoutManager(new LinearLayoutManager(PaymentActivity.this));
        travellerRecyclerView.setAdapter(travellersCountDetailsAdapter);
        selectedTravellers[0] = travellersCountDetailsAdapter.getSelectedTravellerList();
        System.out.println("gson selectedTravellers = " + gson.toJson(selectedTravellers));
        StorageReference storageReference1 = FirebaseStorage.getInstance().getReference().child("AirlineLogos").child(flightDetails.getAirlineImage() + ".png");
        storageReference1.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(PaymentActivity.this).load(uri).into(airlineImageOneWay);
        }).addOnFailureListener(e -> {
            System.out.println("storageReference1 = " + storageReference1);
            System.out.println("e.getMessage() = " + e.getMessage());
        });
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        gstCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
            isChecked = b;
            if (b) {

                gstRelativeLayout.setVisibility(View.VISIBLE);
                EditText editText = findViewById(R.id.GSTNumberInPayment);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String gstNumber = editable.toString();
                        user.setGstNumber(gstNumber);
                        Calendar calendar1 = Calendar.getInstance();
                        Date update = calendar1.getTime();
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("gstNumber", gstNumber);
                        userMap.put("updated_at", update);
                        userReference.child(user.getPhone_no()).updateChildren(userMap).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                SharedPreference.storeUser(PaymentActivity.this, user);
                            }
                        });
                    }
                });
            } else {

                gstRelativeLayout.setVisibility(View.GONE);
            }
        });
        if (sI.size() == 1) {
            oneWayArrivalCityText.setText(sI.get(0).getAsJsonObject().getAsJsonObject("aa").get("cityCode").getAsString());
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
            SimpleDateFormat df1 = new SimpleDateFormat("E, dd MMM yy", Locale.getDefault());
            SimpleDateFormat df2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String departure = sI.get(0).getAsJsonObject().get("dt").getAsString();
            String arrival = sI.get(0).getAsJsonObject().get("at").getAsString();
            try {
                Date dof = df.parse(departure);
                Date aol = df.parse(arrival);
                String dTime = df1.format(dof);
                String aTime = df1.format(aol);
                oneWayDepartureDateText.setText(dTime);
                oneWayArrivalDateText.setText(aTime);
                oneWayDepartureTimeText.setText(df2.format(dof));
                oneWayArrivalTimeText.setText(df2.format(aol));
                System.out.println("aTime = " + aTime + " dTime = " + dTime);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            oneWayArrivalCityText.setText(sI.get(sI.size() - 1).getAsJsonObject().getAsJsonObject("aa").get("cityCode").getAsString());
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
            SimpleDateFormat df1 = new SimpleDateFormat("E, dd MMM yy", Locale.getDefault());
            SimpleDateFormat df2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String departureOfFirst = sI.get(0).getAsJsonObject().get("dt").getAsString();
            String arrivalOfLast = sI.get(sI.size() - 1).getAsJsonObject().get("at").getAsString();
            try {
                Date dof = df.parse(departureOfFirst);
                Date aol = df.parse(arrivalOfLast);
                String dTime = df1.format(dof);
                String aTime = df1.format(aol);
                oneWayDepartureDateText.setText(dTime);
                oneWayArrivalDateText.setText(aTime);
                oneWayDepartureTimeText.setText(df2.format(dof));
                oneWayArrivalTimeText.setText(df2.format(aol));
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
        if (!(returnSI == null)) {
            returnDepartureCityText = findViewById(R.id.cityCodeDepartureRound);
            returnArrivalCityText = findViewById(R.id.cityCodeArrivalRound);
            airline_name_return = findViewById(R.id.airline_nameReturn);
            returnDepartureTimeText = findViewById(R.id.timeToDepartureRound);
            returnArrivalTimeText = findViewById(R.id.timeToArrivalRound);
            returnDepartureDateText = findViewById(R.id.dateToDepartureRound);
            returnArrivalDateText = findViewById(R.id.dateToArrivalRound);
            airlineImageReturn = findViewById(R.id.flagImgSecond);
            StorageReference storageReference2 = FirebaseStorage.getInstance().getReference().child("AirlineLogos").child(returnFlightDetails.getAirlineImage() + ".png");
            storageReference2.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(PaymentActivity.this).load(uri).into(airlineImageReturn);
            }).addOnFailureListener(e -> {
                System.out.println("storageReference1 = " + storageReference2);
                System.out.println("e.getMessage() = " + e.getMessage());
            });
            returnDepartureCityText.setText(returnsI.get(0).getAsJsonObject().getAsJsonObject("da").get("cityCode").getAsString());
            airline_name_return.setText(returnsI.get(0).getAsJsonObject().getAsJsonObject("fD").getAsJsonObject("aI").get("name").getAsString());
            if (returnsI.size() == 1) {
                returnArrivalCityText.setText(returnsI.get(0).getAsJsonObject().getAsJsonObject("aa").get("cityCode").getAsString());
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("E, dd MMM yy", Locale.getDefault());
                SimpleDateFormat df2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String departure = returnsI.get(0).getAsJsonObject().get("dt").getAsString();
                String arrival = returnsI.get(0).getAsJsonObject().get("at").getAsString();
                try {
                    Date dof = df.parse(departure);
                    Date aol = df.parse(arrival);
                    String dTime = df1.format(dof);
                    String aTime = df1.format(aol);
                    returnDepartureDateText.setText(dTime);
                    returnArrivalDateText.setText(aTime);
                    returnDepartureTimeText.setText(df2.format(dof));
                    returnArrivalTimeText.setText(df2.format(aol));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else {
                returnArrivalCityText.setText(returnsI.get(returnsI.size() - 1).getAsJsonObject().getAsJsonObject("aa").get("cityCode").getAsString());
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("E, dd MMM yy", Locale.getDefault());
                SimpleDateFormat df2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String departureOfFirst = returnsI.get(0).getAsJsonObject().get("dt").getAsString();
                String arrivalOfLast = returnsI.get(returnsI.size() - 1).getAsJsonObject().get("at").getAsString();
                try {
                    Date dof = df.parse(departureOfFirst);
                    Date aol = df.parse(arrivalOfLast);
                    String dTime = df1.format(dof);
                    String aTime = df1.format(aol);
                    returnDepartureDateText.setText(dTime);
                    returnArrivalDateText.setText(aTime);
                    returnDepartureTimeText.setText(df2.format(dof));
                    returnArrivalTimeText.setText(df2.format(aol));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }

        } else {
            ConstraintLayout constraintLayout = findViewById(R.id.RoundWay);
            constraintLayout.setVisibility(View.GONE);
        }

        String txnId = "TB" + bookingId.substring(3);
        System.out.println("txnId = " + txnId);
        JsonObject jsonObject = sI.get(0).getAsJsonObject();
        button.setOnClickListener(view -> {
            ProgressDialog progressDialog = new ProgressDialog(PaymentActivity.this);
            progressDialog.setMessage("Please Wait ...");
            progressDialog.show();
            JSONObject bodyObject = new JSONObject();
            JSONObject deliveryInfo = new JSONObject();
            JSONArray travellerInfo = new JSONArray();
            String[] emailStrings = new String[]{emailTextInputLayout.getText().toString()};
            String[] phoneStrings = new String[]{phoneEditText.getText().toString()};
            JSONArray es = new JSONArray();
            es.put(emailTextInputLayout.getText().toString());
            JSONArray ps = new JSONArray();
            ps.put(phoneEditText.getText().toString());
            try {
                for (Traveller tra : selectedTravellers[0]) {
                    JSONObject travellerObject = new JSONObject();
                    travellerObject.put("ti", tra.getTitle());
                    travellerObject.put("fN", tra.getFirstName());
                    travellerObject.put("lN", tra.getLastName());
                    travellerObject.put("pt", tra.getType());
                    travellerObject.put("dob", tra.getDob());
                    travellerInfo.put(travellerObject);
                }
                deliveryInfo.put("emails", es);
                deliveryInfo.put("contacts", ps);
                bodyObject.put("bookingId", bookingId);
                bodyObject.put("travellerInfo", travellerInfo);
                bodyObject.put("deliveryInfo", deliveryInfo);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            JsonObject jsonObject1 = gson.fromJson(bodyObject.toString(), new TypeToken<JsonObject>() {
            }.getType());
            System.out.println("gson = " + gson.toJson(bodyObject));
            System.out.println("gson hold = " + gson.toJson(jsonObject1));
            ApiInterface apiInterface = Retrofitt.initretro().create(ApiInterface.class);
            Call<JsonObject> call = apiInterface.block(jsonObject1);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonObject jsonObject = response.body();
                        if (jsonObject.getAsJsonObject("status").get("success").getAsBoolean()) {
                            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                                @Override
                                public Response intercept(Chain chain) throws IOException {
                                    Request newRequest = chain.request().newBuilder()
                                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                            .addHeader("Accept", "application/json")
                                            .build();
                                    return chain.proceed(newRequest);

                                }
                            }).build();
                            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                            ApiInterface apiInterface1 = new Retrofit.Builder()
                                    .baseUrl("https://travbox.travel/mobileapi/")
                                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                                    .addConverterFactory(new NullOnEmptyConverterFactory())
                                    .client(client)
                                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                                    .build().create(ApiInterface.class);
                            String key = "75489TYf9054653$545746458645&dh$%^&&*($#%^&@#%*";
                            String salt = "PVP9LULEWW";
                            float amount = finalAmount;
                            String productinfo = "Flight" /*+ sI.get(0).getAsJsonObject().getAsJsonObject("da").get("cityCode").getAsString() + " - " + sI.get(sI.size() - 1).getAsJsonObject().getAsJsonObject("aa").get("cityCode").getAsString() + " " + jsonObject.getAsJsonObject("fD").getAsJsonObject("aI").get("name").getAsString() + " " + jsonObject.getAsJsonObject("fD").get("fN").getAsString()*/;
                            String firstname = user.getUser_name();
                            Long phone = user.getMobileNo();
                            String email = user.getEmail_id();
                           /* String surl = "https://www.tripzygo.in/payment-success.html";
                            String furl = "https://www.tripzygo.in/payment-failed.html";
                            String hash = key + "|" + txnId + "|" + amount + "|" + productinfo + "|" + firstname + "|" + email + "|||||||||||" + salt;
                            String hashText = encryptThisString(hash);
                            String show_payment_mode = "NB,DC,CC,MW,UPI,OM,EMI,CBT,BT";*/
                            System.out.println("key = " + key + " txnId = " + txnId + " amount = " + amount + " productinfo = " + productinfo + " firstname = " + firstname + " email = " + email + " phone = " + phone);
                            System.out.println("txnId = " + txnId);
                            System.out.println("amount = " + amount);
                            System.out.println("productinfo = " + productinfo);
                            System.out.println("firstname = " + firstname);
                            System.out.println("email = " + email);
                            System.out.println("phone = " + phone);
                            /*System.out.println("surl = " + surl);
                            System.out.println("furl = " + furl);
                            System.out.println("hashText = " + hashText);
                            System.out.println("hash = " + hash);*/
                            Call<JsonObject> call1 = apiInterface1.getAccessKey(key, txnId, amount, productinfo, firstname, phone, email, "initiate_payment");
                            call1.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                                    if (response.isSuccessful()) {
                                        progressDialog.dismiss();
                                        JsonObject jsonObject = response.body();
                                        data = jsonObject.get("data").getAsString();
                                        System.out.println("data = " + data);
                                        Intent intentProceed = new Intent(PaymentActivity.this, PWECouponsActivity.class);
                                        intentProceed.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // This is mandatory flag
                                        intentProceed.putExtra("access_key", data);
                                        intentProceed.putExtra("pay_mode", "production");
                                        pweActivityResultLauncher.launch(intentProceed);
                                    } else {
                                        System.out.println("response = " + response);
                                    }
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    System.out.println("t = " + t.getMessage());
                                }
                            });
                        } else {
                            System.out.println("jsonObject.getAsJsonObject(\"status\").get(\"success\") = " + jsonObject.getAsJsonObject("status").get("success"));
                            System.out.println("gson.toJson(jsonObject) = " + gson.toJson(jsonObject));
                            Toast.makeText(PaymentActivity.this, jsonObject.getAsJsonArray("errors").get(0).getAsJsonObject().get("message").getAsString(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        System.out.println("response = " + response);
                        System.out.println("gson.toJson(res) error = " + gson.toJson(response.errorBody()));
                        System.out.println("gson.toJson(res) = " + gson.toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    System.out.println("t = " + t);
                }
            });


        });
        pweActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                if (data != null) {
                    String payment_result = data.getStringExtra("result");
                    String payment_response = data.getStringExtra("payment_response");
                    try {
                        // Handle response here
                        if (payment_result.equalsIgnoreCase("payment_successfull")) {
                            ProgressDialog progressDialog = new ProgressDialog(PaymentActivity.this);
                            progressDialog.setMessage("Please Wait ...");
                            progressDialog.show();
                            JSONObject bodyObject = new JSONObject();
                            JSONArray paymentInfos = new JSONArray();
                            JSONObject object = new JSONObject();
                            object.put("amount", finalAmount);
                            paymentInfos.put(object);
                            bodyObject.put("bookingId", bookingId);
                            bodyObject.put("paymentInfos", paymentInfos);
                            JsonObject jsonObject1 = gson.fromJson(String.valueOf(bodyObject), new TypeToken<JsonObject>() {
                            }.getType());
                            System.out.println("gson.toJson(jsonObject1) = " + gson.toJson(jsonObject1));
                            ApiInterface apiInterface = Retrofitt.initretro().create(ApiInterface.class);
                            Call<JsonObject> call = apiInterface.book(jsonObject1);
                            call.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                                    if (response.isSuccessful()) {
                                        JsonObject jsonObject = response.body();
                                        System.out.println("gson.toJson(jsonObject) book = " + gson.toJson(jsonObject));
                                        System.out.println("jsonObject.getAsJsonObject(\"status\").get(\"success\") = " + jsonObject.getAsJsonObject("status").get("success"));
                                        if (jsonObject.getAsJsonObject("status").get("success").getAsBoolean()) {
                                            HashMap<String, Object> userMap = new HashMap<>();
                                            userMap.put("bookingId", bookingId);
                                            userMap.put("phoneNo", user.getPhone_no());
                                            userMap.put("departure", sI.get(0).getAsJsonObject().get("dt").getAsString());
                                            userMap.put("tripInfos", String.valueOf(tripInfos));
                                            System.out.println("userMap = " + userMap);
//                                            startActivity(new Intent(PaymentActivity.this, BookingResultActivity.class).putExtra("status", jsonObject.getAsJsonObject("status").get("success").getAsBoolean()));

                                            reference = FirebaseDatabase.getInstance("https://flightbookingapp-307f0-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Bookings");
                                            reference.child(bookingId).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        progressDialog.dismiss();
                                                        startActivity(new Intent(PaymentActivity.this, BookingResultActivity.class)
                                                                .putExtra("status", task.isComplete())
                                                                .putExtra("gstCheck", isChecked)
                                                                .putExtra("tripInfos", String.valueOf(tripInfos))
                                                                .putExtra("userMap", userMap));
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    System.out.println("e.getMessage() = " + e.getMessage());
                                                }
                                            });
                                        } else {
                                            System.out.println("gson.toJson(jsonObject) = " + gson.toJson(jsonObject));
                                        }
                                    } else {
                                        System.out.println("response = " + response);
                                    }
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    System.out.println("t failure= " + t);
                                }
                            });

                        }
                        System.out.println("pa = " + payment_result);
                        System.out.println("payment_response = " + payment_response);
                    } catch (Exception e) {
                        System.out.println("e.getMessage() = " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void execute(OkHttpClient client, Request request) {
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ResponseBody jsonObject = response.body();
        data = jsonObject.toString();
        System.out.println("data = " + data);
    }
}
