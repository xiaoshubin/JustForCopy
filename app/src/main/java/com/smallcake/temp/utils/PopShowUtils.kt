package com.smallcake.temp.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Message
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.format.DateUtils
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.smallcake.smallutils.*
import com.smallcake.temp.MyApplication
import com.smallcake.temp.R
import com.smallcake.temp.music.MusicClient
import com.smallcake.temp.music.MusicClientListener
import com.smallcake.temp.pop.BottomTimeStartEndSelectPop
import com.smallcake.temp.weight.XImageLoader
import java.util.*

object PopShowUtils {
    /**
     * 显示取消去顶弹框
     * @param listener Function4<DatePicker?, Int, Int, Int, Unit>
     */
    fun showConfirm(context: Context,title: String="",content:String,listener: () -> Unit) {
        XPopup.Builder(context)
            .asConfirm(title,content){
                listener.invoke()
            }.show()
    }
    /**
     * 原生显示时分
     * @param listener Function4<DatePicker?, Int, Int, Int, Unit>
     */
    fun showHM(context: Context,listener: (Int, Int) -> Unit) {
        val ca = Calendar.getInstance()
        val mHour = ca[Calendar.HOUR]
        val mMinute = ca[Calendar.MINUTE]
        TimePickerDialog(context,0,
            { _, hourOfDay, minute ->
                listener.invoke(hourOfDay, minute)
            },mHour,mMinute,true).show()
    }
    /**
     * 原生年月日选择器
     */
    fun showYMD(context: Context, listener: (Int,Int,Int)->Unit) {
        val ca = Calendar.getInstance()
        val caMax = Calendar.getInstance()
        val mYear = ca[Calendar.YEAR]
        val mMonth = ca[Calendar.MONTH]
        val mDay = ca[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(context, R.style.dialog_date,
            { view, year, month, dayOfMonth -> listener.invoke(year,month,dayOfMonth)},
            mYear, mMonth, mDay)
        val msg = Message()
        msg.what = DialogInterface.BUTTON_POSITIVE
        val confirmText = SpannableStringUtils.getBuilder("确定").setForegroundColor(Color.RED).create()
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,confirmText) { dialog, which ->
            listener.invoke(
                datePickerDialog.datePicker.year,
                datePickerDialog.datePicker.month,
                datePickerDialog.datePicker.dayOfMonth
            )
        }
        val cancleText = SpannableStringUtils.getBuilder("取消").setForegroundColor(Color.RED).create()
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,cancleText) { dialog, which ->

        }
        val datePicker = datePickerDialog.datePicker
        //范围控制,最大能选两年前的月份
        caMax.add(Calendar.YEAR, 1)
        datePicker.minDate = ca.timeInMillis //最小为当前
        datePicker.maxDate = caMax.timeInMillis //最大时间为当前年月
        //范围控制
        datePickerDialog.show()
    }
    /**
     * 第三方年月日时分秒选择器
     */
     fun showTimePicker(context: Context,cb:(Date)->Unit) {
        val showTypes = booleanArrayOf(true,true,true,true,true,false)
        val picker = TimePickerBuilder(context) { date, _ ->
            cb.invoke(date)
        }.setType(showTypes)
            .setupDefault()
            .build()
        picker.setTitleText("时间选择")
        picker.show(true)
    }
    /**
     * 开始和结束时间选择器
     * @param context Context
     * @param cb Function2<String, String, Unit>
     */
    fun showDateSelect(context: Context, cb:(String,String)->Unit){
        XPopup.Builder(context)
            .maxHeight(Screen.height/3*2)
            .asCustom(BottomTimeStartEndSelectPop(context,cb))
            .show()
    }

    /**
     * 从底部弹出的选择列表
     * @param tv TextView 点击事件，选中后赋值给此文本控件
     * @param title String 标题
     * @param array Array<String> 数据集合
     * @param iv ImageView 用于右侧箭头的翻转动画
     * @param cb Function1<Int, Unit> 回调，只关心位置信息从而确定要传的值
     * 例如：
     * val list = arrayOfNulls<String>(180)
     * for (index in 120..300)list[index] = "${index}cm"
     */
    fun showBottomList(
        title: String = "",
        array: Array<String>,
        iv: ImageView,
        cb: (Int, String) -> Unit
    ){
        XPopup.Builder(iv.context)
            .setPopupCallback(object : SimpleCallback() {
                override fun onShow(popupView: BasePopupView?) {
                    super.onShow(popupView)
                    AnimUtils.rotateAnimX0To180(iv)
                }

                override fun onDismiss(popupView: BasePopupView?) {
                    super.onDismiss(popupView)
                    AnimUtils.rotateAnimX180To0(iv)
                }
            })
            .asBottomList(title, array){ i: Int, s: String ->
                cb.invoke(i, s)
            }.show()
    }

    /**
     * 点击显示单张大图
     */
    fun showBigPic(iv: ImageView, url: Any){
        XPopup.Builder(iv.context)
            .asImageViewer(iv, url, XImageLoader())
            .isShowSaveButton(false)
            .show()
    }

    /**
     * 点击显示多张大图
     * @param iv ImageView 从那个iv开始
     * @param currentPosition Int  弹出弹窗后从第几张图片开始
     * @param list List<String> 图片列表
     * @param recyclerView RecyclerView 拿到对应的item再拿到对应的ImageView
     * layout_imgs 为当前列表中的装载多张图片的布局id
     */
    fun showBigPics(iv: ImageView, currentPosition: Int, list: List<String>, recyclerView: RecyclerView,){
        XPopup.Builder(iv.context).asImageViewer(iv, currentPosition, list,{ popupView, imgPosition ->
            //用于更新回退动画
//            val ivImg = recyclerView.getChildAt(0).findViewById<LinearLayout>(R.id.layout_imgs).getChildAt(imgPosition) as ImageView
//            popupView.updateSrcView(ivImg)

        }, XImageLoader())
            .isShowSaveButton(false)
            .show()
    }
    /**
     * 显示单张大图,无控件
     * 用于在WebView点击图片查看大图
     */
    fun showBigPic(context: Context,url:Any){
        XPopup.Builder(context)
            .asImageViewer(null,url,false, R.drawable.gray_round6_bg,R.drawable.gray_round6_bg,R.drawable.gray_round6_bg,false,XImageLoader())
            .show()

    }
    /**
     * 点击显示多张大图,无控件
     */
    fun showBigPics(context: Context, currentPosition: Int, list: List<String>){
        XPopup.Builder(context).asImageViewer(null, currentPosition, list,null, XImageLoader())
            .isShowSaveButton(false)
            .show()
    }
    /**
     * 显示一个音乐播放的悬浮小控件
     * 需要悬浮窗权限
     */
     fun showMusicFloatWeight() {
        XXPermissions.with(ActivityCollector.findTopActivity()).permission(Permission.SYSTEM_ALERT_WINDOW).request { _, all ->
            if (!all) return@request
            EasyFloat.with(MyApplication.instance)
                .setLayout(R.layout.music_weight){
                    //关闭音乐小控件
                    it.findViewById<ImageView>(R.id.iv_close).setOnClickListener{ EasyFloat.hideAppFloat("MusicWeight") }
                    //播放音乐按钮
                    val ivPlay = it.findViewById<ImageView>(R.id.iv_play)
                    val tvName = it.findViewById<TextView>(R.id.tv_name)
                    val tvCurrentTime = it.findViewById<TextView>(R.id.tv_current_time)
                    val tvTotalTime = it.findViewById<TextView>(R.id.tv_total_time)
                    val seekBar = it.findViewById<SeekBar>(R.id.seek_bar)
                    //播放+暂停
                    ivPlay.setOnClickListener{
                        MusicClient.instance.transportControls?.apply {
                            val state =  MusicClient.instance.mediaController?.playbackState?.state
                            if (state == PlaybackStateCompat.STATE_PLAYING)pause() else play()
                        }
                    }
                    seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
                        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                        override fun onStopTrackingTouch(seekBar: SeekBar) {
                            val progress = seekBar.progress
                            val max = seekBar.max
                            Log.i("音乐小控件", "onStopTrackingTouch: progress=$progress max=$max")
                            MusicClient.instance.transportControls?.seekTo(progress.toLong())
                        }
                    })

                    val musicClientListener = object : MusicClientListener() {

                        override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
                            ivPlay.setImageResource(if (PlaybackStateCompat.STATE_PLAYING == state.state)R.drawable.exo_ic_pause_circle_filled else  R.drawable.exo_ic_play_circle_filled)
                        }
                        override fun onMetadataChanged(metadata: MediaMetadataCompat) {
                            val duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
                            val title = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                            if (duration > 0) {
                                Log.i("音乐小控件", "更新总进度: duration=$duration")
                                tvName.text = title
                                seekBar.max = duration.toInt()
                                tvTotalTime.text = DateUtils.formatElapsedTime(duration)
                            }
                        }

                        override fun onProgress(currentDuration: Int, totalDuration: Int) {
                            seekBar.progress = currentDuration
                            tvCurrentTime.text = DateUtils.formatElapsedTime(currentDuration.toLong())
                        }


                    }
                    MusicClient.instance.registerListener(musicClientListener)
                }
                .setTag("MusicWeight")
                .setShowPattern(ShowPattern.ALL_TIME)
                .setDragEnable(true)
                .setGravity(Gravity.CENTER_VERTICAL or Gravity.END)
                .show()


            EasyFloat.showAppFloat("MusicWeight")


        }

    }
}

