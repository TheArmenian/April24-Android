package com.partam.april24;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.partam.april24.customclasses.CategoriesAdapter;
import com.partam.april24.customclasses.CitiesAdapter;
import com.partam.april24.customclasses.April24HttpClient;

@SuppressLint("NewApi")
public class MenuFragment extends Fragment
{
	MainActivity act;

	EditText txtSearch;
	
	ImageView btnSelectCountry;
	ImageView btnSelectCategory;

	ListView listCountry; 
	ListView listCategory;
	CitiesAdapter adapterCountry;
	CategoriesAdapter adapterCategory;

	ArrayList<JSONObject> countries;
	ArrayList<JSONObject> categories; 
	ArrayList<JSONObject> selectedCategories = new ArrayList<JSONObject>();
	JSONObject selectedCountry;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
		{
			new LoadCountriesAndCategories().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} 
		else
		{
			new LoadCountriesAndCategories().execute();
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_menu, null);
		act = (MainActivity) getActivity();
		
		txtSearch = (EditText) v.findViewById(R.id.txtSearch);
		txtSearch.setOnEditorActionListener(new OnEditorActionListener() 
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) 
			{
				if (actionId == EditorInfo.IME_ACTION_SEARCH)
				{
					act.getMainFragment().doSearch(txtSearch.getText().toString());
					AppManager.getInstanse().hideKeyboard(txtSearch);
					act.openMainFragment();
					return true;
				}
				return false;
			}
		});
		
		
		v.findViewById(R.id.btnLogo).setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				act.openMainFragment();
			}
		});
		
		v.findViewById(R.id.layHome).setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				act.openMainFragment();
			}
		});
		
		v.findViewById(R.id.btnFavorites).setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				FavoriteFragment frag = new FavoriteFragment();
				act.addFragment(frag, false);
			}
		});
		
		v.findViewById(R.id.btnAboutApp).setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				AboutFragment fragment = new AboutFragment();
				act.addFragment(fragment, false);
			}
		});
		
		btnSelectCountry = (ImageView) v.findViewById(R.id.btnSelectCountry);
		btnSelectCountry.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				v.setSelected(!v.isSelected());
				
				if (v.isSelected())
				{
					btnSelectCategory.setSelected(false);
					listCountry.setVisibility(View.VISIBLE);
					listCategory.setVisibility(View.GONE);
				}
				else
				{
					listCountry.setVisibility(View.GONE);
				}
			}
		});
		
		btnSelectCategory = (ImageView) v.findViewById(R.id.btnSelectCategory);
		btnSelectCategory.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				v.setSelected(!v.isSelected());
				
				if (v.isSelected())
				{
					btnSelectCountry.setSelected(false);
					listCategory.setVisibility(View.VISIBLE);
					listCountry.setVisibility(View.GONE);
				}
				else
				{
					listCategory.setVisibility(View.GONE);
				}
			}
		});

		listCountry = (ListView) v.findViewById(R.id.listCity);
		listCategory = (ListView) v.findViewById(R.id.listCategory);
		adapterCountry = new CitiesAdapter(getActivity(), null);
		adapterCategory = new CategoriesAdapter(getActivity(), null);
		listCountry.setAdapter(adapterCountry);
		listCategory.setAdapter(adapterCategory);
		
		listCountry.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{
				if (adapterCountry.itemSelected(position))
				{
					selectedCountry = countries.get(position);
					Log.e("Info", "selectedCountry ============= " + selectedCountry); 
				}
				else
				{
					selectedCountry = null;
				}
				sendRequest();
			}
		});
		
		listCategory.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{
				if (adapterCategory.itemSelected(position))
				{
					selectedCategories.add(categories.get(position));
				}
				else
				{
					selectedCategories.remove(categories.get(position));
				}
				sendRequest();
			}
		});
		
		return v;
	}
	
	void sendRequest()
	{
		MainFragment fragment = act.getMainFragment();
		act.showContent();
		if (selectedCountry != null && selectedCategories.size() == 0)
		{
	        fragment.isCanLoaded = true;
	        fragment.selectedCitylimit = 0;
	        fragment.country = selectedCountry;
	        fragment.isCanLoadedPointByCountry = true;
	        fragment.isCanLoadedPointByCityAndCategory = false;
	        fragment.isCanLoadedPointByCategory = true;
	       
		}
		else if (selectedCountry != null && selectedCategories.size() > 0)
		{
			String categoriesID = AppManager.getJsonString(selectedCategories.get(0), "id");
			for (int i = 0; i < selectedCategories.size(); i++)
			{
				categoriesID += "," + AppManager.getJsonString(selectedCategories.get(i), "id");
			}
			
	        fragment.isCanLoaded = true;
	        fragment.country = selectedCountry;
	        fragment.categoriresID = categoriesID;
	        fragment.selectedCityCategorylimit = 0;
	        fragment.isCanLoadedPointByCountry = false;
	        fragment.isCanLoadedPointByCityAndCategory = true;
	        fragment.isCanLoadedPointByCategory = false;
	       
		}
		else if (selectedCountry == null && selectedCategories.size() > 0)
		{
			String categoriesID = AppManager.getJsonString(selectedCategories.get(0), "id");
			for (int i = 0; i < selectedCategories.size(); i++)
			{
				categoriesID += "," + AppManager.getJsonString(selectedCategories.get(i), "id");
			}
			
	        fragment.isCanLoaded = true;
	        fragment.country = selectedCountry;
	        fragment.categoriresID = categoriesID;
	        fragment.selectedCategorylimit = 0;
	        fragment.isCanLoadedPointByCountry = false;
	        fragment.isCanLoadedPointByCityAndCategory = false;
	        fragment.isCanLoadedPointByCategory = true;
	        }
		else if (selectedCountry == null && selectedCategories.size() == 0)
		{
	        fragment.limit = -10;
	        fragment.isCanLoaded = true;
	        fragment.isCanLoadedPointByCountry = false;
	        fragment.isCanLoadedPointByCityAndCategory = false;
	        fragment.isCanLoadedPointByCategory = false;
		}
	    
	    fragment.arrInfo = new JSONArray();
	    fragment.viewLoader.setVisibility(View.VISIBLE);
	    fragment.sendLoadPointsRequest();
	}
	
	class LoadCountriesAndCategories extends AsyncTask<Void, Void, Void>
	{
//		ArrayList<HashMap<String, String>> arrSection;
		
		@Override
		protected Void doInBackground(Void... params)
		{
			April24HttpClient client = April24HttpClient.getInstance();
			JSONObject obj = client.loadLocationsInfo("locations");
			JSONArray arr = AppManager.getJsonArray(obj, "countries");
			
			countries = new ArrayList<JSONObject>(arr.length()); 
			for (int i = 0; i < arr.length(); i++) 
			{
				countries.add(AppManager.getJsonObject(arr, i));
			}
			Collections.sort(countries, new CountriesComparator());
//			Log.e("Info", "countries = " + countries.toString());
			
			arr = client.loadCategoriesInfo("categories");
			categories = new ArrayList<JSONObject>();
			for (int i = 0; i < arr.length(); i++)
			{
				categories.add(AppManager.getJsonObject(arr, i));
			}
			Collections.sort(categories, new CategoriesComparator());
//			Log.e("Info", "categories = " + categories.toString());
			AppManager.getInstanse().categories = categories;
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
			adapterCountry.reloadData(countries);
			adapterCategory.reloadData(categories);
		}
	}
	
	class LocationsComparator implements Comparator<JSONObject>
	{
		@Override
		public int compare(JSONObject tr1, JSONObject tr2)
		{
			String ob1 = AppManager.getJsonString(tr1, "name");
			String ob2 = AppManager.getJsonString(tr2, "name");
			return ob1.compareTo(ob2);
		}
	}
	
	class CountriesComparator implements Comparator<JSONObject>
	{
		@Override
		public int compare(JSONObject tr1, JSONObject tr2)
		{
			String ob1 = AppManager.getJsonString(tr1, "name");
			String ob2 = AppManager.getJsonString(tr2, "name");
			return ob1.compareTo(ob2);
		}
	}
	
	class CategoriesComparator implements Comparator<JSONObject>
	{
		@Override
		public int compare(JSONObject tr1, JSONObject tr2)
		{
			String ob1 = AppManager.getJsonString(tr1, "title");
			String ob2 = AppManager.getJsonString(tr2, "title");
			return ob1.compareTo(ob2);
		}
	}
	
//	class LogoutTask extends AsyncTask<Void, Void, Boolean>
//	{
//		@Override
//		protected Boolean doInBackground(Void... params)
//		{
//			PartamHttpClient client = PartamHttpClient.getInstance();
//			return client.logout();
//		}
//	}
}
