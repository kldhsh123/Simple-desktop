package com.Simple_desktop;

/**
 * 桌面配置数据模型
 */
public class DesktopConfig {
    private int firstPageAppCount;   // 首页应用数量，默认2
    private int normalPageAppCount;  // 普通页应用数量，默认4
    private int horizontalOffset;    // 普通页水平偏移量（像素），默认-40，负数向左，正数向右

    public DesktopConfig() {
        this.firstPageAppCount = 2;
        this.normalPageAppCount = 4;
        this.horizontalOffset = -40;  // 默认向左偏移40像素以修正偏右问题
    }

    public int getFirstPageAppCount() { return firstPageAppCount; }
    public void setFirstPageAppCount(int count) {
        // 限制范围1-6
        this.firstPageAppCount = Math.max(1, Math.min(6, count));
    }

    public int getNormalPageAppCount() { return normalPageAppCount; }
    public void setNormalPageAppCount(int count) {
        // 限制范围2-8
        this.normalPageAppCount = Math.max(2, Math.min(8, count));
    }

    public int getHorizontalOffset() { return horizontalOffset; }
    public void setHorizontalOffset(int offset) {
        // 限制范围-200到+200像素
        this.horizontalOffset = Math.max(-200, Math.min(200, offset));
    }
}
