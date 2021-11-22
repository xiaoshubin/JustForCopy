package com.smallcake.temp.service

import android.app.Notification
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.util.Log
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityServiceBinding

import android.graphics.BitmapFactory

import android.app.PendingIntent
import android.content.Context
import android.os.*
import android.text.TextUtils
import androidx.core.app.NotificationCompat

import com.smallcake.temp.MainActivity
import com.smallcake.temp.MyApplication
import com.smallcake.temp.R
import com.smallcake.temp.utils.AppUtils


/**
 * Service是一个应用程序组件，表示应用程序希望执行长期运行的操作
 * 【注意：】
 *    1.请注意，服务和其他应用程序对象一样，是在其宿主进程的主线程中运行的
 *    2.它不是一个独立的进程
 *    3.它不是一个线程
 * 【特性：】
 *    1.一个应用程序告诉系统它想在后台做的事情的工具(即使用户没有直接与应用程序交互)。这对应于Context.startService()的调用，该调用要求系统调度服务的工作，直到服务或其他人显式地停止它。
 *    2.一种应用程序向其他应用程序公开其部分功能的工具。这对应于对Context.bindService()的调用，它允许与服务建立长期连接，以便与它进行交互。
 * 【启动模式】
 *    1.startService
 *    启动Service后，Service就会一直运行下去，直到进程被杀死，Service在被启动后，就和启动它的context没有关系了，context不知道Service在干什么，当然，context还可以让它停止，它也可以自己停止
 *    2.bindService
 *    这个方式启动的Service可以和启动它的context互动
 *
 *【参考文档：】
 * 官方文档：https://developer.android.google.cn/reference/android/app/Service?hl=en
 *
 *【startService创建步骤】
 *     1.创建service
 *       创建一个RoomService:android.app.Service
 *     2.配置service
 *       在AndroidManifest.xml中配置
 *       <service android:name=".service.RoomService"/>
 *     3.启动service
 *       val intent = Intent(this@ServiceActivity, RoomService::class.java)
 *       startService(intent)
 *     4.停止service
 *       val intent = Intent(this@ServiceActivity, RoomService::class.java)
 *       stopService(intent)
 *       或者服务自己调用 stopSelf()
 *
 */
class ServiceActivity : BaseBindActivity<ActivityServiceBinding>() {

    private val TAG = "ServiceActivity"
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("后台服务")

        onEvent()
    }

    private fun showNotification(content:String) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val channelId = "L10001"
        val notification: Notification = NotificationCompat.Builder(this,channelId)
            .setContentTitle("Service")
            .setContentText(content)
            .setChannelId(AppUtils.getAppPackageName())//必须添加（Android 8.0） 【唯一标识】
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setContentIntent(pendingIntent)
            .build()
//        startForeground(this, notification)
        notification.flags = Notification.FLAG_AUTO_CANCEL
        val manager = MyApplication.instance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0,notification)
    }

    private fun onEvent() {
        /**
         * 启动服务
         * 对应服务的 onCreate -> onStartCommand 分别执行
         * 如果服务未停止，再次启动 onStartCommand 执行
         */
        bind.btnStart.setOnClickListener{
            val intent = Intent(this@ServiceActivity, RoomService::class.java)
            startService(intent)
        }
        /**
         * 停止服务
         * context.stopService() 或者服务自己调用 stopSelf()
         * 对应服务的 onDestroy 执行
         */
        bind.btnStop.setOnClickListener{
            val intent = Intent(this@ServiceActivity, RoomService::class.java)
            stopService(intent)
        }

        /**
         * 绑定服务
         * 服务端 onCreate -> onBind 页面 onServiceConnected
         * 如果已经绑定，再次绑定，不会执行任何方法
         */
        bind.btnBind.setOnClickListener{
            val intent = Intent(this@ServiceActivity, RoomService::class.java)
            bindService(intent, connection, BIND_AUTO_CREATE)
        }
        /**
         * 解绑服务
         * 服务端 onUnbind -> onDestroy
         * 如果服务没有绑定，直接解绑，会报异常：Service not registered
         */
        bind.btnUnBind.setOnClickListener{
//            if (isBindService){
//                isBindService = false
                unbindService(connection)
//            }
        }
        /**
         * 发送数据给服务端
         */
        bind.btnSendMsg.setOnClickListener{
//            if (!isBindService)return@setOnClickListener
            val content = bind.etContent.text.toString()
            if (TextUtils.isEmpty(content))return@setOnClickListener
            val msg = Message()
            msg.replyTo = clientMsger
            val data = Bundle()
            data.putString("send",content)
            msg.data = data
            serverMsger?.send(msg)

        }
    }

    /**
     * 处理收到服务端发来的消息
     */
    private val mHandler = Handler{
        bind.tvDesc.text = it.data.get("reply").toString()
        false
    }

    private var serverMsger: Messenger?=null                 //服务端数据发送者
    private val clientMsger: Messenger = Messenger(mHandler) //客服端数据接收者
    private var isBindService =false                         //是否已经绑定了服务

    /**
     * 客服端（UI）与服务端(Service)连接连接的桥梁
     * 1.当页面关闭，服务也会解绑
     */
    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            Log.e(TAG,"onServiceConnected:$componentName $iBinder")
            isBindService = true
            serverMsger= Messenger(iBinder)
            showNotification("已和${componentName.className}建立连接")


        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.e(TAG,"onServiceDisconnected:$componentName")
            isBindService = false
            serverMsger = null
        }
    }





}