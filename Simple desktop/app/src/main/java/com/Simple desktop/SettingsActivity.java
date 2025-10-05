package com.Simple_desktop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 设置Activity
 */
public class SettingsActivity extends Activity {
    private static final int REQUEST_PICK_IMAGE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 更换壁纸按钮
        Button btnWallpaper = findViewById(R.id.btnWallpaper);
        btnWallpaper.setOnClickListener(v -> chooseWallpaper());

        // 恢复默认壁纸按钮
        Button btnResetWallpaper = findViewById(R.id.btnResetWallpaper);
        btnResetWallpaper.setOnClickListener(v -> resetWallpaper());

        // 应用管理按钮
        Button btnAppManager = findViewById(R.id.btnAppManager);
        btnAppManager.setOnClickListener(v -> {
            Intent intent = new Intent(this, AppManagerActivity.class);
            startActivity(intent);
        });

        // 布局配置按钮
        Button btnLayoutConfig = findViewById(R.id.btnLayoutConfig);
        btnLayoutConfig.setOnClickListener(v -> {
            Intent intent = new Intent(this, LayoutConfigActivity.class);
            startActivity(intent);
        });

        // 调整应用顺序按钮
        Button btnReorderApps = findViewById(R.id.btnReorderApps);
        btnReorderApps.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReorderAppsActivity.class);
            startActivity(intent);
        });

        // 返回桌面按钮
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 设置网站链接可点击
        TextView tvWebsite = findViewById(R.id.tvWebsite);
        tvWebsite.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 选择壁纸
     */
    private void chooseWallpaper() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            setWallpaper(imageUri);
        }
    }

    /**
     * 设置壁纸（保存到应用内部）
     */
    private void setWallpaper(Uri imageUri) {
        try {
            // 将图片保存到应用内部存储
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // 保存到应用内部文件
            FileOutputStream fos = openFileOutput("wallpaper.jpg", Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            // 保存壁纸设置标记
            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
            prefs.edit().putBoolean("has_custom_wallpaper", true).apply();

            Toast.makeText(this, R.string.wallpaper_set_success, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.wallpaper_set_failed, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 恢复默认壁纸
     */
    private void resetWallpaper() {
        try {
            // 删除自定义壁纸文件
            File wallpaperFile = new File(getFilesDir(), "wallpaper.jpg");
            if (wallpaperFile.exists()) {
                wallpaperFile.delete();
            }

            // 清除壁纸设置标记
            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
            prefs.edit().putBoolean("has_custom_wallpaper", false).apply();

            Toast.makeText(this, R.string.reset_wallpaper_success, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.reset_wallpaper_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
