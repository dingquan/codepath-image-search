package codepath.imagesearch.dto;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ImageResult {
	private String fullUrl;
	private String thumbUrl;

	public ImageResult(JSONObject a){
		try {
			fullUrl = a.getString("url");
			thumbUrl = a.getString("tbUrl");
		} catch (JSONException e) {
			Log.e("ImageResult", "failed to create ImageResult object", e);
		}
	}
	
	public String getFullUrl() {
		return fullUrl;
	}
	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}
	public String getThumbUrl() {
		return thumbUrl;
	}
	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}
	
	public static List<ImageResult> fromJsonArray(JSONArray a){
		List<ImageResult> results = new ArrayList<ImageResult>();
		for(int i=0; i<a.length(); i++){
			JSONObject o;
			try {
				o = a.getJSONObject(i);
				results.add(new ImageResult(o));
			} catch (JSONException e) {
				Log.e("ImageResult", "Failed to parse JSONObject", e);
			}
		}
		return results;
	}
}
