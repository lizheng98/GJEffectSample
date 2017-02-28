package com.gj.effect.util;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 图片工具类
 */
@SuppressLint("NewApi")
public class BitmapUtility {

	private static final String Tag = "BitmapUtility";

	/**
	 * 通过URL加载图片
	 *
	 * @param context 上下文环境
	 * @param uri     图片URL
	 * @param maxSize 图片加载时允许的最大大小
	 * @return 返回指定的URL缩略图
	 */
	public static Bitmap LoadImageFromUrl(Context context, Uri uri, int maxSize) {
		if (uri == null) {
			return null;
		}
		String filePath = getFilePathFromUri(context, uri);
		return LoadImageFromUrl(filePath, maxSize);
	}

	public static Bitmap LoadImageFromUrl(String filePath, int maxSize) {

		if (filePath == null || filePath.trim().equals("")) {
			return null;
		}

		Log.d(Tag, "filePath = " + filePath + "  maxSize = " + maxSize);
		if (!new File(filePath).exists()) {
			return null;
		}
		Bitmap bmp = null;
		int tryNum = 3;
		while (bmp == null && tryNum > 0) {
			Options options = obtainBitmapOptions(filePath, maxSize);
			try {
				Log.d(Tag, "压缩比例前   options.outHeight = " + options.outHeight + " + options.outWidth = " + options.outWidth);
				bmp = BitmapFactory.decodeFile(filePath, options);
				Log.d(Tag, "压缩比例后  be = " + options.inSampleSize + "   options.outHeight = " + options.outHeight + "  options.outWidth = "
						+ options.outWidth);
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
			options.inSampleSize = be;
		}
		return options;
	}

	public static Options obtainBitmapOptions(byte[] bitmapBuffer, int maxSize) {

		if (bitmapBuffer == null) {
			return null;
		}

		if (maxSize <= 0) {
			return null;
		}

		Options options = new Options();
		// 获得图片文件的属性设置
		options.inJustDecodeBounds = true;
		// 不用加载图片返回图片
		BitmapFactory.decodeByteArray(bitmapBuffer, 0, bitmapBuffer.length, options);
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Config.ARGB_8888;
		options.inDither = true;
		int size = Math.max(options.outHeight, options.outWidth);
		int be = Math.round((float) (size / (float) maxSize));
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		return options;
	}

	// 以流形式加载大图片
	public static Bitmap decodeFileByByte(String filePath) {
		if (filePath == null || "".equals(filePath.trim())) {
			return null;
		}
		Options bfOptions = new Options();
		bfOptions.inDither = false;
		bfOptions.inPurgeable = true;
		bfOptions.inTempStorage = new byte[12 * 1024]; // 12k
		bfOptions.inJustDecodeBounds = true;
		File file = new File(filePath);
		FileInputStream fs = null;
		Bitmap bmp = null;
		try {
			fs = new FileInputStream(file);
			if (fs != null)
				bmp = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bmp;
	}

	/**
	 * 通过Uri获取文件的路径
	 *
	 * @param uri
	 * @return
	 */
	public static String getFilePathFromUri(Context mContext, Uri uri) {
		String fileName = null;
		Uri filePathUri = uri;
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		if (uri != null) {
			if (uri.getScheme().toString().compareTo("content") == 0) { // content://开头的uri

				// 如果是4.4系统，且是从最近，下载等目录下获取的图片
				if (isKitKat && DocumentsContract.isDocumentUri(mContext, uri)) {
					// ExternalStorageProvider
					if (isExternalStorageDocument(uri)) {
						final String docId = DocumentsContract.getDocumentId(uri);
						final String[] split = docId.split(":");
						final String type = split[0];

						if ("primary".equalsIgnoreCase(type)) {
							return Environment.getExternalStorageDirectory() + "/" + split[1];
						}

						// TODO handle non-primary volumes
					}
					// DownloadsProvider
					else if (isDownloadsDocument(uri)) {

						final String id = DocumentsContract.getDocumentId(uri);
						final Uri contentUri = ContentUris.withAppendedId(
								Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

						return getDataColumn(mContext, contentUri, null, null);
					}
					// MediaProvider
					else if (isMediaDocument(uri)) {
						final String docId = DocumentsContract.getDocumentId(uri);
						final String[] split = docId.split(":");
						final String type = split[0];

						Uri contentUri = null;
						if ("image".equals(type)) {
							contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
						} else if ("video".equals(type)) {
							contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
						} else if ("audio".equals(type)) {
							contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
						}

						final String selection = "_id=?";
						final String[] selectionArgs = new String[]{split[1]};

						return getDataColumn(mContext, contentUri, selection, selectionArgs);
					}
				}
				// 是4.3及以下系统，或从图库获取的图片
				else {
					Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
					if (cursor != null && cursor.moveToFirst()) {
						int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						fileName = cursor.getString(column_index); // 取出文件路径
						// if (!fileName.startsWith("/mnt")) {// 检查是否有”/mnt“前缀
						// fileName = "/mnt" + fileName;
						// }
						cursor.close();
					}
				}

			} else if (uri.getScheme().compareTo("file") == 0) { // file:///开头的uri

				fileName = filePathUri.toString();
				fileName = filePathUri.toString().replace("file://", "");// 替换file://
				// if (!fileName.startsWith("/mnt")) {// 加上"/mnt"头
				// fileName += "/mnt";
				// }
			}
		}
		return fileName;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context       The context.
	 * @param uri           The Uri to query.
	 * @param selection     (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {column};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

	/**
	 * 改变图片大小（注意：这是重新创建一张新的图片，原图片不释放，自己处理图片释放）
	 *
	 * @param bm           所要转换的bitmap
	 * @param newWidth新的宽
	 * @param newHeight新的高
	 * @return 指定宽高的bitmap
	 */
	public static Bitmap ZoomImg(Bitmap bm, int newWidth, int newHeight) {
		// 获得图片的宽高
		int width = bm.getWidth();
		int height = bm.getHeight();
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片 www.2cto.com
		// Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
		// true);
		Bitmap newbm = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
		return newbm;
	}

	/**
	 * Bitmap保存到本地（注意：原始Bitma不释放，需自己释放）
	 *
	 * @param croppedImage 需要写入的图片
	 * @param filepath     写入文件的本地路径
	 * @param name         写入本地的图片名称
	 * @param quality      图片压缩质量，默认是85
	 * @return
	 */
	public static File saveBitmapToLocal(Bitmap croppedImage, String filepath, String name, int quality) {
		String dir_path = filepath != null && !filepath.trim().equals("") ? filepath : getSDPath() + File.separator
				+ "KTVDaren/Cache/img/userimage";
		File dir = new File(dir_path);
		File file = new File(dir_path + File.separator
				+ (name != null && !name.trim().equals("") ? name : (System.currentTimeMillis() + ".jpg")));
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				file = null;
				e.printStackTrace();
			}
		}
		OutputStream outStream;
		try {
			// 压缩降低图片质量
			outStream = new FileOutputStream(file);
			croppedImage.compress(Bitmap.CompressFormat.JPEG, quality, outStream);
			outStream.flush();
			outStream.close();
			Log.i("CropImage", "bitmap saved tosd,path:" + file.toString());
		} catch (Exception e) {
			file.delete();
			file = null;
			e.printStackTrace();
		}
		return file;
	}

	public static File saveBitmapToLocal(Bitmap croppedImage, String filepath, String name) {
		return saveBitmapToLocal(croppedImage, filepath, name, 85);
	}

	private static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		sdDir = Environment.getExternalStorageDirectory();
		return sdDir.toString();
	}

	public static InputStream Bitmap2InputStream(Bitmap bm, int quality) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return is;
	}

//	public static Bitmap LoadImageFromInputStream(String zipFileString, String fileString, int maxSize) {
//		Bitmap bmp = null;
//		int tryNum = 1;
//		while (bmp == null && tryNum > 0) {
//			BitmapFactory.Options options = obtainBitmapOptions(zipFileString, fileString, maxSize);
//			InputStream inputStream = null;
//			try {
//				Log.d(Tag, "压缩比例前  options.outHeight = " + options.outHeight + "  options.outWidth = "
//						+ options.outWidth);
//				inputStream = ZipUtil.UpZip(zipFileString, fileString);
//				bmp = BitmapFactory.decodeStream(inputStream, null, options);
//				Log.d(Tag, "压缩比例后  be = " + options.inSampleSize + "   options.outHeight = " + options.outHeight + "  options.outWidth = "
//						+ options.outWidth);
//			} catch (OutOfMemoryError e) {
//				Log.d(Tag, "内存溢出");
//				System.gc();
//				bmp = null;
//				maxSize = maxSize / 2;
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				if (inputStream != null) {
//					try {
//						inputStream.close();
//					} catch (IOException e) {
//					}
//				}
//			}
//			tryNum--;
//		}
//		return bmp;
//	}
//
//	public static Options obtainBitmapOptions(String zipFileString, String fileString, int maxSize) {
//		BitmapFactory.Options options = null;
//		try {
//			InputStream inputStream = ZipUtil.UpZip(zipFileString, fileString);
//			options = new BitmapFactory.Options();
//			// 获得图片文件的属性设置
//			options.inJustDecodeBounds = true;
//			// 不用加载图片返回图片
//			BitmapFactory.decodeStream(inputStream, null, options);
//			options.inJustDecodeBounds = false;
//			options.inDither = true;
//			if (maxSize > 0) {
//				int size = Math.max(options.outHeight, options.outWidth);
//				int be = Math.round((float) (size / (float) maxSize));
//				if (be <= 0) {
//					be = 1;
//				}
//				options.inSampleSize = be;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return options;
//	}
}
