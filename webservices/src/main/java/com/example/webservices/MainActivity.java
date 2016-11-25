package com.example.webservices;

import android.app.Activity;
//import android.support.v7.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    String urlImagen = "https://dl.dropboxusercontent.com/u/39626335/captain_america_civil_war_6-wallpaper-640x960.jpg";
    //String urlTexto = "https://dl.dropboxusercontent.com/u/39626335/pruebaWebServices.txt";
    String urlTexto = "https://dl.dropboxusercontent.com/u/39626335/pruebaEleinco_mapas.txt";
    //String urlTexto = "https://www.dropbox.com/s/livuxsw8wmk6tsr/pruebaEleinco_mapas.txt?dl=0";

    RequestQueue requestQueue;
    StringRequest request;
    ImageRequest imageRequest;

    TextView tv;
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this); // Creando la cola de peticiones
        tv = (TextView) findViewById(R.id.textoPrueba);
        iv = (ImageView) findViewById(R.id.imagenPrueba);
    }


    public void descargarImagen(View view) {
        imageRequest = new ImageRequest(urlImagen, new Response.Listener<Bitmap>(){
                    @Override
                    public void onResponse(Bitmap response) {
                        iv.setImageBitmap(response);
                    }
                }, 0, 0, null, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplication(), "No se pudo descargar imagen", Toast.LENGTH_SHORT).show();
                    }
                }
                );
        imageRequest.setRetryPolicy( new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(imageRequest);
    }

    public void descargarTexto(View view) {
        request = new StringRequest(urlTexto,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        tv.setText(response);
                    }
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplication(), "Error: No se pudo descargar el texto", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        request = new StringRequest(Request.Method.POST,urlTexto,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        tv.setText(response);
                    }
                }, new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplication(), "Error: No se pudo descargar el texto", Toast.LENGTH_SHORT).show();
                        }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("nombre", "david");
                params.put("password", "pass");
                params.put("email", "ludagoga@hola.com");
                return super.getParams();
            }
        };
        request.setRetryPolicy( new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }
}
