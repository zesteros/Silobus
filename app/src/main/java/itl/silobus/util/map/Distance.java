package itl.silobus.util.map;

/**
 * Created by Angelo on 15/08/2017.
 */

public class Distance {
    private double distance;
    private int pos;

    public Distance(double distance, int pos){
        this.distance = distance;
        this.pos = pos;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getIndex() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
