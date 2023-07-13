package in.tripzygo.tripzygoflightbookingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import in.tripzygo.tripzygoflightbookingapp.Tools.SharedPreference;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SharedPreference.loadLogin(SplashActivity.this).matches("Logged out")) {
                    System.out.println("video = " + SharedPreference.loadLogin(SplashActivity.this));
                    Intent startActivity = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(startActivity);
                    finish();
                } else if (SharedPreference.loadLogin(SplashActivity.this).matches("Logged in")) {
                    System.out.println("video = " + SharedPreference.loadLogin(SplashActivity.this));
                    Intent startActivity = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(startActivity);
                    finish();
                }

            }
        }, 3000);
    }
}