package com.gj.effect.util;


import android.text.TextUtils;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.BindException;
import java.net.ConnectException;
import java.net.HttpRetryException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.PortUnreachableException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;

/**
 * 日志打印类.
 *
 * @author
 */
public class EvtLog {
	public static boolean IS_DEBUG_LOGGABLE = true;
	public static boolean IS_ERROR_LOGGABLE = true;

	private static final int INDEX_CLASS = 0;
	private static final int INDEX_FILE = 1;
	private static final int INDEX_METHOD = 2;
	private static final int INDEX_LINE = 3;

	/**
	 * 输出debug信息
	 *
	 * @param tag 标签
	 * @param msg 信息
	 */
	public static void d(String tag, String msg) {
		if (IS_DEBUG_LOGGABLE) {
			Log.d(tag, formatLogToString(tag, msg));
		}
	}

	/**
	 * 输出debug信息
	 *
	 * @param cls 执行类
	 * @param tag 标签
	 * @param msg 信息
	 */
	public static void d(Class<?> cls, String tag, String msg) {
		if (IS_DEBUG_LOGGABLE) {
			if (cls != null) {
				String[] classInfo = getInvokeClassInfo(cls.getName());
				Log.d(tag, formatLogToString(classInfo, tag, msg));
				return;
			}
			Log.d(tag, formatLogToString(tag, msg));
		}
	}

	/**
	 * 输出info信息
	 *
	 * @param tag 标签
	 * @param msg 信息
	 */
	public static void i(String tag, String msg) {
		if (IS_DEBUG_LOGGABLE) {
			Log.i(tag, formatLogToString(tag, msg));
		}
	}


	/**
	 * 输出info信息
	 *
	 * @param cls 执行类
	 * @param tag 标签
	 * @param msg 信息
	 */
	public static void i(Class<?> cls, String tag, String msg) {
		if (IS_DEBUG_LOGGABLE) {
			if (cls != null) {
				String[] classInfo = getInvokeClassInfo(cls.getName());
				Log.i(tag, formatLogToString(classInfo, tag, msg));
				return;
			}
			Log.i(tag, formatLogToString(tag, msg));
		}
	}

	/**
	 * 输出warn信息
	 *
	 * @param tag 标签
	 * @param msg 信息
	 */
	public static void w(String tag, String msg) {
		if (IS_DEBUG_LOGGABLE) {
			Log.w(tag, formatLogToString(tag, msg));
		}
	}

	/**
	 * 输出错误信息
	 *
	 * @param tag       标签
	 * @param exception 输出异常信息到控制台
	 */
	public static void w(String tag, Throwable exception) {
		if (IS_DEBUG_LOGGABLE) {
			Log.w(tag, exception);
		}
	}

	/**
	 * 输出warn信息
	 *
	 * @param cls 执行类
	 * @param tag 标签
	 * @param msg 信息
	 */
	public static void w(Class<?> cls, String tag, String msg) {
		if (IS_DEBUG_LOGGABLE) {
			if (cls != null) {
				String[] classInfo = getInvokeClassInfo(cls.getName());
				Log.w(tag, formatLogToString(classInfo, tag, msg));
				return;
			}
			Log.w(tag, formatLogToString(tag, msg));
		}
	}

	/**
	 * 输出warn信息
	 *
	 * @param exception 异常信息
	 * @param tag       标签
	 * @param msg       信息
	 */
	public static void w(Throwable exception, String tag, String msg) {
		if (IS_DEBUG_LOGGABLE) {
			if (exception != null) {
				String[] classInfo = getInvokeClassInfo(exception);
				Log.w(tag, formatLogToString(classInfo, tag, msg));
				return;
			}
			Log.w(tag, formatLogToString(tag, msg));
		}
	}


	/**
	 * 输出error信息并在程序中toast显示，该错误不会记录在日志文件中
	 *
	 * @param tag 标签
	 * @param msg 信息
	 */
	public static void e(String tag, String msg) {
		if (IS_ERROR_LOGGABLE) {
			Log.e(tag, formatLogToString(tag, msg));
		}
	}

	/**
	 * @param tag
	 * @param msg
	 * @param isShowDialog
	 */
	public static void e(String tag, String msg, boolean isShowDialog) {
		if (IS_ERROR_LOGGABLE) {
			Log.e(tag, formatLogToString(tag, msg));
		}
		if (isShowDialog) {
			// 是否提示toast
		}
	}

