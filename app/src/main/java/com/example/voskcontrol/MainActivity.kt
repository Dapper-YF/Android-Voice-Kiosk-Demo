package com.example.voskcontrol

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.voskcontrol.databinding.ActivityMainBinding
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.StorageService
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class MainActivity : AppCompatActivity(), RecognitionListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var videoView: VideoView

    private var model: Model? = null
    private var speechService: SpeechService? = null
    private var isListening = false

    // 关键词与视频映射
    private val commandMap = mapOf(
        R.raw.v0 to Pair(listOf("翘屁屁笑一个"), listOf("翘屁屁", "笑一个", "俏皮皮", "笑一笑")),
        R.raw.v1 to Pair(listOf("转个身笑一个"), listOf("转身", "转个身")),
        R.raw.v2 to Pair(listOf("跳个蒙古舞"), listOf("蒙古舞", "跳个舞")),
        R.raw.v3 to Pair(listOf("抱一抱"), listOf("抱抱")),
        R.raw.v4 to Pair(listOf("装可怜"), listOf("可怜")),
        R.raw.v5 to Pair(listOf("不开心"), listOf("不高兴", "生气"))
    )

    companion object {
        private const val TAG = "VoskControl"
        private const val PERMISSIONS_REQUEST_RECORD_AUDIO = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoView = binding.videoView

        // --- 核心修改：为VideoView添加点击事件，作为替换开机动画的隐藏触发器 ---
        videoView.setOnClickListener {
            // 使用对话框让用户确认，防止误触
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("开发者选项")
                .setMessage("您要将系统开机动画替换为产品动画吗？\n(此操作需要Root权限，且仅用于测试设备)")
                .setPositiveButton("确认替换") { _, _ ->
                    replaceBootAnimation()
                }
                .setNegativeButton("取消", null)
                .show()
        }

        checkAndRequestPermissions()
        Log.d(TAG, "Activity已创建")
    }

    // --- 新增方法：替换开机动画的核心逻辑 ---
    private fun replaceBootAnimation() {
        Toast.makeText(this, "正在执行替换操作...", Toast.LENGTH_SHORT).show()
        // Root操作是耗时操作，必须在子线程中执行
        Thread {
            // 1. 将我们打包在res/raw中的bootanimation.zip复制到应用的内部存储，方便后续操作
            val internalFile = File(filesDir, "bootanimation.zip")
            try {
                val inputStream: InputStream = resources.openRawResource(R.raw.bootanimation)
                val outputStream = FileOutputStream(internalFile)
                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()
                Log.d(TAG, "动画文件已成功复制到: ${internalFile.absolutePath}")
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread { Toast.makeText(this, "错误：无法从res/raw读取动画文件", Toast.LENGTH_LONG).show() }
                return@Thread
            }

            // 2. 定义需要执行的Root命令
            val commands = listOf(
                "mount -o remount,rw /system", // 重新挂载/system分区为可读写
                "cp ${internalFile.absolutePath} /system/media/bootanimation.zip", // 复制文件
                "chmod 644 /system/media/bootanimation.zip", // 修正文件权限
                "mount -o remount,ro /system" // 操作完成后，恢复/system分区为只读，保证安全
            )

            // 3. 执行命令
            Log.d(TAG, "准备执行Root命令: $commands")
            val success = RootCmd.exec(commands)
            Log.d(TAG, "Root命令执行结果: $success")

            // 4. 在UI线程上显示结果
            runOnUiThread {
                if (success) {
                    Toast.makeText(this, "开机动画替换成功！请重启设备查看效果。", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "替换失败！请确认设备已Root并已授予权限。", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    // --- 以下是您原有的代码，保持不变 ---

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && model != null && !isListening) {
            startRecognition()
        }
        if (!videoView.isPlaying) {
            val currentVideoTag = videoView.tag as? Int ?: R.raw.v0
            playVideo(currentVideoTag)
        }
    }

    override fun onPause() {
        super.onPause()
        stopRecognition()
        if (videoView.isPlaying) {
            videoView.stopPlayback()
        }
    }

    private fun checkAndRequestPermissions() {
        val permission = Manifest.permission.RECORD_AUDIO
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSIONS_REQUEST_RECORD_AUDIO)
        } else {
            initVoskModel()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initVoskModel()
            } else {
                Toast.makeText(this, "需要录音权限才能使用此应用", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun initVoskModel() {
        if (model != null) {
            startDefaultBehavior()
            return
        }
        StorageService.unpack(this, "model-zh-cn", "model",
            { model ->
                this.model = model
                runOnUiThread {
                    Log.d(TAG, "模型加载成功！")
                    startDefaultBehavior()
                }
            },
            { exception ->
                runOnUiThread {
                    Log.e(TAG, "模型加载失败", exception)
                    Toast.makeText(this, "模型加载失败，请重启应用", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun startDefaultBehavior() {
        startRecognition()
        playVideo(R.raw.v0)
    }

    private fun startRecognition() {
        if (isListening) return
        if (model == null) {
            Log.e(TAG, "无法开始识别，模型为null")
            return
        }
        try {
            val recognizer = Recognizer(model, 16000.0f)
            speechService = SpeechService(recognizer, 16000.0f)
            speechService?.startListening(this)
            isListening = true
            Log.d(TAG, ">>> 已开始聆听...")
        } catch (e: IOException) {
            Log.e(TAG, "启动监听失败", e)
        }
    }

    private fun stopRecognition() {
        if (!isListening) return
        speechService?.stop()
        isListening = false
        Log.d(TAG, "<<< 已停止聆听")
    }

    override fun onPartialResult(hypothesis: String) {
        if (!isListening) return

        try {
            val json = JSONObject(hypothesis)
            val partialText = json.getString("partial").replace(" ", "")
            if (partialText.isBlank()) return

            Log.d(TAG, "听到: $partialText")

            for ((resId, keywords) in commandMap) {
                for (keyword in keywords.first) {
                    if (partialText.contains(keyword)) {
                        handleCommand(keyword, resId)
                        return
                    }
                }
            }
            for ((resId, keywords) in commandMap) {
                for (keyword in keywords.second) {
                    if (partialText.contains(keyword)) {
                        handleCommand(keyword, resId)
                        return
                    }
                }
            }
        } catch (e: Exception) { /* 忽略JSON解析错误 */ }
    }

    private fun handleCommand(command: String, videoResId: Int) {
        Log.i(TAG, "检测到指令: '$command', 准备播放视频...")
        playVideo(videoResId)
    }

    private fun playVideo(videoResId: Int) {
        try {
            val currentVideoTag = videoView.tag as? Int
            if (videoView.isPlaying && currentVideoTag == videoResId) {
                return
            }

            Log.d(TAG, "切换视频播放...")
            videoView.stopPlayback()
            videoView.tag = videoResId

            val uri = Uri.parse("android.resource://$packageName/$videoResId")
            videoView.setVideoURI(uri)

            videoView.setOnPreparedListener { mp ->
                mp.isLooping = true
                videoView.start()
                Log.d(TAG, "视频开始播放 (循环)...")
            }

        } catch (e: Exception) {
            Log.e(TAG, "播放视频时出错", e)
        }
    }

    override fun onResult(hypothesis: String) { /* 不使用 */ }
    override fun onFinalResult(hypothesis: String) { /* 不使用 */ }

    override fun onTimeout() {
        if (isListening) {
            isListening = false
            Log.w(TAG, "聆听超时，正在重启...")
            startRecognition()
        }
    }

    override fun onError(exception: Exception) {
        Log.e(TAG, "识别出错", exception)
        if (isListening) {
            isListening = false
            Handler(Looper.getMainLooper()).postDelayed({ startRecognition() }, 1000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechService?.stop()
        speechService?.shutdown()
        Log.d(TAG, "Activity已销毁")
    }
}