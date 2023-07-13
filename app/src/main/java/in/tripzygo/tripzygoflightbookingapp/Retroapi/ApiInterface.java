package in.tripzygo.tripzygoflightbookingapp.Retroapi;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @POST("fms/v1/air-search-all")
    Call<JsonObject> getFlights(@Body JsonObject jsonObject);

    @POST("fms/v1/air-search-all")
    Call<JsonObject> getReturnFlights(@Body JsonObject jsonObject);

    @POST("fms/v1/review")
    Call<JsonObject> getReview(@Body JsonObject jsonObject);

    @POST("seat")
    Call<JsonObject> getSeats(@Body JsonObject jsonObject);

    @POST("oms/v1/air/book")
    Call<JsonObject> block(@Body JsonObject jsonObject);

    @POST("oms/v1/booking-details")
    Call<JsonObject> retrieveBooking(@Body JsonObject jsonObject);

    @POST("oms/v1/air/confirm-book")
    Call<JsonObject> book(@Body JsonObject jsonObject);

    @FormUrlEncoded
    @POST("initiateLink")
    Call<JsonObject> getAccessKey(@Field("key") String key, @Field("txnid") String txnid, @Field("amount") float amount, @Field("productinfo") String productinfo, @Field("firstname") String firstname, @Field("phone") Long phone, @Field("email") String email, @Field("surl") String surl, @Field("furl") String furl, @Field("hash") String hash, @Field("show_payment_mode") String show_payment_mode);
}
