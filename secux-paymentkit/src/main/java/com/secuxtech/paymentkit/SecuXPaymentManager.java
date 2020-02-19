package com.secuxtech.paymentkit;

/**
 * Created by maochuns.sun@gmail.com on 2020-02-05
 */

import android.content.Context;

import androidx.core.util.Pair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SecuXPaymentManager extends SecuXPaymentManagerBase{

    public void setSecuXPaymentManagerCallback(SecuXPaymentManagerCallback callback){
        this.mCallback = callback;
    }

    public void getStoreInfo(Context context, final String paymentInfo){
        this.mContext = context;
        new Thread(new Runnable() {
            @Override
            public void run() {
            if (getPaymentStoreInfo(paymentInfo)){
                handleGetStoreInfoDone(true);
            }else{
                handleGetStoreInfoDone(false);
            }
            }
        }).start();
    }

    public void doPayment(Context context, final SecuXUserAccount account, final String storeName, final String paymentInfo){
        this.mContext = context;
        doPayment(account, storeName, paymentInfo);
    }

    public Pair<Boolean, String> getPaymentHistory(SecuXUserAccount account, String symbol, int pageNum, int count, ArrayList<SecuXPaymentHistory> historyArr){
        Pair<Boolean, String> ret = this.mSecuXSvrReqHandler.getPaymentHistory(account, symbol, pageNum, count);
        if (ret.first){
            try{
                JSONArray hisJsonArr = new JSONArray(ret.second);
                for(int i=0; i<hisJsonArr.length(); i++){
                    JSONObject itemJson = hisJsonArr.getJSONObject(i);
                    SecuXPaymentHistory historyItem = new SecuXPaymentHistory(itemJson);
                    historyArr.add(historyItem);
                }
                return new Pair<>(true, "");
            }catch (Exception e){
                return new Pair<>(false, "Invalid return value");
            }
        }
        return ret;
    }
}
