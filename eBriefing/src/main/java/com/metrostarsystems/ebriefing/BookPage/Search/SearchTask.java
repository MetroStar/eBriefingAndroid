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

package com.metrostarsystems.ebriefing.BookPage.Search;

import java.util.ArrayList;

import com.radaee.pdf.Page.Finder;
import android.os.AsyncTask;

public class SearchTask extends AsyncTask<String, Void, ArrayList<SearchObject>> {

	private SearchTaskCompletedListener mListener;
	private int mPageNumber = 0;
	private com.radaee.pdf.Page mPage;
	
	
	
	public SearchTask(SearchTaskCompletedListener listener, int pageNumber, com.radaee.pdf.Page page) {
		mListener = listener;
		mPageNumber = pageNumber;
		mPage = page;
	}
	
	
	
	@Override
	protected ArrayList<SearchObject> doInBackground(String... params) {
		ArrayList<SearchObject> results = new ArrayList<SearchObject>();
		
		
		mPage.ObjsStart();
		
	    Finder mFinder = mPage.FindOpen(params[0], false, true);
	    
	    if(mFinder != null) { 
    		int finds = mFinder.GetCount();
    		
    		for(int j = 0 ; j < finds ; j++)  { 
    			//System.err.println("page " + (i+1));
    			int foundIndex = mFinder.GetFirstChar(j);
    			int phraseStartIndex = foundIndex - 50 < 0 ? 0 : foundIndex - 50;
    			int phraseEndIndex = foundIndex + 50 < mPage.ObjsGetCharCount() ? foundIndex + 50 : mPage.ObjsGetCharCount() - 1;
		
    			results.add(new SearchObject(mPageNumber, mPage.ObjsGetString(phraseStartIndex, phraseEndIndex)));
    		}
    		
    		mFinder.Close();
	    }
	     
	    return results;
	}

	@Override
	protected void onPostExecute(ArrayList<SearchObject> result) {
		super.onPostExecute(result);
		
		mListener.OnSearchCompleted(result);
	}
	
	

	public static interface SearchTaskCompletedListener {
		public void OnSearchCompleted(ArrayList<SearchObject> objects);
	}
}
