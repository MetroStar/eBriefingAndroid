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

package com.metrostarsystems.ebriefing.Data.Framework;

import java.io.File;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;
import com.radaee.pdf.Document;
import com.radaee.util.PDFFileStream;

public class BookUtilities {

	public static int totalInkAnnotations(MainApplication app, Book book, int pageNumber) {
		Document document = new Document();
		document.Close();

		PDFFileStream stream = new PDFFileStream();


		document = new Document();
		document.Close();
		
		Page page = app.data().database().pagesDatabase().pageByNumber(book.id(), pageNumber);
		
		String filepath = app.readHandle().path() + page.filePath();
		
		stream.open(filepath);
		
		int ret = document.OpenStream(stream, null);
		
		switch( ret ) {
	        case -1://need input password
	        	return -1;
	        case -2://unknown encryption
	        	return -1;
	        case -3://damaged or invalid format
	        	return -1;
	        case -10://access denied or invalid file path
	        	return -1;
	        case 0://succeeded, and continue
	            break;
	        default://unknown error
	        	return -1;
		}
		
		com.radaee.pdf.Page page_object = document.GetPage(0);
		
		page_object.ObjsStart();
		
		int count = page_object.GetAnnotCount();
		

		page_object.Close();
		document.Save();
		document.Close();
		stream.close();
		
		return count;
	}
	
	/**
	 * Remove all annotations from the page
	 * @param filesDir the file directory the page is located in
	 * @param pageNumber the page number of the page
	 */
	public static boolean removeInkAnnotationsFromPDF(MainApplication app, Book book, int pageNumber) {
		Document document = new Document();
		document.Close();
		
		boolean found = false;
		
		PDFFileStream stream = new PDFFileStream();
		
		Page page = app.data().database().pagesDatabase().pageByNumber(book.id(), pageNumber);
		
		String filepath = app.readHandle().path() + page.filePath();
		
		stream.open(filepath);
		
		int ret = document.OpenStream(stream, null);
		
		switch( ret ) {
	         case -1://need input password
	         	return false;
	         case -2://unknown encryption
	         	return false;
	         case -3://damaged or invalid format
	         	return false;
	         case -10://access denied or invalid file path
	         	return false;
	         case 0://succeeded, and continue
	             break;
	         default://unknown error
	         	return false;
		 }
 
		
		com.radaee.pdf.Page page_object = document.GetPage(0);
		
		if(page_object == null) {
			document.Close();
			stream.close();
			
			return false;
		}
		
		page_object.ObjsStart();
		
		int count = page_object.GetAnnotCount();
		for(int index = 0; index < count; index++) {
			com.radaee.pdf.Page.Annotation annotation = page_object.GetAnnot(0);
			
			if(annotation != null) {
				annotation.RemoveFromPage();
				found = true;
			}
		}

		page_object.Close();
		document.Save();
		document.Close();
		stream.close();
		
		return found;
		
	}
	
	public boolean hideInkAnnotationsInPDF(MainApplication app, Book book, int pageNumber) {
		Document document = new Document();
		document.Close();
		
		boolean found = false;

		PDFFileStream stream = new PDFFileStream();


		document = new Document();
		document.Close();

		Page page = app.data().database().pagesDatabase().pageByNumber(book.id(), pageNumber);
		
		String filepath = app.readHandle().path() + page.filePath();

		stream.open(filepath);

		int ret = document.OpenStream(stream, null);
		
		switch( ret ) {
	        case -1://need input password
	        	return false;
	        case -2://unknown encryption
	        	return false;
	        case -3://damaged or invalid format
	        	return false;
	        case -10://access denied or invalid file path
	        	return false;
	        case 0://succeeded, and continue
	            break;
	        default://unknown error
	        	return false;
		}

		com.radaee.pdf.Page page_object = document.GetPage(0);

		if(page_object == null) {
			document.Close();
			stream.close();

			return false;
		}

		page_object.ObjsStart();

		int count = page_object.GetAnnotCount();
		for(int index = 0; index < count; index++) {
			com.radaee.pdf.Page.Annotation annotation = page_object.GetAnnot(0);
			annotation.SetHide(true);
			found = true;
		}

		page_object.Close();
		document.Save();
		document.Close();
		stream.close();

		return found;

	}
	
	public static boolean showInkAnnotationsInPDF(MainApplication app, Book book, int pageNumber) {
		Document document = new Document();
		document.Close();
		
		boolean found = false;
		
		PDFFileStream stream = new PDFFileStream();


		Page page = app.data().database().pagesDatabase().pageByNumber(book.id(), pageNumber);
		
		String filepath = app.readHandle().path() + page.filePath();

		stream.open(filepath);

		int ret = document.OpenStream(stream, null);
		
		switch( ret ) {
	        case -1://need input password
	        	return false;
	        case -2://unknown encryption
	        	return false;
	        case -3://damaged or invalid format
	        	return false;
	        case -10://access denied or invalid file path
	        	return false;
	        case 0://succeeded, and continue
	            break;
	        default://unknown error
	        	return false;
		}

		com.radaee.pdf.Page page_object = document.GetPage(0);

		if(page_object == null) {
			document.Close();
			stream.close();

			return false;
		}

		page_object.ObjsStart();

		int count = page_object.GetAnnotCount();
		for(int index = 0; index < count; index++) {
			com.radaee.pdf.Page.Annotation annotation = page_object.GetAnnot(0);
			annotation.SetHide(false);
			found = true;
		}

		page_object.Close();
		document.Save();
		document.Close();
		stream.close();

		return found;

	}
	
	/**
	 * Remove all annotations from the page
	 * @param page the page to remove the annotations from
	 * @param pageNumber the page number of the page
	 */
	public static boolean removeInkAnnotationsFromPDF(com.radaee.pdf.Page page, int pageNumber) {
		boolean found = false;
		
		if(page == null) {
			return false;
		}
		
		page.ObjsStart();
		
		int count = page.GetAnnotCount();
		for(int index = 0; index < count; index++) {
			com.radaee.pdf.Page.Annotation annotation = page.GetAnnot(0);
			annotation.RemoveFromPage();
			found = true;
		}
		
		return found;
	}
	
	public static  boolean removeInkAnnotationsFromPDF(com.radaee.pdf.Page page, int removeAnnotations, int pageNumber) {
		boolean found = false;
		
		if(page == null) {
			return false;
		}
		
		int annotationCount = 0;
		
		page.ObjsStart();
		
		int count = page.GetAnnotCount();
		for(int index = 0; index < count; index++) {
			int remove = page.GetAnnotCount() - 1;

			com.radaee.pdf.Page.Annotation annotation = page.GetAnnot(remove);
			annotation.RemoveFromPage();
			found = true;
			
			annotationCount++;
			
			if(annotationCount == removeAnnotations) {
				return true;
			}
		}
		
		return found;
	}
}
