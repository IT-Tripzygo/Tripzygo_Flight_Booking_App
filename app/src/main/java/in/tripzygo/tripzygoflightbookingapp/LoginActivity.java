package in.tripzygo.tripzygoflightbookingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;

import in.tripzygo.tripzygoflightbookingapp.Modals.User;

public class LoginActivity extends AppCompatActivity {
    EditText login_phone_text;
    Button sign_in;
    String mobileNo, Code;
    ProgressDialog loadingBar;
    CountryCodePicker countryCodePicker;
    DatabaseReference reference, databaseReference;
    boolean phoneExisted = false;
    User user_login = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        countryCodePicker = findViewById(R.id.country);
        login_phone_text = findViewById(R.id.login_phone);
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
            String phone = login_phone_text.getText().toString();
            loadingBar.show();
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
                     /*       System.out.println("gson = " + gson.toJson(user));
                            System.out.println("user = " + user.getPhone_no().substring(1));
                            System.out.println("mobileNo = " + mobileNo.substring(1));*/
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
        });

    }
}