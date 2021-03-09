package com.stu.service;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.widget.LinearLayout;

import com.just.agentweb.AgentWeb;

public class InformationActivity extends AppCompatActivity {
    private AgentWeb mAgentWeb;
    private String url = "https://gitee.com/syz913/StudentsService/blob/master/README.md";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Announcement");
        bar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_information);
        LinearLayout mLinearLayout = findViewById(R.id.view);
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .createAgentWeb()
                .ready()
                .go(url);
        WebSettings settings = mAgentWeb.getAgentWebSettings().getWebSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}