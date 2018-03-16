# Silobus
An app for gps location of buses (simulated) using SIM808 module

The main objective is the real time location of buses in mexico, for real, this is an academic project.

The used board is a SIM Module called SIM808 which contains GPS and GSM/GPRS, perfect for the purpose of this project.

![diagram](https://github.com/zesteros/Silobus/raw/master/Files/silobus/SIM-FRITZ_bb.png)

The app contains a simulated WAN connection throught Wi-Fi.
what is does mean? The Geolocation and the send-retrieve information throught internet needs logically a 
internet connection and if you want to test and leaving working this you will need so much gasoline or
whatever you have to transport in your city and document your tests. Obviously for academic purposes we
will mantain this in local LAN.

For test, only you will need:

* Arduino UNO/MEGA/NANO
* SIM808
* Android Studio
* Access Point
* ESP8266 or another Wi-Fi module

The app contains two main buttons, the search button and the user-location button,
when you press the search button shows you in a range of 800 meters the bus-stops
in that radio, and if you choose a bus-stop the app draw buses according the selected bus-stop, which is generated randomly (with a SQlite database, bus-stops coordinates, and 
buses and which bus-stop pass throught).

The info dialog of every bus shows relevant data as arrive time, speed, distance to bus-stop and other else.

![SH](https://github.com/zesteros/Silobus/raw/master/Files/screenshots/28946685_1073197926154442_742458026_o.png)
![SH](https://github.com/zesteros/Silobus/raw/master/Files/screenshots/28928853_1073197832821118_697339996_o.png)
![SH](https://github.com/zesteros/Silobus/raw/master/Files/screenshots/28928853_1073197832821118_697339996_o.png)
![sh](https://github.com/zesteros/Silobus/blob/master/Files/screenshots/28943641_1073197902821111_64395006_o.png)
![sh](https://github.com/zesteros/Silobus/raw/master/Files/screenshots/28946698_1073197796154455_1222042427_o.png)



