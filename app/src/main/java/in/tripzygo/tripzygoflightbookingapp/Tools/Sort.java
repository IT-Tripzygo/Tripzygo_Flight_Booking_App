package in.tripzygo.tripzygoflightbookingapp.Tools;

import com.google.gson.JsonObject;

import java.util.Comparator;

public class Sort implements Comparator<JsonObject> {


    @Override
    public int compare(JsonObject jsonObject, JsonObject t1) {
        return (int) (jsonObject.getAsJsonObject("fd").getAsJsonObject("ADULT").getAsJsonObject("fC").get("TF").getAsFloat() - t1.getAsJsonObject("fd").getAsJsonObject("ADULT").getAsJsonObject("fC").get("TF").getAsFloat());

    }
}
