package com.example.mohamed.paybalapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mohamed.paybalapp.baybalBuilder.BayBalServices;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;

import java.math.BigDecimal;

public class SimpleActivity extends AppCompatActivity implements BayBalServices.confirmLisnter{
     private Button orderButton;
     private BayBalServices.BaybalBuilder balServices;
     private BayBalServices services;

    String CONFIG_CLIENT_ID = "AVZUbOX3ry-gyvTBVykh9TnK1v49hM0ycQiquryr8NjuRwnayplCFEm1M4ZnK5Q9JCcWzn5_briWUeRH";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        orderButton=findViewById(R.id.order);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayPalItem[] items =
                        {
                                new PayPalItem("sample item #1", 2, new BigDecimal("87.50"), "USD",
                                        "sku-12345678"),
                                new PayPalItem("free sample item #2", 1, new BigDecimal("0.00"),
                                        "USD", "sku-zero-price"),
                                new PayPalItem("sample item #3 with a longer name", 6, new BigDecimal("37.99"),
                                        "USD", "sku-33333")
                        };
                balServices=new BayBalServices.BaybalBuilder(SimpleActivity.this,CONFIG_CLIENT_ID,
                        PayPalConfiguration.ENVIRONMENT_NO_NETWORK
                ,"USD",PayPalPayment.PAYMENT_INTENT_SALE,SimpleActivity.this)
                .setPrice(12.0)
                .setItems(items,"produects",10.0,2.0)
                ;
               services= balServices.build();
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        services.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onConfirm(String result) {
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onInvalid() {

    }


}
