package com.gj.file.load;

import android.os.AsyncTask;
import android.util.Log;

import com.gj.effect.util.FileUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/11/11.
 */

public class AsyLoadTask implements LoadTask {

	/**
	 * 开启下载任务到线程池里，防止多并发线程过多
	 *
	 * @param uri            下载地址
	 * @param targetFilePath
	 * @param listener
	 */
	@Override
	public void loadTask(final String uri, final String targetFilePath, final LoadingListener listener) {
		new AsyncTask<Void, Void, Void>() {//开启一个多线程池，大小为cpu数量+1

			@Override
			protected Void doInBackground(Void... params) {
				downloadToStream(uri, targetFilePath, listener);
				return null;
			}
		}.execute();
	}


	/**
	 * 通过httpconnection下载一个文件，使用普通的IO接口进行读写
	 *
	 * @param uri
	 * @param targetFilePath
	 * @param listener
	 * @return
	 */
	private long downloadToStream(String uri, final String targetFilePath, final LoadingListener listener) {
		if (listener == null) return -1;
		listener.onLoadingProcess(100, 0);
		HttpURLConnection httpURLConnection = null;
		BufferedInputStream bis = null;
		OutputStream outputStream = null;

		long result = -1;
		long fileLen = 0;
		long currCount = 0;
		try {

			try {
				final URL url = new URL(uri);
				FileUtil.delete(targetFilePath);
				FileUtil.createFiles(targetFilePath);
				outputStream = new FileOutputStream(targetFilePath);
				httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setConnectTimeout(20000);
				httpURLConnection.setReadTimeout(10000);

				final int responseCode = httpURLConnection.getResponseCode();
				if (HttpURLConnection.HTTP_OK == responseCode) {
					bis = new BufferedInputStream(httpURLConnection.getInputStream());
					result = httpURLConnection.getExpiration();
					result = result < System.currentTimeMillis() ? System.currentTimeMillis() + 40000 : result;
					fileLen = httpURLConnection.getContentLength();//这里通过http报文的header Content-Length来获取gif的总大小，需要服务器提前把header写好
				} else {
					Log.e("Alex", "downloadToStream -> responseCode ==> " + responseCode);
					listener.onLoadingFailure(new IllegalStateException("response not http_ok"));
					return -1;
				}
			} catch (final Exception ex) {
				listener.onLoadingFailure(ex);
				return -1;
			}


			byte[] buffer = new byte[4096];//每4k更新进度一次
			int len = 0;
			BufferedOutputStream out = new BufferedOutputStream(outputStream);
			while ((len = bis.read(buffer)) != -1) {
				out.write(buffer, 0, len);
				currCount += len;
				final long finalFileLen = fileLen;
				final long finalCurrCount = currCount;
				listener.onLoadingProcess(finalFileLen, finalCurrCount);
			}
			out.flush();
			listener.onLoadingSuccess(targetFilePath);
		} catch (Throwable e) {
			result = -1;
			listener.onLoadingFailure(e);
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (final Throwable e) {
					listener.onLoadingFailure(e);
				}
			}
		}
		return result;
	}
}
