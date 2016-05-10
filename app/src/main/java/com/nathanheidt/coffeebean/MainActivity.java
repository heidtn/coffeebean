package com.nathanheidt.coffeebean;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final List<Bean> beans = new ArrayList<>();
    Bean CoffeeBean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bt = (Button)findViewById(R.id.brew_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            beans.add(bean);
        }

        @Override
        public void onDiscoveryComplete() {
            for (Bean bean : beans) {
                System.out.println(bean.getDevice().getName());   // "Bean"              (example)
            }

        }
    };


}
