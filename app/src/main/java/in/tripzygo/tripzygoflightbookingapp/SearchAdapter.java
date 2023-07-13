package in.tripzygo.tripzygoflightbookingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import in.tripzygo.tripzygoflightbookingapp.Modals.AirportCode;


public class SearchAdapter extends BaseAdapter {

    private final Context mContext;
    private List<AirportCode> mCountries;
    private LayoutInflater mLayoutInflater;
    private boolean mIsFilterList;
    String url;

    public SearchAdapter(Context context, List<AirportCode> countries, boolean isFilterList) {
        this.mContext = context;
        this.mCountries = countries;
        this.mIsFilterList = isFilterList;
        this.url = url;
    }


    public void updateList(List<AirportCode> filterList, boolean isFilterList) {
        this.mCountries = filterList;
        this.mIsFilterList = isFilterList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mCountries.size();
    }

    @Override
    public AirportCode getItem(int position) {
        return mCountries.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder = null;
        if (v == null) {

            holder = new ViewHolder();

            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = mLayoutInflater.inflate(R.layout.list_item_search, parent, false);
            holder.txtCountry = v.findViewById(R.id.txt_country);
            holder.txtName = v.findViewById(R.id.txt_name);
            v.setTag(holder);
        } else {

            holder = (ViewHolder) v.getTag();
        }
        AirportCode airportCode = mCountries.get(position);
        holder.txtCountry.setText(airportCode.getCode()+" - "+airportCode.getCity()+","+airportCode.getCountry());
        holder.txtName.setText(airportCode.getName());

        return v;
    }

}

class ViewHolder {
    TextView txtCountry,txtName;

}
