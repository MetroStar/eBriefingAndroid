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

package com.metrostarsystems.ebriefing.Data.Framework.Print;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Managers.ManagerImages;
import com.metrostarsystems.ebriefing.Data.Framework.Notes.Note;
import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;
import com.metrostarsystems.ebriefing.MainApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class PrintDocument {

    private static final String TAG = PrintDocument.class.getSimpleName();
	
	public static final int	MAX_PAGE_LIMIT = 100;

    private MainApplication mApp;
    private Book            mBook;
	private boolean         mPrintColor 		= true;
	private boolean         mPrintAnnotations 	= true;
	private boolean         mPrintNotes		 	= false;
    private PageDocument.NoteOrientation mOrientation = PageDocument.NoteOrientation.PORTRAIT_SIDE_BY_SIDE;
	
	private ArrayList<PageDocument> mPages = null;

    public MainApplication app() { return mApp; }
    public Book book() { return mBook; }
	
	public ArrayList<PageDocument> pages() {
		return mPages;
	}

    public int size() { return mPages.size(); }

    public PageDocument page(int index) {
        return mPages.get(index);
    }

    private PrintDocument(Builder build) {
        mApp                = build.mApp;
        mBook               = build.mBook;
        mPrintColor         = build.mPrintColor;
        mPrintAnnotations   = build.mPrintAnnotations;
        mPrintNotes         = build.mPrintNotes;
        mPages              = build.mPages;
        mOrientation        = build.mOrientation;
    }

    public static class Builder {
        private MainApplication mApp;
        private Book            mBook;
        private boolean         mPrintColor 		= true;
        private boolean         mPrintAnnotations 	= true;
        private boolean         mPrintNotes		 	= false;
        private PageDocument.NoteOrientation mOrientation = PageDocument.NoteOrientation.PORTRAIT_SIDE_BY_SIDE;

        private ArrayList<PageDocument> mPages = new ArrayList<PageDocument>();

        public Builder(MainApplication app, Book book) {
            mApp = app;
            mBook = book;
        }

        public Builder() {

        }

        public int size() { return mPages.size(); }

        public boolean isPrintColor() { return mPrintColor; }
        public boolean isPrintAnnotations() { return mPrintAnnotations; }
        public boolean isPrintNotes() { return mPrintNotes; }

        public Builder orientation(PageDocument.NoteOrientation orientation) { mOrientation = orientation; return this; }

        public Builder printColor(boolean color) { mPrintColor = color; return this; }
        public Builder printAnnotations(boolean annotations) { mPrintAnnotations = annotations; return this; }
        public Builder printNotes(boolean notes) { mPrintNotes = notes; return this; }

        /**
         * Clears the documents pages
         * @return
         */
        public Builder clear() { mPages.clear(); return this; }

        public Builder generatePage(int pageNumber) {

            if(!mPrintNotes) {
                generatePagination(pageNumber);
            } else {
                generatePagination(pageNumber, mOrientation);
            }

            return this;
        }

        public Builder generatePages(ArrayList<Integer> pages) {

            for(int index = 0; index < pages.size(); index++) {
                int page_number = pages.get(index);

                if(!mPrintNotes) {
                    generatePagination(page_number);
                } else {
                    if(mApp.data().database().notesDatabase().has(mBook.id(), page_number)) {
                        generatePagination(page_number, mOrientation);
                    } else {
                        generatePagination(page_number);
                    }
                }
            }

            return this;
        }




        private Builder addPage(PageDocument page) {
            if(page != null) {
                mPages.add(page);
            }

            Collections.sort(mPages, PageDocument.PageComparator);

            return this;
        }

        private void generatePagination(int pageNumber) {
            Page page = mApp.data().database().pagesDatabase().pageByNumber(mBook.id(), pageNumber);
            String file_path = mApp.readHandle().path() + page.filePath();
            Bitmap page_bitmap = ManagerImages.getBitmapFromPage(file_path, 1.5f, mPrintAnnotations);

            PageDocument.Builder document = new PageDocument.Builder(mApp, mBook, pageNumber);
            document.noteOrientation(PageDocument.NoteOrientation.NO_NOTES);
            document.printAnnotations(mPrintAnnotations);
            document.printColor(mPrintColor);
            document.pageHeight(page_bitmap.getHeight());
            document.pageWidth(page_bitmap.getWidth());

            // Add page
            addPage(document.build());
        }

        private void generatePagination(int pageNumber, PageDocument.NoteOrientation orientation) {
            Page page = mApp.data().database().pagesDatabase().pageByNumber(mBook.id(), pageNumber);
            String file_path = mApp.readHandle().path() + page.filePath();
            Bitmap page_bitmap = ManagerImages.getBitmapFromPage(file_path, 1.5f, mPrintAnnotations);

            Bitmap note_bitmap = null;

            PageDocument.Builder document = null;

            ArrayList<Note> notes = mApp.data().database().notesDatabase().notesByNumber(mBook.id(), pageNumber);

            boolean note_column1 = true;
            boolean note_column2 = false;

            int page_height = 0;
            int page_width = 0;

            while(!notes.isEmpty()) {

                if(note_bitmap != null && !note_bitmap.isRecycled()) {
                    note_bitmap.recycle();
                }

                switch(orientation) {
                    case LANDSCAPE_NOTES_ONLY:
                        note_bitmap = Bitmap.createBitmap(page_bitmap.getWidth() / 2, page_bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                        page_height = page_bitmap.getWidth();
                        page_width = page_bitmap.getHeight();
                        break;
                    case LANDSCAPE_SIDE_BY_SIDE:
                        note_bitmap = Bitmap.createBitmap(page_bitmap.getHeight() / 2 - (PageDocument.PAGE_MARGIN * 6), page_bitmap.getWidth(), Bitmap.Config.ARGB_8888);
                        page_height = page_bitmap.getWidth();
                        page_width = page_bitmap.getHeight();
                        break;
                    case PORTRAIT_NOTES_ONLY:
                        note_bitmap = Bitmap.createBitmap((page_bitmap.getWidth() / 2) - (PageDocument.PAGE_MARGIN * 2), page_bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                        page_height = page_bitmap.getHeight();
                        page_width = page_bitmap.getWidth();
                        break;
                    case PORTRAIT_SIDE_BY_SIDE:
                        note_bitmap = Bitmap.createBitmap((page_bitmap.getWidth() / 2) - (PageDocument.PAGE_MARGIN * 8), page_bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                        page_height = page_bitmap.getHeight();
                        page_width = page_bitmap.getWidth();
                        break;
                }

                if(note_column1) {
                    document = new PageDocument.Builder(mApp, mBook, pageNumber);
                    document.noteOrientation(orientation);
                    document.printAnnotations(mPrintAnnotations);
                    document.printColor(mPrintColor);
                    document.pageHeight(page_height);
                    document.pageWidth(page_width);
                    document.noteWidth(note_bitmap.getWidth());
                    document.noteHeight(note_bitmap.getHeight());
                }

                Canvas note_canvas = new Canvas(note_bitmap);
                note_canvas.drawColor(Color.WHITE);

                float y_pos = 0;
                int available_height = note_canvas.getHeight();


                Iterator<Note> iter = notes.iterator();

                while(iter.hasNext()) {
                    Note note = iter.next();

                    StaticLayout header_layout = new StaticLayout("\n" + note.dateModifiedFormat() + "\n",
                            PageDocument.mContentHeaderTextPaint,
                            note_canvas.getWidth(),
                            Layout.Alignment.ALIGN_NORMAL,
                            1,
                            0,
                            false);

                    StaticLayout note_layout = new StaticLayout(note.content(),
                            PageDocument.mContentTextPaint,
                            note_canvas.getWidth(),
                            Layout.Alignment.ALIGN_NORMAL,
                            1,
                            0,
                            false);

                    int note_height = header_layout.getHeight() + note_layout.getHeight();

                    if(note_height <= available_height) {
                        note_canvas.save();

                        note_canvas.translate(0f, y_pos);

                        header_layout.draw(note_canvas);

                        note_canvas.restore();

                        note_canvas.save();

                        note_canvas.translate(0f, y_pos + header_layout.getHeight() - 15);

                        note_layout.draw(note_canvas);

                        note_canvas.restore();

                        y_pos += note_height;
                        available_height -= note_height;

                        if(note_column1) {
                            document.noteColumn1(note);
                        }

                        if(note_column2) {
                            document.noteColumn2(note);
                        }

                        iter.remove();
                    } else {
                        // notes dont fit anymore

                        switch(orientation) {
                           case LANDSCAPE_SIDE_BY_SIDE:
                               addPage(document.build());
                               orientation = PageDocument.NoteOrientation.LANDSCAPE_NOTES_ONLY;
                               break;
                            case PORTRAIT_SIDE_BY_SIDE:
                               addPage(document.build());
                               orientation = PageDocument.NoteOrientation.PORTRAIT_NOTES_ONLY;
                               note_column1 = true;
                               break;
                           case PORTRAIT_NOTES_ONLY:

                               if(note_column2) {
                                   addPage(document.build());
                               }

                               if(note_column1) {
                                   note_column1 = false;
                                   // Reset height
                                   available_height = note_canvas.getHeight();
                                   note_column2 = true;
                               }

                               break;
                           case LANDSCAPE_NOTES_ONLY:
                               if(note_column2) {
                                   addPage(document.build());
                               }

                               if(note_column1) {
                                   note_column1 = false;
                                   // Reset height
                                   available_height = note_canvas.getHeight();
                                   note_column2 = true;
                               }

                               break;
                        }
                        break;
                    }
                }
            }

            // Add final page
            addPage(document.build());
        }

        public PrintDocument build() {
            return new PrintDocument(this);
        }
    }




}