/**
 * 范围设定 ：从当前时间 到 一年后的当前时间
 * 自定义 start 前多少年，end 后多少年
 */
fun TimePickerBuilder.setupDefault(start:Int=0, end:Int=1): TimePickerBuilder {
    val currentCalendar = TimeUtils.getTimeCalender(System.currentTimeMillis())
    val currentYear = currentCalendar.get(Calendar.YEAR)
    val currentMonth = currentCalendar.get(Calendar.MONTH)
    val currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH)
    val startDate: Calendar = Calendar.getInstance()
    startDate.set(currentYear-start, currentMonth, currentDay)
    val endDate: Calendar = Calendar.getInstance()
    endDate.set(currentYear + end, currentMonth, currentMonth)
    return setCancelText("取消")
        .setSubmitText("确定")
        .setCancelColor(Color.parseColor("#666666"))
        .setSubmitColor(Color.parseColor("#D5462B"))
        .setTitleColor(Color.parseColor("#999999"))
        .setTitleSize(18)
        .setContentTextSize(18)
        .setSubCalSize(18)
        .isCyclic(false)
        .setLineSpacingMultiplier(1.8F)
        .setOutSideCancelable(true)
        .isDialog(false)
        .setItemVisibleCount(5)
        .setRangDate(startDate, endDate)
        .setDate(currentCalendar)
}