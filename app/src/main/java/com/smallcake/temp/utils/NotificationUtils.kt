package com.smallcake.temp.utils

import android.app.AppOpsManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.smallcake.temp.MyApplication
import com.smallcake.temp.R
import java.lang.reflect.InvocationTargetException

/**
 * MyApplication --  cn.com.smallcake_utils
 * Created by Small Cake on  2017/9/7 17:20.
 * show notification
 * just a simple example
 * maybe you can write more each other notice
 * 新增8.0的兼容性
 */
object NotificationUtils {
    /**
     * 通知消息
     * @param msg
     * 注意：setSmallIcon无效，可能是图片太大（最好32*32），或缓存（重启手机）
     */
    fun showNotice(
        title: CharSequence = MyApplication.instance.getString(R.string.app_name),
        msg: CharSequence
    ) {
        val manager = MyApplication.instance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(AppUtils.getAppPackageName(), title, NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
            NotificationCompat.Builder(MyApplication.instance, "smallcake")
        } else {
            NotificationCompat.Builder(MyApplication.instance)
        }
        val largeBitmap = BitmapFactory.decodeResource(MyApplication.instance.resources,R.mipmap.ic_launcher_round)
        val notification = builder
            .setLargeIcon(largeBitmap)//大图，右侧的图标
            .setSmallIcon(R.mipmap.ic_launcher_round)//必须添加（Android 8.0,最好32*32像素大小
            .setChannelId(AppUtils.getAppPackageName())//必须添加（Android 8.0） 【唯一标识】
            .setContentTitle(title)
            .setWhen(System.currentTimeMillis())
            .setContentText(msg)
            .setAutoCancel(false)
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            .build()
        notification.flags = Notification.FLAG_AUTO_CANCEL
        manager.notify(0, notification)
    }

    /**
     * 显示进度通知消息
     * @param msg
     * @param progress
     */
    fun showNoticeProgress(msg: CharSequence?, progress: Int) {
        val manager =
            MyApplication.instance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder
        val title = MyApplication.instance.getString(R.string.app_name)
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("1", title, NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
            NotificationCompat.Builder(MyApplication.instance, "smallcake")
        } else {
            NotificationCompat.Builder(MyApplication.instance)
        }
        val notification = builder
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setWhen(System.currentTimeMillis())
            .setContentText(msg)
            .setAutoCancel(false)
            .setDefaults(Notification.DEFAULT_LIGHTS)
            .setProgress(100, progress, false)
            .setOngoing(true).build()
        notification.flags = Notification.FLAG_AUTO_CANCEL
        manager.notify(0, notification)
    }

    /**
     * 检测是否开启【通知权限】
     * 使用：
    if (com.smallcake.smallutils.PermissUtils.isNotificationsEnabled(this)){
    AlertDialog.Builder(this)
    .setTitle("提示")
    .setMessage("未开启通知权限,去开启？")
    .setNegativeButton("确定") { _, _ ->
        goNotificationsSetPage(this)
    }
    .setPositiveButton("取消",null)
    .show()
    }
     */
    fun isNotificationsEnabled(mContext: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= 24) {
            NotificationManagerCompat.from(mContext).areNotificationsEnabled()
        } else if (Build.VERSION.SDK_INT >= 19) {
            val appOps = mContext.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val appInfo = mContext.applicationInfo
            val pkg = mContext.applicationContext.packageName
            val uid = appInfo.uid
            try {
                val appOpsClass = Class.forName(AppOpsManager::class.java.name)
                val checkOpNoThrowMethod = appOpsClass.getMethod(
                    "checkOpNoThrow", Integer.TYPE,
                    Integer.TYPE, String::class.java
                )
                val opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION")
                val value = opPostNotificationValue[Int::class.java] as Int
                (checkOpNoThrowMethod.invoke(appOps, value, uid, pkg) as Int
                        == AppOpsManager.MODE_ALLOWED)
            } catch (e: ClassNotFoundException) {
                true
            } catch (e: NoSuchMethodException) {
                true
            } catch (e: NoSuchFieldException) {
                true
            } catch (e: InvocationTargetException) {
                true
            } catch (e: IllegalAccessException) {
                true
            } catch (e: RuntimeException) {
                true
            }
        } else {
            true
        }
    }

    /**
     * 跳转通知设置权限页面
     */
    fun goNotificationsSetPage(context: Context) {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            intent.putExtra(Notification.EXTRA_CHANNEL_ID, context.applicationInfo.uid)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            //这种方案适用于 API21——25
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo.uid)
        } else {
            //下面这种方案是直接跳转到当前应用的设置界面。
            //https://blog.csdn.net/ysy950803/article/details/71910806
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
        }
        context.startActivity(intent)
    }
}