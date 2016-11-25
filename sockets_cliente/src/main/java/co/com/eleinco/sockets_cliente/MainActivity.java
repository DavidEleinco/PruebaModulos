package co.com.eleinco.sockets_cliente;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private Button bt_enviar;
    private Button bt_conectar;
    private EditText et_texto;
    private TextView tv_chat;
    private EditText et_ipserver;

    private boolean connected = false;

    // Server IP
    public static String SERVERIP = "";

    // designate a port
    public static final int SERVERPORT = 8888;

    private Handler handler = new Handler();

    private ServerSocket serverSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_enviar = (Button) findViewById(R.id.main_bt_enviar);
        et_texto = (EditText) findViewById(R.id.main_et_loquevoyadecir);
        tv_chat = (TextView) findViewById(R.id.main_tv_chat);
        et_ipserver = (EditText) findViewById(R.id.main_et_IPserver);
        bt_conectar = (Button) findViewById(R.id.main_bt_conectar);

        bt_conectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conectar();
            }
        });


        bt_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarTexto();
            }
        });
    }

    private void conectar(){
        if (!connected) {
            SERVERIP = et_ipserver.getText().toString();
            if (!SERVERIP.equals("")) {
                Thread cThread = new Thread(new ClientThread());
                cThread.start();
            }
        }
    }

    public class ClientThread implements Runnable {

        public void run() {
            InetAddress serverAddr = null;
            try {
                serverAddr = InetAddress.getByName(SERVERIP);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            Log.d("ClientActivity", "C: Connecting...");
            Socket socket = new Socket();
            try {
                socket.connect(new InetSocketAddress(serverAddr, SERVERPORT), 10000 /*timeout*/);

                connected = true;
                while (connected) {
                    try {
                        Log.d("ClientActivity", "C: Sending command.");
                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
                                .getOutputStream())), true);
                        // where you issue the commands
                        out.println("Hey Server!");
                        Log.d("ClientActivity", "C: Sent.");
                        break;
                    } catch (Exception e) {
                        Log.e("ClientActivity", "S: Error", e);
                    }
                }
                socket.close();
                connected = false;
                Log.d("ClientActivity", "C: Closed.");
            } catch (SocketTimeoutException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Imposible conectar", Toast.LENGTH_SHORT).show();
                    }
                });
                connected = false;
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
                e.printStackTrace();
                connected = false;
            }
        }
    }

    private void enviarTexto(){

    }
}
