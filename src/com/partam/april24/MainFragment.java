package com.partam.april24;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.partam.april24.customclasses.EndlessListView;
import com.partam.april24.customclasses.EndlessListView.EndlessListener;
import com.partam.april24.customclasses.MainAdapter;
import com.partam.april24.customclasses.April24HttpClient;
import com.partam.april24.image_loader.ImageLoader;

public class MainFragment extends ListFragment implements EndlessListener
{
	MainActivity act;
	MainAdapter adapter;
	
	int count = 10;
	int limit = -10;

	View viewLoader;
	View relWhite;
	
	boolean isCanLoaded;
	int selectedCitylimit;
	int selectedCityCategorylimit;
	int selectedCategorylimit;
	boolean isCanLoadedPointByCountry;
	boolean isCanLoadedPointByCityAndCategory;
	boolean isCanLoadedPointByCategory;
	JSONObject country;
	String categoriresID;
	JSONArray arrInfo = new JSONArray();
	
	boolean disableClick = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_main, null);  
		viewLoader = v.findViewById(R.id.viewLoader);
		relWhite = v.findViewById(R.id.relWhite);
		act = (MainActivity) getActivity();
		initActionBar(v);
		adapter = new MainAdapter(getActivity(), null);
		
		return v;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) 
	{
		super.onViewCreated(view, savedInstanceState);
		EndlessListView list = (EndlessListView) getListView();
		list.setLoadingView(R.layout.view_footer_loader);
		list.setListener(this);
		list.setAdapter(adapter);
		sendLoadPointsRequest();
	}
	
	private void initActionBar(View v)
	{
		v.findViewById(R.id.imgLeft).setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				act.showMenu();
			}
		});
	}
	
	public ImageLoader getImageLoader()
	{
		return adapter.getImageLoader();
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) 
	{
		super.onListItemClick(l, v, position, id);
		if (disableClick) {
			return;
		}
		disableOtherClick();
		DetailFragment frag = new DetailFragment();
		frag.mainInfo = AppManager.getJsonObject(arrInfo, position);
		Log.e("Info", "maininfo ========= " + frag.mainInfo.toString());
		act.openDetailFragment(frag);
	}
	
	private void disableOtherClick()
	{
		disableClick = true;
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() 
		{
			@Override
			public void run()
			{
				disableClick = false;
			}
		}, 1000);
	}
	
	@Override
	public void loadData() 
	{
		sendLoadPointsRequest();
	}
	
	@SuppressLint("NewApi")
	void sendLoadPointsRequest()
	{
		AppManager.getInstanse().isFilter = true;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
		{
			new GetInfo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} 
		else
		{
			new GetInfo().execute();
		}
	}
	
	class GetInfo extends AsyncTask<Void, Void, Void>
	{
		JSONArray arr; 
		@Override
		protected Void doInBackground(Void... params)
		{
			April24HttpClient client = April24HttpClient.getInstance();
			
			if (isCanLoadedPointByCountry) 
			{
				arr = client.loadPointByCountries(AppManager.getJsonString(country, "country"), 10, selectedCitylimit);
				selectedCitylimit += 10;
				return null;
			}
			if (isCanLoadedPointByCityAndCategory) 
			{
				arr = client.loadPointByCategoriesAndCountry(categoriresID, AppManager.getJsonString(country, "country"), 10, selectedCityCategorylimit);
				selectedCityCategorylimit += 10;
				return null;
			}
			if (isCanLoadedPointByCategory) 
			{
				arr = client.loadPointByCategory(categoriresID, selectedCategorylimit);
				selectedCategorylimit += 10;
				return null;
			}

			AppManager.getInstanse().isFilter = false;
			limit += 10;
			arr = client.loadMainInfo(count, limit);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) 
		{
			super.onPostExecute(result);
			relWhite.setVisibility(View.GONE);
			viewLoader.setVisibility(View.GONE);
			if (arr.length() > 0)
			{
				for (int i = 0; i < arr.length(); i++)
				{
					arrInfo.put(AppManager.getJsonObject(arr, i));
				}
				((EndlessListView) getListView()).addedNewData();
			}
			else
			{
				((EndlessListView) getListView()).addedFinalData();
			}
			adapter.reloadData(arrInfo);
		}
	}
	
	@SuppressLint("NewApi")
	public void doSearch(String searchText)
	{
		viewLoader.setVisibility(View.VISIBLE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
		{
			new GetSearchInfo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, searchText);
		} 
		else
		{
			new GetSearchInfo().execute(searchText);
		}
	}
	
	class GetSearchInfo extends AsyncTask<String, Void, Void>
	{
		JSONArray arr; 
		String searchText = "";
		@Override
		protected Void doInBackground(String... params)
		{
			searchText = params[0];
			April24HttpClient client = April24HttpClient.getInstance();
			arrInfo = new JSONArray();
			if (searchText.length() == 0)
			{
				AppManager.getInstanse().isFilter = false;
				int limit = 0;
				arr = client.loadMainInfo(count, limit);
				return null;
			}

			AppManager.getInstanse().isFilter = true;
			arr = client.loadSearchPoints(searchText);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) 
		{
			super.onPostExecute(result);
			viewLoader.setVisibility(View.GONE);
			if (arr.length() > 0)
			{
				for (int i = 0; i < arr.length(); i++)
				{
					arrInfo.put(AppManager.getJsonObject(arr, i));
				}
				
				if (searchText.length() > 0)
				{
					((EndlessListView) getListView()).addedFinalData();
				}
				else
				{
					((EndlessListView) getListView()).addedNewData();
				}
			}
			else
			{
				((EndlessListView) getListView()).addedFinalData();
			}
			adapter.reloadData(arrInfo);
		}
	}
}
