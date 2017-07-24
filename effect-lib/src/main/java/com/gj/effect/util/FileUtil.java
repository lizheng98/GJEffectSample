package com.gj.effect.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * 文件帮助类
 *
 * @author
 */
@SuppressLint("NewApi")
public class FileUtil {
	public static final int BUFSIZE = 256;
	public static final int COUNT = 320;
	private static final String TAG = "FileUtils";
	private static final long SIZE_KB = 1024;
	private static final long SIZE_MB = 1048576;
	private static final long SIZE_GB = 1073741824;
	private static final int SO_TIMEOUT = 600000;
	private static final int CONNECTION_TIMEOUT = 5000;

	/**
	 * The file copy buffer size (30 MB)
	 */
	private static final long FILE_COPY_BUFFER_SIZE = SIZE_MB * 30;
	private static final int FILE_SIZE_UNIT = 1024;

	/**
	 * 在SD卡上面创建文件
	 *
	 * @param filePath 文件路径
	 * @return 文件
	 * @throws IOException 异常
	 */
	public static File createSDFile(String filePath) throws IOException {
		File file = new File(filePath);
		file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上面创建目录
	 *
	 * @param dirName 目录名称
	 * @return 文件
	 */
	public static File createSDDir(String dirName) {
		File dir = new File(dirName);
		dir.mkdir();
		return dir;
	}

	/**
	 * 判断指定的文件是否存在
	 *
	 * @param filePath 文件路径
	 * @return 是否存在
	 */
	public static boolean isFileExist(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}

	/**
	 * 准备文件夹，文件夹若不存在，则创建
	 *
	 * @param filePath 文件路径
	 */
	public static void prepareFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * 删除指定的文件或目录
	 *
	 * @param filePath 文件路径
	 */
	public static void delete(String filePath) {
		if (filePath == null) {
			return;
		}
		try {
			File file = new File(filePath);
			delete(file);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	/**
	 * 删除指定的文件或目录
	 *
	 * @param file 文件
	 */
	public static void delete(File file) {
		if (file == null || !file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			deleteDirRecursive(file);
		} else {
			file.delete();
		}
	}

	/**
	 * 递归删除目录
	 *
	 * @param dir 文件路径
	 */
	public static void deleteDirRecursive(File dir) {
		if (dir == null || !dir.exists() || !dir.isDirectory()) {
			return;
		}
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		for (File f : files) {
			if (f.isFile()) {
				f.delete();
			} else {
				deleteDirRecursive(f);
			}
		}
		dir.delete();
	}



	/**
	 * 判断SD卡是否已经准备好
	 *
	 * @return 是否有SDCARD
	 */
	public static boolean isSDCardReady() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * Get the external app cache directory.
	 *
	 * @param context The context to use
	 * @return The external cache path
	 */
	public static String getExternalCachePath(Context context) {
		final String cachePath = "/Android/data/" + context.getPackageName();
		return Environment.getExternalStorageDirectory().getPath() + cachePath;
	}

	/**
	 * Get a usable cache directory (external if available, internal otherwise).
	 *
	 * @param context    The context to use
	 * @param uniqueName A unique directory name to append to the cache dir
	 * @return The cache path
	 */
	public static String getDiskCachePath(Context context, String uniqueName) {
		final String cachePath = isSDCardReady() ? getExternalCachePath(context)
				: context.getCacheDir().getPath();
		return cachePath + File.separator + uniqueName;
	}


	/**
	 * 创建一个文件(包括文件夹)，创建成功返回true
	 *
	 * @param filePath
	 * @return
	 */
	public static boolean createFiles(String filePath) {
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}

				return file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}
