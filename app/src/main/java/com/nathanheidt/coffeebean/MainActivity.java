package com.nathanheidt.coffeebean;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.BeanManager;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.Callback;
import com.punchthrough.bean.sdk.message.DeviceInfo;
import com.punchthrough.bean.sdk.message.ScratchBank;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;


public class MainActivity extends AppCompatActivity {

    final byte COFFEE_START       = '1';
    final byte COFFEE_BREW        = '2';
    final byte COFFEE_HEAT        = '3';
    final byte COFFEE_OFF         = '4';
    final byte COFFEE_RQ_STATE    = '5';
    final byte COFFEE_ECHO        = '6';

    final static int REQUEST_ENABLE_BT = 1;

    Bean CoffeeBean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //access coarse location as BLE needs this to work for some reason
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        0);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }





        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                new AlertDialog.Builder(this)
                        .setTitle("Enable Bluetooth?")
                        .setMessage("You want enable Bluetooth?  Is good I promise.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "BlueTooth On", Toast.LENGTH_LONG).show();
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "BlueTooth Off", Toast.LENGTH_LONG).show();

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }


        if(CoffeeBean != null)
        {
            if(CoffeeBean.isConnected())
            {
                byte[] toSend = {COFFEE_RQ_STATE};
                CoffeeBean.sendSerialMessage(toSend);
            }
        }

        Button bt = (Button)findViewById(R.id.brew_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CoffeeBean != null && CoffeeBean.isConnected())
                {
                    byte[] toSend = {COFFEE_RQ_STATE};
                    CoffeeBean.sendSerialMessage(toSend);
                }
                else
                {
                    ProgressBar spin = (ProgressBar)findViewById(R.id.progressBar);
                    spin.setVisibility(View.VISIBLE);
                    TextView tv = (TextView)findViewById(R.id.coffee_status);
                    tv.setText("finding dem sweet beans");
                    BeanManager.getInstance().startDiscovery(listener);
                }

            }
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();

    }

    BeanDiscoveryListener listener = new BeanDiscoveryListener() {
        @Override
        public void onBeanDiscovered(Bean bean, int rssi) {

            if(bean.getDevice().getName().equals("coffeebean"))
            {
                CoffeeBean = bean;
                bean.connect(getApplicationContext(), beanListener);
                BeanManager.getInstance().cancelDiscovery();
            }
        }

        @Override
        public void onDiscoveryComplete() {
            BeanManager.getInstance().startDiscovery(listener);

        }
    };

    BeanListener beanListener = new BeanListener() {
        @Override
        public void onConnected() {
            System.out.println("connected to Bean!");
            CoffeeBean.endSerialGate();
            CoffeeBean.setAutoReconnect(true);
            TextView tv = (TextView)findViewById(R.id.coffee_status);
            tv.setText("press again to brew");

            byte[] toSend = {COFFEE_START};
            CoffeeBean.sendSerialMessage(toSend);
            ProgressBar spin = (ProgressBar)findViewById(R.id.progressBar);
            spin.setVisibility(View.GONE);
        }

        // In practice you must implement the other Listener methods
        @Override
        public void onReadRemoteRssi(int rssi)
        {}

        @Override
        public void onError(BeanError error)
        {}

        @Override
        public void onScratchValueChanged(ScratchBank bank, byte[] vals)
        {}

        @Override
        public void onSerialMessageReceived(byte[] vals)
        {
            TextView tv = (TextView)findViewById(R.id.coffee_status);
            System.out.print("new data: ");
            System.out.println(vals[0]);

            switch (vals[0])
            {
                case COFFEE_BREW:
                    tv.setText("brewin that coffeh");
                    break;
                case COFFEE_HEAT:
                    tv.setText("ur coffee is ready gurl");
                    break;
                case COFFEE_OFF:
                    tv.setText("sleep now sweet prince");
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onDisconnected()
        {}

        @Override
        public void onConnectionFailed()
        {}
    };


}
