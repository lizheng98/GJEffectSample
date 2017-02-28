package com.gj.effect.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件帮助类
 * 
 * @author
 * 
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
			EvtLog.e(TAG, e);
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
	 * 取得文件大小
	 * 
	 * @param f 文件
	 * @return long 大小
	 * 
	 */
	public long getFileSizes(File f) {
		long s = 0;
		FileInputStream fis = null;
		try {
			if (f.exists()) {
				fis = new FileInputStream(f);
				s = fis.available();
				fis.close();
			} else {
				f.createNewFile();
			}
		} catch (Exception e) {
			EvtLog.w(TAG, e);
		}
		return s;
	}

	/**
	 * 递归取得文件夹大小
	 * 
	 * @param filedir 文件
	 * @return 大小
	 */
	public static long getFileSize(File filedir) {
		long size = 0;
		if (null == filedir) {
			return size;
		}
		File[] files = filedir.listFiles();

		try {
			for (File f : files) {
				if (f.isDirectory()) {
					size += getFileSize(f);
				} else {
					FileInputStream fis = new FileInputStream(f);
					size += fis.available();
					fis.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;

	}

	/**
	 * 递归获取文件路径
	 * 
	 * @return
	 */
	public static List<String> getFilePaths(File fileDir) {
		if (null == fileDir) {
			return null;
		}

		File[] files = fileDir.listFiles();

		if (files == null || files.length == 0) {
			return null;
		}

		List<String> filePaths = new ArrayList<String>();

		for (File f : files) {
			if (f.isDirectory()) {
				List<String> list = getFilePaths(f);
				if (list != null) {
					filePaths.addAll(list);
				}

			} else {
				filePaths.add(f.getAbsolutePath());
			}
		}

		return filePaths;
	}

	/**
	 * 转换文件大小
	 * 
	 * @param fileS 大小
	 * @return 转换后的文件大小
	 */
	public static String formatFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.0");
		String fileSizeString = "";
		if (fileS == 0) {
			fileSizeString = "0" + "KB";
		} else if (fileS < SIZE_KB) {
			fileSizeString = df.format((double) fileS) + "Byte";
		} else if (fileS < SIZE_MB) {
			fileSizeString = df.format((double) fileS / SIZE_KB) + "K";
		} else if (fileS < SIZE_GB) {
			fileSizeString = df.format((double) fileS / SIZE_MB) + "M";
		} else {
			fileSizeString = df.format((double) fileS / SIZE_GB) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 将文件写入SD卡
	 * 
	 * @param path 路径
	 * @param fileName 文件名称
	 * @param input 输入流
	 * @return 文件
	 */
	public static File writeToSDCard(String path, String fileName,
			InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			createSDDir(path);
			file = createSDFile(path + fileName);
			output = new FileOutputStream(file);

			byte[] buffer = new byte[BUFSIZE];
			int readedLength = -1;
			while ((readedLength = input.read(buffer)) != -1) {
				output.write(buffer, 0, readedLength);
			}
			output.flush();

		} catch (Exception e) {
			EvtLog.e(TAG, e);
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				EvtLog.e(TAG, e);
			}
		}

		return file;
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
	 * @param context The context to use
	 * @param uniqueName A unique directory name to append to the cache dir
	 * @return The cache path
	 */
	public static String getDiskCachePath(Context context, String uniqueName) {
		final String cachePath = isSDCardReady() ? getExternalCachePath(context)
				: context.getCacheDir().getPath();
		return cachePath + File.separator + uniqueName;
	}

	/**
	 * 使用Http下载文件，并保存在手机目录中
	 * 
	 * @param urlStr url地址
	 * @param path 路径
	 * @param fileName 文件名称
	 * @param onDownloadingListener 下载监听器
	 * @return -1:文件下载出错 0:文件下载成功
	 * @throws MessageException
	 */
	public static boolean downFile(String urlStr, String path, String fileName,
			boolean isUpgradeMust,boolean delCacheFlag, OnDownloadingListener onDownloadingListener) {
		InputStream inputStream = null;
		try {
			if (!path.endsWith("/")) {
				path += "/";
			}
			String filePath = path + fileName;
			EvtLog.d("test", "当前路径为:   " + filePath);
			if (isFileExist(filePath) && delCacheFlag) {
				delete(filePath);
			}else{
				return true;
			}
			HttpClient client = new DefaultHttpClient();
			// 设置网络连接超时和读数据超时
			client.getParams()
					.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
							CONNECTION_TIMEOUT)
					.setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
			HttpGet httpget = new HttpGet(urlStr);
			HttpResponse response = client.execute(httpget);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				EvtLog.e(TAG, "http status code is: " + statusCode);
				return false;
			}
			InputStream fileStream = response.getEntity().getContent();
			// 检查下载文件夹，若没有，则创建
			prepareFile(path);
			FileOutputStream output = new FileOutputStream(filePath);
			byte[] buffer = new byte[BUFSIZE];
			int len = 0;
			int count = 0;
			int progress = 0;
			while ((len = fileStream.read(buffer)) > 0) {
				count += len;
				progress += len;
				EvtLog.d(TAG, "read " + len + " bytes, total read: " + count
						+ " bytes");
				output.write(buffer, 0, len);
				if (onDownloadingListener != null && count >= BUFSIZE * COUNT) {
					EvtLog.d(TAG, "onDownloadingListener.onDownloading()");
					onDownloadingListener.onDownloading(progress);
					count = 0;
				}
			}
			if (onDownloadingListener != null && count >= 0) {
				EvtLog.d(TAG, "onDownloadingListener else)");
				onDownloadingListener.onDownloading(progress);
				count = 0;
			}
			fileStream.close();
			output.close();
			if (onDownloadingListener != null) {
				onDownloadingListener.onDownloadComplete(filePath);
			}
		} catch (Exception e) {
			EvtLog.d(TAG, "downFile Exception");
			EvtLog.e(TAG, e);
			if (onDownloadingListener != null) {
				onDownloadingListener.onError(isUpgradeMust);
			}
			return false;
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				EvtLog.e(TAG, e);
				EvtLog.d(TAG, "downFile Exception in finally");
			}
		}
		return true;
	}

	/**
	 * 创建文件夹，但不包含文件名
	 * 
	 * @author lcq 2012-12-25
	 * @param path
	 * @return
	 */
	public static File createFile(String path) {
		File localFile1 = new File(path);
		File localFile2 = new File(localFile1.getAbsolutePath().substring(0,
				localFile1.getAbsolutePath().lastIndexOf(File.separator)));
		if (!localFile2.exists()) {
			createFile(localFile2.getPath());
			localFile2.mkdirs();
		}
		return localFile1;
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

	/**
	 * 根据文件路径获取文件名
	 * 
	 * @param filePath 文件路径
	 * @return 文件名
	 */
	public static String getFileNameByFilePath(String filePath) {
		if (filePath == null || "".equals(filePath)) {
			return "";
		}

		File file = new File(filePath);

		return file.getName();
	}

	public static void copyFile(File srcFile, File destFile) throws IOException {
		copyFile(srcFile, destFile, true);
	}

	public static void copyFile(File srcFile, File destFile,
			boolean preserveFileDate) throws IOException {
		if (srcFile == null) {
			throw new NullPointerException("Source must not be null");
		}
		if (destFile == null) {
			throw new NullPointerException("Destination must not be null");
		}
		if (srcFile.exists() == false) {
			throw new FileNotFoundException("Source '" + srcFile
					+ "' does not exist");
		}
		if (srcFile.isDirectory()) {
			throw new IOException("Source '" + srcFile
					+ "' exists but is a directory");
		}
		if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
			throw new IOException("Source '" + srcFile + "' and destination '"
					+ destFile + "' are the same");
		}
		File parentFile = destFile.getParentFile();
		if (parentFile != null) {
			if (!parentFile.mkdirs() && !parentFile.isDirectory()) {
				throw new IOException("Destination '" + parentFile
						+ "' directory cannot be created");
			}
		}
		if (destFile.exists() && destFile.canWrite() == false) {
			throw new IOException("Destination '" + destFile
					+ "' exists but is read-only");
		}
		doCopyFile(srcFile, destFile, preserveFileDate);
	}

	private static void doCopyFile(File srcFile, File destFile,
			boolean preserveFileDate) throws IOException {
		if (destFile.exists() && destFile.isDirectory()) {
			throw new IOException("Destination '" + destFile
					+ "' exists but is a directory");
		}

		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel input = null;
		FileChannel output = null;
		try {
			fis = new FileInputStream(srcFile);
			fos = new FileOutputStream(destFile);
			input = fis.getChannel();
			output = fos.getChannel();
			long size = input.size();
			long pos = 0;
			long count = 0;
			while (pos < size) {
				count = size - pos > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE
						: size - pos;
				pos += output.transferFrom(input, pos, count);
			}
		} finally {
			closeQuietly(output);
			closeQuietly(fos);
			closeQuietly(input);
			closeQuietly(fis);
		}

		if (srcFile.length() != destFile.length()) {
			throw new IOException("Failed to copy full contents from '"
					+ srcFile + "' to '" + destFile + "'");
		}
		if (preserveFileDate) {
			destFile.setLastModified(srcFile.lastModified());
		}
	}

	public static void closeQuietly(OutputStream output) {
		closeQuietly((Closeable) output);
	}

	public static void closeQuietly(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}

	public static String getFileName(String path) {
		String fileName = "";
		if (path != null) {
			fileName = path.substring(path.lastIndexOf("/") + 1);
		}
		return fileName;
	}

	public static File getCameraPhotoFile() {
		File dir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		return new File(dir, "feizao_" + System.currentTimeMillis() + ".jpg");
	}

	/**
	 * 
	 * @author
	 * 
	 */
	public interface OnDownloadingListener {
		/**
		 * 下载
		 * 
		 * @param progressInByte 已下载的字节长度
		 * 
		 */
		void onDownloading(int progressInByte);

		/**
		 * 下载完成后的回调方法
		 * 
		 * @param filePath 文件路径
		 */
		void onDownloadComplete(String filePath);

		/**
		 * 下周失败的回调方法
		 * 
		 * @param isUpgradeMust 是否必须升级
		 */
		void onError(boolean isUpgradeMust);
	}

	public static String getFileMD5(InputStream fis) {

		try {
			if (null == fis) {
				return null;
			}

			byte[] buffer = new byte[FILE_SIZE_UNIT];
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			int numRead = 0;
			while ((numRead = fis.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}
			return byte2Hexs(md5.digest());
		} catch (Exception e) {
			EvtLog.e("Failed Get md5 code from file,fileName={}", "no name", e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fis = null;
			}
		}
		return null;
	}

	/**
	 * 将字节数组转换成16进制字符串
	 * 
	 * @param bytes 待转换的字节数组
	 * @return 16进制的字符串
	 */
	public static String byte2Hexs(byte[] bytes) {
		StringBuilder buff = new StringBuilder();
		for (int index = 0; index < bytes.length; index++) {
			buff.append(byte2Hex(bytes[index]));
		}
		return buff.toString();
	}

	/**
	 * 将字节转换成16进制字符串.
	 * 
	 * @param b 待转换的字节
	 * @return 转换后的16进制字符串
	 */
	private static String byte2Hex(byte b) {
		String hex = "";
		if (b > 0) {
			hex = Integer.toHexString(b);
		} else {
			hex = Integer.toHexString(b & 0xFF);
		}
		if (hex.length() == 1) {
			hex = "0" + hex;
		}
		return hex;
	}
}
