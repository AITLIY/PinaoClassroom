package com.yiyin.aobosh.activitys.mine;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.android.volley.RequestQueue;
import com.githang.statusbar.StatusBarCompat;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.application.GlobalParameterApplication;
import com.yiyin.aobosh.bean.UserInfo;
import com.yiyin.aobosh.utils.Sputils;
import com.yiyin.aobosh.utils.ToastUtil;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserInfoActivity extends Activity {

    private Context mContext;
    private UserInfo mUserInfo;
    private RequestQueue requestQueue;

    AlertDialog mAlertDialog;
    ProgressDialog  mProgressDialog;

    private static final String TAG = "UserPhoto";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.app_title_bar), true);


        init();
    }

    private void init() {

        initView();
        initData();
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.user_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingHeaderPic();
            }
        });
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case REQUEST_CODE_PICK_IMAGE:

                if (resultCode == RESULT_OK && data != null) {

                    Uri uri = data.getData();
                    if (null == uri)
                        return;

                    Log.d(TAG, "PICK_IMAGE: 选择的图片" + uri.getPath());

                    creatFolder();

                    cropPic(uri);
                }

                break;

            case REQUEST_CODE_CAPTURE_CAMEIA:

                String filepath2= Sputils.getSpString(mContext, CAPTURE_PIC_TEMP_PATH, "");
                Log.d(TAG,"CAPTURE_CAMEIA: get imgpath :" + filepath2);

                File spath = new File(filepath2);

                Uri uri = Uri.fromFile(spath);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = getImageContentUri(spath);
                }

                Log.d(TAG,"当前的uri " + uri.toString());

                cropPic(uri);

                break;

            case PHOTO_REQUEST_CUT:

                Log.d(TAG,"PHOTO_REQUEST_CUT 得到裁剪后图片");

                //将Uri图片转换为Bitmap
                try {

                    //                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));

                    //                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cross_img);

                    FileInputStream fis = new FileInputStream(filepath);
                    Bitmap bitmap  = BitmapFactory.decodeStream(fis);

                    Log.d(TAG,"PHOTO_REQUEST_CUT " + String.valueOf(new File(filepath).length()));
                    Log.d(TAG,"PHOTO_REQUEST_CUT " + String.valueOf(bitmap.getAllocationByteCount()));
                    Log.d(TAG,"PHOTO_REQUEST_CUT " + String.valueOf(bitmap.getByteCount()));


              
                    

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }

    }

    private static final int PERMISSION_GRANTED = 1000;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case PERMISSION_GRANTED:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){

                    camera();
                }else {

                    ToastUtil.show(mContext,"您已取消授权，不能拍照");
                }
                break;

        }
    }

    private PopupWindow popupWindow;
    private static final int REQUEST_CODE_PICK_IMAGE = 201;
    private static final int REQUEST_CODE_CAPTURE_CAMEIA = 202;
    private static final int PHOTO_REQUEST_CUT = 203;
    private static final String CAPTURE_PIC_TEMP_PATH = "capture_picture_temp_path";
    private static final String CAPTURE_PIC_PATH = "capture_picture_pat";

    public void showSettingHeaderPic() {

        View view = LayoutInflater.from(mContext).inflate(R.layout.pw_select_pic, null);

        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new PaintDrawable());

        //选择照片
        view.findViewById(R.id.album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");//相片类型
                startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
                popupWindow.dismiss();

            }
        });

        //拍照
        view.findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                    camera();
                else {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                            ActivityCompat.requestPermissions(UserInfoActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_GRANTED);
                    }
                    else {
                        camera();
                    }

                }

            }
        });

        //取消
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, -view.getHeight());

    }

    private void camera() {

        File file = null;

        try {
            creatFolder();

            String filepath = imgFolderFile + File.separator + System.currentTimeMillis() + ".jpg";
            Sputils.put(mContext, CAPTURE_PIC_TEMP_PATH, filepath);

            Log.i(TAG, "CAPTURE_CAMEIA: put imgpath " + filepath);

            file = new File(filepath);

        } catch (Exception e) {
            Log.e(TAG, "CAPTURE_CAMEIA:  exception" + e.getMessage());
            e.printStackTrace();
        }

        Uri uri = getCameraContentUri(this, file);

        //调用系统的拍照权限进行拍照
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMEIA);

        popupWindow.dismiss();
    }

    //创建图片存放文件夹
    File imgFolderFile;
    private void creatFolder() {

        String imgFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + "SDK_Demo" + File.separator + "img";
        imgFolderFile = new File(imgFolder);

        if (!(imgFolderFile).exists()) {
            imgFolderFile.mkdirs();
        }

        Log.d(TAG,"CREAT_FOLDER length:" + imgFolderFile.length());
    }

    private static Uri getCameraContentUri(Context context, File file) {

        Uri uri = Uri.fromFile(file);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        }
        Log.d("File", "拍照保存到位置：" + uri.getPath());

        return uri;
    }

    String filepath;
    Uri uritempFile;
    private void cropPic(Uri uri) {
        Intent intent = new Intent();
        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");// Uri是已经选择的图片Uri
        intent.putExtra("return-data", true);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 3);// 裁剪框比例
        intent.putExtra("aspectY", 4);
        intent.putExtra("outputX", 240);// 输出图片大小 不能输出过大,否则小米手机会出现问题
        intent.putExtra("outputY", 320);
        System.out.println("-------------------------------------------cropPic");

        SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");
        String time = df.format(new Date());

        //裁剪后的图片Uri路径，uritempFile为Uri类变量
        filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + "SDK_Demo" + File.separator + "img" + File.separator
                + "IMG_" + time +".jpg";
        uritempFile  = Uri.parse("file://" + "/" + filepath);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, PHOTO_REQUEST_CUT);

    }

    public Uri getImageContentUri(File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);

        } else {

            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            } else {
                return null;
            }
        }
    }
}
