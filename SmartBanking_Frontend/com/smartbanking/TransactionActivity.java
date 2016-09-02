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
import android.widget.RadioButton;
import android.widget.Toast;
import com.shane.smartbanking.domain.Account;
import com.shane.smartbanking.domain.Account.Builder;
import com.shane.smartbanking.domain.Client;
import com.shane.smartbanking.utils.JSONConverter;
import com.shane.smartbanking.utils.RequestUtils;
import java.io.InputStream;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

public class TransactionActivity extends AppCompatActivity {
    protected EditText accountNumber;
    private String accountNumberValue;
    private Account accountToUpdate;
    protected EditText amount;
    private String amountValue;
    protected EditText cellNumber;
    private String cellValue;
    protected RadioButton deposit;
    protected View oldView;
    protected Button processTransaction;
    private Integer transType;
    protected RadioButton withdrawal;

    /* renamed from: com.shane.smartbanking.TransactionActivity.1 */
    class C01621 implements OnClickListener {
        C01621() {
        }

        public void onClick(View view) {
            TransactionActivity.this.transType = Integer.valueOf(0);
        }
    }

    /* renamed from: com.shane.smartbanking.TransactionActivity.2 */
    class C01632 implements OnClickListener {
        C01632() {
        }

        public void onClick(View view) {
            TransactionActivity.this.transType = Integer.valueOf(1);
        }
    }

    /* renamed from: com.shane.smartbanking.TransactionActivity.3 */
    class C01643 implements OnClickListener {
        C01643() {
        }

        public void onClick(View view) {
            if (TransactionActivity.this.hasErrors().booleanValue()) {
                Toast.makeText(TransactionActivity.this.getApplicationContext(), "One of the required fields is empty !!", 1).show();
                return;
            }
            new HttpASyncTask(null).execute(new String[]{"http://smartbanking-paulie.rhcloud.com/contact/search/" + TransactionActivity.this.cellValue + "/"});
            TransactionActivity.this.oldView = view;
        }
    }

    private class HttpASyncPUT extends AsyncTask<String, Void, String> {
        private HttpASyncPUT() {
        }

        protected String doInBackground(String... params) {
            return TransactionActivity.this.PUT(params[0], TransactionActivity.this.accountToUpdate);
        }

        protected void onPostExecute(String s) {
            if (s != null) {
                try {
                    Toast.makeText(TransactionActivity.this.getApplicationContext(), "Transaction Successful !!", 1).show();
                    TransactionActivity.this.startActivity(new Intent(TransactionActivity.this.oldView.getContext(), DashBoardActivity.class));
                    return;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }
            }
            Toast.makeText(TransactionActivity.this.getApplicationContext(), "Error occurred", 1).show();
        }
    }

    private class HttpASyncTask extends AsyncTask<String, Void, String> {
        private HttpASyncTask() {
        }

        protected void onPostExecute(String s) {
            if (s != null) {
                try {
                    if (!s.equals(BuildConfig.FLAVOR)) {
                        Client client = (Client) JSONConverter.convertJSONObjectToContactDetails(new JSONObject(s)).getClient().get(0);
                        Boolean accountFound = Boolean.valueOf(false);
                        if (client != null && client.getAccounts() != null && !client.getAccounts().isEmpty()) {
                            for (Account current : client.getAccounts()) {
                                if (current.getNumber().equals(TransactionActivity.this.accountNumberValue)) {
                                    accountFound = Boolean.valueOf(true);
                                    if (TransactionActivity.this.transType.intValue() == 1) {
                                        Double readBalance = current.getBalance();
                                        if (readBalance.doubleValue() - Double.valueOf(TransactionActivity.this.amountValue).doubleValue() < 0.0d) {
                                            Toast.makeText(TransactionActivity.this.getApplicationContext(), "Insufficient funds on this Account!", 1).show();
                                        } else {
                                            TransactionActivity.this.accountToUpdate = new Builder(current.getNumber()).copy(current).balance(Double.valueOf(readBalance.doubleValue() - Double.valueOf(TransactionActivity.this.amountValue).doubleValue())).build();
                                        }
                                    } else {
                                        TransactionActivity.this.accountToUpdate = new Builder(current.getNumber()).copy(current).balance(Double.valueOf(current.getBalance().doubleValue() + Double.valueOf(TransactionActivity.this.amountValue).doubleValue())).build();
                                    }
                                    new HttpASyncPUT(null).execute(new String[]{"http://smartbanking-paulie.rhcloud.com/accounts/update/"});
                                    if (!accountFound.booleanValue()) {
                                        Toast.makeText(TransactionActivity.this.getApplicationContext(), "Account not found !!", 1).show();
                                    }
                                }
                            }
                            if (!accountFound.booleanValue()) {
                                Toast.makeText(TransactionActivity.this.getApplicationContext(), "Account not found !!", 1).show();
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        protected String doInBackground(String... params) {
            return TransactionActivity.this.GET(params[0]);
        }
    }

    public TransactionActivity() {
        this.accountToUpdate = null;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0160R.layout.activity_transaction);
        this.processTransaction = (Button) findViewById(C0160R.id.processTransaction);
        this.cellNumber = (EditText) findViewById(C0160R.id.transactionCellNumber);
        this.accountNumber = (EditText) findViewById(C0160R.id.transactionAccountNumber);
        this.amount = (EditText) findViewById(C0160R.id.transactionAmount);
        this.deposit = (RadioButton) findViewById(C0160R.id.depositTransaction);
        this.withdrawal = (RadioButton) findViewById(C0160R.id.withdrawalTransaction);
        this.deposit.setOnClickListener(new C01621());
        this.withdrawal.setOnClickListener(new C01632());
        this.processTransaction.setOnClickListener(new C01643());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0160R.menu.menu_transaction, menu);
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

    public String PUT(String url, Account account) {
        String result = BuildConfig.FLAVOR;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpUriRequest httpPut = new HttpPut(url);
            httpPut.setEntity(new StringEntity(JSONConverter.convertAccountToJSON(account).toString()));
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-type", "application/json");
            InputStream inputStream = httpClient.execute(httpPut).getEntity().getContent();
            if (inputStream != null) {
                return RequestUtils.convertInputStreamToString(inputStream);
            }
            return "Error occurred";
        } catch (Exception ex) {
            ex.printStackTrace();
            return result;
        }
    }

    public Boolean hasErrors() {
        this.cellValue = this.cellNumber.getText().toString();
        this.accountNumberValue = this.accountNumber.getText().toString();
        this.amountValue = this.amount.getText().toString();
        boolean z = this.cellValue.isEmpty() || this.accountNumberValue.isEmpty() || this.amountValue.isEmpty();
        return Boolean.valueOf(z);
    }
}
