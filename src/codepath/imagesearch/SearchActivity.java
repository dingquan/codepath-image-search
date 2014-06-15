package codepath.imagesearch;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import codepath.imagesearch.dto.ImageResult;
import codepath.imagesearch.dto.JsonUtil;
import codepath.imagesearch.dto.SearchFilter;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SearchActivity extends Activity {
	private static final int REQUEST_CODE = 10;
	private static final String BASE_URL = "https://ajax.googleapis.com/ajax/services/search/images";
	
	private EditText etQuery;
	private Button btnSearch;
	private GridView gvImages;
	private List<ImageResult> imageResults = new ArrayList<ImageResult>();
	private AsyncHttpClient client = new AsyncHttpClient();
	private ImageResultArrayAdapter imageAdapter;
	private SearchFilter filter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		setupViews();
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
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        return true;
    }
    
	private void setupViews(){
		etQuery = (EditText)findViewById(R.id.etQuery);
		btnSearch = (Button)findViewById(R.id.btnSearch);
		gvImages = (GridView)findViewById(R.id.gvImages);
	}
	
	public void onClickSearch(View v){
		searchImage();
	}
	
	private void searchImage(){
		String queryText = etQuery.getText().toString();
//		Toast.makeText(this, queryText, Toast.LENGTH_SHORT).show();
		
//		String url = "https://ajax.googleapis.com/ajax/services/search/images?rsz=8&" +  "start=" + 0 + "&v=1.0&q=" + Uri.encode(queryText);
		RequestParams params = new RequestParams();
		params.put("rsz", "8");
		params.put("start", "0");
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
		
		Log.d("SearchActivity", "requestUrl: " + BASE_URL + ", params: " + params.toString());

		client.get(BASE_URL, params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject response) {
				Log.d("SearchActivity", "responseJson: " + response.toString());
				JSONArray imageJsonResults = null;
				try{
					imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
					imageResults.clear();
					imageAdapter.addAll(ImageResult.fromJsonArray(imageJsonResults));
					Log.d("SearchActivity", JsonUtil.toJson(imageResults));
				}
				catch(JSONException e){
					e.printStackTrace();
				}
			}
		});
	}
	
	public void onFilterAction(MenuItem mi){
		Intent i = new Intent(SearchActivity.this, FilterActivity.class);
		startActivityForResult(i, REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  // REQUEST_CODE is defined above
	  if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
	     // Extract name value from result extras
	     String filterData = data.getExtras().getString("filter");
	     // Toast the name to display temporarily on screen
	     Toast.makeText(this, filterData, Toast.LENGTH_SHORT).show();
	     
	     filter = (SearchFilter)JsonUtil.fromJson(filterData, SearchFilter.class);
	     searchImage();
	  }
	} 
}
