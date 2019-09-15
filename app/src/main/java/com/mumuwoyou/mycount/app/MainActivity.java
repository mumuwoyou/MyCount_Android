package com.mumuwoyou.mycount.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.king.zxing.Intents;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import com.mumuwoyou.mycount.app.adapter.StockAdapter;
import com.mumuwoyou.mycount.app.dbmodel.DetailModel;
import com.mumuwoyou.mycount.app.dbmodel.StockModel;
import com.mumuwoyou.mycount.app.util.FileUtils;

import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //请求状态码

    private static final int REQUEST_PERMISSION_CODE = 1;


    private  static  String DB_NAME= "testdb.db";

    private static String packageName = "com.mumuwoyou.mycount.app";

    public static final String KEY_TITLE = "key_title";
    public static final String KEY_IS_QR_CODE = "key_code";
    public static final String KEY_IS_CONTINUOUS = "key_continuous_scan";

    public static final int REQUEST_CODE_SCAN = 0X01;
    public static final int REQUEST_CODE_PHOTO = 0X02;


    public static final int RC_CAMERA = 0X01;

    public static final int RC_READ_PHOTO = 0X02;

    private ListView lv_stock;
    private ArrayList<StockModel> stockModels;
    private StockAdapter adapter;

    private Class<?> cls;
    private String title;
    private boolean isContinuousScan;

    private MyBroadcastReceiver myBroadCastReceiver;

    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        lv_stock = findViewById(R.id.lv_stock);
        stockModels = new ArrayList<>();
        adapter = new StockAdapter(stockModels);
        lv_stock.setAdapter(adapter);

        refreshData();

        lv_stock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                StockModel stock = (StockModel)parent.getAdapter().getItem(position);
//                Toast.makeText(MainActivity.this,stock.getName(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("code", stock.getCode());
                intent.setClass(MainActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });

        myBroadCastReceiver= new MyBroadcastReceiver();
        IntentFilter intentFiltet = new IntentFilter();
        //设置广播的名字（设置Action，可以添加多个要监听的动作）
        intentFiltet.addAction("BROADCAST_ACTION_PC_PUSHED");
        intentFiltet.addAction("BROADCAST_ACTION_PC_PULLING");
        intentFiltet.addAction("BROADCAST_ACTION_PC_PULLED");
        //注册广播,传入两个参数， 实例化的广播接受者对象，intent 动作筛选对象
        registerReceiver(myBroadCastReceiver,intentFiltet);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();//刷新数据
    }



    private void refreshData() {
        List<StockModel> allstock = LitePal.findAll(StockModel.class);

        if (allstock.size() > 0) {
            stockModels.clear();
            stockModels.addAll(allstock);
            adapter.notifyDataSetChanged();
            adapter.notifyDataSetChanged();
        }

    }



    private class MyBroadcastReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "收到自定义的广播", 1).show();
            if (intent.getAction().equals("BROADCAST_ACTION_PC_PUSHED")) {
                DownloadData();
            }
            if (intent.getAction().equals("BROADCAST_ACTION_PC_PULLING")) {
                UploadData();
            }
            if (intent.getAction().equals("BROADCAST_ACTION_PC_PULLED")) {
                Toast.makeText(context, "盘点数据已上传", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void DownloadData()
    {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }else{
                builder=new AlertDialog.Builder(this);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle(R.string.update);
                builder.setMessage("确定已上传盘点数据，要更新数据库吗？");

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (copyDbFromPublicDirectory()) {
                            Toast.makeText(getContext(), "更新数据库完成", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getContext(), "更新数据库失败", Toast.LENGTH_SHORT).show();
                        }
                        refreshData();
                    }

                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), "已取消更新数据库", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setCancelable(true);
                AlertDialog dialog=builder.create();
                dialog.show();

            }
        }
    }

    private void UploadData(){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }else{
                if (copyDbToPublicDirectory()) {
                    Toast.makeText(this, "Adb上传数据准备就绪", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Adb上传数据准备失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void onClick(View v) {
        isContinuousScan = false;
        switch (v.getId()) {
            case R.id.btn_scan:
                this.cls = CustomCaptureActivity.class;
                this.title = ((Button) v).getText().toString();
                checkCameraPermissions();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data!=null){
            switch (requestCode){
                case REQUEST_CODE_SCAN:
                    String result = data.getStringExtra(Intents.Scan.RESULT);
                    //Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
                    List<StockModel> stockList = LitePal.select("code").where("barcode = ?", result).find(StockModel.class);
                    if (stockList.size() > 0)
                    {
                        Intent intent = new Intent();
                        intent.putExtra("code", stockList.get(0).getCode());
                        intent.setClass(MainActivity.this, DetailActivity.class);
                        startActivity(intent);
                    }
                    break;
            }

        }
    }

    private Context getContext(){
        return this;
    }





    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        // ...
    }

    /**
     * 检测拍摄权限
     */
    @AfterPermissionGranted(RC_CAMERA)
    private void checkCameraPermissions(){
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {//有权限
            startScan(cls,title);
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.permission_camera),
                    RC_CAMERA, perms);
        }
    }


    /**
     * 扫码
     * @param cls
     * @param title
     */
    private void startScan(Class<?> cls,String title){
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(this,R.anim.in,R.anim.out);
        Intent intent = new Intent(this, cls);
        intent.putExtra(KEY_TITLE,title);
        intent.putExtra(KEY_IS_CONTINUOUS,isContinuousScan);
        ActivityCompat.startActivityForResult(this,intent,REQUEST_CODE_SCAN,optionsCompat.toBundle());
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_place:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PlaceActivity.class);
                startActivity(intent);
                break;
