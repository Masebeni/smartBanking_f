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
import com.shane.smartbanking.domain.Client;
import com.shane.smartbanking.domain.Client.Builder;
import com.shane.smartbanking.domain.ContactDetails;
import com.shane.smartbanking.utils.JSONConverter;
import com.shane.smartbanking.utils.RequestUtils;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    protected String age;
    protected EditText ageValue;
    protected String cellNumber;
    protected EditText cellNumberValue;
    protected String confirm;
    protected EditText confirmValue;
    private View currentView;
    protected String emailAddress;
    protected EditText emailValue;
    protected String firstName;
    protected String lastName;
    protected EditText nameValue;
    private ContactDetails newContactDetails;
    protected String password;
    protected EditText passwordValue;
    protected Button register;
    protected EditText surnameValue;

    /* renamed from: com.shane.smartbanking.RegisterActivity.1 */
    class C01611 implements OnClickListener {
        C01611() {
        }

        public void onClick(View view) {
            if (RegisterActivity.this.errorsFound().booleanValue()) {
                Toast.makeText(RegisterActivity.this.getApplicationContext(), "One of the required fields is empty!!", 1).show();
                return;
            }
            RegisterActivity.this.newContactDetails = RegisterActivity.this.getContactDetailsObject();
            new HttpASyncTask(null).execute(new String[]{"http://smartbanking-paulie.rhcloud.com/contact/create/"});
            RegisterActivity.this.currentView = view;
        }
    }

    private class HttpASyncTask extends AsyncTask<String, Void, String> {
        private HttpASyncTask() {
        }

        protected String doInBackground(String... params) {
            return RegisterActivity.this.POST(params[0], RegisterActivity.this.newContactDetails);
        }

        protected void onPostExecute(String s) {
            if (s != null) {
                try {
                    ContactDetails contactDetails = JSONConverter.convertJSONObjectToContactDetails(new JSONObject(s));
                    if (contactDetails == null) {
                        Toast.makeText(RegisterActivity.this.getApplicationContext(), "An Error occurred !!", 1).show();
                    } else if (contactDetails.getClient() == null || contactDetails.getClient().isEmpty()) {
                        Toast.makeText(RegisterActivity.this.getApplicationContext(), "Could not create client !!", 1).show();
                    } else {
                        Client foundClient = null;
                        Iterator it = contactDetails.getClient().iterator();
                        if (it.hasNext()) {
                            foundClient = (Client) it.next();
                        }
                        if (foundClient != null) {
                            Toast.makeText(RegisterActivity.this.getApplicationContext(), "Welcome to Smart Bank " + foundClient.getFirstName() + " " + foundClient.getLastName(), 1).show();
                        }
                        RegisterActivity.this.startActivity(new Intent(RegisterActivity.this.currentView.getContext(), LoginActivity.class));
                    }
                } catch (Exception ex) {
                    Log.d("Exception ", ex.getLocalizedMessage());
                }
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0160R.layout.activity_register);
        this.nameValue = (EditText) findViewById(C0160R.id.firstNameValue);
        this.surnameValue = (EditText) findViewById(C0160R.id.lastNameValue);
        this.ageValue = (EditText) findViewById(C0160R.id.ageValue);
        this.cellNumberValue = (EditText) findViewById(C0160R.id.cellNumberValue);
        this.emailValue = (EditText) findViewById(C0160R.id.emailValue);
        this.passwordValue = (EditText) findViewById(C0160R.id.passwordValue);
        this.confirmValue = (EditText) findViewById(C0160R.id.confirmValue);
        this.register = (Button) findViewById(C0160R.id.userSave);
        this.register.setOnClickListener(new C01611());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0160R.menu.menu_register, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == C0160R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String POST(String url, ContactDetails contactDetails) {
        String result = BuildConfig.FLAVOR;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpUriRequest httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(JSONConverter.convertContactDetailsToJSON(contactDetails).toString()));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            InputStream inputStream = httpClient.execute(httpPost).getEntity().getContent();
            if (inputStream != null) {
                return RequestUtils.convertInputStreamToString(inputStream);
            }
            return "Error occurred";
        } catch (Exception ex) {
            Log.d("InputStream ", ex.getLocalizedMessage());
            return result;
        }
    }

    private Boolean errorsFound() {
        this.firstName = this.nameValue.getText().toString();
        this.lastName = this.surnameValue.getText().toString();
        this.age = this.ageValue.getText().toString();
        this.cellNumber = this.cellNumberValue.getText().toString();
        this.emailAddress = this.emailValue.getText().toString();
        this.password = this.passwordValue.getText().toString();
        this.confirm = this.confirmValue.getText().toString();
        boolean z = this.firstName.isEmpty() || this.lastName.isEmpty() || this.age.isEmpty() || this.cellNumber.isEmpty() || this.emailAddress.isEmpty() || this.password.isEmpty() || this.confirm.isEmpty();
        return Boolean.valueOf(z);
    }

    private ContactDetails getContactDetailsObject() {
        if (!errorsFound().booleanValue()) {
            if (this.password.equals(this.confirm)) {
                Client client = new Builder(this.lastName).age(Integer.valueOf(Integer.parseInt(this.age))).firstName(this.firstName).build();
                List<Client> clientList = new ArrayList();
                clientList.add(client);
                return new ContactDetails.Builder(this.cellNumber).email(this.emailAddress).password(this.password).client(clientList).build();
            }
            Toast.makeText(getApplicationContext(), "Passwords doesn't match !!", 1).show();
        }
        return null;
    }
}
