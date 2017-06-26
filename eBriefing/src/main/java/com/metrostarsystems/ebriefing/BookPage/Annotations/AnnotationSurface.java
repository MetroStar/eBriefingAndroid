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

import java.util.ArrayList;
import java.util.Stack;

import com.metrostarsystems.ebriefing.BookPage.Annotations.History.AbstractAnnotationHistory;
import com.metrostarsystems.ebriefing.BookPage.Annotations.History.AnnotationClearHistory;
import com.metrostarsystems.ebriefing.BookPage.Annotations.History.AnnotationHistory;
import com.metrostarsystems.ebriefing.BookPage.Annotations.History.AbstractAnnotationHistory.AnnotationHistoryType;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Annotation;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Ink.InkAnnotation;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Ink.InkPath;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class AnnotationSurface extends SurfaceView implements SurfaceHolder.Callback, OnTouchListener {
	
	private static final String TAG = AnnotationSurface.class.getSimpleName();
	
	private ActivityAnnotation 					mParent;
	
	private DrawThread 							mThread;
	private Paint 								mPaint;
	private Canvas 								mOffScreenCanvas;
	private Canvas 								mOnScreenCanvas;
	
	private Bitmap								mSurface;
	private int									mSurfaceWidth;
	private int									mSurfaceHeight;
	
	private Stack<AbstractAnnotationHistory>	mPreviousHistory = new Stack<AbstractAnnotationHistory>();
	private Stack<AbstractAnnotationHistory>	mNextHistory = new Stack<AbstractAnnotationHistory>();
	
	private ArrayList<InkAnnotation>    		mAnnotations = new ArrayList<InkAnnotation>();
	private InkPath 							mCurrentPath; 
	private InkAnnotation						mCurrentAnnotation;
	
	private Annotation							mAnnotation;
//
//	// We can be in one of these 3 states
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mMode = NONE;

	
	public AnnotationSurface(Context context) {
	    super(context);
	    initialize(context);
	}

	public AnnotationSurface(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    initialize(context);
	}

	public AnnotationSurface(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    initialize(context);
	}
	
	@Override
	public void onLayout(boolean changed, int left, int top, int right, int bottom) {
	    if(changed) {
	    	layout(mParent.pagePositionX(), mParent.pagePositionY(), mParent.pagePositionX() + mParent.pageWidth(), mParent.pagePositionY() + mParent.pageHeight());
	    }
	}
	
	private void initialize(Context context) {
		if(!isInEditMode()) {
			setZOrderOnTop(true);    // necessary
		}
		//setZOrderMediaOverlay(true);
		SurfaceHolder sfhTrackHolder = getHolder();
		sfhTrackHolder.setFormat(PixelFormat.TRANSPARENT);
		//sfhTrackHolder.setFormat( PixelFormat.RGBA_8888 );
		sfhTrackHolder.addCallback(this);
		
		setOnTouchListener(this);
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setFilterBitmap(true);
	    mPaint.setDither(true);
	    mPaint.setColor(0xFFAAAAAA);
	    mPaint.setStyle(Paint.Style.STROKE);
	    mPaint.setStrokeJoin(Paint.Join.ROUND);
	    mPaint.setStrokeCap(Paint.Cap.ROUND);
	    
	}
	
	public void clear() {
		if(!mPreviousHistory.isEmpty() && mPreviousHistory.peek().type() != AnnotationHistoryType.CLEAR) {
			mPreviousHistory.add(new AnnotationClearHistory(mAnnotations));
			mAnnotations.clear();
			
			invalidate();
			mParent.updateUndoRedoTool();
		} else {
			mPreviousHistory.add(new AnnotationClearHistory(mAnnotations));
			mAnnotations.clear();
			
			invalidate();
			mParent.updateUndoRedoTool();
		}
	}
	

	private ArrayList<InkAnnotation> previous() {
		
		if(!mPreviousHistory.isEmpty()) {
		
			AbstractAnnotationHistory previous_history = mPreviousHistory.pop();
			
			mNextHistory.push(new AnnotationHistory(mAnnotations));
			
			return previous_history.restore();
		}
		
		return null;
	}
	
	private ArrayList<InkAnnotation> next() {
		
		if(!mNextHistory.isEmpty()) {
		
			mPreviousHistory.push(new AnnotationHistory(mAnnotations));

			AbstractAnnotationHistory next_history = mNextHistory.pop();
			
		
			return next_history.restore();
		}
		
		return null;
	}
	
	
	public void undo() {

		try {
			ArrayList<InkAnnotation> previous_history = previous();

			if(previous_history != null) {
				mAnnotations.clear();
				mAnnotations.addAll(previous_history);
			}

			invalidate();
			mParent.updateUndoRedoTool();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean canUndo() {
		return !mPreviousHistory.isEmpty();
	}
	
	public void redo() {
		try {

			ArrayList<InkAnnotation> next_history = next();

			if(next_history != null) {
				mAnnotations.clear();
				mAnnotations.addAll(next_history);
			}


			invalidate();
			mParent.updateUndoRedoTool();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean canRedo() {
		return !mNextHistory.isEmpty();
	}
	
	private void removeHistory() {
		mNextHistory.clear();
	}
	
	public Bitmap surfaceBitmap() {
		return mSurface;
	}
	
	public Annotation annotation() {
		return mAnnotation;
	}
	
	public ArrayList<InkAnnotation> inkAnnotations() {
		return mAnnotations;
	}
	
	public void setInkAnnotations(Annotation annotation) {
		
		mAnnotation = annotation;
		
		if(mAnnotation != null && mAnnotation.inkAnnotation() != null && mAnnotation.inkAnnotation().size() > 0) {
			mAnnotations.addAll(mAnnotation.inkAnnotation());
		}
	}
	
	public void setParent(ActivityAnnotation page) {
		mParent = page;
	}
	
	public int surfaceWidth() { return mSurfaceWidth; }
	public int surfaceHeight() { return mSurfaceHeight; }
	
	private void rescaleAnnotation() {
		// Resize annotation ---------------------------------------------------------------------------------
		int sw = surfaceWidth();
		int sh = surfaceHeight();

		if(mAnnotation != null) {
			float aw = mAnnotation.width();
			float ah = mAnnotation.height();
			
			float width_ratio = sw / aw;
			float height_ratio = sh / ah;

			if(width_ratio != 0 && height_ratio != 0) {
				Matrix scale_matrix = new Matrix();
				scale_matrix.setScale(width_ratio, height_ratio);

				for(int index = 0; index < mAnnotations.size(); index++) {
					InkAnnotation ink = mAnnotations.get(index);

					InkPath ink_path = ink.ink();

					ink_path.transform(scale_matrix);
				}
			}
		

			mAnnotation.setWidth(sw);
			mAnnotation.setHeight(sh);
		}
		// ---------------------------------------------------------------------------------------------------

	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) { 
		super.onSizeChanged(w, h, oldw, oldh);
		
		mSurfaceWidth = w;
		mSurfaceHeight = h;

		mSurface = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mOffScreenCanvas = new Canvas(mSurface);
	}
	
	public void setSurface(Bitmap bitmap) {
		if(bitmap != null) {
			mSurface = bitmap;
		}
		
		mOffScreenCanvas.setBitmap(mSurface);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
		rescaleAnnotation();
		
		mThread = new DrawThread(holder, getContext(), this);
		mThread.setRunning(true);
		mThread.start();
		
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mThread.setRunning(false);
		
		boolean retry = true;

		while(retry) {
			try {
				mThread.join();
				retry = false;

			} catch(Exception e) {
				//Log.v("Exception Occured", e.getMessage());
			}
		}
	}
	
	private void doDraw(Canvas canvas) {
		if(canvas == null) {
			return;
		}
		
		canvas.save();
		
		mOffScreenCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		for(int index = 0; index < mAnnotations.size(); index++) {
			InkAnnotation annotation = mAnnotations.get(index);
			annotation.draw(mOffScreenCanvas, ActivityAnnotationToolBar.mCurrentTool.paint());
		}
		
		if(canvas != null && mSurface != null) {
			canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			canvas.drawBitmap(mSurface, 0, 0, mPaint);
		}
		
		canvas.restore();
	}
	
	private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
	
	private void touch_start(float x, float y) {
		
		mPreviousHistory.push(new AnnotationHistory(mAnnotations));
	
		mCurrentPath = new InkPath();
		mCurrentAnnotation = new InkAnnotation(mCurrentPath, ActivityAnnotationToolBar.mCurrentTool);
	
        mCurrentPath.moveTo(x, y);
        mX = x;
        mY = y;
        
        mAnnotations.add(mCurrentAnnotation);
	}
	
	private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
	    
        if(dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
        	mCurrentPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
	        mX = x;
	        mY = y;
	    }
	}
	
	private void touch_up() {
        mCurrentPath.lineTo(mX, mY);
       
        
        removeHistory();
        mParent.updateUndoRedoTool();
    }
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		float x = event.getX();
        float y = event.getY();
        
//        int[] loc = new int[2];
//        getLocationOnScreen(loc);
        
    	switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

            	touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            	mMode = ZOOM;	
            	break;
            case MotionEvent.ACTION_POINTER_UP:
            	mMode = NONE;
            	break;
            case MotionEvent.ACTION_MOVE:

            	touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:

            	touch_up();
                invalidate();
                break;
    	}	
    	
    	//Log.i(TAG, String.valueOf(mMode));

        return true;
	}
	
	private class DrawThread extends Thread {
		private boolean mRun;
		
		private SurfaceHolder mSurfaceHolder;
		private Context mContext;
		private AnnotationSurface mDrawPanel;
		
		public DrawThread(SurfaceHolder holder, Context context, AnnotationSurface panel) {
			mSurfaceHolder = holder;
			mContext = context;
			mDrawPanel = panel;
			
			mRun = false;
		}
		
		public void setRunning(boolean run) {
			mRun = run;
		}
		
		@Override
		public void run() {
			while(mRun) {
				try {
					mOnScreenCanvas = mSurfaceHolder.lockCanvas();
					synchronized(mSurfaceHolder) {
						mDrawPanel.doDraw(mOnScreenCanvas);
					}
				} finally {
					if(mOnScreenCanvas != null) {
						mSurfaceHolder.unlockCanvasAndPost(mOnScreenCanvas);
					}
				}
			}
		}
	}
}
