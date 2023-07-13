package in.tripzygo.tripzygoflightbookingapp;

import com.google.gson.JsonObject;

public class Seat {
    String seatNo, code;
    float amount, ctds;
    Boolean isBooked, isLegroom, isAisle;
    JsonObject seatPosition;

    public String getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getCtds() {
        return ctds;
    }

    public void setCtds(float ctds) {
        this.ctds = ctds;
    }

    public Boolean getBooked() {
        return isBooked;
    }

    public void setBooked(Boolean booked) {
        isBooked = booked;
    }

    public Boolean getLegroom() {
        return isLegroom;
    }

    public void setLegroom(Boolean legroom) {
        isLegroom = legroom;
    }

    public Boolean getAisle() {
        return isAisle;
    }

    public void setAisle(Boolean aisle) {
        isAisle = aisle;
    }

    public JsonObject getSeatPosition() {
        return seatPosition;
    }

    public void setSeatPosition(JsonObject seatPosition) {
        this.seatPosition = seatPosition;
    }
}
