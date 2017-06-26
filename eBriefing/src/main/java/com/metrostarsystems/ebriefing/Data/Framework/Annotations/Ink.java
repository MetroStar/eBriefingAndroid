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

package com.metrostarsystems.ebriefing.Data.Framework.Annotations;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;

import com.metrostarsystems.ebriefing.BookPage.Annotations.Tools.AbstractAnnotationTool;

public class Ink {

	public static class InkAnnotation implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private static transient Paint paint = new Paint();
		
		private InkPaint 		mPaint;
		private InkPath 		mPath;
		
		
		public InkAnnotation(InkPath path, AbstractAnnotationTool tool) {
			mPath = path;
			mPaint = new InkPaint(tool);
		}
		
		private InkAnnotation(Builder build) {
			mPaint = build.mPaint;
			mPath = build.mPath;
			
		}
		
		public int color() {
			return mPaint.color();
		}
		
		public float strokeWidth() {
			return mPaint.strokeWidth();
		}
		
		public String path() {
			return mPath.toString();
		}
	
		
		public InkPath ink() {
			return mPath;
		}
		
		public Xfermode mode() {
			return mPaint.mode();
		}
		
		public Paint paint() {
			paint.reset();
			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);
		    //paint.setDither(true);
		    paint.setColor(mPaint.color());
		    paint.setStyle(Paint.Style.STROKE);
		    paint.setStrokeJoin(Paint.Join.ROUND);
		    paint.setStrokeCap(Paint.Cap.ROUND);
		    paint.setStrokeWidth(mPaint.strokeWidth());
		    paint.setXfermode(mPaint.mode());
		    
