package com.secuxtech.paymentkit;

/**
 * Created by maochuns.sun@gmail.com on 2020-02-19
 */
public class SecuXCoinTokenBalance {

    public Double mBalance = Double.valueOf(0);
    public Double mFormattedBalance = Double.valueOf(0);
    public Double mUSDBalance = Double.valueOf(0);

    SecuXCoinTokenBalance(Double balance, Double formattedBalance, Double usdBalance){
        mBalance = balance;
        mFormattedBalance = formattedBalance;
        mUSDBalance = usdBalance;
    }

}
