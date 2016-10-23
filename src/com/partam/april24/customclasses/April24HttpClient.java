package com.partam.april24.customclasses;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.partam.april24.AppManager;

public class April24HttpClient
{
	private static April24HttpClient client = null;
	private static final String BaseURL = "http://april24.com/api/v1";//"http://partam.partam.com/api/v1";

	public static April24HttpClient getInstance() {
		if(client == null) {
			client = new April24HttpClient();
		}
		return client;
	}

	public JSONArray loadAllMapPoints()
	{
		String url = BaseURL + "/main/map/points";

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);

		JSONArray arr = new JSONArray();

		try
		{
			HttpResponse response = httpClient.execute(httpGet);
			String responseString = EntityUtils.toString(response.getEntity());

			if (response.getStatusLine().getStatusCode() == 0)
			{
				return new JSONArray();
			}

			arr = new JSONArray(responseString);
			String newSize = "w=" + AppManager.getInstanse().screenWidth + "&h=" + AppManager.getInstanse().screenWidth;
			for (int i = 0; i < arr.length(); i++) 
			{
				JSONObject obj = AppManager.getJsonObject(arr, i);
				String picture = AppManager.getJsonString(obj, "picture");
				obj.put("picture", picture.replace("w=400&h=400", newSize));
			}
		} catch (Exception e) { }

