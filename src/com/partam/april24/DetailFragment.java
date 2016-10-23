package com.partam.april24;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.partam.april24.customclasses.DetailPagerAdapter;
import com.partam.april24.customclasses.FB;
import com.partam.april24.customclasses.MediaInfo;
import com.partam.april24.customclasses.April24HttpClient;
import com.partam.april24.customclasses.TwitterActivity;
import com.partam.april24.customclasses.WebImage;
import com.partam.april24.image_loader.ImageLoader;
import com.pinterest.pinit.PinItButton;
import com.viewpagerindicator.CirclePageIndicator;

@SuppressLint("NewApi")
public class DetailFragment extends Fragment implements OnItemClickListener
{
	public interface DetailListener
	{
		public void closeDone();
	}

	private DetailListener listener = null;

	public void setOnDetailEventListener(final DetailListener listener)
	{
		this.listener = listener;
	}

	JSONObject mainInfo;
	JSONObject webInfo;
	ArrayList<HashMap<String, String>> detailInfoArray;
	JSONArray pictures;
	JSONArray videos;
	JSONArray comments;
	JSONArray checkIns;
	MainActivity mainActivity;
	private UiLifecycleHelper uiHelper;

	String category;

	View viewLoader;
	ViewPager pager;
	CirclePageIndicator indicator;
	TextView txtName;
	TextView txtCity;
	View layMap;
	Button btnDirections;
	TextView txtCategory;
	WebView webView;
	ListView list;
	Button btnLogin;
	View viewPostComment;
	ImageView imgProfile;
	EditText txtComment;
	View btnPost;

	ImageButton btnCheckinWithSelfie;
	Bitmap bmpSelife;
	int imgSelfieWidth;
	RelativeLayout relChecks;

	ImageLoader loader = new ImageLoader();
	ListAdapter adapter;
	LayoutInflater inflater;

	View detailMain;
	View viewAllCheckins;

	ImageView imgRight;

	int detailCount = 0;
	FB fb;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{

		this.inflater = inflater;
		View v = inflater.inflate(R.layout.fragment_detail, null);
		mainActivity = (MainActivity) getActivity();
		initActionBar(v);
		viewLoader = v.findViewById(R.id.viewLoader);
		list = (ListView) v.findViewById(R.id.list);
		adapter = new ListAdapter();
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		
		uiHelper = new UiLifecycleHelper(mainActivity, null);
		uiHelper.onCreate(savedInstanceState);

		if (AppManager.getJsonObject(mainInfo, "category") != null)
		{
			category = AppManager.getJsonString(AppManager.getJsonObject(mainInfo, "category"), "title");
		}

		//		viewAllCheckins = inflater.inflate(R.layout.view_all_checkins, null);
		//		viewAllCheckins.setOnClickListener(new OnClickListener() {
		//			
		//			@Override
		//			public void onClick(View v) {
		//				
		//				relChecks.performClick();
		//			}
		//		});

		detailMain = inflater.inflate(R.layout.item_detail_main, null);
		txtName = (TextView) detailMain.findViewById(R.id.txtName);
		txtCity = (TextView) detailMain.findViewById(R.id.txtCity);
		txtCategory = (TextView) detailMain.findViewById(R.id.txtCategory);
		webView = (WebView) detailMain.findViewById(R.id.webView);

		pager = (ViewPager) detailMain.findViewById(R.id.pager);
		indicator = (CirclePageIndicator) detailMain.findViewById(R.id.indicator);
		LayoutParams params = (LayoutParams) pager.getLayoutParams();
		params.height = AppManager.getInstanse().screenWidth;
		params.width = AppManager.getInstanse().screenWidth;
		pager.setLayoutParams(params);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
		{
			new GetInfo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} 
		else
		{
			new GetInfo().execute();
		}


		fb = new FB(getActivity());

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		imgSelfieWidth = (int) (metrics.density * 100);

		return v;
	}

