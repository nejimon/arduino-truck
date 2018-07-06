package com.realm.my.sensors;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;

/**
 * Created by nejimon.ravindran on 5/29/2018.
 */

class BluetoothSocketConnector implements Runnable{
    BluetoothSocket _socket;
    boolean _isFaulted;

    BluetoothSocketConnector(BluetoothSocket socket){
        _socket = socket;
    }

    boolean isFaulted(){
        return _isFaulted;
    }

    @Override
    public void run() {
        try {
            _socket.connect();
        } catch (IOException e) {
            _isFaulted = true;
        }
    }
}