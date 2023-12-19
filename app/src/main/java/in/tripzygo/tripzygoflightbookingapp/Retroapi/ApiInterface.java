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

    @POST("fms/v1/farerule")
    Call<JsonObject> getFareRule(@Body JsonObject jsonObject);

    @POST("seat")
    Call<JsonObject> getSeats(@Body JsonObject jsonObject);

    @POST("oms/v1/air/book")
    Call<JsonObject> block(@Body JsonObject jsonObject);

    @POST("oms/v1/booking-details")
    Call<JsonObject> retrieveBooking(@Body JsonObject jsonObject);

    @POST("oms/v1/air/confirm-book")
    Call<JsonObject> book(@Body JsonObject jsonObject);

    @FormUrlEncoded
    @POST("easebuzz_request.php")
    Call<JsonObject> getAccessKey(@Field("Token") String Token, @Field("txnid") String txnid, @Field("amount") float amount, @Field("productinfo") String productinfo, @Field("firstname") String firstname, @Field("phone") Long phone, @Field("email") String email, @Field("api_name") String api_name);

    @POST("login")
    Call<JsonObject> login(@Body JsonObject jsonObject);
}