	public Bitmap decodeFile(File f) { 
		try {
			//decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			//Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 480;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				//			scale++;
				scale *= 2;
			}

			//decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o.inJustDecodeBounds = false;
			//		Log.e("Info", "scale = " + scale);
			o2.inSampleSize = scale;
			//		Log.e("Info", "width = " + BitmapFactory.decodeStream(new FileInputStream(f), null, o2).getWidth() + "height = " + BitmapFactory.decodeStream(new FileInputStream(f), null, o2).getWidth());
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2); // 320*240  320*320
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	private void initActionBar(View v)
	{
		ImageView imgLeft = (ImageView) v.findViewById(R.id.imgLeft);
		imgLeft.setImageResource(R.drawable.btn_back);
		imgLeft.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				((MainActivity) getActivity()).closeDetailFragment();
			}
		});

		imgRight = (ImageView) v.findViewById(R.id.imgRight);
		imgRight.setImageResource(R.drawable.btn_favorite_empty);

		if(AppManager.getInstanse().localFavorites.length() != 0)
		{
			boolean isEqual = false;
			for(int i = 0; i < AppManager.getInstanse().localFavorites.length(); i++)
			{
				try {
//					if(mainInfo.toString().contentEquals(AppManager.getInstanse().localFavorites.get(i).toString()))
					if(AppManager.getJsonString(mainInfo, "id").contentEquals(AppManager.getJsonString(AppManager.getInstanse().localFavorites.getJSONObject(i), "id")))
					{
						imgRight.setImageResource(R.drawable.btn_favorite_full);
						isEqual = true;
						Log.e("Info", "isEqual == true");
						break;
					}

				} catch (NotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(!isEqual){
				imgRight.setImageResource(R.drawable.btn_favorite_empty);
				Log.e("Info", "isEqual == false ");
			}
		}
		else
			imgRight.setImageResource(R.drawable.btn_favorite_empty);

		imgRight.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{

				if(AppManager.getInstanse().localFavorites.length() == 0)
				{
					AppManager.getInstanse().localFavorites.put(mainInfo);
					imgRight.setImageResource(R.drawable.btn_favorite_full);
				}
				else
				{
					boolean isRemovable = false;
					for(int i = 0; i < AppManager.getInstanse().localFavorites.length(); i++)
					{
						try {
							if(mainInfo.toString().contentEquals(AppManager.getInstanse().localFavorites.get(i).toString()))
							{
								AppManager.getInstanse().localFavorites.remove(i);
								imgRight.setImageResource(R.drawable.btn_favorite_empty); 
								isRemovable = true;
								break;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					if(!isRemovable)
					{
						AppManager.getInstanse().localFavorites.put(mainInfo);
						imgRight.setImageResource(R.drawable.btn_favorite_full);
					}
				}

				viewLoader.setVisibility(View.VISIBLE);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
				{
					new SaveTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} 
				else
				{
					new SaveTask().execute();
				}


				//				boolean favorite = !imgRight.isSelected();
				//				if (imgRight.isSelected())
				//				{
				//					imgRight.setImageResource(R.drawable.btn_favorite_full);
				//					FavoriteFragment.webInfoArray.
				//				}
				//				else
				//				{
				//					imgRight.setImageResource(R.drawable.btn_favorite_empty);
				//				}
				//				FavoriteFragment.webInfoArray.

				//				
			}
		});

		ImageView imgShare = (ImageView) v.findViewById(R.id.imgShare);
		imgShare.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				final String [] items        = new String [] {"Facebook", "Pinterest", "Twitter", "VKontakte", "Cancel"};
				ArrayAdapter<String> adapter = new ArrayAdapter<String> (getActivity(), android.R.layout.select_dialog_item,items);
				AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());

				builder.setTitle("Share via"); 
				builder.setAdapter( adapter, new DialogInterface.OnClickListener()
				{
					public void onClick( DialogInterface dialog, int item ) 
					{
						String description = Html.fromHtml(AppManager.getJsonString(webInfo, "description")).toString();
						MediaInfo mi = ((DetailPagerAdapter) pager.getAdapter()).getMediaInfo(indicator.getCurrentPosition());
						String link = "https://play.google.com/store/apps/details?id=com.partam.april24"; 
						String picture = mi.imageUrl;
						switch (item)
						{
						//facebook
						case 0:
							fb.post("", txtName.getText().toString(), "April24", description, link, picture, null);
//							if (FacebookDialog.canPresentShareDialog(mainActivity,
//									FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
//								// Publish the post using the Share Dialog
//								FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(mainActivity)
//								.setLink("http://neremembers1915.org" + AppManager.getJsonString(selectedEvent, "page_url"))
//								.setName(AppManager.getJsonString(selectedEvent, "title"))
//								.setPicture("http://neremembers1915.org" + AppManager.getJsonString(selectedEvent, "image"))
//								.build();
//								uiHelper.trackPendingDialogCall(shareDialog.present());
//
//							} else {
//								// Fallback. For example, publish the post using the Feed Dialog
//								publishFeedDialog();
//							};
							break;
							//pinterest
						case 1:
							PinItButton pinIt = new PinItButton(getActivity());
							pinIt.setImageUrl(picture);
							pinIt.setUrl(link);
							pinIt.setDescription(txtName.getText() + "\n" + description);
							pinIt.performClick();
							break;
							//twitter
						case 2:
							TwitterActivity.tweetText = txtName.getText().toString();
							TwitterActivity.imgUrl = picture;
							startActivity(new Intent(getActivity(), TwitterActivity.class));
							break;
							//VKontakte
						case 3:
							VKActivity.link = link;
							VKActivity.imgUrl = picture;
							VKActivity.text = txtName.getText() + "\n" + description;
							startActivity(new Intent(getActivity(), VKActivity.class));
							break;

						default:
							break;
						}
					}
				} );

				final AlertDialog dialog = builder.create();
				dialog.show();
			}
		});
	}

	public void loggedIn()
	{
		btnLogin.setVisibility(View.GONE);
		viewPostComment.setVisibility(View.VISIBLE);
		JSONObject userInfo = AppManager.getJsonObject(AppManager.getInstanse().userInfo, "user");
		String pictureUrl = AppManager.getJsonString(userInfo, "picture");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
		{
			new WebImage(imgProfile, pictureUrl).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} 
		else
		{
			new WebImage(imgProfile, pictureUrl).execute();
		} 
	}

	private void showDetailInfo()
	{
		//		if (AppManager.getInstanse().userFavorites.length() > 0)
		//		{
		//			int id = AppManager.getJsonInt(webInfo, "id");
		//			JSONArray favorites = AppManager.getJsonArray(AppManager.getInstanse().userFavorites, "favorites");
		//			for (int i = 0; i < favorites.length(); i++) 
		//			{
		//				int favID = AppManager.getJsonInt(AppManager.getJsonObject(favorites, i), "id");
		//				if (favID == id)
		//				{
		//					imgRight.setSelected(true);
		//					imgRight.setImageResource(R.drawable.btn_favorite_full);
		//					break;
		//				}
		//			}
		//		}


Log.e("Info", "webinfo >>>>>>>> " + webInfo.toString());
		txtName.setText(AppManager.getJsonString(webInfo, "name"));
		txtCity.setText(AppManager.getJsonString(webInfo, "city"));
		txtCategory.setText(category);

		pictures = AppManager.getJsonArray(webInfo, "pictures");
		videos = AppManager.getJsonArray(webInfo, "videos");
		comments = AppManager.getJsonArray(webInfo, "comments");
		checkIns = AppManager.getJsonArray(webInfo, "checkins");

		loader.showDefaultImage(false);
		ArrayList<MediaInfo> arrInfo = new ArrayList<MediaInfo>(pictures.length() + videos.length());
		for (int i = 0; i < pictures.length(); i++) 
		{
			MediaInfo info = new MediaInfo();
			try {
				info.imageUrl = AppManager.getInstanse().getUrlForCrop(pictures.getString(i));
				Log.e("Info", "info.imageUrl ==== = " + info.imageUrl);
				info.loader = loader; 
				arrInfo.add(info);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		for (int i = 0; i < videos.length(); i++) 
		{
			MediaInfo info = new MediaInfo();
			try {
				JSONObject currentVideoInfo = videos.getJSONObject(i);
				info.imageUrl = currentVideoInfo.getString("thumb");
				info.videoID = currentVideoInfo.getString("id");
				info.isVideo = true;
				info.loader = loader;
				arrInfo.add(info);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		pager.setAdapter(new DetailPagerAdapter(getFragmentManager(), arrInfo)); 
		if (arrInfo.size() > 1) 
		{
			indicator.setViewPager(pager);
			indicator.setSnap(true);

			list.setOnTouchListener(new View.OnTouchListener()  
			{
				public boolean onTouch(View v, MotionEvent event) 
				{
					list.requestDisallowInterceptTouchEvent(false);
					return false;
				}
			}); 

			pager.setOnTouchListener(new View.OnTouchListener() 
			{
				public boolean onTouch(View v, MotionEvent event)
				{
					list.requestDisallowInterceptTouchEvent(true);
					return false;
				}
			});
		}

		String html = AppManager.getJsonString(webInfo, "description");
		if (html.length() > 0)
		{
			String mimeType = "text/html";
			String encoding = "UTF-8";
			webView.loadDataWithBaseURL("", html, mimeType, encoding, ""); 
		}
		else
		{
			webView.setVisibility(View.GONE);
		}
	}

	
	class GetInfo extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params)
		{
			April24HttpClient client = April24HttpClient.getInstance();
			webInfo = client.loadDetailInfo(AppManager.getJsonInt(mainInfo, "id"));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) 
		{
			super.onPostExecute(result);
			if (getView() == null) {
				return;
			}
			viewLoader.setVisibility(View.GONE);

			if (webInfo != null)
			{
				showDetailInfo();
				//				showMap();
				adapter.reloadData();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (listener != null) {
			listener.closeDone();
		}
		loader.clearMemoryCache();
		//		SupportMapFragment f = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapDetail);
		//		if (f != null && !getActivity().isFinishing())
		//			getFragmentManager().beginTransaction().remove(f).commit();
	}

	class ListAdapter extends BaseAdapter
	{
		public void reloadData() 
		{
			detailCount = getDetailCount();
			notifyDataSetChanged();
		}

		@Override
		public int getCount() 
		{
			return detailCount + getCommentsCount() + 3;
		}

		@Override
		public Object getItem(int position)
		{
			return position;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			if (position == 0)
			{
				return detailMain;
			}

			//			if (position == detailCount + 1) 
			//			{
			//				return viewAllCheckins;  
			//			}

			//			if (position == detailCount + getCommentsCount() + 2) 
			//			{
			//				return loginPostView;  
			//			}

			View v;

			//			if (position <= detailCount)
			//			{
			//				position = position - 1;
			//				HashMap<String, String> hm = detailInfoArray.get(position);
			//				String type = hm.get("type");
			//				if (!type.contentEquals("address"))
			//				{
			v = inflater.inflate(R.layout.item_detail_info, null);
			//				}
			//				else
			//				{
			//					v = inflater.inflate(R.layout.item_detail_info_direction, null);
			//				}
			//				((ImageView) v.findViewById(R.id.imgIcon)).setImageResource(getImageID(type));
			//				((TextView) v.findViewById(R.id.txt)).setText(hm.get("info"));
			//			}
			//			else
			//			{
			//				position = position - detailCount - 2;
			//				v = inflater.inflate(R.layout.item_comment, null);
			//				JSONObject obj = AppManager.getJsonObject(comments, position);
			//				((TextView) v.findViewById(R.id.txtUserName)).setText(AppManager.getJsonString(obj, "name"));
			//				((TextView) v.findViewById(R.id.txtComment)).setText(AppManager.getJsonString(obj, "message"));
			//
			//				Date date;
			//				try {
			//					date = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.US).parse(AppManager.getJsonString(obj, "created"));
			//					((TextView) v.findViewById(R.id.txtClock)).setText(android.text.format.DateFormat.format("kk:mm", date));
			//				} catch (ParseException e) {
			//					e.printStackTrace();
			//				}
			//				JSONObject user = AppManager.getJsonObject(obj, "user");
			//				loader.DisplayImage(AppManager.getJsonString(user, "picture"), ((ImageView) v.findViewById(R.id.imgProfile)));
			//			}


			return v;
		}
	}

	int getDetailCount()
	{
		if (webInfo == null) {
			return 0;
		}
		detailInfoArray = new ArrayList<>();
		int count = 0;

		if (AppManager.getJsonString(webInfo, "address").length() > 0)
		{
			HashMap<String, String> hm = new HashMap<>(2);
			hm.put("type", "address");
			hm.put("info", AppManager.getJsonString(webInfo, "address"));
			detailInfoArray.add(hm);
			count++;
		}

		if (AppManager.getJsonString(webInfo, "tel").length() > 0)
		{
			HashMap<String, String> hm = new HashMap<>(2);
			hm.put("type", "tel");
			hm.put("info", AppManager.getJsonString(webInfo, "tel"));
			detailInfoArray.add(hm);
			count++;
		}

		if (AppManager.getJsonString(webInfo, "website").length() > 0)
		{
			HashMap<String, String> hm = new HashMap<>(2);
			hm.put("type", "website");
			hm.put("info", AppManager.getJsonString(webInfo, "website"));
			detailInfoArray.add(hm);
			count++;
		}

		if (AppManager.getJsonString(webInfo, "email").length() > 0)
		{
			HashMap<String, String> hm = new HashMap<>(2);
			hm.put("type", "email");
			hm.put("info", AppManager.getJsonString(webInfo, "email"));
			detailInfoArray.add(hm);
			count++;
		}

		if (AppManager.getJsonString(webInfo, "facebook_url").length() > 0)
		{
			HashMap<String, String> hm = new HashMap<>(2);
			hm.put("type", "facebook_url");
			hm.put("info", AppManager.getJsonString(webInfo, "facebook_url"));
			detailInfoArray.add(hm);
			count++;
		}

		if (AppManager.getJsonString(webInfo, "twitter_url").length() > 0)
		{
			HashMap<String, String> hm = new HashMap<>(2);
			hm.put("type", "twitter_url");
			hm.put("info", AppManager.getJsonString(webInfo, "twitter_url"));
			detailInfoArray.add(hm);
			count++;
		}

		if (AppManager.getJsonString(webInfo, "instagram_url").length() > 0)
		{
			HashMap<String, String> hm = new HashMap<>(2);
			hm.put("type", "instagram_url");
			hm.put("info", AppManager.getJsonString(webInfo, "instagram_url"));
			detailInfoArray.add(hm);
			count++;
		}

		return count;
	}

	int getCommentsCount()
	{
		return comments != null ? comments.length() : 0;
	}

	int getImageID(String type)
	{
		int id = 0;
		//		if (type.contentEquals("address")) 
		//		{
		//			id = R.drawable.icon_location;
		//		}
		//		else if (type.contentEquals("tel")) 
		//		{
		//			id = R.drawable.icon_phone;
		//		}
		//		else if (type.contentEquals("website")) 
		//		{
		//			id = R.drawable.icon_open_web;
		//		}
		//		else if (type.contentEquals("email")) 
		//		{
		//			id = R.drawable.icon_email;
		//		}
		//		else if (type.contentEquals("facebook_url")) 
		//		{
		//			id = R.drawable.icon_facebook;
		//		}
		//		else if (type.contentEquals("twitter_url")) 
		//		{
		//			id = R.drawable.icon_twitter;
		//		}
		//		else if (type.contentEquals("instagram_url")) 
		//		{
		//			id = R.drawable.icon_instagram;
		//		}
		return id;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{

		if (position == 0 || (position == detailCount + getCommentsCount() + 1)) 
		{
			return;
		}

		if (position <= detailCount)
		{
			position = position - 1;				
			HashMap<String, String> hm = detailInfoArray.get(position);
			String type = hm.get("type");
			//			if (type.contentEquals("address"))
			//			{
			//				Intent intent = new Intent(getActivity(), NavigationActivity.class);
			//				intent.putExtra("webInfo", webInfo.toString());
			//				intent.putExtra("category", category);
			//				startActivity(intent);
			//			}
			//			else
			if (type.contentEquals("tel"))
			{
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + hm.get("info")));
				startActivity(intent);
			}
			else if (type.contentEquals("website") || type.contentEquals("facebook_url") || type.contentEquals("twitter_url") || type.contentEquals("instagram_url"))
			{
				AppManager.getInstanse().openUrl(getActivity(), hm.get("info"));
			}
			else if (type.contentEquals("email"))
			{
				Intent email = new Intent(Intent.ACTION_SEND);		  
				email.putExtra(Intent.EXTRA_EMAIL, new String[] { hm.get("info") });
				email.setType("message/rfc822");
				startActivity(email);
			}
		}
		else
		{
			position = position - detailCount - 1;
		}
	}

	//	class FavoriteTask extends AsyncTask<Boolean, Void, Boolean>
	//	{
	//		@Override
	//		protected Boolean doInBackground(Boolean... params)
	//		{
	//			Boolean favorite = params[0];
	//			JSONObject obj = PartamHttpClient.getInstance().addOrRemoveFavorites(AppManager.getJsonInt(webInfo, "id"), favorite);
	//			if (AppManager.getJsonBoolean(obj, "success"))
	//			{
	//				PartamHttpClient.getInstance().loadUserFavorites();
	//				return true;
	//			}
	//			return false;
	//		}
	//
	//		@Override
	//		protected void onPostExecute(Boolean result)
	//		{
	//			super.onPostExecute(result);
	//			viewLoader.setVisibility(View.GONE);
	//
	//			if (result)
	//			{
	//				imgRight.setSelected(!imgRight.isSelected());
	//				if (imgRight.isSelected())
	//				{
	//					imgRight.setImageResource(R.drawable.btn_favorite_full);
	//				}
	//				else
	//				{
	//					imgRight.setImageResource(R.drawable.btn_favorite_empty);
	//				}
	//			}
	//		}..
	//	}

	//	class CommentTak extends AsyncTask<Void, Void, Boolean>
	//	{
	//		@Override
	//		protected Boolean doInBackground(Void... params)
	//		{
	//			int pointId = AppManager.getJsonInt(webInfo, "id");
	//			JSONObject obj = PartamHttpClient.getInstance().addComment(txtComment.getText().toString(), pointId);
	//			if (AppManager.getJsonBoolean(obj, "success"))
	//			{
	//				webInfo = PartamHttpClient.getInstance().loadDetailInfo(pointId);
	//				comments = AppManager.getJsonArray(webInfo, "comments");
	//				return true;
	//			}
	//			return false;
	//		}
	//
	//		@Override
	//		protected void onPostExecute(Boolean result)
	//		{
	//			super.onPostExecute(result);
	//			viewLoader.setVisibility(View.GONE);
	//			txtComment.setText("");
	//
	//			if (result)
	//			{
	//				adapter.reloadData();
	//			}
	//		}
	//	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e("Info", "detail fragment onResume!!!!!!!!!!!!!!!!!!!!!");

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
