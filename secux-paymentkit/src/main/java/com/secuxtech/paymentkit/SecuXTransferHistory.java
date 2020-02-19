package com.secuxtech.paymentkit;

/**
 * Created by maochuns.sun@gmail.com on 2020-02-19
 */

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by maochuns.sun@gmail.com on 2020-02-19
 */

import static com.secuxtech.paymentkit.RestRequestHandler.TAG;

public class SecuXTransferHistory {
    public String mTxHash           = "";
    public String mAddress          = "";
    public String mTxType           = "";
    public Double mAmount           = Double.valueOf(0);
    public String mAmountSymbol     = "";
    public String mFeeSymbol        = "";
    public Double mFormattedAmount  = Double.valueOf(0);
    public Double mAmountUsd        = Double.valueOf(0);
    public String mTimestamp        = "";
    public String mDetailslUrl      = "";

    SecuXTransferHistory(JSONObject historyJson) throws Exception{
        try{
            mTxHash = historyJson.getString("txHash");
            mAddress = historyJson.getString("address");
            mTxType = historyJson.getString("tx_type");
            mAmount = historyJson.getDouble("amount");
            mAmountSymbol = historyJson.getString("amount_symbol");
            mFeeSymbol = historyJson.getString("fee_symbol");
            mFormattedAmount = historyJson.getDouble("formatted_amount");
            mAmountUsd = historyJson.getDouble("amount_usd");
            mTimestamp = historyJson.getString("timestamp");
            mDetailslUrl = historyJson.getString("detailsUrl");
        }catch (Exception e){
            Log.i(TAG, historyJson.toString());
            Log.e(TAG, "Load account history data error " + e.getMessage());
            throw e;
        }
    }
}
