package in.tripzygo.tripzygoflightbookingapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;

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
    private Uri outputUri;

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
//            startCameraWithUri();
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
//        ImagePicker.with(ProfileActivity.this)
//                .crop()                    //Crop image(Optional), Check Customization for more option
//                .compress(1024)            //Final image size will be less than 1 MB(Optional)
//                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
//                .start();
       /* CropImageContractOptions options = new CropImageContractOptions(outputUri, new CropImageOptions())
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
                .setGuidelinesColor(R.color.black)
                .setBackgroundColor(R.color.black)
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
                .setCropMenuCropButtonTitle("crop")
                .setCropMenuCropButtonIcon(R.drawable.button_login)
                .setAllowRotation(true)
                .setNoOutputImage(false)
                .setFixAspectRatio(true);
        cropImage.launch(options);*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            // Image Uri will not be null for RESULT_OK
            path = data.getData();
            // Use Uri object instead of File to avoid storage permissions
            circleImageView.setImageURI(path);
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
//        else if (resultCode == ImagePicker.RESULT_ERROR) {
//            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
//        }
    }
}