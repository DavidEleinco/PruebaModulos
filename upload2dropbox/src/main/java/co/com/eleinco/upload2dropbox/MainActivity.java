package co.com.eleinco.upload2dropbox;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String ACCESS_TOKEN = "EHthxjha7PUAAAAAAAAKTT84L9myCGtlmVro8IcwQG1YZom3UqonyvmoIkoKB3yg";
    // Token para usar el dropbox de desarrolloaplicaciones@eleinco.com.co
    private static final String DIRECTORIO_REPORTES = "RE_ELEINCO";

    private FullAccount account;
    private DbxClientV2 client;
    private DbxRequestConfig config;
    private TextView tv;
    private Context contextoMain;
    private Button b_enviar;
    private Button b_descargar;
    private EditText et_cedula;
    private EditText et_reporte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contextoMain = this;

        tv = (TextView) findViewById(R.id.tv_QuienSoy);

        et_cedula = (EditText) findViewById(R.id.main_et_cedula);
        et_reporte = (EditText) findViewById(R.id.main_et_reporte);

        b_enviar = (Button) findViewById(R.id.main_bt_enviar);
        b_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( account == null ){
                    Toast.makeText(contextoMain, "Dropbox: Error de conexión.", Toast.LENGTH_SHORT).show();
                }else {
                    enviarReporte();
                }
            }
        });

        b_descargar = (Button) findViewById(R.id.main_bt_descargar);
        b_descargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (account == null) {
                    Toast.makeText(contextoMain, "Dropbox: Error de conexión.", Toast.LENGTH_SHORT).show();
                } else {
                    new DescargarReporteDropbox().execute();
                }
            }
        });

        config = new DbxRequestConfig("Reporte_eventos_Eleinco");
        client = new DbxClientV2(config, ACCESS_TOKEN);

        account = null;

        new AutenticacionDropbox().execute();
    }

    private class DescargarReporteDropbox extends AsyncTask<Void, Void, String> {
        List<Metadata> metadata;
        List<String> archivos;

        @Override
        protected String doInBackground(Void... params) {
            ListFolderResult result = null;
            try {
                result = client.files().listFolder("/asignaciones");
            } catch (DbxException e) {
                e.printStackTrace();
                return "fail";
            }

            metadata = result.getEntries();

            OutputStream out = null;
            String dataDescargada;
            try {
                out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + DIRECTORIO_REPORTES + "/reportesDescargados.txt");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "Falla creando archivo";
            }

            for (Metadata m : metadata) {
                try {
                    client.files().downloadBuilder(m.getPathLower()).download(out);
                } catch (DbxException e) {
                    e.printStackTrace();
                    return "fail";
                } catch (IOException e) {
                    e.printStackTrace();
                    return "fail";
                }

                dataDescargada = cargarArchivoEnString(Environment.getExternalStorageDirectory() + "/" + DIRECTORIO_REPORTES + "/reportesDescargados.txt");

                if (dataDescargada.contains("$123$")) {
                    archivos.add(m.getPathLower() + " " + dataDescargada);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null) {
                for (int i = 0; i < archivos.size(); i++) {
                    Toast.makeText(contextoMain, archivos.get(i), Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(contextoMain, "Dropbox: Error de conexión. " + s, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private String cargarArchivoEnString(String path) {
        File file = new File(path);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }


    private void enviarReporte(){

        String fecha = getFechaActual();
        String hora = getHoraActual();
        String numeroReporte = "0"; // TODO traerse el numero de reporte con sharedPreference
        String cedula = et_cedula.getText().toString();
        String nombreArchivo = "r"+numeroReporte+".txt";

        String dataReporte = et_reporte.getText().toString();

        File file = new File(Environment.getExternalStorageDirectory() + "/" + DIRECTORIO_REPORTES );
        boolean isDirectoryCreated = file.exists();
        if(!isDirectoryCreated)
            isDirectoryCreated = file.mkdirs();

        try {
            file = new File(Environment.getExternalStorageDirectory() + "/" + DIRECTORIO_REPORTES, nombreArchivo);
            FileOutputStream stream = new FileOutputStream(file);
            try {
                stream.write((fecha + " " + hora +"\n").getBytes());
                stream.write((cedula +"\n").getBytes());
                stream.write((dataReporte+"\n").getBytes());
                stream.close();
            } finally {
                stream.close();
            }
        }catch (IOException e) {
            Toast.makeText(contextoMain, "Falla en la creacion del reporte.", Toast.LENGTH_SHORT).show();
            Log.e("Exception", "File write failed: " + e.toString());
        }

        new EnviarArchivoADropbox(Environment.getExternalStorageDirectory() + "/" + DIRECTORIO_REPORTES + "/" + nombreArchivo, "/" + cedula + "/" + nombreArchivo).execute();

        //Foto
        //new EnviarArchivoADropbox(Environment.getExternalStorageDirectory() + "/WhatsApp/Media/WhatsApp Images/fotoPrueba.jpg", "/fotoPrueba.jpg").execute();

        //Video
        //new EnviarArchivoADropbox(Environment.getExternalStorageDirectory() + "/WhatsApp/Media/WhatsApp Video/videoPrueba.mp4", "/videoPrueba.mp4").execute();
    }

    private class EnviarArchivoADropbox extends AsyncTask<Void, Void, String> {

        private String nombreArchivoLocal;
        private String nombreArchivoDropbox;


        public EnviarArchivoADropbox(String nombreArchivoLocal, String nombreArchivoDropbox) {
            this.nombreArchivoLocal = nombreArchivoLocal;
            this.nombreArchivoDropbox = nombreArchivoDropbox;
        }

        @Override
        protected String doInBackground(Void... params) {
            InputStream in = null;
            try {
                in = new FileInputStream(nombreArchivoLocal);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "fail";
            }

            try {
                client.files().uploadBuilder(nombreArchivoDropbox).withAutorename(true).uploadAndFinish(in);
            } catch (DbxException e) {
                e.printStackTrace();
                return "fail";
            } catch (IOException e) {
                e.printStackTrace();
                return "fail";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if( s == null ){
                Toast.makeText(contextoMain, "Reporte enviado exitosamente.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(contextoMain, "Falla en el envio del reporte.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class AutenticacionDropbox extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                account = client.users().getCurrentAccount();
            } catch (DbxException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (account != null) {
                tv.setText(account.getName().getDisplayName());
            } else {
                Toast.makeText(contextoMain, "Dropbox: Error de conexión.", Toast.LENGTH_SHORT).show();
            }
        }
    }







    private String getFechaActual(){
        Calendar cal = Calendar.getInstance();
        int actualYear = cal.get(Calendar.YEAR);
        int actualMonth = cal.get(Calendar.MONTH)+1;
        int actualDay = cal.get(Calendar.DAY_OF_MONTH);

        String fechaFormatoCR = date2string(actualDay, actualMonth, actualYear);

        return fechaFormatoCR;
    }

    private String getHoraActual(){
        Calendar cal = Calendar.getInstance();
        int actualHour = cal.get(Calendar.HOUR_OF_DAY);
        int actualMinute = cal.get(Calendar.MINUTE);
        int actualSeconds = cal.get(Calendar.SECOND);

        String fechaFormatoCR = time2string(actualHour, actualMinute, actualSeconds);

        return fechaFormatoCR;
    }

    private String time2string(int hour, int minute, int sec){
        String sTime;
        String sminute;
        String shour;
        String sseconds;
        if( sec < 10 ){
            sseconds = "0" + Integer.toString(sec);
        }else{
            sseconds = Integer.toString(sec);
        }
        if( minute < 10 ){
            sminute = "0"+Integer.toString(minute);
        }else{
            sminute = Integer.toString(minute);
        }
        if( hour < 10 ){
            shour = "0"+Integer.toString(hour);
        }else{
            shour = Integer.toString(hour);
        }
        sTime = shour + ":" + sminute + ":" + sseconds;
        return sTime;
    }

    private String date2string(int day, int month, int year){
        String sDate;
        String sday;
        String smonth;
        String syear;

        // Convertir a string
        if( day < 10 ){
            sday = "0"+Integer.toString(day);
        }else{
            sday = Integer.toString(day);
        }
        if( month < 10 ){
            smonth = "0"+Integer.toString(month);
        }else{
            smonth = Integer.toString(month);
        }
        syear = Integer.toString(year);
        sDate = syear + "/" + smonth + "/" + sday;
        return sDate;
    }
}
