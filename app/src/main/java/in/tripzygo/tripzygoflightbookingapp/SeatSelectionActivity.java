package in.tripzygo.tripzygoflightbookingapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import in.tripzygo.tripzygoflightbookingapp.Retroapi.ApiInterface;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.Retrofitt;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeatSelectionActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private static final int COLUMNS = 7;
    private TextView txtSeatSelected;
    List<Seat> seats = new ArrayList<>();
    String bookingId, segmentKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);
        bookingId = getIntent().getStringExtra("bookingId");
        segmentKey = getIntent().getStringExtra("segmentKey");
        ApiInterface apiInterface = Retrofitt.initretro().create(ApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("bookingId", bookingId);
        Call<JsonObject> call = apiInterface.getSeats(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    if (body != null) {
                        JsonArray array = jsonObject.getAsJsonObject("tripSeatMap").getAsJsonObject("tripSeat").getAsJsonObject(segmentKey).getAsJsonArray("sInfo");
                        Gson gson = new Gson();
                        seats = gson.fromJson(String.valueOf(array), new TypeToken<List<Seat>>() {
                        }.getType());
                        GridLayoutManager manager = new GridLayoutManager(SeatSelectionActivity.this, COLUMNS);
                        RecyclerView recyclerView = findViewById(R.id.lst_items);
                        recyclerView.setLayoutManager(manager);
                        SeatAdapter adapter = new SeatAdapter(seats, SeatSelectionActivity.this);
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    System.out.println("response = " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }
}