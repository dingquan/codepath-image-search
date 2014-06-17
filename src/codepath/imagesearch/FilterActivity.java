package codepath.imagesearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import codepath.imagesearch.dto.JsonUtil;
import codepath.imagesearch.dto.SearchFilter;

public class FilterActivity extends Activity {

	private Spinner spImgSize;
	private Spinner spImgType;
	private Spinner spImgColor;
	private EditText etSite;
	private SearchFilter filter = new SearchFilter();
	private SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter);
		prefs = this.getSharedPreferences("com.codepath.tipcalculator", Context.MODE_PRIVATE);
		setupViews();

		//load from preference
		loadFilterSettings();
	}

	private void loadFilterSettings() {
		String filterSettings = prefs.getString("filters", "");
		if (filterSettings != null && !filterSettings.isEmpty()){
			filter = (SearchFilter) JsonUtil.fromJson(filterSettings, SearchFilter.class);
			setSpinnerToValue(spImgSize, filter.getSize());
			setSpinnerToValue(spImgType, filter.getType());
			setSpinnerToValue(spImgColor, filter.getColor());
			etSite.setText(filter.getSite());
		}
	}

	private void setSpinnerToValue(Spinner spinner, String value) {
		int index = 0;
		SpinnerAdapter adapter = spinner.getAdapter();
		for (int i = 0; i < adapter.getCount(); i++) {
			if (adapter.getItem(i).equals(value)) {
				index = i;
			}
		}
		spinner.setSelection(index);
	}
	
	private void setupViews() {
		spImgSize = (Spinner)findViewById(R.id.spImgSize);
		spImgType = (Spinner)findViewById(R.id.spImgType);
		spImgColor = (Spinner)findViewById(R.id.spColor);
		etSite = (EditText)findViewById(R.id.etSiteFilter);
	}
	
	public void onSubmit(View v){
		filter.setSize(spImgSize.getSelectedItem().toString());
		filter.setColor(spImgColor.getSelectedItem().toString());
		filter.setType(spImgType.getSelectedItem().toString());
		filter.setSite(etSite.getText().toString());
		
		//save the filter
		prefs.edit().putString("filters", JsonUtil.toJson(filter)).commit();
		Intent data = new Intent();
		data.putExtra("filter", JsonUtil.toJson(filter));
		setResult(RESULT_OK, data);
		finish();
	}
	
}
