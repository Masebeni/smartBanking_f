package com.shane.smartbanking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.shane.smartbanking.domain.Client;
import com.shane.smartbanking.domain.ContactDetails;
import com.shane.smartbanking.utils.JSONConverter;
import org.json.JSONObject;

public class DashBoardActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "MyPrefsFile";
    protected Button checkAccounts;
    protected Button manageAccounts;
    protected Button quit;
    protected Button transaction;

    /* renamed from: com.shane.smartbanking.DashBoardActivity.1 */
    class C01541 implements OnClickListener {
        C01541() {
        }

        public void onClick(View view) {
            DashBoardActivity.this.startActivity(new Intent(view.getContext(), AccountManageActivity.class));
        }
    }

    /* renamed from: com.shane.smartbanking.DashBoardActivity.2 */
    class C01552 implements OnClickListener {
        C01552() {
        }

        public void onClick(View view) {
            DashBoardActivity.this.startActivity(new Intent(view.getContext(), TransactionActivity.class));
        }
    }

    /* renamed from: com.shane.smartbanking.DashBoardActivity.3 */
    class C01563 implements OnClickListener {
        C01563() {
        }

        public void onClick(View view) {
            try {
                if (DashBoardActivity.this.hasAccounts().booleanValue()) {
                    DashBoardActivity.this.startActivity(new Intent(view.getContext(), AccountList.class));
                    return;
                }
                Toast.makeText(DashBoardActivity.this.getApplicationContext(), "No accounts for this user !!", 1).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* renamed from: com.shane.smartbanking.DashBoardActivity.4 */
    class C01574 implements OnClickListener {
        C01574() {
        }

        public void onClick(View view) {
            DashBoardActivity.this.finish();
            System.exit(0);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0160R.layout.activity_dash_board);
        this.manageAccounts = (Button) findViewById(C0160R.id.accountManage);
        this.transaction = (Button) findViewById(C0160R.id.doTransaction);
        this.checkAccounts = (Button) findViewById(C0160R.id.checkAccounts);
        this.quit = (Button) findViewById(C0160R.id.leave);
        this.manageAccounts.setOnClickListener(new C01541());
        this.transaction.setOnClickListener(new C01552());
        this.checkAccounts.setOnClickListener(new C01563());
        this.quit.setOnClickListener(new C01574());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0160R.menu.menu_dash_board, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == C0160R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Boolean hasAccounts() throws Exception {
        ContactDetails contactDetails = JSONConverter.convertJSONObjectToContactDetails(new JSONObject(getSharedPreferences(PREFS_NAME, 0).getString("entireObject", BuildConfig.FLAVOR)));
        if (contactDetails == null || ((Client) contactDetails.getClient().get(0)).getAccounts() == null || ((Client) contactDetails.getClient().get(0)).getAccounts().isEmpty()) {
            return Boolean.valueOf(false);
        }
        return Boolean.valueOf(true);
    }
}
