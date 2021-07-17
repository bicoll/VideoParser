package com.evercocer.tool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.evercocer.tool.utils.ClipboardUtil;

import org.example.factory.*;
import org.example.factory.parsers.DouyinParser;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText et_main;
    private Button bt_main;
    private TextView tv_content;
    static ParserFactory parserFactory = new ParserFactory();
    static DouyinParser douyinParser = (DouyinParser) parserFactory.createParser(ParserFactory.PlatForm.DOU_YIN);

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 200:
                    String url = (String) msg.obj;
                    System.out.println("解析结果：------------" + url);
                    tv_content.setText(url);
                    bt_main.setClickable(true);
                    bt_main.setText(R.string.start_parse);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载主视图
        setContentView(R.layout.activity_main);
        //初始化View视图
        initView();
        //初始化监听事件
        initListener();
    }

    private void initView() {
        et_main = findViewById(R.id.et_main);
        bt_main = findViewById(R.id.bt_main);
        tv_content = findViewById(R.id.tv_content);
        bt_main.setClickable(false);
    }


    /**
     * 初始化View监听事件
     */
    private void initListener() {
        //点击按钮开始解析视频链接
        bt_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_main.setClickable(false);
                bt_main.setText(R.string.parsing);
                //获取编辑文本框的内容
                String baseLink = et_main.getText().toString();
                System.out.println("剪贴板内容" + baseLink);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            douyinParser.parseShare(baseLink, new ParseCallback() {
                                @Override
                                public void onSuccess(List<String> list) {
                                    Message message = Message.obtain();
                                    message.what = 200;
                                    String s = list.get(0);
                                    message.obj = s;
                                    handler.sendMessage(message);
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });

        //解析结果点击复制
        tv_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardUtil.getInstance().setPrimaryClip(MainActivity.this, tv_content.getText().toString());
                Toast.makeText(MainActivity.this, "解析结果已复制，打开浏览器访问即可。", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 窗体得到或失去焦点的时候的时候调用
     * 在 Android Q（10）中，应用在前台的时候才可以获取到剪切板内容。
     *
     * @param hasFocus 是否聚焦
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //没有焦点的时候不获取剪贴板内容
        if (!hasFocus) {
            return;
        }


        String shareText = ClipboardUtil.getInstance().getClipboardContent(this);
        if (shareText != null && !shareText.isEmpty()) {
            bt_main.setClickable(true);
            et_main.setText(shareText);
        } else {
            bt_main.setClickable(false);
            et_main.setText("No valid information was obtained!");
        }

    }


}