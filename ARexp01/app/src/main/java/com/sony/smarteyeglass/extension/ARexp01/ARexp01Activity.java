package com.sony.smarteyeglass.extension.ARexp01;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sony.smarteyeglass.extension.sampleARLouvre.R;
import com.sonyericsson.extras.liveware.aef.registration.Registration;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.regex.Pattern;

/**
 * Created by Sushrut on 16/26/04.
 */
public final class ARexp01Activity extends Activity{

    private static UDPUtils udpUtils;

    private Button btSend, button;
    private TextView tvServerLabel, textView;
    private EditText etIp, etRxPort, etTxPort, etMessage;
    private CheckBox cbBroadcast;
    private LinearLayout llLog;

    private String serverLabelMessage = "UDP server listening at port ";

    private String testServer = "192.168.1.91"; //127.0.0.1
    private String udpMsg = "HELLO!\r\n\0";
    private int defaultServerPort = 7400;

    /**
     * Pattern for validate ip well writted
     */
    private static final Pattern PARTIAl_IP_ADDRESS =
            Pattern.compile("^((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])\\.){0,3}"+
                    "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])){0,1}$");


    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // setContentView(new GraphicView(this));
        setContentView(R.layout.hostlayout);
        //setContentView(new GraphicView(this));
        startExtention();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String message = extras.getString("Message");
            Log.d(Constants.LOG_TAG,message);
            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
            //        .show();
        }
        if(SampleExtensionService.Object == null){
            Intent intent = new Intent(Registration.Intents.EXTENSION_REGISTER_REQUEST_INTENT);
            Context context = getApplicationContext();
            intent.setClass(context, SampleExtensionService.class);
            context.startService(intent);
        }


        etIp = (EditText) findViewById(R.id.etIp);
        etRxPort = (EditText) findViewById(R.id.etRxPort);
        etTxPort = (EditText) findViewById(R.id.etTxPort);
        etMessage = (EditText) findViewById(R.id.etMessage);
        tvServerLabel = (TextView) findViewById(R.id.tvServerLabel);
        btSend = (Button) findViewById(R.id.btSend);
       // button = (Button) findViewById(R.id.button);
        llLog = (LinearLayout) findViewById(R.id.llLog);
        cbBroadcast = (CheckBox) findViewById(R.id.cbBroadcast);
        textView = (TextView) findViewById(R.id.textView);


        etRxPort.setText(String.valueOf(defaultServerPort), TextView.BufferType.EDITABLE);
        etIp.setText(String.valueOf(testServer), TextView.BufferType.EDITABLE);


        //Starting udp server
        udpUtils = new UDPUtils(this);
        udpUtils.setServerPort(defaultServerPort);
        udpUtils.startReceiveUdp();

        //Label with the terminal ip and listening port, by default 45450
        tvServerLabel.setText(serverLabelMessage + getIPAddress(true) + ":" + String.valueOf(udpUtils.getServerPort()));

        //Listener for the Reception port Edit text for change the server label text view and the listening port of udp server
        etRxPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }


            private String mPreviousText = "";

            @Override
            public void afterTextChanged(Editable s) {
                if (!(etRxPort.getText().toString().matches(""))) {
                    udpUtils.restartReceiveUdp(Integer.parseInt(etRxPort.getText().toString()));

                    tvServerLabel.setText(serverLabelMessage + getIPAddress(true) + ":" + String.valueOf(udpUtils.getServerPort()));
                }
            }
        });


        //Listener for the Ip Edit text that makes it an edit text that only accepts valid ip numbers
        etIp.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            String mPreviousText = "";

            @Override
            public void afterTextChanged(Editable s) {
                if (isValidIp(s.toString())) {
                    mPreviousText = s.toString();
                    etIp.setTextColor(Color.BLACK);
                    //Log.v("IP" , "" + etIp.getText());
                } else {
                    Toast.makeText(getApplicationContext(), "Not valid IP", Toast.LENGTH_SHORT).show();
                    //etIp.setTextColor(Color.RED);
                    s.replace(0, s.length(), mPreviousText);
                }
            }
        });


        //Listener on send button for send an UDP msg datagram to specific ip:port
        btSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                if ((isValidIp(etIp.getText().toString())) && !(etTxPort.getText().toString().matches(""))) {

                    HashMap<String, Object> params = new HashMap<String, Object>();
//                    params.put("ip", testServer);
//                    params.put("msg", udpMsg);
//                    params.put("port", defaultServerPort);

                    if (cbBroadcast.isChecked()) {

                    }
//                    params.put("ip", etIp.getText().toString());
//                    params.put("msg", etMessage.getText().toString());
//                    params.put("port", Integer.parseInt(etTxPort.getText().toString()));
//                    params.put("bdc", cbBroadcast.isChecked());
//
                    Params.put("ip", etIp.getText().toString());
                    Params.put("msg", etMessage.getText().toString());
                    Params.put("port", Integer.parseInt(etTxPort.getText().toString()));
                    Params.put("bdc", cbBroadcast.isChecked());

                    //new UDPSender().execute(params);
                    udpUtils.sendUDP(Params);

                } else {
                    Toast.makeText(getApplicationContext(), "Parameters not corrects", Toast.LENGTH_SHORT).show();
                }

            }
        });

