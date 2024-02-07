package support.util.net;


import support.util.CollectUtil;
import support.util.IoUtil;
import support.util.StringUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author zhuangly
 */
public class HttpClient {
	public static enum RequestType {
		GET, POST
	};

	private String encode = "UTF-8";
	private String cookie;

	public HttpClient() {
	}
	public HttpClient(String cookie) {
		this.cookie = cookie;
	}

	public String getString(RequestType requestType, String url, Map<String, Object> paramMap) {
		String paramString = joinParamToStr(paramMap);
		return getString(requestType, url, paramString, null);
	}
	public String getString(RequestType requestType, String url, Map<String, Object> paramMap, Map<String, String> headerMap) {
		String paramString = joinParamToStr(paramMap);
		return getString(requestType, url, paramString, headerMap);
	}
	public String getStringByJson(String url, String paramJson) {
		return getString(RequestType.POST, url, paramJson, null);
	}
	public String getStringByJson(String url, String paramJson, Map<String, String> headerMap) {
		if (headerMap == null) {
			headerMap = CollectUtil.getMap();
		}
		headerMap.put("Content-Type", "application/json;charset=UTF-8");
		return getString(RequestType.POST, url, paramJson, headerMap);
	}

	private String getString(RequestType requestType, String url, String paramString, Map<String, String> headerMap) {
		HttpURLConnection urlConnection = null;
		try {
			urlConnection = openURLConnection(requestType, url, paramString, headerMap);
			// urlConnection.getResponseCode();

			String readStrByInputStream = IoUtil.readStrByInputStream(urlConnection.getInputStream(), encode);

			String setCookie = urlConnection.getHeaderField("Set-Cookie");
			if (StringUtil.isNotEmpty(setCookie)) {
				this.cookie = setCookie;
			}

			return readStrByInputStream;
		} catch (Exception e) {
			if (urlConnection != null) {
				return IoUtil.readStrByInputStream(urlConnection.getErrorStream(), encode);
			} else {
				throw new RuntimeException(e);
			}
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
				urlConnection = null;
			}
		}
	}


	public HttpURLConnection openURLConnection(RequestType requestType, String url, String paramString, Map<String, String> headerMap) throws MalformedURLException, IOException {
		HttpURLConnection urlConnection;
		URL urlObj = null;
		if (RequestType.GET.equals(requestType)) {
			urlObj = new URL(url + "?" + paramString);
		} else {
			urlObj = new URL(url);
		}

		System.out.println(requestType.toString() + " -> " + urlObj.toString());

		urlConnection = (HttpURLConnection) urlObj.openConnection();
		urlConnection.setRequestMethod(requestType.toString());
		urlConnection.setRequestProperty("Charset", "UTF-8");
		// 允许输入输出
		// 有些请求不需要输出
		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);
		// 禁止使用缓存
		urlConnection.setUseCaches(false);
		// 模拟谷歌浏览器的头部
		urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.117 Safari/537.36");
		urlConnection.addRequestProperty("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
		// 设置连接时间，读取时间
		urlConnection.setConnectTimeout(15 * 1000);
		urlConnection.setReadTimeout(60 * 1000);

		if (StringUtil.isNotEmpty(cookie)) {
			urlConnection.setRequestProperty("cookie", cookie);
		}
		if (headerMap != null) {
			for (Entry<String, String> entry : headerMap.entrySet()) {
				urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}

		// getOutputStream,getInputStream,会隐含的进行connect()方法,所以在开发中不调用connect()也可以)。
		urlConnection.connect();

		if (RequestType.POST.equals(requestType) && StringUtil.isNotEmpty(paramString)) {
			OutputStream outputStream = urlConnection.getOutputStream();
			outputStream.write(paramString.getBytes());
			outputStream.flush();
			outputStream.close();
		}
		return urlConnection;
	}

	public String joinParamToStr(Map<String, Object> paramMap) {
		if (paramMap == null || paramMap.isEmpty()) {
			return "";
		}

		StringBuilder paramBuilder = new StringBuilder();

		for (Iterator<Entry<String, Object>> paramIterator = paramMap.entrySet().iterator(); paramIterator.hasNext();) {
			Entry<String, Object> nextEntry = paramIterator.next();

			String paramKey = nextEntry.getKey();
			Object paramValue = nextEntry.getValue();

			if (paramValue == null) {
				paramValue = "";
			}

			paramBuilder.append(paramKey);
			paramBuilder.append("=");
			paramBuilder.append(paramValue);

			if (paramIterator.hasNext()) {
				paramBuilder.append("&");
			}
		}
		return paramBuilder.toString();
	}

	@SuppressWarnings("unused")
	private String encoderParam(String param) {
		try {
			param = URLEncoder.encode(param, encode);
			return param;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}