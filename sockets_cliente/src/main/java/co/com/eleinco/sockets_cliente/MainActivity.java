package co.com.eleinco.sockets_cliente;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

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
    }

    private void enviarTexto(){

    }
}
