/*
Copyright (C) 2017 MetroStar Systems

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

The full license text can be found is the included LICENSE file.

You can freely use any of this software which you make publicly
available at no charge.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

package com.metrostarsystems.ebriefing.Dashboard.Sort;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;

public class FragmentSort extends DialogFragment {

	private MainApplication		mApp;
	
	private Button 					mSortAscendingButton;
	private Button 					mSortDescendingButton;
	
	private Spinner					mSortSpinner;
	private SortSpinnerAdapter		mSortSpinnerAdapter;
	
	private SortOption		mSortOption = SortOption.TITLE;
	private SortDirection	mSortDirection = SortDirection.DESCENDING;
	
	private boolean					mChoiceChanged = false;
	
	public static final FragmentSort newInstance() {
		FragmentSort fragment = new FragmentSort();
	   
	    return fragment;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		     
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.activity_dashboard_sort, container, false);
		
		mApp = (MainApplication) getActivity().getApplicationContext();
		
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		mSortSpinner = (Spinner) view.findViewById(R.id.spinner_sort);
		
		mSortOption = mApp.data().sort();
		mSortDirection = mApp.data().sortDirection();
		
		mSortSpinnerAdapter = new SortSpinnerAdapter(getActivity(), SortOption.options());
		mSortSpinner.setAdapter(mSortSpinnerAdapter);
		
		mSortSpinner.setSelection(mSortOption.id());
		
		mSortSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mSortOption = (SortOption) mSortSpinner.getSelectedItem();
				
				mChoiceChanged = true;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		mSortAscendingButton = (Button) view.findViewById(R.id.button_sort_ascending);
		
		if(mSortDirection == SortDirection.ASCENDING) {
			mSortAscendingButton.setBackgroundResource(R.drawable.button_overview_description_active);
			mSortAscendingButton.setTextColor(Color.WHITE);
		}
		
		
		mSortAscendingButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mChoiceChanged = true;
				
				mSortDirection = SortDirection.ASCENDING;
				
				mSortDescendingButton.setBackgroundResource(R.drawable.button_overview_chapters);
				mSortDescendingButton.setTextColor(Color.BLACK);
				
				mSortAscendingButton.setBackgroundResource(R.drawable.button_overview_description_active);
				mSortAscendingButton.setTextColor(Color.WHITE);
			
			}
			
		});
		
		mSortDescendingButton = (Button) view.findViewById(R.id.button_sort_descending);
		
		if(mSortDirection == SortDirection.DESCENDING) {
			mSortDescendingButton.setBackgroundResource(R.drawable.button_overview_description_active);
			mSortDescendingButton.setTextColor(Color.WHITE);
		}
		
		mSortDescendingButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mChoiceChanged = true;
				
				mSortDirection = SortDirection.DESCENDING;
				
				mSortAscendingButton.setBackgroundResource(R.drawable.button_overview_description);
				mSortAscendingButton.setTextColor(Color.BLACK);
				
				mSortDescendingButton.setBackgroundResource(R.drawable.button_overview_chapters_active);
				mSortDescendingButton.setTextColor(Color.WHITE);
				
			}
			
		});
		
		Button doneButton = (Button) view.findViewById(R.id.button_done);
		doneButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mChoiceChanged) {
					mApp.data().setSort(mSortOption);
					mApp.data().setSortDirection(mSortDirection);
					mApp.dashBoard().refresh();
					mChoiceChanged = false;
				}
				dismiss();
			}
			
		});
		
		return view;
	}

}
