package com.Simple_desktop;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 应用顺序调整Activity
 */
public class ReorderAppsActivity extends Activity {
    private RecyclerView recyclerView;
    private ReorderAdapter adapter;
    private List<AppInfo> appList;
    private SharedPreferences prefs;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reorder_apps);

        prefs = getSharedPreferences("SimpleDesktopPrefs", MODE_PRIVATE);
        gson = new Gson();

        // 加载应用列表
        loadApps();

        // 初始化RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReorderAdapter(appList);
        recyclerView.setAdapter(adapter);

        // 设置拖拽功能
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(appList, fromPosition, toPosition);
                adapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    // 拖拽时改变透明度
                    viewHolder.itemView.setAlpha(0.5f);
                }
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                // 恢复透明度
                viewHolder.itemView.setAlpha(1.0f);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // 不支持滑动删除
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // 保存按钮
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveOrder());

        // 取消按钮
        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> finish());
    }

    /**
     * 加载应用列表
     */
    private void loadApps() {
        String json = prefs.getString("desktopApps", "[]");
        Type type = new TypeToken<List<AppInfo>>() {}.getType();
        appList = gson.fromJson(json, type);

        if (appList == null) {
            appList = new ArrayList<>();
        }

        // 重新加载应用图标（因为Drawable无法序列化）
        PackageManager pm = getPackageManager();
        for (AppInfo app : appList) {
            try {
                ApplicationInfo appInfo = pm.getApplicationInfo(app.getPackageName(), 0);
                app.setIcon(appInfo.loadIcon(pm));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存新的顺序
     */
    private void saveOrder() {
        // 保存前清除icon（Drawable无法序列化）
        for (AppInfo app : appList) {
            app.setIcon(null);
        }

        String json = gson.toJson(appList);
        prefs.edit().putString("desktopApps", json).apply();
        Toast.makeText(this, R.string.reorder_success, Toast.LENGTH_SHORT).show();
        finish();
    }
}
