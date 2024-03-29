package com.ando.views.floatwindow.platform;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.ando.views.floatwindow.FloatPermission;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.ando.views.floatwindow.FloatWindow.FloatLifecycle;

/**
 * <p>
 * 一个MIUI版本对应小米各种机型，基于不同的安卓版本，但是权限设置页跟MIUI版本有关
 * 测试TYPE_TOAST类型：
 * 7.0：
 * 小米      5        MIUI8         -------------------- 失败
 * 小米   Note2       MIUI9         -------------------- 失败
 * 6.0.1
 * 小米   5                         -------------------- 失败
 * 小米   红米note3                  -------------------- 失败
 * 6.0：
 * 小米   5                         -------------------- 成功
 * 小米   红米4A      MIUI8         -------------------- 成功
 * 小米   红米Pro     MIUI7         -------------------- 成功
 * 小米   红米Note4   MIUI8         -------------------- 失败
 * <p>
 * 经过各种横向纵向测试对比，得出一个结论，就是小米对TYPE_TOAST的处理机制毫无规律可言！
 * 跟Android版本无关，跟MIUI版本无关，addView方法也不报错
 * 所以最后对小米6.0以上的适配方法是：不使用 TYPE_TOAST 类型，统一申请权限
 */
public class Miui {
    private static final String TAG = "FloatWindow";

    private static final String FACTORY = "Xiaomi";
    private static final String MIUI = "ro.miui.ui.version.name";
    private static final String MIUI_5 = "V5";
    private static final String MIUI_6 = "V6";
    private static final String MIUI_7 = "V7";
    private static final String MIUI_8 = "V8";
    private static final String MIUI_9 = "V9";
    private static List<FloatPermission.PermissionListener> mPermissionListenerList;
    private static FloatPermission.PermissionListener mPermissionListener;

    public static boolean rom() {
        return Build.MANUFACTURER.equals(FACTORY);
    }

    private static String getProp() {
        return Rom.getRomProp(MIUI);
    }

    /**
     * Android6.0以下申请权限
     */
    public static void req(final Context context, FloatPermission.PermissionListener permissionListener) {
        if (FloatPermission.hasPermission(context)) {
            permissionListener.onSuccess();
            return;
        }
        if (mPermissionListenerList == null) {
            mPermissionListenerList = new ArrayList<>();
            mPermissionListener = new FloatPermission.PermissionListener() {
                @Override
                public void onSuccess() {
                    for (FloatPermission.PermissionListener listener : mPermissionListenerList) {
                        listener.onSuccess();
                    }
                    mPermissionListenerList.clear();
                }

                @Override
                public void onFail() {
                    for (FloatPermission.PermissionListener listener : mPermissionListenerList) {
                        listener.onFail();
                    }
                    mPermissionListenerList.clear();
                }
            };
            req_(context);
        }
        mPermissionListenerList.add(permissionListener);
    }


    private static void req_(final Context context) {
        switch (getProp()) {
            case MIUI_5:
                reqForMiui5(context);
                break;
            case MIUI_6:
            case MIUI_7:
                reqForMiui67(context);
                break;
            case MIUI_8:
            case MIUI_9:
                reqForMiui89(context);
                break;
            default:
                break;
        }
        FloatLifecycle.setResumedListener(new FloatLifecycle.ResumedListener() {
            @Override
            public void onResumed() {
                if (FloatPermission.hasPermission(context)) {
                    mPermissionListener.onSuccess();
                } else {
                    mPermissionListener.onFail();
                }
            }
        });
    }


    private static void reqForMiui5(Context context) {
        String packageName = context.getPackageName();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Rom.isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            Log.e(TAG, "intent is not available!");
        }
    }

    private static void reqForMiui67(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter",
                "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Rom.isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            Log.e(TAG, "intent is not available!");
        }
    }

    private static void reqForMiui89(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Rom.isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setPackage("com.miui.securitycenter");
            intent.putExtra("extra_pkgname", context.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Rom.isIntentAvailable(intent, context)) {
                context.startActivity(intent);
            } else {
                Log.e(TAG, "intent is not available!");
            }
        }
    }

    /**
     * 有些机型在添加TYPE-TOAST类型时会自动改为TYPE_SYSTEM_ALERT，通过此方法可以屏蔽修改
     * 但是...即使成功显示出悬浮窗，移动的话也会崩溃
     */
    private static void addViewToWindow(WindowManager wm, View view, WindowManager.LayoutParams params) {
        setMiUI_International(true);
        wm.addView(view, params);
        setMiUI_International(false);
    }

    private static void setMiUI_International(boolean flag) {
        try {
            Class BuildForMi = Class.forName("miui.os.Build");
            Field isInternational = BuildForMi.getDeclaredField("IS_INTERNATIONAL_BUILD");
            isInternational.setAccessible(true);
            isInternational.setBoolean(null, flag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
