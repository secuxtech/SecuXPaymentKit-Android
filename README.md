# SecuXPaymentKit

[![](https://jitpack.io/v/secuxtech/secux-paymentkit-android.svg)](https://jitpack.io/#secuxtech/secux-paymentkit-android)

## Usage

### Add JitPack repository

```java
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

### Add dependency secux-paymentkit-android

```java
dependencies {
    implementation 'com.github.secuxtech:secux-paymentkit-android:1.0.9'
}
```

### Add dependency secux-peripheralkit-1.0.0.aar

Download the [secux-peripheralkit-1.0.0.aar](https://github.com/secuxtech/secux-peripheralkit-android/tree/master/repository/com/secuxtech/secux-peripheralkit/1.0.0)

Copy the secux-peripheralkit-1.0.0.aar to ~/app/libs

Add dependency
```java
implementation fileTree(dir: 'libs', include: ['*.aar'])
```

### Add bluetooth privacy permissions

    Add permission to the AndroidManifest.xml

    <uses-permission android:name="android.permission.BLUETOOTH"/>
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

    Request permission

    ```java
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
    ```

### Import the the module

```java 
 import com.secuxtech.paymentkit.*;
```

### Use SecuXAccountManager to get account balance and history

```java

private SecuXAccountManager mAccountManager = new SecuXAccountManager();
private SecuXAccount mAccount;

mAccount = new SecuXAccount("Alice-Libra", SecuXCoinType.LBR, "", "XXXX", "");

Map<String, Double> coinRate = mAccountManager.getCoinUSDRate();
SecuXAccountBalance balance = new SecuXAccountBalance();
if (mAccountManager.getAccountBalance(mAccount, balance)){
    Double usdBalance = balance.mUSDBalance;
    if (balance.mUSDBalance==0 && coinRate!=null && coinRate.containsKey(mAccount.mCoinType)){
        usdBalance = balance.mFormatedBalance * coinRate.get(mAccount.mCoinType);
    }

    Log.i("secux-paymentkit-exp",
            "getAccountBalance done. balance= " + String.valueOf(balance.mFormatedBalance) + ", usdBalance=" + String.valueOf(usdBalance));
}else{
    Log.i("secux-paymentkit-exp", "get account balance failed!");
}
```
boolean getAccountBalance(SecuXAccount account, SecuXAccountBalance balance) output SecuXAccountBalance object

```java
public class SecuXAccountBalance {

    public Double mBalance = Double.valueOf(0);
    public Double mFormatedBalance = Double.valueOf(0);
    public Double mUSDBalance = Double.valueOf(0);
}
```
boolean getAccountHistory(SecuXAccount account, ArrayList<SecuXAccountHisotry> historyList) output SecuXAccountHistory object array

```java
public class SecuXAccountHisotry {
    public String address = "";
    public String tx_type = "";
    public Double amount = Double.valueOf(0);
    public String amount_symbol = "";
    public Double formatted_amount = Double.valueOf(0);
    public Double amount_usd = Double.valueOf(0);
    public String timestamp = "";
    public String detailslUrl = "";
}
 ```

### Use SecuXPaymentManager to get store info. and do payment

* Implement SecuXPaymentManagerCallback functions

    **public void paymentDone(final boolean ret, final String errorMsg)** 
    Called when payment is completed. Returns payment result and error message.
        
    **public void updatePaymentStatus(final String status)**
    Called when payment status is changed. Payment status are: "Device connecting...", "DCT transferring..." and "Device verifying..."
       
    **public void getStoreInfoDone(final boolean ret, final String storeName, final Bitmap storeLogo)**   
    Called when get store information is completed. Returns store name and store logo.
      
    
```java
 mPaymentManager.setSecuXPaymentManagerCallback(mPaymentMgrCallback);

 private SecuXPaymentManagerCallback mPaymentMgrCallback = new SecuXPaymentManagerCallback() {

        @Override
        public void paymentDone(final boolean ret, final String errorMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ret){
                        Toast toast = Toast.makeText(mContext, "Payment successful!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }else{
                        Toast toast = Toast.makeText(mContext, "Payment failed! Error: " + errorMsg, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                }
            });

        }

        @Override
        public void updatePaymentStatus(final String status){
            Log.i("secux-paymentkit-exp", "Update payment status: " + status);
        }

        @Override
        public void getStoreInfoDone(final boolean ret, final String storeName, final Bitmap storeLogo){
            Log.i("secux-paymentkit-exp", "Get store info. done ret=" + String.valueOf(ret) + ",name=" + storeName);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ret){
                        final String name = storeName;

                        //Use SecuXManager to do payment, must call in main thread
                        mPaymentManager.doPayment(mContext, mAccount, name, mPaymentInfo);

                    }else{
                        Toast toast = Toast.makeText(mContext, "Get store info. failed!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                }
            });

        }

    };
```

* Get store information

```java
 mPaymentManager.getStoreInfo(getBaseContext(), mPaymentInfo);
```

* Do payment

```java
 String mPaymentInfo = "{\"amount\":\"11\", \"coinType\":\"LBR\", \"deviceID\":\"4ab10000726b\"}";
 mPaymentManager.doPayment(mContext, mAccount, name, mPaymentInfo);
```

## Demo APP

Please find more in our [demo app](https://github.com/secuxtech/secux-paymentdemo-android)

## Author

SecuX, maochunsun@secuxtech.com

## License

SecuXPaymentKit is available under the MIT license. See the LICENSE file for more info.
