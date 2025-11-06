package com.example.voskcontrol

import java.io.DataOutputStream
import java.io.IOException

object RootCmd {
    /**
     * 在拥有Root权限的设备上执行一条或多条shell命令
     * @param commands 要执行的命令列表
     * @return 如果命令成功执行（退出码为0），返回true
     */
    fun exec(commands: List<String>): Boolean {
        var process: Process? = null
        var os: DataOutputStream? = null
        try {
            // 请求su权限
            process = Runtime.getRuntime().exec("su")
            os = DataOutputStream(process.outputStream)

            // 依次执行所有命令
            for (command in commands) {
                os.writeBytes("$command\n")
            }

            // 执行完毕后退出su
            os.writeBytes("exit\n")
            os.flush()

            // 等待进程结束，并获取退出码
            val exitValue = process.waitFor()
            return exitValue == 0
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            try {
                os?.close()
                process?.destroy()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}