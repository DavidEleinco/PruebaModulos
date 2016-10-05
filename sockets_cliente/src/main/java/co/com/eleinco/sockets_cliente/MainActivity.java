package co.com.eleinco.sockets_cliente;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private Socket socket;
    private static final int SERVERPORT = 6000;
    private static final String SERVERIP = "192.168.0.13";

    Button bt_enviar;
    EditText et_texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_enviar = (Button) findViewById(R.id.main_bt_enviar);
        et_texto = (EditText) findViewById(R.id.main_et_loquevoyadecir);

        bt_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarTexto();
            }
        });

        new Thread(new ClientThread()).start();
    }

    private void enviarTexto(){
        try{
            String str = et_texto.getText().toString();
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            out.println(str);
        }catch (UnknownHostException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    class ClientThread implements Runnable{

        @Override
        public void run() {
            try {
                InetAddress serAddress = InetAddress.getByName(SERVERIP);
                socket = new Socket(serAddress, SERVERPORT);
                Toast.makeText(MainActivity.this, "Funciona", Toast.LENGTH_SHORT).show();
            }catch (UnknownHostException e1){
                e1.printStackTrace();
            }catch (IOException e1){
                e1.printStackTrace();
            }
        }
    }
}
