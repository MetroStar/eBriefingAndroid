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

package com.metrostarsystems.ebriefing.BookPage.Annotations.History;

import java.util.ArrayList;

import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Ink.InkAnnotation;


public abstract class AbstractAnnotationHistory {

	protected AnnotationHistoryType			mType = AnnotationHistoryType.NORMAL;
	protected ArrayList<InkAnnotation>    	mAnnotations;
	
	public AbstractAnnotationHistory(ArrayList<InkAnnotation> annotations, AnnotationHistoryType type) {
		mAnnotations = new ArrayList<InkAnnotation>();
		mAnnotations.addAll(annotations);
		mType = type;
	}
	
	public AbstractAnnotationHistory(InkAnnotation annotation, AnnotationHistoryType type) {
		mAnnotations = new ArrayList<InkAnnotation>();
		mAnnotations.add(annotation);
		mType = type;
	}
	
	public void addAll(ArrayList<InkAnnotation> annotations) {
		mAnnotations.addAll(annotations);
	}
	
	public void add(InkAnnotation annotation) {
		mAnnotations.add(annotation);
	}
	
	public ArrayList<InkAnnotation> restore() {
		return mAnnotations;
	}
	
	public AnnotationHistoryType type() {
		return mType;
	}
	
	public static enum AnnotationHistoryType {
		NORMAL,
		CLEAR
	}
}
