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

package com.metrostarsystems.ebriefing.BookPage;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class PageTransformer {
	
	public static class TestTransformer implements ViewPager.PageTransformer {

		@Override
	    public void transformPage(View page, float position) {
	
	    }
    }
	
	public static class FadeTransformer implements ViewPager.PageTransformer {
		@Override
		public void transformPage(View view, float position) {
			final float normalizedposition = Math.abs(Math.abs(position) - 1);
		    view.setAlpha(normalizedposition);
		}
	}
	
	public static class ZRotateTransformer implements ViewPager.PageTransformer {
		private static final float MIN_SCALE = 0.85f;
		
		@Override
		public void transformPage(View view, float position) {
			int pageWidth = view.getWidth();
	        int pageHeight = view.getHeight();

	        view.setAlpha(1);
			
			if (position < -1) { // [-Infinity,-1)
	            view.setAlpha(0);
	            view.setVisibility(View.VISIBLE);

			} else if (position <= 0) { // [-1,0]
	            // Use the default slide transition when moving to the left page
				view.setVisibility(View.VISIBLE);
				view.setPivotY(0);
				view.setRotationY(position * 5);
				

	        } else if (position <= 1) { // (0,1]
	            // Fade the page out.
	        	view.setVisibility(View.VISIBLE);
	            view.setAlpha(1 - position);
	            

	            // Counteract the default slide transition
	            view.setTranslationX(pageWidth * -position);


	        } else { // (1,+Infinity]
	            // This page is way off-screen to the right.
	            view.setAlpha(0);
	            view.setVisibility(View.VISIBLE);
	        }
		}
	} 
	
	public static class ZRotateOutTransformer implements ViewPager.PageTransformer {
		@Override
		public void transformPage(View view, float position) {
			view.setRotationY(position * 30);
		}
	} 
	
	public static class AccordionTransformer implements ViewPager.PageTransformer {
		@Override
		public void transformPage(View view, float position) {
			view.setTranslationX(-1 * view.getWidth() * position);
	        
	        if(position < 0) {
	        	view.setPivotX(0f);
	        } else if(position > 0) {
	        	view.setPivotX(view.getWidth());
	        }
	        
	        view.setScaleX(1-Math.abs(position));
		}
		
	}

	
	
	public static class FlipCenterTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View view, float position) {
            view.setTranslationX(-1 * view.getWidth() * position);
            if(position >= -.5 && position <= .5) {
                view.setAlpha(1);
            } else {
                view.setAlpha(0);
            }
            
            view.setRotationY(position * 180);
        }
    }
	
	public class ScaleFadeTransformer implements ViewPager.PageTransformer {
		 
	    private int mScreenXOffset;
	    
	    public ScaleFadeTransformer(Context context) {
	        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	        Display display = wm.getDefaultDisplay();
	        mScreenXOffset = display.getWidth()/2;
	    }
	    	
	    @Override
	    public void transformPage(View page, float position) {
	        final float transformValue = Math.abs(Math.abs(position) - 1);
	        // apply fade effect
	        page.setAlpha(transformValue);
	        if (position > 0) {
	            // apply zoom effect only for pages to the right
	            page.setScaleX(transformValue);
	            page.setScaleY(transformValue);
	            page.setPivotX(0.5f);
	            final float translateValue = position * -mScreenXOffset;
	            if (translateValue > -mScreenXOffset) {
	                page.setTranslationX(translateValue);
	            } else {
	                page.setTranslationX(0);
	            }
	        }
	    }
	}
	
	public class ZoomOutTransformer implements ViewPager.PageTransformer {
	    private static final float MIN_SCALE = 0.85f;
	    private static final float MIN_ALPHA = 0.5f;

	    public void transformPage(View view, float position) {
	        int pageWidth = view.getWidth();
	        int pageHeight = view.getHeight();

	        if (position < -1) { // [-Infinity,-1)
	            // This page is way off-screen to the left.
	            view.setAlpha(0);

	        } else if (position <= 1) { // [-1,1]
	            // Modify the default slide transition to shrink the page as well
	            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
	            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
	            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
	            if (position < 0) {
	                view.setTranslationX(horzMargin - vertMargin / 2);
	            } else {
	                view.setTranslationX(-horzMargin + vertMargin / 2);
	            }

	            // Scale the page down (between MIN_SCALE and 1)
	            view.setScaleX(scaleFactor);
	            view.setScaleY(scaleFactor);

	            // Fade the page relative to its size.
	            view.setAlpha(MIN_ALPHA +
	                    (scaleFactor - MIN_SCALE) /
	                    (1 - MIN_SCALE) * (1 - MIN_ALPHA));

	        } else { // (1,+Infinity]
	            // This page is way off-screen to the right.
	            view.setAlpha(0);
	        }
	    }
	}
	
	public class DepthTransformer implements ViewPager.PageTransformer {
	    private static final float MIN_SCALE = 0.75f;

	    public void transformPage(View view, float position) {
	        int pageWidth = view.getWidth();

	        if (position < -1) { // [-Infinity,-1)
	            // This page is way off-screen to the left.
	            view.setAlpha(0);

	        } else if (position <= 0) { // [-1,0]
	            // Use the default slide transition when moving to the left page
	            view.setAlpha(1);
	            view.setTranslationX(0);
	            view.setScaleX(1);
	            view.setScaleY(1);

	        } else if (position <= 1) { // (0,1]
	            // Fade the page out.
	            view.setAlpha(1 - position);

	            // Counteract the default slide transition
	            view.setTranslationX(pageWidth * -position);

	            // Scale the page down (between MIN_SCALE and 1)
	            float scaleFactor = MIN_SCALE
	                    + (1 - MIN_SCALE) * (1 - Math.abs(position));
	            view.setScaleX(scaleFactor);
	            view.setScaleY(scaleFactor);

	        } else { // (1,+Infinity]
	            // This page is way off-screen to the right.
	            view.setAlpha(0);
	        }
	    }
	}
	
	
}
