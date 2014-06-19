package codepath.imagesearch;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import codepath.imagesearch.dto.JsonUtil;
import codepath.imagesearch.dto.SearchFilter;

public class SearchFilterDialog extends DialogFragment {
	private Spinner spImgSize;
	private Spinner spImgType;
	private Spinner spImgColor;
	private EditText etSite;
	private Button btnSave;
	
	private SearchFilter filter = new SearchFilter();
	private SharedPreferences prefs;

	public SearchFilterDialog() {
		// Empty constructor required for DialogFragment
	}
	
    public interface SearchFilterDialogListener {
        void onFinishDialog(String filterJson);
    }
    
	public static SearchFilterDialog newInstance(String title, SharedPreferences prefs) {
		SearchFilterDialog frag = new SearchFilterDialog();
		frag.prefs = prefs;
		Bundle args = new Bundle();
		args.putString("title", title);
		frag.setArguments(args);
		return frag;
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
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_search_filter, container);
		String title = getArguments().getString("title", "Enter Name");
		getDialog().setTitle(title);

		setupViews(view);
		
		//load from preference
		loadFilterSettings();
		
		btnSave.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				filter.setSize(spImgSize.getSelectedItem().toString());
				filter.setColor(spImgColor.getSelectedItem().toString());
				filter.setType(spImgType.getSelectedItem().toString());
				filter.setSite(etSite.getText().toString());
				
				String filterJson = JsonUtil.toJson(filter);
				//save the filter
				prefs.edit().putString("filters", filterJson).commit();
				
				SearchFilterDialogListener activity = (SearchFilterDialogListener)getActivity();
				activity.onFinishDialog(filterJson);
				dismiss();
			}
			
		});
		return view;
	}
	

	private void setupViews(View v) {
		spImgSize = (Spinner)v.findViewById(R.id.spImgSize);
		spImgType = (Spinner)v.findViewById(R.id.spImgType);
		spImgColor = (Spinner)v.findViewById(R.id.spColor);
		etSite = (EditText)v.findViewById(R.id.etSiteFilter);
		btnSave = (Button)v.findViewById(R.id.btnSave);
	}
	
}
