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

package com.metrostarsystems.ebriefing.BookPage.Annotations.Colors;

import android.graphics.Color;

public class AnnotationColor {
	
	public static final int PEN_BLACK = 0;
	public static final int PEN_BLUE = 1;
	public static final int PEN_GREEN = 2;
	public static final int PEN_RED = 3;
	public static final int PEN_WHITE = 4;
	
	public static final int HIGHLIGHTER_CYAN = 4;
	public static final int HIGHLIGHTER_GREEN = 5;
	public static final int HIGHLIGHTER_ORANGE = 6;
	public static final int HIGHLIGHTER_PINK = 7;
	public static final int HIGHLIGHTER_YELLOW = 8;

	private int mId;
	private int mColor;
	
	private AnnotationColor() {
		mColor = 0;
	}
	
	protected AnnotationColor(int alpha, int red, int green, int blue) {
		mColor = Color.argb(alpha, red, green, blue);
		
	}
	
	public int color() { return mColor; }
	
	public String colorHex() { 
		return Integer.toHexString(mColor);
	}
	
	public int id() {
		return mId; 
	}
	
	public void setAlpha(int alpha) {
		int a = alpha;

		int r = Color.red(mColor);
		int g = Color.green(mColor);
		int b = Color.blue(mColor);
		
		mColor = Color.argb(a, r, g, b);
	}

																			//    A    R    G    B
	public static final AnnotationColor PEN_WHITE()			{ return new Builder(PEN_WHITE).color("#FFFFFFFF").build(); }
	public static final AnnotationColor PEN_BLACK()			{ return new Builder(PEN_BLACK).color("#FF000000").build(); }
	public static final AnnotationColor PEN_RED() 			{ return new Builder(PEN_RED).color("#FFC40d14").build(); }
	public static final AnnotationColor PEN_GREEN() 		{ return new Builder(PEN_GREEN).color("#FF007236").build(); }
	public static final AnnotationColor PEN_BLUE() 			{ return new Builder(PEN_BLUE).color("#FF0F109E").build(); }
	
	public static final AnnotationColor HIGHLIGHTER_CYAN() 	{ return new Builder(HIGHLIGHTER_CYAN).color("#8000AEEF").build(); }
	public static final AnnotationColor HIGHLIGHTER_GREEN() { return new Builder(HIGHLIGHTER_GREEN).color("#8000FF00").build(); }
	public static final AnnotationColor HIGHLIGHTER_ORANGE(){ return new Builder(HIGHLIGHTER_ORANGE).color("#80F7941D").build(); }
	public static final AnnotationColor HIGHLIGHTER_PINK() 	{ return new Builder(HIGHLIGHTER_PINK).color("#80EC008C").build(); }
	public static final AnnotationColor HIGHLIGHTER_YELLOW(){ return new Builder(HIGHLIGHTER_YELLOW).color("#80FFF200").build(); }
	
	
	public static String ALPHA_100 	= "FF"; // 255
	public static String ALPHA_75 	= "C0"; // 192
	public static String ALPHA_50 	= "80"; // 128
	public static String ALPHA_25 	= "40"; // 64
	
	private AnnotationColor(Builder build) {
		mColor = build.mColor.mColor;
		mId = build.mColor.mId;
	}
	
	public static class Builder {
		private AnnotationColor mColor;
		
		
		public Builder(int id) {
			mColor = new AnnotationColor();
			
			mColor.mId = id;
			mColor.mColor = Color.BLACK;
		}
		
		public Builder color(int alpha, int red, int green, int blue) {
			mColor.mColor = Color.argb(alpha, red, green, blue); return this;
		}
		
		public Builder color(String color) {
			mColor.mColor = Color.parseColor(color); return this;
		}
		
		public Builder alpha(int alpha) {
			int a = alpha;
			int r = Color.red(mColor.mColor);
			int g = Color.green(mColor.mColor);
			int b = Color.blue(mColor.mColor);
			
			mColor.mColor = Color.argb(a, r, g, b);
			
			return this;
		}
		
		public AnnotationColor build() {
			return new AnnotationColor(this);
		}
	}
	
	
}
