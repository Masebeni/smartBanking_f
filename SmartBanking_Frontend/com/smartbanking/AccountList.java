package com.shane.smartbanking;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import com.shane.smartbanking.domain.Client;
import com.shane.smartbanking.utils.AccountAdapter;
import com.shane.smartbanking.utils.JSONConverter;
import com.shane.smartbanking.utils.RequestUtils;
import java.io.InputStream;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

public class AccountList extends AppCompatActivity {
    private static final String PREFS_NAME = "MyPrefsFile";
    private ListView listView;

    private class HttpASyncTask extends AsyncTask<String, Void, String> {
        private HttpASyncTask() {
        }

        protected void onPostExecute(String s) {
            if (s != null) {
                try {
                    Client client = JSONConverter.convertJSONObjectToClient(new JSONObject(s));
                    if (client.getAccounts() == null) {
                        Toast.makeText(AccountList.this.getApplicationContext(), "No Accounts for this user!", 1).show();
                    } else if (!client.getAccounts().isEmpty()) {
                        AccountAdapter adapter = new AccountAdapter(AccountList.this, client.getAccounts());
                        AccountList.this.listView = (ListView) AccountList.this.findViewById(C0160R.id.list);
                        AccountList.this.listView.setAdapter(adapter);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        protected String doInBackground(String... params) {
            return AccountList.this.GET(params[0]);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0160R.layout.activity_account_list);
        try {
            Integer clientID = ((Client) JSONConverter.convertJSONObjectToContactDetails(new JSONObject(getSharedPreferences(PREFS_NAME, 0).getString("entireObject", BuildConfig.FLAVOR))).getClient().get(0)).getId();
            new HttpASyncTask().execute(new String[]{"http://smartbanking-paulie.rhcloud.com/clients/" + clientID + "/"});
        } catch (Exception ex) {
            Log.d("Exception ", ex.getLocalizedMessage());
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0160R.menu.menu_account_list, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == C0160R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String GET(String url) {
        String result = BuildConfig.FLAVOR;
        try {
            InputStream inputStream = new DefaultHttpClient().execute(new HttpGet(url)).getEntity().getContent();
            if (inputStream != null) {
                return RequestUtils.convertInputStreamToString(inputStream);
            }
            return "Error Occurred";
        } catch (Exception ex) {
            Log.d("InputStream", ex.getLocalizedMessage());
            return result;
        }
    }
}
