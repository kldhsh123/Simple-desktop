package com.Simple_desktop;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 桌面页面适配器
 */
public class DesktopPagerAdapter extends RecyclerView.Adapter<DesktopPagerAdapter.PageViewHolder> {
    private MainActivity activity;
    private List<List<AppInfo>> pages;  // 每页的应用列表
    private DesktopConfig config;
    private List<View> pageViews;  // 保存页面视图引用，用于更新时间

    public DesktopPagerAdapter(MainActivity activity, List<AppInfo> apps, DesktopConfig config) {
        this.activity = activity;
        this.config = config;
        this.pageViews = new ArrayList<>();
        organizeApps(apps);
    }

    /**
     * 将应用按页组织
     */
    private void organizeApps(List<AppInfo> apps) {
        pages = new ArrayList<>();
        int firstPageCount = config.getFirstPageAppCount();
        int normalPageCount = config.getNormalPageAppCount();

        int index = 0;
        // 第一页
        List<AppInfo> firstPage = new ArrayList<>();
        for (int i = 0; i < firstPageCount && index < apps.size(); i++, index++) {
            firstPage.add(apps.get(index));
        }
        if (!firstPage.isEmpty()) {
            pages.add(firstPage);
        }

        // 后续页
        while (index < apps.size()) {
            List<AppInfo> page = new ArrayList<>();
            for (int i = 0; i < normalPageCount && index < apps.size(); i++, index++) {
                page.add(apps.get(index));
            }
            pages.add(page);
        }

        // 如果没有应用，至少显示第一页
        if (pages.isEmpty()) {
            pages.add(new ArrayList<>());
        }
    }

    /**
     * 更新数据
     */
    public void updateData(List<AppInfo> apps, DesktopConfig config) {
        this.config = config;
        Log.d("DesktopPager", "updateData - 配置偏移量: " + config.getHorizontalOffset());
        pageViews.clear();
        organizeApps(apps);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 0 : 1;  // 0=首页，1=普通页
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == 0) {
            // 第一页
            view = inflater.inflate(R.layout.page_first, parent, false);
        } else {
            // 普通页
            view = inflater.inflate(R.layout.page_normal, parent, false);
        }
        return new PageViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        List<AppInfo> pageApps = pages.get(position);
        boolean isFirstPage = (position == 0);

        // 应用配置的偏移量到GridLayout
        if (isFirstPage) {
            holder.appGrid.setTranslationX(0);
            Log.d("DesktopPager", "onBind - 第一页，偏移=0");
        } else {
            int offset = config.getHorizontalOffset();
            holder.appGrid.setTranslationX(offset);
            Log.d("DesktopPager", "onBind - 普通页(位置" + position + ")，偏移=" + offset + "，config对象=" + config);
        }

        holder.bind(pageApps, isFirstPage);

