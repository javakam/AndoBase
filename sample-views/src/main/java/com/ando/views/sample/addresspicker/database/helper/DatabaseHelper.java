package com.ando.views.sample.addresspicker.database.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.ando.views.sample.addresspicker.dao.DaoUtils;
import com.ando.views.sample.addresspicker.FileUtils;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Title:DatabaseHelper
 * <p>
 * Description:数据库工具类
 * </p>
 * Author Jming.LogUtils
 * Date 2017/8/29 15:02
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "ando-views.db";
    private static final int version = DaoUtils.getInstance().getDatabaseVersion();
    private static final  String DATA = "data";

    private static DatabaseHelper helper;
    private static String path = DATABASE_NAME;
    private Map<String, Dao> daoMap;
    private List<Class<?>> clazzList;

    private DatabaseHelper(Context context) {
        super(context, path, null, version);
        daoMap = new HashMap<>();
        clazzList = DaoUtils.getInstance().getDatabaseBeans();
    }

    public static DatabaseHelper initDatabaseHelper(Context context) {
        boolean available = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        SQLiteDatabase db = null;
        if (available) {
            String dirFile = FileUtils.getFilesPath(context, DATA) + DATABASE_NAME;
            db = SQLiteDatabase.openOrCreateDatabase(dirFile, null);
        } else {
            Log.e(TAG, "SdCard is not useful!");
        }
        return initDatabaseHelper(context, db);
    }

    public static DatabaseHelper initDatabaseHelper(Context context, SQLiteDatabase database) {
        if (helper == null) {
            synchronized (DatabaseHelper.class) {
                if (helper == null) {
                    helper = new DatabaseHelper(context);
                }
            }
        }

        //不存在时使用默认的SQLiteDatabase
        if (database != null) {
            File file = new File(database.getPath());
            if (file.canRead() && file.canWrite()) {
                path = database.getPath();
                if (file.exists()) {
                    helper.onUpgrade(database, database.getVersion(), version);
                } else {
                    helper.onCreate(database);
                }
            } else {
                Log.e(TAG, file.getAbsolutePath() + " can't write or read!");
            }
            database.close();
        }
        return helper;
    }

    public static DatabaseHelper getInstance() {
        if (helper == null) {
            Log.e(TAG, "DatabaseHelper is null");
        }
        return helper;
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase database;
        if (TextUtils.equals(path, DATABASE_NAME)) {
            database = super.getWritableDatabase();
        } else {
            database = SQLiteDatabase.openDatabase(path, null,
                    SQLiteDatabase.OPEN_READWRITE);
        }
        return database;
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase database;
        if (TextUtils.equals(path, DATABASE_NAME)) {
            database = super.getReadableDatabase();
        } else {
            database = SQLiteDatabase.openDatabase(path, null,
                    SQLiteDatabase.OPEN_READONLY);
        }
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase,
                         ConnectionSource connectionSource) {
        try {
            for (Class<?> clazz : clazzList) {
                TableUtils.createTableIfNotExists(connectionSource, clazz);
            }
        } catch (SQLException e) {
            Log.e(TAG, "onCreate error", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase,
                          ConnectionSource connectionSource, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            try {
                for (Class<?> clazz : clazzList) {
                    TableUtils.dropTable(connectionSource, clazz, true);// 会话消息
                }
                onCreate(sqliteDatabase, connectionSource);
                sqliteDatabase.setVersion(newVersion);
            } catch (SQLException e) {
                Log.e(TAG, "onUpgrade error", e);
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <D extends Dao<T, ?>, T> D getDao(Class<T> clazz) throws SQLException {
        Dao<T, ?> dao = null;
        String simpleName = clazz.getSimpleName();
        if (daoMap.containsKey(simpleName)) {
            dao = daoMap.get(simpleName);
        }
        if (dao == null) {
            dao = super.getDao(clazz);
            daoMap.put(simpleName, dao);
        }
        return (D) dao;
    }

    @Override
    public void close() {
        super.close();
        daoMap.clear();
    }
}
