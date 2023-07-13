package in.tripzygo.tripzygoflightbookingapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easebuzz.payment.kit.PWECouponsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private ActivityResultLauncher<Intent> pweActivityResultLauncher;
    Button button;
    private static int counter = 0;
    String data;
    DatabaseReference reference;
    RecyclerView flightRecyclerView, returnFlightRecyclerView;
    TextView DepartureCityText, ArrivalCityText, classText, stopText, timeText,
            ChangeText, priceFLightPayment, returnDepartureCityText, returnArrivalCityText, returnClassText, returnStopText, returnTimeText;
    TextInputLayout emailTextInputLayout;
    CountryCodePicker codePicker;
    EditText phoneEditText;
    CheckBox checkBox;
    FlightDetails flightDetails, returnFlightDetails;
    final List<Traveller>[] selectedTravellers = new List[]{new ArrayList<>()};

    RecyclerView recyclerView;
    int finalAmount;
    boolean isChecked = false;
    JsonArray tripInfos;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        button = findViewById(R.id.checkout);
        recyclerView = findViewById(R.id.passengerRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(PaymentActivity.this));
        priceFLightPayment = findViewById(R.id.price_flight_payment);
        DepartureCityText = findViewById(R.id.DepartureCityText);
        ArrivalCityText = findViewById(R.id.ArrivalCityText);
        classText = findViewById(R.id.classText);
        stopText = findViewById(R.id.stopText);
        timeText = findViewById(R.id.timeText);
        ChangeText = findViewById(R.id.ChangeText);
        emailTextInputLayout = findViewById(R.id.login_rel1_email);
        codePicker = findViewById(R.id.country_payment);
        phoneEditText = findViewById(R.id.payment_phone);
        checkBox = findViewById(R.id.checkbox);
        checkBox.setChecked(isChecked);