//            case R.id.menu_updatedb:
//                DownloadData();
//                break;
//            case R.id.menu_uploaddb:
//                UploadData();
//                break;




        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);



        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
//                    if (copyDbToPublicDirectory()) {
//                        Toast.makeText(this, "Adb上传数据准备就绪", Toast.LENGTH_SHORT).show();
//                    }else{
//                        Toast.makeText(this, "Adb上传数据准备失败", Toast.LENGTH_SHORT).show();
//                    };

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    /**
     * 拷贝数据库db文件到外部的public文件夹内
     */
    public boolean copyDbToPublicDirectory() {
        boolean success = false;
        try {
            //获取db文件
            String dbDirPath = "/data/data/" + packageName
                    + "/databases/";

            File dir = new File(dbDirPath);
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        if (file.getName().endsWith("db")) {


                            //拷贝文件方式
                            Log.i("hf", "获取到数据库文件" + file.getName());
                            //拷贝这个文件到外部共享文档文件夹下
                            File documentDir = Environment.getExternalStorageDirectory();
                            Log.i("hf", "获取到SDka" + documentDir.getAbsolutePath());
                            String saveDir = documentDir.getAbsolutePath() + "/" + packageName + "/" + "dbfiles";
                            File f_saveDir = new File(saveDir);
                            if (!f_saveDir.exists()) {
                                f_saveDir.mkdirs();
                            }
                            String saveFile = saveDir + "/" + file.getName();
                            Log.i("hf", "拷贝文件的路径:" + saveFile);
                            File f_saveFile = new File(saveFile);
                            if (!f_saveFile.exists()) {
                                f_saveFile.createNewFile();
                            }
                            //拷贝文件
                            Log.i("hf", "开始拷贝文件");
                            FileUtils.copyFileUsingFileChannels(file, f_saveFile);
                            Log.i("hf", "拷贝文件成功");
                        }
                    }
                    success = true;
                }

            }
        } catch (Exception ex) {
            Log.e("hf", ex.getMessage());
        }
        return success;
    }

    /**
     * 从外部的public目录下拷贝db文件
     */
    public boolean copyDbFromPublicDirectory() {
        boolean success = false;

        try {
            LitePal.deleteDatabase("testdb");
            File documentDir = Environment.getExternalStorageDirectory();
            //pc端推送的地址
            String saveDir = documentDir.getAbsolutePath() + "/" + packageName + "/" + "dbfiles" + "/" + DB_NAME;
            ;
            //应用内db文件路径
            String dbDirPath = "/data/data/" + packageName
                    + "/databases/" + DB_NAME;

            File originFile = new File(saveDir);
            if (!originFile.exists()) {
                Log.i("hf", "pc推送的文件不存在");
//                Toast.makeText(this,"pc推送的文件不存在",Toast.LENGTH_LONG).show();
                return success;
            }

            File f_saveFile = new File(dbDirPath);
            //拷贝文件
            FileUtils.copyFileUsingFileChannels(originFile, f_saveFile);
            success = true;
        } catch (Exception ex) {
            Log.e("hf", ex.getMessage());
        }
        return success;
    }
}
