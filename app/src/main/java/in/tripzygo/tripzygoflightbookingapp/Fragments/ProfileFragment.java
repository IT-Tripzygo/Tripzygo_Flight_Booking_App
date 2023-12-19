package in.tripzygo.tripzygoflightbookingapp.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;
import in.tripzygo.tripzygoflightbookingapp.BuildConfig;
import in.tripzygo.tripzygoflightbookingapp.LoginActivity;
import in.tripzygo.tripzygoflightbookingapp.MainActivity;
import in.tripzygo.tripzygoflightbookingapp.Modals.User;
import in.tripzygo.tripzygoflightbookingapp.R;
import in.tripzygo.tripzygoflightbookingapp.Tools.SharedPreference;


public class ProfileFragment extends Fragment {
    CircleImageView userCircleImageView;
    TextView nameTextView, emailTextView, editTextView, shareTextView, faqTextView, myBookingTextView, customerServiceTextView, notificationsTextView, rateTextView, versionTextView, privacyTextView;
    Button logoutButton;
    DatabaseReference userReference;
    User user;
    SwitchMaterial notificationSwitchMaterial;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        userCircleImageView = view.findViewById(R.id.profile_image);
        nameTextView = view.findViewById(R.id.profile_name_profilePage);
        emailTextView = view.findViewById(R.id.profile_email_profilePage);
        versionTextView = view.findViewById(R.id.version_no_);
        editTextView = view.findViewById(R.id.edit_profile);
        myBookingTextView = view.findViewById(R.id.myBooking);
        customerServiceTextView = view.findViewById(R.id.customerService);
        rateTextView = view.findViewById(R.id.rate);
        shareTextView = view.findViewById(R.id.share);
        faqTextView = view.findViewById(R.id.faq);
        logoutButton = view.findViewById(R.id.logout);
        privacyTextView = view.findViewById(R.id.privacy);
        myBookingTextView.setOnClickListener(view1 -> {
            getParentFragmentManager().beginTransaction().replace(R.id.frame, new MyBookingFragment()).commit();
            MainActivity.materialToolbar.setTitle("My Bookings");
        });
        privacyTextView.setOnClickListener(view1 -> {
            showOpenLinkDialog("https://www.travbox.in/privacy_policy.html");
        });
        faqTextView.setOnClickListener(view1 -> {
            showOpenLinkDialog("https://www.travbox.in/faq.html");
        });
        customerServiceTextView.setOnClickListener(view1 -> {
            showCallDialog();
        });
        notificationSwitchMaterial = view.findViewById(R.id.notification_switch);
        user = SharedPreference.loadUser(getContext());
        FirebaseApp.initializeApp(getContext());
        userReference = FirebaseDatabase.getInstance("https://flightbookingapp-307f0-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        System.out.println("user = " + user.getProfile_img());
        if (user.getProfile_img() != null) {
            Glide.with(getContext()).load(user.getProfile_img()).into(userCircleImageView);
        }
        notificationSwitchMaterial.setOnCheckedChangeListener((compoundButton, b) -> {
            ColorStateList stateList;
            if (!b) {
                stateList = ColorStateList.valueOf(getResources().getColor(R.color.light_grey));
            } else {
                stateList = ColorStateList.valueOf(getResources().getColor(R.color.royalBlue));
            }
            notificationSwitchMaterial.setTrackTintList(stateList);
        });
        nameTextView.setText(user.getUser_name());
        emailTextView.setText(user.getEmail_id());
        versionTextView.setText("Version " + BuildConfig.VERSION_NAME);
        logoutButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("dataSaved", "notSaved");
            SharedPreference.storeData(getContext(), bundle);
            user = new User();
            SharedPreference.storeUser(getContext(), user);
            SharedPreference.storeLogin(getContext(), "Logged out");
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
        rateTextView.setOnClickListener(view1 -> {
            giveReview();
        });
        shareTextView.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "I just found Travbox flight booking App on Google Play Store.\n" +
                    "\n" +
                    "Keep your Travel Hassle free  and get the best fares.\n" +
                    "\n" +
                    "Download the app here:https://play.google.com/store/apps/details?id=com.loose_weight";
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        });
        return view;
    }

    private void giveReview() {
        ReviewManager manager = ReviewManagerFactory.create(getContext());
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                System.out.println("task = " + task.getResult());
                System.out.println("reviewInfo = " + reviewInfo.describeContents());
                Task<Void> task1 = manager.launchReviewFlow(getActivity(), reviewInfo);
                task1.addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        Void vo = task2.getResult();
                        System.out.println("vo = " + task2.getResult());
                    } else {
                        System.out.println("task2.getException().getMessage() = " + task2.getException().getMessage());
                    }
                });
            } else {
                // There was some problem, log or handle the error code.
                String reviewErrorCode = task.getException().getMessage();
                System.out.println("reviewErrorCode = " + reviewErrorCode);
            }
        });
    }

    private void showCallDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Call Confirmation");
        builder.setMessage("Do you want to call " + "+918929000444" + "?");

        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "Call," so initiate the call
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + "+918929000444"));
                startActivity(intent);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "Cancel," so do nothing
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showOpenLinkDialog(String link) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Open Link");
        builder.setMessage("Do you want to open this \"" + link + "\" link?");
        builder.setPositiveButton("Open", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "Open," so open the link in a web browser
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link));
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "Cancel," so do nothing
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}