/*
 * BruceHurrican
 * Copyright (c) 2016.
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *    This document is Bruce's individual learning the android demo, wherein the use of the code from the Internet, only to use as a learning exchanges.
 *    And where any person can download and use, but not for commercial purposes.
 *    Author does not assume the resulting corresponding disputes.
 *    If you have good suggestions for the code, you can contact BurrceHurrican@foxmail.com
 *    本文件为Bruce's个人学习android的作品, 其中所用到的代码来源于互联网，仅作为学习交流使用。
 *    任和何人可以下载并使用, 但是不能用于商业用途。
 *    作者不承担由此带来的相应纠纷。
 *    如果对本代码有好的建议，可以联系BurrceHurrican@foxmail.com
 */

package com.bruce.demo;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bruce.demo.base.BaseActivity;
import com.bruce.demo.facebook.stetho.StethoDemoActivity;
import com.bruce.demo.mvp.view.MVPActivity;
import com.bruce.demo.social.qq.QQActivity;
import com.bruce.demo.social.sina.SinaActivity;
import com.bruce.demo.studydata.activities.floatwindow.FloatWindowActivity;
import com.bruce.demo.studydata.activities.ipc.IPCActivity;
import com.bruce.demo.studydata.activities.media.CameraActivity;
import com.bruce.demo.studydata.fragments.FragmentsActivity;
import com.bruce.demo.studydata.game.game2048.activity.GameActivity;
import com.bruce.demo.studydata.game.gamepuzzle.PuzzleActivity;
import com.bruce.demo.utils.KKReflect;
import com.bruce.demo.widget.AnimListView;
import com.bruce.demo.widget.TitleBar;
import com.bruceutils.utils.DataCleanManager;
import com.bruceutils.utils.LogUtils;
import com.bruceutils.utils.PublicUtil;
import com.bruceutils.utils.logdetails.LogDetails;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

//import com.TT;

/**
 * 主Activity
 * Created by BruceHurrican on 2015/5/24.
 */
public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener, Serializable, TitleBar.OnTitleBarClickListener {
    private static final long serialVersionUID = -3277762441808693645L;
    @Bind(R.id.titlebar)
    TitleBar titlebar;
    @Bind(R.id.alv_demo_list)
    AnimListView alv_demo_list;
    @Bind(R.id.bottomBar)
    RadioGroup bottomBar;
    @Bind(R.id.btn_favourite)
    RadioButton btn_favourite;
    @Bind(R.id.btn_search)
    RadioButton btn_search;
    @Bind(R.id.btn_pocket)
    RadioButton btn_pocket;
    @Bind(R.id.btn_mine)
    RadioButton btn_mine;
    private List<Class<? extends Activity>> demos;
    private List<String> demoNamesList;
    private Intent it;
    private NetWorkAvailableReceiver netWorkAvailableReceiver = new NetWorkAvailableReceiver();
//    TT tt = new TT();

    public static void sdfile2datadata(Context context) throws IOException {
        // 读取sdcard文件
        File sourceFile = Environment.getExternalStorageDirectory(); // 文件所在sd卡路径
        File fleDir = new File(sourceFile, "1a.txt"); // 源文件名称
        FileInputStream fis = new FileInputStream(fleDir);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] array = new byte[1024];
        int len = -1;
        while ((len = fis.read(array)) != -1) {
            bos.write(array, 0, len);
        }
        bos.close();
        fis.close();
        LogDetails.i(fleDir.getAbsolutePath());
        LogDetails.i(bos.toString());

        // 将读取到的信息写入新文件
        File targetFile = context.getDir("dex", Context.MODE_PRIVATE); // 新文件所在路径
        // data/data/包名/app_dex
        File tarFileDir = new File(targetFile, "1c.txt"); // 新文件名称
        FileOutputStream fos = new FileOutputStream(tarFileDir);
        fos.write(bos.toByteArray());
        fos.close();
        LogDetails.i(tarFileDir.getAbsolutePath());

//        File sourceFile2 = Environment.getExternalStorageDirectory();
        // 验证复制新文件是否写入成功
        File fleDir2 = new File(targetFile, "1c.txt");
        FileInputStream fis2 = new FileInputStream(fleDir2);
        ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
        byte[] array2 = new byte[1024];
        int len2 = -1;
        while ((len2 = fis2.read(array2)) != -1) {
            bos2.write(array2, 0, len2);
        }
        bos2.close();
        fis2.close();
        LogDetails.i(fleDir2.getAbsolutePath());
        LogDetails.i(bos2.toString());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        LogUtils.i("当前进程ID：" + android.os.Process.myPid());
        initContainer();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkAvailableReceiver, filter);
        titlebar.setOnTitleBarClickListener(this);
        bottomBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.btn_favourite:
                        LogUtils.d("收藏按钮被点击");
                        break;
                    case R.id.btn_search:
