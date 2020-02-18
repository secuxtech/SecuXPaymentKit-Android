package com.secuxtech.paymentkit;

public class SecuXSymbolAccountBalance {

    public Double mBalance = Double.valueOf(0);
    public Double mFormattedBalance = Double.valueOf(0);
    public Double mUSDBalance = Double.valueOf(0);

    SecuXSymbolAccountBalance(Double balance, Double formattedBalance, Double usdBalance){
        mBalance = balance;
        mFormattedBalance = formattedBalance;
        mUSDBalance = usdBalance;
    }
}
