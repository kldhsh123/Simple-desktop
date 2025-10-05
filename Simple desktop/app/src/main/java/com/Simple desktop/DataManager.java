package com.Simple_desktop;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据管理器 - 负责保存和加载桌面配置
 */
public class DataManager {
    private static final String PREF_NAME = "SimpleDesktopPrefs";
    private static final String KEY_DESKTOP_APPS = "desktopApps";
    private static final String KEY_CONFIG = "desktopConfig";

    private SharedPreferences prefs;
    private Gson gson;

    public DataManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    /**
     * 保存桌面应用列表
     */
    public void saveDesktopApps(List<AppInfo> apps) {
        // 创建简化版本的数据（不包含Drawable）
        List<SimpleAppInfo> simpleApps = new ArrayList<>();
        for (AppInfo app : apps) {
            simpleApps.add(new SimpleAppInfo(app));
        }
        String json = gson.toJson(simpleApps);
        prefs.edit().putString(KEY_DESKTOP_APPS, json).apply();
    }

    /**
     * 加载桌面应用列表
     */
    public List<AppInfo> loadDesktopApps() {
        String json = prefs.getString(KEY_DESKTOP_APPS, "[]");
        Type type = new TypeToken<List<SimpleAppInfo>>(){}.getType();
        List<SimpleAppInfo> simpleApps = gson.fromJson(json, type);

        List<AppInfo> apps = new ArrayList<>();
        for (SimpleAppInfo simple : simpleApps) {
            apps.add(simple.toAppInfo());
        }
        return apps;
    }

    /**
     * 保存桌面配置
     */
    public void saveConfig(DesktopConfig config) {
        String json = gson.toJson(config);
        Log.d("DataManager", "保存配置JSON: " + json);
        prefs.edit().putString(KEY_CONFIG, json).apply();
    }

    /**
     * 加载桌面配置
     */
    public DesktopConfig loadConfig() {
        String json = prefs.getString(KEY_CONFIG, "");
        Log.d("DataManager", "加载配置JSON: " + json);
        if (json.isEmpty()) {
            return new DesktopConfig(); // 返回默认配置
        }
        DesktopConfig config = gson.fromJson(json, DesktopConfig.class);
        Log.d("DataManager", "加载配置 - 偏移量: " + config.getHorizontalOffset());
        return config;
    }

    /**
     * 简化的应用信息（用于JSON序列化）
     */
    private static class SimpleAppInfo {
        String appName;
        String packageName;
        String activityName;
        int pageIndex;
        int positionInPage;

        SimpleAppInfo(AppInfo app) {
            this.appName = app.getAppName();
            this.packageName = app.getPackageName();
            this.activityName = app.getActivityName();
            this.pageIndex = app.getPageIndex();
            this.positionInPage = app.getPositionInPage();
        }

        AppInfo toAppInfo() {
            AppInfo app = new AppInfo(appName, packageName, activityName);
            app.setPageIndex(pageIndex);
            app.setPositionInPage(positionInPage);
            return app;
        }
    }
}
