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

package com.metrostarsystems.ebriefing.BookPage.Annotations.Tools;

import com.metrostarsystems.ebriefing.BookPage.Annotations.Colors.AnnotationColor;

import android.graphics.Paint;

public abstract class AbstractAnnotationTool {
	
	public final static int PEN = 0;
	public final static int HIGHLIGHTER = 1;
	public final static int ERASER = 2;

	private int 				mId = -1;
	
	protected Paint 			mPaint;
	protected AnnotationColor 	mColor;
	private float				mStrokeWidth = 4f;
	
	public Paint paint() {
		return mPaint;
	}
	
	protected AbstractAnnotationTool() { }
	
	protected AbstractAnnotationTool(Builder build) {
		
		mId 			= build.mId;
		mColor 			= build.mColor;
		mStrokeWidth 	= build.mStrokeWidth;
		
		if(mPaint == null) {
			mPaint = new Paint();
		}
			
		mPaint.setAntiAlias(true);
	    mPaint.setDither(true);
	    mPaint.setStyle(Paint.Style.STROKE);
	    mPaint.setStrokeJoin(Paint.Join.ROUND);
	    mPaint.setStrokeCap(Paint.Cap.ROUND);
	    mPaint.setStrokeWidth(mStrokeWidth);
	}
	
	public int id() { return mId; }
	public int color() { return mColor.color(); }
	public AnnotationColor annotationColor() { return mColor; }
	public float width() { return mStrokeWidth; }
	
	public void setColor(AnnotationColor color) { 
		mColor = color; 
		mPaint.setColor(color.color());
	}
	
	public void setWidth(float width) {
		mStrokeWidth = width;
		mPaint.setStrokeWidth(width);
	}

	public abstract static class Builder {
		
		private int 				mId = -1;
		
		private AnnotationColor		mColor;
		private float				mStrokeWidth = 4f;
		
		public Builder(int id) {
			mId = id;
		}
		
		public Builder color(AnnotationColor color) { mColor = color; return this; }
		public Builder width(float width) { mStrokeWidth = width; return this; }

		public abstract AbstractAnnotationTool build();
			
	}
	
	
	
}
