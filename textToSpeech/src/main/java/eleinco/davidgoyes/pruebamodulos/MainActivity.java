package eleinco.davidgoyes.pruebamodulos;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText paDecir;
    TextToSpeech ttobj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        paDecir = (EditText) findViewById(R.id.texto);

        ttobj=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    ttobj.setLanguage(Locale.getDefault());
                }
            }
        });
    }

    public void suenelo(View view){
        ttobj.speak("Did you speak well?", TextToSpeech.QUEUE_FLUSH, null);
        //ttobj.speak(paDecir.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
    }
}
