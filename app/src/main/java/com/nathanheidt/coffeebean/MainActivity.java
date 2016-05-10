package com.nathanheidt.coffeebean;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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






public class MainActivity extends AppCompatActivity {

    final byte COFFEE_START       = 0x01;
    final byte COFFEE_BREW        = 0x02;
    final byte COFFEE_HEAT        = 0x03;
    final byte COFFEE_OFF         = 0x04;
    final byte COFFEE_RQ_STATE    = 0x05;
    final byte COFFEE_ECHO        = 'e';

    Bean CoffeeBean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                TextView tv = (TextView)findViewById(R.id.coffee_status);
                tv.setText("finding dem sweet beans");
                BeanManager.getInstance().startDiscovery(listener);
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
            byte[] toSend = {COFFEE_START};
            CoffeeBean.sendSerialMessage(toSend);
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
