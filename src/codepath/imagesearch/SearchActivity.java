package codepath.imagesearch;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import codepath.imagesearch.dto.ImageResult;
import codepath.imagesearch.dto.JsonUtil;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class SearchActivity extends Activity {

	EditText etQuery;
	Button btnSearch;
	GridView gvImages;
	List<ImageResult> imageResults = new ArrayList<ImageResult>();
	AsyncHttpClient client = new AsyncHttpClient();
	ImageResultArrayAdapter imageAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		setupViews();
		imageAdapter = new ImageResultArrayAdapter(this, imageResults);
		gvImages.setAdapter(imageAdapter);
	}
	
	private void setupViews(){
		etQuery = (EditText)findViewById(R.id.etQuery);
		btnSearch = (Button)findViewById(R.id.btnSearch);
		gvImages = (GridView)findViewById(R.id.gvImages);
	}
	
	public void searchImage(View v){
		String queryText = etQuery.getText().toString();
		Toast.makeText(this, queryText, Toast.LENGTH_SHORT).show();
		
		String url = "https://ajax.googleapis.com/ajax/services/search/images?" +  "start=" + 0 + "&v=1.0&q=" + Uri.encode(queryText);
		Log.d("SearchActivity", "requestUrl: " + url);
		client.get(url, new JsonHttpResponseHandler(){
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
}
