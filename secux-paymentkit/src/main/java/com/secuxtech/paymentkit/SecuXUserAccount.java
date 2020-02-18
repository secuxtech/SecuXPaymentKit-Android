package com.secuxtech.paymentkit;

import java.util.ArrayList;

public class SecuXUserAccount {
    public String mAccountName = "";
    public String mPassword = "";
    public String mEmail = "";
    public String mAlias = "";
    public String mPhoneNum = "";
    public String mUserType = "";

    public ArrayList<SecuXCoinAccount> mCoinAccountArr = new ArrayList<>();

    public SecuXUserAccount(String email, String phone, String password){
        mAccountName = email.substring(0, email.indexOf('@'));
        mPassword = password;
        mEmail = email;
        mAlias = mAccountName;
        mPhoneNum = phone;
    }
    
    public SecuXCoinAccount getCoinAccount(@SecuXCoinType.CoinType String coinType){

        for(int i=0; i<mCoinAccountArr.size(); i++){
            SecuXCoinAccount account = mCoinAccountArr.get(i);
            if (account.mCoinType.compareTo(coinType)==0){
                return account;
            }
        }
            
        return null;
    }
}
