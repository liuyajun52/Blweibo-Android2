package com.blacklighting.blwebio_android.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.blacklighting.blwebio_android.internet.API;

import android.os.AsyncTask;

public class GetRequireTokenTask extends AsyncTask<Void, Void, String> {

	String baseURL = API.OAUTH_REQUEST_TOKEN_API + "?oauth_callback=oob";
	String consumer_key = API.CONSUMER_KEY;
	String consumer_secret = API.CONSUMER_SECRET;
	String nonce;
	String hmacMethod = "HMAC-SHA1";
	String version = "1.0";
	String url = "";
	String timestamp;
	String signature;
	
	@Override
	protected String doInBackground(Void... params) {
		SortedSet<KeyValuePair> paramsMap = new TreeSet<KeyValuePair>();
		Long test = System.currentTimeMillis();
		// 时间戳只有10位，需要处理
		timestamp = test.toString().substring(0, 10);
		nonce = getRandomString(8);

		paramsMap.add(new KeyValuePair(encoderStr("oauth_nonce"),
				encoderStr(nonce)));
		paramsMap.add(new KeyValuePair(encoderStr("oauth_timestamp"),
				encoderStr(timestamp)));
		paramsMap.add(new KeyValuePair(encoderStr("oauth_consumer_key"),
				encoderStr(API.CONSUMER_KEY)));
		paramsMap.add(new KeyValuePair(encoderStr("oauth_signature_method"),
				encoderStr(hmacMethod)));
		paramsMap.add(new KeyValuePair(encoderStr("oauth_version"),
				encoderStr(version)));
		paramsMap.add(new KeyValuePair(encoderStr("oauth_callback"),
				encoderStr("oob")));

		StringBuilder baseString = new StringBuilder("");

		for (KeyValuePair keyValuePair : paramsMap) {
			baseString.append(keyValuePair.key);
			baseString.append("=");
			baseString.append(keyValuePair.value);
			baseString.append("&");
		}
		baseString.deleteCharAt(baseString.length() - 1);

		String finalString = "POST&";
		finalString += encoderStr(API.OAUTH_REQUEST_TOKEN_API);
		finalString += "&";
		finalString += encoderStr(baseString.toString());

		byte[] signatureBytes = encodeHmacSHA(finalString.getBytes(),
				(encoderStr(API.CONSUMER_SECRET) + "&").getBytes());
		signature = BASE64Encoder.encode(signatureBytes);
		// signature =encoderStr( new
		// String(Base64.encode(signatureBytes,Base64.DEFAULT)));

		paramsMap
				.add(new KeyValuePair(encoderStr("oauth_signature"), signature));

		StringBuilder authHeader = new StringBuilder("OAuth ");

		for (KeyValuePair keyValuePair : paramsMap) {
			authHeader.append(encoderStr(keyValuePair.key));
			authHeader.append("=\"");
			authHeader.append(encoderStr(keyValuePair.value));
			authHeader.append("\",");
		}
		authHeader.deleteCharAt(authHeader.length() - 1);

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(baseURL);

		try {
			post.addHeader("Authorization", authHeader.toString());
			HttpResponse httpResponse = httpclient.execute(post);

			httpResponse.getStatusLine();

			StringBuilder sb = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent()), 65728);
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		// Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT)
		// .show();
	}

	/**
	 * 使用HmacSHA算法计算
	 * 
	 * @param data
	 *            字符基串
	 * @param key
	 *            密钥
	 * @return 加密后的签名（长度为16的字节数组）
	 */
	public static byte[] encodeHmacSHA(byte[] data, byte[] key) {
		String method = "HmacSHA1";
		Key k = new SecretKeySpec(key, method);
		Mac mac = null;
		try {
			mac = Mac.getInstance(method);
			mac.init(k);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}

		return mac.doFinal(data);
	}

	/*
	 * 获取随机字符
	 */
	public static String getRandomString(int length) {
		StringBuffer buffer = new StringBuffer(
				"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
		StringBuffer sb = new StringBuffer();
		Random r = new Random();
		int range = buffer.length();
		for (int i = 0; i < length; i++) {
			sb.append(buffer.charAt(r.nextInt(range)));
		}
		return sb.toString();
	}

	public static String encoderStr(String str) {
		if (str == null) {
			return "";
		}
		try {
			return URLEncoder.encode(str, "ASCII")
					// OAuth encodes some characters differently:
					.replace("+", "%20").replace("*", "%2A")
					.replace("%7E", "~");
			// This could be done faster with more hand-crafted code.
		} catch (UnsupportedEncodingException wow) {
			throw new RuntimeException(wow.getMessage(), wow);
		}
	}

	class KeyValuePair implements Comparable<KeyValuePair> {
		String key, value;

		public KeyValuePair(String key, String value) {
			super();
			this.key = key;
			this.value = value;
		}

		public int compareTo(KeyValuePair o) {
			return key.compareTo(o.key);
		}
	}

}
