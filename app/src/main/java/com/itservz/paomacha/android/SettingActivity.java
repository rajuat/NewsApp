package com.itservz.paomacha.android;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Line;
import com.itservz.paomacha.android.backend.FirebaseDatabaseService;
import com.itservz.paomacha.android.utils.ScreenSizeScaler;
import com.itservz.paomacha.android.view.FlowLayout;

public class SettingActivity extends AppCompatActivity {

    private FlowLayout flowLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarTop);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        toolbar.setTitleTextColor(getResources().getColor(R.color.primary_dark));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        flowLayout = (FlowLayout) findViewById(R.id.flowLayout);
        String[] staticCategories = getResources().getStringArray(R.array.static_categories);
        for(int i = 0; i < staticCategories.length ; i++){
            addCategories(staticCategories[i]);
        }
        for(int i = 0; i < 10; i++) {
            addCategories("Category :"+i);
        }

        findViewById(R.id.notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingActivity.this, "This feature is coming soon", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static final String CATEGORY = "category";
    private void addCategories(String i) {
        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layout);
        ScreenSizeScaler screenSizeScaler = new ScreenSizeScaler(getResources());
        int px = screenSizeScaler.getdpAspixel(8);
        linearLayout.setPadding(px, px, px, px);

        final TextView textView = new TextView(this);
        textView.setLayoutParams(layout);
        textView.setBackground(getResources().getDrawable(R.drawable.rounded_border));
        textView.setPadding(px, px, px, px);
        textView.setTextColor(getResources().getColor(R.color.primary_dark));
        textView.setText(i);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = textView.getText().toString();
                setTitle(value);
                Intent intent = new Intent();
                intent.putExtra(CATEGORY, value);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        linearLayout.addView(textView);
        flowLayout.addView(linearLayout);
    }
}