	/**
	 * 输出错误信息
	 *
	 * @param tag       标签
	 * @param exception
	 */
	public static void e(String tag, Throwable exception) {
		if (IS_ERROR_LOGGABLE) {
			Log.e(tag, "", exception);
		}
	}

	/**
	 * 输出error信息
	 *
	 * @param cls 执行类
	 * @param tag 标签
	 * @param msg 信息
	 */
	public static void e(Class<?> cls, String tag, String msg) {
		if (IS_ERROR_LOGGABLE) {
			if (cls != null) {
				String[] classInfo = getInvokeClassInfo(cls.getName());
				Log.e(tag, formatLogToString(classInfo, tag, msg));
				return;
			}
			Log.e(tag, formatLogToString(tag, msg));
		}
	}

	/**
	 * 输出error信息
	 *
	 * @param tag       标签
	 * @param msg       信息
	 * @param exception 异常信息
	 */
	public static void e(String tag, String msg, Throwable exception) {
		if (IS_ERROR_LOGGABLE) {
			if (exception != null) {
				String[] classInfo = getInvokeClassInfo(exception);
				Log.e(tag, formatLogToString(classInfo, tag, msg), exception);
				return;
			}
			Log.e(tag, formatLogToString(tag, msg));
		}
	}

	// 根据sdk文档列出的java.net包中会抛出的所有异常，来进行对比
	static boolean isNetworkException(Throwable exception) {
		if (exception == null) {
			return false;
		}
		if (exception instanceof BindException || exception instanceof ConnectException
				|| exception instanceof HttpRetryException || exception instanceof MalformedURLException
				|| exception instanceof NoRouteToHostException || exception instanceof PortUnreachableException
				|| exception instanceof ProtocolException || exception instanceof SocketException
				|| exception instanceof SocketTimeoutException || exception instanceof UnknownHostException
				|| exception instanceof UnknownServiceException || exception instanceof URISyntaxException) {
			return true;
		}
		return false;
	}

	/**
	 * 返回类信息.
	 *
	 * @param className 类名
	 */
	static String[] getInvokeClassInfo(String className) {
		if (className == null || TextUtils.isEmpty(className)) {
			return null;
		}
		String[] result = null;
		int index = -1;
		StackTraceElement[] stack = (new Throwable()).getStackTrace();
		if (stack != null) {
			for (int i = 0; i < stack.length; i++) {
				if (stack[i].getClassName().contains(className)) {
					index = i;
					break;
				}
			}
			if (index > -1) {
				result = new String[4];
				// 类的完全限定名
				result[INDEX_CLASS] = stack[index].getClassName();
				// 源文件名
				result[INDEX_FILE] = stack[index].getFileName();
				// 方法名
				result[INDEX_METHOD] = stack[index].getMethodName();
				// 行号
				result[INDEX_LINE] = String.valueOf(stack[index].getLineNumber());
			}
		}
		return result;
	}

	/**
	 * 返回异常信息.
	 *
	 * @param exception 异常
	 */
	static String[] getInvokeClassInfo(Throwable exception) {
		if (exception == null) {
			return null;
		}
		String[] result = null;
		StackTraceElement[] stack = exception.getStackTrace();
		if (stack != null) {
			result = new String[4];
			// 类的完全限定名
			result[INDEX_CLASS] = stack[2].getClassName();
			// 源文件名
			result[INDEX_FILE] = stack[2].getFileName();
			// 方法名
			result[INDEX_METHOD] = stack[2].getMethodName();
			// 行号
			result[INDEX_LINE] = String.valueOf(stack[2].getLineNumber());
		}
		return result;
	}

	/**
	 * 返回异常的详细信息字符串.
	 *
	 * @param exception 异常
	 */
	static String getStackTraceToString(Throwable exception) {
		if (exception == null) {
			return null;
		}
		String result = null;
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		result = sw.toString();
		return result;
	}

	static String formatLogToString(String[] classInfo, String tag, String msg) {
		StringBuffer sb = new StringBuffer();
		sb.append("[" + tag + "]");

		if (classInfo != null) {
			sb.append("[" + classInfo[INDEX_CLASS] + "]");
			sb.append("[" + classInfo[INDEX_METHOD] + "]");
			sb.append("[line:" + classInfo[INDEX_LINE] + "] \n");
		}
		sb.append("[" + msg + "]");
		return sb.toString();
	}

	static String formatLogToString(String tag, String msg) {
		StringBuffer sb = new StringBuffer();
		sb.append("[" + tag + "]");
		sb.append("[" + msg + "]");
		return sb.toString();
	}
}
