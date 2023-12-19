package in.tripzygo.tripzygoflightbookingapp;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

import it.mirko.rangeseekbar.OnRangeSeekBarListener;
import it.mirko.rangeseekbar.RangeSeekBar;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.MyViewHolder> {
    private final HashMap<Integer, Filter> filters;
    private final RecyclerView filterValuesRV, filterRv;
    RelativeLayout relativeLayout;
    RangeSeekBar priceSeekBar;
    LinearLayout filterValuesContainer;
    private int selectedPosition = 0;
    public FilterAdapter(HashMap<Integer, Filter> filters, RecyclerView filterValuesRV, RecyclerView filterRv, RelativeLayout relativeLayout, RangeSeekBar priceSeekBar, LinearLayout filterValuesContainer) {
        this.filters = filters;
        this.filterValuesRV = filterValuesRV;
        this.filterRv = filterRv;
        this.relativeLayout = relativeLayout;
        this.priceSeekBar = priceSeekBar;
        this.filterValuesContainer = filterValuesContainer;
    }

    public FilterAdapter(HashMap<Integer, Filter> filters, RecyclerView filterValuesRV, RecyclerView filterRv) {
        this.filters = filters;
        this.filterValuesRV = filterValuesRV;
        this.filterRv = filterRv;
    }

    public FilterAdapter(HashMap<Integer, Filter> filters, RecyclerView filterValuesRV, RecyclerView filterRv, RangeSeekBar priceSeekBar, LinearLayout filterValuesContainer) {
        this.filters = filters;
        this.filterValuesRV = filterValuesRV;
        this.filterRv = filterRv;
        this.priceSeekBar = priceSeekBar;
        this.filterValuesContainer = filterValuesContainer;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filter_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, @SuppressLint("RecyclerView") final int position) {
        myViewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterValuesRV.setAdapter(new FilterValuesAdapter(position));
                if (position == Filter.INDEX_PRICE) {
                    showSeekBar();
                } else {
                    showFilterValuesRV();
                }
                selectedPosition = position;
                notifyDataSetChanged();
            }
        });
        showFilterValuesRV();
        filterValuesRV.setAdapter(new FilterValuesAdapter(selectedPosition));
        myViewHolder.container.setBackgroundColor(selectedPosition == position ? Color.WHITE : Color.TRANSPARENT);
        myViewHolder.title.setText(filters.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return filters.size();
    }

    private void showFilterValuesRV() {
        filterValuesContainer.removeAllViews();
        filterValuesContainer.addView(filterRv);
        filterValuesContainer.addView(filterValuesRV);
        filterValuesRV.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.GONE);
    }

    private void showSeekBar() {
        filterValuesContainer.removeAllViews();
        filterValuesContainer.addView(filterRv);
        relativeLayout.setVisibility(View.VISIBLE);
        priceSeekBar.setVisibility(View.VISIBLE);
        filterValuesContainer.addView(relativeLayout);
        relativeLayout.removeAllViews();
        relativeLayout.addView(priceSeekBar);
        filterValuesRV.setVisibility(View.GONE);
        priceSeekBar.setOnRangeSeekBarListener(new OnRangeSeekBarListener() {
            @Override
            public void onRangeValues(RangeSeekBar rangeSeekBar, int start, int end) {

            }
        });
        // You can customize SeekBar attributes here
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        final View container;
        final TextView title;

        MyViewHolder(View view) {
            super(view);
            container = view;
            title = view.findViewById(R.id.title);
        }
    }
}
