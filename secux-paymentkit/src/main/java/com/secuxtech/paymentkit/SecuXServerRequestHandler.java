package com.secuxtech.paymentkit;

/**
 * Created by maochuns.sun@gmail.com on 2020-02-10
 */

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

    private static String mToken = "";

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

            Pair<Boolean, String> result = this.processPostRequest(registerUrl, param, token, 30000);

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

    public Pair<Boolean, String> getAccountBalance(String account, String cointype, String token){
        Log.i(TAG, "getAccountBalance " + account + " " + cointype + " " + token);

        if (mToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(false, "No token");
        }

        try{
            JSONObject param = new JSONObject();
            //param.put("account", account);
            param.put("coinType", cointype);
            param.put("symbol", token);
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
            //param.put("account", account);
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

            Pair<Boolean, String> response = this.processPostRequest(getStoreUrl, param, mToken);

            Log.i(TAG, response.second);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(false, e.getLocalizedMessage());
        }

    }

    public Pair<Boolean, String> doPayment(String sender, String storeName, PaymentInfo payInfo){
        Log.i(TAG, "doPayment");
        if (mToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(false, "No token");
        }

        try{
            JSONObject param = new JSONObject();
            param.put("ivKey", payInfo.mIVKey);
            param.put("memo", "");
            param.put("symbol", payInfo.mToken);
            param.put("amount", payInfo.mAmount);
            param.put("coinType", payInfo.mCoinType);
            //param.put("account", sender);
            param.put("receiver", payInfo.mDevID);

            Pair<Boolean, String> response = this.processPostRequest(paymentUrl, param, mToken, 12000);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(false, e.getLocalizedMessage());
        }
    }

    public Pair<Boolean, String> doTransfer(String cointype, String token, String feesymbol, String account, String receiver, String amount){
        Log.i(TAG, "doTransfer");
        if (mToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(false, "No token");
        }

        try{
            JSONObject param = new JSONObject();
            param.put("coinType", cointype);
            param.put("symbol", token);
            param.put("feeSymbol", feesymbol);
            //param.put("account", account);
            param.put("receiver", receiver);
            param.put("amount", amount);

            Pair<Boolean, String> response = this.processPostRequest(transferUrl, param, mToken, 10000);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(false, e.getLocalizedMessage());
        }
    }

    public Pair<Boolean, String> getPaymentHistory(SecuXUserAccount account, String token, int pageIdx, int pageItemCount){
        Log.i(TAG, "getPaymentHistory");
        if (mToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(false, "No token");
        }

        try{
            JSONObject param = new JSONObject();
            //param.put("account", account.mAccountName);
            param.put("symbol", token);
            param.put("page", pageIdx);
            param.put("count", pageItemCount);
            param.put("columnName", "");
            param.put("sorting", "");

            Pair<Boolean, String> response = this.processPostRequest(paymentHistoryUrl, param, mToken);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(false, e.getLocalizedMessage());
        }
    }

    public Pair<Boolean, String> getTransferHistory(SecuXUserAccount account, String cointype, String symboltype, int page, int count){
        Log.i(TAG, "getTransferHistory");
        if (mToken.length()==0){
            Log.e(TAG, "No token");
            return new Pair<>(false, "No token");
        }

        try{
            JSONObject param = new JSONObject();
            //param.put("account", account.mAccountName);
            param.put("coinType", cointype);
            param.put("symbol", symboltype);
            param.put("page", page);
            param.put("count", count);

            Pair<Boolean, String> response = this.processPostRequest(transferHistoryUrl, param, mToken);
            return response;

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return new Pair<>(false, e.getLocalizedMessage());
        }
    }
}
