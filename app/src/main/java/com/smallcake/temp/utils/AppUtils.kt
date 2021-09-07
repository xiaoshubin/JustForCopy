@file:Suppress("DEPRECATION")

package com.smallcake.temp.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.lxj.xpopup.util.XPermission
import com.smallcake.smallutils.FileUtils
import com.smallcake.smallutils.FormatUtils
import com.smallcake.smallutils.SmallUtils
import com.smallcake.temp.MyApplication
import java.io.File
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * 如果获取打包时间到页面中

    1.首先在build.gradle文件的android外面定义获取时间函数
        static def getBuildTime() {
            String now = new Date().format("yyyy_MM_dd HH:mm:ss")
            return "\"${now}\""
        }

    2.在defaultConfig中定义变量
        buildConfigField "String", "buildTime", getBuildTime()

    3.使用
        BuildConfig.buildTime

 */
object AppUtils {
    //获取应用包名
     fun getAppPackageName(): String = MyApplication.instance.packageName

    //获取版本号
    fun getVersionCode(): Int {
        var versioncode = 0
        try {
            val pm: PackageManager = MyApplication.instance.packageManager
            val pi: PackageInfo = pm.getPackageInfo(getAppPackageName(), 0)
            versioncode = pi.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versioncode
    }

    //获取版本名称
    fun getVersionName(): String {
        var versionName = ""
        try {
            val pm: PackageManager = MyApplication.instance.packageManager
            val pi: PackageInfo = pm.getPackageInfo(getAppPackageName(), 0)
            versionName = pi.versionName
            if (versionName == null || versionName.isEmpty()) return ""
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return versionName
    }

    /**
     * 是否开启安装未知应用权限
     * 8.0以上需要检测是否开启，
     * 8.0以下默认开启了
     */
    private fun isOpenInstallPackages():Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            SmallUtils.context?.packageManager?.canRequestPackageInstalls()?:false
        }else true
    }

    /**
     * 安装APK前的权限判断
     * @param activity Activity
     * @param downloadApk String 下载apk后的手机上的文件地址
     * 需要安装未知应用权限：
     * <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
     */
    fun installApk(activity: Activity, downloadApk: String) {
        if (!isOpenInstallPackages()){
            XXPermissions.with(activity)
                .permission(listOf(Permission.REQUEST_INSTALL_PACKAGES))
                .request(object :OnPermissionCallback{
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                        if (all) openInstallApk(downloadApk, activity)
                    }
                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                       if (never){//被永久拒绝，再次申请打开权限设置页面
                           XXPermissions.startPermissionActivity(activity,permissions)
                       }
                    }
                })
            return
        }
        openInstallApk(downloadApk, activity)
    }

    /**
     * 权限申请通过后，打开安装器开始安装APK
     * @param activity Activity
     * @param downloadApk String 下载apk后的手机上的文件地址
     * 注意：出现解析软件包时出现问题，要检测一下我们的路径是否正确，也许路径和apk之间少了/
     * 例如：/storage/emulated/0/Android/data/com.smallcake.justforcopy/cacheqq.apk
     * 但正确路径应该是：/storage/emulated/0/Android/data/com.smallcake.justforcopy/cache/qq.apk
     *
     */
    private fun openInstallApk(downloadApk: String, activity: Activity) {
        val intent = Intent(Intent.ACTION_VIEW)
        val file = File(downloadApk)
        //        ldd("安装路径==$downloadApk")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//7.0以上需要FileProvider来获取文件地址
            val apkUri = FileProvider.getUriForFile(activity, "${getAppPackageName()}.fileprovider", file)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)//添加此处 是临时对文件的授权 必须加上此句
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val uri = Uri.fromFile(file)
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
        }
        activity.startActivity(intent)
    }



    /**
     * 获取进程名称
     */
    private fun getProcessName(context: Context, pid:Int):String?{
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        for (runningApp in runningApps) {
            if (runningApp.pid==pid)return runningApp.processName
        }
        return null
    }

    /**
     * 获取SHA1命令
     * keytool -keystore 秘钥路径 -list -v
     * 获取应用的的key hashes 例如FaceBook接入SDK就需要
     *
     * 也可以通过命令获取
     * sh D:\keyHash.sh 别名 别名密码  签名文件jks
     * 其中sh为git中bin的命令
     * keyHash.sh为我们自己创建的一个可运行命令
    RELEASE_KEY_ALIAS=$1
    RELEASE_KEY_PASSWARD=$2
    RELEASE_KEY_PATH=$3
    keytool -exportcert -alias $RELEASE_KEY_ALIAS -storepass $RELEASE_KEY_PASSWARD -keystore $RELEASE_KEY_PATH | openssl sha1 -binary | openssl base64
     *
     *
     */
    private fun getKeyHashValue(context: Context) {
        try {
            val info = context.packageManager.getPackageInfo(
                getAppPackageName(),
                PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", android.util.Base64.encodeToString(md.digest(), android.util.Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d("KeyHash:",e.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.d("KeyHash:", e.toString())
        }
    }
    /**
     * 获取缓存大小
     */
    fun getTotalCacheSize(context: Context): String {
        var cacheSize: Long = FileUtils.getFileSize(context.cacheDir)
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            val size =  if (context.externalCacheDir==null)0 else FileUtils.getFileSize(context.externalCacheDir!!)
            cacheSize += size
        }
        return FormatUtils.formatSize(context,cacheSize)
    }

    /**
     * 清除缓存
     */
    fun clearCache(){
        val am = SmallUtils.context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        am!!.clearApplicationUserData()
    }

}