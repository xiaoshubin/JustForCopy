package com.smallcake.smallutils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import com.smallcake.smallutils.ToastUtil.Companion.showShort
import java.io.File
import java.io.FileInputStream
import java.io.IOException

/**
 * Date:2021/5/28 16:26
 * Author:SmallCake
 * Desc:
 **/
object FileUtils {
    /**
     * 如果文件不存在就创建它，否则什么也不做。
     *
     * @param file The file.
     * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
     */
    fun createOrExistsFile(file: File?): Boolean {
        if (file == null) return false
        if (file.exists()) return file.isFile
        return if (createOrExistsDir(file.parentFile)) false else try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 如果文件夹不存在就创建它，否则什么也不做。
     *
     * @param file The file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    fun createOrExistsDir(file: File?): Boolean {
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }
    /**
     * 创建文件夹,如果不存在的话
     * @param filePath
     * @return
     */
    fun makeDirs(filePath: String?): Boolean {
        if (filePath == null || filePath.isEmpty()) {
            return false
        }
        val folder = File(filePath)
        return if (folder.exists() && folder.isDirectory()) true else folder.mkdirs()
    }

    /**
     * 创建一个文件的上级目录，如果不存在的话
     * @param filePath
     * @return
     */
    fun makeParentDirs(filePath: String?, fileName: String?): Boolean {
        if (filePath == null || filePath.isEmpty()) {
            return false
        }
        val folder = File(filePath, fileName)
        return if (folder.getParentFile().exists() && folder.getParentFile()
                .isDirectory()
        ) true else folder.getParentFile().mkdirs()
    }
    /**
     * 调用系统文件管理器
     * 打开指定路径目录
     * document/primary:后面写死对应的根目录路径
     * 注意只能是根目录下的一级路径
     * 固定格式：content://com.android.externalstorage.documents/document/primary
     * 然如果再想得到下一级文件夹还需要%2f既 :Android%2fdata
     *
     * 例如：想打开/storage/emulated/0/Android/data/com.cxsr.zfdd/cache/04f700dcb0634d6e959887f02e10789d.pdf文件夹
     * 可以先去掉/storage/emulated/0/根目录前缀，在把后缀文件名称去掉，最后替换其中的/为%2f
    val path = "/storage/emulated/0/Android/data/com.cxsr.zfdd/cache/04f700dcb0634d6e959887f02e10789d.pdf"
    val newPath = path.replace("/storage/emulated/0/","")//去前缀
    val parentPath = File(newPath).parent//去后缀
    val lastPath = parentPath.replace("/","%2f")//改分割符
    val uri =Uri.parse("content://com.android.externalstorage.documents/document/primary:$lastPath")
     *
     */
    fun openFilePath(context: Context, path: String?) {
        val uri =Uri.parse("content://com.android.externalstorage.documents/document/primary:Download")
//        val uri =Uri.parse("content://com.android.externalstorage.documents/document/primary:%2fAndroid%2fdata%2f")
        val intent =  Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
        context.startActivity(intent)
    }

    /**
     *  获取文件大小
     */

    fun getFileSize(file: File): Long {
        var size: Long = 0
        if (file.exists()) {
            try {
                val fis = FileInputStream(file)
                size = fis.available().toLong()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            showShort("文件不存在！")
        }
        return size
    }

    fun getFileSize(path: String?) = getFileSize(File(path))
    /**
     * 获取文件后缀
     * /storage/emulated/0/DCIM/Camera/IMG_20210829_115105.jpg
     * 返回jpg
     * @param fileName String
     */
    fun getFileSuffix(fileName: String):String{
        return fileName.substring(fileName.lastIndexOf(".") + 1)
    }
    /**
     * 获取文件名称
     * http://testing.cloudjoytech.com.cn:50011/upload/2021-09/04f700dcb0634d6e959887f02e10789d.pdf
     * 返回04f700dcb0634d6e959887f02e10789d.pdf
     * @param fileName String
     */
    fun getFileName(fileName: String):String{
        return fileName.substring(fileName.lastIndexOf("/") + 1)
    }
}