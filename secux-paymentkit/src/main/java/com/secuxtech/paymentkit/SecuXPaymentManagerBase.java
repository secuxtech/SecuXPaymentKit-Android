package com.secuxtech.paymentkit;

/**
 * Created by maochuns.sun@gmail.com on 2020-02-10
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;
import android.util.Log;

import androidx.core.util.Pair;

import org.json.JSONObject;

import com.secux.payment.sdk.BoxError;
import com.secux.payment.sdk.DiscoveredDevice;
import com.secux.payment.sdk.MachineIoControlParam;
import com.secux.payment.sdk.PaymentPeripheralManager;
import com.secux.payment.sdk.listener.OnConnectCompleteListener;
import com.secux.payment.sdk.listener.OnErrorListener;
import com.secux.payment.sdk.listener.OnGetDataMapCompleteListener;
import com.secux.payment.sdk.listener.OnScanCompleteListener;
import com.secux.payment.sdk.listener.OnSendStringCompleteListener;

import java.util.Map;
import java.util.Set;

import static com.secuxtech.paymentkit.RestRequestHandler.TAG;

class PaymentInfo{
    String mCoinType;
    String mToken;
    String mAmount;
    String mDevID;
    String mIVKey;
}

public class SecuXPaymentManagerBase {

    protected SecuXPaymentManagerCallback mCallback = null;
    protected Context mContext = null;

    protected SecuXServerRequestHandler mSecuXSvrReqHandler = new SecuXServerRequestHandler();
    private PaymentPeripheralManager mPaymentPeripheralManager = new PaymentPeripheralManager();

    private SecuXUserAccount mAccount = null;
    private PaymentInfo mPaymentInfo = new PaymentInfo();

    private String mStoreName = "";
    private Bitmap mStoreLogo = null;

    SecuXPaymentManagerBase(){
        Log.i(TAG, "SecuXPaymentManagerBase");

        mPaymentPeripheralManager.setOnErrorListener(new OnErrorListener() {
            @Override
            public void onError(BoxError boxError) {
                if (boxError != null){
                    mPaymentPeripheralManager.stopScan();
                    handlePaymentDone(false, boxError.getMessage());
                }

            }
        });
        mPaymentPeripheralManager.setOnSendStringCompleteListener(new OnSendStringCompleteListener() {
            @Override
            public void onComplete(final String response) {
                mPaymentInfo.mIVKey = response;
                Log.i(TAG, "getIVkey " + response);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        sendInfoToDevice();
                    }
                }).start();

            }
        });
        /*
        mPaymentPeripheralManager.setOnScanCompleteListener(new OnScanCompleteListener() {
            @Override
            public void onScanComplete(Set<DiscoveredDevice> scannedDevices) {

                int scanTimeout = 5;
                int connectionTimeout = 5;
                int rssi = -80;
                mPaymentPeripheralManager.doPeripheralAuthenticityVerification(mContext, scanTimeout, mPaymentInfo.mDevID, rssi, connectionTimeout);
            }
        });

         */
        mPaymentPeripheralManager.setOnConnectCompleteListener(new OnConnectCompleteListener() {
            @Override
            public void onConnectComplete(DiscoveredDevice discoveredDevice) {
                Log.i(TAG, "peripheral manager onConnectComplete");
            }
        });

    }

    protected Integer getPaymentStoreInfo(String deviceID){
        Log.i(TAG, "getStoreInfo");

        mStoreName = "";
        mStoreLogo = null;

        Pair<Integer, String> response = this.mSecuXSvrReqHandler.getStoreInfo(deviceID);
        if (response.first==SecuXServerRequestHandler.SecuXRequestOK) {
            try {
                JSONObject storeInfoJson = new JSONObject(response.second);
                mStoreName = storeInfoJson.getString("name");

                String base64String = storeInfoJson.getString("icon");
                String base64Image = base64String.split(",")[1];
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                mStoreLogo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                return SecuXServerRequestHandler.SecuXRequestOK;
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }else{
            return response.first;
        }

        return SecuXServerRequestHandler.SecuXRequestFailed;
    }


    protected void doPayment(SecuXUserAccount account, String storeName, String paymentInfo){

        Log.e(TAG, "doPayment");

        this.mAccount = account;
        this.mStoreName = storeName;

        int scanTimeout = 5;
        int connectionTimeout = 5;
        int rssi = -80;

        if (getPaymentInfo(paymentInfo)){
            handlePaymentStatus("Device connecting ...");

            mPaymentPeripheralManager.doPeripheralAuthenticityVerification(mContext, scanTimeout, mPaymentInfo.mDevID, rssi, connectionTimeout);

        }else {
            handlePaymentDone(false, "Wrong payment information");
        }

    }

    protected boolean getPaymentInfo(String paymentInfo){
        try {
            JSONObject jsonInfo = new JSONObject(paymentInfo);
            mPaymentInfo.mAmount = jsonInfo.getString("amount");
            mPaymentInfo.mDevID = jsonInfo.getString("deviceID");
            mPaymentInfo.mCoinType = jsonInfo.getString("coinType");
            mPaymentInfo.mToken = jsonInfo.getString("symbol");

        }catch (Exception e){
            return false;
        }
        return true;
    }

    protected void sendInfoToDevice(){

        Log.i(TAG, "sendInfoToDevice");

        Pair<Integer, String> payRet = mSecuXSvrReqHandler.doPayment(mAccount.mAccountName, mStoreName, mPaymentInfo);
        if (payRet.first == SecuXServerRequestHandler.SecuXRequestUnauthorized){
            handleAccountUnauthorized();
            return;
        }else if (payRet.first != SecuXServerRequestHandler.SecuXRequestOK){
            handlePaymentDone(false, payRet.second);
            return;
        }

        handlePaymentStatus(mPaymentInfo.mToken + " transferring...");
        try {

            JSONObject payRetJson = new JSONObject(payRet.second);
            Log.i(TAG, "Send server request done " + payRetJson.toString());

            int statusCode = payRetJson.getInt("statusCode");
            String statusDesc = payRetJson.getString("statusDesc");

            if (statusCode != 200){
                handlePaymentDone(false, statusDesc);
                return;
            }

            String ioControlParams = payRetJson.getString("machineControlParam");
            JSONObject ioCtrlParamJson = new JSONObject(ioControlParams);

            final MachineIoControlParam machineIoControlParam=new MachineIoControlParam();
            machineIoControlParam.setGpio1(ioCtrlParamJson.getString("gpio1"));
            machineIoControlParam.setGpio2(ioCtrlParamJson.getString("gpio2"));
            machineIoControlParam.setGpio31(ioCtrlParamJson.getString("gpio31"));
            machineIoControlParam.setGpio32(ioCtrlParamJson.getString("gpio32"));
            machineIoControlParam.setGpio4(ioCtrlParamJson.getString("gpio4"));
            machineIoControlParam.setGpio4c(ioCtrlParamJson.getString("gpio4c"));
            machineIoControlParam.setGpio4cCount(ioCtrlParamJson.getString("gpio4cCount"));
            machineIoControlParam.setGpio4cInterval(ioCtrlParamJson.getString("gpio4cInterval"));
            machineIoControlParam.setGpio4dOn(ioCtrlParamJson.getString("gpio4dOn"));
            machineIoControlParam.setGpio4dOff(ioCtrlParamJson.getString("gpio4dOff"));
            machineIoControlParam.setGpio4dInterval(ioCtrlParamJson.getString("gpio4dInterval"));
            machineIoControlParam.setUart(ioCtrlParamJson.getString("uart"));
            machineIoControlParam.setRunStatus(ioCtrlParamJson.getString("runStatus"));
            machineIoControlParam.setLockStatus(ioCtrlParamJson.getString("lockStatus"));

            String encryptedStr = payRetJson.getString("encryptedTransaction");
            final byte[] encryptedData = Base64.decode(encryptedStr, Base64.DEFAULT);

            handlePaymentStatus("Device verifying...");
            mPaymentPeripheralManager.setOnGetDataMapCompleteListener(new OnGetDataMapCompleteListener() {
                @Override
                public void onComplete(final Map<String, Object> dataMap) {
                    handlePaymentDone(true, "");
                }
            });


            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   mPaymentPeripheralManager.doPaymentVerification(encryptedData, machineIoControlParam);
                }
            });

        }catch (Exception e){
            Log.e(TAG, e.getLocalizedMessage());
            handlePaymentDone(false, mPaymentInfo.mCoinType.toString() + " transfer failed!");
        }
    }

    protected void handleGetStoreInfoDone(final boolean ret){
        Log.i(TAG, "Get store info. done " + String.valueOf(ret) + " " + mStoreName);
        if (mCallback!=null){
            mCallback.getStoreInfoDone(ret, mStoreName, mStoreLogo);
        }
    }

    protected void handlePaymentStatus(final String status){
        Log.i(TAG, "Payment status " + status);
        if (mCallback!=null){
            mCallback.updatePaymentStatus(status);
        }
    }

    protected void handlePaymentDone(final boolean ret, final String errorMsg){
        Log.i(TAG, "Payment done " + String.valueOf(ret));
        if (mCallback!=null){
            mCallback.paymentDone(ret, errorMsg);
        }
    }

    protected void handleAccountUnauthorized(){
        Log.i(TAG, "Account unauthorized!");
        if (mCallback!=null){
            mCallback.userAccountUnauthorized();
        }
    }

}
