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

import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Utilities;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Managers.ManagerImages.GeneratePageThumbnailTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FragmentNotesListView implements OnScrollListener {
	
	private static final String TAG = FragmentNotesListView.class.getSimpleName();
	
	private View						mRootView;
	
	private RelativeLayout				mIncludeChapterLayout;
	private RelativeLayout				mChapterHeaderLayout;
	private TextView					mChapterTextView;
	private TextView					mTitleTextView;
	private TextView					mPageNumberTextView;
	
//	private LinearLayout				mPageHeader;
//	private LinearLayout				mPageImageInclude;
//	private TextView					mPageTextView;
	
//	private ImageView					mPageImageView;
//	private GeneratePageThumbnailTask	mThumbnailTask;
	
	
	private ListView					mListView = null;
	private FragmentNotesListAdapter	mListAdapter = null;
//	private ListView.OnScrollListener 	mListener = null;
	
//	private LayoutInflater 				mInflater;
//	
//	private FragmentNotesChapter	 	mPreviousChapter;
	private FragmentNotesChapter		mCurrentChapter;
	
	public FragmentNotesListView(View rootView) {
		
		mRootView = rootView;
		
		initialize();
	}

	private void initialize() {
		
		
		
		LinearLayout emptyLayout = (LinearLayout) mRootView.findViewById(R.id.empty);
		
		mListView = (ListView) mRootView.findViewById(R.id.listView_notes);
		
		mListView.setEmptyView(emptyLayout);
		mListView.setOnScrollListener(this);
		
		mIncludeChapterLayout = (RelativeLayout) mRootView.findViewById(R.id.include_chapter);
		mChapterTextView = (TextView) mIncludeChapterLayout.findViewById(R.id.textView_chapter);
		mTitleTextView = (TextView) mIncludeChapterLayout.findViewById(R.id.textView_chapter_title);
		mPageNumberTextView = (TextView) mIncludeChapterLayout.findViewById(R.id.textView_chapter_page_number);
		
		
//		mChapterHeader.setVisibility(View.GONE);
        
//        mPageHeader = (LinearLayout) mRootView.findViewById(R.id.linearLayout_page); 
        
//		mPageHeader.setVisibility(View.GONE);
//        mPageImageInclude = (LinearLayout) mRootView.findViewById(R.id.include_list_item);
       
//        LayoutParams page_headerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//        page_headerParams.addRule(RelativeLayout.BELOW, mChapterHeader.getId());
//        mPageHeader.setLayoutParams(page_headerParams);
//        mPageHeader.setGravity(Gravity.BOTTOM);
//        addView(mPageHeader);
//        
//        mChapterHeader.setVisibility(View.GONE);
//		mPageHeader.setVisibility(View.GONE);
	}
	

	public void setAdapter(FragmentNotesListAdapter adapter) {
        if(adapter != null) {
            mListAdapter = adapter;
            mListView.setAdapter(adapter);
            
            if(mListAdapter.getCount() > 0) {
		        AbstractNotesObject object = (AbstractNotesObject) mListAdapter.getItem(0);
		            
		        if(object != null && object.isHeader()) {
//		        	mChapterCounter = 1;
		        	mCurrentChapter = (FragmentNotesChapter) object;
//		        	mPreviousChapter = mCurrentChapter;
		        	mChapterTextView.setText(mCurrentChapter.chapter());
		        	mTitleTextView.setText(mCurrentChapter.title());
		        	mIncludeChapterLayout.setVisibility(View.VISIBLE);
		        }
		        
		        FragmentNotesPage first_page = (FragmentNotesPage) mListAdapter.getItem(1);
				
				if(first_page != null) {
					mPageNumberTextView.setText(String.valueOf(first_page.pageNumber()));
				}
           } else {
        	   mIncludeChapterLayout.setVisibility(View.GONE);
           }
        }
    }
	
	
	
//	private int		mChapterCounter = 0;
	private boolean mScrolling = false;

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL || 
				scrollState == OnScrollListener.SCROLL_STATE_FLING) {
			mScrolling = true;
		} else {
			mScrolling = false;
		}
	}
	
