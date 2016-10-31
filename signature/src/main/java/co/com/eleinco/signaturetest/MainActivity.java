package co.com.eleinco.signaturetest;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.github.gcacace.signaturepad.views.SignaturePad;


public class MainActivity extends AppCompatActivity {

    private Button b_guardar;
    private Button b_borrar;
    private SignaturePad firma;
    private ImageView mostrarFirma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b_guardar = (Button) findViewById(R.id.main_botonGuardarFirma);
        b_borrar = (Button) findViewById(R.id.main_botonBorrarFirma);
        firma = (SignaturePad) findViewById(R.id.main_signaturePad);
        mostrarFirma = (ImageView) findViewById(R.id.main_AquiSeVaAMostrarLaFirma);

        b_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarFirma();
            }
        });

        b_borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarFirma();
            }
        });
    }

    private void guardarFirma(){
        Bitmap firmaLista = firma.getSignatureBitmap();
        //getSignatureBitmap(); // A signature bitmap with a white background
        //getTransparentSignatureBitmap(); // A signature bitmap with a transparent background
        mostrarFirma.setImageBitmap(firmaLista);
    }

    private void borrarFirma(){
        firma.clear();
    }
}