//        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                isChecked = b;
//                if (!b) {
//                    button.setClickable(false);
//                } else {
//                    button.setClickable(true);
//                }
//            }
//        });

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
        priceFLightPayment.setText("₹ " + finalAmount);
        String pa = getIntent().getStringExtra("paxInfo");
        flightDetails = (FlightDetails) bundle.get("flightDetails");
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
        JsonArray returntotalPriceList = gson.fromJson(returnTotalPrice, new TypeToken<JsonArray>() {
        }.getType());
        User user = SharedPreference.loadUser(PaymentActivity.this);
        emailTextInputLayout.getEditText().setText(user.getEmail_id());
        phoneEditText.setText(String.valueOf(user.getMobileNo()));
        codePicker.setCountryForPhoneCode(Integer.parseInt(user.getCode()));
        reference = FirebaseDatabase.getInstance("https://flightbookingapp-307f0-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users").child(user.getPhone_no()).child("Travellers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ChangeText.setText("Change Traveller");
                    ChangeText.setOnClickListener(view -> {
                        View v1 = LayoutInflater.from(PaymentActivity.this).inflate(R.layout.activity_change_traveller, null, false);
                        RecyclerView adultRecyclerView = v1.findViewById(R.id.changeAdultRecycler);
                        RecyclerView childRecyclerView = v1.findViewById(R.id.changeChildRecycler);
                        RecyclerView infantRecyclerView = v1.findViewById(R.id.changeInfantRecycler);
                        TextView doneTextView = v1.findViewById(R.id.Done);
                        TextView selectAdultTextView = v1.findViewById(R.id.selectAdultText);
                        TextView selectChildTextView = v1.findViewById(R.id.selectChildText);
                        TextView selectInfantTextView = v1.findViewById(R.id.selectInfantText);
                        TextView addAdultTextView = v1.findViewById(R.id.ADDText);
                        TextView addChildTextView = v1.findViewById(R.id.ADDChildText);
                        TextView addInfantTextView = v1.findViewById(R.id.ADDInfantText);
                        View view1 = v1.findViewById(R.id.viewsept3);
                        View view2 = v1.findViewById(R.id.viewsept4);
                        View view3 = v1.findViewById(R.id.viewsept5);
                        View view4 = v1.findViewById(R.id.viewsept6);
                        View view5 = v1.findViewById(R.id.viewsept7);
                        View view6 = v1.findViewById(R.id.viewsept8);
                        adultRecyclerView.setLayoutManager(new LinearLayoutManager(PaymentActivity.this));
                        childRecyclerView.setLayoutManager(new LinearLayoutManager(PaymentActivity.this));
                        infantRecyclerView.setLayoutManager(new LinearLayoutManager(PaymentActivity.this));
                        addAdultTextView.setOnClickListener(view7 -> {
                            startActivity(new Intent(PaymentActivity.this, AddNewTravellerActivity.class));
                        });
                        addChildTextView.setOnClickListener(view7 -> {
                            startActivity(new Intent(PaymentActivity.this, AddNewTravellerActivity.class));
                        });
                        addInfantTextView.setOnClickListener(view7 -> {
                            startActivity(new Intent(PaymentActivity.this, AddNewTravellerActivity.class));
                        });
                        List<Traveller> AdultTravellers = new ArrayList<>();
                        List<Traveller> ChildTravellers = new ArrayList<>();
                        List<Traveller> InfantTravellers = new ArrayList<>();

                        List<Traveller> travellerList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Traveller traveller = dataSnapshot.getValue(Traveller.class);
                            travellerList.add(traveller);
                        }
                        for (Traveller traveller : travellerList) {
                            if (traveller.getType().equalsIgnoreCase("Adult")) {
                                if (!AdultTravellers.contains(traveller)) {
                                    AdultTravellers.add(traveller);
                                } else {
                                    System.out.println("gson.toJson(AdultTravellers) = " + gson.toJson(AdultTravellers));
                                }
                            } else if (traveller.getType().equalsIgnoreCase("Child")) {
                                if (!ChildTravellers.contains(traveller)) {
                                    ChildTravellers.add(traveller);
                                } else {
                                    System.out.println("gson.toJson(AdultTravellers) = " + gson.toJson(ChildTravellers));
                                }
                            } else if (traveller.getType().equalsIgnoreCase("Infant")) {
                                if (!InfantTravellers.contains(traveller)) {
                                    InfantTravellers.add(traveller);
                                } else {
                                    System.out.println("gson.toJson(AdultTravellers) = " + gson.toJson(InfantTravellers));
                                }
                            }
                        }
                        System.out.println("gson.toJson(AdultTravellers) = " + gson.toJson(AdultTravellers));
                        TravellerAdapter AdultTravellerAdapter = new TravellerAdapter(AdultTravellers, selectedTravellers[0], PaymentActivity.this);
                        TravellerAdapter ChildTravellerAdapter = new TravellerAdapter(ChildTravellers, selectedTravellers[0], PaymentActivity.this);
                        TravellerAdapter InfantTravellerAdapter = new TravellerAdapter(InfantTravellers, selectedTravellers[0], PaymentActivity.this);
                        adultRecyclerView.setAdapter(AdultTravellerAdapter);
                        AdultTravellerAdapter.notifyDataSetChanged();
                        childRecyclerView.setAdapter(ChildTravellerAdapter);
                        ChildTravellerAdapter.notifyDataSetChanged();
                        infantRecyclerView.setAdapter(InfantTravellerAdapter);
                        InfantTravellerAdapter.notifyDataSetChanged();
                        if (pax.get("INFANT").getAsString().matches("0") && pax.get("CHILD").getAsString().matches("0")) {
                            childRecyclerView.setVisibility(View.GONE);
                            infantRecyclerView.setVisibility(View.GONE);
                            selectChildTextView.setVisibility(View.GONE);
                            selectInfantTextView.setVisibility(View.GONE);
                            addChildTextView.setVisibility(View.GONE);
                            addInfantTextView.setVisibility(View.GONE);
                            view2.setVisibility(View.GONE);
                            view4.setVisibility(View.GONE);
                            view5.setVisibility(View.GONE);
                            view6.setVisibility(View.GONE);
                        } else if (pax.get("INFANT").getAsString().matches("0")) {
                            infantRecyclerView.setVisibility(View.GONE);
                            selectInfantTextView.setVisibility(View.GONE);
                            addInfantTextView.setVisibility(View.GONE);
                            view5.setVisibility(View.GONE);
                            view6.setVisibility(View.GONE);
                        } else if (pax.get("CHILD").getAsString().matches("0")) {
                            childRecyclerView.setVisibility(View.GONE);
                            selectChildTextView.setVisibility(View.GONE);
                            addChildTextView.setVisibility(View.GONE);
                            view2.setVisibility(View.GONE);
                            view4.setVisibility(View.GONE);
                        }
//                        reference.addChildEventListener(new ChildEventListener() {
//                            @Override
//                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                                List<Traveller> travellerList = new ArrayList<>();
//                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                                    Traveller traveller = dataSnapshot.getValue(Traveller.class);
//                                    travellerList.add(traveller);
//                                }
//                                for (Traveller traveller : travellerList) {
//                                    if (traveller.getType().equalsIgnoreCase("Adult")) {
//                                        if (!AdultTravellers.contains(traveller)) {
//                                            AdultTravellers.add(traveller);
//                                        }else {
//                                            System.out.println("gson.toJson(AdultTravellers) = " + gson.toJson(AdultTravellers));
//                                        }
//                                    } else if (traveller.getType().equalsIgnoreCase("Child")) {
//                                        if (!ChildTravellers.contains(traveller)) {
//                                            ChildTravellers.add(traveller);
//                                        }else {
//                                            System.out.println("gson.toJson(AdultTravellers) = " + gson.toJson(ChildTravellers));
//                                        }
//                                    } else if (traveller.getType().equalsIgnoreCase("Infant")) {
//                                        if (!InfantTravellers.contains(traveller)) {
//                                            InfantTravellers.add(traveller);
//                                        }else {
//                                            System.out.println("gson.toJson(AdultTravellers) = " + gson.toJson(InfantTravellers));
//                                        }
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                            }
//
//                            @Override
//                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//                            }
//
//                            @Override
//                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
                        final Dialog bottomSheetDialog = new Dialog(PaymentActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                        bottomSheetDialog.setContentView(v1);
                        bottomSheetDialog.setCanceledOnTouchOutside(false);
                        bottomSheetDialog.setCancelable(false);
                        final Window window = bottomSheetDialog.getWindow();
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                        bottomSheetDialog.show();
                        doneTextView.setOnClickListener(view7 -> {
                            bottomSheetDialog.dismiss();
                            TravellerAdapter adapter = (TravellerAdapter) adultRecyclerView.getAdapter();
                            selectedTravellers[0] = adapter.getSelectedTravellerList();

                            System.out.println("selectedTravellers = " + gson.toJson(selectedTravellers[0]));
//                            selectedTravellers[0].addAll(ChildTravellerAdapter.getSelectedTravellerList());
//                            selectedTravellers[0].addAll(InfantTravellerAdapter.getSelectedTravellerList());
                            System.out.println("selectedTravellers = " + gson.toJson(selectedTravellers[0]));
                            SelectedPassengerAdapter selectedPassengerAdapter1 = new SelectedPassengerAdapter(selectedTravellers[0], PaymentActivity.this);
                            recyclerView.setAdapter(selectedPassengerAdapter1);
                            selectedPassengerAdapter1.notifyDataSetChanged();
                        });
                    });
                } else {
                    recyclerView.setVisibility(View.GONE);
                    ChangeText.setText("Add Traveller");
                    ChangeText.setOnClickListener(view -> {
                        startActivity(new Intent(PaymentActivity.this, AddNewTravellerActivity.class));
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("error = " + error);
            }
        });
        stopText.setText(flightDetails.getTotalStops());
        flightRecyclerView = findViewById(R.id.flightDetailRecycler);
        flightRecyclerView.setLayoutManager(new LinearLayoutManager(PaymentActivity.this));
        FlightDetailsAdapter flightDetailsAdapter = new FlightDetailsAdapter(flightDetails, PaymentActivity.this, sI.size());
        flightRecyclerView.setAdapter(flightDetailsAdapter);

        if (sI.size() == 1) {
            DepartureCityText.setText(sI.get(0).getAsJsonObject().getAsJsonObject("da").get("city").getAsString());
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
            JsonObject jsonObject2 = jsonElement.getAsJsonObject();
            if (!jsonObject2.get("fareIdentifier").getAsString().matches("SPECIAL_RETURN")) {
                jsonObjects.add(jsonObject2);
            }
        }
        jsonObjects.sort(new Sort());
        classText.setText(jsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString());
        if (!(returnSI == null)) {
            returnFlightRecyclerView = findViewById(R.id.returnflightDetailRecycler);
            returnFlightRecyclerView.setLayoutManager(new LinearLayoutManager(PaymentActivity.this));
            returnDepartureCityText = findViewById(R.id.returnDepartureCityText);
            returnArrivalCityText = findViewById(R.id.returnArrivalCityText);
            returnClassText = findViewById(R.id.returnclassText);
            returnStopText = findViewById(R.id.returnstopText);
            returnTimeText = findViewById(R.id.returntimeText);
            returnStopText.setText(returnFlightDetails.getTotalStops());
            FlightDetailsAdapter returnFlightDetailsAdapter = new FlightDetailsAdapter(returnFlightDetails, PaymentActivity.this, returnsI.size());
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
            for (JsonElement jsonElement : returntotalPriceList) {
                JsonObject jsonObject2 = jsonElement.getAsJsonObject();
                if (!jsonObject2.get("fareIdentifier").getAsString().matches("SPECIAL_RETURN")) {
                    returnjsonObjects.add(jsonObject2);
                }
            }
            returnjsonObjects.sort(new Sort());
            returnClassText.setText(returnjsonObjects.get(0).getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString());
        } else {
            RelativeLayout relativeLayout = findViewById(R.id.returnFLightLayout);
            relativeLayout.setVisibility(View.GONE);
        }

        String txnId = "TPZ" + bookingId.substring(3);
        System.out.println("txnId = " + txnId);
        JsonObject jsonObject = sI.get(0).getAsJsonObject();
        button.setOnClickListener(view -> {
            JSONObject bodyObject = new JSONObject();
            JSONObject deliveryInfo = new JSONObject();
            JSONArray travellerInfo = new JSONArray();
            String[] emailStrings = new String[]{emailTextInputLayout.getEditText().getText().toString()};
            String[] phoneStrings = new String[]{phoneEditText.getText().toString()};
            JSONArray es = new JSONArray();
            es.put(emailTextInputLayout.getEditText().getText().toString());
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
            System.out.println("gson = " + gson.toJson(jsonObject1));
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
                                    .baseUrl("https://testpay.easebuzz.in/payment/")
                                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                                    .addConverterFactory(new NullOnEmptyConverterFactory())
                                    .client(client)
                                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                                    .build().create(ApiInterface.class);
                            String key = "2PBP7IABZ2";
                            String salt = "DAH88E3UWQ";
                            System.out.println("txnId = " + txnId);
                            float amount = finalAmount;
                            String productinfo = "Flight" /*+ sI.get(0).getAsJsonObject().getAsJsonObject("da").get("cityCode").getAsString() + " - " + sI.get(sI.size() - 1).getAsJsonObject().getAsJsonObject("aa").get("cityCode").getAsString() + " " + jsonObject.getAsJsonObject("fD").getAsJsonObject("aI").get("name").getAsString() + " " + jsonObject.getAsJsonObject("fD").get("fN").getAsString()*/;
                            System.out.println("productinfo = " + productinfo);
                            String firstname = user.getUser_name();
                            Long phone = user.getMobileNo();
                            String email = user.getEmail_id();
                            String surl = "https://www.tripzygo.in/payment-success.html";
                            String furl = "https://www.tripzygo.in/payment-failed.html";
                            String hash = key + "|" + txnId + "|" + amount + "|" + productinfo + "|" + firstname + "|" + email + "|||||||||||" + salt;
                            String hashText = encryptThisString(hash);
                            String show_payment_mode = "NB,DC,CC,MW,UPI,OM,EMI,CBT,BT";
                            System.out.println("key = " + key + " txnId = " + txnId + " amount = " + amount + " productinfo = " + productinfo + " firstname = " + firstname + " email = " + email + " phone = " + phone + " surl = " + surl + " furl = " + furl + " hashText = " + hashText);
                            System.out.println("txnId = " + txnId);
                            System.out.println("amount = " + amount);
                            System.out.println("productinfo = " + productinfo);
                            System.out.println("firstname = " + firstname);
                            System.out.println("email = " + email);
                            System.out.println("phone = " + phone);
                            System.out.println("surl = " + surl);
                            System.out.println("furl = " + furl);
                            System.out.println("hashText = " + hashText);
                            System.out.println("hash = " + hash);
                            Call<JsonObject> call1 = apiInterface1.getAccessKey(key, txnId, amount, productinfo, firstname, phone, email, surl, furl, hashText, show_payment_mode);
                            call1.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                                    if (response.isSuccessful()) {
                                        JsonObject jsonObject = response.body();
                                        data = jsonObject.get("data").getAsString();
                                        System.out.println("data = " + data);
                                        Intent intentProceed = new Intent(PaymentActivity.this, PWECouponsActivity.class);
                                        intentProceed.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // This is mandatory flag
                                        intentProceed.putExtra("access_key", data);
                                        intentProceed.putExtra("pay_mode", "test");
                                        pweActivityResultLauncher.launch(intentProceed);
                                    } else {
                                        System.out.println("response = " + response.message());
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
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    System.out.println("t = " + t);
                }
            });
         /*   OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
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
                    .baseUrl("https://testpay.easebuzz.in/payment/")
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .addConverterFactory(new NullOnEmptyConverterFactory())
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                    .build().create(ApiInterface.class);
            JsonObject bodyJsonObject = new JsonObject();
            String key = "2PBP7IABZ2";
            String salt = "DAH88E3UWQ";

//            String txnId = "TRIP" + generateTransactionId();
            System.out.println("txnId = " + txnId);
            float amount = finalAmount;
            String productinfo = "Flight " + sI.get(0).getAsJsonObject().getAsJsonObject("da").get("cityCode").getAsString() + " - " + sI.get(sI.size() - 1).getAsJsonObject().getAsJsonObject("aa").get("cityCode").getAsString() + " " + jsonObject.getAsJsonObject("fD").getAsJsonObject("aI").get("name").getAsString() + " " + jsonObject.getAsJsonObject("fD").get("fN").getAsString();
            System.out.println("productinfo = " + productinfo);
            String firstname = user.getUser_name();
            int phone = Integer.parseInt(user.getMobileNo());
            String email = user.getEmail_id();
            String surl = "https://www.tripzygo.in/payment-success.html";
            String furl = "https://www.tripzygo.in/payment-failed.html";
            String hash = key + "|" + txnId + "|" + amount + "|" + productinfo + "|" + firstname + "|" + email + "|||||||||||" + salt;
            String hashText = encryptThisString(hash);
            System.out.println("hashText = " + hashText);
            bodyJsonObject.addProperty("key", key);
            bodyJsonObject.addProperty("txnId", txnId);
            bodyJsonObject.addProperty("amount", amount);
            bodyJsonObject.addProperty("productinfo", productinfo);
            bodyJsonObject.addProperty("firstname", firstname);
            bodyJsonObject.addProperty("phone", phone);
            bodyJsonObject.addProperty("email", email);
            bodyJsonObject.addProperty("surl", surl);
            bodyJsonObject.addProperty("furl", furl);
            bodyJsonObject.addProperty("hash", hashText);


            System.out.println("mediaType = " + mediaType);
            RequestBody body = RequestBody.create(mediaType, "key=" + key + "&txnid=" + txnId + "&amount=" + amount + "&productinfo=" + productinfo + "&firstname=" + firstname + "&phone=" + phone + "&email=" + email + "&surl=" + surl + "&furl=" + furl
                    + "&hash=" + hash);
            System.out.println("gson = " + gson.toJson(bodyJsonObject));
            System.out.println("body = " + gson.toJson(body));
       */
