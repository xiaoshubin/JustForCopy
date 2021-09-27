package com.smallcake.temp.utils

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.smallcake.temp.weight.CameraActivity

object TakePhotoUtils {
    const val REQUEST_CAMERA = 1909

    private const val TAG ="CustomCameraInvisibleFragment"
    fun takePhoto(activity: FragmentActivity, cb:(String?) -> Unit, cameraFacing: Boolean=false){
        val fragmentManager = activity.supportFragmentManager
        val existedFragment  = fragmentManager.findFragmentByTag(TAG)
        val fragment = if (existedFragment!=null){
            existedFragment as CustomCameraInvisibleFragment
        }else{
            val invisibleFragment = CustomCameraInvisibleFragment()
            fragmentManager.beginTransaction().add(invisibleFragment, TAG).commitNow()
            invisibleFragment
        }
        fragment.takePhotoNow(activity,cb,cameraFacing)
    }
}

class CustomCameraInvisibleFragment : Fragment() {
    private var callback:((String?) -> Unit)?=null
    fun takePhotoNow(activity: FragmentActivity, cb:(String?) -> Unit,cameraFacing: Boolean=false) {
        callback=cb
        XXPermissions.with(activity)
            .permission(arrayListOf(Permission.CAMERA, Permission.MANAGE_EXTERNAL_STORAGE))
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    if (!all)return
                    val intent = Intent(activity, CameraActivity::class.java)
                    startActivityForResult(intent, TakePhotoUtils.REQUEST_CAMERA)
                }

                override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                    XXPermissions.startPermissionActivity(activity,permissions)
                }

            })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode ==TakePhotoUtils.REQUEST_CAMERA) {
            val picSavePath = data?.getStringExtra("imagePath")
            Log.i("图片地址","picSavePath:$picSavePath")
            callback?.invoke(picSavePath)
        }else{
            callback?.invoke(null)
        }
    }
}