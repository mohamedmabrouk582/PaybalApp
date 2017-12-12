package com.example.mohamed.paybalapp.baybalBuilder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

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
 * on 05/12/2017.  time :14:41
 *
 *
 * this class for online payment using pay bal
 *
 *
 *
 */

public class BayBalServices {
    public interface confirmLisnter{
        void  onConfirm(String result);
        void  onCancel();
        void onInvalid();
    }
    private String clientID;
    private String config_environment;
    private int requrestCodePayment;
    private String merchantName;
    private String paymentIntent;
    private Double price;
    private Double shoppingPrice;
    private Double TaxPrice;
    private String priceType;
    private String prodectName;
    private String itemsHeaderName;
    private confirmLisnter lisnter;
    private PayPalItem[] items;


    public String getClientID() {
        return clientID;
    }

    public String getConfig_environment() {
        return config_environment;
    }

    public int getRequrestCodePayment() {
        return requrestCodePayment;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getPaymentIntent() {
        return paymentIntent;
    }

    public Double getPrice() {
        return price;
    }

    public String getPriceType() {
        return priceType;
    }

    public String getProdectName() {
        return prodectName;
    }

    public Double getShoppingPrice() {
        return shoppingPrice;
    }

    public Double getTaxPrice() {
        return TaxPrice;
    }

    public String getItemsHeaderName() {
        return itemsHeaderName;
    }

    public PayPalItem[] getItems() {
        return items;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == requrestCodePayment) {
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
                        //PayBal.getInstance().confrimResult(confirm.getPayment().toJSONObject().toString(4));
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
                lisnter.onInvalid();
                Log.i(
                        "result",
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }

    private BayBalServices(BaybalBuilder builder){
        this.clientID=builder.clientID;
        this.config_environment=builder.config_environment;
        this.requrestCodePayment=builder.requrestCodePayment;
        this.merchantName=builder.merchantName;
        this.paymentIntent=builder.paymentIntent;
        this.price=builder.price;
        this.shoppingPrice=builder.shoppingPrice;
        this.TaxPrice=builder.TaxPrice;
        this.priceType=builder.priceType;
        this.prodectName=builder.prodectName;
        this.itemsHeaderName=builder.itemsHeaderName;
        this.items=builder.items;
        this.lisnter=builder.lisnter;
    }

    public static class BaybalBuilder{
        private String clientID;
        private String config_environment;
        private int requrestCodePayment=555;
        private String merchantName;
        private String paymentIntent;
        private Double price;
        private Double shoppingPrice;
        private Double TaxPrice;
        private String priceType;
        private String prodectName;
        private String itemsHeaderName;
        private PayPalItem[] items;
        private Activity activity;
        private confirmLisnter lisnter;
        /**
         *
         * @param activity
         * @param clientID
         * @param config_environment
         * @param priceType
         * @param paymentIntent
         */
        public BaybalBuilder(Activity activity,String clientID,String config_environment ,String priceType,String paymentIntent,confirmLisnter lisnter){
            this.activity=activity;
            this.clientID=clientID;
            this.config_environment=config_environment;
            this.paymentIntent=paymentIntent;
            this.priceType=priceType;
            this.lisnter=lisnter;
        }

        public BaybalBuilder setMerchantName(String merchantName){
            this.merchantName=merchantName;
            return this;
        }

        public BaybalBuilder setPrice(Double  price){
            this.price=price;
            return this;
        }
        public BaybalBuilder setPriceType(String priceType){
            this.priceType=priceType;
            return this;
        }

        public BaybalBuilder setProdectName(String productName){
            this.prodectName=productName;
            return this;
        }


        /**
         *
         * @see   for set array of items for buy
         * @param items
         * @param itemsHeaderName
         * @param TaxPrice
         * @param shoppingPrice
         * @return BaybalBuilder
         */
        public BaybalBuilder setItems(PayPalItem[] items,String itemsHeaderName,Double TaxPrice,Double shoppingPrice){
            this.items=items;
            this.itemsHeaderName=itemsHeaderName;
            this.TaxPrice=TaxPrice;
            this.shoppingPrice=shoppingPrice;
            return this;
        }

        public BayBalServices build(){
            BayBalServices services=new BayBalServices(this);
            setService(services);
            return  services;
        }


        /**
         * @see for start service
         * @param service
         */
        private void setService(BayBalServices service){
            PayPalConfiguration config = getConfig(service.getConfig_environment(), service.getClientID(), service.getMerchantName());
            Intent intent = new Intent(activity, PayPalService.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
             activity.startService(intent);

            if (service.getItems()!=null){
               buyNow(getStuffToBuy(service.paymentIntent,service.items,service.getShoppingPrice(),service.getTaxPrice()
               ,service.getPriceType(),service.getItemsHeaderName()),config);
            }else {
             buyNow(getThingToBuy(service.paymentIntent,service.price,service.getPriceType(),TextUtils.isEmpty(service.prodectName)?"Item # 1":service.prodectName),config);
            }



        }

        /**
         * @see  for start buy
         * @param palPayment
         * @param config
         */
        public void buyNow(PayPalPayment palPayment, PayPalConfiguration config){
            Intent intent = new Intent(activity, PaymentActivity.class);

            // send the same configuration for restart resiliency
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);

            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, palPayment);

            activity.startActivityForResult(intent,requrestCodePayment);
        }


        /**
         *
         * @param CONFIG_ENVIRONMENT
         * @param CONFIG_CLIENT_ID
         * @param merchantName
         * @return PayPalConfiguration
         */
        private PayPalConfiguration getConfig(String CONFIG_ENVIRONMENT,String CONFIG_CLIENT_ID,String merchantName){

            return new PayPalConfiguration()
                    .environment(CONFIG_ENVIRONMENT)
                    .clientId(CONFIG_CLIENT_ID)
                    // The following are only used in PayPalFuturePaymentActivity.
                    .merchantName(TextUtils.isEmpty(merchantName)?"Example Merchant":merchantName)
                    .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
                    .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));
        }

        /**
         *@see for buy to more one item toghter
         * @param paymentIntent
         * @param items
         * @param shippingPrice
         * @param taxprice
         * @param priceType
         * @param itemsHeaderName
         * @return PayPalPayment
         */
        private PayPalPayment getStuffToBuy(String paymentIntent,PayPalItem[] items,Double shippingPrice,Double taxprice,String priceType,String itemsHeaderName) {
            BigDecimal subtotal = PayPalItem.getItemTotal(items);
            BigDecimal shipping = new BigDecimal(shippingPrice);
            BigDecimal tax = new BigDecimal(taxprice);
            PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);
            BigDecimal amount = subtotal.add(shipping).add(tax);
            PayPalPayment payment = new PayPalPayment(amount, priceType, itemsHeaderName, paymentIntent);
            payment.items(items).paymentDetails(paymentDetails);
            //--- set other optional fields like invoice_number, custom field, and soft_descriptor
            payment.custom("This is text that will be associated with the payment that the app can use.");
            return payment;
        }

        /**
         * @see  for buy to one item
         * @param paymentIntent
         * @param price
         * @param priceType
         * @param productName
         * @return PayPalPayment
         */
        private PayPalPayment getThingToBuy(String paymentIntent,Double price ,String priceType,String productName) {
            return new PayPalPayment(new BigDecimal(price), priceType,productName ,
                    paymentIntent);
        }
    }
}
