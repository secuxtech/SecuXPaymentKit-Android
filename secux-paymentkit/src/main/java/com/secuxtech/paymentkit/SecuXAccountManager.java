package com.secuxtech.paymentkit;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SecuXAccountManager {

    private SecuXServerRequestHandler mSecuXSvrReqHandler = new SecuXServerRequestHandler();

    public boolean getAccountBalance(SecuXAccount account, SecuXAccountBalance balance){
        switch (account.mCoinType){
            case SecuXCoinType.DCT:
            case SecuXCoinType.IFC:
                return getDCTAccountBalance(account, balance);

            default:
                break;

        }

        return false;
    }

    public boolean getAccountHistory(SecuXAccount account, ArrayList<SecuXAccountHisotry> historyList){
        switch (account.mCoinType){
            case SecuXCoinType.DCT:
            case SecuXCoinType.IFC:
                return getDCTAccountHistory(account, historyList);

            default:
                break;

        }

        return false;
    }

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
}
