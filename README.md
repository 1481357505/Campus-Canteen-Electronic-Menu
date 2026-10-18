# Campus Canteen Electronic Menu 校园食堂电子菜单 App

面向校园场景的食堂电子菜单 Android 应用，支持多食堂窗口级菜品浏览、实时人流监控、菜品评价系统，采用双角色（学生 / 管理员）设计，提升校园就餐体验。

## 技术栈

- **开发语言**: Kotlin
- **UI 框架**: Jetpack Compose + Material 3
- **架构模式**: MVVM + Repository 模式
- **本地数据库**: Room (SQLite)
- **页面导航**: Navigation Compose
- **异步处理**: Kotlin Coroutines
- **构建工具**: Gradle + Kotlin DSL

## 功能特性

### 双角色权限体系
- **学生端**: 多食堂浏览、窗口菜品查看、人流监控、菜品评价、搜索、收藏、个人中心
- **管理员端**: 菜品管理、窗口管理、人流状态更新、评价管理、低分预警、数据统计

### 核心功能模块
- 多食堂菜单浏览（食堂 → 窗口 → 菜品三级结构）
- 窗口级实时人流监控（拥挤度 + 预计等待时间）
- 菜品评价系统（1-5 星评分 + 文字评论 + 管理员回复）
- 多维度搜索（按菜品名称、分类、食材搜索）
- 收藏系统（菜品收藏 + 窗口收藏）
- 低分自动预警（≤2 星评价提醒管理员）
- 个人数据面板（评价历史、收藏列表）

### 系统特性
- 双角色登录与权限隔离
- Room 本地数据库离线支持
- 菜品-窗口精准映射关系
- 评价互动闭环（学生评价 → 管理员回复）
- MVVM 架构，代码清晰易维护

## 项目结构

```
Campus-Canteen-Electronic-Menu/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/eaten/
│   │   │   ├── MainActivity.kt          # 主入口
│   │   │   ├── data/                    # 数据层
│   │   │   │   ├── db/                  # Room 数据库
│   │   │   │   │   ├── dao/             # 数据访问对象
│   │   │   │   │   └── entity/          # 数据实体
│   │   │   │   └── repository/          # 仓库层
│   │   │   ├── ui/                      # 界面层
│   │   │   │   ├── theme/               # Compose 主题
│   │   │   │   ├── screen/              # 各页面
│   │   │   │   └── navigation/          # 导航配置
│   │   │   └── viewmodel/               # 视图模型层
│   │   ├── res/                         # 资源文件
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
│   ├── wrapper/
│   └── libs.versions.toml               # 版本目录
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## 快速开始

### 环境要求
- Android Studio Hedgehog 或更高版本
- Android SDK 24+ (Android 7.0)
- Kotlin 1.9+
- JDK 11+

### 构建运行

1. 克隆仓库
   ```bash
   git clone https://github.com/1481357505/Campus-Canteen-Electronic-Menu.git
   ```

2. 使用 Android Studio 打开项目

3. 等待 Gradle 同步完成

4. 连接设备或启动模拟器，点击运行

## 数据模型

- **User**: 用户表（学生 / 管理员，角色权限）
- **Canteen**: 食堂信息（营业时间、位置）
- **Window**: 窗口信息（所属食堂、编号）
- **Dish**: 菜品信息（价格、分类、图片、营养信息）
- **Review**: 评价记录（评分、评论、管理员回复）
- **WindowCrowdStatus**: 窗口人流状态（拥挤度、等待时间）
- **Favorite**: 收藏记录（收藏的菜品和窗口）

## License

本项目用于学习和教育目的。
