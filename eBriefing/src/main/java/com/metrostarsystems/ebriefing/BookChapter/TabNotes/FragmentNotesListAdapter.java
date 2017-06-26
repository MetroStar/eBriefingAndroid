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

package com.metrostarsystems.ebriefing.BookChapter.TabNotes;

import java.util.ArrayList;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.BookPage.Contents.Section;
import com.metrostarsystems.ebriefing.BookPage.Contents.SectionPage;
import com.metrostarsystems.ebriefing.BookPage.Search.SearchObject;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Chapter.Chapter;
import com.metrostarsystems.ebriefing.Data.Framework.Managers.ManagerImages.GeneratePageThumbnailTask;
import com.metrostarsystems.ebriefing.Data.Framework.Notes.Note;
import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentNotesListAdapter extends ArrayAdapter<AbstractNotesObject> {
	
	private static final int TYPE_NOTE 	= 0;
	private static final int TYPE_CHAPTER 	= 1;
	private static final int TYPE_PAGE = 2;
	
	private Activity			mActivity;
	private MainApplication 	mApp;
	private Context 			mContext;
	private Book 				mBook;
	
	private LayoutInflater		mInflater;
	
	private ArrayList<Integer>	mSections;
	
	public FragmentNotesListAdapter(Activity activity, Context context, Book book) {
		super(context, R.layout.activity_chapter_notes_chapter_item);
		
		mActivity = activity;
		mApp = (MainApplication) context.getApplicationContext();
		mContext = context;
		
		mBook = book;
		
		mSections = new ArrayList<Integer>();
		
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		processNotes();
	}
	
	
	private void processNotes() {
		clear();
		
		if(mBook == null) {
			return;
		}
		
		ArrayList<Chapter> chapters = mApp.data().database().chaptersDatabase().chaptersByBook(mBook.id());
		
		for(int chapter_index = 0; chapter_index < chapters.size(); chapter_index++) {
			Chapter chapter = chapters.get(chapter_index);
			
			ArrayList<Page> pages = mApp.data().database().pagesDatabase().pagesByChapter(chapter.id());
			
			for(int page_index = 0; page_index < pages.size(); page_index++) {
				Page page = pages.get(page_index);
					
				if(mApp.data().database().notesDatabase().has(page.bookId(), page.pageNumber())) {
					FragmentNotesChapter.Builder chapter_builder = new FragmentNotesChapter.Builder();
					
					chapter_builder.chapter("Chapter " + String.valueOf(chapter_index + 1) + ": ");
					chapter_builder.title(chapter.title());
					
					add(chapter_builder.build());
					mSections.add(TYPE_CHAPTER);
					break;
				}
			}
			
			for(int page_index = 0; page_index < pages.size(); page_index++) {
				Page page = pages.get(page_index);
				
				if(mApp.data().database().notesDatabase().has(page.bookId(), page.pageNumber())) {
				
					FragmentNotesPage.Builder page_builder = new FragmentNotesPage.Builder();
					
					page_builder.page(page);
					
					FragmentNotesPage notes_page = page_builder.build();
					add(notes_page);
					
					mSections.add(TYPE_PAGE);
					
					ArrayList<Note> notes = mApp.data().database().notesDatabase().notesByNumber(page.bookId(), page.pageNumber());
					
					for(int note_index = 0; note_index < notes.size(); note_index += 2) {
						Note note1 = notes.get(note_index);
						Note note2 = null;
						
						if(note_index + 1 < notes.size()) {
							note2 = notes.get(note_index + 1);
						}
						
						FragmentNotesObject.Builder note_builder = new FragmentNotesObject.Builder();
						
						note_builder.note(note1);
						note_builder.note(note2);
						
						FragmentNotesObject notes_object = note_builder.build();
						add(notes_object);
						notes_page.add(notes_object);
						mSections.add(TYPE_NOTE);
					}
				}
			}
		}
	}
	
	@Override
	public int getItemViewType(int position) {
		return mSections.contains(position) ? TYPE_CHAPTER : mSections.contains(position) ? TYPE_NOTE : TYPE_PAGE;
	}
	
	@Override
	public int getViewTypeCount() {
		return 3;
	}
	
	public Book book() {
		return mBook;
	}
	
	public int getPreviousHeader(int position) {
		for(int index = position - 1; index >= 0; index--) {
			AbstractNotesObject object = getItem(index);
			
			if(object.isHeader()) {
				return index;
			}
		}
		
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		
		// If convertview is null create the layout
		if(convertView == null) {
			
			// Create the view holder
			holder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.activity_chapter_notes_list_item, parent, false);

			holder.mIncludeChapterLayout = (RelativeLayout) convertView.findViewById(R.id.include_chapter);
			holder.mIncludePageHeader = (LinearLayout) holder.mIncludeChapterLayout.findViewById(R.id.linearLayout_page_header);
			holder.mChapterTextView = (TextView) holder.mIncludeChapterLayout.findViewById(R.id.textView_chapter);
			holder.mTitleTextView = (TextView) holder.mIncludeChapterLayout.findViewById(R.id.textView_chapter_title);
			
			

			holder.mIncludeImageLayout = (RelativeLayout) convertView.findViewById(R.id.relativeLayout_image);
			holder.mPageLayout = (RelativeLayout) convertView.findViewById(R.id.relativeLayout_page);
			
			holder.mPageTextLayout = (LinearLayout) holder.mPageLayout.findViewById(R.id.linearLayout_page_text);
			holder.mContentTextView0 = (TextView) convertView.findViewById(R.id.textView_content0);
			holder.mDateTextView0 = (TextView) convertView.findViewById(R.id.textView_date0);
			
			
			holder.mIncludeThumbnailLayout = (LinearLayout) convertView.findViewById(R.id.include_list_item);
			holder.mBookmarkImageView = (ImageView) holder.mIncludeThumbnailLayout.findViewById(R.id.imageView_bookmark);

			holder.mPageImageView = (ImageView) holder.mIncludeThumbnailLayout.findViewById(R.id.imageView_thumbnail);
			holder.mNotesCountIconImageView = (ImageView) convertView.findViewById(R.id.imageView_note_page_icon);
			holder.mNotesCountTextView = (TextView) holder.mPageLayout.findViewById(R.id.textView_note_count);
			
			holder.mPageNumberTextView = (TextView) convertView.findViewById(R.id.textView_page_number); 
			holder.mNotesCollapseButton = (ImageView) convertView.findViewById(R.id.imageView_collapse);
			
			holder.mNoteArrowImageView = (ImageView) convertView.findViewById(R.id.imageView_page_arrow); 
			
			holder.mIncludeContentLayout = (LinearLayout) convertView.findViewById(R.id.linearLayout_content);
			
			holder.mContentTextView1 = (TextView) convertView.findViewById(R.id.textView_content1);
			holder.mDateTextView1 = (TextView) convertView.findViewById(R.id.textView_date1);
			
			holder.mContentTextView2 = (TextView) convertView.findViewById(R.id.textView_content2);
			holder.mDateTextView2 = (TextView) convertView.findViewById(R.id.textView_date2);

			convertView.setTag(holder);

		} else {
			// Reuse the view holder
			holder = (ViewHolder) convertView.getTag();
		}
		
		// Otherwise recycle the layout
		
		AbstractNotesObject object = getItem(position);
		
		if(object.isHeader()) {
			
			holder.mIncludeChapterLayout.setVisibility(View.VISIBLE);
			holder.mIncludePageHeader.setVisibility(View.GONE);
			holder.mIncludeImageLayout.setVisibility(View.GONE);
			holder.mIncludeContentLayout.setVisibility(View.GONE);
			
			FragmentNotesChapter notes_chapter = (FragmentNotesChapter) object;
			
			holder.mChapterTextView.setText(notes_chapter.chapter());
			holder.mTitleTextView.setText(notes_chapter.title());
			
		} else if(object.isPage()) {
			
			FragmentNotesPage notes_page = (FragmentNotesPage) object;
			
			holder.mIncludeChapterLayout.setVisibility(View.GONE);
			holder.mIncludeImageLayout.setVisibility(View.VISIBLE);
			holder.mIncludeContentLayout.setVisibility(View.GONE);
			
			if(mApp.serverConnection().isMultiNotes()) {
				
				if(notes_page.isCollapsed()) {
					holder.mPageTextLayout.setVisibility(View.VISIBLE);
					holder.mNoteArrowImageView.setVisibility(View.GONE);
					
					if(notes_page.hasNotes()) {
						FragmentNotesObject notes_object = notes_page.firstNote();
						Note note = notes_object.note(0);
					
						holder.mContentTextView0.setText(note.content());
						holder.mDateTextView0.setText(note.dateModifiedFormat());
					}
				} else {
					holder.mPageTextLayout.setVisibility(View.GONE);
					holder.mNoteArrowImageView.setVisibility(View.VISIBLE);
				}
				
				holder.mNotesCountIconImageView.setVisibility(View.VISIBLE);
				holder.mNotesCountTextView.setVisibility(View.VISIBLE);
				holder.mNotesCollapseButton.setVisibility(View.VISIBLE);
				
				int note_count = mApp.data().database().notesDatabase().countByPage(notes_page.pageId());
				
				holder.mNotesCountTextView.setText(String.valueOf(note_count));
				
				holder.mNotesCollapseButton.setOnClickListener(new CollapseClickListener(this, notes_page, convertView));
			} else {
				
				holder.mPageTextLayout.setVisibility(View.VISIBLE);
				holder.mNoteArrowImageView.setVisibility(View.GONE);
				
				if(notes_page.hasNotes()) {
					FragmentNotesObject notes_object = notes_page.firstNote();
					Note note = notes_object.note(0);
				
					holder.mContentTextView0.setText(note.content());
					holder.mDateTextView0.setText(note.dateModifiedFormat());
				}
				
				holder.mNotesCountIconImageView.setVisibility(View.GONE);
				holder.mNotesCountTextView.setVisibility(View.GONE);
				holder.mNotesCollapseButton.setVisibility(View.GONE);
			}
			
			if(mApp.data().database().bookmarksDatabase().has(notes_page.page().bookId(), notes_page.pageNumber())) {
				holder.mBookmarkImageView.setVisibility(View.VISIBLE);
			} else {
				holder.mBookmarkImageView.setVisibility(View.GONE);
			}
			
			try {
				if(holder.mThumbnailTask != null) {
					if(holder.mThumbnailTask.getStatus() == AsyncTask.Status.RUNNING) {
						holder.mThumbnailTask.cancel(true);
					}
				}
				
				holder.mThumbnailTask = new GeneratePageThumbnailTask(holder.mPageImageView, mBook, true);
				holder.mThumbnailTask.execute(notes_page.pageNumber());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			holder.mPageImageView.setOnClickListener(new OnImageClickListener(mBook, notes_page.pageNumber()));
			
			holder.mPageNumberTextView.setText(String.valueOf(notes_page.pageNumber()));
			
			
			
		} else {
			
			if(mApp.serverConnection().isMultiNotes()) {
				FragmentNotesObject notes_object = (FragmentNotesObject) object;
				
				holder.mIncludeChapterLayout.setVisibility(View.GONE);
				holder.mIncludeImageLayout.setVisibility(View.GONE);
				
				if(!notes_object.isCollapsed()) {
					holder.mIncludeContentLayout.setVisibility(View.VISIBLE);
					
					Note note = notes_object.note(0);
					
					if(note != null) {
						holder.mContentTextView1.setText(note.content());
						holder.mDateTextView1.setText(note.dateModifiedFormat());
					}
					
					note = notes_object.note(1);
					
					if(note != null) {
						holder.mContentTextView2.setVisibility(View.VISIBLE);
						holder.mDateTextView2.setVisibility(View.VISIBLE);
						
						holder.mContentTextView2.setText(note.content());
						holder.mDateTextView2.setText(note.dateModifiedFormat());
					} else {
						holder.mContentTextView2.setVisibility(View.INVISIBLE);
						holder.mDateTextView2.setVisibility(View.INVISIBLE);
					}
					
				} else {
					holder.mIncludeContentLayout.setVisibility(View.GONE);
				}
			} else {
				holder.mIncludeChapterLayout.setVisibility(View.GONE);
				holder.mIncludeImageLayout.setVisibility(View.GONE);
				holder.mIncludeContentLayout.setVisibility(View.GONE);
			}
		}
		return convertView;
	}
	
	public void refresh(Book book) {
		mBook = book;
		processNotes();
	}
	
	private static class ViewHolder {
		public RelativeLayout				mIncludeChapterLayout;
		public LinearLayout					mIncludePageHeader;
		public TextView 					mChapterTextView;
		public TextView 					mTitleTextView;
		
		public RelativeLayout				mIncludeImageLayout;
		
		public LinearLayout					mIncludeThumbnailLayout;
		public ImageView					mBookmarkImageView;
		
		public LinearLayout					mPageTextLayout;
		
		
		// Page
		public RelativeLayout				mPageLayout;
		public GeneratePageThumbnailTask 	mThumbnailTask;
		
		public ImageView 					mPageImageView;
		
		public TextView						mContentTextView0;
		public TextView						mDateTextView0;
		public TextView						mPageNumberTextView;
		public ImageView					mNotesCountIconImageView;
		public TextView						mNotesCountTextView;
		public ImageView					mNotesCollapseButton;
		public ImageView					mNoteArrowImageView;
		
		// Note
		public LinearLayout					mIncludeContentLayout;
		
		public TextView						mContentTextView1;
		public TextView						mDateTextView1;
		
		public TextView						mContentTextView2;
		public TextView						mDateTextView2;
		
	}

	private class CollapseClickListener implements OnClickListener {
		
		private View mView;
		private FragmentNotesListAdapter mAdapter;
		private FragmentNotesPage mNotesPage;
		
		public CollapseClickListener(FragmentNotesListAdapter adapter, FragmentNotesPage page, View view) {
			mAdapter = adapter;
			mNotesPage = page;
			mView = view;
		}

		@Override
		public void onClick(View v) {
			if(mNotesPage.isCollapsed()) {
				mNotesPage.setCollapsed(mView, false);
			} else {
				mNotesPage.setCollapsed(mView, true);
			}
			
			mAdapter.notifyDataSetChanged();
		}
		
	}
	
	private class OnImageClickListener implements OnClickListener {
		
		private Book mBook;
		private int	 mPageNumber;
		
		public OnImageClickListener(Book book, int pageNumber) {
			mBook = book;
			mPageNumber = pageNumber;
		}

		@Override
		public void onClick(View v) {
			Book.generateBookPageIntent(mActivity, mBook.id(), mPageNumber);
		}
		
	}
}
