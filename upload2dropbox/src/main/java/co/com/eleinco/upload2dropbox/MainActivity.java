package co.com.eleinco.upload2dropbox;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String ACCESS_TOKEN = "EHthxjha7PUAAAAAAAAKTT84L9myCGtlmVro8IcwQG1YZom3UqonyvmoIkoKB3yg";
    // Token para usar el dropbox de desarrolloaplicaciones@eleinco.com.co


    private FullAccount account;
    private DbxClientV2 client;
    private DbxRequestConfig config;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv_QuienSoy);

        config = new DbxRequestConfig("Reporte_eventos_Eleinco");
        client = new DbxClientV2(config, ACCESS_TOKEN);

        account = null;

        new AutenticacionDropbox().execute();
    }


    private class AutenticacionDropbox extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String toReturn = null;
            try {
                account = client.users().getCurrentAccount();
                toReturn = "ok";
            } catch (DbxException e) {
                e.printStackTrace();
            }
            return toReturn;
        }

        @Override
        protected void onPostExecute(String s) {
            if( s.equals("ok") ) tv.setText(account.getName().getDisplayName());
        }
    }
}
