package in.tripzygo.tripzygoflightbookingapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.gson.JsonObject;

import in.tripzygo.tripzygoflightbookingapp.Fragments.CancellationFragment;
import in.tripzygo.tripzygoflightbookingapp.Fragments.DateChangeFragment;

public class ViewPager2PolicyAdapter extends FragmentStateAdapter {
    int mNumOfTabs;
    Context context;
    JsonObject fr;
    String Dep, Arr;

    public ViewPager2PolicyAdapter(@NonNull FragmentManager fragment, int NumOfTabs, Lifecycle lifecycle, Context context, JsonObject fr, String dep, String arr) {
        super(fragment, lifecycle);
        this.mNumOfTabs = NumOfTabs;
        this.context = context;
        this.fr = fr;
        this.Dep = dep;
        this.Arr = arr;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        JsonObject CANCELLATION, DATECHANGE;
        if (fr.equals(new JsonObject())) {
            CANCELLATION = new JsonObject();
            DATECHANGE = new JsonObject();
        } else {
            if (fr.has("CANCELLATION")) {
                CANCELLATION = fr.getAsJsonObject("CANCELLATION");
            } else {
                CANCELLATION = new JsonObject();
            }
            if (fr.has("DATECHANGE")) {
                DATECHANGE = fr.getAsJsonObject("DATECHANGE");
            } else {
                DATECHANGE = new JsonObject();
            }
        }
        switch (position) {
            case 0:
                CancellationFragment cancellationFragment = new CancellationFragment(CANCELLATION, Dep, Arr);
                return cancellationFragment;
            case 1:
                DateChangeFragment dateChangeFragment = new DateChangeFragment(DATECHANGE, Dep, Arr);
                return dateChangeFragment;
            default:
                CancellationFragment cancellationFragment1 = new CancellationFragment(CANCELLATION, Dep, Arr);
                return cancellationFragment1;
        }
    }

    @Override
    public int getItemCount() {
        return mNumOfTabs;
    }
}
