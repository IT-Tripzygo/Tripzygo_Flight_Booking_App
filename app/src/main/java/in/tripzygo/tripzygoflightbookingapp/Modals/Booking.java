package in.tripzygo.tripzygoflightbookingapp.Modals;

public class Booking {
    String bookingId, tripInfos, departure, phoneNo;

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getTripInfos() {
        return tripInfos;
    }

    public void setTripInfos(String tripInfos) {
        this.tripInfos = tripInfos;
    }
}
