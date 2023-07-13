package in.tripzygo.tripzygoflightbookingapp;

import static android.graphics.Color.WHITE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import in.tripzygo.tripzygoflightbookingapp.Modals.User;
import in.tripzygo.tripzygoflightbookingapp.Tools.CustomToast;

public class ProfileActivity extends AppCompatActivity {
    static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    static final String FILE_NAMING_PREFIX = "JPEG_";
    static final String FILE_NAMING_SUFFIX = "_";
    static final String FILE_FORMAT = ".jpg";
    static final String AUTHORITY_SUFFIX = ".cropper.fileprovider";
    CircleImageView circleImageView;
    Uri path;
    Bitmap bitmap;
    ImageView editImageView;
    TextInputEditText nameInputEditText, emailInputEditText;
    EditText phoneInputEditText;
    CountryCodePicker countryCodePicker;
    Button startButton;
    String mobileNo, nameCode, phone, Code, name, email, imageUrl;
    boolean phoneExisted;
    StorageReference storageReference;
    User user = new User();
    private final ActivityResultLauncher<CropImageContractOptions> cropImage =
            registerForActivityResult(new CropImageContract(), this::onCropImageResult);
    private Uri outputUri;
    private final ActivityResultLauncher<Uri> takePicture =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), this::onTakePictureResult);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        storageReference = FirebaseStorage.getInstance().getReference().child("User Images");
        circleImageView = findViewById(R.id.register_image);
        editImageView = findViewById(R.id.edit_image);
        nameInputEditText = findViewById(R.id.profile_name);
        emailInputEditText = findViewById(R.id.profile_email);
        phoneInputEditText = findViewById(R.id.profile_phone);
        countryCodePicker = findViewById(R.id.country_profile);
        startButton = findViewById(R.id.start_profile);
        phone = getIntent().getStringExtra("mobileNo");
        nameCode = getIntent().getStringExtra("code");
        phoneExisted = getIntent().getBooleanExtra("phoneExisted", false);
        countryCodePicker.setCountryForNameCode(nameCode);
        phoneInputEditText.setText(phone);
        editImageView.setOnClickListener(v -> {
            startCameraWithUri();
        });
        startButton.setOnClickListener(v -> {
            Code = countryCodePicker.getSelectedCountryCodeWithPlus();
            String Code1 = countryCodePicker.getSelectedCountryCode();
            String mob = phoneInputEditText.getText().toString();
            name = nameInputEditText.getText().toString();
            email = emailInputEditText.getText().toString();
            if (mob.length() != 10) {
                phoneInputEditText.setError("Please Enter Valid Phone Number ");
                phoneInputEditText.requestFocus();
            } else if (name.isEmpty()) {
                nameInputEditText.setError("Please Enter Your Name");
                nameInputEditText.requestFocus();
            } else if (!email.contains("@")) {
                emailInputEditText.setError("Please Enter Email");
                emailInputEditText.requestFocus();
            } else {
                mobileNo = Code + mob;
                user.setCode(Code1);
                user.setMobileNo(Long.parseLong(mob));
                user.setUser_name(name);
                user.setPhone_no(mobileNo);
                user.setEmail_id(email);
//                user.setProfile_img("");
                Intent intent;
                intent = new Intent(ProfileActivity.this, OtpActivity.class);
                intent.putExtra("mobileNo", mobileNo);
                intent.putExtra("phoneExisted", phoneExisted);
                intent.putExtra("user", user);
                startActivity(intent);
                finish();
            }
        });
    }

    public void startCameraWithUri() {
        CropImageContractOptions options = new CropImageContractOptions(outputUri, new CropImageOptions())
                .setScaleType(CropImageView.ScaleType.FIT_CENTER)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .setAspectRatio(1, 1)
                .setMaxZoom(4)
                .setAutoZoomEnabled(true)
                .setMultiTouchEnabled(true)
                .setCenterMoveEnabled(true)
                .setShowCropOverlay(true)
                .setAllowFlipping(true)
                .setSnapRadius(3f)
                .setTouchRadius(48f)
                .setInitialCropWindowPaddingRatio(0.1f)
                .setBorderLineThickness(3f)
                .setBorderLineColor(Color.argb(170, 255, 255, 255))
                .setBorderCornerThickness(2f)
                .setBorderCornerOffset(5f)
                .setBorderCornerLength(14f)
                .setBorderCornerColor(WHITE)
                .setGuidelinesThickness(1f)
                .setGuidelinesColor(R.color.white)
                .setBackgroundColor(Color.argb(119, 0, 0, 0))
                .setMinCropWindowSize(24, 24)
                .setMinCropResultSize(20, 20)
                .setMaxCropResultSize(99999, 99999)
                .setActivityTitle("")
                .setActivityMenuIconColor(0)
                .setOutputUri(null)
                .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                .setOutputCompressQuality(90)
                .setRequestedSize(0, 0)
                .setRequestedSize(0, 0, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                .setInitialCropWindowRectangle(null)
                .setInitialRotation(0)
                .setAllowCounterRotation(false)
                .setFlipHorizontally(false)
                .setFlipVertically(false)
                .setCropMenuCropButtonTitle(null)
                .setCropMenuCropButtonIcon(0)
                .setAllowRotation(true)
                .setNoOutputImage(false)
                .setFixAspectRatio(false);
        cropImage.launch(options);

    }

    public void showErrorMessage(@NotNull String message) {
        Log.e("Camera Error:", message);
        Toast.makeText(this, "Crop failed: " + message, Toast.LENGTH_SHORT).show();
    }

    private void startTakePicture() {
        try {
            Context ctx = getApplicationContext();
            String authorities = ctx.getPackageName() + AUTHORITY_SUFFIX;
            outputUri = FileProvider.getUriForFile(ctx, authorities, createImageFile());
            takePicture.launch(outputUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleCropImageResult(@NotNull String uri) {
        path = Uri.parse(uri);
        System.out.println("path = " + path);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
            circleImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        StorageReference filepath = storageReference.child("image" + phone + path.getLastPathSegment());

        final UploadTask uploadTask = filepath.putFile(path);
        ProgressDialog loadingBar;
        loadingBar = new ProgressDialog(this);
        loadingBar.show();
        uploadTask.addOnFailureListener(e -> {
            loadingBar.dismiss();
            CustomToast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT, Color.RED);
        }).addOnSuccessListener(taskSnapshot -> {

            Task<Uri> uriTask = uploadTask.continueWithTask(task -> {

                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return filepath.getDownloadUrl();

            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {
                        loadingBar.dismiss();
                        imageUrl = task.getResult().toString();
                        user.setProfile_img(imageUrl);
                        System.out.println("url " + task.getResult().toString());
                        CustomToast.makeText(ProfileActivity.this, "Image uploaded Successfully...", Toast.LENGTH_SHORT, Color.parseColor("#32CD32"));

                    }
                }
            });
        });
    }

    private File createImageFile() throws IOException {
        SimpleDateFormat timeStamp = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                FILE_NAMING_PREFIX + timeStamp + FILE_NAMING_SUFFIX,
                FILE_FORMAT,
                storageDir
        );
    }

    public void onCropImageResult(@NonNull CropImageView.CropResult result) {
        if (result.isSuccessful()) {
            handleCropImageResult(Objects.requireNonNull(result.getUriContent())
                    .toString()
                    .replace("file:", ""));
        } else if (result.equals(CropImage.CancelledResult.INSTANCE)) {
            showErrorMessage("cropping image was cancelled by the user");
        } else {
            showErrorMessage("cropping image failed");
        }
    }

    public void onTakePictureResult(boolean success) {
        if (success) {
            startCameraWithUri();
        } else {
            showErrorMessage("taking picture failed");
        }
    }
}