//	private int oldTop;
//	private int oldFirstVisibleItem;
//	private boolean mUp = false;

	@Override
	public void onScroll(AbsListView absListView, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
		if(!mScrolling) {
			return;
		}
		
		if(mListAdapter == null || visibleItemCount ==  0) {
			return;
		}
		
//		View view = mListView.getChildAt(0);
//		int top = (view == null) ? 0 : view.getTop();
//		
//		if(firstVisibleItem == oldFirstVisibleItem) {
//			if(top > oldTop) {
//				mUp = false;
//			} else if(top < oldTop) {
//				mUp = true;
//			}
//		} else {
//			if(firstVisibleItem < oldFirstVisibleItem) {
//				mUp = false;
//			} else {
//				mUp = true;
//			}
//		}
//
//		oldTop = top;
//		oldFirstVisibleItem = firstVisibleItem;
		
//		Log.i(TAG, String.valueOf(mListView.getFirstVisiblePosition()));
		
		int previousChapterPosition = mListAdapter.getPreviousHeader(mListView.getFirstVisiblePosition());
		
		
		
//		Log.i(TAG, String.valueOf(mListView.getFirstVisiblePosition()) + " " + String.valueOf(previousChapterPosition));
		
		AbstractNotesObject object = null;
		
		if(isChapterHeader(firstVisibleItem)) {
			object = (AbstractNotesObject) mListAdapter.getItem(firstVisibleItem);
		} else {
			object = (AbstractNotesObject) mListAdapter.getItem(previousChapterPosition);
		}
//			Log.i(TAG, "isHeader");
//			View chapterView = mListView.getChildAt(0);
			
			
			
			FragmentNotesChapter notes_chapter = (FragmentNotesChapter) object;
				
			mChapterTextView.setText(notes_chapter.chapter());
			mTitleTextView.setText(notes_chapter.title());
			
		if(isPage(firstVisibleItem)) {
			FragmentNotesPage notes_page = (FragmentNotesPage) mListAdapter.getItem(firstVisibleItem);
				
			if(notes_page != null) {
				mPageNumberTextView.setText(String.valueOf(notes_page.pageNumber()));
			}
		}
			
//			if(chapterView.getTop() == mChapterHeader.getBottom()) {
//				
//				
////				mChapterHeader.setTranslationY(-chapterView.getTop() / 2);
//			} else {
//				
//			}
//		} else {
//			
//		}
		

		
		
//		if(mListAdapter != null && !mListAdapter.isEmpty()) {
//			if(mChapterHeader != null && mPageHeader != null) {
//				mChapterHeader.setVisibility(View.VISIBLE);
////				mPageHeader.setVisibility(View.VISIBLE);
//				
//				AbstractNotesObject object = (AbstractNotesObject) mListAdapter.getItem(firstVisibleItem);
//				
//				// First item is a header, set it
//				if(object.isHeader()) {
//					FragmentNotesChapter notes_chapter = (FragmentNotesChapter) object;
//					
//					mChapterTextView.setText(notes_chapter.chapter());
//					mTitleTextView.setText(notes_chapter.title());
//				} else {
//				
//					Rect rect1 = new Rect(mChapterHeader.getLeft(), mChapterHeader.getTop(), 
//							  mChapterHeader.getRight(), mChapterHeader.getBottom());
//					Rect rect2 = new Rect(absListView.getLeft(), absListView.getTop(), 
//							  absListView.getRight(), absListView.getBottom());
//		
//					if(rect1.intersect(rect2)) {
//						object = (AbstractNotesObject) mListAdapter.getItem(firstVisibleItem + 1);
//						
//						if(object.isHeader()) {
//							FragmentNotesChapter notes_chapter = (FragmentNotesChapter) object;
//							
//							mChapterTextView.setText(notes_chapter.chapter());
//							mTitleTextView.setText(notes_chapter.title());
//						}
//						
//						
//					}
//				}
				
				
					
					
					
					
					
//					if(mChapterHeader.getTop() >= absListView.getTop() &&
//							mChapterHeader.getLeft() >= absListView.getLeft() &&
//							mChapterHeader.getRight() <= absListView.getRight() &&
//							mChapterHeader.getBottom() <= absListView.getBottom()) {
//						
//						mCurrentChapter = notes_chapter;
//						
//						if(mUp) {
//							if(mCurrentChapter != null) {
//								mPreviousChapter = mCurrentChapter;
//								mChapterTextView.setText(mCurrentChapter.chapter());
//								mTitleTextView.setText(mCurrentChapter.title());
//							}
//						} else {
//							if(mPreviousChapter != null) {
//								mChapterTextView.setText(mPreviousChapter.chapter());
//								mTitleTextView.setText(mPreviousChapter.title());
//							}
//						}
//					}
					
					
					
//					if(mUp) {
//						if(mPreviousChapter != null) {
//							mChapterTextView.setText(mPreviousChapter.chapter());
//							mTitleTextView.setText(mPreviousChapter.title());
//						}
//						
//						
//					} else {
//						
//						
//					}
//				}
//				
//				object = (AbstractNotesObject) mListAdapter.getItem(firstVisibleItem + 1);
//				
//				if(object.isPage()) {
//					
//					
//					

//					
//					FragmentNotesPage notes_page = (FragmentNotesPage) object;
//					
//					try {
//						if(mThumbnailTask != null) {
//							if(mThumbnailTask.getStatus() == AsyncTask.Status.RUNNING) {
//								mThumbnailTask.cancel(true);
//							}
//						}
//						
//						mThumbnailTask = new GeneratePageThumbnailTask(mPageImageView, mListAdapter.book(), true);
//						mThumbnailTask.execute(notes_page.pageNumber());
//						//holder.mThumbnailTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, note.page());
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		} else {
//			if(mChapterHeader != null && mPageHeader != null) {
//				mChapterHeader.setVisibility(View.GONE);
//				mPageHeader.setVisibility(View.GONE);
//			}
//		}
		
		

//		if (mListener != null) {
//            mListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
//        }
		
		
	}
	
	private boolean isChapterHeader(int visibleItem) {
		AbstractNotesObject object = (AbstractNotesObject) mListAdapter.getItem(visibleItem);
		
		return (object.isHeader()) ? true : false;
			
	}
	
	private boolean isPage(int item) {
		AbstractNotesObject object = (AbstractNotesObject) mListAdapter.getItem(item);
		
		return (object.isPage()) ? true : false;
			
	}
}
