package com.yx.jiading.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.draggable.library.extension.ImageViewerHelper
import com.smallcake.temp.R
import java.io.File

/**
 * 添加图片
 * @author Abel
 */
class GridImageAdapter(private val maxCount: Int? = -1) :
    RecyclerView.Adapter<GridImageAdapter.ViewHolder>() {

    fun setOnInsertImageListener(onInsertImageListener: OnInsertImageListener) {
        this.onInsertImageListener = onInsertImageListener
    }

    fun setOnClickImageListener(onClickImageListener: OnClickImageListener) {
        this.onClickImageListener = onClickImageListener
    }

    fun insertImage(path: String, deleteVisibility: Boolean = true) {
//        dataList.add(0,Bean(path, deleteVisibility))
        dataList.add(Bean(path, deleteVisibility))
        if (maxCount == -1) {
            notifyDataSetChanged()
        } else {
            isImageSizeMeet = dataList.size == maxCount
            notifyDataSetChanged()
//            if (isImageSizeMeet) {
//                notifyDataSetChanged()
//            } else {
//                notifyItemInserted(0)
//            }
        }
    }

    fun insertImages(paths: List<String>, deleteVisibility: Boolean = true) {
        dataList.clear()
        paths.forEach {
            dataList.add(Bean(it, deleteVisibility))
        }
        isImageSizeMeet = if (maxCount != -1)
            dataList.size == maxCount
        else
            true

        notifyDataSetChanged()
    }

    fun getDataList(): MutableList<Bean> = dataList

    private var onInsertImageListener: OnInsertImageListener? = null
    private var onClickImageListener: OnClickImageListener? = null

    private val dataList = mutableListOf<Bean>()

    private var isImageSizeMeet = false

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

    private fun isInsertItem() = maxCount != -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ViewHolder.getView(parent))

    override fun getItemCount(): Int = if (isInsertItem()) {
        if (isImageSizeMeet) {
            dataList.size
        } else {
            dataList.size + 1
        }
    } else {
        dataList.size
    }

    private fun setUpOnClickImageDefault(view: View, position: Int) {
        onClickImageListener?.onClick(view, position)

        val paths = mutableListOf<String>()
        getDataList().forEach {
            if (it.path.isNotEmpty()) {
                paths.add(it.path)
            }
        }

        ImageViewerHelper.showImages(
            view.context,
            paths,
            position,
            true
        )
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
                        onInsertImageListener?.onInsertImage()
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
                LayoutInflater.from(viewGroup.context).inflate(
                    R.layout.grid_item_image,
                    viewGroup,
                    false
                )
        }

        fun setOnDeleteListener(onDeleteListener: OnDeleteListener?) {
            this.onDeleteListener = onDeleteListener
        }

        fun getItemImageView(): AppCompatImageView = imageView

        private var onDeleteListener: OnDeleteListener? = null

        private val imageView: AppCompatImageView by lazy {
            itemView.findViewById<AppCompatImageView>(
                R.id.iv_grid_item_image
            )
        }

        private val deleteView: View by lazy {
            itemView.findViewById<View>(R.id.btn_grid_list_image_delete)
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
            itemView.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.white
                )
            )
        }
    }

    data class Bean(
        val path: String,
        val deleteVisibility: Boolean
    )

    interface OnInsertImageListener {
        fun onInsertImage()
    }

    interface OnDeleteListener {
        /**
         * 删除item
         *
         * @param view     itemView
         * @param position 下标
         */
        fun onDelete(view: View, position: Int)
    }

    interface OnClickImageListener {
        /**
         * 点击item
         *
         * @param view     itemView
         * @param position 下标
         */
        fun onClick(view: View, position: Int)
    }
}