        // 保存页面视图
        if (position == 0 && !pageViews.contains(holder.itemView)) {
            pageViews.add(holder.itemView);
        }
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    /**
     * 更新时间显示（仅第一页）
     */
    public void updateTime() {
        for (View pageView : pageViews) {
            TextView clockText = pageView.findViewById(R.id.clockText);
            TextView ampmText = pageView.findViewById(R.id.ampmText);
            TextView dateText = pageView.findViewById(R.id.dateText);

            if (clockText != null && ampmText != null && dateText != null) {
                Calendar calendar = Calendar.getInstance();

                // 12小时制时间
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.CHINA);
                clockText.setText(timeFormat.format(calendar.getTime()));

                // 上午/下午
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                ampmText.setText(hour < 12 ? activity.getString(R.string.am) : activity.getString(R.string.pm));

                // 日期和星期
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                String weekday = getWeekdayString(dayOfWeek);
                String dateStr = String.format(Locale.CHINA, "%d年%d月%d日 %s", year, month, day, weekday);
                dateText.setText(dateStr);
            }
        }
    }

    /**
     * 获取星期字符串
     */
    private String getWeekdayString(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY: return activity.getString(R.string.weekday_sunday);
            case Calendar.MONDAY: return activity.getString(R.string.weekday_monday);
            case Calendar.TUESDAY: return activity.getString(R.string.weekday_tuesday);
            case Calendar.WEDNESDAY: return activity.getString(R.string.weekday_wednesday);
            case Calendar.THURSDAY: return activity.getString(R.string.weekday_thursday);
            case Calendar.FRIDAY: return activity.getString(R.string.weekday_friday);
            case Calendar.SATURDAY: return activity.getString(R.string.weekday_saturday);
            default: return "";
        }
    }

    /**
     * 页面ViewHolder
     */
    class PageViewHolder extends RecyclerView.ViewHolder {
        GridLayout appGrid;
        boolean isFirstPage;

        PageViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            appGrid = itemView.findViewById(R.id.appGrid);
            isFirstPage = (viewType == 0);
        }

        void bind(List<AppInfo> apps, boolean isFirst) {
            appGrid.removeAllViews();

            // 如果是第一页，立即更新时间显示
            if (isFirst) {
                updateTimeForView(itemView);
            }

            // 根据应用数量设置网格布局
            int appCount = isFirst ? config.getFirstPageAppCount() : config.getNormalPageAppCount();
            int columnCount = calculateColumnCount(appCount);
            int rowCount = (int) Math.ceil((double) appCount / columnCount);

            appGrid.setColumnCount(columnCount);
            appGrid.setRowCount(rowCount);

            // 添加应用视图
            for (int i = 0; i < appCount; i++) {
                View appView = LayoutInflater.from(activity).inflate(R.layout.item_app, appGrid, false);
                ImageView iconView = appView.findViewById(R.id.appIcon);
                TextView nameView = appView.findViewById(R.id.appName);

                if (i < apps.size()) {
                    final AppInfo app = apps.get(i);
                    iconView.setImageDrawable(app.getIcon());
                    nameView.setText(app.getAppName());
                    appView.setOnClickListener(v -> activity.launchApp(app));
                } else {
                    // 空位不显示内容，不可点击
                    iconView.setImageResource(android.R.color.transparent);
                    nameView.setText("");  // 不显示文字
                    appView.setOnClickListener(null);  // 移除点击事件
                }

                // 设置 GridLayout.LayoutParams 确保居中
                // 使用 spec() 方法正确设置行列对齐
                int column = i % columnCount;
                int row = i / columnCount;
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                    GridLayout.spec(row, GridLayout.CENTER),
                    GridLayout.spec(column, GridLayout.CENTER)
                );
                params.width = GridLayout.LayoutParams.WRAP_CONTENT;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.setGravity(android.view.Gravity.CENTER);
                appView.setLayoutParams(params);

                appGrid.addView(appView);
            }
        }

        /**
         * 为指定视图更新时间
         */
        private void updateTimeForView(View pageView) {
            TextView clockText = pageView.findViewById(R.id.clockText);
            TextView ampmText = pageView.findViewById(R.id.ampmText);
            TextView dateText = pageView.findViewById(R.id.dateText);

            if (clockText != null && ampmText != null && dateText != null) {
                Calendar calendar = Calendar.getInstance();

                // 12小时制时间
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.CHINA);
                clockText.setText(timeFormat.format(calendar.getTime()));

                // 上午/下午
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                ampmText.setText(hour < 12 ? activity.getString(R.string.am) : activity.getString(R.string.pm));

                // 日期和星期
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                String weekday = getWeekdayString(dayOfWeek);
                String dateStr = String.format(Locale.CHINA, "%d年%d月%d日 %s", year, month, day, weekday);
                dateText.setText(dateStr);
            }
        }

        /**
         * 计算列数
         */
        private int calculateColumnCount(int appCount) {
            if (appCount <= 2) return 2;      // 1-2个：2列1行
            if (appCount <= 4) return 2;      // 3-4个：2列2行
            if (appCount <= 6) return 3;      // 5-6个：3列2行
            if (appCount <= 9) return 3;      // 7-9个：3列3行
            return 3;                          // 更多：3列多行
        }
    }
}
