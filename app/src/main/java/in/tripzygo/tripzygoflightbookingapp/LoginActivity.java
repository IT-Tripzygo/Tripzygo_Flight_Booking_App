package in.tripzygo.tripzygoflightbookingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import in.tripzygo.tripzygoflightbookingapp.Modals.User;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.ApiInterface;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.NullOnEmptyConverterFactory;
import in.tripzygo.tripzygoflightbookingapp.Tools.SharedPreference;
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

public class LoginActivity extends AppCompatActivity {
    EditText login_phone_text;
    Button sign_in;
    String mobileNo, Code;
    ProgressDialog loadingBar;
    CountryCodePicker countryCodePicker;
    DatabaseReference reference, databaseReference;
    boolean phoneExisted = false;
    User user_login = new User();
    TextInputLayout emailTextInputLayout, passwordTextInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        countryCodePicker = findViewById(R.id.country);
        login_phone_text = findViewById(R.id.login_phone);
        emailTextInputLayout = findViewById(R.id.login_rel1);
        passwordTextInputLayout = findViewById(R.id.login_rel);
        sign_in = findViewById(R.id.login_loginbt);
        loadingBar = new ProgressDialog(this);
        reference = FirebaseDatabase.getInstance("https://flightbookingapp-307f0-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        Code = countryCodePicker.getDefaultCountryCodeWithPlus();
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                Code = countryCodePicker.getSelectedCountryCodeWithPlus();
            }
        });
        sign_in.setOnClickListener(v -> {
            Gson gson = new Gson();
            String phone = login_phone_text.getText().toString();
            String email = emailTextInputLayout.getEditText().getText().toString();
            String password = passwordTextInputLayout.getEditText().getText().toString();
            loadingBar.show();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", email);
                jsonObject.put("password", password);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            JsonObject jsonObject1 = gson.fromJson(String.valueOf(jsonObject), new TypeToken<JsonObject>() {
            }.getType());
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("Accept", "application/json")
                            .build();
                    return chain.proceed(newRequest);
                }
            }).build();
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            ApiInterface apiInterface1 = new Retrofit.Builder()
                    .baseUrl("https://travbox.travel/travboxapi/")
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .addConverterFactory(new NullOnEmptyConverterFactory())
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                    .build().create(ApiInterface.class);
            Call<JsonObject> call = apiInterface1.login(jsonObject1);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    JsonObject jsonObject2 = response.body();
                    if (response.isSuccessful()) {
                        System.out.println(response);
                        String token = jsonObject2.get("token").getAsString();
                        SharedPreference.storeToken(LoginActivity.this, token);
                        loadingBar.dismiss();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        System.out.println(response);
                        ResponseBody responseBody = response.errorBody();
                        System.out.println(gson.toJson(responseBody));
                        try {
                            // Convert error body to FailureResponse
                            JsonObject jsonObject3 = gson.fromJson(response.errorBody().string(), JsonObject.class);
                            System.out.println(gson.toJson(jsonObject3));
                            MaterialAlertDialogBuilder materialAlertDialogBuilder=new MaterialAlertDialogBuilder(LoginActivity.this);
                            materialAlertDialogBuilder.setMessage(jsonObject3.get("message").getAsString());
                            materialAlertDialogBuilder.setPositiveButton("Open Link",(dialogInterface, i) -> {

                            });
//                            loadingBar.setMessage(jsonObject2.get("message").getAsString());
                            // Access the message or other properties from failureResponse
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                        loadingBar.setMessage(jsonObject2.get("message").getAsString());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    System.out.println(t);
                    loadingBar.setMessage(t.getMessage());
                }
            });
/*
            if (phone.length() != 10) {
                login_phone_text.setError("Please Enter Valid Phone Number ");
                login_phone_text.requestFocus();
                loadingBar.dismiss();
            } else {
                loadingBar.dismiss();
                mobileNo = Code + phone;
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            User user = snap.getValue(User.class);
                            System.out.println("snap = " + snap);
                            Gson gson = new Gson();
                     */
/*       System.out.println("gson = " + gson.toJson(user));
                            System.out.println("user = " + user.getPhone_no().substring(1));
                            System.out.println("mobileNo = " + mobileNo.substring(1));*//*

                            if (user.getPhone_no().substring(1).matches(mobileNo.substring(1))) {
                                phoneExisted = true;
                                user_login = user;
                            }
                        }
                        Intent intent;
                        System.out.println("phoneExisted = " + phoneExisted);
                        if (phoneExisted) {
                            intent = new Intent(LoginActivity.this, OtpActivity.class);
                            intent.putExtra("mobileNo", mobileNo);
                            intent.putExtra("user", user_login);
                            loadingBar.dismiss();
                        } else {
                            intent = new Intent(LoginActivity.this, ProfileActivity.class);
                            String code = countryCodePicker.getSelectedCountryNameCode();
                            intent.putExtra("mobileNo", phone);
                            intent.putExtra("code", code);
                            loadingBar.dismiss();
                        }

                        intent.putExtra("phoneExisted", phoneExisted);
                        startActivity(intent);
                        finish();

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("error = " + error.getMessage());
                    }
                });
            }
*/
        });

    }
}