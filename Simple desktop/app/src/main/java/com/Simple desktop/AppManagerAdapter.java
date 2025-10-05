package com.Simple_desktop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * 应用管理列表适配器
 */
public class AppManagerAdapter extends RecyclerView.Adapter<AppManagerAdapter.ViewHolder> {
    private Context context;
    private List<AppInfo> allApps;
    private List<AppInfo> desktopApps;
    private OnAppActionListener listener;

    public interface OnAppActionListener {
        void onAction(AppInfo app, boolean isAdd);
    }

    public AppManagerAdapter(Context context, List<AppInfo> allApps,
                           List<AppInfo> desktopApps, OnAppActionListener listener) {
        this.context = context;
        this.allApps = allApps;
        this.desktopApps = desktopApps;
        this.listener = listener;
    }

    /**
     * 更新桌面应用列表
     */
    public void updateDesktopApps(List<AppInfo> desktopApps) {
        this.desktopApps = desktopApps;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app_manager, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppInfo app = allApps.get(position);
        holder.bind(app);
    }

    @Override
    public int getItemCount() {
        return allApps.size();
    }

    /**
     * 检查应用是否已在桌面
     */
    private boolean isOnDesktop(AppInfo app) {
        for (AppInfo desktopApp : desktopApps) {
            if (desktopApp.getPackageName().equals(app.getPackageName()) &&
                desktopApp.getActivityName().equals(app.getActivityName())) {
                return true;
            }
        }
        return false;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        Button btnAction;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
            btnAction = itemView.findViewById(R.id.btnAction);
        }

        void bind(AppInfo app) {
            appIcon.setImageDrawable(app.getIcon());
            appName.setText(app.getAppName());

            boolean onDesktop = isOnDesktop(app);
            if (onDesktop) {
                btnAction.setText(R.string.remove_from_desktop);
                btnAction.setOnClickListener(v -> listener.onAction(app, false));
            } else {
                btnAction.setText(R.string.add_to_desktop);
                btnAction.setOnClickListener(v -> listener.onAction(app, true));
            }
        }
    }
}
