package com.realm.my.sensors;

import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.SensorEventListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity
        extends AppCompatActivity
        implements View.OnTouchListener, IOnMessageEvent
{

    public static MainActivity Instance;
    private Utility _utility;
    private Button _btnForward;
    private Button _btnReverse;
    private Button _btnLeft;
    private Button _btnRight;

    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeApp();

    }

    @Override
    protected void onResume(){
        super.onResume();
        //initializeApp();
    }

    private void initializeApp(){
        Instance = this;

        _utility = new Utility(getApplicationContext());

        if(!Utility.deviceSupportsBluetooth()){
            _utility.showToast("This Device does not support Bluetooth");
            this.finishAffinity();
            return;
        }

        if(!Utility.isBluetoothTurnedOn()){
            _utility.showToast("Please turn on Bluetooth first.");
            this.finishAffinity();
            return;
        }


        _btnForward = (Button) findViewById(R.id.btnForward);
        _btnReverse = (Button) findViewById(R.id.btnReverse);
        _btnLeft = (Button) findViewById(R.id.btnLeft);
        _btnRight = (Button) findViewById(R.id.btnRight);

        _btnForward.setOnTouchListener(this);
        _btnReverse.setOnTouchListener(this);
        _btnLeft.setOnTouchListener(this);
        _btnRight.setOnTouchListener(this);

        new BluetoothConnector().execute();

        Truck.Current().addOnMessageEvent(this);
    }


    @Override
    protected void onPause(){
        super.onPause();
    }



    private boolean _isAccelerating = false;
    private boolean _isReversing = false;

    private String _lastCommand;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int button = v.getId();
        int eventAction = event.getAction();
        //String command = "Z";

        switch (button){
            case R.id.btnForward:
                if(eventAction == MotionEvent.ACTION_DOWN){
                    Truck.Current().sendCommand("F");
                    _btnReverse.setEnabled(false);
                    _isAccelerating = true;
                    _isReversing = false;
                }else if (eventAction == MotionEvent.ACTION_UP){
                    Truck.Current().sendCommand("Z");
                    _btnReverse.setEnabled(true);
                    _isAccelerating = false;
                    _isReversing = false;
                }
                break;
            case R.id.btnReverse:
                if(eventAction == MotionEvent.ACTION_DOWN){
                    Truck.Current().sendCommand("B");
                    _btnForward.setEnabled(false);
                    _isReversing = true;
                    _isAccelerating = false;
                }else if (eventAction == MotionEvent.ACTION_UP){
                    Truck.Current().sendCommand("Z");
                    _btnForward.setEnabled(true);
                    _isReversing = false;
                    _isAccelerating = false;
                }
                break;
            case R.id.btnLeft:
                if(eventAction == MotionEvent.ACTION_DOWN){
                    Truck.Current().sendCommand("L");
                    _btnRight.setEnabled(false);
                }else if (eventAction == MotionEvent.ACTION_UP){
                    if(_isAccelerating)
                        Truck.Current().sendCommand("F");
                    else if(_isReversing)
                        Truck.Current().sendCommand("B");
                    else
                        Truck.Current().sendCommand("Z");

                    _btnRight.setEnabled(true);
                }
                break;
            case R.id.btnRight:
                if(eventAction == MotionEvent.ACTION_DOWN){
                    Truck.Current().sendCommand("R");
                    _btnLeft.setEnabled(false);
                }else if (eventAction == MotionEvent.ACTION_UP){
                    if(_isAccelerating)
                        Truck.Current().sendCommand("F");
                    else if(_isReversing)
                        Truck.Current().sendCommand("B");
                    else
                        Truck.Current().sendCommand("Z");

                    _btnLeft.setEnabled(true);
                }
                break;
            default:
                //command = "Z";
                break;
        }

//        if(_lastCommand == command)
//            return false;
//
//
//        _lastCommand = command;

        return false;
    }

    @Override
    public void onTruckMessage(final TruckMessage message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (message.messageType)
                {
                    case TruckMessageType.CONNECTING:
                        _utility.showToast("MyTruck connecting...");
                        break;
                    case TruckMessageType.CONNECTED:
                        _utility.showToast("MyTruck connected");
                        break;
                    case TruckMessageType.DISCONNECTED:
                        _utility.showToast("MyTruck disconnected!!!");;
                        break;
                    case TruckMessageType.ERROR:
                        _utility.showToast(message.messageData.toString());
                        break;
                    case TruckMessageType.DATA_RECEIVED:
                        break;
                    default:
                        //do nothing
                        break;

                }
            }
        });

    }



    private class BluetoothConnector extends AsyncTask<Void, Void, Void> {
        ProgressDialog asyncDialog = new ProgressDialog(MainActivity.this);
        String typeStatus;


        @Override
        protected void onPreExecute() {
            asyncDialog.setMessage("Connecting My Truck...");
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            //Truck.Current().connect();
            Truck.Current().connectTruck();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //hide the dialog
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }

    }
}
