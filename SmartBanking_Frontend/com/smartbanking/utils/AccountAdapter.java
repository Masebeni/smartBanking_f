package com.shane.smartbanking.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.shane.smartbanking.C0160R;
import com.shane.smartbanking.domain.Account;
import java.util.List;

public class AccountAdapter extends ArrayAdapter<Account> {
    private final List<Account> accountArrayList;
    private final Context context;

    public AccountAdapter(Context context, List<Account> accountList) {
        super(context, C0160R.layout.row, accountList);
        this.context = context;
        this.accountArrayList = accountList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(C0160R.layout.row, parent, false);
        TextView balanceView = (TextView) rowView.findViewById(C0160R.id.row_account_balance);
        ((TextView) rowView.findViewById(C0160R.id.row_account_number)).setText(((Account) this.accountArrayList.get(position)).getNumber());
        balanceView.setText("R " + ((Account) this.accountArrayList.get(position)).getBalance().toString());
        return rowView;
    }
}
