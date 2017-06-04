package com.aohanyao.product.flavorsdemos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.aohanyao.product.flavorsdemos.global.Constant;

public class MainActivity extends AppCompatActivity {

    private TextView tvApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvApi = (TextView) findViewById(R.id.tv_api);
        tvApi.setText(Constant.API_ADDRESS);
    }
}
