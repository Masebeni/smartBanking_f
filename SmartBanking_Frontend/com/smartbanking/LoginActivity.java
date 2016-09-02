package com.shane.smartbanking;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
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
import com.shane.smartbanking.domain.Client;
import com.shane.smartbanking.domain.ContactDetails;
import com.shane.smartbanking.utils.JSONConverter;
import com.shane.smartbanking.utils.RequestUtils;
import java.io.InputStream;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "MyPrefsFile";
    private EditText cellNumber;
    private Button loginBtn;
    private View oldView;
    private EditText password;
    private Button registerBtn;

    /* renamed from: com.shane.smartbanking.LoginActivity.1 */
    class C01581 implements OnClickListener {
        C01581() {
        }

        public void onClick(View view) {
            LoginActivity.this.startActivity(new Intent(view.getContext(), RegisterActivity.class));
        }
    }

    /* renamed from: com.shane.smartbanking.LoginActivity.2 */
    class C01592 implements OnClickListener {
        C01592() {
        }

        public void onClick(View view) {
            if (LoginActivity.this.cellNumber.getText().toString().trim().equals(BuildConfig.FLAVOR) || LoginActivity.this.password.getText().toString().trim().equals(BuildConfig.FLAVOR)) {
                Toast.makeText(LoginActivity.this.getApplicationContext(), "Required field empty !!", 1).show();
                return;
            }
            String userCellNumber = LoginActivity.this.cellNumber.getText().toString();
            LoginActivity.this.oldView = view;
            new HttpASyncTask(null).execute(new String[]{"http://smartbanking-paulie.rhcloud.com/contact/search/" + userCellNumber + "/"});
        }
    }

    private class HttpASyncTask extends AsyncTask<String, Void, String> {
        private HttpASyncTask() {
        }

        protected String doInBackground(String... params) {
            return LoginActivity.this.GET(params[0]);
        }

        protected void onPostExecute(String s) {
            if (s != null) {
                try {
                    if (!s.equals(BuildConfig.FLAVOR)) {
                        ContactDetails contactDetails = JSONConverter.convertJSONObjectToContactDetails(new JSONObject(s));
                        if (contactDetails.getId() != null) {
                            String pass = LoginActivity.this.password.getText().toString();
                            if (contactDetails.getPassword() == null || !contactDetails.getPassword().equals(pass)) {
                                Toast.makeText(LoginActivity.this.getApplicationContext(), "Wrong password !!", 1).show();
                            } else if (contactDetails.getClient() == null || contactDetails.getClient().isEmpty()) {
                                Toast.makeText(LoginActivity.this.getApplicationContext(), "Failed to load Client object !!", 1).show();
                            } else {
                                Client foundClient = null;
                                for (Client current : contactDetails.getClient()) {
                                    if (current != null) {
                                        foundClient = current;
                                        break;
                                    }
                                }
                                Editor editor = LoginActivity.this.getSharedPreferences(LoginActivity.PREFS_NAME, 0).edit();
                                editor.putString("entireObject", JSONConverter.convertContactDetailsToJSON(contactDetails).toString());
                                editor.apply();
                                Toast.makeText(LoginActivity.this.getApplicationContext(), "Welcome to Smart Bank \n " + foundClient.getFirstName() + " " + foundClient.getLastName(), 1).show();
                                LoginActivity.this.startActivity(new Intent(LoginActivity.this.oldView.getContext(), DashBoardActivity.class));
                            }
                        }
                        LoginActivity.this.cellNumber.setText(BuildConfig.FLAVOR);
                        LoginActivity.this.password.setText(BuildConfig.FLAVOR);
                        super.onPostExecute(s);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            Toast.makeText(LoginActivity.this.getApplicationContext(), "User not found !!", 1).show();
            LoginActivity.this.cellNumber.setText(BuildConfig.FLAVOR);
            LoginActivity.this.password.setText(BuildConfig.FLAVOR);
            super.onPostExecute(s);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0160R.layout.activity_login);
        this.cellNumber = (EditText) findViewById(C0160R.id.loginCell);
        this.password = (EditText) findViewById(C0160R.id.loginPass);
        this.loginBtn = (Button) findViewById(C0160R.id.loginBtn);
        this.registerBtn = (Button) findViewById(C0160R.id.loginRegisterBtn);
        this.registerBtn.setOnClickListener(new C01581());
        this.loginBtn.setOnClickListener(new C01592());
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0160R.menu.menu_login, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == C0160R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
