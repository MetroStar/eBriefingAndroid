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

import java.util.ArrayList;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SortSpinnerAdapter extends ArrayAdapter<SortOption> {

	private MainApplication mApp;
	private Context			mContext;
	
	public SortSpinnerAdapter(Context context, ArrayList<SortOption> objects) {
		super(context, R.layout.fragment_mybooks_sort_spinner_item);
		
		mApp = (MainApplication) context.getApplicationContext();
		mContext = context;
		
		addAll(objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		// If convertview is null create the layout
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.fragment_mybooks_sort_spinner_item, parent, false);
			
			// Create the view holder
			holder = new ViewHolder();
			holder.mTextTextView = (TextView) convertView.findViewById(R.id.textView_text);
			
			convertView.setTag(holder);
		} else {
			// Reuse the view holder
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.mTextTextView.setText(getItem(position).toString());
		
		return convertView;
	}
	
	public void refresh(ArrayList<SortOption> objects) {
		clear();
		addAll(objects);
	}
	
	private class ViewHolder {
		public TextView mTextTextView;
	}

}