		return arr;
	}

	public JSONArray loadMainInfo(int limits, int offset)
	{
		String url = BaseURL + "/mains/" + limits + "/limits/" + offset + "/offset";

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);

		JSONArray arr = new JSONArray();

		try
		{
			HttpResponse response = httpClient.execute(httpGet);
			String responseString = EntityUtils.toString(response.getEntity());

			if (response.getStatusLine().getStatusCode() == 0)
			{
				return new JSONArray();
			}

			arr = new JSONArray(responseString);
			String newSize = "w=" + AppManager.getInstanse().screenWidth + "&h=" + AppManager.getInstanse().screenWidth;
			for (int i = 0; i < arr.length(); i++) 
			{
				JSONObject obj = AppManager.getJsonObject(arr, i);
				String picture = AppManager.getJsonString(obj, "picture");
				obj.put("picture", picture.replace("w=400&h=400", newSize));
			}

		} catch (Exception e) { }

		return arr;
	}

	public JSONArray loadSearchPoints(String searchText)
	{
		String url = BaseURL + "/mains/" + searchText + "/search/point";
		url = url.replace(" ", "%20");
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);

		JSONArray arr = new JSONArray();

		try
		{
			HttpResponse response = httpClient.execute(httpGet);
			String responseString = EntityUtils.toString(response.getEntity());

			if (response.getStatusLine().getStatusCode() == 0)
			{
				return new JSONArray();
			}

			arr = new JSONArray(responseString);
			String newSize = "w=" + AppManager.getInstanse().screenWidth + "&h=" + AppManager.getInstanse().screenWidth;
			for (int i = 0; i < arr.length(); i++) 
			{
				JSONObject obj = AppManager.getJsonObject(arr, i);
				String picture = AppManager.getJsonString(obj, "picture");
				obj.put("picture", picture.replace("w=400&h=400", newSize));
			}

		} catch (Exception e) { }

		return arr;
	}

	public JSONArray loadPointByCountries(String countryAbv, int limits, int offset)
	{
		String url = BaseURL + "/mains/" + countryAbv +"/countries/" + limits + "/limits/" + offset + "/offset";
		Log.e("Info", "loadPointByCountries url = " + url);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);

		JSONArray arr = new JSONArray();

		try
		{
			HttpResponse response = httpClient.execute(httpGet);
			String responseString = EntityUtils.toString(response.getEntity());

			if (response.getStatusLine().getStatusCode() == 0)
			{
				return new JSONArray();
			}

			arr = new JSONArray(responseString);
			String newSize = "w=" + AppManager.getInstanse().screenWidth + "&h=" + AppManager.getInstanse().screenWidth;
			for (int i = 0; i < arr.length(); i++) 
			{
				JSONObject obj = AppManager.getJsonObject(arr, i);
				String picture = AppManager.getJsonString(obj, "picture");
				obj.put("picture", picture.replace("w=400&h=400", newSize));
			}
		} catch (Exception e) { }

		return arr;
	}

	public JSONArray loadPointByCategoriesAndCountry(String categoryId, String countryAbv, int limit, int offset)
	{
		String url = BaseURL + "/mains/"  + categoryId + "/cats/" + countryAbv +"/countries/" + limit + "/limits/" + offset + "/offset";
		Log.e("Info", "loadPointByCategoriesAndCountry url = " + url);
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);

		JSONArray arr = new JSONArray();

		try
		{
			HttpResponse response = httpClient.execute(httpGet);
			String responseString = EntityUtils.toString(response.getEntity());

			if (response.getStatusLine().getStatusCode() == 0)
			{
				return new JSONArray();
			}

			arr = new JSONArray(responseString);
			String newSize = "w=" + AppManager.getInstanse().screenWidth + "&h=" + AppManager.getInstanse().screenWidth;
			for (int i = 0; i < arr.length(); i++) 
			{
				JSONObject obj = AppManager.getJsonObject(arr, i);
				String picture = AppManager.getJsonString(obj, "picture");
				obj.put("picture", picture.replace("w=400&h=400", newSize));
			}
		} catch (Exception e) { }

		return arr;
	}

	public JSONArray loadPointByCategory(String categories, int limit)
	{
		String url = BaseURL + "/mains/" + categories + "/points/" + limit + "/starts/10/limits/name/orders/asc/direction";

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);

		JSONArray arr = new JSONArray();

		try
		{
			HttpResponse response = httpClient.execute(httpGet);
			String responseString = EntityUtils.toString(response.getEntity());

			if (response.getStatusLine().getStatusCode() == 0)
			{
				return new JSONArray();
			}

			arr = new JSONArray(responseString);
			String newSize = "w=" + AppManager.getInstanse().screenWidth + "&h=" + AppManager.getInstanse().screenWidth;
			for (int i = 0; i < arr.length(); i++) 
			{
				JSONObject obj = AppManager.getJsonObject(arr, i);
				String picture = AppManager.getJsonString(obj, "picture");
				obj.put("picture", picture.replace("w=400&h=400", newSize));
			}
		} catch (Exception e) { }

		return arr;
	}

	public JSONArray loadCategoriesInfo(String categories)
	{
		String url = BaseURL + "/main/" + categories;

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);

		JSONArray arrCategories = new JSONArray();

		try
		{
			HttpResponse response = httpClient.execute(httpGet);
			String responseString = EntityUtils.toString(response.getEntity());

			if (response.getStatusLine().getStatusCode() == 0)
			{
				return new JSONArray();
			}

			arrCategories = new JSONArray(responseString);

		} catch (Exception e) { }

		return arrCategories;
	}

	public JSONObject loadLocationsInfo(String locations)
	{
		String url = BaseURL + "/main/" + locations;

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);

		JSONObject arrCategories = new JSONObject();

		try
		{
			HttpResponse response = httpClient.execute(httpGet);
			String responseString = EntityUtils.toString(response.getEntity());

			if (response.getStatusLine().getStatusCode() == 0)
			{
				return new JSONObject();
			}

			arrCategories = new JSONObject(responseString);

		} catch (Exception e) { }

		return arrCategories;
	}

	public JSONObject loadDetailInfo(int detailID)
	{
		String url = BaseURL + "/mains/" + detailID + "/point";
		Log.e("Info", "url = " + url);

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);

		JSONObject arrCategories = new JSONObject();

		try
		{
			HttpResponse response = httpClient.execute(httpGet);
			String responseString = EntityUtils.toString(response.getEntity());
			Log.e("Info", "responseString = " + responseString);
			if (response.getStatusLine().getStatusCode() == 0)
			{
				return new JSONObject();
			}

			arrCategories = new JSONObject(responseString);

		} catch (Exception e) { }

		return arrCategories;
	}
	
	
	public JSONObject loadUserInfo()
	{
		String url = BaseURL + "/users/" + AppManager.getInstanse().token + "/profile";

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);

		JSONObject userInfo = new JSONObject();

		try
		{
			HttpResponse response = httpClient.execute(httpGet);
			String responseString = EntityUtils.toString(response.getEntity());
			Log.e("Info", "User info response = " + responseString);
			if (response.getStatusLine().getStatusCode() == 0)
			{
				return new JSONObject();
			}

			userInfo = new JSONObject(responseString);
			AppManager.getInstanse().userInfo = userInfo;
//			loadUserFavorites();

		} catch (Exception e) { }

		return userInfo;
	}
	
    public void changeUserAvatar(byte [] picture)
    {
        String url = BaseURL + "/users/" + AppManager.getInstanse().token + "/images";

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        reqEntity.addPart("user[file]", new ByteArrayBody(picture, "userAvatar.png"));
        httppost.setEntity(reqEntity);

        try {
            HttpResponse response = httpClient.execute(httppost);
            String responseString = EntityUtils.toString(response.getEntity());
    		Log.d("myLogs", "changeUserAvatar responseString = " + responseString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}