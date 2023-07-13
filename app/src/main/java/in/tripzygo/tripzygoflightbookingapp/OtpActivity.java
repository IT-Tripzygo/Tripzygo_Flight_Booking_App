package in.tripzygo.tripzygoflightbookingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import in.tripzygo.tripzygoflightbookingapp.Modals.User;
import in.tripzygo.tripzygoflightbookingapp.Tools.CustomToast;
import in.tripzygo.tripzygoflightbookingapp.Tools.SharedPreference;

public class OtpActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    TextView phoneTextView, resendTextView;
    TextInputEditText otpTextInputEditText;
    Button verifyButton;
    ProgressDialog loadingBar;
    String mobileNo;
    int sec = 20;
    String otpId;
    DatabaseReference reference;
    User user;
    boolean phoneExisted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        mAuth = FirebaseAuth.getInstance();
        mobileNo = getIntent().getStringExtra("mobileNo");
        phoneExisted = getIntent().getBooleanExtra("phoneExisted", false);
        user = (User) getIntent().getSerializableExtra("user");
        loadingBar = new ProgressDialog(this);
        phoneTextView = findViewById(R.id.otp_phone);
        resendTextView = findViewById(R.id.resend_otp);
        otpTextInputEditText = findViewById(R.id.otp);
        verifyButton = findViewById(R.id.verify);
        phoneTextView.setText(mobileNo);
        phoneAuth();
        verifyButton.setOnClickListener(v -> {
            String otp = otpTextInputEditText.getText().toString();
            if (otp.isEmpty()) {
                CustomToast.makeText(this, "Please enter otp", Toast.LENGTH_SHORT, Color.RED);
            } else {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpId, otp);
                signinwithcredentials(credential);
            }
        });
    }

    private void signinwithcredentials(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                reference = FirebaseDatabase.getInstance("https://flightbookingapp-307f0-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
                HashMap<String, Object> userMap = new HashMap<>();
                userMap.put(user.getPhone_no(), user);
                reference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            SharedPreference.storeUser(OtpActivity.this, user);
                            SharedPreference.storeLogin(OtpActivity.this, "Logged in");
                            startActivity(new Intent(OtpActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("e = " + e.getMessage());
                    }
                });
                SharedPreference.storeLogin(OtpActivity.this, "Logged in");
                startActivity(new Intent(OtpActivity.this, MainActivity.class));
                finish();

            }
        });
    }

    void phoneAuth() {
        loadingBar.show();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth).setPhoneNumber(mobileNo).setTimeout(50L, TimeUnit.SECONDS)
                .setActivity(OtpActivity.this).setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signinwithcredentials(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        System.out.println("e.getMessage() firebase " + e.getMessage());
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        otpId = s;
                        System.out.println("s = " + s);
                        System.out.println("forceResendingToken = " + forceResendingToken);
                        loadingBar.dismiss();
                        Timer();
                    }
                }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    void Timer() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                sec--;
                resendTextView.setText("Resend Otp in " + sec);
                if (sec == 0) {
                    handler.removeCallbacksAndMessages(null);
                    resendTextView.setText("Resend OTP");
                    resendTextView.setOnClickListener(v -> {
                        sec = 20;
                        phoneAuth();
                    });
                }
            }
        });
    }
}