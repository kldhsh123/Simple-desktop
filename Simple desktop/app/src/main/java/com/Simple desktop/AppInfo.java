package com.Simple_desktop;

import android.graphics.drawable.Drawable;

/**
 * 应用信息数据模型
 */
public class AppInfo {
    private String appName;        // 应用名称
    private String packageName;    // 包名
    private String activityName;   // Activity名称
    private Drawable icon;         // 应用图标
    private int pageIndex;         // 所在页面索引
    private int positionInPage;    // 在页面中的位置

    // 构造函数
    public AppInfo(String appName, String packageName, String activityName) {
        this.appName = appName;
        this.packageName = packageName;
        this.activityName = activityName;
        this.pageIndex = -1;
        this.positionInPage = -1;
    }

    // Getter和Setter方法
    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }

    public Drawable getIcon() { return icon; }
    public void setIcon(Drawable icon) { this.icon = icon; }

    public int getPageIndex() { return pageIndex; }
    public void setPageIndex(int pageIndex) { this.pageIndex = pageIndex; }

    public int getPositionInPage() { return positionInPage; }
    public void setPositionInPage(int positionInPage) { this.positionInPage = positionInPage; }
}
