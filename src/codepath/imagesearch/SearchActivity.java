package codepath.imagesearch;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;
import codepath.imagesearch.SearchFilterDialog.SearchFilterDialogListener;
import codepath.imagesearch.dto.ImageResult;
import codepath.imagesearch.dto.JsonUtil;
import codepath.imagesearch.dto.SearchFilter;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SearchActivity extends Activity implements SearchFilterDialogListener {
	private static final String BASE_URL = "https://ajax.googleapis.com/ajax/services/search/images";
	
	private GridView gvImages;
	private List<ImageResult> imageResults = new ArrayList<ImageResult>();
	private AsyncHttpClient client = new AsyncHttpClient();
	private ImageResultArrayAdapter imageAdapter;
	private SearchFilter filter;
	private SearchView searchView;
	
	private SharedPreferences prefs;
	
	private String queryText;
	private int offset=0; //page start offset
	private int resultSz=8; //size of each API result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		setupViews();
		
		prefs = this.getSharedPreferences("com.codepath.imagesearch", Context.MODE_PRIVATE);

		imageAdapter = new ImageResultArrayAdapter(this, imageResults);
		gvImages.setAdapter(imageAdapter);
		gvImages.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(getApplicationContext(), ImageDisplayActivity.class);
				ImageResult imageResult = imageResults.get(position);
				i.putExtra("result", JsonUtil.toJson(imageResult));
				startActivity(i);
			}
			
		});
		gvImages.setOnScrollListener(new EndlessScrollListener(){

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
				customLoadMoreDataFromApi(page); 
                // or customLoadMoreDataFromApi(totalItemsCount); 	
			}
			
		});
	}
	
    // Append more data into the adapter
    private void customLoadMoreDataFromApi(int offset) {
      // This method probably sends out a network request and appends new data items to your adapter. 
      // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
      // Deserialize API response and then construct new objects to append to the adapter
    	this.offset = offset;

    	searchImage();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				queryText = query;
				
				// reset the results
				imageAdapter.clear();
				offset = 0;
				searchImage();
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);
    }
    
	private void setupViews(){
		gvImages = (GridView)findViewById(R.id.gvImages);
	}
	
	
	private Boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	
	private RequestParams buildRequestParams(){		
		RequestParams params = new RequestParams();
		params.put("rsz", String.valueOf(resultSz));
		params.put("start", String.valueOf(offset*resultSz));
		params.put("v", "1.0");
		params.put("q", Uri.encode(queryText));
		if (filter != null){
			String color = filter.getColor();
			if (color != null && !color.isEmpty()){
				params.put("imgcolor", color);
			}
			String type = filter.getType();
			if (type != null && !type.isEmpty()){
				params.put("imgtype", type);
			}
			String size = filter.getSize();
			if (size != null && !size.isEmpty()){
				params.put("imgsz", size);
			}
			String site = filter.getSite();
			if (site != null && !site.isEmpty()){
				params.put("as_sitesearch", site);
			}
		}
		return params;
	}
	
	private void searchImage(){
		if (!isNetworkAvailable()){
			Toast.makeText(this, "hmm... looks like you have zero bar", Toast.LENGTH_SHORT).show();
			return;
		}

		if (queryText == null || queryText.isEmpty()){
			Toast.makeText(this, R.string.no_query_string, Toast.LENGTH_SHORT).show();
		}
		
		RequestParams params = buildRequestParams();
		
		Log.d("SearchActivity", "requestUrl: " + BASE_URL + ", params: " + params.toString());
		
		client.get(BASE_URL, params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject response) {
				Log.d("SearchActivity", "responseJson: " + response.toString());
				JSONArray imageJsonResults = null;
				try{
					imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
					imageAdapter.addAll(ImageResult.fromJsonArray(imageJsonResults));
					Log.d("SearchActivity", JsonUtil.toJson(imageResults));
				}
				catch(JSONException e){
					e.printStackTrace();
				}
			}
		});
	}
	
	public void showFilterDialog(MenuItem mi){
		FragmentManager fm = getFragmentManager();
		SearchFilterDialog searchFilterDialog = SearchFilterDialog
				.newInstance(getString(R.string.filter_title), prefs);
		searchFilterDialog.show(fm, "fragment_search_filter");
	}

	@Override
	public void onFinishDialog(String filterJson) {
		filter = (SearchFilter) JsonUtil.fromJson(filterJson, SearchFilter.class);
		// reset the results
		imageAdapter.clear();
		this.offset = 0;
		
		searchImage();
		
	}
}
