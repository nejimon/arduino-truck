package com.realm.my.sensors;

import android.bluetooth.BluetoothSocket;

/**
 * Created by nejimon.ravindran on 5/29/2018.
 */

public class CommandSender implements Runnable {
    BluetoothSocket _socket;
    String _command = "";
    boolean _isFaulted = false;

    CommandSender(BluetoothSocket socket, String command){
        _socket = socket;
        _command = command;
    }

    public boolean isFaulted(){
        return _isFaulted;
    }

    @Override
    public void run() {
        try
        {
            if (_socket == null || !_socket.isConnected())
                throw new Exception(Constants.ROBOT_NAME + " is not connected.");

            byte[] arrCommand = _command.getBytes();

            _socket.getOutputStream().write(arrCommand, 0, arrCommand.length);
        }
        catch (Exception ex)
        {
            _isFaulted = true;
        }
    }
}