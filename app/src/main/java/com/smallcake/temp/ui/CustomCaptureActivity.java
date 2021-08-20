package com.smallcake.temp.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.google.zxing.Result;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.smallcake.smallutils.BitmapUtils;
import com.smallcake.smallutils.ToastUtil;
import com.smallcake.temp.R;
import com.smallcake.temp.utils.GlideEngine;
import com.smallcake.temp.utils.L;
import com.xuexiang.xqrcode.XQRCode;
import com.xuexiang.xqrcode.ui.CaptureActivity;
import com.xuexiang.xqrcode.util.QRCodeAnalyzeUtils;

import java.util.List;

/**
 * 自定义二维码扫描界面
 *
 * @author xuexiang
 * @since 2019/5/30 10:43
 */
public class CustomCaptureActivity extends CaptureActivity implements View.OnClickListener {

    private AppCompatImageView mIvFlashLight;
    private AppCompatImageView mPic;

    private boolean mIsOpen;

    /**
     * 开始二维码扫描
     *
     * @param fragment
     * @param requestCode 请求码
     * @param theme       主题
     */
    public static void start(Fragment fragment, int requestCode, int theme) {
        Intent intent = new Intent(fragment.getContext(), CustomCaptureActivity.class);
        intent.putExtra(KEY_CAPTURE_THEME, theme);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 开始二维码扫描
     *
     * @param activity
     * @param requestCode 请求码
     * @param theme       主题
     */
    public static void start(Activity activity, int requestCode, int theme) {
        Intent intent = new Intent(activity, CustomCaptureActivity.class);
        intent.putExtra(KEY_CAPTURE_THEME, theme);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getCaptureLayoutId() {
        return R.layout.activity_custom_capture;
    }

    @Override
    protected void beforeCapture() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        mIvFlashLight = findViewById(R.id.iv_flash_light);
        mPic = findViewById(R.id.iv_pic);
    }

    @Override
    protected void onCameraInitSuccess() {
        mIvFlashLight.setVisibility(View.VISIBLE);
        mPic.setVisibility(View.VISIBLE);

        mIsOpen = XQRCode.isFlashLightOpen();
        refreshFlashIcon();
        mIvFlashLight.setOnClickListener(this);
        mPic.setOnClickListener(this);
    }

    @Override
    protected void onCameraInitFailed() {
        mIvFlashLight.setVisibility(View.GONE);
        mPic.setVisibility(View.GONE);
    }

    private void refreshFlashIcon() {
        if (mIsOpen) {
            mIvFlashLight.setImageResource(R.drawable.picture_ic_flash_on);
        } else {
            mIvFlashLight.setImageResource(R.drawable.picture_ic_flash_off);
        }
    }

    private void switchFlashLight() {
        mIsOpen = !mIsOpen;
        try {
            XQRCode.switchFlashLight(mIsOpen);
            refreshFlashIcon();
        } catch (RuntimeException e) {
            e.printStackTrace();
            ToastUtil.showLong("设备不支持闪光灯!");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_flash_light:
                switchFlashLight();
                break;
            case R.id.iv_pic:
                getPhoto(this);
                break;
            default:
                break;
        }
    }

    private void getPhoto(AppCompatActivity activity) {
        PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage()) //全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                .isWeChatStyle(true) // 是否开启微信图片选择风格
                .maxSelectNum(1) // 最大图片选择数量
                .imageSpanCount(3) // 每行显示个数
                .isReturnEmpty(false) // 未选择数据时点击按钮是否可以返回
                .compressQuality(80) // 图片压缩后输出质量 0~ 100
                .cutOutQuality(90) // 裁剪输出质量 默认100
                .minimumCompressSize(100) // 小于多少kb的图片不压缩
                //.isAndroidQTransform(true)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);不压缩不裁剪有效,默认处理
                .isCompress(true) // 是否压缩
                .isEnableCrop(false) // 是否裁剪
                .isZoomAnim(true) // 图片列表点击 缩放效果 默认true
                .synOrAsy(false) //同步true或异步false 压缩 默认同步
                .isPreviewImage(false) // 是否可预览图片
                .isCamera(true) // 是否显示拍照按钮
                .withAspectRatio(1, 1) // 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(true) // 是否显示uCrop工具栏，默认不显示
                .freeStyleCropEnabled(true) // 裁剪框是否可拖拽
                .circleDimmedLayer(false) // 是否圆形裁剪
                .showCropFrame(false) // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                //.cropImageWideHigh(120,120)// 裁剪宽高比，设置如果大于图片本身宽高则无效
                .rotateEnabled(false) // 裁剪是否可旋转图片
                .scaleEnabled(false) // 裁剪是否可放大缩小图片
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        LocalMedia media = result.get(0);
                        printMedia(media);
                        String androidQToPath = media.getAndroidQToPath();
                        String path = "";
                        if (TextUtils.isEmpty(androidQToPath)) {
                            if (media.isCompressed()) path = media.getCompressPath();
                            else path = media.getRealPath();
                        } else {
                            path = androidQToPath;
                        }

                        Bitmap bitmapPath = BitmapUtils.INSTANCE.getBitmapPath(path);
                        XQRCode.analyzeQRCode(bitmapPath, new QRCodeAnalyzeUtils.AnalyzeCallback() {
                            @Override
                            public void onAnalyzeSuccess(Bitmap bitmap, String result) {
                                ToastUtil.showLong("解析结果："+result);
                                L.e("解析结果："+result);
                            }

                            @Override
                            public void onAnalyzeFailed() {

                            }
                        });

                    }

                    @Override
                    public void onCancel() {
                        L.e("没有选择图片");
                    }


                });
    }



    private void printMedia(LocalMedia media){
        L.e("是否压缩:" + media.isCompressed());
        L.e("压缩:" + media.getCompressPath());
        L.e("原图:" + media.getPath());
        L.e("绝对路径:" + media.getRealPath());
        L.e("是否裁剪:" + media.isCut());
        L.e("裁剪:" + media.getCutPath());
        L.e("是否开启原图:" + media.isOriginal());
        L.e("原图路径:" + media.getOriginalPath());
        L.e("Android Q 特有Path:" + media.getAndroidQToPath());
        L.e("宽高: " + media.getWidth() + "x" + media.getHeight());
        L.e("Size: " + media.getSize());
    }
}
