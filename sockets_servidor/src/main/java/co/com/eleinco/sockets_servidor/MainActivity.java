package co.com.eleinco.sockets_servidor;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import javax.net.ServerSocketFactory;

public class MainActivity extends AppCompatActivity {

    private Button bt_enviar;
    private EditText et_texto;
    private TextView tv_chat;
    private TextView serverStatus;

    public static String SERVERIP = "";
    public static final int SERVERPORT = 8888;

    private Handler handler = new Handler();

    private ServerSocket serverSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverStatus = (TextView) findViewById(R.id.main_tv_infoIP);

        tv_chat = (TextView) findViewById(R.id.main_tv_chat);

        et_texto = (EditText) findViewById(R.id.main_et_loquevoyadecir);

        bt_enviar = (Button) findViewById(R.id.main_bt_enviar);
        bt_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarTexto();
            }
        });

        SERVERIP = getIpAddress();

        Thread fst = new Thread(new ServerThread());
        fst.start();

    }


    public class ServerThread implements Runnable {
        public void run() {
            if (SERVERIP != null) {

                try {
                    //serverSocket = new ServerSocket(SERVERPORT);
                    serverSocket = ServerSocketFactory.getDefault().createServerSocket(SERVERPORT);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Listening on IP: " + SERVERIP + ":"+SERVERPORT);
                        }
                    });

                    while (true) {
                        // listen for incoming clients
                        Socket client = serverSocket.accept();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                serverStatus.setText("Listening on IP: " + SERVERIP + ":"+SERVERPORT+"\nConnected.");
                            }
                        });

                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        String line = null;
                        while ((line = in.readLine()) != null) {
                            Log.d("ServerActivity", line);
                            final String finalLine = line;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // do whatever you want to the front end
                                    // this is where you can be creative
                                    tv_chat.setText(tv_chat.getText().toString() + "\n*OTRO: " + finalLine);
                                }
                            });
                        }
                    }

                } catch (SecurityException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("No se puede aceptar conexión");
                        }
                    });
                    e.printStackTrace();
                } catch (Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Error");
                        }
                    });
                    e.printStackTrace();
                }


            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverStatus.setText("Couldn't detect internet connection.");
                    }
                });
            }
        }
    }

    private void enviarTexto(){

    }

    // gets the ip address of your phone's network
    private String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("rmnet")) { // Es red celular
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                            return inetAddress.getHostAddress().toString();
                        }
                    }
                } else if (intf.getName().contains("wlan")) {
                } // Es red WiFi
            }
        } catch (SocketException ex) {
            Log.e("ServerActivity", ex.toString());
        }
        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            // make sure you close the socket upon exiting
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
