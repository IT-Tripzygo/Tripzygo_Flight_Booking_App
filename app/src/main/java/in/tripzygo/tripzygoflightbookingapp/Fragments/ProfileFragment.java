package in.tripzygo.tripzygoflightbookingapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;
import in.tripzygo.tripzygoflightbookingapp.BuildConfig;
import in.tripzygo.tripzygoflightbookingapp.LoginActivity;
import in.tripzygo.tripzygoflightbookingapp.Modals.User;
import in.tripzygo.tripzygoflightbookingapp.R;
import in.tripzygo.tripzygoflightbookingapp.Tools.SharedPreference;


public class ProfileFragment extends Fragment {
    CircleImageView userCircleImageView;
    TextView nameTextView, emailTextView, editTextView, shareTextView, faqTextView, myBookingTextView, customerServiceTextView, notificationsTextView, rateTextView, versionTextView, privacyTextView;
    Button logoutButton;
    DatabaseReference userReference;
    User user;

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
        shareTextView = view.findViewById(R.id.share);
        faqTextView = view.findViewById(R.id.faq);
        logoutButton = view.findViewById(R.id.logout);
        privacyTextView = view.findViewById(R.id.privacy);
        user = SharedPreference.loadUser(getContext());
        FirebaseApp.initializeApp(getContext());
        userReference = FirebaseDatabase.getInstance("https://flightbookingapp-307f0-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        Glide.with(getContext()).load(user.getProfile_img()).into(userCircleImageView);
        nameTextView.setText(user.getUser_name());
        emailTextView.setText(user.getEmail_id());
        versionTextView.setText("v " + BuildConfig.VERSION_NAME);
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
        return view;
    }
}