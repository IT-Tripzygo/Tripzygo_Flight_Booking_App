package in.tripzygo.tripzygoflightbookingapp.Modals;

import java.io.Serializable;

public class FlightDetails implements Serializable {
    String AirlineName, AirlineImage, departureTime, ArrivalTime, totalStops, totalPrice;
    int totalTime;
    String SI,totalPriceList;

    public String getSI() {
        return SI;
    }

    public void setSI(String SI) {
        this.SI = SI;
    }

    public String getTotalPriceList() {
        return totalPriceList;
    }

    public void setTotalPriceList(String totalPriceList) {
        this.totalPriceList = totalPriceList;
    }

    public String getAirlineName() {
        return AirlineName;
    }

    public void setAirlineName(String airlineName) {
        AirlineName = airlineName;
    }

    public String getAirlineImage() {
        return AirlineImage;
    }

    public void setAirlineImage(String airlineImage) {
        AirlineImage = airlineImage;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return ArrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        ArrivalTime = arrivalTime;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public String getTotalStops() {
        return totalStops;
    }

    public void setTotalStops(String totalStops) {
        this.totalStops = totalStops;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
