package in.tripzygo.tripzygoflightbookingapp;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

public class BookingResultActivity extends AppCompatActivity implements Animator.AnimatorListener {
    LottieAnimationView lottieAnimationView;
    TextView congotxt;
    Button backToHomeButton;
    boolean booked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_result);
        booked = getIntent().getBooleanExtra("status", false);
        lottieAnimationView = findViewById(R.id.lottie_main);
        congotxt = findViewById(R.id.congotxt);
        backToHomeButton = findViewById(R.id.backToHome);
        if (booked) {
            congotxt.setText("Booking Confirmed");
            lottieAnimationView.setAnimation("134696-success-booking.json");
        } else {
            congotxt.setText("Booking Failed");
            congotxt.setTextColor(Color.RED);
            lottieAnimationView.setAnimation("134695-cancelled-parcel.json");
        }
        lottieAnimationView.setRepeatCount(0);
        lottieAnimationView.setRepeatMode(LottieDrawable.RESTART);
        lottieAnimationView.playAnimation();
        lottieAnimationView.addAnimatorListener(BookingResultActivity.this);
        backToHomeButton.setOnClickListener(view -> {
            startActivity(new Intent(BookingResultActivity.this,MainActivity.class));
        });
    }

    @Override
    public void onAnimationStart(@NonNull Animator animator) {

    }

    @Override
    public void onAnimationEnd(@NonNull Animator animator) {
        animator.pause();
    }

    @Override
    public void onAnimationCancel(@NonNull Animator animator) {

    }

    @Override
    public void onAnimationRepeat(@NonNull Animator animator) {

    }
}