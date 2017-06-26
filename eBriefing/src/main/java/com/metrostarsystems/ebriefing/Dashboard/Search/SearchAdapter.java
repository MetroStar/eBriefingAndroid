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

package com.metrostarsystems.ebriefing.Dashboard.Search;

import java.util.ArrayList;
import java.util.List;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

public class SearchAdapter extends ArrayAdapter<String> {
	
	private static final String TAG = SearchAdapter.class.getSimpleName();

	private MainApplication mApp;
	private Context mContext;
	private Filter filter;
	
	public SearchAdapter(Context context, List<String> objects) {
		super(context, R.layout.actionbar_search_item);
		
		addAll(objects);
		
		mContext = context;
		
		mApp = (MainApplication) context.getApplicationContext();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.actionbar_search_item, parent, false);
		}

		TextView entry = (TextView) convertView.findViewById(R.id.search_item_entry);
		

		final String name = this.getItem(position);
		
		
		entry.setText(name);
			

		return convertView;

	}

	@Override
	public Filter getFilter() {
		if(filter == null) {
			filter = new TitleFilter();
		}
		return filter;
	}
	
	private class TitleFilter extends Filter {
		 
	    @Override
	    protected FilterResults performFiltering(CharSequence constraint) {
	        List<String> list = mApp.data().database().booksDatabase().getMyBooksTitles();
	        FilterResults result = new FilterResults();
	        
	        if(constraint != null) {
		        String substr = constraint.toString().toLowerCase();
		        // if no constraint is given, return the whole list
		        if (substr == null || substr.length() == 0) {
		            result.values = list;
		            result.count = list.size();
		        } else {
		            // iterate over the list and find if the title matches the constraint. if it does, add to the result list
		            final ArrayList<String> retList = new ArrayList<String>();
		            for (String entry : list) {
		                 if(entry.toLowerCase().contains(constraint)) {
		                        retList.add(entry);
		                 }
		            }
		            result.values = retList;
		            result.count = retList.size();
		        }
	        }
	        return result;
	    }
	 
	    @SuppressWarnings("unchecked")
	    @Override
	    protected void publishResults(CharSequence constraint, FilterResults results) {
	    	if(mApp == null || mApp.data() == null) {
	    		return;
	    	}
	    	
	      // we clear the adapter and then populate it with the new results
	        SearchAdapter.this.clear();
	        if (results.count > 0) {
	            for (String o : (ArrayList<String>) results.values) {
	            	SearchAdapter.this.add(o);
	            }
	        }
	            
	        mApp.data().setSearchText((String) constraint);
	        mApp.dashBoard().refresh();
	    }
	 
	}
}
