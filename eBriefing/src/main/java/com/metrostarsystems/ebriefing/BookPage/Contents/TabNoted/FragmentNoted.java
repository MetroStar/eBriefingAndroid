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

package com.metrostarsystems.ebriefing.BookPage.Contents.TabNoted;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.BookPage.Contents.ActivityContents;
import com.metrostarsystems.ebriefing.BookPage.Contents.FlowLayout;
import com.metrostarsystems.ebriefing.BookPage.Contents.Section;
import com.metrostarsystems.ebriefing.BookPage.Contents.SectionPage;
import com.metrostarsystems.ebriefing.Data.Framework.AbstractPagerFragment;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Chapter.Chapter;
import com.metrostarsystems.ebriefing.Data.Framework.Managers.ManagerImages.GeneratePageThumbnailTask;
import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;

public class FragmentNoted extends AbstractPagerFragment {

	private MainApplication			mApp;		
	private ScrollView 				mScrollView;
	
	private Book					mBook;
	
	private ActivityContents		mActivity;
	
	private int						mSelectedPosition;
	
	private ArrayList<Section>		mSections = null;
	
	private LinearLayout			mSectionLayout;
	
	public static final FragmentNoted newInstance() {
		FragmentNoted fragment = new FragmentNoted();

	    return fragment;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mApp = (MainApplication) getActivity().getApplicationContext();
        
        mActivity = (ActivityContents) getActivity();
        
        if(savedInstanceState != null) {
        	mBook = mApp.data().database().booksDatabase().book(savedInstanceState.getString("bookid"));
        } else {
        	mBook = mActivity.book();
        }
        
        
        
        mSections = new ArrayList<Section>();
        
        ArrayList<Chapter> chapters = mApp.data().database().chaptersDatabase().chaptersByBook(mBook.id());
		
		for(int index = 0; index < chapters.size(); index++) {
			Chapter chapter = chapters.get(index);
			mSections.add(new Section("Chapter " + String.valueOf(index + 1) + ": " + 
												chapter.title()));
		}
		
		ArrayList<Page> pages = mApp.data().database().pagesDatabase().pagesByBook(mBook.id());
		
		for(int index = 0; index < pages.size(); index++) {
			Page page = pages.get(index);
			
			int chapter = mApp.data().database().chaptersDatabase().chapterNumber(mBook.id(), page.pageNumber());

			mSections.get(chapter - 1).addPage(new SectionPage(page.id(), page.pageNumber()));
		}
		
		mActivity.update();

    }
	

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.page_list, null);

		mScrollView = (ScrollView) rootView.findViewById(R.id.scrollView_pages);
		mSectionLayout = (LinearLayout) mScrollView.findViewById(R.id.linearLayout_pages);
		
		generateList();
		
		return rootView;
	}
	
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		
		outState.putString("bookid", mBook.id());
		
		super.onSaveInstanceState(outState);
	}

	private View generateHeader(String header) {
		View view;

		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.page_bookmarked_header, null);
		
		TextView headerTextView = (TextView) view.findViewById(R.id.textView_bookmarked_header);
		headerTextView.setText(header);
		
		return view;
	}
	
	private View generatePage(Page page) {
		View view;

		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.page_bookmarked_list_item, null);
		
		ImageView pageImageView = (ImageView) view.findViewById(R.id.imageView_thumbnail);
		ImageView bookmarkImageView = (ImageView) view.findViewById(R.id.imageView_bookmark);
		TextView pageNumberTextView = (TextView) view.findViewById(R.id.textView_page_number);
		
		try {
			new GeneratePageThumbnailTask(pageImageView, mBook, true)
					.execute(page.pageNumber());

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(mApp.data().database().bookmarksDatabase().has(page.bookId(), page.pageNumber())) {
			bookmarkImageView.setVisibility(View.VISIBLE);
		} else {
			bookmarkImageView.setVisibility(View.GONE);
		}
		
		pageNumberTextView.setText(String.valueOf(page.pageNumber()));
		
		view.setOnClickListener(new ThumbnailClickListener(mActivity, page.pageNumber()));
		
		return view;
	}
	
	private void generateList() {
		mSectionLayout.removeAllViews();
		
		View view;
		
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		
		for(int index = 0; index < mSections.size(); index++) {
			view = inflater.inflate(R.layout.page_bookmarked_item, null);
			
			LinearLayout headerLayout = (LinearLayout) view.findViewById(R.id.linearLayout_header);
			FlowLayout thumbnailLayout = (FlowLayout) view.findViewById(R.id.flowLayout_thumbnail);
			
			Section section = mSections.get(index);
			
			View headerView = generateHeader(section.title());
			View pageView = null;
			
			// Add the chapter header
			headerLayout.addView(headerView);
			
			// Add the page thumbnails
			for(int pages = 0; pages < section.size(); pages++) {
				if(mApp.data().database().notesDatabase().has(mBook.id(), section.pageNumber(pages))) {
					pageView = generatePage(mApp.data().database().pagesDatabase().page(section.id(pages)));
					thumbnailLayout.addView(pageView);
				}
			}

			mSectionLayout.addView(view);
			
		}
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private class ThumbnailClickListener implements OnClickListener {
		
		private ActivityContents mActivity;
		private int mPageNumber = 0;
		
		public ThumbnailClickListener(ActivityContents activity, int pageNumber) {
			mActivity = activity;
			mPageNumber = pageNumber;
		}

		@Override
		public void onClick(View v) {
			Intent returnIntent = new Intent();
			
			Bundle extras = new Bundle();
			extras.putInt("pagenumber", mPageNumber);
			returnIntent.putExtras(extras);
			
			mActivity.setResult(1, returnIntent);        
			mActivity.finish();
		}
		
	}
	
	@Override
	public void refresh() {
		
	}

	@Override
	public int count(MainApplication main) {
		if(main == null || mBook == null) {
			return 0;
		}
		
		return mApp.data().database().notesDatabase().countByBook(mBook.id());
	}
}
