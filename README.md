# Android Voice Kiosk Demo

### A showcase project demonstrating an Android Kiosk-mode app with offline voice control (Vosk) and root-based boot animation replacement.

---

This project was developed to simulate a real-world commercial requirement, transforming a standard Android device into a dedicated, voice-controlled interactive terminal. It showcases key techniques in system-level customization and third-party API integration.

## 🎥 Live Demo

*(Here you should insert a GIF or a short video. This is the most important part! You can record your screen showing the whole process: device reboots -> custom boot animation plays -> app launches -> you say a command -> video changes. You can upload the GIF to the repo and link it here.)*

`![App Demo](link_to_your_demo.gif)`

## ✨ Core Features

*   **Offline Voice Control**: Integrated the **Vosk API** for real-time, on-device speech recognition without relying on cloud services.
*   **Kiosk Mode**: The application acts as the device's **home screen (Launcher)**. It automatically starts on boot and prevents users from exiting to the underlying Android system.
*   **Dynamic Content Switching**: Listens for specific voice commands and plays corresponding videos, demonstrating a responsive voice-driven UI.
*   **System-Level Customization (Root)**: Implemented a developer feature to **replace the native Android boot animation** with a custom one by executing root commands from within the app.
*   **Full-Screen Immersive UI**: Hides system bars and navigation buttons for a seamless, full-screen experience.

## 🛠️ Tech Stack

*   **Language**: Kotlin
*   **Platform**: Android
*   **Core API**: Vosk Android (Offline Speech Recognition)
*   **Build Tool**: Gradle

## 🚀 How To Run

1.  Clone the repository: `git clone https://github.com/Dapper-YF/Android-Voice-Kiosk-Demo.git`
2.  Open the project in Android Studio.
3.  Place your custom `bootanimation.zip` in the `app/src/main/res/raw` directory.
4.  (Optional) For the boot animation replacement feature to work, the target device must have **Root access**.
5.  Build and run the app.
6.  To set the app as the default launcher, go to `Settings > Apps > Default apps > Home app` and select "Voice Kiosk Demo".
7.  To trigger the boot animation replacement, tap the video view and confirm the dialog. Then, reboot the device.

---

*This project was created for educational and portfolio purposes, based on a simulated commercial use case.*
---

## / 中文说明

你好！我是一名正在学习软件工程的大二学生 Dapper-YF。这个项目是我为了模拟一个真实的商业需求，并探索Android系统级定制而独立开发的。

这个项目不仅仅是代码的堆砌，更是我一次完整的学习和解决问题过程的记录。

### ✨ 项目背景与我的思考

最初，我的目标只是做一个简单的语音控制APP。但在和“甲方”（我为自己设定的虚拟客户）的沟通中，需求不断升级：从简单的语音识别，到需要实现“Kiosk模式”让APP成为设备桌面，再到最终极的、需要Root权限才能实现的“替换系统开机动画”。

这个过程让我深刻体会到，一个真正的产品开发，是如何将商业需求转化为具体技术解决方案的。

### 核心功能与技术实现细节

*   **离线语音识别 (Vosk)**：我调研了多个语音识别方案，最终选择了Vosk，因为它能够完全在设备端离线运行，满足了项目的特定需求。我在这里遇到了模型加载、权限申请等问题，通过……方法解决了。
*   **“Kiosk”桌面模式**：为了实现开机直接进入APP，我深入研究了`AndroidManifest.xml`中`intent-filter`的配置，特别是`CATEGORY_HOME`的作用。我发现，在小米等定制系统上，还需要关闭“MIUI优化”或使用ADB命令才能成功设置，这让我对安卓系统的权限管理有了更深的理解。
*   **系统开机动画替换 (Root)**：这是项目中最具挑战性的部分。我从CSDN的一篇博客得到启发，学习了如何通过`su`命令获取Root权限，并挂载`/system`分区为可读写状态，最终成功用代码替换了`bootanimation.zip`。这个过程让我第一次接触到了安卓的底层文件系统和Linux命令。

### 最大的收获与成长

通过这个项目，我最大的收获不仅仅是写了多少行代码，而是：
1.  **完整的项目思维**：学会了如何将一个模糊的想法，拆解成一个个具体的技术任务。
2.  **主动解决问题的能力**：从GitHub报错`unrelated histories`到安卓的各种运行时报错，我学会了如何通过搜索引擎和技术文档，定位并解决未知问题。
3.  **对个人作品的打磨意识**：我明白了保护商业代码的重要性，并学会了如何制作一个“脱敏”的公开Demo来展示自己的技术。

我将这个项目记录在GitHub，并计划撰写一系列博客文章来复盘整个过程，希望能与更多人交流，也希望它能成为我未来求职道路上一块有分量的基石。

**感谢您的阅读！**
