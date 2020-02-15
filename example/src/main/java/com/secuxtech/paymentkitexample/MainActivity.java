package com.secuxtech.paymentkitexample;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.secuxtech.paymentkit.*;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private SecuXPaymentManager mPaymentManager = new SecuXPaymentManager();
    private SecuXAccountManager mAccountManager = new SecuXAccountManager();
    private SecuXAccount mAccount;

    private String mPaymentInfo = "{\"amount\":\"11\", \"coinType\":\"LBR\", \"deviceID\":\"4ab10000726b\"}";
    private final Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check

            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                Map<String, Double> coinRate = mAccountManager.getCoinUSDRate();

                //Create SecuX account
                //mAccount = new SecuXAccount("ifun-886-936105934-6", SecuXCoinType.DCT, "", "", "");
                mAccount = new SecuXAccount("Alice-Libra", SecuXCoinType.LBR, "", "842d1a7e65d0e7788564dc03ea4bbe4e15d57719003bee6981f1a6d765443822", "");

                SecuXAccountBalance balance = new SecuXAccountBalance();
                if (mAccountManager.getAccountBalance(mAccount, balance)){
                    Double usdBalance = balance.mUSDBalance;
                    if (balance.mUSDBalance==0 && coinRate!=null && coinRate.containsKey(mAccount.mCoinType)){
                        usdBalance = balance.mFormatedBalance * coinRate.get(mAccount.mCoinType);
                    }

                    Log.i("secux-paymentkit-exp",
                            "getAccountBalance done. balance= " + String.valueOf(balance.mFormatedBalance) + ", usdBalance=" + String.valueOf(usdBalance));
                }else{
                    Log.i("secux-paymentkit-exp", "get account balance failed!");
                }

                ArrayList<SecuXAccountHisotry> historyList = new ArrayList<>();
                if (mAccountManager.getAccountHistory(mAccount, historyList)){
                    for(int i=0; i<historyList.size(); i++){
                        SecuXAccountHisotry item = historyList.get(i);

                        Log.i("secux-paymentkit-exp", item.timestamp + " " + item.tx_type + " "
                                + item.formatted_amount + " " + item.amount_symbol + " $ " + item.amount_usd + " " + item.detailslUrl);
                    }
                }else{
                    Log.i("secux-paymentkit-exp", "get account history failed!");
                }

                //Must set the callback for the SecuXPaymentManager
                mPaymentManager.setSecuXPaymentManagerCallback(mPaymentMgrCallback);

                //Use SecuXPaymentManager to get store info.
                mPaymentManager.getStoreInfo(getBaseContext(), mPaymentInfo);
            }
        }).start();
    }

    //Callback for SecuXPaymentManager
    private SecuXPaymentManagerCallback mPaymentMgrCallback = new SecuXPaymentManagerCallback() {

        //Called when payment is completed. Returns payment result and error message.
        @Override
        public void paymentDone(final boolean ret, final String errorMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ret){
                        Toast toast = Toast.makeText(mContext, "Payment successful!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }else{
                        Toast toast = Toast.makeText(mContext, "Payment failed! Error: " + errorMsg, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                }
            });

        }

        //Called when payment status is changed. Payment status are: "Device connecting...", "DCT transferring..." and "Device verifying..."
        @Override
        public void updatePaymentStatus(final String status){
            Log.i("secux-paymentkit-exp", "Update payment status: " + status);
        }

        //Called when get store information is completed. Returns store name and store logo.
        @Override
        public void getStoreInfoDone(final boolean ret, final String storeName, final Bitmap storeLogo){
            Log.i("secux-paymentkit-exp", "Get store info. done ret=" + String.valueOf(ret) + ",name=" + storeName);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ret){
                        final String name = storeName;


                        //Use SecuXManager to do payment, must call in main thread
                        mPaymentManager.doPayment(mContext, mAccount, name, mPaymentInfo);

                    }else{
                        Toast toast = Toast.makeText(mContext, "Get store info. failed!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                }
            });

        }

    };
}
