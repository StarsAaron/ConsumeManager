package com.rujian.consumemanager.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rujian.consumemanager.Myapplication;
import com.rujian.consumemanager.R;
import com.rujian.consumemanager.db.dao.RecordDao;
import com.rujian.consumemanager.ui.fragment.CalendarFragment;
import com.rujian.consumemanager.util.FileUtil;
import com.rujian.consumemanager.view.MyDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhrjian on 2015/11/16.
 */
public class SettingActivity extends BaseHeadActivity implements View.OnClickListener {
    private static final String databaseName = "recordDB.db";
    private Myapplication myapplication;
    private Button btn_about;
    private RelativeLayout rl_resetgesture, rl_backup, rl_restore,rl_clean_lib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_setting);
        myapplication = (Myapplication) getApplication();
        myapplication.addActivity(this);
        registView();
        initView();
    }

    private void registView() {
        setTitle("设置");
        showBackButton();
        setLeftImageViewOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_about = (Button) findViewById(R.id.btn_about);
        rl_resetgesture = (RelativeLayout) findViewById(R.id.rl_resetgesture);
        rl_backup = (RelativeLayout) findViewById(R.id.rl_backup);
        rl_restore = (RelativeLayout) findViewById(R.id.rl_restore);
        rl_clean_lib = (RelativeLayout) findViewById(R.id.rl_clean_lib);
    }

    private void initView() {
        btn_about.setOnClickListener(this);
        rl_resetgesture.setOnClickListener(this);
        rl_backup.setOnClickListener(this);
        rl_restore.setOnClickListener(this);
        rl_clean_lib.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_about:
                StringBuffer message = new StringBuffer();
                message.append("作者：Aaron\n邮箱：103514303@qq.com\n版本：V");
                String version = null;
                try {
                    version = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                message.append(version);
                message.append("\n如果有什么意见可以给我发邮件哦！我会做得更好！");
                MyDialog myDialog = new MyDialog(SettingActivity.this, "关于", message.toString(), false, null);
                myDialog.showDialog();
                break;
            case R.id.rl_resetgesture:
                Intent intent = new Intent(SettingActivity.this, ResetGestureLockActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_backup: //备份数据库
                String path = "";
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    path = Environment.getExternalStorageDirectory() + File.separator + databaseName;
                } else {
                    path = "/storage/extSdCard/" + databaseName;
                }
                final String path2 = path;
                MyDialog myDialog4 = new MyDialog(SettingActivity.this, "备份", "确定要备份到：\n" + path2, true, new MyDialog.AfterClickBtn() {
                    @Override
                    public void clickOk() {
                        FileUtil fileUtil = new FileUtil(SettingActivity.this);
                        if (fileUtil.backupDatabase(databaseName, path2)) {
                            Toast.makeText(getApplicationContext(), "备份成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "备份失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void clickConcel() {
                    }
                });
                myDialog4.showDialog();
                break;
            case R.id.rl_restore: //恢复数据库
                //打开文件浏览器
                Intent intent2 = new Intent();
                intent2.setAction(Intent.ACTION_GET_CONTENT);
                Uri startDir = Uri.fromFile(new File("/sdcard"));
                intent2.setDataAndType(startDir, "vnd.android.cursor.dir/lysesoft.andexplorer.file");
                startActivityForResult(intent2, 0x122);
                break;
            case R.id.rl_clean_lib: //清空数据库
                MyDialog myDialog3 = new MyDialog(SettingActivity.this, "清空", "确定要清空数据记录吗？", true, new MyDialog.AfterClickBtn() {
                    @Override
                    public void clickOk() {
                        RecordDao recordDao = new RecordDao(SettingActivity.this);
                        if (recordDao.deleteAll()) {
                            Toast.makeText(SettingActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SettingActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void clickConcel() {
                    }
                });
                myDialog3.showDialog();
                break;
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // 打印文本
        if (requestCode == 0x122) {
            if (resultCode == RESULT_OK) {
                Uri uri = intent.getData();
                if (uri != null) {
                    String path = uri.toString();
                    path = path.substring(path.indexOf("/storage"), path.length());
                    FileUtil fileUtil = new FileUtil(SettingActivity.this);
                    if(fileUtil.restoteDatabase(databaseName, path)){
                        Intent intent3 = new Intent();
                        intent3.setAction("com.rujian.consumemanager.mydataChangeReceiver.datechange");
                        sendBroadcast(intent3);
                        Toast.makeText(getApplicationContext(),"恢复成功",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"恢复失败",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
