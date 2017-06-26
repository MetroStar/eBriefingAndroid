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

package com.metrostarsystems.ebriefing.BookPage.Print;

import java.util.ArrayList;

import com.metrostarsystems.ebriefing.Data.Framework.Print.PageDocument;
import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Utilities;
import com.metrostarsystems.ebriefing.BookPage.Print.Preview.PrintPreviewPagerAdapter;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Print.PrintDocument;
import com.metrostarsystems.ebriefing.Services.PrintService.PrintService;

import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class DialogFragmentPrint extends DialogFragment implements OnPageChangeListener {
	
	private static final String TAG = DialogFragmentPrint.class.getSimpleName();

	private MainApplication		mApp;
	private View				mView;
	
	private Book				mBook;
	private int					mPageNumber = 0;
	
	private static PrintDocument.Builder		mDocumentBuilder;
	
	private	PageRange			mPageRange = PageRange.RANGE_CURRENT;
	
	private int					mMaxPageLimit = PrintDocument.MAX_PAGE_LIMIT;
	
	
	private PrintService				mPrintService;
	
	private ViewPager 					mViewPager;
    private PrintPreviewPagerAdapter	mViewPagerAdapter;
    
    private TextView			mPageCurrentTextView;
    private TextView			mPageCountTextView;

	private RadioButton			mCurrentPageRadioButton;
	
	private RadioButton			mRangePageRadioButton;
	
	private TextView			mRangePageLimitTextView;
	private EditText			mRangePageEditText;
	private TextView			mRangePageHelpTextView;
	
	private RadioButton			mColorRadioButton;
	private RadioButton			mBlackAndWhiteRadioButton;
	
	private RadioButton 		mAnnotationsYesRadioButton;
	private RadioButton 		mAnnotationsNoRadioButton;
	
	private RadioButton 		mNotesYesRadioButton;
	private RadioButton 		mNotesNoRadioButton;

    private RadioButton         mOrientationLandscapeButton;
    private RadioButton         mOrientationPortraitButton;

	
	private TextView			mCancelButton;
	private TextView			mPrintButton;
	
	public static final DialogFragmentPrint newInstance(String bookId, int currentPage) {
		DialogFragmentPrint fragment = new DialogFragmentPrint();
	    Bundle bundle = new Bundle();
	    bundle.putString("bookid", bookId);
	    bundle.putInt("pagenumber", currentPage);
	    fragment.setArguments(bundle);
	    return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mView = inflater.inflate(R.layout.dialog_print, container);
		
		mApp = (MainApplication) getActivity().getApplicationContext();

		if(mApp == null || !getArguments().containsKey("bookid") || !getArguments().containsKey("pagenumber")) {
			dismiss();
		}
		
		String book_id = getArguments().getString("bookid");
		mPageNumber = getArguments().getInt("pagenumber");
		
		mBook = mApp.data().database().booksDatabase().book(book_id);
		
		if(mBook == null) {
			dismiss();
		}
		
		if(mBook.pageCount() > PrintDocument.MAX_PAGE_LIMIT) {
			mMaxPageLimit = PrintDocument.MAX_PAGE_LIMIT;
		} else {
			mMaxPageLimit = mBook.pageCount();
		}
		
		mDocumentBuilder = new PrintDocument.Builder(mApp, mBook);
        mDocumentBuilder.printColor(true);
        mDocumentBuilder.printAnnotations(true);
        mDocumentBuilder.printNotes(false);
        mDocumentBuilder.generatePage(1);

		mPageRange = PageRange.RANGE_CURRENT;
		
		
		TextView titleTextView = (TextView) mView.findViewById(R.id.textView_title);
		titleTextView.setText(mBook.title());
		
		mApp.data().imageManager().clearPrintCache();
		
		mViewPager = (ViewPager) mView.findViewById(R.id.viewPager_page_preview);
		mViewPagerAdapter = new PrintPreviewPagerAdapter(getChildFragmentManager(), mBook, mDocumentBuilder.build());
		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setCurrentItem(0);
		
		mPageCurrentTextView = (TextView) mView.findViewById(R.id.textView_page_current);
		mPageCurrentTextView.setText(String.valueOf(mViewPager.getCurrentItem() + 1));
		
		mPageCountTextView = (TextView) mView.findViewById(R.id.textView_page_count);

        mPageCountTextView.setText(String.valueOf(mDocumentBuilder.size()));
		
		mCurrentPageRadioButton = (RadioButton) mView.findViewById(R.id.radioButton_page_current);
		mCurrentPageRadioButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPageRange = PageRange.RANGE_CURRENT;
				refreshPages();

                mPageCountTextView.setText(String.valueOf(mDocumentBuilder.size()));
				mRangePageRadioButton.setChecked(false);
				
				mPrintButton.setEnabled(true);
				
			}
 			
		});
		
		mRangePageRadioButton = (RadioButton) mView.findViewById(R.id.radioButton_page_range);
		mRangePageRadioButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPageRange = PageRange.RANGE_PAGES;
				mCurrentPageRadioButton.setChecked(false);
				
				if(!mRangePageEditText.getText().toString().isEmpty()) {
					refreshPages();
				}
			}
			
		});
		
		mRangePageRadioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked) {
					mRangePageEditText.setEnabled(true);
					mRangePageHelpTextView.setEnabled(true);
					mRangePageLimitTextView.setEnabled(true);
					mPrintButton.setEnabled(false);
					
					mRangePageEditText.requestFocus();
					Utilities.showKeyboard(getActivity(), mRangePageEditText);
				} else {
					mRangePageEditText.setEnabled(false);
					mRangePageHelpTextView.setEnabled(false);
					mRangePageLimitTextView.setEnabled(false);
				}
			}
			
		});
		
		mRangePageLimitTextView = (TextView) mView.findViewById(R.id.textView_page_limit);
		mRangePageLimitTextView.setText(String.valueOf(mMaxPageLimit));
		
		mRangePageEditText = (EditText) mView.findViewById(R.id.editText_page_range);
		mRangePageEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				mApp.data().imageManager().clearPrintCache();
				refreshPages();
			}
			
		});
		
		mRangePageHelpTextView = (TextView) mView.findViewById(R.id.textView_page_range_help);
		
		mColorRadioButton = (RadioButton) mView.findViewById(R.id.radioButton_appearance_color);
		mColorRadioButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mBlackAndWhiteRadioButton.setChecked(false);

                mDocumentBuilder.printColor(true);
				
				mApp.data().imageManager().clearPrintCache();
				refreshPages();
			}
			
		});
		
		mBlackAndWhiteRadioButton = (RadioButton) mView.findViewById(R.id.radioButton_appearance_black_and_white);
		mBlackAndWhiteRadioButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mColorRadioButton.setChecked(false);

                mDocumentBuilder.printColor(false);
				
				mApp.data().imageManager().clearPrintCache();
				refreshPages();
			}
			
		});
		
		mAnnotationsYesRadioButton = (RadioButton) mView.findViewById(R.id.radioButton_annotations_yes);
		mAnnotationsYesRadioButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

                mDocumentBuilder.printAnnotations(true);
				mAnnotationsNoRadioButton.setChecked(false);
				mApp.data().imageManager().clearPrintCache();
				refreshPages();
			}
			
		});
		
		mAnnotationsNoRadioButton = (RadioButton) mView.findViewById(R.id.radioButton_annotations_no);
		mAnnotationsNoRadioButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

                mDocumentBuilder.printAnnotations(false);
				mAnnotationsYesRadioButton.setChecked(false);
				mApp.data().imageManager().clearPrintCache();
				refreshPages();
			}
			
		});
		
		mNotesYesRadioButton = (RadioButton) mView.findViewById(R.id.radioButton_notes_yes);
		mNotesYesRadioButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

                mDocumentBuilder.printNotes(true);
                mOrientationPortraitButton.setEnabled(true);
                mOrientationLandscapeButton.setEnabled(true);
                mOrientationPortraitButton.setChecked(true);
				mNotesNoRadioButton.setChecked(false);
				mApp.data().imageManager().clearPrintCache();
				refreshPages();
			}
			
		});
		
		mNotesNoRadioButton = (RadioButton) mView.findViewById(R.id.radioButton_notes_no);
		mNotesNoRadioButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                mDocumentBuilder.printNotes(false);
                mOrientationPortraitButton.setEnabled(false);
                mOrientationLandscapeButton.setEnabled(false);
                mOrientationLandscapeButton.setChecked(false);
                mOrientationPortraitButton.setChecked(false);
				mNotesYesRadioButton.setChecked(false);
				mApp.data().imageManager().clearPrintCache();
				refreshPages();
			}
			
		});

        mOrientationLandscapeButton = (RadioButton) mView.findViewById(R.id.radioButton_orientation_landscape);
        mOrientationLandscapeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDocumentBuilder.orientation(PageDocument.NoteOrientation.LANDSCAPE_SIDE_BY_SIDE);
                mOrientationPortraitButton.setChecked(false);
                mApp.data().imageManager().clearPrintCache();
                refreshPages();
            }
        });

        mOrientationPortraitButton = (RadioButton) mView.findViewById(R.id.radioButton_orientation_portrait);
        mOrientationPortraitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDocumentBuilder.orientation(PageDocument.NoteOrientation.PORTRAIT_SIDE_BY_SIDE);
                mOrientationLandscapeButton.setChecked(false);
                mApp.data().imageManager().clearPrintCache();
                refreshPages();
            }
        });
		
		mCancelButton = (TextView) mView.findViewById(R.id.textView_cancel);
		mCancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				

				dismiss();
				
			}
			
		});
		
		mPrintButton = (TextView) mView.findViewById(R.id.textView_print);
		mPrintButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				
				
				
				if(mDocumentBuilder.size() == 0) {
					return;
				}
				
				PrintService.Builder service_builder = new PrintService.Builder(getActivity(), mBook);
				
				service_builder.addDocument(mDocumentBuilder.build());
								
				mPrintService = service_builder.build();
				
				PrintManager printManager = (PrintManager) getActivity().getSystemService(android.content.Context.PRINT_SERVICE);
					
				
				int color_mode = (printColor()) ? 2 : 1;
				
				printManager.print("doc", mPrintService, new PrintAttributes.Builder()
																	.setColorMode(color_mode)
																	.build());
				
				dismiss();
			}
			
		});
		
		return mView;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    final Dialog dialog = super.onCreateDialog(savedInstanceState);
	    
	    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	    dialog.getWindow().getAttributes().windowAnimations = R.style.PrintDialogAnimation;
		
	    return dialog;
	}

	@Override
	public void dismiss() {
		Utilities.hideKeyboardFrom(getActivity(), mRangePageEditText);
		
		super.dismiss();
	}

	public static boolean printColor() {
		if(mDocumentBuilder == null) {
			return false;
		}
		
		return mDocumentBuilder.isPrintColor();
	}

	private void refreshPages() {

        mDocumentBuilder.clear();

        // Generate the pages
		switch(mPageRange) {
            case RANGE_CURRENT:
                mDocumentBuilder.generatePage(mPageNumber);
                break;
            case RANGE_PAGES:
                mDocumentBuilder.generatePages(parseRange(mRangePageEditText.getText().toString()));
                break;
		}
		
		int count = mMaxPageLimit - mDocumentBuilder.size();
		
		mRangePageLimitTextView.setText(String.valueOf(count));
		
		if(count > 0 && count < mMaxPageLimit) {
			mPageCountTextView.setText(String.valueOf(mDocumentBuilder.size()));
			mPrintButton.setEnabled(true);
		} else if(count == mMaxPageLimit) {
			mPageCountTextView.setText(String.valueOf(1));
			mPrintButton.setEnabled(false);
		} else if(count < 0) {
			mPageCountTextView.setText(String.valueOf(mDocumentBuilder.size()));
			mPrintButton.setEnabled(false);
		}
		
		mViewPagerAdapter.refresh(mDocumentBuilder.build());
		mViewPager.setCurrentItem(0);
		mViewPager.invalidate();
	}

	private ArrayList<Integer> parseRange(String text) {
		// Remove spaces
		text = text.replace(" ", "");
		ArrayList<Integer> page_numbers = new ArrayList<Integer>();
		
		
		String[] split1 = text.split(",");
		
		try {
		
			for(int index = 0; index < split1.length; index++) {
				String range = split1[index];
				
				if(range.isEmpty()) {
					break;
				}
				
				if(range.contains("-")) {
					String[] split2 = range.split("-");
					
					if(split2.length > 1) {
						for(int numbers = Integer.parseInt(split2[0]); numbers <= Integer.parseInt(split2[1]); numbers++) {
							if(!page_numbers.contains(numbers)) {
								page_numbers.add(numbers);
							}
						}
					} else {
						if(!page_numbers.contains(Integer.parseInt(split2[0]))) {
							page_numbers.add(Integer.parseInt(split2[0]));
						}
					}
				} else {
					if(!page_numbers.contains(Integer.parseInt(range))) {
						page_numbers.add(Integer.parseInt(range));
					}
				}
			}
		} catch (Exception e) {
			page_numbers = new ArrayList<Integer>();
		}
		
		return page_numbers;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int position) {
		mPageCurrentTextView.setText(String.valueOf(position + 1));
	}
	
	
	
	private static enum PageRange {
		RANGE_CURRENT,
		RANGE_PAGES;
	}
}