//                        TT.show();
                        LogUtils.d("搜索按钮被点击");
                        try {
                            accessBlueModule();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.btn_pocket:
//                        tt.showTxt(true, MainActivity.this);
                        LogUtils.d("钱包按钮被点击");
                        try {
                           sdfile2datadata(MainActivity.this);
                        } catch (IOException e) {
                            LogDetails.e(e.toString());
                        }
                        break;
                    case R.id.btn_mine:
//                        tt.showTxt(false, MainActivity.this);
//                        startActivity(new Intent(MainActivity.this, LibActiviy.class));
                        LogUtils.d("我的按钮被点击");
                        break;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(netWorkAvailableReceiver);
        ButterKnife.unbind(this);
        super.onDestroy();
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (alv_demo_list.getVisibility() == View.VISIBLE) {
            btn_favourite.setChecked(true);
        }
    }

    @Override
    public String getTAG() {
        return "MainActivity -- >";
    }

    private void accessBlueModule() throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        // 反射工具类方法访问
        KKReflect reflect = KKReflect.on("com.bruce.demo.BlueManager");
        LogDetails.d(reflect.get());
        LogDetails.d(reflect.getClass());
        LogDetails.d(reflect.getClass().getMethods());
        LogDetails.d(reflect.fields().containsKey("instance"));
        reflect.call("getInstance", null).call("init", null).call("accessBlueActivity", this);
        // 传统反射方法访问
