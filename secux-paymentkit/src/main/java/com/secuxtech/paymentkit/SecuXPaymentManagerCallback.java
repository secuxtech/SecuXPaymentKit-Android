package com.secuxtech.paymentkit;

import android.media.Image;

public abstract class SecuXPaymentManagerCallback {

    public void paymentDone(final boolean ret, final String errorMsg){

    }

    public void updatePaymentStatus(final String status){

    }

    public void getStoreInfoDone(final boolean ret, final String storeName, final Image storeLogo){

    }

}
