package com.smallcake.temp.weight

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.smallcake.smallutils.text.ShapeTextButton
import com.smallcake.temp.R

class SearchView: ConstraintLayout {

    private var myClick: ((String)->Unit)? = null
    private var mContext: Context? = null
    private var etSearch:EditText? = null
    private var btnSearch: ShapeTextButton? = null
    private var ivDelete:ImageView? = null


    constructor(context: Context) : super(context) {
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        mContext = context
        LayoutInflater.from(context).inflate(R.layout.layout_search_view, this)
        etSearch =  findViewById(R.id.et_search)
        btnSearch =  findViewById(R.id.btn_search)
        ivDelete =  findViewById(R.id.iv_delete)
        btnSearch?.setOnClickListener {
            val str = etSearch?.text.toString().trim()
            myClick?.invoke(str)
        }
        etSearch?.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val str = s.toString()
                if(str.isEmpty()){
                    ivDelete?.visibility = View.GONE
                }else{
                    ivDelete?.visibility = View.VISIBLE
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        ivDelete?.setOnClickListener {
            etSearch?.text = Editable.Factory.getInstance().newEditable("")
        }


    }

    fun getSearchEdit():EditText{
        return etSearch!!
    }

    fun setEditHintText(str:String){
        etSearch?.hint = str
    }

    fun setButtonClick(cb: (String)->Unit){
        myClick = cb
    }


}