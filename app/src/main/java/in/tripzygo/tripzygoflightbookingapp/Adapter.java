package in.tripzygo.tripzygoflightbookingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import in.tripzygo.tripzygoflightbookingapp.Modals.FlightDetails;
import in.tripzygo.tripzygoflightbookingapp.Modals.Modelclass;

public class Adapter extends RecyclerView.Adapter<Adapter.Viewolder> {

    private final List<Modelclass> userlist;
    List<FlightDetails> flightDetailsList;
    JSONArray flightDetailsList1;
    public boolean isAllChecked = false;
    Context context;


    public Adapter(List<Modelclass> userlist) {
        this.userlist = userlist;
    }


    public Adapter(List<Modelclass> userlist, List<FlightDetails> flightDetailsList, JSONArray flightDetailsList1, Context context) {
        this.userlist = userlist;
        this.flightDetailsList = flightDetailsList;
        this.flightDetailsList1 = flightDetailsList1;
        this.context = context;
    }

    @NonNull
    @Override
    public Viewolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemdesign, parent, false);
        return new Viewolder(v);


    }


    @SuppressLint("RecyclerView")

    public void setAllChecked() {
        isAllChecked = true;
        notifyDataSetChanged();


    }

    public void unselectAll() {
        isAllChecked = false;
        flightDetailsList1 = new JSONArray();
        notifyDataSetChanged();
    }

    public JSONArray getFlightDetailsList1() {
        return flightDetailsList1;
    }

    @Override
    public void onBindViewHolder(@NonNull Viewolder holder, int position) {
        holder.text1.setText(userlist.get(position).getName());
        holder.text2.setText(userlist.get(position).getPrice());
        if (userlist.get(position).isCheck()) {
            holder.checkk.setChecked(userlist.get(position).isCheck());
        } else {
            holder.checkk.setChecked(isAllChecked);
        }
        holder.checkk.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("code", userlist.get(position).getImage());
                    flightDetailsList1.put(jsonObject);


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("flightDetailsList1 = " + flightDetailsList1);
            } else {
                Gson gson = new Gson();
                List<JsonObject> jsonObjects = gson.fromJson(String.valueOf(flightDetailsList1), new TypeToken<List<JsonObject>>() {
                }.getType());
                List<JsonObject> jsonObjects1 = gson.fromJson(String.valueOf(flightDetailsList1), new TypeToken<List<JsonObject>>() {
                }.getType());
                try {
                    for (JsonObject jsonObject : jsonObjects) {
                        if (jsonObject.get("code").getAsString().matches(userlist.get(position).getImage())) {
                            jsonObjects1.remove(jsonObject);

//                        flightDetailsList1 = gson.fromJson(String.valueOf(jsonObjects), new TypeToken<JSONArray>() {
//                        }.getType());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("jsonObjects1 = " + jsonObjects1);
            }
        });
        System.out.println("userlist = " + userlist.get(position).getImage());
        StorageReference storageReference1 = FirebaseStorage.getInstance().getReference().child("AirlineLogos").child(userlist.get(position).getImage() + ".png");
        storageReference1.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(context).load(uri).into(holder.img);
        }).addOnFailureListener(e -> {
            System.out.println("storageReference1 = " + storageReference1);
            System.out.println("e.getMessage() = " + e.getMessage());
        });

    }

    @Override
    public int getItemCount() {
        return userlist.size();
    }

    public class Viewolder extends RecyclerView.ViewHolder {

        private final ImageView img;

        private final TextView text1;
        private final TextView text2;
        private final CheckBox checkk;

        public Viewolder(@NonNull View itemView) {


            super(itemView);
            img = itemView.findViewById(R.id.imggg);
            text1 = itemView.findViewById(R.id.indigo);
            text2 = itemView.findViewById(R.id.prrice);
            checkk = itemView.findViewById(R.id.checbox);


        }
    }
}
