package com.shane.smartbanking;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.shane.smartbanking.domain.Account;
import com.shane.smartbanking.domain.Account.Builder;
import com.shane.smartbanking.domain.Client;
import com.shane.smartbanking.utils.JSONConverter;
import com.shane.smartbanking.utils.RequestUtils;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

public class AccountManageActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "MyPrefsFile";
    private String accNumber;
    protected EditText accountNumber;
    protected EditText amount;
    private Client client;
    protected Button create;
    private View oldView;
    private String startUpBalance;

    /* renamed from: com.shane.smartbanking.AccountManageActivity.1 */
    class C01531 implements OnClickListener {
        C01531() {
        }

        public void onClick(View view) {
            if (AccountManageActivity.this.hasErrors().booleanValue()) {
                Toast.makeText(AccountManageActivity.this.getApplicationContext(), "One of the Required fields is empty !!", 1).show();
                return;
            }
            try {
                Client currentClient = (Client) JSONConverter.convertJSONObjectToContactDetails(new JSONObject(AccountManageActivity.this.getSharedPreferences(AccountManageActivity.PREFS_NAME, 0).getString("entireObject", BuildConfig.FLAVOR))).getClient().get(0);
                Boolean alreadyExists = Boolean.valueOf(false);
                Account account = new Builder(AccountManageActivity.this.accNumber).balance(Double.valueOf(AccountManageActivity.this.startUpBalance)).build();
                List<Account> accountList = null;
                if (!(currentClient.getAccounts() == null || currentClient.getAccounts().isEmpty())) {
                    accountList = currentClient.getAccounts();
                    for (Account currentAccount : accountList) {
                        if (currentAccount.getNumber().equals(AccountManageActivity.this.accNumber)) {
                            alreadyExists = Boolean.valueOf(true);
                            break;
                        }
                    }
                }
                if (alreadyExists.booleanValue()) {
                    Toast.makeText(AccountManageActivity.this.getApplicationContext(), "The Account Number " + AccountManageActivity.this.accNumber + " already exists for " + currentClient.getFirstName() + " " + currentClient.getLastName() + ".", 1).show();
                    return;
                }
                if (currentClient.getAccounts() == null) {
                    accountList = new ArrayList();
                } else if (!currentClient.getAccounts().isEmpty()) {
                    accountList = currentClient.getAccounts();
                }
                accountList.add(account);
                AccountManageActivity.this.client = new Client.Builder(currentClient.getLastName()).copy(currentClient).accounts(accountList).build();
                AccountManageActivity.this.oldView = view;
                new HttpASyncTask(null).execute(new String[]{"http://smartbanking-paulie.rhcloud.com/clients/" + updatedClient.getId() + "/update/"});
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private class HttpASyncTask extends AsyncTask<String, Void, String> {
        private HttpASyncTask() {
        }

        protected void onPostExecute(String s) {
            if (s != null) {
                try {
                    Client client = JSONConverter.convertJSONObjectToClient(new JSONObject(s));
                    if (client != null) {
                        for (Account current : client.getAccounts()) {
                            if (current.getNumber().equals(AccountManageActivity.this.accNumber)) {
                                Toast.makeText(AccountManageActivity.this.getApplicationContext(), "Account created", 0).show();
                                AccountManageActivity.this.startActivity(new Intent(AccountManageActivity.this.oldView.getContext(), DashBoardActivity.class));
                                return;
                            }
                        }
                        return;
                    }
                    Toast.makeText(AccountManageActivity.this.getApplicationContext(), "Failed to convert object !!", 1).show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        protected String doInBackground(String... params) {
            return AccountManageActivity.this.PUT(params[0], AccountManageActivity.this.client);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0160R.layout.activity_account_manage);
        this.create = (Button) findViewById(C0160R.id.account_create);
        this.accountNumber = (EditText) findViewById(C0160R.id.account_manage_account_number);
        this.amount = (EditText) findViewById(C0160R.id.startup_account_balance);
        this.create.setOnClickListener(new C01531());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0160R.menu.menu_account_manage, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == C0160R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Boolean hasErrors() {
        this.accNumber = this.accountNumber.getText().toString();
        this.startUpBalance = this.amount.getText().toString();
        boolean z = this.accNumber.isEmpty() || this.startUpBalance.isEmpty();
        return Boolean.valueOf(z);
    }

    public String PUT(String url, Client client) {
        String result = BuildConfig.FLAVOR;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpUriRequest httpPut = new HttpPut(url);
            httpPut.setEntity(new StringEntity(JSONConverter.convertClientTOJSON(client).toString()));
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-type", "application/json");
            InputStream inputStream = httpClient.execute(httpPut).getEntity().getContent();
            if (inputStream != null) {
                return RequestUtils.convertInputStreamToString(inputStream);
            }
            return "Error occurred";
        } catch (Exception ex) {
            Log.d("InputStream ", ex.getLocalizedMessage());
            return result;
        }
    }
}
