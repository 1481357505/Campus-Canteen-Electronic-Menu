# 🏫 Campus Canteen Electronic Menu 校园食堂电子菜单 App

> 一款面向校园场景的食堂电子菜单 Android 应用，支持多食堂窗口级菜品浏览、实时人流监控、菜品评价系统，采用双角色（学生 / 管理员）设计，全面提升校园就餐体验。

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-green.svg?style=flat)](https://developer.android.com/jetpack/compose)
[![Room](https://img.shields.io/badge/Room-Database-orange.svg?style=flat)](https://developer.android.com/training/data-storage/room)
[![MVVM](https://img.shields.io/badge/Architecture-MVVM-purple.svg?style=flat)]()
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24%20(Android%207.0)-red.svg?style=flat)]()

---

## ✨ 功能特性

### 👨‍🎓 学生端
- **多食堂菜单浏览**：查看不同食堂的所有窗口和菜品信息
- **窗口级精准映射**：每道菜品关联具体售卖窗口，方便快速定位
- **实时人流监控**：查看各窗口拥挤度和预计等待时间，错峰就餐
- **菜品评价系统**：1-5 星评分 + 文字评论，支持查看他人评价
- **多维度搜索**：按菜品名称、分类、食材进行搜索
- **收藏系统**：收藏喜欢的菜品和窗口，快速访问
- **个人中心**：查看评价历史、收藏列表、个人信息

### 👨‍💼 管理员端
- **菜品管理**：菜品的增删改查，支持详细信息（价格、分类、营养信息等）
- **窗口管理**：维护窗口信息和营业状态
- **人流状态更新**：实时更新窗口拥挤度和等待时间
- **评价管理**：查看并回复学生评价，形成反馈闭环
- **低分预警**：低分菜品（≤2 星）自动提醒管理员关注
- **数据统计**：食堂运营数据和学生反馈概览

---

## 🛠 技术栈

| 类别 | 技术 |
|------|------|
| **开发语言** | Kotlin |
| **UI 框架** | Jetpack Compose + Material 3 |
| **架构模式** | MVVM + Repository 模式 |
| **本地数据库** | Room (SQLite) |
| **页面导航** | Navigation Compose |
| **异步处理** | Kotlin Coroutines |
| **依赖注入** | Hilt (预留) |
| **后台任务** | WorkManager |
| **图片加载** | Coil |
| **构建工具** | Gradle + Kotlin DSL |

---

## 🏗 架构设计

### 整体架构
采用 **MVVM (Model-View-ViewModel)** 架构模式，结合 **Repository 模式** 统一数据访问层：

```
┌──────────────────────────────────────────┐
│              View (Compose UI)            │  ← 页面展示与用户交互
├──────────────────────────────────────────┤
│               ViewModel                   │  ← UI 状态管理与业务逻辑
├──────────────────────────────────────────┤
│              Repository                   │  ← 统一数据访问入口
├──────────────────────────────────────────┤
│         Room Database / DataSource        │  ← 本地数据持久化
└──────────────────────────────────────────┘
```

### 数据库设计
使用 Room 本地数据库，设计 7 个核心实体：

| 实体 | 说明 |
|------|------|
| **User** | 用户表（学生/管理员，角色权限区分） |
| **Canteen** | 食堂信息（营业时间、位置等） |
| **Window** | 窗口信息（所属食堂、编号等） |
| **Dish** | 菜品信息（价格、分类、图片、营养信息） |
| **Review** | 评价记录（评分、评论、管理员回复） |
| **WindowCrowdStatus** | 窗口人流状态（拥挤度、等待时间） |
| **Favorite** | 收藏记录（学生收藏的菜品和窗口） |

### 亮点设计
1. **窗口级人流监控**：区别于普通食堂 App 仅提供食堂级人流数据，本应用实现窗口粒度的实时监控
2. **双角色权限系统**：学生和管理员使用同一应用，登录后自动切换对应界面和功能
3. **菜品-窗口精确映射**：每道菜品关联具体窗口，解决"不知道在哪买"的痛点
4. **评价互动闭环**：学生评价 → 管理员回复 → 低分预警，形成完整的反馈机制

---

## 📦 项目结构

```
Campus-Canteen-Electronic-Menu/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/eaten/
│   │   │   ├── MainActivity.kt          # 主入口
│   │   │   ├── data/                    # 数据层 (Model)
│   │   │   │   ├── db/                  # Room 数据库
│   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   ├── dao/             # 数据访问对象
│   │   │   │   │   └── entity/          # 数据实体
│   │   │   │   └── repository/          # 仓库层
│   │   │   ├── ui/                      # 界面层 (View)
│   │   │   │   ├── theme/               # Compose 主题
│   │   │   │   ├── screen/              # 各页面
│   │   │   │   └── navigation/          # 导航配置
│   │   │   └── viewmodel/               # 视图模型层 (ViewModel)
│   │   ├── res/                         # 资源文件
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
│   ├── wrapper/
│   └── libs.versions.toml               # 版本目录
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

---

## 🚀 快速开始

### 环境要求
- Android Studio Hedgehog 或更高版本
- Android SDK 24+ (Android 7.0)
- Kotlin 1.9+
- JDK 11+

### 构建运行

1. **克隆仓库**
   ```bash
   git clone https://github.com/1481357505/Campus-Canteen-Electronic-Menu.git
   ```

2. **打开项目**
   - 启动 Android Studio
   - 选择 "Open an existing project"
   - 选择项目根目录

3. **同步 Gradle**
   - 等待 Android Studio 自动同步 Gradle 依赖
   - 如未自动同步，点击 "Sync Project with Gradle Files"

4. **运行应用**
   - 连接 Android 设备或启动模拟器
   - 点击运行按钮 (▶) 或使用快捷键 `Shift + F10`

---

## 📱 主要页面

| 角色 | 页面 | 功能 |
|------|------|------|
| 通用 | 登录/注册页 | 双角色注册登录 |
| 学生 | 食堂列表页 | 查看所有食堂及概览信息 |
| 学生 | 食堂详情页 | 窗口列表 + 人流状态 |
| 学生 | 菜品列表页 | 浏览菜品、搜索、筛选 |
| 学生 | 菜品详情页 | 菜品信息 + 评价列表 + 发表评价 |
| 学生 | 收藏页 | 收藏的菜品和窗口 |
| 学生 | 个人中心 | 评价历史、个人信息 |
| 管理员 | 管理仪表盘 | 数据统计概览 |
| 管理员 | 菜品管理 | 菜品增删改查 |
| 管理员 | 窗口管理 | 窗口信息与人流状态维护 |
| 管理员 | 评价管理 | 查看评价、回复评价 |

---

## 🔮 未来规划

- [ ] **推送通知**：菜单更新、优惠活动实时推送
- [ ] **营养分析**：详细营养信息展示和 dietary 筛选
- [ ] **社交功能**：分享评价和就餐体验
- [ ] **高级数据分析**：管理员端详细数据可视化仪表盘
- [ ] **校园卡集成**：对接校园卡支付系统
- [ ] **后端服务**：从纯本地升级为前后端分离架构
- [ ] **暗色模式**：支持深色主题切换

---

## 📄 License

本项目为学习和教育目的开发，仅供校园餐饮管理场景学习研究使用。

---

<div align="center">
  <p>Made with ❤️ by Erenin</p>
  <p>Beijing University of Technology · Software Engineering</p>
</div>
