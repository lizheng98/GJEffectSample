package com.gj.file.load;

/**
 * Created by Administrator on 2016/11/11.
 * 下载任务类
 */
public interface LoadTask {

	/**
	 * 下载任务
	 *
	 * @param uri
	 * @param targetFilePath 生成文件路径
	 * @param listener
	 */
	void loadTask(String uri, String targetFilePath, LoadingListener listener);
}
