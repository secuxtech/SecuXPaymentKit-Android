package com.secuxtech.paymentkit;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SecuXCoinType {
    public static final String BTC = "BTC";
    public static final String DCT = "DCT";
    public static final String LBR = "LBR";
    public static final String IFC = "IFC";

    @StringDef({BTC, DCT, LBR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CoinType {}
}
