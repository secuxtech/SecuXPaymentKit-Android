package com.secuxtech.paymentkit;


public class SecuXAccount {

    public String mName;
    public @SecuXCoinType.CoinType String mCoinType;

    public String mPath;
    public String mAddress;
    public String mKey;

    public SecuXAccount(String name, @SecuXCoinType.CoinType String type, String path, String address, String key){
        this.mName = name;
        this.mCoinType = type;
        this.mPath = path;
        this.mAddress = address;
        this.mKey = key;
    }


}
