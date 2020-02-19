package com.secuxtech.paymentkit;

/**
 * Created by maochuns.sun@gmail.com on 2020-02-12
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SecuXCoinAccount {
    public String mCoinType = "";
    public String mAccountName = "";

    public Map<String, SecuXCoinTokenBalance> mSymbolBalanceMap = new HashMap<>();

    SecuXCoinAccount(String coinType, Map<String, SecuXCoinTokenBalance> symbolBalance){
        mCoinType = coinType;
        mSymbolBalanceMap.putAll(symbolBalance);
    }

    public boolean updateSymbolBalance(String symbolType, Double balance, Double formattedBalance, Double usdBalance){
        SecuXCoinTokenBalance accBalance = mSymbolBalanceMap.get(symbolType);
        if (accBalance != null) {
            accBalance.mBalance = balance;
            accBalance.mFormattedBalance = formattedBalance;
            accBalance.mUSDBalance = usdBalance;

            return true;
        }
        return false;
    }

    public SecuXCoinTokenBalance getBalance(String symbolType){
        Set<Map.Entry<String, SecuXCoinTokenBalance>> entrySet = mSymbolBalanceMap.entrySet();
        for (Map.Entry<String, SecuXCoinTokenBalance> entry: entrySet){
            if (entry.getKey().compareTo(symbolType)==0){
                return entry.getValue();
            }
        }
        return null;
    }
}
