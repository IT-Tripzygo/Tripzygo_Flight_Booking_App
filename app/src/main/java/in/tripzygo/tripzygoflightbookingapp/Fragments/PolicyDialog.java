package in.tripzygo.tripzygoflightbookingapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.tripzygo.tripzygoflightbookingapp.R;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.ApiInterface;
import in.tripzygo.tripzygoflightbookingapp.Retroapi.Retrofitt;
import in.tripzygo.tripzygoflightbookingapp.ViewPager2PolicyAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PolicyDialog extends BottomSheetDialogFragment {
    Context context;
    String priceId, desArrString, dep, arr;


    public PolicyDialog() {
    }

    public PolicyDialog(Context context, String priceId) {
        this.context = context;
        this.priceId = priceId;
    }

    public PolicyDialog(Context context, String priceId, String desArrString) {
        this.context = context;
        this.priceId = priceId;
        this.desArrString = desArrString;
    }

    public PolicyDialog(Context context, String priceId, String desArrString, String dep, String arr) {
        this.context = context;
        this.priceId = priceId;
        this.desArrString = desArrString;
        this.dep = dep;
        this.arr = arr;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        BottomSheetBehavior bottomSheetBehavior = dialog.getBehavior();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        // Do something with your dialog like setContentView() or whatever
        return dialog;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.description_dialog, null);
        dialog.setContentView(view);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.description_dialog, container,
                false);
        RelativeLayout layout = view.findViewById(R.id.sheetRelative);
        Button Continue = view.findViewById(R.id.cancel_button);
        Continue.setTextColor(Color.WHITE);
        TabLayout tabLayout = view.findViewById(R.id.tablay);
        ViewPager2 viewPager2 = view.findViewById(R.id.pager);
        JSONObject jsonObject = new JSONObject();
        try {
            Gson gson = new Gson();
            jsonObject.put("id", priceId);
            jsonObject.put("flowType", "SEARCH");
            JsonObject jsonObject1 = gson.fromJson(String.valueOf(jsonObject), new TypeToken<JsonObject>() {
            }.getType());
            ApiInterface apiInterface = Retrofitt.initretro().create(ApiInterface.class);
            Call<JsonObject> call = apiInterface.getFareRule(jsonObject1);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonObject object = response.body();
                        System.out.println(desArrString);
                        JsonObject fr = new JsonObject();
                        System.out.println("fr =" + gson.toJson(fr));
                        System.out.println("object = " + object);
                        if (object.getAsJsonObject("fareRule").getAsJsonObject(desArrString).has("fr")) {
                            fr = object.getAsJsonObject("fareRule").getAsJsonObject(desArrString).getAsJsonObject("fr");
                        }
                        tabLayout.addTab(tabLayout.newTab().setText("Cancellation"));
                        tabLayout.addTab(tabLayout.newTab().setText("Date Change"));
                        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                        ArrayList<String> strings = new ArrayList<>();
                        strings.add("Cancellation");
                        strings.add("Date Change");
                        ViewPager2PolicyAdapter viewPagerAdapter = new ViewPager2PolicyAdapter(getChildFragmentManager(), tabLayout.getTabCount(), getLifecycle(), getContext(), fr, dep, arr);
                        viewPager2.setAdapter(viewPagerAdapter);
                        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2,
                                (TabLayout.Tab tab, int position) -> tab.setText(strings.get(position))
                        );
                        tabLayoutMediator.attach();
                        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                            @Override
                            public void onTabSelected(TabLayout.Tab tab) {
                                viewPager2.setCurrentItem(tab.getPosition());
                            }

                            @Override
                            public void onTabUnselected(TabLayout.Tab tab) {

                            }

                            @Override
                            public void onTabReselected(TabLayout.Tab tab) {

                            }
                        });
                    } else {
                        System.out.println("response review = " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    System.out.println("t = " + t.getMessage());

                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        BottomSheetBehavior bottomSheetBehavior = new BottomSheetBehavior();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        Continue.setOnClickListener(v2 -> {
            getDialog().dismiss();

        });
        return view;

    }
}
