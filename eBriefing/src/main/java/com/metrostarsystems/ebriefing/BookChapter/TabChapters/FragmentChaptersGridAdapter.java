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

package com.metrostarsystems.ebriefing.BookChapter.TabChapters;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.BookChapter.Tab.ChapterTabs.ChapterTab;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Chapter.Chapter;
import com.metrostarsystems.ebriefing.Data.Framework.Managers.ManagerImages.LoadChapterImageTask;

public class FragmentChaptersGridAdapter extends ArrayAdapter<Chapter> {

	private MainApplication 		mApp;
	private Book 					mBook; 
	private Context 				mContext;
	private ChapterTab 				mTab;
	
	public FragmentChaptersGridAdapter(Context context, ChapterTab tab, Book book) {
		super(context, R.layout.activity_chapter_grid_list_item);
		
		mApp = (MainApplication) context.getApplicationContext();
		mBook = book;
		mTab = tab;
		mContext = context;
		
		addAll(mApp.data().database().chaptersDatabase().chaptersByBook(mBook.id()));
		
		
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		// If convertview is null create the layout
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.activity_chapter_grid_list_item, parent, false);
			
			// Create the view holder
			holder = new ViewHolder();
			holder.mDataLayout = (LinearLayout) convertView.findViewById(R.id.include_data_layout);
			holder.mChapterImageView = (ImageView) convertView.findViewById(R.id.imageView_chapter);
			holder.mChapterTextView = (TextView) convertView.findViewById(R.id.textView_chapter);
			holder.mTitleTextView = (TextView) convertView.findViewById(R.id.textView_title);
			holder.mNotesTextView = (TextView) holder.mDataLayout.findViewById(R.id.textView_notes);
			holder.mBookmarksTextView = (TextView) holder.mDataLayout.findViewById(R.id.textView_bookmarks);
			holder.mAnnotationsTextView = (TextView) holder.mDataLayout.findViewById(R.id.textView_annotations);
			holder.mPagesTextView = (TextView) holder.mDataLayout.findViewById(R.id.textView_pages);
			
			convertView.setTag(holder);
		} else {
			// Reuse the view holder
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		Chapter chapter = getItem(position);
		
		try {
			if(mApp.data().imageManager().hasLargeImageFile(mApp, chapter)) {
				if(holder.mImageTask != null) {
					if(holder.mImageTask.getStatus() == AsyncTask.Status.RUNNING) {
						holder.mImageTask.cancel(true);
					}
				}
				
				holder.mImageTask = new LoadChapterImageTask(mApp, holder.mChapterImageView, chapter);
				holder.mImageTask.execute();
			} else {
				mApp.imageLoader().displayImage(chapter.largeImageUrl(), holder.mChapterImageView, mApp.imageOptions());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		holder.mChapterTextView.setText("Chapter " + String.valueOf(position + 1));
		holder.mTitleTextView.setText(chapter.title());
		
		
		
		holder.mNotesTextView.setText(String.valueOf(mApp.data().database().notesDatabase().countByChapter(chapter.id())));
		holder.mBookmarksTextView.setText(String.valueOf(mApp.data().database().bookmarksDatabase().countByChapter(chapter.id())));
		holder.mAnnotationsTextView.setText(String.valueOf(mApp.data().database().annotationsDatabase().countByChapter(chapter.id())));
		holder.mPagesTextView.setText(String.valueOf(mApp.data().database().pagesDatabase().countByChapter(chapter.id())));
		
		return convertView;
	}
	
	public void refresh(Book book) {
		clear();
		mBook = book;
		addAll(mApp.data().database().chaptersDatabase().chaptersByBook(book.id()));
	}
	
	private class ViewHolder {
		public LoadChapterImageTask mImageTask;
		public LinearLayout	mDataLayout;
		public ImageView 	mChapterImageView;
		public TextView 	mChapterTextView;
		public TextView 	mTitleTextView;
		public TextView 	mNotesTextView;
		public TextView 	mBookmarksTextView;
		public TextView 	mAnnotationsTextView;
		public TextView 	mPagesTextView;
	}

}
