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
import com.metrostarsystems.ebriefing.Services.DownloadService.DownloadService;
import com.metrostarsystems.ebriefing.Services.DownloadService.Books.DownloadBookDataTasks.DownloadBookDataChaptersTask;
import com.metrostarsystems.ebriefing.Services.DownloadService.Books.DownloadBookDataTasks.DownloadBookDataChaptersTask.BookDownloadChaptersListener;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FragmentOverview extends DialogFragment implements BookDownloadChaptersListener {

	private MainApplication					mApp;
	private Book							mBook;
	
	private Button 							mDescriptionButton;
	private Button 							mChaptersButton;
	
	private TextView 						mTextTextView;
	
	private ProgressBar 					mProgressBar;
	private boolean 						mDoneLoadingChapters = false;
	
	private boolean 						mShowChapters = false;
	
	public static final FragmentOverview newInstance(String bookId) {
		FragmentOverview fragment = new FragmentOverview();
	    Bundle bundle = new Bundle();
	    bundle.putString("bookid", bookId);
	    fragment.setArguments(bundle);
	    return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_available_overview, null);
		
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		String book_id = getArguments().getString("bookid");
		mApp = (MainApplication) getActivity().getApplicationContext();
		mBook = mApp.data().database().booksDatabase().book(book_id);
		
		mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar_waiting);
		
		
		TextView titleTextView = (TextView) view.findViewById(R.id.textView_title);
		titleTextView.setText(mBook.title());
		
		mTextTextView = (TextView) view.findViewById(R.id.textView_text);
		mTextTextView.setText(mBook.description());
		
		mDescriptionButton = (Button) view.findViewById(R.id.button_description);
		
		mDescriptionButton.setBackgroundResource(R.drawable.button_overview_description_active);
		mDescriptionButton.setTextColor(Color.WHITE);
		
		mDescriptionButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mShowChapters = false;
				
				mChaptersButton.setBackgroundResource(R.drawable.button_overview_chapters);
				mChaptersButton.setTextColor(Color.BLACK);
				
				mDescriptionButton.setBackgroundResource(R.drawable.button_overview_description_active);
				mDescriptionButton.setTextColor(Color.WHITE);
				
				mTextTextView.setText(mBook.description());
				mProgressBar.setVisibility(View.GONE);
			}
			
		});
		
		mChaptersButton = (Button) view.findViewById(R.id.button_chapters);
		
		mChaptersButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mShowChapters = true;
				
				mDescriptionButton.setBackgroundResource(R.drawable.button_overview_description);
				mDescriptionButton.setTextColor(Color.BLACK);
				
				mChaptersButton.setBackgroundResource(R.drawable.button_overview_chapters_active);
				mChaptersButton.setTextColor(Color.WHITE);
				
				if(!mDoneLoadingChapters) {
					mProgressBar.setVisibility(View.VISIBLE);
					mTextTextView.setVisibility(View.GONE);
				} else {
					mProgressBar.setVisibility(View.GONE);
					mTextTextView.setVisibility(View.VISIBLE);
					mTextTextView.setText(mApp.data().database().chaptersDatabase().toString(mBook.id()));
				}
			}
			
		});
		
		
		
		
		ImageView bookImageView = (ImageView) view.findViewById(R.id.imageView_book);
		
		mApp.imageLoader().displayImage(mBook.largeImageUrl(), bookImageView, mApp.imageOptions());

		
		TextView pagesTextView = (TextView) view.findViewById(R.id.textView_pages);
		pagesTextView.setText(String.valueOf(mBook.pageCount()) + " Pages");
		
		TextView modifiedTextView = (TextView) view.findViewById(R.id.textView_date_modified);
		modifiedTextView.setText(mBook.dateModifiedOverviewFormat());

		if(!mDoneLoadingChapters) {
			new DownloadBookDataChaptersTask(mApp, this).execute(mBook);
		}
		
		Button downloadButton = (Button) view.findViewById(R.id.button_download);
		downloadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mBook.isUpdated()) {
					DownloadService.downloadUpdateBookData(mBook);
				} else {
					DownloadService.downloadDeviceBookData(mBook);
				}

				dismiss();
			}
			
		});
		
		
		return view;
	}



	@Override
	public void onBookDownloadChaptersFinished(Book book) {
		mDoneLoadingChapters = true;
		mProgressBar.setVisibility(View.GONE);
		mApp.data().database().booksDatabase().update(book);
	
		if(mShowChapters) {
			mTextTextView.setText(mApp.data().database().chaptersDatabase().toString(book.id()));
			mTextTextView.setVisibility(View.VISIBLE);
		}	
	}
}
