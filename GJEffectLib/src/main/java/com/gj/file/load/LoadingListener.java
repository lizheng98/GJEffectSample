package com.gj.file.load;

/**
 * Created by Administrator on 2016/11/11.
 */

/**
 * 文件加载监听类
 */
public interface LoadingListener {

	void onLoadingProcess(long total, long current);

	void onLoadingSuccess(String targetFilePath);

	void onLoadingFailure(Throwable e);
}
