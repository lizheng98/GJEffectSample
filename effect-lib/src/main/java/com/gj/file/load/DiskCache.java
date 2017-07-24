package com.gj.file.load;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.gj.effect.util.EvtLog;
import com.gj.effect.util.FileUtil;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2016/11/10.
 */

public class DiskCache implements FileCache {

	private String mFileRootPath;

	public DiskCache(Context context, String fileDir) {
		this.mFileRootPath = FileUtil.getDiskCachePath(context, fileDir);
	}

	@Override
	public void put(String url) {

	}

	@Override
	public File get(String url) {
		//首先查询一下这个gif是否已被缓存
		String suffix = getFileSuffix(url);
		String md5FileName = getMd5(url);
		String path = mFileRootPath + File.separator + md5FileName + suffix;//带.tmp后缀的是没有下载完成的，用于加载第一帧，不带tmp后缀是下载完成的，
		//这样做的目的是为了防止一个图片正在下载的时候，另一个请求相同url的imageView使用未下载完毕的文件显示一半图像
		Log.i("AlexGIF", "gif图片的缓存路径是" + path);
		File cacheFile = new File(path);
		return cacheFile;
	}

	/**
	 * 用于获取一个String的md5值
	 *
	 * @param str
	 * @return
	 */
	private static String getMd5(String str) {
		if (TextUtils.isEmpty(str))
			throw new NullPointerException("url is null");
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
			byte[] bs = md5.digest(str.getBytes());
			StringBuilder sb = new StringBuilder(40);
			for (byte x : bs) {
				if ((x & 0xff) >> 4 == 0) {
					sb.append("0").append(Integer.toHexString(x & 0xff));
				} else {
					sb.append(Integer.toHexString(x & 0xff));
				}
			}
			if (sb.length() < 24) return sb.toString();
			return sb.toString().substring(8, 24);//为了提高磁盘的查找文件速度，让文件名为16位
		} catch (NoSuchAlgorithmException e) {
			EvtLog.i("Alex", "MD5加密失败");
			return "no_name";
		}
	}

	private static String getFileSuffix(String url) {
		if (TextUtils.isEmpty(url))
			throw new NullPointerException("url is null");
		int lastIndexOf = url.lastIndexOf(".");
		String suffix = url.substring(lastIndexOf);
		return suffix;
	}
}
