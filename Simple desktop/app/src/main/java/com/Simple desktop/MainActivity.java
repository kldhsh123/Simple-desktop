package com.Simple_desktop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.viewpager2.widget.ViewPager2;
import java.text.SimpleDateFormat;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 主桌面Activity
 */
public class MainActivity extends Activity {
    private ViewPager2 viewPager;
    private LinearLayout indicatorLayout;
    private DesktopPagerAdapter pagerAdapter;
    private DataManager dataManager;
    private DesktopConfig config;
    private List<AppInfo> desktopApps;
    private Handler timeHandler;
    private Runnable timeUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 加载自定义壁纸（如果存在）
        loadWallpaper();

        // 初始化数据管理器
        dataManager = new DataManager(this);
        config = dataManager.loadConfig();
        desktopApps = dataManager.loadDesktopApps();

        // 加载应用图标
        loadAppIcons();

        // 初始化界面
        initViews();

        // 启动时间更新
        startTimeUpdate();

        // 首次启动或应用列表为空时显示提示
        showFirstLaunchTip();

        // 确保设置按钮初始状态正确（只在第一页显示）
        updateSettingsButtonVisibility(0);
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        indicatorLayout = findViewById(R.id.indicatorLayout);

        // 设置适配器
        pagerAdapter = new DesktopPagerAdapter(this, desktopApps, config);
        viewPager.setAdapter(pagerAdapter);

        // 设置页面变化监听
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateIndicators(position);
                updateSettingsButtonVisibility(position);  // 更新设置按钮可见性
            }
        });

        // 初始化指示器
        setupIndicators();

        // 设置按钮点击事件（带震动反馈）
        ImageButton settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> {
            // 震动反馈
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                vibrator.vibrate(50); // 震动50毫秒
            }

            // 跳转到设置页面
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    /**
     * 加载应用图标
     */
    private void loadAppIcons() {
        PackageManager pm = getPackageManager();
        for (AppInfo app : desktopApps) {
            try {
                Intent intent = new Intent();
                intent.setClassName(app.getPackageName(), app.getActivityName());
                ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
                if (resolveInfo != null) {
                    app.setIcon(resolveInfo.loadIcon(pm));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置页面指示器
     */
    private void setupIndicators() {
        indicatorLayout.removeAllViews();
        int pageCount = pagerAdapter.getItemCount();

        // 如果只有1页或没有页面，隐藏指示器容器
        if (pageCount <= 1) {
            indicatorLayout.setVisibility(View.GONE);
            return;
        }

        // 多页时显示指示器
        indicatorLayout.setVisibility(View.VISIBLE);

        for (int i = 0; i < pageCount; i++) {
            ImageView indicator = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.indicator_size),
                    getResources().getDimensionPixelSize(R.dimen.indicator_size)
            );
            params.setMargins(
                    getResources().getDimensionPixelSize(R.dimen.indicator_margin),
                    0,
                    getResources().getDimensionPixelSize(R.dimen.indicator_margin),
                    0
            );
            indicator.setLayoutParams(params);
            indicator.setBackgroundResource(R.drawable.indicator_dot);
            indicatorLayout.addView(indicator);
        }
        updateIndicators(0);
    }

    /**
     * 更新指示器状态
     */
    private void updateIndicators(int currentPosition) {
        for (int i = 0; i < indicatorLayout.getChildCount(); i++) {
            ImageView indicator = (ImageView) indicatorLayout.getChildAt(i);
            if (i == currentPosition) {
                indicator.setColorFilter(getResources().getColor(R.color.indicator_active, null));
            } else {
                indicator.setColorFilter(getResources().getColor(R.color.indicator_inactive, null));
            }
        }
    }

    /**
     * 启动应用
     */
    public void launchApp(AppInfo app) {
        try {
            Intent intent = new Intent();
            intent.setClassName(app.getPackageName(), app.getActivityName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, R.string.launch_failed, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 启动时间更新
     */
    private void startTimeUpdate() {
        timeHandler = new Handler();
        timeUpdater = new Runnable() {
            @Override
            public void run() {
                if (pagerAdapter != null) {
                    pagerAdapter.updateTime();
                }
                // 每分钟更新一次
                timeHandler.postDelayed(this, 60000);
            }
        };

        // 启动定时器（60秒后更新，首次显示在bind()中已处理）
        timeHandler.postDelayed(timeUpdater, 60000);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 重新加载壁纸
        loadWallpaper();

        // 刷新数据（可能在设置中修改了）
        config = dataManager.loadConfig();
        desktopApps = dataManager.loadDesktopApps();
        loadAppIcons();

        // 刷新适配器
        pagerAdapter.updateData(desktopApps, config);
        setupIndicators();

        // 立即更新时间
        if (pagerAdapter != null) {
            pagerAdapter.updateTime();
        }
    }

    /**
     * 显示首次启动提示
     */
    private void showFirstLaunchTip() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean("is_first_launch", true);

        // 如果是首次启动，或者应用列表为空，显示提示
        if (isFirstLaunch || desktopApps.isEmpty()) {
            Toast.makeText(this, R.string.tip_long_press_settings, Toast.LENGTH_LONG).show();

            // 标记已显示过提示
            if (isFirstLaunch) {
                prefs.edit().putBoolean("is_first_launch", false).apply();
            }
        }
    }

    /**
     * 加载壁纸
     */
    private void loadWallpaper() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean hasCustomWallpaper = prefs.getBoolean("has_custom_wallpaper", false);

        View rootLayout = findViewById(R.id.rootLayout);

        if (hasCustomWallpaper) {
            try {
                File wallpaperFile = new File(getFilesDir(), "wallpaper.jpg");
                if (wallpaperFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(wallpaperFile.getAbsolutePath());
                    BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
                    rootLayout.setBackground(drawable);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 使用默认背景
            rootLayout.setBackgroundColor(getResources().getColor(R.color.background_default, null));
        }
    }

    /**
     * 更新设置按钮可见性（只在第一页显示）
     */
    private void updateSettingsButtonVisibility(int currentPage) {
        ImageButton settingsButton = findViewById(R.id.settingsButton);
        if (settingsButton != null) {
            if (currentPage == 0) {
                settingsButton.setVisibility(View.VISIBLE);
            } else {
                settingsButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeHandler != null && timeUpdater != null) {
            timeHandler.removeCallbacks(timeUpdater);
        }
    }
}
