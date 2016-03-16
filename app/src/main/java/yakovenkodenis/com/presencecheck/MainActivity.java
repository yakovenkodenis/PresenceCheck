package yakovenkodenis.com.presencecheck;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.net.UnknownHostException;

import yakovenkodenis.com.presencecheck.utils.network.InetUtils;
import yakovenkodenis.com.presencecheck.utils.network.WifiTetheringUtils;


public class MainActivity extends AppCompatActivity {

    private WebServer server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Snackbar.make(view, InetUtils.getLocalHostLANAddress().toString(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    new WifiTetheringAsyncTask(true, getApplicationContext()).execute();

                } catch (UnknownHostException e) {
                    Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    e.printStackTrace();
                }
            }
        });

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff),
                (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

        Log.d("IP_ADDRESS", formatedIpAddress);

        Toast.makeText(this, "http://" + formatedIpAddress + ":8080", Toast.LENGTH_LONG).show();

        server = new WebServer();



        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (server != null) {
            server.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (server != null) {
            server.stop();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class WifiTetheringAsyncTask extends AsyncTask<Void, Void, Void> {

        private boolean enabled;

        private Context context;

        public WifiTetheringAsyncTask(boolean enabled, Context context) {
            this.enabled = enabled;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... booleans) {
            WifiTetheringUtils.setWifiTetheringEnabled(enabled, context);

            return null;
        }
    }

}
