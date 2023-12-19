package in.tripzygo.tripzygoflightbookingapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;


public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {
    private final Context context;
    private List<String> mSliderItems = new ArrayList<>();

    public SliderAdapter(Context context, List<String> list){
    this.context= context;
    this.mSliderItems= list;
}

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        String item=mSliderItems.get(position);
        Glide.with(context).load(item).into(viewHolder.imageViewBackground);
    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    public static class SliderAdapterVH extends ViewHolder{
        View itemView;
        ImageView imageViewBackground;


        public SliderAdapterVH(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);


        }
    }
}
