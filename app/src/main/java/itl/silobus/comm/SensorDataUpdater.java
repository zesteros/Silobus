package itl.silobus.comm;

import android.util.Log;

public class SensorDataUpdater {

    private StringBuilder message;
    private GlobalData global;
    private float avg;
    private float i = 1;
    private float avg1;
    private float sum;
    private float sum1;

    public SensorDataUpdater() {message = new StringBuilder();}

    public void debugMessage(String data) {
        message.append(data);
        int endOfLineIndex = message.indexOf("~");// determine the end-of-line
        Log.d("data:",data);

        if (endOfLineIndex > 0) { // make sure there data before ~
            String dataInPrint = message.substring(0, endOfLineIndex);// extract string

            if (message.charAt(0) == '#') {//if it starts with # we know it is what we are looking for
                int[] endOfValues = new int[3];//determine when a read value is finish
                int j = 0;
                for (int i = 1; i < dataInPrint.length(); i++) {
                    if (dataInPrint.charAt(i) == '+') {//if find a + save into an array that position
                        try {
                            endOfValues[j] = i;
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                            return;
                        }
                        j++;
                    }
                }
                try {
                    setData(endOfValues);
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    return;
                }
            }
            message.delete(0, message.length());//clear all string data
        }
    }

    public void setData(int endOfValues[]) {
        global = global.getInstance();
        global.setAmountPassengers(Integer.parseInt(message.substring(endOfValues[2] + 1, message.indexOf("~"))));
        String lat = message.substring(endOfValues[0] + 1,endOfValues[1]);
        String lon = message.substring(endOfValues[1] + 1, endOfValues[2]);
        lat.replace(",","");
        lon.replace(",","");
        double gradesLat=0;
        if(!lat.substring(0,2).equals("0."))
        gradesLat = Integer.parseInt(lat.substring(0,2));
        double minutesLat = Integer.parseInt(lat.substring(2,4));
        double secondsLat = 0;
        if(!lat.substring(5,lat.length()).equals("000,"))
        secondsLat = Float.parseFloat(lat.substring(5,lat.length()))/100;

        secondsLat /=3600;
        minutesLat /=60;

        double finalLatitude = gradesLat+minutesLat+secondsLat;

        Log.d("COORD","Lat:"+finalLatitude);
        double gradesLon = Integer.parseInt(lon.substring(0,3));
        double minutesLon = Integer.parseInt(lon.substring(3,5));
        double secondsLon = Float.parseFloat(lon.substring(6,lon.length()))/100;
        secondsLon /=3600;
        minutesLon /=60;

        double finalLongitude = (gradesLon+minutesLon+secondsLon)*-1;
        Log.d("COORD","Lat:"+finalLongitude);

        global.setActualLatitude(finalLatitude);
        global.setActualLongitude(finalLongitude);


        // Log.d("COORD","Lon:"+);//);
        //Log.d("people","");//);



        //Log.d("PSGRS","PASAJERS:"+message.substring(mes);//);

        //global.setPanicButtonActivated(
          //      Integer.parseInt(message.substring(endOfValues[0] + 1,endOfValues[1])));
    }
}