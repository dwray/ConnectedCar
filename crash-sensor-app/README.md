#CarDemo for Android
CarDemo is a demo application for interacting with a Solace Router.
The application turns your mobile device into a sensor that publishes and receives data to and from a Solace Router using the MQTT protocol.

##Short Description
This application demonstrates using an Android device as an IoT device, and provides a variety of events and commands that it can publish or receive data to and from.

Events and commands are user defined values used to differentiate the data that you publish or receive. For example, if you have a device that is publishing GPS coordinates, you may choose to publish it as a 'GPS' event. Or, if you want to send an alert command to a device, you may choose to publish it as an 'alert' or 'notification' command.

The sensor application can publish data to the following event topics:
- `track` (Tracking data)
- `crash` (Crash notification)

It can receive data on the following command topics:
- `engine` (Start Engine)
- `horn` (Sound Horn)
- `lock` (Unlock Door)
- `light` (Turn on/off Lights)
- `color` (Change Ambience)

The controller application is essentially the reverse.

For more information on Solace for IoT, refer to https://solace.com/internet-of-things.

##How it works
A device that is connected to a Solace Router may publish and subscribe to data that is presented as either an event or command using the MQTT protocol.
The Eclipse Paho MQTT Android Service is used to publish and subscribe to the Solace Router. This can be downloaded from
[Eclipse Paho MQTT Android Service](http://www.eclipse.org/paho/clients/android/).

MQTT is a lightweight messaging protocol that supports publish/subscribe messaging. With MQTT, an application publishes messages to a topic. These messages may then be received by another application that is subscribed to that topic. This allows for a detached messaging network where the subscribers and publishers do not need to be aware of each other.
The topics used by this application can be seen in the table below:

##Topics
|Topic|Sample Topic|Sample Messages|
|:---------- |:---------- |:------------|
|`cardemo/<Vehicle VIN>/evt/<eventId>/fmt/json`|`cardemo/1234567890123456789/evt/track/fmt/json`|`{"d":{"acceleration_x":2.23517E-7,"acceleration_y":9.77631,"acceleration_z":0.812348,"roll":-2.7514932E-7,"pitch":-1.4878927,"yaw":0.0,"longitude":0.0,"latitude":0.0,"heading":0.0,"speed":0.0,"trip_id":"1511515976","timestamp":"2017-11-24T09:33:02.637+00:00"}}`|
|`cardemo/<Vehicle VIN>/cmd/<commandId>/fmt/json`|`cardemo/1234567890123456789/cmd/engine/fmt/json`|`{"d":{"engine":"start"}}`

For more information on the MQTT protocol, see http://mqtt.org/.

##Try it
The IoT Starter application can be used in three ways.

### 1. Connect using the default settings to the Solace London Lab
In order to connect to the Solace London Lab, all you need to do is activate the app.

Once the controller app and the sensor app are connected, you can control the sensor by selecting the appropriate buttons and track it using the tracker map.

You can also view the car's location on an online version of the tracker, which receives the same  data using our JavaScript API, here: http://london.solace.com/connected-car/vin-track/ 
### 2. Download a free Community Edition VMR and connect to that
Download a Community Edition VMR from http://dev.solace.com/downloads/ 
...other stuff?

### 3. Connect to Solace Cloud
Put some Solace Cloud Stuff here

##Prerequisites
Required:
- Two Android phones, one for the controller and one for the sensor (although you can use Emulated phones in Android Studio)
- An Android SDK installation

##Installation
1. `git clone https://github.com/dwray/ConnectedCar.git`
2. Launch Android Studio and select "Open an Existing Android Studio Project".
3. Navigate to your `ConnecrtedCar` folder and open the projects.
4. Run the applications.

##Notes
In order to really see this demo do something, you must have the sensor app running, ideally on a phone attached to a remote controlled car!


##Resources
- [Solace IoT](https://solace.com/internet-of-things)
- [VMR Community Edition](http://dev.solace.com/downloads/)
- [Solace Cloud](http://dev.solace.com/downloads/) -- needs proper solace cloud link
- [MQTT](http://mqtt.org/)
