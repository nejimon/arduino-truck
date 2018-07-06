package com.realm.my.sensors;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.util.Set;
import java.util.UUID;

/**
 * Created by nejimon.ravindran on 5/29/2018.
 */

public class Truck {
    private static Truck _instance = null;
    private BluetoothSocket _btSocket = null;
    private String _truckAddress = "";
    //private BackgroundWorker _worker = null;
    private IOnMessageEvent _onMessageEvent;

    private Truck(){}

    public final static Truck Current(){
        if(_instance == null){
            _instance = new Truck();
        }
        return _instance;
    }

    public final void addOnMessageEvent(IOnMessageEvent handler){
        _onMessageEvent = handler;
    }

//    public final void connect()
//    {
//        final Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                connectTruck();
//            }
//        });
//
//        thread.start();
//    }

    public final void connectTruck(){

        BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();


        if (_truckAddress.trim().length() <= 0) //address not already set
        {
            Set<BluetoothDevice> pairedDevices = bt.getBondedDevices();
            BluetoothDevice robot = Utility.getBluetoothDeviceWithName(pairedDevices, Constants.ROBOT_NAME);

            if(robot != null){
                _truckAddress = robot.getAddress();
            }
        }

        if (_truckAddress.trim().length() <= 0)
        {
            publishMessage(TruckMessageType.DISCONNECTED, Constants.ROBOT_NAME + " not found in the paired list.");
            return;
        }

        if (_btSocket == null) //socket not already set.
        {
            BluetoothDevice robotDevice = bt.getRemoteDevice(_truckAddress);

            if (robotDevice == null)
            {
                publishMessage(TruckMessageType.DISCONNECTED, "Unable to connect " + Constants.ROBOT_NAME);
                return;
            }

            try{
                _btSocket = robotDevice.createRfcommSocketToServiceRecord(UUID.fromString(Constants.ROBOT_UUID));
            }catch (Exception ex){
                publishMessage(TruckMessageType.ERROR, "Error connecting " + Constants.ROBOT_NAME);
                return;
            }
        }
        else
        {
            if (_btSocket.isConnected()) return;
        }


        try
        {
            bt.cancelDiscovery();
            publishMessage(TruckMessageType.CONNECTING, _truckAddress);

            BluetoothSocketConnector btSocketConnector = new BluetoothSocketConnector(_btSocket);
            final Thread thread = new Thread(btSocketConnector);
            thread.start();
            thread.join();

            if(btSocketConnector.isFaulted()){
                publishMessage(TruckMessageType.DISCONNECTED, Constants.ROBOT_NAME + " is not connected.");
                return;
            }

            //startWorker();
            publishMessage(TruckMessageType.CONNECTED, _truckAddress);
        }
        catch (Exception e)
        {
            publishMessage(TruckMessageType.ERROR, "Error connecting " + Constants.ROBOT_NAME);
        }
    }


    public final void sendCommand(final String command)
    {
        try{
//            CommandSender commandSender = new CommandSender(_btSocket, command);
//            Thread thread = new Thread(commandSender);
//            thread.start();
//            thread.join();
            byte[] arrCommand = command.getBytes();

            _btSocket.getOutputStream().write(arrCommand, 0, arrCommand.length);

        }catch (Exception ex){
            publishMessage(TruckMessageType.ERROR, "Error sending data to " + Constants.ROBOT_NAME );
        }
    }

    private final void publishMessage(int messageType, Object messageData){
        if(_onMessageEvent == null) return;

        TruckMessage message = new TruckMessage();
        message.messageType = messageType;
        message.messageData = messageData;

        _onMessageEvent.onTruckMessage(message);
    }
}