//        // Send UDP message periodically
//        mHandler = new Handler();
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                udpSendTest();
//                Log.d(Constants.LOG_TAG, "Call postDelayed");
//                mHandler.postDelayed(this, 1000);
//            }
//        }, 1000);

    }

    public void onDestroy() {
        if ( udpUtils != null ) udpUtils.stopReceiveUdp();
        super.onDestroy();
        Toast.makeText(getApplicationContext(),"Stopped UDP Server", Toast.LENGTH_SHORT).show();
    }

    private static HashMap<String, Object> Params = new HashMap<String, Object>();
    private static String sAngle = "0";
    private Handler mHandler = null;

    // Send Angle data via UDP
    public static void udpSendTest(){
        Params.put("msg", sAngle);
        udpUtils.sendUDP(Params);
    }

    // Save Angle data received from SmartEyeGlass
    public static void udpTest(String s){
        Log.d(Constants.LOG_TAG, "udpTest: " + s);
        sAngle = s;
    }

    public void startExtention(){
        if(SampleExtensionService.Object!=null){
            SampleExtensionService.Object.sendMessageToExtention(0,0,"coordinates are" );
        }
        Log.d(Constants.LOG_TAG,"startExtention");
    }

//    private class GraphicView extends SurfaceView implements SurfaceHolder.Callback{
//        WindowManager wm = getWindowManager();
//        Display disp = wm.getDefaultDisplay();
//        float dW = (float)disp.getWidth();
//        float dH = (float)disp.getHeight();
//        int x=(int)(dW*0.5f);
//        int y=(int)(dH*0.5f);
//        float rcpW = 1.f/dW;
//        float rcpH = 1.f/dH;
//
//        public GraphicView(Context context) {
//            super(context);
//            getHolder().addCallback(this);
//        }
//        public void surfaceCreated(SurfaceHolder holder) {
//            draw(Color.GREEN) ;
//        }
//        @Override
//        public boolean onTouchEvent(MotionEvent ev) {
//            int x2, y2 ;
//            int _x = 1, _y =1 ;
//            int color=Color.WHITE;
//            boolean isdraw = false;
//            boolean loop = true ;
//            switch(ev.getAction()){
//                case MotionEvent.ACTION_DOWN:
//                    color=Color.YELLOW;
//                    isdraw=true;
//                    break;
//            }
//            if(isdraw){
//                x = (int)ev.getX();
//                y = (int) ev.getY();
//                draw(color) ;
//                Log.d(Constants.LOG_TAG, "value of x is "+x);
//                SampleExtensionService.Object.sendMessageToExtention((float)x*rcpW,(float)y*rcpH);
//            }
//            return true;
//        }
//
//        public void draw(int color)
//        {
//            Canvas canvas = getHolder().lockCanvas();
//            if ( canvas!=null) {
//                canvas.drawColor(Color.BLACK);
//                Paint paint = new Paint();
//                paint.setAntiAlias(true);
//                paint.setStyle(Paint.Style.STROKE);
//                paint.setColor(color);
//                paint.setDither(true);
//                canvas.drawCircle(x, y, 10.0f, paint);
//
//                paint.setTextSize(12);
//                paint.setColor(color);
//                canvas.drawText("x=" + x + " y=" + y , x , y+15, paint);
//
//                getHolder().unlockCanvasAndPost(canvas);
//            }
//        }
//        public void surfaceChanged(SurfaceHolder holder,
//                                   int format, int width, int height) {
//        }
//        public void surfaceDestroyed(SurfaceHolder holder) {
//        }
//    }

//    public void WriteExternalStorage(View View){
//        String nameMo = UDPServer.kuhaName();
//        Scanner in = new Scanner(nameMo).useDelimiter("[^0-9]+");
//        int x = in.nextInt();
//        Log.d(Constants.LOG_TAG,"the received Int is nameMo  "+x);
//        SampleExtensionService.Object.sendMessageToExtention((float)x,(float)512);
//
//    }

    private boolean isValidIp (String s){
        return PARTIAl_IP_ADDRESS.matcher(s).matches();
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }

}
