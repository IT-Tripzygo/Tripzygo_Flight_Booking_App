package in.tripzygo.tripzygoflightbookingapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.tripzygo.tripzygoflightbookingapp.Fragments.PolicyDialog;
import in.tripzygo.tripzygoflightbookingapp.Modals.FlightDetails;
import in.tripzygo.tripzygoflightbookingapp.Tools.Sort;

public class FareAdapter extends RecyclerView.Adapter<FareAdapter.ViewHolder> {
    List<JsonObject> jsonObjects;
    Context context;
    FlightDetails flightDetails ,returnFlightDetails;
    FragmentManager fragmentManager;
    String type;
    JsonObject paxInFo;
    boolean isReturn;
    String id1;


    public FareAdapter(List<JsonObject> jsonObjects, Context context, FlightDetails flightDetails, FragmentManager fragmentManager, String type, JsonObject paxInFo, boolean isReturn) {
        this.jsonObjects = jsonObjects;
        this.context = context;
        this.flightDetails = flightDetails;
        this.fragmentManager = fragmentManager;
        this.type = type;
        this.paxInFo = paxInFo;
        this.isReturn = isReturn;
    }

    public FareAdapter(List<JsonObject> jsonObjects, Context context, FlightDetails flightDetails,FlightDetails returnFlightDetails, FragmentManager fragmentManager, String type, JsonObject paxInFo, boolean isReturn, String id1) {
        this.jsonObjects = jsonObjects;
        this.context = context;
        this.flightDetails = flightDetails;
        this.returnFlightDetails = returnFlightDetails;
        this.fragmentManager = fragmentManager;
        this.type = type;
        this.paxInFo = paxInFo;
        this.isReturn = isReturn;
        this.id1 = id1;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fare_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Gson gson = new Gson();
        JsonArray sI = gson.fromJson(flightDetails.getSI(), new TypeToken<JsonArray>() {
        }.getType());
        JsonArray totalPriceList = gson.fromJson(flightDetails.getTotalPriceList(), new TypeToken<JsonArray>() {
        }.getType());
        List<JsonObject> jsonObjects = new ArrayList<>();
        for (JsonElement jsonElement : totalPriceList) {
            JsonObject jsonObject2 = jsonElement.getAsJsonObject();
            jsonObjects.add(jsonObject2);
        }
        jsonObjects.sort(new Sort());
        JsonObject jsonObject = jsonObjects.get(position);
        Locale locale = new Locale.Builder().setLanguage("en").setRegion("IN").build();
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        int d = jsonObject.getAsJsonObject("fd").getAsJsonObject("ADULT").getAsJsonObject("fC").get("TF").getAsInt();
        String currency = formatter.format(d);
        int centsIndex = currency.lastIndexOf(".00");
        if (centsIndex != -1) {
            currency = currency.substring(0, centsIndex);
        }
        holder.price.setText(currency);
        System.out.println("currency = " + currency);
        holder.FareIdentifier.setText(jsonObject.get("fareIdentifier").getAsString());
        holder.Baggage.setText(jsonObject.getAsJsonObject("fd").getAsJsonObject("ADULT").getAsJsonObject("bI").get("cB").getAsString() + ", " + jsonObject.getAsJsonObject("fd").getAsJsonObject("ADULT").getAsJsonObject("bI").get("iB").getAsString());
        if (jsonObject.getAsJsonObject("fd").getAsJsonObject("ADULT").has("rT")) {
            ColorStateList colorStateList;
            if (jsonObject.getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 0) {
                holder.FareType.setText("NR");
                colorStateList = ColorStateList.valueOf(context.getResources().getColor(R.color.red));
                holder.FareType.setBackgroundTintList(colorStateList);
            } else if (jsonObject.getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 1) {
                holder.FareType.setText("RF");
                colorStateList = ColorStateList.valueOf(context.getResources().getColor(R.color.forestGreen));
                holder.FareType.setBackgroundTintList(colorStateList);
            } else if (jsonObject.getAsJsonObject("fd").getAsJsonObject("ADULT").get("rT").getAsInt() == 2) {
                holder.FareType.setText("PR");
                colorStateList = ColorStateList.valueOf(context.getResources().getColor(R.color.green));
                holder.FareType.setBackgroundTintList(colorStateList);
            }
        }
        if (jsonObject.getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString().equalsIgnoreCase("ECONOMY")) {
            holder.classType.setText("EC");
        } else if (jsonObject.getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString().equalsIgnoreCase("Premium_Economy")) {
            holder.classType.setText("PE");
        } else if (jsonObject.getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString().equalsIgnoreCase("Business")) {
            holder.classType.setText("BS");
        } else if (jsonObject.getAsJsonObject("fd").getAsJsonObject("ADULT").get("cc").getAsString().equalsIgnoreCase("First")) {
            holder.classType.setText("FC");
        }
        holder.itemView.setOnClickListener(view -> {
            String id = jsonObject.getAsJsonObject().get("id").getAsString();
            if (isReturn) {
                List<String> stringList = new ArrayList<>();
                stringList.add(id1);
                stringList.add(id);
                Bundle bundle = new Bundle();
                bundle.putString("paxInfo", String.valueOf(paxInFo));
                bundle.putString("priceIds", String.valueOf(stringList));
                bundle.putString("type", type);
                bundle.putSerializable("flightDetails", returnFlightDetails);
                bundle.putSerializable("returnFlightDetails", flightDetails);
                context.startActivity(new Intent(context, ReturnFlightCheckoutActivity.class).putExtra("bundle", bundle));
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("paxInfo", String.valueOf(paxInFo));
                bundle.putString("type", type);
                bundle.putString("id", id);
                bundle.putSerializable("flightDetails", flightDetails);
                context.startActivity(new Intent(context, FlightCheckoutActivity.class).putExtra("bundle", bundle));
            }
        });
        holder.FareRules.setOnClickListener(view -> {
            String priceId = jsonObject.getAsJsonObject().get("id").getAsString();
            String s = sI.get(0).getAsJsonObject().getAsJsonObject("da").get("cityCode").getAsString() + "-" + sI.get(sI.size() - 1).getAsJsonObject().getAsJsonObject("aa").get("cityCode").getAsString();
            String D = sI.get(0).getAsJsonObject().getAsJsonObject("da").get("city").getAsString();
            String A = sI.get(sI.size() - 1).getAsJsonObject().getAsJsonObject("aa").get("city").getAsString();
            PolicyDialog dialog = new PolicyDialog(context, priceId, s, D, A);
            dialog.setCancelable(false);
            dialog.show(fragmentManager, "show");

        });
    }

    @Override
    public int getItemCount() {
        return jsonObjects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView FareIdentifier, price, FareRules, Baggage, FareType, classType;
        ImageView fareColor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            FareIdentifier = itemView.findViewById(R.id.FareIdentifier);
            price = itemView.findViewById(R.id.priceFare);
            FareRules = itemView.findViewById(R.id.FareRules);
            Baggage = itemView.findViewById(R.id.Baggage);
            FareType = itemView.findViewById(R.id.FareTypeFareLayout);
            classType = itemView.findViewById(R.id.classTypeFareLayout);
            fareColor = itemView.findViewById(R.id.fareColor);

        }
    }
}
