package in.tripzygo.tripzygoflightbookingapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import in.tripzygo.tripzygoflightbookingapp.Modals.Traveller;

public class InfantAdapter extends RecyclerView.Adapter<InfantAdapter.MyViewHolder> {

    int count;
    Context ctx;
    List<Traveller> list;
    String type;

    public InfantAdapter(int adultCount, Context context, List<Traveller> list, String type) {
        this.count = adultCount;
        this.ctx = context;
        this.list = list;
        this.type = type;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.infant_item_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.title.setText("Infant " + (position + 1));
        Traveller traveller = new Traveller();
        final String[] dob = new String[1];
        holder.calendarChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar mCalendar = Calendar.getInstance();
                final DatePickerDialog mDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(mYear, mMonth, mDay);
                        dob[0] = sdf.format(calendar.getTime());
                        holder.DOB.setText(dob[0]);
                        traveller.setDob(dob[0]);
                    }
                },
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH));

                Calendar cal = Calendar.getInstance();
                Log.e("CALENDAR DATE", cal.getTime().getDate() + " " + cal.getTime().getMonth() + " " + (cal.getWeekYear() - 2));
                Log.e("CALENDAR DATE", cal.getTime().getDate() + " " + cal.getTime().getMonth() + " " + (cal.getWeekYear()));

                final int minDay = cal.getTime().getDate();
                final int minMonth = cal.getTime().getMonth();
                final int minYear = cal.getWeekYear() - 2;
                mCalendar.set(minYear, minMonth, minDay);
                mDialog.getDatePicker().setMinDate(mCalendar.getTimeInMillis());
                final int maxDay = cal.getTime().getDate();
                final int maxMonth = cal.getTime().getMonth();
                final int maxYear = cal.getWeekYear();
                mCalendar.set(maxYear, maxMonth, maxDay);
                mDialog.getDatePicker().setMaxDate(mCalendar.getTimeInMillis());

                mDialog.show();

            }
        });
        if (type.equalsIgnoreCase("International")) {
            holder.passportNoEditText.setVisibility(View.VISIBLE);
            holder.passportNoEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String passportNo = editable.toString();
                    if (!passportNo.isEmpty()) {
                        traveller.setPassportNo(passportNo);
                    }else {
                        holder.passportNoEditText.setError("Please Enter Passport Number");
                    }
                }
            });
        }
        final String[] title = {""};
        holder.radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.masterInfant:
                    title[0] = holder.masterRadioButton.getText().toString();
                    traveller.setTitle(title[0]);
                    break;
                case R.id.missInfant:
                    title[0] = holder.missRadioButton.getText().toString();
                    traveller.setTitle(title[0]);
                    break;
            }
        });
        holder.firstNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                String firstname = editable.toString();
                if (!firstname.isEmpty()) {
                    traveller.setFirstName(firstname);
                } else {
                    holder.firstNameEditText.setError("Please Enter first name");
                }            }
        });
        holder.lastNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                String lastname = editable.toString();
                if (!lastname.isEmpty()) {
                    traveller.setLastName(lastname);
                } else {
                    holder.lastNameEditText.setError("Please Enter last name");
                }

            }
        });

        traveller.setType("INFANT");
        list.add(traveller);
    }

    @Override
    public int getItemCount() {
        return count;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, DOB;
        ConstraintLayout calendarChoose;
        ImageView calendar;
        EditText firstNameEditText, lastNameEditText, passportNoEditText;
        RadioGroup radioGroup;
        RadioButton masterRadioButton, missRadioButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.TravellerTypeInfant);
            calendar = itemView.findViewById(R.id.calenderImageDOBInfant);
            DOB = itemView.findViewById(R.id.infantDOB);
            calendarChoose = itemView.findViewById(R.id.InfantCalendar);
            firstNameEditText = itemView.findViewById(R.id.FirstNameInfant);
            lastNameEditText = itemView.findViewById(R.id.LastNameInfant);
            radioGroup = itemView.findViewById(R.id.radioGroup2Infant);
            masterRadioButton = itemView.findViewById(R.id.masterInfant);
            missRadioButton = itemView.findViewById(R.id.missInfant);
            passportNoEditText = itemView.findViewById(R.id.pNoInfant);
        }
    }

    public List<Traveller> getSelectedTravellerList() {
        return list;
    }
}
