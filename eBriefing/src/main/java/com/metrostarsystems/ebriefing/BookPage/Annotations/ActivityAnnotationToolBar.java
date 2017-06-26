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

package com.metrostarsystems.ebriefing.BookPage.Annotations;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.BookPage.Annotations.Colors.AnnotationColor;
import com.metrostarsystems.ebriefing.BookPage.Annotations.Tools.AbstractAnnotationTool;
import com.metrostarsystems.ebriefing.BookPage.Annotations.Tools.AnnotationEraser;
import com.metrostarsystems.ebriefing.BookPage.Annotations.Tools.AnnotationHighlighter;
import com.metrostarsystems.ebriefing.BookPage.Annotations.Tools.AnnotationPen;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;

public class ActivityAnnotationToolBar {
	
	private static final String TAG = ActivityAnnotationToolBar.class.getSimpleName();
	
	private MainApplication				mApp;
	private ActivityAnnotation 			mParent;
	
	private Book						mBook;
	
	private RelativeLayout				mToolBarLayout;
	private boolean 					mShowToolBar = false;
	
	private ImageView					mDrawTool;
	private ImageView					mContentsTool;
	
	private boolean						mShowNotesTool = true;
	private ImageView					mNotesTool;
	
	private ImageView					mUndoTool;
	private ImageView					mRedoTool;
	private ImageView					mClearTool;
	
	private ImageView					mPrintTool;
	private ImageView					mCurrentModeButton;
	
	
	public static AbstractAnnotationTool		mCurrentTool = AnnotationPen.PEN();
	private ImageView					mCurrentToolButton;	
	private ImageView					mCurrentColorButton;
	
	private boolean						mShowToolLayout = false;
	
	private ImageView					mChoicePenTool;
	private ImageView					mChoiceHighlighterTool;
	private ImageView					mChoiceEraserTool;
	
	private boolean						mShowColorLayout = false;
	
	private static AnnotationColor		mCurrentPenColor = AnnotationColor.PEN_BLACK();
	private static AnnotationColor		mCurrentHighlighterColor = AnnotationColor.HIGHLIGHTER_CYAN();
	
	
	private ImageView					mChoicePenColorBlack;
	private ImageView					mChoicePenColorBlue;
	private ImageView					mChoicePenColorRed;
	private ImageView					mChoicePenColorGreen;
	
	private ImageView					mChoiceHighlighterColorCyan;
	private ImageView					mChoiceHighlighterColorGreen;
	private ImageView					mChoiceHighlighterColorOrange;
	private ImageView					mChoiceHighlighterColorPink;
	private ImageView					mChoiceHighlighterColorYellow;
	
	
	private LinearLayout				mDrawActionsLayout;
	private ImageView					mSaveAction;
	private ImageView					mCloseAction;
	

	
	public ActivityAnnotationToolBar(ActivityAnnotation page) {
		mApp = (MainApplication) page.getApplicationContext();
		mParent = page;
		mBook = mParent.book();
		
		// Tool Bar
		mToolBarLayout = (RelativeLayout) mParent.findViewById(R.id.include_page_toolbar);
		
		
		
		mDrawTool		= (ImageView) mToolBarLayout.findViewById(R.id.imageView_draw);
		mContentsTool	= (ImageView) mToolBarLayout.findViewById(R.id.imageView_contents);
		mNotesTool 		= (ImageView) mToolBarLayout.findViewById(R.id.imageView_notes);
		
		
		
		mUndoTool = (ImageView) mToolBarLayout.findViewById(R.id.imageView_undo);
		mUndoTool.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mParent.undo();
			}
			
		});
		
		mRedoTool = (ImageView) mToolBarLayout.findViewById(R.id.imageView_redo);
		mRedoTool.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mParent.redo();
			}
			
		});
		
		mClearTool = (ImageView) mToolBarLayout.findViewById(R.id.imageView_clear);
		mClearTool.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mParent.clear();
			}
			
		});
		

		mCurrentToolButton = (ImageView) mToolBarLayout.findViewById(R.id.imageView_current_tool);
		mCurrentToolButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleTools();
			}
			
		});
		
		mCurrentColorButton = (ImageView) mToolBarLayout.findViewById(R.id.imageView_current_color);
		mCurrentColorButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleColors();
			}
			
		});
		
		mCurrentModeButton = (ImageView) mToolBarLayout.findViewById(R.id.imageView_current_mode);
		mCurrentModeButton.setVisibility(View.GONE);
		
		mPrintTool = (ImageView) mToolBarLayout.findViewById(R.id.imageView_print);
		mPrintTool.setVisibility(View.GONE);
		
		// Tool Bar Actions Bar
		mDrawActionsLayout = (LinearLayout) mParent.annotationLayout().findViewById(R.id.include_page_bar_actions);
		
		mSaveAction = (ImageView) mDrawActionsLayout.findViewById(R.id.imageView_save);
		mSaveAction.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mParent.save();
				mParent.finish();
			}
			
		});
		
		mCloseAction = (ImageView) mDrawActionsLayout.findViewById(R.id.imageView_close);
		mCloseAction.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mParent.cancel();
				mParent.finish();
			}
			
		});
		
		mChoicePenTool = (ImageView) mToolBarLayout.findViewById(R.id.imageView_pen);
		mChoicePenTool.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTool(AnnotationPen.PEN(), mCurrentPenColor);
				hideTools();
				hideColors();
				showCurrentColor();
			}
			
		});
		
		mChoiceHighlighterTool = (ImageView) mToolBarLayout.findViewById(R.id.imageView_highlighter);
		mChoiceHighlighterTool.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTool(AnnotationHighlighter.HIGHLIGHTER(), mCurrentHighlighterColor);
				hideTools();
				hideColors();
				showCurrentColor();
			}
			
		});
		
		mChoiceEraserTool = (ImageView) mToolBarLayout.findViewById(R.id.imageView_eraser);
		mChoiceEraserTool.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTool(AnnotationEraser.ERASER(), mCurrentPenColor);
				hideTools();
				hideColors();
				hideCurrentColor();
			}
			
		});
		
