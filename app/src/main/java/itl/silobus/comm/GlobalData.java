package itl.silobus.comm;

/**
 * Created by Angelo on 30/07/2017.
 */

public class GlobalData {

    private static GlobalData instance;
    private float frontDoorDistance;
    private float backDoorDistance;
    private int panicButtonActivated;
    private int amountPassengers;
    private double actualLongitude;
    private double actualLatitude;
    private float actualDistance;
    private long distanceTime = 0;
    public static synchronized GlobalData getInstance() {
        if (instance == null) instance = new GlobalData();
        return instance;
    }

    public float getFrontDoorDistance() {
        return frontDoorDistance;
    }

    public void setFrontDoorDistance(float frontDoorDistance) {
        this.frontDoorDistance = frontDoorDistance;
    }

    public float getBackDoorDistance() {
        return backDoorDistance;
    }

    public void setBackDoorDistance(float backDoorDistance) {
        this.backDoorDistance = backDoorDistance;
    }

    public int isPanicButtonActivated() {
        return panicButtonActivated;
    }

    public void setPanicButtonActivated(int panicButtonActivated) {
        this.panicButtonActivated = panicButtonActivated;
    }

    public void setAmountPassengers(int amountPassengers) {
        this.amountPassengers = amountPassengers;
    }

    public int getAmountPassengers() {
        return amountPassengers;
    }

    public float getActualDistance() {
        return actualDistance;
    }

    public void setActualDistance(float actualDistance) {
        this.actualDistance = actualDistance;
    }

    public long getDistanceTime() {
        return distanceTime;
    }

    public void setDistanceTime(long distanceTime) {
        this.distanceTime = distanceTime;
    }

    public double getActualLongitude() {
        return actualLongitude;
    }

    public void setActualLongitude(double actualLongitude) {
        this.actualLongitude = actualLongitude;
    }

    public double getActualLatitude() {
        return actualLatitude;
    }

    public void setActualLatitude(double actualLatitude) {
        this.actualLatitude = actualLatitude;
    }
}
