package com.smallcake.temp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.bean.ConstactBean
import com.smallcake.temp.databinding.ActivityGreenDaoBinding
import com.smallcake.temp.utils.GreenDaoHelper

/**
 * 数据存储
 * 参考：
 * 简书 GreenDao使用总结：https://www.jianshu.com/p/967d402d411d
 * Github地址：          https://github.com/greenrobot/greenDAO
 *
1.引入对应的包
    在Project build.gradle中配置
    classpath 'org.greenrobot:greendao-gradle-plugin:3.3.0'
    在Module build.gradle配置插件,并引入
    apply plugin: 'org.greenrobot.greendao'
    并引入包
    implementation 'org.greenrobot:greendao:3.3.0'

2.初始化配置
    在Module build.gradle 的android{}中加入初始版本
    greendao {
    schemaVersion 1 //当前数据库版本
    }

3.在MyApplication中初始化
    companion object{
        lateinit var daoSession : DaoSession
    }
    fun initDao(){
        val helper = DaoMaster.DevOpenHelper(this,"smallcake.db")//创建的数据库名。
        val db = helper.writableDb
        daoSession = DaoMaster(db).newSession()
    }

4.创建一个工具类，来增删改查
    *@see GreenDaoHelper

5.定义一个数据存储类,只支持java的实体类
    @Entity
    public class ConstactBean {
        @Id(autoincrement = true)//主键，自增
        private Long id;
        private String name;
    }


问题集锦：
1.无法生成实体类对应属性，get set方法，
原因：不支持kotlin，需要使用java创建实体类
2.无法生成GreenDao目录，没有看到greendao目录，其实它在app/build/source里面
3.报错，无法运行：Unresolved reference: GreenDao
原因："org.greenrobot.greendao" 必须放在第二排
4.Expected unique result, but count was 2,
原因：查询出来的结果可能是多个，但定义的返回是单个，
解决：unique()改为list()
5.保存的数据在手机具体哪里
在当前包名下的databases里面，对应的数据库名称/data/data/com.smallcake.justforcopy/databases/smallcake.db
6.模糊查询无效
 解决：查询的关键字后面要加%号
7.SQLiteException: no such table
 原因：修改了数据库，却没有版本升级，
 解决：卸载重装，或升级版本

 *
 */
class GreenDaoActivity : BaseBindActivity<ActivityGreenDaoBinding>() {
    private val TAG = "GreenDaoActivity"
    private var selectBean:ConstactBean?=null
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("数据存储GreenDao")
        bind.apply {
            btnAdd.setOnClickListener{add()}
            btnDel.setOnClickListener{del()}
            btnSearch.setOnClickListener{search()}
            btnSearchM.setOnClickListener{searchLike()}
            btnSearchAll.setOnClickListener{searchAll()}
            btnDelAll.setOnClickListener{
                GreenDaoHelper.delAll()
                bind.layoutList.removeAllViews()
            }
            btnEdit.setOnClickListener{
                if (selectBean==null)return@setOnClickListener
                val str = bind.etContent.text.toString()
                if (TextUtils.isEmpty(str)) return@setOnClickListener
                GreenDaoHelper.update(selectBean,str)
                searchAll()
            }
        }

    }

    private fun add() {
        val str = bind.etContent.text.toString()
        if (TextUtils.isEmpty(str)) return
        val bean = ConstactBean()
        bean.name = str
        val long = GreenDaoHelper.insert(bean)
        Log.e(TAG, "插入的id:$long")
        searchAll()
    }

    private fun del() {
        val str = bind.etContent.text.toString()
        if (TextUtils.isEmpty(str)) return
        GreenDaoHelper.del(str)
        searchAll()
    }

    private fun search() {
        val str = bind.etContent.text.toString()
        if (TextUtils.isEmpty(str)) return
        val beanList = GreenDaoHelper.searchByWhere(str)
        showList(beanList)
    }
    private fun searchLike() {
        val str = bind.etContent.text.toString()
        if (TextUtils.isEmpty(str)) return
        val beanList = GreenDaoHelper.searchLike(str)
        showList(beanList)
    }
    private fun searchAll() {
        val beanList =GreenDaoHelper.searchAll()
        showList(beanList)
    }

    @SuppressLint("SetTextI18n")
    private fun showList(beanList: List<ConstactBean>?) {
        bind.layoutList.removeAllViews()
        beanList?.forEach {bean->
            val tv = TextView(this@GreenDaoActivity)
            tv.text = "[id:${bean.id},name:${bean.name}]"
            tv.setOnClickListener{
                selectBean = bean
                bind.tvEditBefore.text="已选中 [id:${bean.id},name:${bean.name}]"
            }
            bind.layoutList.addView(tv)
        }
    }

}
