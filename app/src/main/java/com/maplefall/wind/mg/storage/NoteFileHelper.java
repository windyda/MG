package com.maplefall.wind.mg.storage;

import android.content.Context;
import android.os.Environment;

import com.maplefall.wind.mg.R;
import com.maplefall.wind.mg.bean.Note;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 定制式的文件读写操作文件
 * 虽然是 Util 类，但读写操作只针对笔记外存读取
 */

public class NoteFileHelper {

    private String mAppFile;
    private Context mContext;
    private boolean couldEXStorage;

    public NoteFileHelper(Context context) {
        mContext = context;
        mAppFile = mContext.getString(R.string.external_folder);
        couldEXStorage = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        createRootFolder();
    }

    public void createRootFolder() {
        if (couldEXStorage) {
            try {
                File folderName = new File(Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + mAppFile);
                if (!folderName.exists()) {
                    folderName.mkdir();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeFile2SD(Note note, String fileName) {
        try {
            if (couldEXStorage) {
                String file_name = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + mAppFile + "/" + fileName + ".txt";
                File writeFile = new File(file_name);
                if (!writeFile.exists()) {
                    writeFile.createNewFile();
                }
                BufferedWriter writer = new BufferedWriter(new FileWriter(writeFile, true)); // 以 append 方式写入
                writer.write(mContext.getString(R.string.note_dividing_start));
                writer.write(tagContent(note.getTitle(), "title"));
                writer.write(tagContent(note.getTime(), "time"));
                writer.write(tagContent(note.getContent(), "content"));
                writer.write(tagContent(note.getDictum(), "dictum"));
                writer.write(tagContent(note.getIcon(), "icon"));
                writer.write(mContext.getString(R.string.note_dividing_end));

                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String tagContent(String content, String key) {
        return "<?" + key + ">" + content + "</" + key + ">";
    }

    public ArrayList<Note> readNoteFile(String fileName, int count, String noteTime) {
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String file_name = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + mAppFile + "/" + fileName + ".txt";
                BufferedReader buffReader = new BufferedReader(new FileReader(file_name));
                StringBuilder noteStr = new StringBuilder();
                String tempLine;
                while ((tempLine = buffReader.readLine()) != null && !tempLine.equals(mContext.getString(R.string.note_dividing_end))) {
                    noteStr.append(tempLine);
                }
                return parserToNoteList(noteStr.toString(), count, noteTime);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<Note> parserToNoteList(String noteStr, int count, String noteTime) {
        // 处理删除的笔记时间关键值
        String[] delNoteTime = noteTime.split(",", count);
        StringBuilder tempTimeStr = new StringBuilder("");
        for (String str : delNoteTime) {
            if (str.contains("*")) {
                tempTimeStr.append(str + ",");
            }
        }
        String delTimeStr = tempTimeStr.toString();

        ArrayList<Note> noteList = new ArrayList<>();
        String[] notes = noteStr.split(mContext.getString(R.string.note_dividing_end), count);
        for (String note : notes) {
            String time = parserContent(note, "time");
            if (!delTimeStr.contains(time)) {  // 不是已删除的笔记
                String title = parserContent(note, "title");
                String content = parserContent(note, "content");
                String dictum = parserContent(note, "dictum");
                String icon = parserContent(note, "icon");
                noteList.add(new Note(title, content, time, dictum, icon));
            }
        }
        return noteList;
    }

    private String parserContent(String content, String key) {
        return content.substring(content.indexOf("<?" + key + ">") + ("<?" + key + ">").length(), content.indexOf("</" + key + ">"));
    }
}
