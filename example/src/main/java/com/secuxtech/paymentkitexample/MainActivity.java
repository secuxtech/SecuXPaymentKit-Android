package com.secuxtech.paymentkitexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import android.view.Gravity;
import android.widget.Toast;

import com.secuxtech.paymentkit.*;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private SecuXPaymentManager mPaymentManager = new SecuXPaymentManager();
    private SecuXAccountManager mAccountManager = new SecuXAccountManager();
    private SecuXUserAccount    mAccount;

    private String mPaymentInfo = "{\"amount\":\"16.5\", \"coinType\":\"DCT\", \"deviceID\":\"41193D32D520E114A3730D458F4389B5B9A7114D\",\"token\":\"SPC\"}";
    private final Context mContext = this;

    private final static String TAG = "secux_paymentkit_exp";

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
                //User account operations
                mAccount = new SecuXUserAccount("maochuntest9@secuxtech.com", "0975123456", "12345678");

                //Account registration
                SecuXUserAccount newAccount = new SecuXUserAccount("maochuntest6@secuxtech.com", "0975123456", "12345678");
                Pair<Integer, String> ret = mAccountManager.registerUserAccount(newAccount);
                if (ret.first==SecuXServerRequestHandler.SecuXRequestOK) {
                    showMessageInMain("Account registration successful!");
                }else {
                    showMessageInMain("registration failed! Error: " + ret.second);
                }

                //Account login

                ret = mAccountManager.loginUserAccount(mAccount);

                if (ret.first==SecuXServerRequestHandler.SecuXRequestOK){

                    //ret = mAccountManager.changePassword("12345678", "12345678");

                    //Get account all balance
                    ret = mAccountManager.getAccountBalance(mAccount);
                    if (ret.first==SecuXServerRequestHandler.SecuXRequestOK){
                        for(int i=0; i<mAccount.mCoinAccountArr.size(); i++){
                            SecuXCoinAccount coinAcc = mAccount.mCoinAccountArr.get(i);

                            Set<Map.Entry<String, SecuXCoinTokenBalance>> entrySet = coinAcc.mTokenBalanceMap.entrySet();
                            for (Map.Entry<String, SecuXCoinTokenBalance> entry: entrySet){
                                String symbol = entry.getKey();
                                SecuXCoinTokenBalance balance = entry.getValue();

                                Log.i(TAG, "Symbol=" + symbol + " balance=" + balance.mFormattedBalance.toString() + " usdBalance=" + balance.mUSDBalance.toString());
                            }
                        }
                    }else{
                        showMessageInMain("Get account balance failed! Error: " + ret.second);
                    }

                    //Get account balance for a specified coin and token
                    ret = mAccountManager.getAccountBalance(mAccount, "DCT", "SPC");
                    if (ret.first==SecuXServerRequestHandler.SecuXRequestOK){
                        SecuXCoinAccount coinAcc = mAccount.getCoinAccount("DCT");
                        SecuXCoinTokenBalance balance = coinAcc.getBalance("SPC");
                        Log.i(TAG, "balance=" +  balance.mFormattedBalance.toString() + " usdBalance=" + balance.mUSDBalance.toString());

                    }else{
                        showMessageInMain("Get account balance failed! Error: " + ret.second);
                    }

                    //Account transfer
                    SecuXTransferResult transRet = new SecuXTransferResult();
                    ret = mAccountManager.doTransfer(mAccount, "DCT", "SPC", "SFC", "10.5", "maochuntest2", transRet);
                    if (ret.first==SecuXServerRequestHandler.SecuXRequestOK){
                        Log.i(TAG, "Transfer done. TxID=" + transRet.mTxID + " url="+transRet.mDetailsUrl);
                    }else{
                       showMessageInMain("Transfer failed! Error: " + ret.second);
                    }

                    //Account history
                    ArrayList<SecuXTransferHistory> historyArr = new ArrayList<>();
                    int idx = 1;
                    int historyCount = 10;

                    while (true){
                        int preHisItemCount = historyArr.size();
                        ret = mAccountManager.getTransferHistory(mAccount, "DCT", "SPC", idx, historyCount, historyArr);

                        if (ret.first!=SecuXServerRequestHandler.SecuXRequestOK){
                            showMessageInMain("Get account transfer history failed! Error: " + ret.second);
                            break;
                        }else if (historyArr.size() - preHisItemCount < historyCount){
                            Log.i(TAG, "Get all transfer history items");
                            break;
                        }

                        idx += 1;
                    }

                    for(int i=0; i<historyArr.size(); i++){
                        SecuXTransferHistory hisItem = historyArr.get(i);
                        Log.i(TAG, "Address="+hisItem.mAddress + " type="+hisItem.mTxType +
                                " amount="+hisItem.mFormattedAmount.toString() + " usd="+hisItem.mAmountUsd.toString() +
                                " timestamp="+hisItem.mTimestamp.toString());
                    }
                }else{
                    showMessageInMain("Login failed! Error: " + ret.second);
                }

                //Payment operations
                ret = mAccountManager.loginUserAccount(mAccount);
                if (ret.first==SecuXServerRequestHandler.SecuXRequestOK) {
                    //Get payment history
                    ArrayList<SecuXPaymentHistory> payHisArr = new ArrayList<>();
                    int idx = 1;
                    int hisItemCount = 10;
                    while (true){
                        int preHisItemCount = payHisArr.size();
                        ret = mPaymentManager.getPaymentHistory("SPC", idx, hisItemCount, payHisArr);
                        if (ret.first!=SecuXServerRequestHandler.SecuXRequestOK){
                            showMessageInMain("Get payment history failed!");
                            break;
                        }else if (payHisArr.size() - preHisItemCount < hisItemCount){
                            Log.i(TAG, "Get all history items");
                            break;
                        }
                        idx += 1;
                    }

                    for(int i=0; i<payHisArr.size(); i++){
                        SecuXPaymentHistory history = payHisArr.get(i);
                        Log.i(TAG, "Store = " + history.mStoreName + " CoinType =" + history.mCoinType +
                                " amount=" + history.mAmount.toString() + history.mToken + " timestamp=" + history.mTransactionTime);
                    }

                    //Must set the callback for the SecuXPaymentManager
                    mPaymentManager.setSecuXPaymentManagerCallback(mPaymentMgrCallback);

                    //User SecuXPaymentManager to get valid payment info. from the QRCode string;
                    ret = mPaymentManager.getDeviceInfo("{\"amount\":\"12\", \"coinType\":\"DCT\", \"token\":\"SPC\",\"deviceIDhash\":\"04793D374185C2167A420D250FFF93F05156350C\"}");
                    if (ret.first==SecuXServerRequestHandler.SecuXRequestOK) {

                        try{
                            JSONObject replyJson = new JSONObject(ret.second);
                            String devID = replyJson.getString("deviceIDhash");
                            mPaymentInfo = ret.second;

                            //Use SecuXPaymentManager to get store info.
                            mPaymentManager.getStoreInfo(devID);
                        }catch (Exception e){
                            Log.i(TAG, "Invalid store info "+e.getLocalizedMessage());
                        }

                    }
                }


            }
        }).start();
    }

    private void showMessageInMain(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        });
    }

    //Callback for SecuXPaymentManager
    private SecuXPaymentManagerCallback mPaymentMgrCallback = new SecuXPaymentManagerCallback() {

        //Called when payment is completed. Returns payment result and error message.
        @Override
        public void paymentDone(final boolean ret, final String transactionCode, final String errorMsg) {
            if (ret){
                SecuXPaymentHistory payhistory = new SecuXPaymentHistory();
                Pair<Integer, String> hisret = mPaymentManager.getPaymentHistory("SPC", transactionCode, payhistory);
                if (hisret.first == SecuXServerRequestHandler.SecuXRequestOK) {

                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ret){
                        Toast toast = Toast.makeText(mContext, "Payment successful! " + transactionCode, Toast.LENGTH_LONG);
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
        public void getStoreInfoDone(final boolean ret, final String storeInfo, final Bitmap storeLogo){
            Log.i("secux-paymentkit-exp", "Get store info. done ret=" + String.valueOf(ret) + ",info=" + storeInfo);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (ret){
                        //final String name = storeName;

                        //Use SecuXManager to do payment, must call in main thread
                        mPaymentManager.doPayment(mContext, mAccount, storeInfo, mPaymentInfo);

                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(mContext, "Get store info. failed!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER,0,0);
                                toast.show();
                            }
                        });
                    }
                }
            }).start();

        }

        //Called when the user login account token is timeout. Just login in the account again.
        @Override
        public void userAccountUnauthorized(){
            Log.i(TAG, "account unauthorized login again");

            mAccount = new SecuXUserAccount("maochuntest6@secuxtech.com", "0975123456", "12345678");
            Pair<Integer, String> ret = mAccountManager.loginUserAccount(mAccount);

            if (ret.first==SecuXServerRequestHandler.SecuXRequestOK){
                Log.i(TAG, "Login successfully");
            }else{
                Log.i(TAG, "Login failed!");
            }
        }

    };
}
