package com.Simple_desktop;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用管理Activity
 */
public class AppManagerActivity extends Activity {
    private RecyclerView recyclerView;
    private AppManagerAdapter adapter;
    private List<AppInfo> allApps;
    private List<AppInfo> desktopApps;
    private DataManager dataManager;
    private DesktopConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        dataManager = new DataManager(this);
        config = dataManager.loadConfig();
        desktopApps = dataManager.loadDesktopApps();

        // 加载所有应用
        loadAllApps();

        // 设置RecyclerView
        recyclerView = findViewById(R.id.appListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppManagerAdapter(this, allApps, desktopApps, this::onAppAction);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 加载所有已安装应用
     */
    private void loadAllApps() {
        allApps = new ArrayList<>();
        PackageManager pm = getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, 0);

        for (ResolveInfo info : resolveInfos) {
            String packageName = info.activityInfo.packageName;
            String activityName = info.activityInfo.name;
            String appName = info.loadLabel(pm).toString();

            AppInfo appInfo = new AppInfo(appName, packageName, activityName);
            appInfo.setIcon(info.loadIcon(pm));
            allApps.add(appInfo);
        }
    }

    /**
     * 应用操作回调
     */
    private void onAppAction(AppInfo app, boolean isAdd) {
        if (isAdd) {
            // 添加到桌面
            int maxApps = config.getFirstPageAppCount() +
                         (config.getNormalPageAppCount() * 10); // 假设最多10页
            if (desktopApps.size() >= maxApps) {
                Toast.makeText(this, R.string.desktop_full, Toast.LENGTH_SHORT).show();
                return;
            }

            // 计算位置
            int pageIndex = 0;
            int positionInPage = 0;
            if (desktopApps.size() < config.getFirstPageAppCount()) {
                pageIndex = 0;
                positionInPage = desktopApps.size();
            } else {
                int remainingApps = desktopApps.size() - config.getFirstPageAppCount();
                pageIndex = 1 + (remainingApps / config.getNormalPageAppCount());
                positionInPage = remainingApps % config.getNormalPageAppCount();
            }

            app.setPageIndex(pageIndex);
            app.setPositionInPage(positionInPage);
            desktopApps.add(app);
            dataManager.saveDesktopApps(desktopApps);
            adapter.updateDesktopApps(desktopApps);
            Toast.makeText(this, app.getAppName() + " " + getString(R.string.add_to_desktop), Toast.LENGTH_SHORT).show();
        } else {
            // 从桌面移除
            desktopApps.removeIf(a ->
                a.getPackageName().equals(app.getPackageName()) &&
                a.getActivityName().equals(app.getActivityName())
            );
            dataManager.saveDesktopApps(desktopApps);
            adapter.updateDesktopApps(desktopApps);
            Toast.makeText(this, app.getAppName() + " " + getString(R.string.remove_from_desktop), Toast.LENGTH_SHORT).show();
        }
    }
}
