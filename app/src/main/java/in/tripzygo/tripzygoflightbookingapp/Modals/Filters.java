package in.tripzygo.tripzygoflightbookingapp.Modals;

public class Filters {
    int lowPrice, highPrice;
    boolean nonstop, oneStop, twoStop, earlyMorning, morning, afternoon, night, refundable;

    public boolean isRefundable() {
        return refundable;
    }

    public void setRefundable(boolean refundable) {
        this.refundable = refundable;
    }

    public int getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(int lowPrice) {
        this.lowPrice = lowPrice;
    }

    public int getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(int highPrice) {
        this.highPrice = highPrice;
    }

    public boolean isNonstop() {
        return nonstop;
    }

    public void setNonstop(boolean nonstop) {
        this.nonstop = nonstop;
    }

    public boolean isOneStop() {
        return oneStop;
    }

    public void setOneStop(boolean oneStop) {
        this.oneStop = oneStop;
    }

    public boolean isTwoStop() {
        return twoStop;
    }

    public void setTwoStop(boolean twoStop) {
        this.twoStop = twoStop;
    }

    public boolean isEarlyMorning() {
        return earlyMorning;
    }

    public void setEarlyMorning(boolean earlyMorning) {
        this.earlyMorning = earlyMorning;
    }

    public boolean isMorning() {
        return morning;
    }

    public void setMorning(boolean morning) {
        this.morning = morning;
    }

    public boolean isAfternoon() {
        return afternoon;
    }

    public void setAfternoon(boolean afternoon) {
        this.afternoon = afternoon;
    }

    public boolean isNight() {
        return night;
    }

    public void setNight(boolean night) {
        this.night = night;
    }
}
