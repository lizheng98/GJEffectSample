package com.gj.file.load;

import java.io.File;

/**
 * Created by Administrator on 2016/11/10.
 */

public interface FileCache {

	void put(String url);

	File get(String url);
}
