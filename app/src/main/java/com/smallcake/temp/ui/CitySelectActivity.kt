package com.smallcake.temp.ui

import android.os.Bundle
import android.text.TextUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.smallcake.smallutils.EditTextUtils
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.adapter.CitySelectAdapter
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.bean.CityBean
import com.smallcake.temp.databinding.ActivityCitySelectBinding
import com.smallcake.temp.utils.showToast
import com.smallcake.temp.weight.IndexBar

/**
 * 选择城市

推荐使用registerForActivityResult替代onActivityResult

1.启动页面自定义注册器
private val register = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
    if (activityResult.resultCode == Activity.RESULT_OK) {
        city = activityResult.data?.getStringExtra("city")?:""
        if (!TextUtils.isEmpty(city)){
            bind.tvLocationAddress.text = "当前定位城市：${city}"
        }
    }
}

2.点击按钮启动此注册器
    bind.tvSwitchCity.setOnClickListener{
        register.launch(Intent(this,CitySelectActivity::class.java))
    }

3.在需要选择数据的页面回传数据
    val intent = Intent()
    intent.putExtra("city",item.name)
    setResult(RESULT_OK, intent)
    finish()

这样在第一步中的回调方法中就能拿到回调的数据
 */
class CitySelectActivity : BaseBindActivity<ActivityCitySelectBinding>() {
    private val mAdapter = CitySelectAdapter()
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
       bar.hide()
        initView()
        onEvent()
        loadData()
    }

    private fun initView() {
        bind.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CitySelectActivity)
            adapter = mAdapter

        }
    }

    private fun loadData() {
        val list:ArrayList<CityBean> = arrayListOf(
            CityBean("阿拉善盟"),
            CityBean("鞍山市"),
            CityBean("阿布"),
            CityBean("飓风等级"),
            CityBean("目标"),
            CityBean("密码"),
            CityBean("的关键的关键"),
            CityBean("接到鉴定机构"),
            CityBean("北京的房价"),
            CityBean("促进肌肤"),
            CityBean("的经济"),
            CityBean("而解放军"),
            CityBean("服端"),
            CityBean("分解机"),
            CityBean("附近的肌肤"),
            CityBean("和大家"),
            CityBean("就到家附近的"),
            CityBean("的经济高度"),
        )
        list.sortBy { it.firstLetter }
        val letters = ArrayList<String>()  //字母索引列表
        list.forEach {
            val letter = it.firstLetter
            if (!letters.contains(letter))
            letters.add(letter)
        }
        initLetter(letters)

        mAdapter.setList(list)

    }

    private fun initLetter(letters: java.util.ArrayList<String>) {
        bind.indexBar.setLetters(letters)
        bind.indexBar.setMyOnLetterChangeListener(object : IndexBar.OnLetterChangeListener{
            override fun onLetterChange(position: Int, letter: String?) {
                    mAdapter.data.forEachIndexed{index, cityBean ->
                        if ( letters.size > 0 && letters[0] == letter) {
                            bind.recyclerView.scrollToPosition(0)
                            return
                        }
                        val le: String = cityBean.firstLetter
                        if (
                            TextUtils.equals(letters[0],letter)
                            && TextUtils.equals(letters[letters.size - 1],le)
                            || TextUtils.equals(letter,le)
                        ){
                            bind.recyclerView.scrollToPosition(index)
                            val mLayoutManager = bind.recyclerView.layoutManager as LinearLayoutManager
                            mLayoutManager.scrollToPositionWithOffset(index, 0)
                            return
                        }


                    }
            }

            override fun onLetterNone() {

            }

        })

    }

    private fun onEvent() {
        bind.ivBack.setOnClickListener{finish()}
        EditTextUtils.setOnSearch(this,bind.etSearch){
            if (it.isNullOrEmpty()){
                showToast("请输入搜索城市中文名称")
                return@setOnSearch
            }
            showToast("开始搜素：$it")

        }

    }

}