//        Class<?> blueClass = Class.forName("com.bruce.demo.BlueManager");
//        LogDetails.d(blueClass.getMethods());
//        Method instanceMethod = blueClass.getMethod("getInstance");
//        instanceMethod.invoke(null);
//        Method initMethod = blueClass.getDeclaredMethod("init");
//        initMethod.invoke(instanceMethod.invoke(null));
//        Method accessBlueActivity = blueClass.getDeclaredMethod("accessBlueActivity", Activity
//                .class);
//        accessBlueActivity.invoke(instanceMethod.invoke(null), this);
    }

    /**
     * 初始化demo容器
     */
    private void initContainer() {
        demos = new ArrayList<>(5);
        demoNamesList = new ArrayList<>(5);
//        ListView lv_demo_list = (ListView) findViewById(R.id.lv__demo_list);
//        lv_demo_list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, demoNamesList));
        alv_demo_list.setAdapter(new ArrayAdapter<>(this, R.layout.main_item, demoNamesList));
        it = new Intent();

        addDemoContainer(FragmentsActivity.class, "fragment 入口");
        addDemoContainer(IPCActivity.class, "ipc 入口");
        addDemoContainer(FloatWindowActivity.class, "悬浮窗");
        addDemoContainer(QQActivity.class, "QQ 登录分享 QQ空间分享");
        addDemoContainer(SinaActivity.class, "新浪微博 登录分享");
        addDemoContainer(CameraActivity.class, "调用系统相机相册获取相片");
        addDemoContainer(GameActivity.class, "2048游戏");
        addDemoContainer(StethoDemoActivity.class, "stetho demo");
        addDemoContainer(MVPActivity.class, "mvp demo");
        addDemoContainer(PuzzleActivity.class, "拼图游戏");

        alv_demo_list.setOnItemClickListener(this);
        LogUtils.i("加载列表完成");
        LogDetails.getLogConfig().configShowBorders(true);
        LogDetails.i("当前线程: " + Thread.currentThread().getName());
    }

    /**
     * 增加demo
     *
     * @param cls  demo class
     * @param name demo 名称
     */
    private void addDemoContainer(Class<? extends Activity> cls, String name) {
        demos.add(cls);
        demoNamesList.add(name);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(MainActivity.this, "你点击了第" + (position + 1) + "条Demo", Toast.LENGTH_SHORT).show();
//        showToastShort("你点击了第" + (position + 1) + "条Demo--" + demoNamesList.get(position));
        showToastShort(String.format("你点击了第 %s 条Demo %s", position + 1, demoNamesList.get(position)));
        it.setClass(MainActivity.this, demos.get(position));
//        Logs.i(TAG, "你点击了第" + (position + 1) + "条Demo--"+demoNamesList.get(position));
        LogUtils.i(String.format("你点击了第 %s 条Demo %s", position + 1, demoNamesList.get(position)));
        LogUtils.i("当前线程为 -->" + Thread.currentThread());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(it, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
        } else {
            startActivity(it);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.testmenu1:
                LogUtils.d(item.getTitle() + " 菜单按钮被点击");
                LogUtils.i("隐式启动 Intent");
                startActivity(new Intent("START_FRAGMENT_ACTIVITY"));
                break;
            case R.id.testmenu2:
                LogUtils.d(item.getTitle() + " 菜单按钮被点击");
                LogUtils.i("本机DPI：" + getResources().getDisplayMetrics().densityDpi + "\nXDPI：" + getResources().getDisplayMetrics().xdpi + "\nYDPI：" + getResources().getDisplayMetrics().ydpi);
                showToastShort("本机DPI：" + getResources().getDisplayMetrics().densityDpi + "\nXDPI：" + getResources().getDisplayMetrics().xdpi + "\nYDPI：" + getResources().getDisplayMetrics().ydpi);
                break;
            case R.id.menu_3:
                try {
                    showToastShort("缓存文件大小->" + DataCleanManager.getFormatSize(this, getCacheDir()));
//                    LogUtils.i("缓存文件大小->" + DataCleanManager.getFileSize(getCacheDir())
//                            + "\n数据文件大小->" + DataCleanManager.getFileSize(getFilesDir())
//                            + "\nsharePreference大小->" + DataCleanManager.getFileSize(new File("/data/data/" + getPackageName() + "/shared_prefs"))
//                            + "\nSD卡中缓存文件大小->" + DataCleanManager.getFileSize(getExternalCacheDir())
//                            + "\nSD卡中数据文件大小->" + DataCleanManager.getFileSize(getExternalFilesDir(Environment.DIRECTORY_PICTURES))
//                            + "\n整个应用数据文件大小->" + DataCleanManager.getFileSize(new File("/data/data/" + getPackageName())));
                    LogUtils.i("缓存文件大小->" + DataCleanManager.getFormatSize(this, getCacheDir()) + "\n数据文件大小->" + DataCleanManager.getFormatSize(this, getFilesDir()) + "\nsharePreference大小->" + DataCleanManager.getFormatSize(this, new File("/data/data/" + getPackageName() + "/shared_prefs")) + "\nSD卡中缓存文件大小->" + DataCleanManager.getFormatSize(this, getExternalCacheDir()) + "\nSD卡中数据文件大小->" + DataCleanManager.getFormatSize(this, getExternalFilesDir(Environment.DIRECTORY_PICTURES)) + "\n整个应用数据文件大小->" + DataCleanManager.getFormatSize(this, new File("/data/data/" + getPackageName())));
                } catch (Exception e) {
                    LogUtils.e(e.toString());
                }
                break;
            case R.id.menu_4:
                showToastShort("清除缓存完毕");
                DataCleanManager.cleanApplicationData(MainActivity.this, "/data/data/" + getPackageName());
                break;
            case R.id.menu_5:
                try {
                    PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
                    LogUtils.i("应用名称：" + info.versionName + "\n应用版本：" + info.versionCode + "\n应用包名：" + info.packageName);
                    showToastShort("应用名称：" + info.versionName + "\n应用版本：" + info.versionCode + "\n应用包名：" + info.packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    LogUtils.e(e.toString());
                }
                break;
        }
        return true;
    }

    @OnCheckedChanged({R.id.btn_favourite, R.id.btn_search, R.id.btn_pocket, R.id.btn_mine})
    public void bottomBarBtnTxtPressed(CompoundButton buttonView, boolean isChecked) {
        LogUtils.d("buttonView:" + buttonView + "\nisChecked:" + isChecked);
        switch (buttonView.getId()) {
            case R.id.btn_favourite:
                btn_favourite.setTextColor(getResources().getColor(isChecked ? R.color
                        .bottombartxt_pressed : R.color.bottombartxt_unpressed));
                break;
            case R.id.btn_search:
                btn_search.setTextColor(getResources().getColor(isChecked ? R.color
                        .bottombartxt_pressed : R.color.bottombartxt_unpressed));
                break;
            case R.id.btn_pocket:
                btn_pocket.setTextColor(getResources().getColor(isChecked ? R.color
                        .bottombartxt_pressed : R.color.bottombartxt_unpressed));
                break;
            case R.id.btn_mine:
                btn_mine.setTextColor(getResources().getColor(isChecked ? R.color
                        .bottombartxt_pressed : R.color.bottombartxt_unpressed));
                break;
        }

    }

    @Override
    public void onLeftBtnClick() {
        LogUtils.d("左侧按钮被点击");
        String channelName = PublicUtil.getChannelName(this);
        LogUtils.i("channelName:" + channelName);
        showToastShort("channelName:" + channelName);
        if (channelName.equals("tt") || BuildConfig.DEBUG) {
            try {
                Class<?> performanceClass = Class.forName("android.support.v7.widget.RecyclerView");
                LogUtils.i(performanceClass.getCanonicalName());
//                Method method = performanceClass.getMethod("init", Application.class);
//                method.invoke(performanceClass, MainActivity.this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRightBtnClick() {
        LogUtils.d("右侧按钮被点击");
    }

    private class NetWorkAvailableReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.i("网络状态发生改变 ");
            if (PublicUtil.isNetWorkAvailable(MainActivity.this)) {
                LogUtils.d("当前设备已经联网");
            } else {
                showToastShort("亲的网络不给力啊╮(╯3╰)╭");
            }
        }
    }
}
