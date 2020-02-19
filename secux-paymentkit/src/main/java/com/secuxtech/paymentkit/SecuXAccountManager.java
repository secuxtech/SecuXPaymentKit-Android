package com.secuxtech.paymentkit;

/**
 * Created by maochuns.sun@gmail.com on 2020-02-03
 */

import androidx.core.util.Pair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SecuXAccountManager {

    private static String TAG = "secux-paymentkit";
    private SecuXServerRequestHandler mSecuXSvrReqHandler = new SecuXServerRequestHandler();

    public Pair<Boolean, String> registerUserAccount(SecuXUserAccount userAccount){
        Pair<Boolean, String> response = mSecuXSvrReqHandler.userRegister(userAccount.mAccountName, userAccount.mPassword, userAccount.mEmail, userAccount.mAlias, userAccount.mPhoneNum);

        if (response.first){
            try{
                JSONObject responseJson = new JSONObject(response.second);
                String coinType = responseJson.getString("coinType");
                String coinSymbol = responseJson.getString("symbol");
                Double balance = responseJson.getDouble("balance");
                Double formattedBalance = responseJson.getDouble("formattedBalance");
                Double usdBlance = responseJson.getDouble("balance_usd");

                return new Pair<>(true, "");

            }catch (Exception e){
                return new Pair<>(false, "Invalid return value");
            }
        }

        return response;
    }

    public Pair<Boolean, String> loginUserAccount(SecuXUserAccount userAccount){
        Pair<Boolean, String>  response = mSecuXSvrReqHandler.userLogin(userAccount.mAccountName, userAccount.mPassword);
        if (response.first) {
            try {
                JSONObject responseJson = new JSONObject(response.second);
                String coinType = responseJson.getString("coinType");
                String coinSymbol = responseJson.getString("symbol");
                Double balance = responseJson.getDouble("balance");
                Double formattedBalance = responseJson.getDouble("formattedBalance");
                Double usdBlance = responseJson.getDouble("balance_usd");

                SecuXCoinTokenBalance symbolBalance = new SecuXCoinTokenBalance(balance, formattedBalance, usdBlance);
                Map<String, SecuXCoinTokenBalance> symbolBalanceMap = new HashMap<>();
                symbolBalanceMap.put(coinSymbol, symbolBalance);

                SecuXCoinAccount coinAccount = new SecuXCoinAccount(coinType, symbolBalanceMap);

                userAccount.mCoinAccountArr.add(coinAccount);
                return new Pair<>(true, "");

            } catch (Exception e) {
                return new Pair<>(false, "Invalid return value");
            }
        }

        return response;
    }

    public Pair<Boolean, String> getAccountBalance(SecuXUserAccount userAccount, String coinType, String symbolType){
        Pair<Boolean, String>  response = this.mSecuXSvrReqHandler.getAccountBalance(userAccount.mAccountName, coinType, symbolType);
        if (response.first) {
            try {
                JSONObject responseJson = new JSONObject(response.second);
                Double balance = responseJson.getDouble("balance");
                Double formattedBalance = responseJson.getDouble("formattedBalance");
                Double usdBlance = responseJson.getDouble("balance_usd");

                SecuXCoinAccount coinAcc = userAccount.getCoinAccount(coinType);
                if (coinAcc != null) {
                    Boolean ret = coinAcc.updateSymbolBalance(symbolType, balance, formattedBalance, usdBlance);
                    if (ret){
                        return new Pair<>(true, "");
                    }else{
                        return new Pair<>(false, "Invalid symbol type");
                    }
                }else{
                    return new Pair<>(false, "Invalid coin type");
                }

            } catch (Exception e) {
                return new Pair<>(false, "Invalid return value");
            }
        }

        return response;
    }

    public Pair<Boolean, String>  getAccountBalance(SecuXUserAccount userAccount){
        Pair<Boolean, String>  response = this.mSecuXSvrReqHandler.getAccountBalance(userAccount.mAccountName);
        if (response.first) {
            try {
                JSONArray responseJsonArr = new JSONArray(response.second);
                for (int i = 0; i < responseJsonArr.length(); i++) {
                    JSONObject itemJson = responseJsonArr.getJSONObject(i);
                    String cointype = itemJson.getString("coinType");
                    String symboltype = itemJson.getString("symbol");
                    Double balance = itemJson.getDouble("balance");
                    Double formattedBalance = itemJson.getDouble("formattedBalance");
                    Double usdBlance = itemJson.getDouble("balance_usd");

                    SecuXCoinAccount coinAcc = userAccount.getCoinAccount(cointype);
                    if (coinAcc != null) {
                        if (!coinAcc.updateSymbolBalance(symboltype, balance, formattedBalance, usdBlance)){
                            SecuXCoinTokenBalance symbolBalance = new SecuXCoinTokenBalance(balance, formattedBalance, usdBlance);
                            coinAcc.mSymbolBalanceMap.put(symboltype, symbolBalance);
                        }
                    }else{
                        SecuXCoinTokenBalance symbolBalance = new SecuXCoinTokenBalance(balance, formattedBalance, usdBlance);
                        Map<String, SecuXCoinTokenBalance> symbolBalanceMap = new HashMap<>();
                        symbolBalanceMap.put(cointype, symbolBalance);

                        SecuXCoinAccount coinAccount = new SecuXCoinAccount(cointype, symbolBalanceMap);
                        userAccount.mCoinAccountArr.add(coinAccount);
                    }
                }

                return new Pair<>(true, "");

            } catch (Exception e) {
                return new Pair<>(false, "Invalid return value");
            }
        }
        return response;
    }

    public Pair<Boolean, String> doTransfer(SecuXUserAccount account, String cointype, String symboltype, String feeSymbol,
                                            String amount, String receiver, SecuXTransferResult transRet){

        Pair<Boolean, String> response = mSecuXSvrReqHandler.doTransfer(cointype, symboltype, feeSymbol, account.mAccountName, receiver, amount);
        if (response.first){
            try{
                JSONObject transRetJson = new JSONObject(response.second);
                int statusCode = transRetJson.getInt("statusCode");
                String statusDesc = transRetJson.getString("statusDesc");

                if (statusCode != 200){
                    return new Pair<>(false, statusDesc);
                }
                transRet.mTxID = transRetJson.getString("txId");
                transRet.mDetailsUrl = transRetJson.getString("detailsUrl");
                return new Pair<>(true, "");
            }catch (Exception e){
                return new Pair<>(false, "Invalid return value");
            }
        }
        return response;
    }

    public Pair<Boolean, String> getTransferHistory(SecuXUserAccount account, String cointype, String symboltype,
                                                    int page, int count, ArrayList<SecuXTransferHistory> historyArr){

        Pair<Boolean, String> response = mSecuXSvrReqHandler.getTransferHistory(account, cointype, symboltype, page, count);
        if (response.first){
            try {
                JSONArray responseJsonArr = new JSONArray(response.second);
                for (int i = 0; i < responseJsonArr.length(); i++) {
                    JSONObject itemJson = responseJsonArr.getJSONObject(i);
                    SecuXTransferHistory history = new SecuXTransferHistory(itemJson);
                    historyArr.add(history);
                }
                return new Pair<>(true, "");
            }catch (Exception e){
                return new Pair<>(true, "Invalid return value");
            }
        }

        return response;
    }



    /*
    public Map<String, Double> getCoinUSDRate(){
        Map<String, Double> rateMap = new HashMap<String, Double>();
        String ret = mSecuXSvrReqHandler.getCoinCurrency();
        try{
            JSONArray rateJsonArr = new JSONArray(ret);
            for(int i=0; i<rateJsonArr.length(); i++){
                JSONObject rateJsonObj = rateJsonArr.getJSONObject(i);

                @SecuXCoinType.CoinType String type = rateJsonObj.getString("coinType");
                Double rate = Double.valueOf(rateJsonObj.getString("usdPrice"));

                rateMap.put(type, rate);
            }
            return rateMap;
        }catch (Exception e){
            Log.e("secux-paymentkit", e.getLocalizedMessage());
        }

        return rateMap;
    }

    private boolean getDCTAccountBalance(SecuXAccount account, SecuXAccountBalance balance){
        if (account.mName.length()==0)
            return false;

        try{
            JSONObject param = new JSONObject();
            param.put("coinType", account.mCoinType.toString());
            param.put("pubKey", account.mName);

            String strRet = mSecuXSvrReqHandler.getAccountBalance(param);
            return handleAccountBalanceData(strRet, balance);

        }catch(Exception e){
            Log.e("secux-paymentkit", e.getLocalizedMessage());
        }

        return false;
    }

    private boolean getLBRAccountBalance(SecuXAccount account, SecuXAccountBalance balance){
        if (account.mName.length()==0)
            return false;

        try{
            JSONObject param = new JSONObject();
            param.put("coinType", account.mCoinType.toString());
            param.put("pubKey", account.mAddress);

            String strRet = mSecuXSvrReqHandler.getAccountBalance(param);
            return handleAccountBalanceData(strRet, balance);

        }catch(Exception e){
            Log.e("secux-paymentkit", e.getLocalizedMessage());
        }

        return false;
    }

    private boolean getDCTAccountHistory(SecuXAccount account, ArrayList<SecuXAccountHisotry> historyList){
        if (account.mName.length()==0)
            return false;

        try{
            JSONObject param = new JSONObject();
            param.put("coinType", account.mCoinType.toString());
            param.put("pubKey", account.mName);

            String strRet = mSecuXSvrReqHandler.getAccountHistory(param);
            return handleAccountHistoryData(strRet, historyList);
        }catch(Exception e){
            Log.e("secux-paymentkit", e.getLocalizedMessage());
        }

        return false;
    }

    private boolean getLBRAccountHistory(SecuXAccount account, ArrayList<SecuXAccountHisotry> historyList){
        if (account.mName.length()==0)
            return false;

        try{
            JSONObject param = new JSONObject();
            param.put("coinType", account.mCoinType.toString());
            param.put("pubKey", account.mAddress);

            String strRet = mSecuXSvrReqHandler.getAccountHistory(param);
            return handleAccountHistoryData(strRet, historyList);
        }catch(Exception e){
            Log.e("secux-paymentkit", e.getLocalizedMessage());
        }

        return false;
    }

    private boolean handleAccountBalanceData(String accBalanceStr, SecuXAccountBalance balance){
        try{
            JSONObject accBalanceJson = new JSONObject(accBalanceStr);
            balance.mBalance = accBalanceJson.getDouble("balance");
            balance.mFormatedBalance = accBalanceJson.getDouble("formattedBalance");
            balance.mUSDBalance = accBalanceJson.getDouble("balance_usd");
            return true;
        }catch (Exception e){
            Log.e("secux-paymentkit", e.getLocalizedMessage());
        }
        return false;
    }

    private boolean handleAccountHistoryData(String accHistoryStr, ArrayList<SecuXAccountHisotry> histryList){
        try{
            JSONArray accHisJsonArr = new JSONArray(accHistoryStr);
            for (int i=0; i<accHisJsonArr.length(); i++){
                JSONObject historyItem = accHisJsonArr.getJSONObject(i);

                SecuXAccountHisotry history = new SecuXAccountHisotry();
                history.address = historyItem.getString("address");
                history.tx_type = historyItem.getString("tx_type");
                history.amount = historyItem.getDouble("amount");
                history.amount_symbol = historyItem.getString("amount_symbol");
                history.formatted_amount = historyItem.getDouble("formatted_amount");
                history.amount_usd = historyItem.getDouble("amount_usd");
                history.timestamp = historyItem.getString("timestamp");
                history.detailslUrl = historyItem.getString("detailsUrl");

                histryList.add(history);

            }
            return true;
        }catch (Exception e){
            Log.e("secux-paymentkit", e.getLocalizedMessage());
        }

        return false;
    }
    */
}
