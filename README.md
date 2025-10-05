# 简易桌面

一款专为老年人设计的 Android 桌面启动器应用，具有超大字体、简洁界面和易用的操作方式。

## 项目特点

- **超大字体显示**：所有文字和图标都经过放大优化，方便老年人查看
- **简洁界面**：去除复杂功能，只保留最核心的应用启动功能
- **大时钟显示**：首页显示超大 12 小时制时钟、日期和星期
- **灵活布局**：首页 1-6 个应用，其他页面 2-9 个应用
- **自定义壁纸**：支持设置应用内壁纸（不影响系统壁纸）
- **触觉反馈**：设置按钮带震动反馈，提升操作体验

## 功能说明

### 主界面
- **时间显示**：首页顶部显示超大时钟（12小时制）、上午/下午标识、日期和星期
- **应用图标**：点击图标启动对应应用
- **页面导航**：左右滑动切换多个页面
- **设置按钮**：右上角齿轮图标（仅在首页显示），点击进入设置

### 设置界面
- **添加应用**：选择要在桌面显示的应用
- **编辑应用**：修改已添加的应用
- **更换壁纸**：从相册选择图片作为桌面壁纸（仅影响本应用）
- **恢复默认壁纸**：一键恢复默认背景色

## 技术架构

- **开发语言**：Java
- **最低 Android 版本**：Android 5.0 (API 21)
- **目标 Android 版本**：Android 14 (API 34)
- **核心组件**：
  - ViewPager2：多页面滑动
  - RecyclerView：应用图标网格布局
  - SharedPreferences + Gson：数据持久化
  - 内部存储：壁纸文件管理

## 编译指南

### 前置要求
- Android Studio（推荐最新稳定版）
- JDK 11 或更高版本
- Android SDK（API 34）
- Gradle（项目已包含 Gradle Wrapper）

### 编译步骤

#### 方法一：使用 Android Studio（推荐）
1. 打开 Android Studio
2. 选择 `File` → `Open`
3. 选择项目目录
4. 等待 Gradle 同步完成
5. 选择构建变体：
   - `Build` → `Select Build Variant`
   - 选择 `arm7Debug` 或 `arm8Debug`（根据目标设备）
6. 点击 `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
7. 编译完成后会显示 APK 文件位置链接

#### 方法二：使用命令行（Windows）
```bash
# 进入项目目录
cd "J:/vsrepos/Simple desktop/Simple desktop"

# 编译 ARM7 Debug 版本（适用于 32 位设备，如 Redmi 9C）
./gradlew.bat assembleArm7Debug

# 编译 ARM8 Debug 版本（适用于 64 位设备）
./gradlew.bat assembleArm8Debug

# 编译 ARM7 Release 版本（已签名，可直接安装）
./gradlew.bat assembleArm7Release

# 编译 ARM8 Release 版本（已签名，可直接安装）
./gradlew.bat assembleArm8Release

# 编译所有 Debug 版本
./gradlew.bat assembleDebug

# 编译所有 Release 版本
./gradlew.bat assembleRelease
```

### 编译输出位置

编译完成后，APK 文件位于以下目录：

```
项目根目录/app/build/outputs/apk/
├── arm7/
│   ├── debug/
│   │   └── app-arm7-debug.apk          # ARM7 Debug 版本
│   └── release/
│       └── app-arm7-release.apk        # ARM7 Release 版本
└── arm8/
    ├── debug/
    │   └── app-arm8-debug.apk          # ARM8 Debug 版本
    └── release/
        └── app-arm8-release.apk        # ARM8 Release 版本
```

**完整路径示例**（Windows）：
```
J:\vsrepos\Simple desktop\Simple desktop\app\build\outputs\apk\arm7\debug\app-arm7-debug.apk
```

### 安装到设备

```bash
# 使用 ADB 安装（-r 参数表示覆盖安装）
adb install -r "路径/app-arm7-debug.apk"

