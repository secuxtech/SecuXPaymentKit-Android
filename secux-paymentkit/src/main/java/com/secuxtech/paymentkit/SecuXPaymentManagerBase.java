package com.secuxtech.paymentkit;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import com.secux.payment.sdk.BoxError;
import com.secux.payment.sdk.DiscoveredDevice;
import com.secux.payment.sdk.MachineIoControlParam;
import com.secux.payment.sdk.PaymentPeripheralManager;
import com.secux.payment.sdk.listener.OnConnectCompleteListener;
import com.secux.payment.sdk.listener.OnErrorListener;
import com.secux.payment.sdk.listener.OnGetDataMapCompleteListener;
import com.secux.payment.sdk.listener.OnSendStringCompleteListener;

import java.util.Map;

class PaymentInfo{
    String mCoinType;
    String mAmount;
    String mDevID;
    String mIVKey;
}

public class SecuXPaymentManagerBase {

    protected SecuXPaymentManagerCallback mCallback = null;
    protected Context mContext = null;

    private SecuXServerRequestHandler mSecuXSvrReqHandler = new SecuXServerRequestHandler();
    private PaymentPeripheralManager mPaymentPeripheralManager = new PaymentPeripheralManager();

    private PaymentInfo mPaymentInfo = new PaymentInfo();
    private SecuXAccount mAccount = null;
    private String mStoreName = "";
    private Image mStoreLogo = null;


    SecuXPaymentManagerBase(){
        mPaymentPeripheralManager.setOnErrorListener(new OnErrorListener() {
            @Override
            public void onError(BoxError error) {
                handlePaymentDone(false, error.getMessage());
            }
        });
        mPaymentPeripheralManager.setOnSendStringCompleteListener(new OnSendStringCompleteListener() {
            @Override
            public void onComplete(final String response) {
                mPaymentInfo.mIVKey = response;
                Log.i("secux-paymentkit", "getIVkey " + response);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendInfoToDevice();
                    }
                }).start();

            }
        });
        mPaymentPeripheralManager.setOnConnectCompleteListener(new OnConnectCompleteListener() {
            @Override
            public void onConnectComplete(DiscoveredDevice discoveredDevice) {

            }
        });
    }

    protected Boolean getPaymentStoreInfo(String paymentInfo){
        Log.i("secux-paymentkit", "getStoreInfo");

        mStoreName = "";
        mStoreLogo = null;
        try{
            if (getPaymentInfo(paymentInfo)) {
                String param = "{\"coinType\":\"" + mPaymentInfo.mCoinType + "\",\"id\":\"" + mPaymentInfo.mDevID + "\",\"type\":\"Device\"}";
                JSONObject jsonParam = new JSONObject(param);
                JSONObject storeInfoJson = new JSONObject(this.mSecuXSvrReqHandler.getAccountInfo(jsonParam));
                mStoreName = storeInfoJson.getString("name").toString();
                return true;
            }
        }catch (Exception e){
            Log.e("secux-paymentkit", e.getLocalizedMessage());
        }

        return false;
    }

    protected void doPayment(SecuXAccount account, String storeName, String paymentInfo){

        Log.e("secux-paymentkit", "doPayment");

        this.mAccount = account;
        this.mStoreName = storeName;


        int scanTimeout = 8000;
        int connectionTimeout = 8000;
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

        }catch (Exception e){
            return false;
        }
        return true;
    }


    protected void sendInfoToDevice(){

        Log.i("secux-paymentkit", "sendInfoToDevice");
        String fromAcc = mAccount.mName;
        if (mAccount.mCoinType.toString().compareTo(SecuXCoinType.LBR)==0){
            fromAcc = mAccount.mAddress;
        }

        handlePaymentStatus(mAccount.mCoinType.toString() + " transferring...");
        try {
            JSONObject param = new JSONObject();
            param.put("coinType", mAccount.mCoinType.toString());
            param.put("from", fromAcc);
            param.put("txId", "P123456789123456");
            param.put("to", mPaymentInfo.mDevID);
            param.put("amount", mPaymentInfo.mAmount);
            param.put("ivKey", mPaymentInfo.mIVKey);
            param.put("memo", mStoreName);
            param.put("currency", mPaymentInfo.mCoinType);

            Log.i("secux-paymentkit", param.toString());
            String payRet = mSecuXSvrReqHandler.doPayment(param);
            JSONObject payRetJson = new JSONObject(payRet);
            Log.i("secux-paymentkit", "Send server request done " + payRetJson.toString());

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
            Log.e("secux-paymentkit", e.getLocalizedMessage());
            handlePaymentDone(false, mAccount.mCoinType.toString() + " transfer failed!");
        }
    }

    protected void handleGetStoreInfoDone(final boolean ret){
        Log.i("secux-paymentkit", "Get store info. done " + String.valueOf(ret) + " " + mStoreName);
        if (mCallback!=null){
            mCallback.getStoreInfoDone(ret, mStoreName, mStoreLogo);
        }
    }

    protected void handlePaymentStatus(final String status){
        Log.i("secux-paymentkit", "Payment status " + status);
        if (mCallback!=null){
            mCallback.updatePaymentStatus(status);
        }
    }

    protected void handlePaymentDone(final boolean ret, final String errorMsg){
        Log.i("secux-paymentkit", "Payment done " + String.valueOf(ret));
        if (mCallback!=null){
            mCallback.paymentDone(ret, errorMsg);
        }
    }

}
