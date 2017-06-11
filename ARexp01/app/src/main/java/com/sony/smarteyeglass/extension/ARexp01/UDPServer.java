package com.sony.smarteyeglass.extension.ARexp01;

import android.os.AsyncTask;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

/**
 * Created by  Sushrut on 18/01/16.
 */

public class UDPServer extends AsyncTask<String, String, String> {

    private int port;
    public AsyncResponse delegate = null;
    DatagramSocket clientsocket;
    static String command;
    static String commandy;
    //static int mitali=0;


    protected void cancelClientSocket (){
        try {
            clientsocket.close();
            clientsocket = new DatagramSocket(null);
            clientsocket.setReuseAddress(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        while (true) {
            try {
                publishProgress(receiveMessage());
                if(isCancelled()) {
                    Log.v("CANCEL SOCKET: " , "CANCELLED");
                    break;
                }
            } catch (Exception e) {
                //
            }

        }
        return "";
    }

    public String[] receiveMessage(){

        String[] rec_arr = null;
        try {
            if (clientsocket == null){
                clientsocket=new DatagramSocket(port);
            }

            byte[] receivedata = new byte[30];

            DatagramPacket recv_packet = new DatagramPacket(receivedata, receivedata.length);
            //Log.d("UDP", "S: Receiving...");
            clientsocket.receive(recv_packet);
            String str = new String(recv_packet.getData()); //stringa con mesasggio ricevuto

            InetAddress ipaddress = recv_packet.getAddress();
            int portR = recv_packet.getPort();
            Log.d("IPAddress : ",ipaddress.toString());
            Log.d(" Port : ",Integer.toString(portR));
            String rec_str = ipaddress.toString() + ":" + Integer.toString(portR) + "|";
            rec_str += str.replace(Character.toString ((char) 0), "");
            Log.d(" Received String ",rec_str);

            rec_arr=rec_str.split("\\|");


            return rec_arr;

        } catch (Exception e) {
//            Log.e("UDP", "S: Error", e);
            Log.e("UDP Server", "socket failed -> cancelClientSocket");
            cancelClientSocket();
        }
        return rec_arr;
    }


    @Override
    protected void onPostExecute(String result) {
        //
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected void onProgressUpdate(String... rec_arr) {
        //ricevi la stringa,
        //splittala
        //esegui l'azione richiesta sulla GUI



        if (rec_arr.length>1){
            String ip=rec_arr[0].substring(1);
            //String command=rec_arr[1];
            command= rec_arr[1];
            String[] parts = command.split("\\/"); // String array, each element is text between dots

            String valueofx = parts[0];
            String valueofy =parts[1];
            Log.v("UNIQUE x", valueofx);
            Log.v("UNIQUE y", valueofy);
           // commandy=rec_arr[2];
           //k mitali=Integer.parseInt(command);
            String uniqueString = "RX|" + command ;
            Log.v("UNIQUE String", uniqueString);

           // String uniqueString2 = "RX|" + commandy ;
           // Log.v("UNIQUE String2", uniqueString2);

            delegate.processFinish(uniqueString);

            uniqueString =uniqueString.replaceAll( "[^\\d-]", "" );

            valueofx=valueofx.replaceAll("[^\\d-]", "");
            valueofy=valueofy.replaceAll("[^\\d-]", "");
            Log.v("UNIQUE x", valueofx);
            Log.v("UNIQUE y", valueofy);

            Log.v("UNIQUE String2", uniqueString);
            try {
                //int x = Integer.parseInt(uniqueString);
                // int x = uniqueString.nextInt();
                int x = Integer.parseInt(valueofx);
                int y = Integer.parseInt(valueofy);
                Log.d(Constants.LOG_TAG, "The received Int is nameMo  " + x);
                SampleExtensionService.Object.sendMessageToExtention((float) x, (float) y, " hello world");
            } catch (NumberFormatException nfex) {
                Log.d(Constants.LOG_TAG, "The received message is not an integer.");
            }
               /* if(command.contentEquals("go")){
                    //press button go
                    //startAction(null);
                }*/

        }
//            Log.v("RECEIVED UDP", "onProgressUpdate: " + rec_arr[0]);

    }
    // you may separate this or combined to caller class.


    public static String kuhaName()
    {

        return command;
    }




    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


}