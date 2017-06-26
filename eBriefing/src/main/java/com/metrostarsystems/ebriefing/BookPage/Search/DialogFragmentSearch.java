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

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Utilities;
import com.metrostarsystems.ebriefing.BookPage.ActivityPage;
import com.metrostarsystems.ebriefing.BookPage.Search.SearchTask.SearchTaskCompletedListener;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;
import com.radaee.pdf.Document;
import com.radaee.util.PDFFileStream;

import android.os.Bundle;
import android.app.Dialog;
import android.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DialogFragmentSearch extends DialogFragment implements SearchTaskCompletedListener {
	
	private MainApplication		mApp;

	private ListView			mResultsListView;
	private SearchListAdapter 	mResultsListAdapter;
	private EditText 			mSearchEditText;
	private Button				mClearButton;
	private TextView			mStatusTextView;
	
	
	private TextView			mCloseButton;
	private TextView			mSearchButton;
	
	private Book				mBook;
	private int					mCurrentPage = 0;
	private int					mTotalPages = 0;
	private static String				mSearchTerm = "";
	
	private SearchTask			mSearchTask;
	private ProgressBar			mSearchProgress;

	
	private com.radaee.pdf.Page	mPage;
	
	protected static Document mDocument = new Document();
	protected static PDFFileStream mStream = new PDFFileStream();
	
	private static ArrayList<SearchObject> mFound;

	
	public static final DialogFragmentSearch newInstance(String bookId) {
		DialogFragmentSearch fragment = new DialogFragmentSearch();
	    Bundle bundle = new Bundle();
	    bundle.putString("bookid", bookId);
	    fragment.setArguments(bundle);
	    return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.dialog_search, container);
		
		String book_id = getArguments().getString("bookid");
		mApp = (MainApplication) getActivity().getApplicationContext();
		mBook = mApp.data().database().booksDatabase().book(book_id);
		mCurrentPage = 1;
		mTotalPages = mBook.pageCount();
		
		mResultsListView = (ListView) view.findViewById(R.id.listView_results);
		mSearchEditText = (EditText) view.findViewById(R.id.editText_text);
		
		mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
		            performSearch();
		            return true;
		        }
		        return false;
		    }
		});
		
		mSearchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
				if(s.length() != 0) {
					mSearchButton.setEnabled(true);
					mClearButton.setVisibility(View.VISIBLE);
				} else {
					mSearchButton.setEnabled(false);
					mClearButton.setVisibility(View.GONE);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
			
		});
		
		mStatusTextView = (TextView) view.findViewById(R.id.textView_status);
		
		mClearButton = (Button) view.findViewById(R.id.button_clear);
		
		
		
		mClearButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mSearchEditText.setText("");
				mResultsListView.setVisibility(View.GONE);
				mStatusTextView.setText("NO RESULTS TO DISPLAY");
				mFound = new ArrayList<SearchObject>();
				mResultsListAdapter.refresh(mFound);
				mResultsListAdapter.notifyDataSetChanged();
			}
			
		});
		
		mCloseButton = (TextView) view.findViewById(R.id.textView_cancel);
		
		mCloseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
			
		});
		
		mSearchButton = (TextView) view.findViewById(R.id.textView_search);
 		mSearchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performSearch();
			}
 			
 		});
		
		
		
		mSearchProgress = (ProgressBar) view.findViewById(R.id.progressBar_search);
		mSearchProgress.setMax(mTotalPages);
		
		if(mFound == null) {
			mFound = new ArrayList<SearchObject>();
		}
		
		if(!mSearchTerm.isEmpty()) {
			mSearchEditText.setText(mSearchTerm);
			mStatusTextView.setText(String.valueOf(mFound.size()) + " RESULTS FOUND"); 
			mResultsListView.setVisibility(View.VISIBLE);
		}
		
		mResultsListAdapter = new SearchListAdapter(view.getContext(), mBook, mFound);
		
		mResultsListView.setAdapter(mResultsListAdapter);
		
		mResultsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				SearchObject object = mResultsListAdapter.getItem(position);
				
				((ActivityPage) getActivity()).gotoPage(object.pageNumber());
				dismiss();
			}
			
		});
		
		return view;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    final Dialog dialog = super.onCreateDialog(savedInstanceState);
	    
	    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	    dialog.getWindow().getAttributes().windowAnimations = R.style.SearchDialogAnimation;
		
	    return dialog;
	}
	
	@Override
	public void dismiss() {
		Utilities.hideKeyboardFrom(getActivity(), mSearchEditText);
		
		super.dismiss();
	}
	
	private void performSearch() {
		mCurrentPage = 1;
		mFound = new ArrayList<SearchObject>();
		mResultsListAdapter.refresh(mFound);
		mResultsListAdapter.notifyDataSetChanged();
		
		mSearchTerm = mSearchEditText.getText().toString();
		
		if(!mSearchTerm.isEmpty()) {
			mStatusTextView.setText("SEARCHING...");
			Utilities.closeSoftKeyboard(getActivity(), mSearchEditText.getWindowToken());
			mSearchProgress.setVisibility(View.VISIBLE);
			
			mPage = openPage();
			
			mSearchTask = new SearchTask(DialogFragmentSearch.this, mCurrentPage, mPage);
			mSearchTask.execute(mSearchTerm);
		}
	}

	@Override
	public void OnSearchCompleted(ArrayList<SearchObject> objects) {
		mFound.addAll(objects);

		if(mCurrentPage < mTotalPages) {
			mSearchProgress.setProgress(mCurrentPage+1);
			mCurrentPage++;
			
			closePage();
			
			mPage = openPage();
			
			mSearchTask = new SearchTask(DialogFragmentSearch.this, mCurrentPage, mPage);
			mSearchTask.execute(mSearchTerm);
		} else {
			mSearchProgress.setProgress(mTotalPages);
			mResultsListAdapter.addResults(mFound);
			mResultsListAdapter.notifyDataSetChanged();
			mSearchProgress.setVisibility(View.GONE);
			mStatusTextView.setText(String.valueOf(mFound.size()) + " RESULTS FOUND"); 
			mResultsListView.setVisibility(View.VISIBLE);
			
			closePage();
		}
	}
	
	private com.radaee.pdf.Page openPage() {
		mDocument.Close();
		
		Page page = mApp.data().database().pagesDatabase().pageByNumber(mBook.id(), mCurrentPage);
		
		String filePath = mApp.readHandle().path() + page.filePath();
		
		mStream.open(filePath);
		
		int ret = mDocument.OpenStream(mStream, null);
		
		return mDocument.GetPage(0);
		
	}
	
	private void closePage() {
		mPage.Close();
		mDocument.Close();
		mStream.close();
	}

}
