package in.tripzygo.tripzygoflightbookingapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import in.tripzygo.tripzygoflightbookingapp.Modals.Traveller;
import in.tripzygo.tripzygoflightbookingapp.Modals.User;
import in.tripzygo.tripzygoflightbookingapp.Tools.SharedPreference;

public class AddNewTravellerActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    TextInputLayout firstNameTextInputLayout, lastNameTextInputLayout, nationalityTextInputLayout;
    Spinner titleSpinner, typeSpinner;
    Button saveButton;
    List<String> stringList, list;
    String title, firstName, lastName, nationality, type;
    TextView DateOfBirth;
    ProgressDialog loadingBar;
    String dob;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_traveller);
        titleSpinner = findViewById(R.id.spinner_prefix);
        typeSpinner = findViewById(R.id.spinner_type);
        firstNameTextInputLayout = findViewById(R.id.first_nameTextInputLayout);
        lastNameTextInputLayout = findViewById(R.id.last_nameTextInputLayout);
        nationalityTextInputLayout = findViewById(R.id.nationalityTextInputLayout);
        DateOfBirth = findViewById(R.id.dateOfBirth);
        saveButton = findViewById(R.id.saveTraveller);
        loadingBar = new ProgressDialog(this);
        list = new ArrayList<>();
        list.add("Adult");
        list.add("Child");
        list.add("Infant");
        ArrayAdapter aa1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, list);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(aa1);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = adapterView.getItemAtPosition(i).toString();
                if (type.matches("Infant")) {
                    stringList = new ArrayList<>();
                    stringList.add("Miss");
                    stringList.add("Mast");
                    ArrayAdapter aa = new ArrayAdapter(AddNewTravellerActivity.this, android.R.layout.simple_spinner_item, stringList);
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    titleSpinner.setAdapter(aa);
                    titleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            title = adapterView.getItemAtPosition(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                            title = adapterView.getItemAtPosition(0).toString();
                        }
                    });
                } else if (type.matches("Adult")) {
                    stringList = new ArrayList<>();
                    stringList.add("Mr");
                    stringList.add("Mrs");
                    stringList.add("Ms");
                    ArrayAdapter aa = new ArrayAdapter(AddNewTravellerActivity.this, android.R.layout.simple_spinner_item, stringList);
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    titleSpinner.setAdapter(aa);
                    titleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            title = adapterView.getItemAtPosition(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                            title = adapterView.getItemAtPosition(0).toString();
                        }
                    });
                } else if (type.matches("Child")) {
                    stringList = new ArrayList<>();
                    stringList.add("Master");
                    stringList.add("Ms");
                    ArrayAdapter aa = new ArrayAdapter(AddNewTravellerActivity.this, android.R.layout.simple_spinner_item, stringList);
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    titleSpinner.setAdapter(aa);
                    titleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            title = adapterView.getItemAtPosition(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                            title = adapterView.getItemAtPosition(0).toString();
                        }
                    });
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        DateOfBirth.setOnClickListener(view -> {
            showDatePickerDialog(type);
        });

        saveButton.setOnClickListener(view -> {
            loadingBar.show();
            firstName = firstNameTextInputLayout.getEditText().getText().toString();
            lastName = lastNameTextInputLayout.getEditText().getText().toString();
            nationality = nationalityTextInputLayout.getEditText().getText().toString();
            Traveller traveller = new Traveller();
            traveller.setFirstName(firstName);
            traveller.setLastName(lastName);
            traveller.setTitle(title);
            traveller.setNationality(nationality);
            traveller.setType(type.toUpperCase());
            traveller.setDob(dob);
            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put(title +" "+ firstName +" "+ lastName, traveller);
            User user = SharedPreference.loadUser(AddNewTravellerActivity.this);
            DatabaseReference reference = FirebaseDatabase.getInstance("https://flightbookingapp-307f0-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users").child(user.getPhone_no()).child("Travellers");
            reference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        loadingBar.dismiss();
                        onBackPressed();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("e = " + e.getMessage());
                }
            });
        });
    }

    private void showDatePickerDialog(String type) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        if (type.matches("Infant")) {
            Date today = Calendar.getInstance().getTime();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDate localDate = LocalDate.now();
                datePickerDialog.getDatePicker().setMaxDate(today.getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(today);
                calendar.set(Calendar.YEAR, localDate.minusYears(2L).getYear());
                System.out.println("calendar = " + calendar.getTime());
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            }

        } else if (type.matches("Child")) {
            Date today = Calendar.getInstance().getTime();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDate localDate = LocalDate.now();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(today);
                calendar.set(Calendar.YEAR, localDate.minusYears(2L).getYear());
                System.out.println("calendar = " + calendar.getTime());
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                calendar.setTime(today);
                calendar.set(Calendar.YEAR, localDate.minusYears(12L).getYear());
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            }
        } else if (type.matches("Adult")) {
            Date today = Calendar.getInstance().getTime();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDate localDate = LocalDate.now();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(today);
                calendar.set(Calendar.YEAR, localDate.minusYears(12L).getYear());
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            }
        }
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        dob = sdf.format(calendar.getTime());
        DateOfBirth.setText(dob);
    }
}