		    return paint;
		}
		
		public void setInkPaint(InkPaint paint) {
			mPaint = paint;
		}
		
		public void setInkPath(InkPath path) {
			mPath = path;
		}
		
		public void draw(Canvas canvas, Paint paint) {	
			if(canvas != null) {
				paint.setColor(color());
				paint.setStrokeWidth(strokeWidth());
				paint.setXfermode(mode());
				canvas.drawPath(mPath, paint);
			}
		}
		
		public void load(String annotation) {
			
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			
			sb.append("<inkannotation>");
			sb.append(mPaint.toString());
			sb.append(mPath.toString());
			sb.append("</inkannotation>");
			
			return sb.toString();
		}
		
		public static class Builder {
			private InkPaint 		mPaint;
			private InkPath 		mPath;
			
			
			public Builder() {
				
			}
			
			public Builder paint(InkPaint paint) { mPaint = paint; return this; }
			public Builder path(InkPath path) { mPath = path; return this; }
			
			
			public InkAnnotation build() {
				return new InkAnnotation(this);
			}
		}
	}
	
	public static class InkPaint {
		
		private int mColor;
		private float mStrokeWidth;
		private Mode mMode = Mode.NORMAL;

		public InkPaint(AbstractAnnotationTool tool) {
			mColor = tool.color();
			mStrokeWidth = tool.width();

			if(tool.id() == AbstractAnnotationTool.PEN || tool.id() == AbstractAnnotationTool.HIGHLIGHTER) {
				mMode = Mode.NORMAL;
			} else {
				mMode = Mode.ERASER;
			}
		}
		
		public InkPaint(int color, float strokeWidth, Mode mode) {
			mColor = color;
			mStrokeWidth = strokeWidth;
			mMode = mode;
		}
		
		private InkPaint(Builder build) {
			mColor = build.mColor;
			mStrokeWidth = build.mStrokeWidth;
			mMode = build.mMode;
		}
		
		public int color() { return mColor; }
		
		public float strokeWidth() { return mStrokeWidth; }
		
		public void setColor(int color) {
			mColor = color;
		}
		
		public void setStrokeWidth(float width) {
			mStrokeWidth = width;
		}
		
		public Xfermode mode() { 
			switch(mMode) {
				case NORMAL:  return new PorterDuffXfermode(PorterDuff.Mode.XOR);
				case ERASER:  return new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
			}
			
			return new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			
			sb.append("<inkpaint>");
			sb.append("<color>" + String.valueOf(color()) + "</color>");
			sb.append("<strokewidth>" + String.valueOf(strokeWidth()) + "</strokewidth>");
			sb.append("<mode>" + mMode.toString() + "</mode>");
			sb.append("</inkpaint>");
			
			return sb.toString();
		}
		
		private static enum Mode {
			NORMAL,
			ERASER;
		}
		
		public static class Builder {
			
			private int mColor;
			private float mStrokeWidth;
			private Mode mMode = Mode.NORMAL;
			
			public Builder() {
				
			}
			
			public Builder color(int color) { mColor = color; return this; }
			public Builder strokeWidth(float strokeWidth) { mStrokeWidth = strokeWidth; return this; }
			
			public Builder mode(String mode) {
				if(mode.equalsIgnoreCase(Mode.NORMAL.name())) {
					mMode = Mode.NORMAL;
				} else {
					mMode = Mode.ERASER;
				}
				
				return this;
			}
			public Builder mode(Mode mode) { mMode = mode; return this; }
			
			public InkPaint build() {
				return new InkPaint(this);
			}
		}
	}
	
	public static class InkPath extends Path {
		
		private ArrayList<InkAction> mActions;
		
		public InkPath() {
			super();
			
			mActions = new ArrayList<InkAction>();
		}
		
		private InkPath(Builder build) {
			super();
			
			mActions = build.mActions;
			
			initialize();
		}
		
		private void initialize() {
			for(InkAction action : mActions) {
				switch(action.type()) {
					case MOVE_TO: 
						super.moveTo(action.x1(), action.y1());
						break;
					case LINE_TO:
						super.lineTo(action.x1(), action.y1());
						break;
					case QUAD_TO:
						super.quadTo(action.x1(), action.y1(), action.x2(), action.y2());
				}
			}
		}
		
		@Override
		public void moveTo(float x, float y) {
		    mActions.add(new InkAction(InkActionType.MOVE_TO, x, y));
		    super.moveTo(x, y);
		}

		@Override
		public void lineTo(float x, float y){
		    mActions.add(new InkAction(InkActionType.LINE_TO, x, y));
		    super.lineTo(x, y);
		}
		
		@Override
		public void quadTo(float x1, float y1, float x2, float y2) {
			mActions.add(new InkAction(InkActionType.QUAD_TO, x1, y1, x2, y2));
			super.quadTo(x1, y1, x2, y2);
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			
			sb.append("<inkpath>");
			for(InkAction action : mActions) {
				switch(action.type()) {
					case MOVE_TO: 
						sb.append("<moveto>");
						sb.append("<action>" + action.type().toString() + "</action>");
						sb.append("<x1>" + action.x1() + "</x1>");
						sb.append("<y1>" + action.y1() + "</y1>");
						sb.append("</moveto>");
						
						break;
					case LINE_TO:
						sb.append("<lineto>");
						sb.append("<action>" + action.type().toString() + "</action>");
						sb.append("<x1>" + action.x1() + "</x1>");
						sb.append("<y1>" + action.y1() + "</y1>");
						sb.append("</lineto>");
						
						break;
					case QUAD_TO:
						sb.append("<quadto>");
						sb.append("<action>" + action.type().toString() + "</action>");
						sb.append("<x1>" + action.x1() + "</x1>");
						sb.append("<y1>" + action.y1() + "</y1>");
						sb.append("<x2>" + action.x1() + "</x2>");
						sb.append("<y2>" + action.y1() + "</y2>");
						sb.append("</quadto>");
						
						break;
				}
			}
			sb.append("</inkpath>");
			
			return sb.toString();
		}


		public static class InkAction {
			
			private InkActionType 	mType;
		    private float[] 		mPoint;
		    
		    
		    public InkAction(InkActionType type, float x, float y) {
		    	mType = type;
		    	mPoint = new float[2];
		    	mPoint[0] = x;
		    	mPoint[1] = y;
		    }
		    
		    public InkAction(InkActionType type, float x1, float y1, float x2, float y2) {
		    	mType = type;
		    	mPoint = new float[4];
		    	mPoint[0] = x1;
		    	mPoint[1] = y1;
		    	mPoint[2] = x2;
		    	mPoint[3] = y2;
		    }
		    
		    public InkActionType type() { return mType; }
		    
		    public float x1() { return mPoint[0]; }
		    public float y1() { return mPoint[1]; }
		    public float x2() { return mPoint[2]; }
		    public float y2() { return mPoint[3]; }
		    
		    public static InkActionType stringToType(String type) {
		    	InkActionType action_type = InkActionType.LINE_TO;
				
				if(type.equalsIgnoreCase(InkActionType.LINE_TO.toString())) {
					action_type = InkActionType.LINE_TO;
				} else if(type.equalsIgnoreCase(InkActionType.MOVE_TO.toString())) {
					action_type = InkActionType.MOVE_TO;
				} else if(type.equalsIgnoreCase(InkActionType.QUAD_TO.toString())) {
					action_type = InkActionType.QUAD_TO;
				}
				
				return action_type;
		    }
		}

		public static enum InkActionType {
			MOVE_TO,
			LINE_TO,
			QUAD_TO;
		}
		
		public static class Builder {
			private ArrayList<InkAction> mActions;
			
			public Builder() {
				mActions = new ArrayList<InkAction>();
			}
			
			public Builder addAction(String type, float x, float y) {
				mActions.add(new InkAction(InkAction.stringToType(type), x, y));
				return this;
			}
			
			public Builder addAction(String type, float x1, float y1, float x2, float y2) {
				mActions.add(new InkAction(InkAction.stringToType(type), x1, y1, x2, y2));
				return this;
			}
			
			public InkPath build() {
				return new InkPath(this);
			}
		}
	}
}
