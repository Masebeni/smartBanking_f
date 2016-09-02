package com.shane.smartbanking.utils;

import android.util.Log;
import com.google.gson.Gson;
import com.shane.smartbanking.domain.Account;
import com.shane.smartbanking.domain.Client;
import com.shane.smartbanking.domain.ContactDetails;
import com.shane.smartbanking.domain.Transaction;
import org.json.JSONObject;

public class JSONConverter {
    public static ContactDetails convertJSONObjectToContactDetails(JSONObject jsonObject) {
        ContactDetails contactDetails = null;
        if (jsonObject == null) {
            return contactDetails;
        }
        try {
            return (ContactDetails) new Gson().fromJson(jsonObject.toString(), ContactDetails.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return contactDetails;
        }
    }

    public static Account convertJSONObjectToAccount(JSONObject jsonObject) {
        Account account = null;
        if (jsonObject == null) {
            return account;
        }
        try {
            return (Account) new Gson().fromJson(jsonObject.toString(), Account.class);
        } catch (Exception ex) {
            Log.d("JSON Conversion ", ex.getLocalizedMessage());
            return account;
        }
    }

    public static Transaction convertJSONObjectToTransaction(JSONObject jsonObject) {
        Transaction transaction = null;
        if (jsonObject == null) {
            return transaction;
        }
        try {
            return (Transaction) new Gson().fromJson(jsonObject.toString(), Transaction.class);
        } catch (Exception ex) {
            Log.d("JSON Conversion ", ex.getLocalizedMessage());
            return transaction;
        }
    }

    public static JSONObject convertTransactionToJSON(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        try {
            return new JSONObject(new Gson().toJson((Object) transaction));
        } catch (Exception ex) {
            Log.d("JSON Conversion ", ex.getLocalizedMessage());
            return null;
        }
    }

    public static JSONObject convertAccountToJSON(Account account) {
        if (account == null) {
            return null;
        }
        try {
            return new JSONObject(new Gson().toJson((Object) account));
        } catch (Exception ex) {
            Log.d("JSON Conversion ", ex.getLocalizedMessage());
            return null;
        }
    }

    public static JSONObject convertClientTOJSON(Client client) {
        if (client == null) {
            return null;
        }
        try {
            return new JSONObject(new Gson().toJson((Object) client));
        } catch (Exception ex) {
            Log.d("JSON Conversion ", ex.getLocalizedMessage());
            return null;
        }
    }

    public static JSONObject convertContactDetailsToJSON(ContactDetails contactDetails) {
        if (contactDetails == null) {
            return null;
        }
        try {
            return new JSONObject(new Gson().toJson((Object) contactDetails));
        } catch (Exception ex) {
            Log.d("JSON Conversion ", ex.getLocalizedMessage());
            return null;
        }
    }

    public static Client convertJSONObjectToClient(JSONObject jsonObject) {
        Client client = null;
        if (jsonObject == null) {
            return client;
        }
        try {
            return (Client) new Gson().fromJson(jsonObject.toString(), Client.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return client;
        }
    }
}
