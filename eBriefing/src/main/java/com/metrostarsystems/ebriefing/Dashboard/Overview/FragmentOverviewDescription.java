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

package com.metrostarsystems.ebriefing.Dashboard.Overview;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentOverviewDescription extends Fragment {
	
	private MainApplication		mApp;
	private Book 				mBook;
	
	public static final FragmentOverviewDescription newInstance(String bookId) {
		FragmentOverviewDescription fragment = new FragmentOverviewDescription();
	    Bundle bundle = new Bundle();
	    bundle.putString("bookid", bookId);
	    fragment.setArguments(bundle);
	    return fragment;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
        View rootView = inflater.inflate(R.layout.fragment_available_overview_description, null);
        
        mApp = (MainApplication) getActivity().getApplicationContext();
        
        String book_id = getArguments().getString("bookid");
        mBook = mApp.data().database().booksDatabase().book(book_id);
        
        TextView descriptionTextView = (TextView) rootView.findViewById(R.id.textView_description);
        descriptionTextView.setText(mBook.description());
        
        return rootView;
    }
}
