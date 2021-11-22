package com.smallcake.temp.service

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import com.contrarywind.timer.MessageHandler


/**
 * 生命周期
 * 1.startService() -> stopService()
 * onCreate -> onStartCommand -> onDestroy
 * 2.bindService()-> unbindService()
 * onCreate -> onBind (UI onServiceConnected) -> onUnbind -> onDestroy
 */
class RoomService: Service() {
    private val TAG = "RoomService"


    override fun onBind(intent: Intent?): IBinder? {
        Log.e(TAG,"onBind")
        return serverMsger.binder
    }
    override fun onUnbind(intent: Intent?): Boolean {
        Log.e(TAG,"onUnbind")
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG,"onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand == Received start id $startId: $intent");
        return START_NOT_STICKY
    }

    /**
     * 注意：当服务执行了onDestroy msgHandler照样可以收发消息
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG,"onDestroy")
    }

    private val msgHandler = Handler{msg->
        val cliendText = msg.data.getString("send")//收到客服端发来的消息
        if (cliendText?.contains("时间")==true){
            val timeMsg = Message()
            timeMsg.arg1 = (System.currentTimeMillis()/1000).toInt()
            msg.replyTo.send(timeMsg)
        }

        //通过当前收到的客户端消息类型，给客户端发送消息
        val replyMsg = Message()
        val data = Bundle()
        data.putString("reply",  "Service: $cliendText")
        replyMsg.data = data
        try {
            msg.replyTo.send(replyMsg)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        false
    }
    private val serverMsger: Messenger = Messenger(msgHandler)



}