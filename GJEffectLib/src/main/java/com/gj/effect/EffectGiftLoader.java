package com.gj.effect;

import android.content.Context;
import android.util.Log;

import com.gj.file.load.AsyLoadTask;
import com.gj.file.load.DiskCache;
import com.gj.file.load.FileCache;
import com.gj.file.load.LoadTask;
import com.gj.file.load.LoadingListener;

import java.io.File;

/**
 * Created by Administrator on 2017/2/13.
 * 特效礼物加载器
 */

public class EffectGiftLoader {
	private static String TAG = EffectGiftLoader.class.getSimpleName();
	public static String EFFECT_CACHE_FILE_DIR = "effect";
	public static String EFFECT_TEMP_FILE_SUBIFX = ".tmp";
	private static EffectGiftLoader mInstance;
	// 硬盘缓存
	private FileCache mGifCache;
	// 文件加载器
	private LoadTask mLoadTask;

	private Context mContext;

	protected EffectGiftLoader(Context context) {
		mContext = context.getApplicationContext();
		mGifCache = new DiskCache(mContext, EFFECT_CACHE_FILE_DIR);
		mLoadTask = new AsyLoadTask();
	}

	public static EffectGiftLoader getInstance(Context context) {
		if (mInstance == null) {
			synchronized (EffectGiftLoader.class) {
				if (mInstance == null) {
					mInstance = new EffectGiftLoader(context);
				}
			}
		}
		return mInstance;
	}

	/**
	 * 加载动效礼物，下载数据到本地sd卡
	 *
	 * @param url
	 */
	public void loadData(String url, LoadingListener loadingListener) {
//		loadingListener.onLoadingSuccess("/storage/emulated/0/Android/data/com.guojiang.meitu.boys/effect/growing.zip");
//		return;
		File cacheFile = mGifCache.get(url);
		if (cacheFile.exists()) {
			loadingListener.onLoadingSuccess(cacheFile.getAbsolutePath());
			Log.i(TAG, "动效资源已有缓存");
			return;
		}
		final String cacheFileAbsPath = cacheFile.getAbsolutePath() + EFFECT_TEMP_FILE_SUBIFX;
		mLoadTask.loadTask(url, cacheFileAbsPath, loadingListener);
	}


	/**
	 * 加载动效礼物，并解析动效数据
	 *
	 * @param url
	 * @param listener
	 */
	public void loadDataForComposition(String url, final EffectComposition.OnCompositionLoadedListener listener) {
		loadData(url, new LoadingListener() {
			@Override
			public void onLoadingProcess(long total, long current) {

			}

			@Override
			public void onLoadingSuccess(String targetFilePath) {
				//如果是网络下载成功，会返回临时文件后缀
				if (targetFilePath.endsWith(EFFECT_TEMP_FILE_SUBIFX)) {
					File downloadFile = new File(targetFilePath);
					File renameFile = new File(targetFilePath.replace(EFFECT_TEMP_FILE_SUBIFX, ""));
					downloadFile.renameTo(renameFile);
					targetFilePath = renameFile.getAbsolutePath();
				}
				CompositionLoader compositionLoader = new CompositionLoader(EffectGiftLoader.this.mContext, listener);
				compositionLoader.execute(targetFilePath);
			}

			@Override
			public void onLoadingFailure(Throwable e) {
				listener.onCompositionLoaded(null);
			}
		});
	}

}
