package in.tripzygo.tripzygoflightbookingapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kizitonwose.calendar.view.ViewContainer;

public class DayViewContainer1 extends ViewContainer {
    public TextView textView;

    public DayViewContainer1(@NonNull View view) {
        super(view);
        textView = view.findViewById(R.id.calendarDayText);
    }

    public void setOnClickListener(View.OnClickListener listener) {

        textView.setOnClickListener(listener);
    }
}

