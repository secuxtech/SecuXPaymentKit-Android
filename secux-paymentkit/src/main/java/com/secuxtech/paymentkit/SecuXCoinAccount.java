package com.secuxtech.paymentkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SecuXCoinAccount {
    public String mCoinType = "";
    public String mAccountName = "";

    public Map<String, SecuXSymbolAccountBalance> mSymbolBalanceMap = new HashMap<>();

    SecuXCoinAccount(String coinType, Map<String, SecuXSymbolAccountBalance> symbolBalance){
        mCoinType = coinType;
        mSymbolBalanceMap.putAll(symbolBalance);
    }

    public boolean updateSymbolBalance(String symbolType, Double balance, Double formattedBalance, Double usdBalance){
        SecuXSymbolAccountBalance accBalance = mSymbolBalanceMap.get(symbolType);
        if (accBalance != null) {
            accBalance.mBalance = balance;
            accBalance.mFormattedBalance = formattedBalance;
            accBalance.mUSDBalance = usdBalance;

            return true;
        }
        return false;
    }
}