# 如果连接多个设备，需指定设备
adb -s 设备序列号 install -r "路径/app-arm7-debug.apk"
```

### 如何选择 ARM 版本

- **ARM7 (32位)**：适用于较旧的 Android 设备（如 Redmi 9C、Redmi Note 4X 等）
- **ARM8 (64位)**：适用于较新的 Android 设备（2019 年后的大部分手机）

**查看设备架构**：
```bash
adb shell getprop ro.product.cpu.abi
```

输出示例：
- `armeabi-v7a` → 使用 ARM7 版本
- `arm64-v8a` → 使用 ARM8 版本

## APK 签名说明

### 🔓 开源项目签名说明

**本项目为开源学习项目，签名密钥已包含在代码仓库中。**

- **密钥库文件**：`simple-desktop.keystore`
- **别名**：`simple-desktop`
- **密码**：`123456`
- **有效期**：10,000 天
- **SHA-1 指纹**：`99:39:A7:0B:3E:53:62:E0:24:A1:20:D2:BA:47:C6:C7:BC:33:00:91`

⚠️ **重要说明**：
- ✅ 此密钥仅供开源项目使用，方便任何人克隆后直接编译
- ✅ 任何人都可以使用此密钥编译和签名 APK

### 签名验证
查看 APK 签名信息：
```bash
# 查看签名报告
./gradlew.bat :app:signingReport

# 使用 keytool 查看密钥库
keytool -list -v -keystore simple-desktop.keystore -storepass 123456
```

### 重新生成签名密钥（可选）
如果需要重新生成签名密钥（例如用于正式发布）：
```bash
# 使用 JDK 的 keytool 生成新密钥
keytool -genkeypair -v \
  -keystore my-release-key.keystore \
  -alias my-key-alias \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass 你的密码 \
  -keypass 你的密码 \
  -dname "CN=你的名字, OU=组织单位, O=组织, L=城市, ST=省份, C=国家代码"
```

然后修改 `app/build.gradle` 中的签名配置：
```gradle
signingConfigs {
    release {
        storeFile file('../my-release-key.keystore')
        storePassword '你的密码'
        keyAlias 'my-key-alias'
        keyPassword '你的密码'
    }
}
```

**重要提示**：
- ⚠️ 请妥善保管密钥库文件和密码
- ⚠️ 密钥库丢失将无法更新已发布的应用
- ⚠️ 正式发布时请使用强密码
- ⚠️ 不要将密钥库文件上传到公开代码仓库

## 权限说明

应用需要以下权限：

- `QUERY_ALL_PACKAGES`：查询设备上所有已安装应用
- `VIBRATE`：设置按钮震动反馈
- `READ_EXTERNAL_STORAGE`：读取相册图片设置壁纸

## 开发环境

- **操作系统**：Windows 10.0.26100
- **Shell**：Git Bash
- **文件系统**：不区分大小写，CRLF 换行符

## 项目结构

```
Simple desktop/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/simpledesktop/
│   │       │   ├── MainActivity.java           # 主界面
│   │       │   ├── SettingsActivity.java       # 设置界面
│   │       │   ├── DesktopPagerAdapter.java    # 页面适配器
│   │       │   ├── AppInfo.java                # 应用信息模型
│   │       │   └── AppListAdapter.java         # 应用列表适配器
│   │       ├── res/
│   │       │   ├── layout/                     # 布局文件
│   │       │   ├── drawable/                   # 图标和背景
│   │       │   ├── values/                     # 字符串、颜色、尺寸
│   │       │   └── xml/                        # 备份规则
│   │       └── AndroidManifest.xml
│   └── build.gradle                            # 应用级构建配置
├── build.gradle                                # 项目级构建配置
├── settings.gradle                             # Gradle 设置
└── README.md                                   # 本文件
```

## 常见问题

### 1. 编译失败：ClassNotFoundException
**解决方案**：检查 `AndroidManifest.xml` 中 Activity 的 `android:name` 属性是否正确。

### 2. 设备连接后无法识别
**解决方案**：
```bash
# 检查设备连接
adb devices

# 如果显示 unauthorized，检查手机上的 USB 调试授权提示
# 如果未显示设备，重新插拔 USB 或重启 ADB
adb kill-server
adb start-server
```

### 3. 应用无法设为默认桌面
**解决方案**：进入手机 `设置` → `应用` → `默认应用` → `桌面`，选择"简易老人桌面"。

## 更新日志

### v1.0 (当前版本)
- ✅ 首页超大时钟和日期显示
- ✅ 多页面应用布局（首页 1-6 个，其他页 2-9 个）
- ✅ 应用添加、编辑和删除
- ✅ 自定义壁纸（应用内部）
- ✅ 恢复默认壁纸
- ✅ 右上角设置按钮（仅首页显示）
- ✅ 震动反馈
- ✅ 时间即时加载优化
