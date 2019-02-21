package com.maplefall.wind.mg.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ACache {
    private static final int MAX_SIZE = 1000 * 1000 * 50;  //50Mb
    private static final int MAX_COUNT = Integer.MAX_VALUE;  //no limit of data number
    private static Map<String, ACache> mInstanceMap = new HashMap<>();
    private ACacheManager mCache;

    public static ACache get(Context context) {
        return get(context, "ACache");
    }

    public static ACache get(Context context, String cacheName) {
        File file = new File(context.getCacheDir(), cacheName);
        return get(file, MAX_SIZE, MAX_COUNT);
    }

    public static ACache get(Context context, long maxSize, int maxCount) {
        File file = new File(context.getCacheDir(), "ACache");
        return get(file, maxSize, maxCount);
    }

    public static ACache get(File file) {
        return get(file, MAX_SIZE, MAX_COUNT);
    }

    public static ACache get(File file, long maxSize, int maxCount) {
        ACache cache = mInstanceMap.get(file.getAbsoluteFile() + myPid());
        if (cache == null) {
            cache = new ACache(file, maxSize, maxCount);
            mInstanceMap.put(file.getAbsolutePath() + myPid(), cache);
        }
        return cache;
    }

    private ACache(File file, long maxSize, int maxCount) {
        if (!file.exists() && !file.mkdir()) {
            throw new RuntimeException("can't make dirs in " + file.getAbsolutePath());
        }
        mCache = new ACacheManager(file, maxSize, maxCount);
    }

    private static String myPid() {
        return "_" + android.os.Process.myPid();
    }

    /**
     * write string value to a file to save int the cache
     *
     * @param key
     * @param value
     */
    public void put(String key, String value) {
        File file = mCache.newFile(key);
        BufferedWriter out = null;
        try {
            //write the value to the file
            out = new BufferedWriter(new FileWriter(file), 1024);
            out.write(value);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //close the pipe
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mCache.putFile(file);
        }
    }

    /**
     * write string value with save time to a file to save int the cache
     *
     * @param key
     * @param value
     * @param saveTime
     */
    public void put(String key, String value, int saveTime) {
        put(key, TimeUtils.newStringWithDateInfo(saveTime, value));
    }

    /**
     * get data from cache in string type
     *
     * @param key
     * @return
     */
    public String getAsString(String key) {
        File file = mCache.getFile(key);
        if (!file.exists()) {
            return null;
        }
        boolean removeFile = false;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String readString = "";
            String currentLine;
            while ((currentLine = in.readLine()) != null) {
                readString += currentLine;
            }
            if (!TimeUtils.isDue(readString)) {
                return TimeUtils.clearDateInfo(readString);
            } else {
                removeFile = true;
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            //close the pipe
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (removeFile) {
                mCache.removeFile(key);
            }
        }
    }

    /**
     * save the JSONObject value to cache
     *
     * @param key
     * @param value
     */
    public void put(String key, JSONObject value) {
        put(key, value.toString());
    }

    /**
     * save the JSONObject value with save time to cache
     *
     * @param key
     * @param value
     * @param saveTime
     */
    public void put(String key, JSONObject value, int saveTime) {
        put(key, value.toString(), saveTime);
    }

    /**
     * get data form cache in JSONObject
     *
     * @param key
     * @return
     */
    public JSONObject getAsJSONObject(String key) {
        String JSONString = getAsString(key);
        try {
            if (isNullOrEmpty(JSONString)) {
                return new JSONObject();
            } else {
                return new JSONObject(JSONString);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * save the JSONArray value to cache
     *
     * @param key
     * @param value
     */
    public void put(String key, JSONArray value) {
        put(key, value.toString());
    }

    /**
     * save the JSONArray value with save time to cache
     *
     * @param key
     * @param value
     * @param saveTime
     */
    public void put(String key, JSONArray value, int saveTime) {
        put(key, value.toString(), saveTime);
    }

    /**
     * get data form cache in JSONArray
     *
     * @param key
     * @return
     */
    public JSONArray getAsJSONArray(String key) {
        String JSONString = getAsString(key);
        try {
            if (isNullOrEmpty(JSONString)) {
                return new JSONArray();
            } else {
                return new JSONArray(JSONString);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * save the byte date to cache
     *
     * @param key
     * @param value
     */
    public void put(String key, byte[] value) {
        File file = mCache.newFile(key);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(value);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mCache.putFile(file);
        }
    }

    /**
     * save byte data in the cache
     *
     * @param key
     * @param value
     * @param saveTime
     */
    public void put(String key, byte[] value, int saveTime) {
        put(key, TimeUtils.newByteArrayWithDateInfo(saveTime, value));
    }

    /**
     * get the data from cache in byte type
     *
     * @param key
     * @return
     */
    public byte[] getAsBinary(String key) {
        RandomAccessFile RAFile = null;
        boolean removeFile = false;
        try {
            File file = mCache.getFile(key);
            if (!file.exists()) {
                return null;
            }
            RAFile = new RandomAccessFile(file, "r");
            byte[] bytes = new byte[(int) RAFile.length()];
            RAFile.read(bytes);
            if (!TimeUtils.isDue(bytes)) {
                return TimeUtils.clearDateInfo(bytes);
            } else {
                removeFile = true;
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (RAFile != null) {
                try {
                    RAFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (removeFile) {
                mCache.removeFile(key);
            }
        }
    }

    /**
     * save serializable data in cache with save time equals -1
     *
     * @param key
     * @param value
     */
    public void put(String key, Serializable value) {
        put(key, value.toString(), -1);
    }

    /**
     * save serializable data in cache with save time
     * @param key
     * @param value
     * @param saveTime
     */
    public void put(String key, Serializable value, int saveTime) {
        ByteArrayOutputStream byteStream = null;
        ObjectOutputStream oos = null;
        try {
            byteStream = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(byteStream);
            oos.writeObject(value);
            byte[] data = byteStream.toByteArray();
            if (saveTime != -1) {
                put(key, data, saveTime);
            } else {
                put(key, data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * get Serializable data in Object
     * @param key
     * @return
     */
    public Object getAsSerialize(String key) {
        byte[] data = getAsBinary(key);
        if (data != null) {
            ByteArrayInputStream byteStream = null;
            ObjectInputStream ois = null;
            try {
                byteStream = new ByteArrayInputStream(data);
                ois = new ObjectInputStream(byteStream);
                Object readObject = ois.readObject();
                return readObject;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    if (byteStream != null)
                        byteStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (ois != null)
                        ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * save bitmap data in cache
     * @param key
     * @param value
     */
    public void put(String key, Bitmap value) {
        put(key, Bitmap2Bytes(value));
    }

    /**
     * save bitmap data in cache
     * @param key
     * @param value
     * @param saveTime
     */
    public void put(String key, Bitmap value, int saveTime) {
        put(key, Bitmap2Bytes(value), saveTime);
    }

    /**
     * get data from cache in bitmap
     * @param key
     * @return
     */
    public Bitmap getAsBitmap(String key) {
        if (getAsBinary(key) == null) {
            return null;
        }
        return bytes2Bitmap(getAsBinary(key));
    }

    /**
     * save drawable data in cache
     * @param key
     * @param value
     */
    public void put(String key, Drawable value) {
        put(key, drawable2Bitmap(value));
    }

    /**
     * save drawable data in cache with save time
     * @param key
     * @param value
     * @param saveTime
     */
    public void put(String key, Drawable value, int saveTime) {
        put(key, drawable2Bitmap(value), saveTime);
    }

    /**
     * get data from cache in drawable type
     * @param key
     * @return
     */
    public Drawable getAsDrawable(String key) {
        if (getAsBinary(key) == null) {
            return null;
        }
        return bitmap2Drawable(bytes2Bitmap(getAsBinary(key)));
    }

    /**
     * cache for a stream
     *
     * @param key
     * @return
     * @throws FileNotFoundException
     */
    public OutputStream put(String key) throws FileNotFoundException {
        return new MyFileOutputStream(mCache.newFile(key));
    }

    public InputStream get(String key) throws FileNotFoundException {
        File file = mCache.getFile(key);
        if (!file.exists()) {
            return null;
        }
        return new FileInputStream(file);
    }

    private boolean isNullOrEmpty(String str) {
        return null == str || "".equals(str);
    }

    class MyFileOutputStream extends FileOutputStream {
        private File mFile;

        public MyFileOutputStream(File file) throws FileNotFoundException {
            super(file);
            mFile = file;
        }

        public void close() throws IOException {
            super.close();
            mCache.putFile(mFile);
        }
    }

    private class ACacheManager {
        private final AtomicLong mCacheSize;
        private final AtomicInteger mCacheCount;
        private final long mSizeLimit;
        private final int mCountLimit;
        private final Map<File, Long> mLastUsageDates = Collections.synchronizedMap(new HashMap<File, Long>());
        protected File mCacheDir;

        private ACacheManager(File cacheDir, long sizeLimit, int countLimit) {
            mCacheDir = cacheDir;
            mSizeLimit = sizeLimit;
            mCountLimit = countLimit;
            mCacheSize = new AtomicLong();
            mCacheCount = new AtomicInteger();
            calculateCacheSizeAndCacheCount();
        }

        /**
         * calculate the mCacheSize and mCacheCount
         */
        private void calculateCacheSizeAndCacheCount() {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    int size = 0;
                    int count = 0;
                    File[] cacheFiles = mCacheDir.listFiles();
                    if (cacheFiles != null) {
                        for (File cacheFile : cacheFiles) {
                            size += calculateSize(cacheFile);
                            count += 1;
                            mLastUsageDates.put(cacheFile, cacheFile.lastModified());
                        }
                        mCacheSize.set(size);
                        mCacheCount.set(count);
                    }
                }
            };
            new Thread(runnable).start();
        }

        private long calculateSize(File file) {
            return file.length();
        }

        private void putFile(File file) {
            int curCacheCount = mCacheCount.get();
            while (curCacheCount + 1 > mCountLimit) {
                long freeSize = removeNext();
                mCacheSize.addAndGet(-freeSize);

                curCacheCount = mCacheCount.addAndGet(-1);
            }

            mCacheCount.addAndGet(1);

            long valueSize = calculateSize(file);
            long curCacheSize = mCacheSize.get();
            while (curCacheSize + valueSize > mSizeLimit) {
                long freeSize = removeNext();
                curCacheSize = mCacheSize.addAndGet(-freeSize);
            }
            mCacheSize.addAndGet(valueSize);

            Long currentTime = System.currentTimeMillis();
            file.setLastModified(currentTime);
            mLastUsageDates.put(file, currentTime);
        }

        /**
         * remove the oldest file
         *
         * @return
         */
        private long removeNext() {
            if (mLastUsageDates.isEmpty()) {
                return 0;
            }

            Long oldestUsage = null;
            File oldestUsedFile = null;
            Set<Map.Entry<File, Long>> entries = mLastUsageDates.entrySet();
            synchronized (mLastUsageDates) {
                for (Map.Entry<File, Long> entry : entries) {
                    if (oldestUsedFile == null) {
                        //initial the oldestUsedFile and oldestUsage
                        oldestUsedFile = entry.getKey();
                        oldestUsage = entry.getValue();
                    } else {
                        //compare and get the true wanted oldestUsedFile and oldestUsage
                        Long lastValueUsage = entry.getValue();
                        if (lastValueUsage < oldestUsage) {
                            oldestUsage = lastValueUsage;
                            oldestUsedFile = entry.getKey();
                        }
                    }
                }
            }

            //calculate the size of the file
            Long fileSize = calculateSize(oldestUsedFile);
            if (oldestUsedFile.delete()) {
                mLastUsageDates.remove(oldestUsedFile);
            }
            return fileSize;
        }

        /**
         * get the file according to the key
         *
         * @param key
         * @return
         */
        private File getFile(String key) {
            File file = newFile(key);
            Long currentTime = System.currentTimeMillis();
            file.setLastModified(currentTime);
            mLastUsageDates.put(file, currentTime);

            return file;
        }

        /**
         * make a new file
         *
         * @param key
         * @return
         */
        private File newFile(String key) {
            return new File(mCacheDir, key.hashCode() + "");
        }

        private boolean removeFile(String key) {
            File file = getFile(key);
            return file.delete();
        }

        /**
         * clear the cache and delete all files
         */
        private void clearCache() {
            mLastUsageDates.clear();
            mCacheSize.set(0);
            File[] files = mCacheDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }

    /**
     * inner time util class
     */
    private static class TimeUtils {

        private static boolean isDue(String data) {
            return isDue(data.getBytes());
        }

        private static boolean isDue(byte[] data) {
            String[] strings = getDateInfoFromDate(data);
            if (strings != null && strings.length == 2) {
                String saveTimeStr = strings[0];
                while (saveTimeStr.startsWith("0")) {
                    saveTimeStr = saveTimeStr.substring(1, saveTimeStr.length());
                }
                long saveTime = Long.valueOf(saveTimeStr);
                long deleteAfter = Long.valueOf(strings[1]);
                if (System.currentTimeMillis() > saveTime + deleteAfter * 1000) {
                    return true;
                }
            }
            return false;
        }

        private static String newStringWithDateInfo(int second, String strInfo) {
            return createDateInfo(second) + strInfo;
        }

        private static byte[] newByteArrayWithDateInfo(int second, byte[] data2) {
            byte[] data1 = createDateInfo(second).getBytes();
            byte[] retData = new byte[data1.length + data2.length];
            System.arraycopy(data1, 0, retData, 0, data1.length);
            System.arraycopy(data2, 0, retData, data1.length, data2.length);
            return retData;
        }

        private static String clearDateInfo(String strInfo) {
            if (strInfo != null && hasDateInfo(strInfo.getBytes())) {
                strInfo = strInfo.substring(strInfo.indexOf(mSeparator) + 1, strInfo.length());
            }
            return strInfo;
        }

        private static byte[] clearDateInfo(byte[] data) {
            if (hasDateInfo(data)) {
                return copyOfRange(data, indexOf(data, mSeparator) + 1, data.length);
            }
            return data;
        }

        private static boolean hasDateInfo(byte[] data) {
            return data != null && data.length > 15 && data[13] == '-' && indexOf(data, mSeparator) > 14;
        }

        private static String[] getDateInfoFromDate(byte[] data) {
            if (hasDateInfo(data)) {
                String saveDate = new String(copyOfRange(data, 0, 13));
                String deleteAfter = new String(copyOfRange(data, 14, indexOf(data, mSeparator)));
                return new String[]{saveDate, deleteAfter};
            }
            return null;
        }

        private static int indexOf(byte[] data, char c) {
            for (int i = 0; i < data.length; i++) {
                if (data[i] == c) {
                    return i;
                }
            }
            return -1;
        }

        private static byte[] copyOfRange(byte[] original, int from, int to) {
            int newLength = to - from;
            if (newLength < 0)
                throw new IllegalArgumentException(from + " > " + to);
            byte[] copy = new byte[newLength];
            System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
            return copy;
        }

        private static final char mSeparator = ' ';

        private static String createDateInfo(int second) {
            String currentTime = System.currentTimeMillis() + "";
            while (currentTime.length() < 13) {
                currentTime = "0" + currentTime;
            }
            return currentTime + "-" + second + mSeparator;
        }
    }

    /*
     * Bitmap → byte[]
     */
    private static byte[] Bitmap2Bytes(Bitmap bm) {
        if (bm == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /*
     * byte[] → Bitmap
     */
    private static Bitmap bytes2Bitmap(byte[] b) {
        if (b.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    /*
     * Drawable → Bitmap
     */
    private static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        // get drawable length
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        // get drawable colors
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        // make bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // draw the drawable in the canvas
        drawable.draw(canvas);
        return bitmap;
    }

    /*
     * Bitmap → Drawable
     */
    @SuppressWarnings("deprecation")
    private static Drawable bitmap2Drawable(Bitmap bm) {
        if (bm == null) {
            return null;
        }
        BitmapDrawable bd = new BitmapDrawable(bm);
        bd.setTargetDensity(bm.getDensity());
        return new BitmapDrawable(bm);
    }
}
