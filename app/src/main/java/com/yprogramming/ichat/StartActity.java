package com.yprogramming.ichat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class StartActity extends AppCompatActivity {

    private Button btnStart;
    private Toolbar ichStartChatToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ichStartChatToolBar = (Toolbar)findViewById(R.id.ichStartChatToolBar);
        setSupportActionBar(ichStartChatToolBar);
        getSupportActionBar().setTitle("Ready to iChat");


        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRegister = new Intent(StartActity.this, RegisterActivity.class);
                startActivity(intentRegister);
                finish();
            }
        });
    }
}
