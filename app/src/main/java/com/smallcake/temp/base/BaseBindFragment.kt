package com.smallcake.temp.base

import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.viewbinding.ViewBinding
import com.dylanc.viewbinding.base.ViewBindingUtil
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle
import com.trello.rxlifecycle2.LifecycleProvider


/**
 * 扩展函数：省去beginTransaction()和commit()代码
 * @receiver FragmentManager
 * @param func [@kotlin.ExtensionFunctionType] Function1<FragmentTransaction, Unit>
 */
inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commit()
}
//添加Fragment
fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int){
    supportFragmentManager.inTransaction { add(frameId, fragment) }
}
//替换Fragment
fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction{replace(frameId, fragment)}
}
/**
 * Fragment基类
 bind需要在 onViewCreated(view: View, savedInstanceState: Bundle?)后调用
 注意在onViewCreated不要调用super.onViewCreated(view, savedInstanceState)方法，否则此方法会调用两次
 */
abstract class BaseBindFragment<VB : ViewBinding>: Fragment() {
    private var _binding: VB? = null
    val bind:VB get() = _binding!!
    protected lateinit var provider: LifecycleProvider<Lifecycle.Event>//生命周期提供者
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ViewBindingUtil.inflateWithGeneric(this,layoutInflater)
        provider = AndroidLifecycle.createLifecycleProvider(this)
        return bind.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun goActivity(clz: Class<*>) = startActivity(Intent(requireActivity(), clz))
}