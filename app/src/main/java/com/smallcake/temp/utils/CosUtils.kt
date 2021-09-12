package com.smallcake.temp.utils

import android.util.Log
import com.smallcake.smallutils.FileUtils
import com.smallcake.smallutils.FormatUtils
import com.smallcake.temp.MyApplication
import com.tencent.cos.xml.CosXmlService
import com.tencent.cos.xml.ktx.cosBucket
import com.tencent.cos.xml.ktx.cosObject
import com.tencent.cos.xml.ktx.cosService
import com.tencent.cos.xml.transfer.TransferState
import com.tencent.qcloud.core.auth.SessionQCloudCredentials
import kotlinx.coroutines.*
import java.io.File

/**
 * 腾讯云COS，上传文件
CosUtils.uploadFiles(files) {position, state, progress, url ->
when (state) {
TransferState.WAITING -> {
L.e("准备开始上传$position")
}
TransferState.IN_PROGRESS -> {
L.e("$position == 上传中$progress%")
}
TransferState.COMPLETED -> {
L.e("$position == 上传中完成:$url")
bind.iv.load(url)
}
TransferState.FAILED -> {
L.e("$position == 上传中失败")
}
else -> {L.e("$position == 上传失败$state")}
}

}
 */
object CosUtils {
    private const val BUCKET_HOST = "https://biubiu-static-1306772580.file.myqcloud.com"//存储桶域名：问后端要
    private const val cosRegin = "ap-singapore"//存储桶区域：问后端要
    private const val bucketName = "biubiu-static-1306772580"//存储桶名称：问后端要
    private val cosXmlService: CosXmlService
        get() = cosService(MyApplication.instance.applicationContext) {
            configuration {
                setRegion(cosRegin)
                isHttps(true)
            }
            credentialProvider {
                lifecycleCredentialProvider {
                    val txCos = UserUtils.txCos
                    return@lifecycleCredentialProvider SessionQCloudCredentials(
                        txCos?.credentials?.tmpSecretId,
                        txCos?.credentials?.tmpSecretKey,
                        txCos?.credentials?.sessionToken,
                        txCos?.expiredTime ?: 0L
                    )
                }
            }

        }

    /**
     * 上传文件到腾讯云
     * 域名前缀 https://biubiu-static-1306772580.file.myqcloud.com
     * 返回的地址：
     * https://biubiu-static-1306772580.cos.ap-singapore.myqcloud.com/xiao.jpg
     * 实际地址：
     * https://biubiu-static-1306772580.file.myqcloud.com/xiao.jpg
     *
     */
    fun uploadFile(sourceFile:File, cb:(TransferState,Int,String)->Unit){
        Log.d("cosxmlktx", "upload sourceFile:${sourceFile.path}")
        val fileSuffix = FileUtils.getFileSuffix(sourceFile.path)
        CoroutineScope(Dispatchers.IO).launch {
            val obj = cosObject {
                bucket = cosBucket {
                    service = cosXmlService
                    name = bucketName
                }
                key = "${System.currentTimeMillis()}.$fileSuffix"//对象键，也就是在存储中的名称
            }
            try {
                    obj.upload(
                        localFile = sourceFile,
                        progressListener = {currentLength,totalLength->
                            val progress = FormatUtils.getProgress(currentLength, totalLength)
                            cb.invoke(TransferState.IN_PROGRESS,progress,BUCKET_HOST+"/"+obj.key)
                        },
                        transferStateListener = {
                            Log.e("cosxmlktx", "upload state:${it}")
                            cb.invoke(it,100,BUCKET_HOST+"/"+obj.key)
                        }
                    )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    /**
     * 上传多张图片
     * @param sourceFiles List<File>
     * @param cb Function3<Int,TransferState, Int, String, Unit>
     * 位置，状态，进度，上传成功后的地址
     * //目前是顺序执行，非阻塞的
     * 串行：按顺序执行,9张图片36s
     * 采用并发：无法把控顺序，9张图片36s,网络最大上传6.3m/s,所以无优势
     * 故还是采用串行
     */
    fun uploadFiles(sourceFiles:List<File>, cb:(Int,TransferState,Int,String)->Unit){
        CoroutineScope(Dispatchers.IO).launch {
            sourceFiles.forEachIndexed{ i: Int, sourceFile: File ->
                Log.d("cosxmlktx", "upload sourceFile:${sourceFile.path}")
                val fileSuffix = FileUtils.getFileSuffix(sourceFile.path)
                val obj = cosObject {
                    bucket = cosBucket {
                        service = cosXmlService
                        name = bucketName
                    }
                    key = "${System.currentTimeMillis()}.$fileSuffix"//对象键，也就是在存储中的名称
                }
                try {
                        obj.upload(
                            localFile = sourceFile,
                            progressListener = {currentLength,totalLength->
                                val progress = FormatUtils.getProgress(currentLength, totalLength)
                                if (currentLength!=0L)cb.invoke(i,TransferState.IN_PROGRESS,progress,BUCKET_HOST+"/"+obj.key)
                            },
                            transferStateListener = {
                                Log.e("cosxmlktx", "upload state:${it}")
                                cb.invoke(i,it,100,BUCKET_HOST+"/"+obj.key)
                            }
                        )

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }
}