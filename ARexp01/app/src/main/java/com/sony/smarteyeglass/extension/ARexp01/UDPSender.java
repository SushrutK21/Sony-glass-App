package com.sony.smarteyeglass.extension.ARexp01;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created by bolet on 14/01/16.
 */
public class UDPSender extends AsyncTask<HashMap, Integer, String> {


    public AsyncResponse delegate = null;

    /**
     * Function for send an UDP datagram.
     *
     * @param params msg, ip and port for sending
     * @return
     */
    @Override
    protected String doInBackground(HashMap... params) {

        //Retrieve params
        HashMap<String, Object> p = params[0];
        String testServer = (String) p.get("ip");
        String udpMsg = (String) p.get("msg");
        udpMsg += "\r\n\0";
//        Integer port = //(int) p.get("port");
        Integer port = 0;
        if(p.get("port")!=null) {
            try {
                port = Integer.parseInt(p.get("port").toString());
            } catch (NumberFormatException nfex) {
                port = 0;
            }
        }else{
            port = 0;
        }
//        boolean broadcast = (boolean) p.get("bdc");
        boolean broadcast = false;
        if(p.get("bdc") != null) {
            String tmpbdc = p.get("bdc").toString();
            if ("true".equals(tmpbdc) || "1".equals(tmpbdc)) {
                broadcast = true;
            } else {
                broadcast = false;
            }
        }else{
            broadcast = false;
        }
        InetAddress serverAddr = null;
        DatagramSocket ds = null;
        DatagramPacket dp = null;

        Log.v("BROADCAST SEND", String.valueOf(broadcast) );

        try {
            serverAddr = InetAddress.getByName(testServer);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            ds = new DatagramSocket();
            ds.setBroadcast(broadcast);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        String rtnString = null;
        if (testServer != null && port != null) {
            if (broadcast) {
                try {
                    dp = new DatagramPacket(udpMsg.getBytes(), udpMsg.length(), getBroadcastAddress(), port);
                } catch (Exception e) {
                } /*catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            } else {
                dp = new DatagramPacket(udpMsg.getBytes(), udpMsg.length(), serverAddr, port);
            }

            try {
                Log.v("Sending UDP", "Sending udp pckg, msg: " + udpMsg + " " + testServer + ":" + String.valueOf(port));
                ds.send(dp);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ds.close();
            rtnString = "TX|" +testServer + ":" + p.get("port") + "-> " + udpMsg.substring(0, udpMsg.indexOf("\r") );

        } else{
            Log.d(Constants.LOG_TAG, "IP Address(" + testServer + ") and/or Port(" + port + ") is invalid.");
        }
        return rtnString;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            delegate.processFinish(result);
        }
    }

    public InetAddress getBroadcastAddress() {
        InetAddress broadcastAddress = null;
        try {
            Enumeration<NetworkInterface> networkInterface = NetworkInterface
                    .getNetworkInterfaces();

            while (broadcastAddress == null
                    && networkInterface.hasMoreElements()) {
                NetworkInterface singleInterface = networkInterface
                        .nextElement();
                String interfaceName = singleInterface.getName();
                if (interfaceName.contains("wlan0")
                        || interfaceName.contains("eth0")) {
                    for (InterfaceAddress infaceAddress : singleInterface
                            .getInterfaceAddresses()) {
                        broadcastAddress = infaceAddress.getBroadcast();
                        if (broadcastAddress != null) {
                            break;
                        }
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }

        return broadcastAddress;
    }

/*    public static InetAddress getNetworkLocalBroadcastAddressdAsInetAddress() throws IOException {
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
            NetworkInterface intf = en.nextElement();
            if(Build.VERSION.SDK_INT < 9) {
                if(!intf.getInetAddresses().nextElement().isLoopbackAddress()){
                    byte[] quads = intf.getInetAddresses().nextElement().getAddress();
                    quads[0] = (byte)255;
                    quads[1] = (byte)255;
                    return InetAddress.getByAddress(quads);
                }
            }else{
                if(!intf.isLoopback()){
                    List<InterfaceAddress> intfaddrs = intf.getInterfaceAddresses();
                    return intfaddrs.get(0).getBroadcast(); //return first IP address
                }
            }
        }
        return null;
    }*/

    /*public static String getBroadcast() throws SocketException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        for (Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces(); niEnum.hasMoreElements();) {
            NetworkInterface ni = niEnum.nextElement();
            if (!ni.isLoopback()) {
                for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                    return interfaceAddress.getBroadcast().toString().substring(1);
                }
            }
        }
        return null;
    }*/


}
