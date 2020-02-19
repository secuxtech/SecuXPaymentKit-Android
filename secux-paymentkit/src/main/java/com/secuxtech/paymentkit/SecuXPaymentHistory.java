package com.secuxtech.paymentkit;

/**
 * Created by maochuns.sun@gmail.com on 2020-02-19
 */

import org.json.JSONObject;

public class SecuXPaymentHistory {
    public Integer  mID                 = -1;
    public Integer  mParentID           = -1;
    public Integer  mStoreID            = -1;
    public Integer  mDevID              = -1;
    public String   mUserAccountName    = "";
    public String   mTransactionID      = "";
    public String   mTransactionCode    = "";
    public String   mTransactionType    = "";
    public String   mExchangeStoreID    = "";
    public String   mPayPlatform        = "";
    public String   mPayChannel         = "";
    public String   mCurrency           = "";
    public Double   mAmount             = 0.0;
    public String   mTransactionStatus  = "";
    public String   mTransactionTime    = "";
    public String   mRemark             = "";
    public String   mSysRemark          = "";
    //public String   mOperator           = "";

    SecuXPaymentHistory(JSONObject hisJson) throws Exception{
        try{
            mID = hisJson.getInt("id");

            if (hisJson.getString("parentID").compareTo("null")!=0) {
                mParentID = hisJson.getInt("parentID");
            }
            mStoreID = hisJson.getInt("storeID");
            mDevID = hisJson.getInt("deviceID");
            mUserAccountName = hisJson.getString("account");

            if (hisJson.getString("transactionId").compareTo("null")!=0) {
                mTransactionID = hisJson.getString("transactionId");
            }

            mTransactionCode = hisJson.getString("transactionCode");
            mTransactionType = hisJson.getString("transactionType");

            if (hisJson.getString("exchangeStoreID").compareTo("null")!=0) {
                mExchangeStoreID = hisJson.getString("exchangeStoreID");
            }

            mPayPlatform = hisJson.getString("payPlatform");
            mPayChannel = hisJson.getString("payChannel");
            mCurrency = hisJson.getString("currency");
            mAmount = hisJson.getDouble("amount");
            mTransactionStatus = hisJson.getString("transactionStatus");
            mTransactionTime = hisJson.getString("transactionTime");
            mRemark = hisJson.getString("remark");

            if (hisJson.getString("sysRemark").compareTo("null")!=0) {
                mSysRemark = hisJson.getString("sysRemark");
            }

        }catch (Exception e){
            throw e;
        }
    }
}
