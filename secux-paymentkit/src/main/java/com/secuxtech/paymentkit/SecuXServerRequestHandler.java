package com.secuxtech.paymentkit;

import org.json.JSONObject;

public class SecuXServerRequestHandler extends RestRequestHandler {

    static final String baseURL = "https://pmsweb.secuxtech.com/";
    static final String balanceSvrUrl = baseURL + "Account/GetAccountBalance";
    static final String addrBalanceSvrUrl = baseURL + "Account/GetAccountBalanceByAddr";
    static final String historySvrUrl = baseURL + "Transaction/GetTxHistory";
    static final String addrHistorySvrUrl = baseURL + "Transaction/GetTxHistoryByAddr";
    static final String currencySvrUrl = baseURL + "Common/GetCryptocurrencySetting";
    static final String networkFeeSvrUrl = baseURL + "Common/GetNetworkFee";
    static final String paymentSvrUrl = baseURL + "Transaction/Payment";
    static final String swTransDataSvrUrl = baseURL + "Transaction/GetSWTransactionData";
    static final String hwTransDataSvrUrl = baseURL + "Transaction/GetHWTransactionData";
    static final String broadcastTransSvrUrl = baseURL + "Transaction/Transfer";
    static final String getAccountInfoSvrUrl = baseURL + "Account/GetAccountInfo";

    public String getCoinCurrency(){
        return this.processPostRequest(currencySvrUrl);
    }

    public String getAccountBalance(JSONObject param){
        return this.processPostRequest(balanceSvrUrl, param);
    }

    public String getAccountHistory(JSONObject param){
        return this.processPostRequest(historySvrUrl, param);
    }

    public String doPayment(JSONObject param){
        return this.processPostRequest(paymentSvrUrl, param);
    }

    public String getAccountInfo(JSONObject param){
        return this.processPostRequest(getAccountInfoSvrUrl, param);
    }
}
