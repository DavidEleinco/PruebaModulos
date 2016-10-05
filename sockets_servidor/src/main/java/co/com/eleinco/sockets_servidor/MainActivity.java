package co.com.eleinco.sockets_servidor;

import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private ServerSocket serverSocket;
    Handler updateConversationHandler;
    Thread serverThread = null;

    private TextView text;
    private TextView ip_puerto;

    private static final int SERVERPORT = 6000;
    private int SERVERIP;

    class ServerThread implements Runnable {
        @Override
        public void run() {
            Socket socket = null;
            try{
                serverSocket = new ServerSocket(SERVERPORT);
            }catch (IOException e){
                e.printStackTrace();
            }

            while( !Thread.currentThread().isInterrupted() ){
                try{
                    socket = serverSocket.accept();
                    CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    class CommunicationThread implements Runnable {

        private Socket clientSocket;
        private BufferedReader input;

        public CommunicationThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try{
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while( !Thread.currentThread().isInterrupted() ){
                try{
                    String read = input.readLine();
                    updateConversationHandler.post(new updateUIThread(read) );
                }catch (IOException e){

                }
            }
        }
    }

    class updateUIThread implements Runnable {
        private String msg;

        public updateUIThread(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            text.setText(text.getText().toString()+"Client Says: "+ msg + "\n");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        text = (TextView) findViewById(R.id.main_serverCommunication);
        ip_puerto = (TextView) findViewById(R.id.main_IP_PORT);

        ip_puerto.setText("IP="+ip+" - PORT="+SERVERPORT);

        updateConversationHandler = new Handler();

        this.serverThread = new Thread( new ServerThread());
    }
}
