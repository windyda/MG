package com.maplefall.wind.mg.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.maplefall.wind.mg.bean.User;
import com.maplefall.wind.mg.bean.Note;

import java.util.ArrayList;

/**
 * 数据库操作类
 * 提供增、删、改、查操作
 */

public class DBHelper {

    private Context mContext;
    private SQLiteDatabase mSqlDB;
    private OpenHelper mHelper;
    private String DB_NAME = "mygarden.db";
    private String TABLE_NAME = "MGNote";
    private static int mVersion = 1;
    private final int maxNoteRecord = 5;  // 获取最大笔记数量;

    public DBHelper(Context context) {
        mContext = context;
        mHelper = new OpenHelper(mContext);
        mSqlDB = mHelper.getWritableDatabase();
    }

    /**
     * 判断可添加笔记后，数据库类调用 FileOperationUtil 类添加笔记，
     * 并在数据库中添加记录，以此提供统一的添加笔记接口
     *
     * @param note 要保存的笔记
     */
    public void addNote(final Note note) {
        Cursor cursor = mSqlDB.rawQuery("SELECT * FROM " + TABLE_NAME + " where phone=? order by noteTime desc",
                new String[]{User.mgUser.getPhone()});
        if (cursor.moveToFirst()) {
            final String fileName = cursor.getString(cursor.getColumnIndex("fileName"));
            int count = cursor.getInt(cursor.getColumnIndex("noteCount"));
            String noteTime = cursor.getString(cursor.getColumnIndex("noteTime"));
            if (count < maxNoteRecord) {
                ContentValues values = new ContentValues();
                values.put("noteCount", ++count);
                values.put("noteTime", noteTime + "," + note.getTime());
                String whereClause = "fileName = ?";
                String[] whereArgs = {fileName};
                mSqlDB.update(TABLE_NAME, values, whereClause, whereArgs);
//                mSqlDB.execSQL("update " + TABLE_NAME + " set noteCount = ?, noteTime = ? where fileName = ?",
//                        new String[]{++count + "", noteTime + "," + note.getTime(), fileName});

                writeNote(note, fileName);
            } else {
                mSqlDB.execSQL("insert into " + TABLE_NAME + " values (?, ?, ?, ?)",
                        new String[]{User.mgUser.getPhone(), note.getTime(), 1 + "", note.getTime()});
                writeNote(note, note.getTime()); // 新文件用时间戳命名
            }
        } else {
            mSqlDB.execSQL("insert into " + TABLE_NAME + " values (?, ?, ?, ?)",
                    new String[]{User.mgUser.getPhone(), note.getTime(), 1 + "", note.getTime()});
            writeNote(note, note.getTime()); // 新文件用时间戳命名
        }
        cursor.close();
    }

    private void writeNote(final Note note, final String fileName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new NoteFileHelper(mContext).writeFile2SD(note, fileName);
            }
        }).start();
    }

    /**
     * 删除 note 时，只是标记，不做删除操作
     *
     * @param keyTime 查询关键字段为时间值
     */
    public void deleteNote(String keyTime) {
        Cursor cursor = mSqlDB.rawQuery("SELECT * FROM " + TABLE_NAME + " where phone=?", new String[]{User.mgUser.getPhone()});
        if (cursor.moveToFirst()) {
            do {
                String fileName = cursor.getString(cursor.getColumnIndex("fileName"));
                String timestamps = cursor.getString(cursor.getColumnIndex("noteTime"));
                if (timestamps.contains(keyTime)) {
                    StringBuilder builder = new StringBuilder(timestamps);
                    builder.insert(timestamps.indexOf(keyTime), "*");  // 添加 * 表示标记为已删除笔记
                    mSqlDB.execSQL("UPDATE " + TABLE_NAME + " SET noteTime = ? WHERE fileName = ?",
                            new String[]{builder.toString(), fileName});
                    break;
                }
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
    }

    public ArrayList<Note> getAllNote() {
        ArrayList<Note> noteList = new ArrayList<>();

        Cursor cursor = mSqlDB.rawQuery("SELECT * FROM " + TABLE_NAME + " where phone=?", new String[]{User.mgUser.getPhone()});
        if (cursor.moveToFirst()) {
            NoteFileHelper fileHelper = new NoteFileHelper(mContext);
            do {
                String fileName = cursor.getString(cursor.getColumnIndex("fileName"));
                int count = cursor.getInt(cursor.getColumnIndex("noteCount"));
                String noteTime = cursor.getString(cursor.getColumnIndex("noteTime"));
                ArrayList<Note> notePartList = fileHelper.readNoteFile(fileName, count, noteTime);
                if (notePartList != null) {
                    noteList.addAll(notePartList);
                }
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return noteList;
    }

    private class OpenHelper extends SQLiteOpenHelper {

        private OpenHelper(Context context) {
            super(context, DB_NAME, null, mVersion);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + TABLE_NAME + "(" +
                    "phone text," + // 用户手机号为唯一标识
                    "fileName text primary key," +  // 以时间戳作为文件名，作为数据库的 key 值
                    "noteCount integer," +  // 笔记数量
                    "noteTime text)");  // 笔记的 key 值，以英文逗号分隔
        }

        public void onUpgrade(SQLiteDatabase db, int newVersion, int oldVersion) {
            db.execSQL("drop table if exists " + TABLE_NAME);
            onCreate(db);
        }
    }
}
