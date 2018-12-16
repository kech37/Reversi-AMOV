package pt.isec.a21240456.a2120528.reversisec;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class NetworkAdapter {

    public static final int SERVER = 0;
    public static final int CLIENT = 1;

    public static final int SETTING_UP = 0;
    public static final int READY = 1;

    private static final int PORT = 8899;
    private static final int PORTaux = 9988; // to test with emulators

    private int mode;
    Context context;

    private ProgressDialog pd;

    ServerSocket serverSocket;
    Socket socketGame;
    Handler hNet;
    PrintWriter output;
    BufferedReader input;
    String inputString;

    public NetworkAdapter(Context context, int mode) {
        this.context = context;
        this.mode = mode;
        hNet = new Handler();
    }

    public int getMode() {
        return mode;
    }

    public boolean startNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(context, "No network connection", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    void startServerSide() {
        // WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        // String ip = Formatter.formatIpAddress(wm.getConnectionInfo()
        // .getIpAddress());
        String ip = getLocalIpAddress();
        pd = new ProgressDialog(context);
        pd.setMessage("(IP: " + ip + ")");
        pd.setTitle(context.getResources().getString(R.string.waiting_for_player));
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ((Activity)(context)).finish();
                if (serverSocket!=null) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                    }
                    Toast.makeText(context, "Socket Closed", Toast.LENGTH_SHORT).show();
                    serverSocket=null;
                }
            }
        });
        pd.show();

        Thread s = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(PORT);
                    socketGame = serverSocket.accept();
                    serverSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Problemas na cThread", Toast.LENGTH_SHORT).show();
                } finally {
                    cThread.start();
                }
                hNet.post(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        if (socketGame == null) {
                            ((Activity)(context)).finish();
                            Toast.makeText(context, "socketGame == null", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        s.start();
    }

    void startClientSide(final String strIP) {

        Thread c = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socketGame = new Socket(strIP, PORTaux);
                } catch (Exception e) {
                    socketGame = null;
                } finally {
                    cThread.start();
                }
            }
        });
        c.start();
    }

    public void sendSerializedPacket(final String serializedPacket) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        output.println(serializedPacket);
                        output.flush();
                    } catch (Exception e) {
                        Toast.makeText(context, "Error sending packet", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            t.start();
    }



    Thread cThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                input = new BufferedReader(new InputStreamReader(
                        socketGame.getInputStream()));
                output = new PrintWriter(socketGame.getOutputStream());
                ((PlayGame)context).NetReadyHandler.sendEmptyMessage(0);
                while (!Thread.currentThread().isInterrupted()) {
                    inputString = input.readLine();
                    hNet.post(new Runnable() {
                        @Override
                        public void run() {
                            ((PlayGame)context).readPacket(inputString);
                        }
                    });
                }
            } catch (Exception e) {
                hNet.post(new Runnable() {
                    @Override
                    public void run() {
                        ((Activity)(context)).finish();
                        Toast.makeText(context, "Game Ended", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    });

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }



}
