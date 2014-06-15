package codepath.imagesearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import codepath.imagesearch.dto.JsonUtil;
import codepath.imagesearch.dto.SearchFilter;

public class FilterActivity extends Activity {

	private Spinner spImgSize;
	private Spinner spImgType;
	private Spinner spImgColor;
	private EditText etSite;
	private SearchFilter filter = new SearchFilter();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter);
		
		setupViews();
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
		
		Intent data = new Intent();
		data.putExtra("filter", JsonUtil.toJson(filter));
		setResult(RESULT_OK, data);
		finish();
	}
	
}