//		mColorsLayout = (LinearLayout) mParent.readerLayout().findViewById(R.id.include_page_bar_pen_colors);
		
		mChoicePenColorBlack = (ImageView) mToolBarLayout.findViewById(R.id.imageView_pen_black);
		mChoicePenColorBlack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTool(mCurrentTool, AnnotationColor.PEN_BLACK());
				hideColors();
			}
			
		});
		
		mChoicePenColorBlue = (ImageView) mToolBarLayout.findViewById(R.id.imageView_pen_blue);
		mChoicePenColorBlue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTool(mCurrentTool, AnnotationColor.PEN_BLUE());
				hideColors();
			}
			
		});
		
		mChoicePenColorRed = (ImageView) mToolBarLayout.findViewById(R.id.imageView_pen_red);
		mChoicePenColorRed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTool(mCurrentTool, AnnotationColor.PEN_RED());
				hideColors();
			}
			
		});
		
		mChoicePenColorGreen = (ImageView) mToolBarLayout.findViewById(R.id.imageView_pen_green);
		mChoicePenColorGreen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTool(mCurrentTool, AnnotationColor.PEN_GREEN());
				hideColors();
			}
			
		});
		
		mChoiceHighlighterColorCyan = (ImageView) mToolBarLayout.findViewById(R.id.imageView_highlighter_cyan);
		mChoiceHighlighterColorCyan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTool(mCurrentTool, AnnotationColor.HIGHLIGHTER_CYAN());
				hideColors();
			}
			
		});
		
		mChoiceHighlighterColorGreen = (ImageView) mToolBarLayout.findViewById(R.id.imageView_highlighter_green);
		mChoiceHighlighterColorGreen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTool(mCurrentTool, AnnotationColor.HIGHLIGHTER_GREEN());
				hideColors();
			}
			
		});
		
		mChoiceHighlighterColorOrange = (ImageView) mToolBarLayout.findViewById(R.id.imageView_highlighter_orange);
		mChoiceHighlighterColorOrange.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTool(mCurrentTool, AnnotationColor.HIGHLIGHTER_ORANGE());
				hideColors();
			}
			
		});
		
		mChoiceHighlighterColorPink = (ImageView) mToolBarLayout.findViewById(R.id.imageView_highlighter_pink);
		mChoiceHighlighterColorPink.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTool(mCurrentTool, AnnotationColor.HIGHLIGHTER_PINK());
				hideColors();
			}
			
		});
		
		mChoiceHighlighterColorYellow = (ImageView) mToolBarLayout.findViewById(R.id.imageView_highlighter_yellow);
		mChoiceHighlighterColorYellow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setTool(mCurrentTool, AnnotationColor.HIGHLIGHTER_YELLOW());
				hideColors();
			}
			
		});
	}
	
	public AbstractAnnotationTool currentTool() {
		return mCurrentTool;
	}

	public void invalidate() {
		mToolBarLayout.invalidate();
	}
	
	public void showDrawTool() {
		mDrawTool.setVisibility(View.VISIBLE);
	}
	
	public void hideDrawTool() {
		mDrawTool.setVisibility(View.GONE);
	}
	
	public void showUndoTool() {
		mUndoTool.setVisibility(View.VISIBLE);
	}
	
	public void hideUndoTool() {
		mUndoTool.setVisibility(View.GONE);
	}
	
	public void updateUndoTool() {
		if(mParent.canUndo()) {
			mUndoTool.setEnabled(true);
		} else {
			mUndoTool.setEnabled(false);
		}
	}
	
	public void showRedoTool() {
		mRedoTool.setVisibility(View.VISIBLE);
	}
	
	public void hideRedoTool() {
		mRedoTool.setVisibility(View.GONE);
	}
	
	public void updateRedoTool() {
		if(mParent.canRedo()) {
			mRedoTool.setEnabled(true);
		} else {
			mRedoTool.setEnabled(false);
		}
	}
	
	public void updateUndoRedoTool() {
		updateUndoTool();
		updateRedoTool();
	}
	
	public void showClearTool() {
		mClearTool.setVisibility(View.VISIBLE);
	}
	
	public void hideClearTool() {
		mClearTool.setVisibility(View.GONE);
	}
	
	private void showDrawOptions() {
		mContentsTool.setVisibility(View.GONE);
		mDrawTool.setVisibility(View.GONE);
		mNotesTool.setVisibility(View.GONE);
		
		showRedoTool();
		showUndoTool();
		showClearTool();
		
		updateUndoRedoTool();
		
		showCurrentTool();

		if(mCurrentTool.id() == AbstractAnnotationTool.ERASER) {
			hideCurrentColor();
		} else {
			updateCurrentColorLayout();
			showCurrentColor();
		}
		
		showClearTool();
	}
	
