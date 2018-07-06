package com.realm.my.sensors;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by nejimon.ravindran on 5/16/2018.
 */

public class Utility {

    Context _context;
    Utility(Context context){
        _context = context;
    }

    public final void showToast(CharSequence text){
        Toast.makeText(_context, text, Toast.LENGTH_LONG).show();
    }

    public static final boolean deviceSupportsBluetooth(){
        return BluetoothAdapter.getDefaultAdapter() != null;
    }

    public static final boolean isBluetoothTurnedOn(){
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    public static final void turnOnBluetooth() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        MainActivity.Instance.startActivityForResult(intent, Constants.REQUEST_ENABLE_BT);
    }

    public static final void killProcess()
    {
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }

    public static final BluetoothDevice getBluetoothDeviceWithName(Set<BluetoothDevice> devices, String name){
        BluetoothDevice foundDevice = null;

        for (BluetoothDevice device:devices) {
            if(device.getName().toLowerCase().equals(name.toLowerCase())){
                foundDevice = device;
                break;
            }
        }
        return foundDevice;
    }
}