//            Request request = new Request.Builder()
//                    .url("https://testpay.easebuzz.in/payment/initiateLink")
//                    .post(body)
//                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
//                    .addHeader("Accept", "application/json")
//                    .build();
//            System.out.println("request.toString() = " + request.toString());
//
//            execute(client,request);
//            try {
//                Response response = client.newCall(request).execute();
//                ResponseBody jsonObject = response.body();
//                data = jsonObject.toString();
//                System.out.println("data = " + data);

//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }


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
                            JSONObject bodyObject = new JSONObject();
                            JSONArray paymentInfos = new JSONArray();
                            JSONObject object = new JSONObject();
                            object.put("amount", finalAmount);
                            paymentInfos.put(object);
                            bodyObject.put("bookingId", bookingId);
                            bodyObject.put("paymentInfos", paymentInfos);
                            JsonObject jsonObject1 = gson.fromJson(String.valueOf(bodyObject), new TypeToken<JsonObject>() {
                            }.getType());
                            ApiInterface apiInterface = Retrofitt.initretro().create(ApiInterface.class);
                            Call<JsonObject> call = apiInterface.book(jsonObject1);
                            call.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                                    if (response.isSuccessful()) {
                                        JsonObject jsonObject = response.body();
                                        if (jsonObject.getAsJsonObject("status").get("success").getAsBoolean()) {
                                            HashMap<String, Object> userMap = new HashMap<>();
                                            userMap.put("bookingId", bookingId);
                                            userMap.put("departure",sI.get(0).getAsJsonObject().get("dt").getAsString());
                                            userMap.put("tripInfos", String.valueOf(tripInfos));
                                            System.out.println("userMap = " + userMap);
//                                            startActivity(new Intent(PaymentActivity.this, BookingResultActivity.class).putExtra("status", jsonObject.getAsJsonObject("status").get("success").getAsBoolean()));

                                            reference = FirebaseDatabase.getInstance("https://flightbookingapp-307f0-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users").child(user.getPhone_no()).child("Bookings");
                                            reference.child(bookingId).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        startActivity(new Intent(PaymentActivity.this, BookingResultActivity.class).putExtra("status", task.isComplete()));
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    System.out.println("e.getMessage() = " + e.getMessage());
                                                }
                                            });
                                        } else {
                                            System.out.println("gson.toJson(jsonObject) = " + gson.toJson(jsonObject));
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {

                                }
                            });

                        }
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
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            // return the HashText
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