//	private void hideDrawOptions() {
//		//mContentsTool.setVisibility(View.VISIBLE);
//		//mDrawTool.setVisibility(View.VISIBLE);
//		//mNotesTool.setVisibility(View.VISIBLE);
//		//mPageModeImageView.setVisibility(View.VISIBLE);
//		
//		//mParent.actionBar().showActionBar();
//		//mDrawActionsLayout.setVisibility(View.GONE);
//		
//		hideCurrentTool();
//		hideCurrentColor();
//		
//		hideUndoTool();
//		hideRedoTool();
//		hideClearTool();
//	}
	
	private void showCurrentTool() {
		mCurrentToolButton.setVisibility(View.VISIBLE);
	}
	
	private void showCurrentColor() {
		mCurrentColorButton.setVisibility(View.VISIBLE);
	}
	
	private void hideCurrentTool() {
		mCurrentToolButton.setVisibility(View.GONE);
	}
	
	private void hideCurrentColor() {
		mCurrentColorButton.setVisibility(View.GONE);
	}
	
	public boolean isToolsOpen() {
		return mShowToolLayout;
	}
	
	private void showTools() {
		mShowToolLayout = true;
		
		if(mCurrentTool.id() == AbstractAnnotationTool.PEN) {
			
			mChoicePenTool.setVisibility(View.GONE);
			mChoiceHighlighterTool.setVisibility(View.VISIBLE);
			mChoiceEraserTool.setVisibility(View.VISIBLE);
			
		} else if(mCurrentTool.id() == AbstractAnnotationTool.HIGHLIGHTER) {
			
			mChoicePenTool.setVisibility(View.VISIBLE);
			mChoiceHighlighterTool.setVisibility(View.GONE);
			mChoiceEraserTool.setVisibility(View.VISIBLE);
			
		} else if(mCurrentTool.id() == AbstractAnnotationTool.ERASER) {
			
			mChoicePenTool.setVisibility(View.VISIBLE);
			mChoiceHighlighterTool.setVisibility(View.VISIBLE);
			mChoiceEraserTool.setVisibility(View.GONE);
		}
		
		hideCurrentColor();
		
		mUndoTool.setVisibility(View.GONE);
		mRedoTool.setVisibility(View.GONE);
		mClearTool.setVisibility(View.GONE);
	}
	
	private void toggleTools() {
		mShowToolLayout = !mShowToolLayout;
		
		if(mShowToolLayout) {
			showTools(); }
		else {
			hideTools(); 
		}
	}
	
	public void hideTools() {
		mShowToolLayout = false;
		
		mChoicePenTool.setVisibility(View.GONE);
		mChoiceHighlighterTool.setVisibility(View.GONE);
		mChoiceEraserTool.setVisibility(View.GONE);
		
		mChoicePenColorBlack.setVisibility(View.GONE);
		mChoicePenColorBlue.setVisibility(View.GONE);
		mChoicePenColorRed.setVisibility(View.GONE);
		mChoicePenColorGreen.setVisibility(View.GONE);
		
		showCurrentColor();
		
		mUndoTool.setVisibility(View.VISIBLE);
		mRedoTool.setVisibility(View.VISIBLE);
		mClearTool.setVisibility(View.VISIBLE);
	}
	
	private void setTool(AbstractAnnotationTool tool, AnnotationColor color) {
		setTool(tool);
		
		if(tool.id() == AbstractAnnotationTool.PEN) {
			setPenColor(color);
		} else if(tool.id() == AbstractAnnotationTool.HIGHLIGHTER) {
			setHighlighterColor(color);
		} else {
			setPenColor(color);
		}
		
//		updateUndoRedoTool();
		
//		if(mParent.mode() == PageMode.MODE_SINGLE) {
//			FragmentPage selectedFragment = mParent.currentFragment();
//			
//			if(selectedFragment != null) {
//				if(mCurrentTool instanceof AnnotationEraser) {
//					selectedFragment.save();
//				} else {
//					selectedFragment.save();
//					selectedFragment.reader().PDFSetInk(0);
//				}
//			}
//		}
//		} else {
//			FragmentPage selectedFragment = mParent.currentFragment();
//			FragmentPage selectedFragment2 = mParent.currentFragmentDuel();
//			
//			if(selectedFragment != null && selectedFragment2 != null) {
//				if(mCurrentTool instanceof AnnotationEraser) {
//					selectedFragment.save();
//					selectedFragment2.save();
//				} else {
//					selectedFragment.save();
//					selectedFragment.reader().PDFSetInk(0);
//					selectedFragment2.save();
//					selectedFragment2.reader().PDFSetInk(0);
//				}
//			}
//		}
	}
	
	private void setTool(AbstractAnnotationTool tool) {
		Log.i("ActivityPageToolBar", "SetPen:" + String.valueOf(tool.id()));
		mCurrentTool = tool;
		
		if(mCurrentTool.id() == AbstractAnnotationTool.PEN) {
			mCurrentTool.setColor(mCurrentPenColor);
		} else if(mCurrentTool.id() == AbstractAnnotationTool.HIGHLIGHTER) {
			mCurrentTool.setColor(mCurrentHighlighterColor);
		}
		
		updateCurrentToolLayout();
	}
	
	private void updateCurrentToolLayout() {
		if(mCurrentTool.id() == AbstractAnnotationTool.PEN) {
			mCurrentToolButton.setImageResource(R.drawable.activity_page_bar_button_pen);
			
		} else if(mCurrentTool.id() == AbstractAnnotationTool.HIGHLIGHTER) {
			mCurrentToolButton.setImageResource(R.drawable.activity_page_bar_button_highlighter);
			
		} else if(mCurrentTool.id() == AbstractAnnotationTool.ERASER) {
			mCurrentToolButton.setImageResource(R.drawable.activity_page_bar_button_eraser);
			
		}
		
		showCurrentTool();
        
	}
	
	public boolean isColorsOpen() {
		return mShowColorLayout;
	}
	
	private void showColors() {
		mShowColorLayout = true;
		
		if(mCurrentTool.id() == AbstractAnnotationTool.PEN) {
			mChoiceHighlighterColorCyan.setVisibility(View.GONE);
			mChoiceHighlighterColorGreen.setVisibility(View.GONE);
			mChoiceHighlighterColorOrange.setVisibility(View.GONE);
			mChoiceHighlighterColorPink.setVisibility(View.GONE);
			mChoiceHighlighterColorYellow.setVisibility(View.GONE);
			
			if(mCurrentPenColor.id() == AnnotationColor.PEN_BLACK) {
				mChoicePenColorBlack.setVisibility(View.GONE);
				mChoicePenColorBlue.setVisibility(View.VISIBLE);
				mChoicePenColorRed.setVisibility(View.VISIBLE);
				mChoicePenColorGreen.setVisibility(View.VISIBLE);
			} else if(mCurrentPenColor.id() == AnnotationColor.PEN_BLUE) {
				mChoicePenColorBlack.setVisibility(View.VISIBLE);
				mChoicePenColorBlue.setVisibility(View.GONE);
				mChoicePenColorRed.setVisibility(View.VISIBLE);
				mChoicePenColorGreen.setVisibility(View.VISIBLE);
			} else if(mCurrentPenColor.id() == AnnotationColor.PEN_GREEN) {
				mChoicePenColorBlack.setVisibility(View.VISIBLE);
				mChoicePenColorBlue.setVisibility(View.VISIBLE);
				mChoicePenColorRed.setVisibility(View.VISIBLE);
				mChoicePenColorGreen.setVisibility(View.GONE);
			} else if(mCurrentPenColor.id() == AnnotationColor.PEN_RED) {
				mChoicePenColorBlack.setVisibility(View.VISIBLE);
				mChoicePenColorBlue.setVisibility(View.VISIBLE);
				mChoicePenColorRed.setVisibility(View.GONE);
				mChoicePenColorGreen.setVisibility(View.VISIBLE);
			}
		} else if(mCurrentTool.id() == AbstractAnnotationTool.HIGHLIGHTER) {
			mChoicePenColorBlack.setVisibility(View.GONE);
			mChoicePenColorBlue.setVisibility(View.GONE);
			mChoicePenColorRed.setVisibility(View.GONE);
			mChoicePenColorGreen.setVisibility(View.GONE);
			
			if(mCurrentHighlighterColor.id() == AnnotationColor.HIGHLIGHTER_CYAN) {
				mChoiceHighlighterColorCyan.setVisibility(View.GONE);
				mChoiceHighlighterColorGreen.setVisibility(View.VISIBLE);
				mChoiceHighlighterColorOrange.setVisibility(View.VISIBLE);
				mChoiceHighlighterColorPink.setVisibility(View.VISIBLE);
				mChoiceHighlighterColorYellow.setVisibility(View.VISIBLE);
				
			} else if(mCurrentHighlighterColor.id() == AnnotationColor.HIGHLIGHTER_GREEN) {
				mChoiceHighlighterColorCyan.setVisibility(View.VISIBLE);
				mChoiceHighlighterColorGreen.setVisibility(View.GONE);
				mChoiceHighlighterColorOrange.setVisibility(View.VISIBLE);
				mChoiceHighlighterColorPink.setVisibility(View.VISIBLE);
				mChoiceHighlighterColorYellow.setVisibility(View.VISIBLE);
				
			} else if(mCurrentHighlighterColor.id() == AnnotationColor.HIGHLIGHTER_ORANGE) {
				mChoiceHighlighterColorCyan.setVisibility(View.VISIBLE);
				mChoiceHighlighterColorGreen.setVisibility(View.VISIBLE);
				mChoiceHighlighterColorOrange.setVisibility(View.GONE);
				mChoiceHighlighterColorPink.setVisibility(View.VISIBLE);
				mChoiceHighlighterColorYellow.setVisibility(View.VISIBLE);
				
			} else if(mCurrentHighlighterColor.id() == AnnotationColor.HIGHLIGHTER_PINK) {
				mChoiceHighlighterColorCyan.setVisibility(View.VISIBLE);
				mChoiceHighlighterColorGreen.setVisibility(View.VISIBLE);
				mChoiceHighlighterColorOrange.setVisibility(View.VISIBLE);
				mChoiceHighlighterColorPink.setVisibility(View.GONE);
				mChoiceHighlighterColorYellow.setVisibility(View.VISIBLE);
				
			} else if(mCurrentHighlighterColor.id() == AnnotationColor.HIGHLIGHTER_YELLOW) {
				mChoiceHighlighterColorCyan.setVisibility(View.VISIBLE);
				mChoiceHighlighterColorGreen.setVisibility(View.VISIBLE);
				mChoiceHighlighterColorOrange.setVisibility(View.VISIBLE);
				mChoiceHighlighterColorPink.setVisibility(View.VISIBLE);
				mChoiceHighlighterColorYellow.setVisibility(View.GONE);
				
			}
		}
		
		mCurrentToolButton.setVisibility(View.GONE);
		
		mUndoTool.setVisibility(View.GONE);
		mRedoTool.setVisibility(View.GONE);
		mClearTool.setVisibility(View.GONE);
		
	}
	
	private void toggleColors() {
		mShowColorLayout = !mShowColorLayout;
		
		if (mShowColorLayout) showColors(); else hideColors(); 
	}
	
	public void hideColors() {
		mShowColorLayout = false;
		
		mChoicePenColorBlack.setVisibility(View.GONE);
		mChoicePenColorBlue.setVisibility(View.GONE);
		mChoicePenColorRed.setVisibility(View.GONE);
		mChoicePenColorGreen.setVisibility(View.GONE);
		
		mChoiceHighlighterColorCyan.setVisibility(View.GONE);
		mChoiceHighlighterColorGreen.setVisibility(View.GONE);
		mChoiceHighlighterColorOrange.setVisibility(View.GONE);
		mChoiceHighlighterColorPink.setVisibility(View.GONE);
		mChoiceHighlighterColorYellow.setVisibility(View.GONE);
		
		mCurrentToolButton.setVisibility(View.VISIBLE);
		
		mUndoTool.setVisibility(View.VISIBLE);
		mRedoTool.setVisibility(View.VISIBLE);
		mClearTool.setVisibility(View.VISIBLE);
	}
	
	private void setPenColor(AnnotationColor color) {
		mCurrentPenColor = color;
		
		mCurrentTool.setColor(color);

		updateCurrentColorLayout();
	}
	
	private void setHighlighterColor(AnnotationColor color) {
		mCurrentHighlighterColor = color;
		
		mCurrentTool.setColor(color);
		
		updateCurrentColorLayout();
	}
	
	private void updateCurrentColorLayout() {
		
		if(mCurrentTool.id() == AbstractAnnotationTool.PEN) {
			if(mCurrentPenColor.id() == AnnotationColor.PEN_BLACK) {
				mCurrentColorButton.setImageResource(R.drawable.ic_action_pen_black);
			} else if(mCurrentPenColor.id() == AnnotationColor.PEN_BLUE) {
				mCurrentColorButton.setImageResource(R.drawable.ic_action_pen_blue);
			} else if(mCurrentPenColor.id() == AnnotationColor.PEN_GREEN) {
				mCurrentColorButton.setImageResource(R.drawable.ic_action_pen_green);
			} else if(mCurrentPenColor.id() == AnnotationColor.PEN_RED) {
				mCurrentColorButton.setImageResource(R.drawable.ic_action_pen_red);
			}
		} else if(mCurrentTool.id() == AbstractAnnotationTool.HIGHLIGHTER) {
			if(mCurrentHighlighterColor.id() == AnnotationColor.HIGHLIGHTER_CYAN) {
				mCurrentColorButton.setImageResource(R.drawable.ic_action_highlighter_cyan);
			} else if(mCurrentHighlighterColor.id() == AnnotationColor.HIGHLIGHTER_GREEN) {
				mCurrentColorButton.setImageResource(R.drawable.ic_action_highlighter_green);
			} else if(mCurrentHighlighterColor.id() == AnnotationColor.HIGHLIGHTER_ORANGE) {
				mCurrentColorButton.setImageResource(R.drawable.ic_action_highlighter_orange);
			} else if(mCurrentHighlighterColor.id() == AnnotationColor.HIGHLIGHTER_PINK) {
				mCurrentColorButton.setImageResource(R.drawable.ic_action_highlighter_pink);
			} else if(mCurrentHighlighterColor.id() == AnnotationColor.HIGHLIGHTER_YELLOW) {
				mCurrentColorButton.setImageResource(R.drawable.ic_action_highlighter_yellow);
			}
		}
		
		showCurrentColor();
	}
	
	
	
	public void activateAnnotationMode() {
		setTool(mCurrentTool);
		
//		if(mParent.mode() == PageMode.MODE_SINGLE_PAGE) {
//			FragmentPage selectedFragment = mParent.currentFragment();
//			
//			Annotation annotation = selectedFragment.annotation();
//			
//			selectedFragment.activateAnnotationMode();
//			selectedFragment.open();
//			
////			mAnnotationsTotal = selectedFragment.totalAnnotations();
//			
//			
//			
//			mParent.hideBookmark();
//
//			
//		} else {
//			
//			mParent.hideBookmark();
//			
//			
//		}
		
		showDrawOptions();
	}
	
}
