package com.secuxtech.paymentkit;

import android.util.Log;
import androidx.core.util.Pair;

import org.json.JSONObject;

public class SecuXServerRequestHandler extends RestRequestHandler {

    static final String baseURL = "https://pmsweb-test.secux.io";
    static final String adminLoginUrl = baseURL + "/api/Admin/Login";
    static final String registerUrl = baseURL + "/api/Consumer/Register";
    static final String userLoginUrl = baseURL + "/api/Consumer/Login";
    static final String transferUrl = baseURL + "/api/Consumer/Transfer";
    static final String balanceUrl = baseURL + "/api/Consumer/GetAccountBalance";
    static final String balanceListUrl = baseURL + "/api/Consumer/GetAccountBalanceList";
    static final String paymentUrl = baseURL + "/api/Consumer/Payment";
    static final String paymentHistoryUrl = baseURL + "/api/Consumer/GetPaymentHistory";
    static final String getStoreUrl = baseURL + "/api/Terminal/GetStore";
    static final String transferHistoryUrl = baseURL + "/api/Consumer/GetTxHistory";

    private String mToken = "";

    public String getAdminToken(){
        Log.i(TAG, "getAdminToken");
        try {
            JSONObject param = new JSONObject();
            param.put("account", "secux_register");
            param.put("password", "!secux_register@123");
            Pair<Boolean, String> response = this.processPostRequest(adminLoginUrl, param);

            if (response.first) {
                JSONObject responseJson = new JSONObject(response.second);
                String token = responseJson.getString("token");
                return token;
            }

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }

        return "";
    }

    public Pair<Boolean, String> userRegister(String account, String password, String email, String alias, String phoneNum){
        Log.i(TAG, "userRegister");
        String token = getAdminToken();

        String response = "";
        if (token.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(false, "No token");
        }

        try{
            JSONObject param = new JSONObject();
            param.put("account", account);
            param.put("password", password);
            param.put("email", email);
            param.put("alias", alias);
            param.put("tel", phoneNum);
            param.put("optional", "{}");

            Pair<Boolean, String> result = this.processPostRequest(registerUrl, param, token);

            Log.i(TAG, result.second);
            return result;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(false, e.getLocalizedMessage());
        }


    }

    public Pair<Boolean, String> userLogin(String account, String pwd){
        Log.i(TAG, "userLogin");

        try{
            JSONObject param = new JSONObject();
            param.put("account", account);
            param.put("password", pwd);
            Pair<Boolean, String> response = this.processPostRequest(userLoginUrl, param);
            if (response.first){
                JSONObject responseJson = new JSONObject(response.second);
                String token = responseJson.getString("token");
                mToken = token;
            }

            Log.i(TAG, response.second);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());

            return new Pair<>(false, e.getLocalizedMessage());
        }

    }

    public Pair<Boolean, String> getAccountBalance(String account, String cointype, String coinsymbol){
        Log.i(TAG, "getAccountBalance " + account + " " + cointype + " " + coinsymbol);

        if (mToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(false, "No token");
        }

        try{
            JSONObject param = new JSONObject();
            param.put("account", account);
            param.put("coinType", cointype);
            param.put("symbol", coinsymbol);
            Pair<Boolean, String> response = this.processPostRequest(balanceUrl, param, mToken);

            Log.i(TAG, response.second);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(false, e.getLocalizedMessage());
        }

    }

    public Pair<Boolean, String> getAccountBalance(String account){
        Log.i(TAG, "getAccountBalance " + account);

        if (mToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(false, "No token");
        }

        try{
            JSONObject param = new JSONObject();
            param.put("account", account);
            Pair<Boolean, String> response = this.processPostRequest(balanceListUrl, param, mToken);

            Log.i(TAG, response.second);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(false, e.getLocalizedMessage());
        }
    }

    public Pair<Boolean, String> getStoreInfo(String devID){
        Log.i(TAG, "getStoreInfo");

        if (mToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(false, "No token");
        }

        try{
            JSONObject param = new JSONObject();
            param.put("deviceId", devID);

            Pair<Boolean, String> response = this.processPostRequest(userLoginUrl, param, mToken);

            Log.i(TAG, response.second);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(false, e.getLocalizedMessage());
        }

    }

    public Pair<Boolean, String> doPayment(String ivKey, String momo, String symbol, String amount, String cointype, String account, String receiver){

        if (mToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(false, "No token");
        }

        try{
            JSONObject param = new JSONObject();
            param.put("ivKey", ivKey);
            param.put("memo", momo);
            param.put("symbol", symbol);
            param.put("amount", amount);
            param.put("coinType", cointype);
            param.put("receiver", receiver);

            Pair<Boolean, String> response = this.processPostRequest(transferUrl, param, mToken);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(false, e.getLocalizedMessage());
        }


    }

    public Pair<Boolean, String> doTransfer(String cointype, String symbol, String feesymbol, String account, String receiver, String amount){

        if (mToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(false, "No token");
        }

        try{
            JSONObject param = new JSONObject();
            param.put("coinType", cointype);
            param.put("symbol", symbol);
            param.put("feeSymbol", feesymbol);
            param.put("account", account);
            param.put("receiver", receiver);
            param.put("amount", amount);

            Pair<Boolean, String> response = this.processPostRequest(transferUrl, param, mToken);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(false, e.getLocalizedMessage());
        }

    }

    public Pair<Boolean, String> getPaymentHistory(){
        return new Pair<>(false, "");
    }

    public Pair<Boolean, String> getTransferHistory(SecuXUserAccount account, String cointype, String symboltype){

        if (mToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(false, "No token");
        }

        try{
            JSONObject param = new JSONObject();
            param.put("account", account.mAccountName);
            param.put("coinType", cointype);
            param.put("symbol", symboltype);

            Pair<Boolean, String> response = this.processPostRequest(transferHistoryUrl, param, mToken);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(false, e.getLocalizedMessage());
        }

    }
}
