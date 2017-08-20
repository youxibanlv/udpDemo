package com.strike.udpdemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button btnStart;
    private TextView receiveData;
    private TextView sendData;
    private EditText sn;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpConstance.SCAN_SUCCESS:
                    String rec = (String) msg.obj;
                    if (!TextUtils.isEmpty(rec)) {
                        String s1 = receiveData.getText().toString();
                        s1 = s1+"\n"+getCurrentTime()+"\n"+rec;
                        receiveData.setText(s1);
                    }
                    break;
                case HttpConstance.SCAN_COMPLETE:
                    String send = (String) msg.obj;
                    if (!TextUtils.isEmpty(send)) {
                        String s2 = sendData.getText().toString();
                        s2 = s2+"\n"+getCurrentTime()+"\n"+send;
                        sendData.setText(s2);
                    }
                    break;
                case HttpConstance.SCAN_FAILD:
                   Toast.makeText(MainActivity.this,"监听超时",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart = (Button) findViewById(R.id.btnStart);
        receiveData = (TextView) findViewById(R.id.receiveData);
        sendData = (TextView) findViewById(R.id.sendData);
        sn = (EditText) findViewById(R.id.sn);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData.setText("");
                receiveData.setText("");
                String deviceId = sn.getText().toString();
                if (!TextUtils.isEmpty(deviceId)) {
                    UdpUtils.getInstance().listener(handler, WifiUtils.getIP(MainActivity.this), deviceId);
                }else {
                    Toast.makeText(MainActivity.this,"请输入deviceId",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss.SS");

    private String getCurrentTime(){
        String timeStr = format.format(new Date());
        return timeStr;
    }
}
