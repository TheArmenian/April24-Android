package com.partam.april24;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.mobeta.android.dslv.DragSortListView;
import com.partam.april24.DetailFragment.DetailListener;
import com.partam.april24.customclasses.FavoritesAdapter;

@SuppressLint("NewApi")
public class FavoriteFragment extends Fragment implements OnItemClickListener
{
	View viewLoader;
	ArrayList<JSONObject> webInfoArray;

	FavoritesAdapter adapter;
	DragSortListView list;

	EditText txtSearch;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_favorite, null);
		initActionBar(v);

		viewLoader = v.findViewById(R.id.viewLoader);
		viewLoader.setVisibility(View.GONE);

		View search = inflater.inflate(R.layout.view_search_favorite, null);
		txtSearch = (EditText) search.findViewById(R.id.txtSearch);
		txtSearch.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				if (s.toString().length() == 0)
				{
					showFavorites();
					return;
				}
				
				webInfoArray.clear();
				String search = s.toString().toLowerCase(Locale.US);
				for (int i = 0; i < AppManager.getInstanse().localFavorites.length(); i++)
				{
					try {
						JSONObject obj = AppManager.getInstanse().localFavorites.getJSONObject(i);
						if (AppManager.getJsonString(obj, "name").toLowerCase(Locale.US).contains(search))
						{
							webInfoArray.add(obj);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					adapter.reloadData(webInfoArray, adapter.isEditMode());
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			
			@Override
			public void afterTextChanged(Editable s) { }
		});

		list = (DragSortListView) v.findViewById(R.id.list);
		list.setDropListener(onDrop);
		list.setRemoveListener(onRemove);
		list.addHeaderView(search);
		adapter = new FavoritesAdapter(getActivity(), null, ((MainActivity) getActivity()).getMainFragment().getImageLoader());
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);

		showFavorites();
		return v;
	}

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener()
	{
		@Override
		public void drop(int from, int to) 
		{
			Log.e("Info", " drop !!!!!!!!!!!" );
			
			if (from != to)
			{
				JSONObject obj = webInfoArray.get(from);
				webInfoArray.remove(from);
				webInfoArray.add(to, obj);
				adapter.reloadData(webInfoArray, true); // webInfoArray
				
			AppManager.getInstanse().localFavorites = new JSONArray();
			for(int i = 0; i< webInfoArray.size(); i++)
				AppManager.getInstanse().localFavorites.put(webInfoArray.get(i));
				
				viewLoader.setVisibility(View.VISIBLE);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
				{
					new SaveTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} 
				else
				{
					new SaveTask().execute();
				}
				
			}
		}
	};

	private DragSortListView.RemoveListener onRemove =  new DragSortListView.RemoveListener()
	{
		@Override
		public void remove(int which) 
		{
			Log.e("Info", " remove !!!!!!!!!!!" );
			
			AppManager.getInstanse().localFavorites.remove(which);
			webInfoArray.clear();
			for(int i = 0; i< AppManager.getInstanse().localFavorites.length(); i++)
				try {
					webInfoArray.add(AppManager.getInstanse().localFavorites.getJSONObject(i));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			adapter.reloadData(webInfoArray, true);
			viewLoader.setVisibility(View.VISIBLE);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
			{
				new SaveTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} 
			else
			{
				new SaveTask().execute();
			}
		}
	};
	
	@Override
	public void onPause()
	{
		super.onPause();
//		if (AppManager.getInstanse().localFavorites != null)
//		{
//			if (AppManager.getInstanse().localFavorites.length() == webInfoArray.size())
//			{
//				AppManager.getInstanse().localFavorites = new JSONArray();
//				for (int i = 0; i < webInfoArray.size(); i++)
//				{
//					AppManager.getInstanse().localFavorites.put(webInfoArray.get(i));
//				}
//				AppManager.getInstanse().save();
//			}
//		}
	}

	private void initActionBar(View v)
	{
		ImageView imgLeft = (ImageView) v.findViewById(R.id.imgLeft);
		imgLeft.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				((MainActivity) getActivity()).showMenu();
			}
		});

		ImageView imgRight = (ImageView) v.findViewById(R.id.imgRight);
		imgRight.setImageResource(R.drawable.btn_edit);
		imgRight.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				v.setSelected(!v.isSelected());
				if (v.isSelected()) {
					list.setRemoveEnabled(true);
					list.setDragEnabled(true);
				}
				else
				{
					list.setRemoveEnabled(false);
					list.setDragEnabled(false);
				}
				webInfoArray.clear();
				for(int i = 0; i < AppManager.getInstanse().localFavorites.length(); i++)
					try {
						webInfoArray.add(AppManager.getInstanse().localFavorites.getJSONObject(i));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				Log.e("Info", "webInfoArray size = " + webInfoArray.size());
				adapter.reloadData(webInfoArray, v.isSelected()); // 
			}
		});
	}

	private void showFavorites()
	{
		webInfoArray = new ArrayList<>();
		if (AppManager.getInstanse().localFavorites != null)
		{
			for (int i = 0; i < AppManager.getInstanse().localFavorites.length(); i++)
			{
				try {
					webInfoArray.add(AppManager.getInstanse().localFavorites.getJSONObject(i));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			adapter.reloadData(webInfoArray, adapter.isEditMode());
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{
		JSONObject currentInfo;
		try {
			currentInfo = AppManager.getInstanse().localFavorites.getJSONObject(position - 1);
			DetailFragment frag = new DetailFragment();
			frag.mainInfo = currentInfo;
			frag.category = AppManager.getJsonString(AppManager.getJsonObject(currentInfo, "category"), "title");
			((MainActivity) getActivity()).openDetailFragment(frag);
			frag.setOnDetailEventListener(new DetailListener()
			{
				@Override
				public void closeDone() 
				{
					showFavorites();
				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	class SaveTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params) {
			AppManager.getInstanse().save();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			viewLoader.setVisibility(View.GONE);
		}
	}

	
}
