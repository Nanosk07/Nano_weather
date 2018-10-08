package com.nano.nano_weather.Ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.nano.nano_weather.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.text_aaa)
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        textView.setText("毕设项目,所有图标资源均来源于iconfont.cn，" +
                "同时感谢xcc3641，本APP界面布局参照就看天气APP。");
    }
}
