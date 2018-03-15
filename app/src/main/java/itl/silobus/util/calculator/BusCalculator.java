package itl.silobus.util.calculator;

import itl.silobus.comm.GlobalData;

/**
 * Created by Angelo on 04/08/2017.
 */

public class BusCalculator {

    /*
    * The sensor reacts when something across him, the up distance determined for
    * front sensor is 12.36206913 cm and 7.931034565 cm for back sensor.
    * in which cases the most low distance value is 3 cm, 3.465517283 cm for front sensor
    * and 3.224138022 cm for back sensor, the average of distance for front sensor is
    * 5.493871393 cm and 4.397390845 cm for back sensor
    *
    * */

    private static final int SENSOR_DELAY = 1200;

    private static final float LIMIT_UP_FRONT = 6.868197737f;
    private static final float LIMIT_DOWN_FRONT = 2.02835411f;
    private static final float LIMIT_UP_BACK = 3.53364372f;
    private static final float LIMIT_DOWN_BACK = 1.173252823f;

    private static final float FRONT_DOOR_DISTANCE_REFERENCE = 5.493871393f;
    private static final float BACK_DOOR_DISTANCE_REFERENCE = 4.397390845f;
    private int amountPassengers;

    /**
     *
     */
    public void determinePassengers() {
        float frontDoorDistance = GlobalData.getInstance().getFrontDoorDistance();
        float backDoorDistance = GlobalData.getInstance().getBackDoorDistance();
        if (frontDoorDistance != 0)
            if (
                    frontDoorDistance > getSuperiorLimitFrontDoor()
                            ||
                            frontDoorDistance < getInferiorLimitFrontDoor()) {
                GlobalData.getInstance().setAmountPassengers(
                        GlobalData.getInstance().getAmountPassengers() + 1
                );
                delay();
            }
        if (backDoorDistance != 0)
            if (
                    backDoorDistance > getSuperiorLimitBackDoor()
                            ||
                            backDoorDistance < getInferiorLimitBackDoor()) {
                if (GlobalData.getInstance().getAmountPassengers() > 0)
                    GlobalData.getInstance().setAmountPassengers(
                            GlobalData.getInstance().getAmountPassengers() - 1
                    );
                delay();
            }
    }

    public void delay() {

                try {
                    Thread.sleep(SENSOR_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            ;
    }

    public float getSuperiorLimitFrontDoor() {
        return FRONT_DOOR_DISTANCE_REFERENCE + LIMIT_UP_FRONT;
    }

    public float getInferiorLimitFrontDoor() {
        return FRONT_DOOR_DISTANCE_REFERENCE - LIMIT_DOWN_FRONT;
    }

    public float getSuperiorLimitBackDoor() {
        return BACK_DOOR_DISTANCE_REFERENCE + LIMIT_UP_BACK;
    }

    public float getInferiorLimitBackDoor() {
        return BACK_DOOR_DISTANCE_REFERENCE - LIMIT_DOWN_BACK;
    }
}
