package com.gj.effect.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

import java.io.File;

/**
 * 图片工具类
 */
public class BitmapUtility {

	private static final String Tag = "BitmapUtility";


	public static Bitmap LoadImageFromUrl(String filePath, int maxSize) {

		if (filePath == null || filePath.trim().equals("")) {
			return null;
		}

		Log.d(Tag, "filePath = " + filePath + "  maxSize = " + maxSize);
		if (!new File(filePath).exists()) {
			return null;
		}

		Log.d(Tag, " filepath = " + filePath);
		Bitmap bmp = null;
		int tryNum = 3;
		while (bmp == null && tryNum > 0) {
			Options options = obtainBitmapOptions(filePath, maxSize);
			try {
				bmp = BitmapFactory.decodeFile(filePath, options);
			} catch (OutOfMemoryError e) {
				Log.d(Tag, "内存溢出");
				System.gc();
				bmp = null;
				maxSize = maxSize / 2;
			}
			tryNum--;
		}
		return bmp;
	}

	/**
	 * 获得图片的Options信息，可以指定最大值，设置缩小比例，但不影响原始大小读取
	 *
	 * @param filePath
	 * @param maxSize  值为0时不设置 inSampleSize
	 * @return
	 */
	public static Options obtainBitmapOptions(String filePath, int maxSize) {

		if (filePath == null || filePath.trim().equals("")) {
			return null;
		}

		if (!new File(filePath).exists()) {
			return null;
		}

		Options options = new Options();
		// 获得图片文件的属性设置
		options.inJustDecodeBounds = true;
		// 不用加载图片返回图片
		BitmapFactory.decodeFile(filePath, options);
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Config.RGB_565;
		options.inDither = true;
		if (maxSize > 0) {
			int size = Math.max(options.outHeight, options.outWidth);
			int be = Math.round((float) (size / (float) maxSize));
			if (be <= 0) {
				be = 1;
			}
			Log.d(Tag, "压缩比例  be = " + be + "   options.outHeight = " + options.outHeight + "  options.outWidth = "
					+ options.outWidth);
			options.inSampleSize = be;
		}
		return options;
	}
}
