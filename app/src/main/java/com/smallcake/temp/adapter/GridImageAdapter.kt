package com.smallcake.temp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.smallcake.temp.R
import java.io.File

/**
 * 添加图片适配器
 * 布局文件
 * @see R.layout.grid_item_image
 */
class GridImageAdapter(private val maxCount: Int? = -1) : RecyclerView.Adapter<GridImageAdapter.ViewHolder>() {
    data class Bean(
        val path: String,//图片路径
        val deleteVisibility: Boolean//删除按钮是否显示
    )

    private val dataList = mutableListOf<Bean>()
    var isImageSizeMeet = false//是否选择满了图片：选择的图片==最大限制图片数量
    private var addImgListener: (()->Unit)? = null//插入了图片事件
    private var onClickImageListener: ((View, Int)->Unit)? = null//点击item
    //外部获取当前的数据集，用来判定还能添加的图片张数
    fun getDataList(): MutableList<Bean> = dataList
    private fun isInsertItem() = maxCount != -1//是否是添加子项的操作

    interface OnDeleteListener {
        /**
         * 删除item
         * @param view     itemView
         * @param position 下标
         */
        fun onDelete(view: View, position: Int)
    }

    /**
     * 添加图片事件
     */
    fun setOnAddImgListener(addImgListener: ()->Unit) {
        this.addImgListener = addImgListener
    }
    /**
     * 点击图片
     */
    fun setOnClickImageListener(onClickImageListener: (View, Int)->Unit) {
        this.onClickImageListener = onClickImageListener
    }
    /**
     * 点击已经显示的图片
     */
    fun setUpOnClickImageDefault(view: View, position: Int) {
        onClickImageListener?.invoke(view, position)

    }
    //添加单张图片
    fun insertImage(path: String, deleteVisibility: Boolean = true) {
        dataList.add(Bean(path, deleteVisibility))
        if (maxCount == -1) {
            notifyDataSetChanged()
        } else {
            isImageSizeMeet = dataList.size == maxCount
            notifyDataSetChanged()
        }
    }
    //添加多张张图片
    fun insertImages(paths: List<String>, deleteVisibility: Boolean = true) {
        dataList.clear()
        paths.forEach {dataList.add(Bean(it, deleteVisibility))}
        isImageSizeMeet = if (maxCount != -1)dataList.size == maxCount else true
        notifyDataSetChanged()
    }

    private val mOnDeleteListener = object : OnDeleteListener {
        override fun onDelete(view: View, position: Int) {
            val deleteBean = dataList[position]
            dataList.remove(deleteBean)
            if (isImageSizeMeet) {
                notifyDataSetChanged()
            } else {
                notifyItemRemoved(position)
            }
            isImageSizeMeet = dataList.size == maxCount
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(ViewHolder.getView(parent))

    override fun getItemCount(): Int = if (isInsertItem()) {
        if (isImageSizeMeet) {
            dataList.size
        } else {
            dataList.size + 1
        }
    } else {
        dataList.size
    }



    private fun getItemBean(position: Int): Bean = dataList[position]

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (isInsertItem()) {
            when {
                isImageSizeMeet -> {
                    holder.setValue(getItemBean(position))
                    holder.itemView.setOnClickListener {
                        setUpOnClickImageDefault(holder.getItemImageView(), holder.layoutPosition)
                    }
                    holder.setOnDeleteListener(mOnDeleteListener)
                }
                position != itemCount - 1 -> {
                    holder.setValue(getItemBean(position))
                    holder.itemView.setOnClickListener {
                        setUpOnClickImageDefault(holder.getItemImageView(), holder.layoutPosition)
                    }
                    holder.setOnDeleteListener(mOnDeleteListener)
                }
                else -> {
                    holder.emptyValue()
                    holder.setOnDeleteListener(null)
                    holder.itemView.setOnClickListener {
                        addImgListener?.invoke()
                    }
                }
            }
        } else {
            holder.setValue(getItemBean(position))
            holder.itemView.setOnClickListener {
                setUpOnClickImageDefault(holder.getItemImageView(), holder.layoutPosition)
            }
            holder.setOnDeleteListener(null)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        companion object {
            fun getView(viewGroup: ViewGroup): View =
                LayoutInflater.from(viewGroup.context).inflate(R.layout.grid_item_image,viewGroup,false)
        }

        fun setOnDeleteListener(onDeleteListener: OnDeleteListener?) {
            this.onDeleteListener = onDeleteListener
        }

        fun getItemImageView(): AppCompatImageView = imageView

        private var onDeleteListener: OnDeleteListener? = null

        private val imageView: AppCompatImageView by lazy {
            itemView.findViewById(R.id.iv_grid_item_image)
        }

        private val deleteView: View by lazy {
            itemView.findViewById(R.id.btn_grid_list_image_delete)
        }

        fun setValue(bean: Bean) {
            if (bean.path.isNotEmpty()) {
                imageView.visibility = View.VISIBLE
                imageView.load(File(bean.path)){
                    transformations(RoundedCornersTransformation(6f))
                }
                deleteView.visibility = if (bean.deleteVisibility) View.VISIBLE else View.GONE
                itemView.setBackgroundColor(Color.TRANSPARENT)
            } else {
                deleteView.visibility = View.GONE
            }

            deleteView.setOnClickListener {
                onDeleteListener?.onDelete(it, layoutPosition)
            }
        }

        fun emptyValue() {
            imageView.visibility = View.GONE
            deleteView.visibility = View.GONE
            itemView.setBackgroundColor(Color.WHITE)
        }
    }

}