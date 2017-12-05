package com.example.mohamed.paybalapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

/**
 * Created by mohamed mabrouk
 * 0201152644726
 * on 05/12/2017.  time :13:34
 */

public class BayBalService {
    interface confirmLisnter{
        void  onConfirm(String result);
        void  onCancel();
        void  onInvilad();
    }
    private Activity activity;
    private confirmLisnter lisnter;
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;

    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "AVZUbOX3ry-gyvTBVykh9TnK1v49hM0ycQiquryr8NjuRwnayplCFEm1M4ZnK5Q9JCcWzn5_briWUeRH";

    public static final int REQUEST_CODE_PAYMENT = 1;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("Example Merchant")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));

    public BayBalService(Activity activity,confirmLisnter lisnter){
        this.activity=activity;
        this.lisnter=lisnter;
        Intent intent = new Intent(activity, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        activity.startService(intent);
    }

    public void buyNow(PayPalPayment palPayment){
        Intent intent = new Intent(activity, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, palPayment);

        activity.startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    public void onBuyPressedOne(String paymentIntent,Double price ,String priceType,String prodectName){
      buyNow(getThingToBuy(paymentIntent,price,priceType,prodectName));
    }


    public void onBuyPressedStuff(String paymentIntent,PayPalItem[] items,Double shippingPrice,Double taxprice,String priceType,String itemsHeaderName){
      buyNow(getStuffToBuy(paymentIntent,items,shippingPrice,taxprice,priceType,itemsHeaderName));
    }

    private PayPalPayment getStuffToBuy(String paymentIntent,PayPalItem[] items,Double shippingPrice,Double taxprice,String priceType,String itemsHeaderName) {
        //--- include an item list, payment amount details
//        PayPalItem[] items =
//                {
//                        new PayPalItem("sample item #1", 2, new BigDecimal("87.50"), "USD",
//                                "sku-12345678"),
//                        new PayPalItem("free sample item #2", 1, new BigDecimal("0.00"),
//                                "USD", "sku-zero-price"),
//                        new PayPalItem("sample item #3 with a longer name", 6, new BigDecimal("37.99"),
//                                "USD", "sku-33333")
//                };
        BigDecimal subtotal = PayPalItem.getItemTotal(items);
        BigDecimal shipping = new BigDecimal(shippingPrice);
        BigDecimal tax = new BigDecimal(taxprice);
        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);
        BigDecimal amount = subtotal.add(shipping).add(tax);
        PayPalPayment payment = new PayPalPayment(amount, priceType, itemsHeaderName, paymentIntent);
        payment.items(items).paymentDetails(paymentDetails);

        //--- set other optional fields like invoice_number, custom field, and soft_descriptor
        payment.custom("This is text that will be associated with the payment that the app can use.");
        String it="\n";
        for (PayPalItem payment1:items) {
            it+=payment1.toString()+"\n";
        }
        Log.d("paymentDetails", paymentDetails.toJSONObject() + "");
        //addAppProvidedShippingAddress(payment);

        // Toast.makeText(this, payment.toJSONObject()+"", Toast.LENGTH_LONG).show();
        return payment;
    }

    private PayPalPayment getThingToBuy(String paymentIntent,Double price ,String priceType,String productName) {
        return new PayPalPayment(new BigDecimal(price), priceType,productName ,
                paymentIntent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i("result", confirm.toJSONObject().toString(4));
                        Log.i("result", confirm.getPayment().toJSONObject().toString(4));
                        /**
                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         *
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */
                        //displayResultText("PaymentConfirmation info received from PayPal");
                         lisnter.onConfirm(confirm.getPayment().toJSONObject().toString(4));

                    } catch (JSONException e) {
                        Log.e("result", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                lisnter.onCancel();
                Log.i("result", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                lisnter.onInvilad();
                Log.i(
                        "result",
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }



}
