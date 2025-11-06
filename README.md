# Android Voice Kiosk Demo

### A public demo of a freelance project for a smart interactive product, featuring Kiosk Mode, offline voice control, and system-level customization.

---

This project was developed as a freelance commission, turning a standard Android device into a dedicated, voice-controlled interactive terminal. The initial requirement was a simple voice control program, which later evolved, based on client requests, to include full Kiosk Mode and a custom boot animation.

This public demo showcases the core technical solutions, with all commercial-sensitive assets replaced.

## 🎥 Live Demo

*(Insert your GIF or short video here. This is the most compelling part of your `README`!)*

`![App Demo](link_to_your_demo.gif)`

## ✨ Core Features

*   **Offline Voice Control**: Integrated the **Vosk API** for real-time, on-device speech recognition to meet client's privacy and offline-first requirements.
*   **Kiosk Mode**: The application serves as the device's **dedicated home screen (Launcher)**. It automatically starts on boot and locks the device into the app, preventing user access to the underlying Android system.
*   **Dynamic Content Switching**: Listens for specific voice commands and plays corresponding videos, delivering a responsive, voice-driven user experience.
*   **System-Level Customization (Root)**: Implemented a feature to **replace the native Android boot animation** with the client's branding. This was achieved by programmatically executing root commands, inspired by [this technical blog post](https://blog.csdn.net/godiors_163/article/details/72529210).
*   **Full-Screen Immersive UI**: Hides system bars and navigation for a seamless, branded, full-screen experience.

## 🛠️ Tech Stack

*   **Language**: Kotlin
*   **Platform**: Android
*   **Core API**: Vosk Android (Offline Speech Recognition)
*   **Build Tool**: Gradle

## 🚀 How To Run

1.  Clone the repository: `git clone https://github.com/Dapper-YF/Android-Voice-Kiosk-Demo.git`
2.  Open the project in Android Studio.
3.  Place a custom `bootanimation.zip` in the `app/src/main/res/raw` directory to test the replacement feature.
4.  **Note**: The boot animation replacement feature requires the target device to have **Root access**.
5.  Build and run the app.
6.  To set the app as the default launcher, go to `Settings > Apps > Default apps > Home app` and select "Voice Kiosk Demo".
7.  To trigger the boot animation replacement, tap the video view and confirm the dialog, then reboot the device.
8.  ---

## / 中文说明

你好！我是一名大二的软件工程学生 Dapper-YF。

这个项目是我在大学期间承接的一个真实**智能交互产品的外包项目**。它完整地记录了我如何将一个客户的商业需求，通过技术调研和编码实践，最终落地为一个成熟产品的过程。

### 项目背景与演进

项目初始，甲方的需求只是做一个简单的离线语音控制程序。

随着项目的推进，为了打造一个完整的、沉浸式的产品体验，甲方陆续提出了更深入的定制化需求：
1.  **实现Kiosk模式**：要求设备开机后直接进入我们的APP，并且用户不能退出到原生的安卓桌面。
2.  **替换开机动画**：要求将系统原生的“Android”开机动画，替换成客户自己的产品LOGO动画，实现完全的品牌化。

这个从“应用开发”到“系统级定制”的演进过程，对我来说是巨大的挑战，也是宝贵的成长经历。

### 核心功能与技术实现细节

*   **离线语音识别 (Vosk)**：根据甲方对隐私和无网络环境使用的要求，我调研并最终采用了Vosk离线识别框架。在集成过程中，我解决了模型加载、多线程识别以及权限处理等一系列问题。

*   **“Kiosk”桌面模式**：为了实现“霸占”屏幕的功能，我深入研究了 `AndroidManifest.xml` 中 `intent-filter` 的配置，通过将 `MainActivity` 声明为 `CATEGORY_HOME` 类型，成功地将其注册为系统桌面。这个过程也让我对Android的Activity启动模式有了更深的理解。

*   **系统开机动画替换 (Root)**：这是项目中最具挑战性的部分。要在APP内实现对系统文件的修改，必须获取Root权限。我通过阅读和实践CSDN上的这篇博客：[https://blog.csdn.net/godiors_163/article/details/72529210](https://blog.csdn.net/godiors_163/article/details/72529210)，学习并掌握了如何通过 `su` 命令获取Root权限，动态挂载 `/system` 分区为可读写状态，并最终用代码成功替换了 `bootanimation.zip` 文件，实现了客户的品牌化需求。

### 项目收获

通过这个真实的外包项目，我最大的收获是：
1.  **商业需求转化能力**：学会了如何倾听客户需求，并将其转化为具体、可行的技术解决方案。
2.  **深度问题解决能力**：面对从未知API的集成到系统底层的修改，我锻炼了独立调研、动手实践并最终解决问题的能力。
3.  **对代码价值的认知**：我亲身体会到，技术不仅仅是代码，更是为客户创造价值、解决实际问题的工具。

我将这个项目的核心技术剥离出来，制作成这个公开的Demo，一方面是为了记录我的成长，另一方面也希望能与更多人交流技术。

**感谢您的阅读！**
