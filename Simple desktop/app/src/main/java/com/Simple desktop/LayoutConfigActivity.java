package com.Simple_desktop;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 布局配置Activity
 */
public class LayoutConfigActivity extends Activity {
    private TextView tvFirstPageCount;
    private TextView tvNormalPageCount;
    private TextView tvOffsetValue;
    private DesktopConfig config;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_config);

        dataManager = new DataManager(this);
        config = dataManager.loadConfig();

        // 初始化视图
        tvFirstPageCount = findViewById(R.id.tvFirstPageCount);
        tvNormalPageCount = findViewById(R.id.tvNormalPageCount);
        tvOffsetValue = findViewById(R.id.tvOffsetValue);

        // 显示当前配置
        updateDisplay();

        // 首页应用数量按钮
        Button btnFirstPageMinus = findViewById(R.id.btnFirstPageMinus);
        btnFirstPageMinus.setOnClickListener(v -> {
            int count = config.getFirstPageAppCount();
            if (count > 1) {
                config.setFirstPageAppCount(count - 1);
                updateDisplay();
            }
        });

        Button btnFirstPagePlus = findViewById(R.id.btnFirstPagePlus);
        btnFirstPagePlus.setOnClickListener(v -> {
            int count = config.getFirstPageAppCount();
            if (count < 6) {
                config.setFirstPageAppCount(count + 1);
                updateDisplay();
            }
        });

        // 普通页应用数量按钮
        Button btnNormalPageMinus = findViewById(R.id.btnNormalPageMinus);
        btnNormalPageMinus.setOnClickListener(v -> {
            int count = config.getNormalPageAppCount();
            if (count > 2) {
                config.setNormalPageAppCount(count - 1);
                updateDisplay();
            }
        });

        Button btnNormalPagePlus = findViewById(R.id.btnNormalPagePlus);
        btnNormalPagePlus.setOnClickListener(v -> {
            int count = config.getNormalPageAppCount();
            if (count < 9) {
                config.setNormalPageAppCount(count + 1);
                updateDisplay();
            }
        });

        // 普通页偏移量按钮
        Button btnOffsetMinus = findViewById(R.id.btnOffsetMinus);
        btnOffsetMinus.setOnClickListener(v -> {
            int offset = config.getHorizontalOffset();
            config.setHorizontalOffset(offset - 10);  // 每次调整10像素
            updateDisplay();
        });

        Button btnOffsetPlus = findViewById(R.id.btnOffsetPlus);
        btnOffsetPlus.setOnClickListener(v -> {
            int offset = config.getHorizontalOffset();
            config.setHorizontalOffset(offset + 10);  // 每次调整10像素
            updateDisplay();
        });

        // 保存按钮
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            Log.d("LayoutConfig", "保存配置 - 偏移量: " + config.getHorizontalOffset());
            dataManager.saveConfig(config);
            Toast.makeText(this, R.string.config_saved, Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    /**
     * 更新显示
     */
    private void updateDisplay() {
        tvFirstPageCount.setText(String.valueOf(config.getFirstPageAppCount()));
        tvNormalPageCount.setText(String.valueOf(config.getNormalPageAppCount()));
        tvOffsetValue.setText(String.valueOf(config.getHorizontalOffset()));
    }
}
