package com.secuxtech.paymentkit;

import android.content.Context;


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

    public void doPayment(Context context, final SecuXAccount account, final String storeName, final String paymentInfo){
        this.mContext = context;
        doPayment(account, storeName, paymentInfo);
    }
}
