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
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.widget.ImageView;

import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Managers.ManagerImages;
import com.metrostarsystems.ebriefing.Data.Framework.Notes.Note;
import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;
import com.metrostarsystems.ebriefing.MainApplication;
import com.radaee.pdf.Document;
import com.radaee.util.PDFFileStream;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.UUID;

/**
 * Created by jhyde on 4/10/2015.
 */
public class PageDocument {

    private static final String TAG = PageDocument.class.getSimpleName();

    public static final int PAGE_MARGIN = 20;
    public static final float NOTE_TEXT_SIZE = 14;
    public static final float NOTE_TITLE_OFFSET = 20;
    public static final float PAGE_NUMBER_TEXT_SIZE = 14;
    public static final float PAGE_HEADER_TEXT_SIZE = 20;

    private MainApplication mApp;
    private Book            mBook;
    private String          mId;
    private int             mPageNumber = 0;
    private NoteOrientation mOrientation;
    private int             mPageWidth;
    private int             mPageHeight;
    private int             mNoteWidth;
    private int             mNoteHeight;
    private ArrayList<Note> mNotesColumn1;
    private ArrayList<Note> mNotesColumn2;
    private boolean         mPrintAnnotations = false;
    private boolean         mPrintColor = true;

    public static Paint     mPaint;
    public static TextPaint mContentTextPaint;
    public static TextPaint mContentHeaderTextPaint;
    public static TextPaint mHeaderTextPaint;
    public static TextPaint mHeaderPageTextPaint;
    public static TextPaint mNumberTextPaint;

    static {
        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);

        mContentTextPaint = new TextPaint();
        mContentTextPaint.setSubpixelText(true);
        mContentTextPaint.setColor(Color.BLACK);
        mContentTextPaint.setTextSize(NOTE_TEXT_SIZE);
        mContentTextPaint.setTextAlign(Paint.Align.LEFT);
        mContentTextPaint.setStyle(Paint.Style.FILL);
        mContentTextPaint.setAntiAlias(true);
        mContentTextPaint.setFilterBitmap(true);

        mContentHeaderTextPaint = new TextPaint();
        mContentHeaderTextPaint.setColor(Color.BLACK);
        mContentHeaderTextPaint.setTextSize(NOTE_TEXT_SIZE);
        mContentHeaderTextPaint.setFakeBoldText(true);
        mContentHeaderTextPaint.setTextAlign(Paint.Align.LEFT);
        mContentHeaderTextPaint.setAntiAlias(true);
        mContentHeaderTextPaint.setFilterBitmap(true);

        mHeaderTextPaint = new TextPaint();
        mHeaderTextPaint.setColor(Color.BLACK);
        mHeaderTextPaint.setTextSize(PAGE_HEADER_TEXT_SIZE);
        mHeaderTextPaint.setFakeBoldText(true);
        mHeaderTextPaint.setTextAlign(Paint.Align.LEFT);
        mHeaderTextPaint.setAntiAlias(true);
        mHeaderTextPaint.setFilterBitmap(true);

        mHeaderPageTextPaint = new TextPaint();
        mHeaderPageTextPaint.setColor(Color.BLACK);
        mHeaderPageTextPaint.setTextSize(PAGE_HEADER_TEXT_SIZE);
        mHeaderPageTextPaint.setTextAlign(Paint.Align.LEFT);
        mHeaderPageTextPaint.setAntiAlias(true);
        mHeaderPageTextPaint.setFilterBitmap(true);

        mNumberTextPaint = new TextPaint();
        mNumberTextPaint.setColor(Color.BLACK);
        mNumberTextPaint.setTextSize(PAGE_NUMBER_TEXT_SIZE);
        mNumberTextPaint.setTextAlign(Paint.Align.LEFT);
        mNumberTextPaint.setAntiAlias(true);
        mNumberTextPaint.setFilterBitmap(true);
    }

    public int pageNumber() { return mPageNumber; }
    public NoteOrientation orientation() { return mOrientation; }


    public Bitmap generateBitmap() {
        Bitmap combined_bitmap = null;

        Log.i(TAG, "Orientation: " + mOrientation);

        switch(mOrientation) {
            case NO_NOTES: {
                Bitmap page_bitmap = generatePageBitmap(1.5f);
                return page_bitmap;
            }

            case PORTRAIT_SIDE_BY_SIDE: {
                Bitmap page_bitmap = generatePageBitmap(.9f);
                Bitmap note_bitmap = generateNoteBitmap(mNotesColumn1);

                combined_bitmap = generatePagePortrait(page_bitmap, note_bitmap);

                page_bitmap.recycle();
                note_bitmap.recycle();
                return combined_bitmap;
            }

            case PORTRAIT_NOTES_ONLY: {


                if(mNotesColumn1.size() > 0 && mNotesColumn2.isEmpty()) {
                    Bitmap note_bitmap1 = generateNoteBitmap(mNotesColumn1);

                    combined_bitmap = generateSingleColumnNotesPortrait(note_bitmap1);

                    note_bitmap1.recycle();

                } else if(mNotesColumn1.size() > 0 && mNotesColumn2.size() > 0) {
                    Bitmap note_bitmap1 = generateNoteBitmap(mNotesColumn1);
                    Bitmap note_bitmap2 = generateNoteBitmap(mNotesColumn2);

                    combined_bitmap = generateDoubleColumnNotesPortrait(note_bitmap1, note_bitmap2);

                    note_bitmap1.recycle();
                    note_bitmap2.recycle();

                }

                return combined_bitmap;
            }

            case LANDSCAPE_SIDE_BY_SIDE: {
                Bitmap page_bitmap = generatePageBitmap(1.1f);
                Bitmap note_bitmap = generateNoteBitmap(mNotesColumn1);

                combined_bitmap = generatePageLandscape(page_bitmap, note_bitmap);

                return combined_bitmap;
            }

            case LANDSCAPE_NOTES_ONLY: {


                if(mNotesColumn1.size() > 0 && mNotesColumn2.isEmpty()) {
                    Bitmap note_bitmap1 = generateNoteBitmap(mNotesColumn1);

                    combined_bitmap = generateSingleColumnNotesLandscape(note_bitmap1);

                    note_bitmap1.recycle();

                } else if(mNotesColumn1.size() > 0 && mNotesColumn2.size() > 0) {
                    Bitmap note_bitmap1 = generateNoteBitmap(mNotesColumn1);
                    Bitmap note_bitmap2 = generateNoteBitmap(mNotesColumn2);

                    combined_bitmap = generateDoubleColumnNotesLandscape(note_bitmap1, note_bitmap2);

                    note_bitmap1.recycle();
                    note_bitmap2.recycle();

                }

                return combined_bitmap;
            }

        }

        return combined_bitmap;
    }

    private Bitmap generatePageLandscape(Bitmap pageBitmap, Bitmap noteBitmap) {



        if(!mPrintColor) {
            Log.i(TAG, "Print black & white");
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);
            mPaint.setColorFilter(new ColorMatrixColorFilter(cm));
        } else {
            mPaint.setColorFilter(null);
        }

        Bitmap bitmap = Bitmap.createBitmap((int) mPageWidth,
                (int) mPageHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        int notes = mApp.data().database().notesDatabase().countByPage(mBook.id(), mPageNumber);

        canvas.drawBitmap(pageBitmap, PAGE_MARGIN, PAGE_MARGIN, mPaint);
        canvas.drawBitmap(noteBitmap, mPageWidth / 2 + (PAGE_MARGIN * 6), PAGE_MARGIN + PageDocument.NOTE_TITLE_OFFSET, mPaint);
        canvas.drawText(String.valueOf(mPageNumber), mPageWidth / 2, mPageHeight - PAGE_MARGIN, mNumberTextPaint);
        canvas.drawText(String.valueOf(notes) + " Notes", pageBitmap.getWidth() + (PAGE_MARGIN * 2), PAGE_MARGIN * 2, mHeaderTextPaint);
        canvas.drawText("(pg " + String.valueOf(mPageNumber) + ")", pageBitmap.getWidth() + (PAGE_MARGIN * 6), PAGE_MARGIN * 2, mHeaderPageTextPaint);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

    }

    private Bitmap generatePagePortrait(Bitmap pageBitmap, Bitmap noteBitmap) {

        if(!mPrintColor) {
            Log.i(TAG, "Print black & white");
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);
            mPaint.setColorFilter(new ColorMatrixColorFilter(cm));
        } else {
            mPaint.setColorFilter(null);
        }



        Bitmap bitmap = Bitmap.createBitmap((int) mPageWidth,
                (int) mPageHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        int notes = mApp.data().database().notesDatabase().countByPage(mBook.id(), mPageNumber);


        canvas.drawBitmap(pageBitmap, PAGE_MARGIN, PAGE_MARGIN, mPaint);
        canvas.drawBitmap(noteBitmap, pageBitmap.getWidth() + (PAGE_MARGIN * 2), PAGE_MARGIN + PageDocument.NOTE_TITLE_OFFSET, mPaint);
        canvas.drawText(String.valueOf(mPageNumber), mPageWidth / 2, mPageHeight - PAGE_MARGIN, mNumberTextPaint);
        canvas.drawText(String.valueOf(notes) + " Notes", pageBitmap.getWidth() + (PAGE_MARGIN * 2), PAGE_MARGIN * 2, mHeaderTextPaint);
        canvas.drawText("(pg " + String.valueOf(mPageNumber) + ")", pageBitmap.getWidth() + (PAGE_MARGIN * 6), PAGE_MARGIN * 2, mHeaderPageTextPaint);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());

    }

    private Bitmap generateSingleColumnNotesPortrait(Bitmap noteBitmap1) {

        Bitmap bitmap = Bitmap.createBitmap((int) mPageWidth,
                (int) mPageHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        canvas.drawBitmap(noteBitmap1, PAGE_MARGIN, PAGE_MARGIN + NOTE_TITLE_OFFSET, mPaint);
        canvas.drawText("Notes Continued ", PAGE_MARGIN, PAGE_MARGIN * 2, mHeaderTextPaint);
        canvas.drawText("(pg " + String.valueOf(mPageNumber) + ")", PAGE_MARGIN * 9, PAGE_MARGIN * 2, mHeaderPageTextPaint);
        canvas.drawText(String.valueOf(mPageNumber),mPageWidth / 2, mPageHeight - PAGE_MARGIN, mNumberTextPaint);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());

    }

    private Bitmap generateDoubleColumnNotesPortrait(Bitmap noteBitmap1, Bitmap noteBitmap2) {



        Bitmap bitmap = Bitmap.createBitmap((int) mPageWidth,
                (int) mPageHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        canvas.drawBitmap(noteBitmap1, PAGE_MARGIN, PAGE_MARGIN + NOTE_TITLE_OFFSET, mPaint);
        canvas.drawBitmap(noteBitmap2, mNoteWidth + (PAGE_MARGIN * 2), PAGE_MARGIN + NOTE_TITLE_OFFSET, mPaint);
        canvas.drawText("Notes Continued ", PAGE_MARGIN, PAGE_MARGIN * 2, mHeaderTextPaint);
        canvas.drawText("(pg " + String.valueOf(mPageNumber) + ")", PAGE_MARGIN * 3, PAGE_MARGIN * 2, mHeaderPageTextPaint);
        canvas.drawText(String.valueOf(mPageNumber),mPageWidth / 2, mPageHeight - PAGE_MARGIN, mNumberTextPaint);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());

    }

    private Bitmap generateSingleColumnNotesLandscape(Bitmap noteBitmap1) {

        Bitmap bitmap = Bitmap.createBitmap((int) mPageWidth,
                (int) mPageHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        canvas.drawBitmap(noteBitmap1, PAGE_MARGIN, PAGE_MARGIN + NOTE_TITLE_OFFSET, mPaint);
        canvas.drawText("Notes Continued ", PAGE_MARGIN, PAGE_MARGIN * 2, mHeaderTextPaint);
        canvas.drawText("(pg " + String.valueOf(mPageNumber) + ")", PAGE_MARGIN * 9, PAGE_MARGIN * 2, mHeaderPageTextPaint);
        canvas.drawText(String.valueOf(mPageNumber),mPageWidth / 2, mPageHeight - PAGE_MARGIN, mNumberTextPaint);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

    }

    private Bitmap generateDoubleColumnNotesLandscape(Bitmap noteBitmap1, Bitmap noteBitmap2) {

        Bitmap bitmap = Bitmap.createBitmap((int) mPageWidth,
                (int) mPageHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        canvas.drawBitmap(noteBitmap1, PAGE_MARGIN, PAGE_MARGIN + NOTE_TITLE_OFFSET, mPaint);
        canvas.drawBitmap(noteBitmap2, mNoteWidth + (PAGE_MARGIN * 2), PAGE_MARGIN + NOTE_TITLE_OFFSET, mPaint);
        canvas.drawText("Notes Continued ", PAGE_MARGIN, PAGE_MARGIN * 2, mHeaderTextPaint);
        canvas.drawText("(pg " + String.valueOf(mPageNumber) + ")", PAGE_MARGIN * 3, PAGE_MARGIN * 2, mHeaderPageTextPaint);
        canvas.drawText(String.valueOf(mPageNumber),mPageWidth / 2, mPageHeight - PAGE_MARGIN, mNumberTextPaint);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    private Bitmap generateNoteBitmap(ArrayList<Note> notes) {
        Bitmap note_bitmap = null;

        note_bitmap = Bitmap.createBitmap(mNoteWidth, mNoteHeight, Bitmap.Config.ARGB_8888);

        Canvas note_canvas = new Canvas(note_bitmap);
        note_canvas.drawColor(Color.WHITE);


        float y_pos = 0;

        Iterator<Note> iter = notes.iterator();

        while(iter.hasNext()) {
            Note note = iter.next();

            StaticLayout header_layout = new StaticLayout("\n" + note.dateModifiedFormat() + "\n",
                    mContentHeaderTextPaint,
                    note_canvas.getWidth(),
                    Layout.Alignment.ALIGN_NORMAL,
                    1,
                    0,
                    false);

            StaticLayout note_layout = new StaticLayout(note.content(),
                    mContentTextPaint,
                    note_canvas.getWidth(),
                    Layout.Alignment.ALIGN_NORMAL,
                    1,
                    0,
                    false);

            float note_height = header_layout.getHeight() + note_layout.getHeight();

            note_canvas.save();

            note_canvas.translate(0f, y_pos);

            header_layout.draw(note_canvas);

            note_canvas.restore();

            note_canvas.save();

            note_canvas.translate(0f, y_pos + header_layout.getHeight() - 15);

            note_layout.draw(note_canvas);

            note_canvas.restore();

            y_pos += note_height;
        }

        return note_bitmap;
    }

    private Bitmap generatePageBitmap(float scale) {
        Page book_page = mApp.data().database().pagesDatabase().pageByNumber(mBook.id(), mPageNumber);

        String file_path = mApp.readHandle().path() + book_page.filePath();

        Document document = new Document();
        document.Close();

        PDFFileStream stream = new PDFFileStream();
        stream.open(file_path);

        int ret = document.OpenStream(stream, null);

        switch( ret ) {
            case -1://need input password
                return null;
            case -2://unknown encryption
                return null;
            case -3://damaged or invalid format
                return null;
            case -10://access denied or invalid file path
                return null;
            case 0://succeeded, and continue
                break;
            default://unknown error
                return null;
        }

        com.radaee.pdf.Page page = document.GetPage(0);

        int w = (int) (document.GetPageWidth(0) * scale);
        int h = (int) (document.GetPageHeight(0) * scale);
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap page_bitmap = Bitmap.createBitmap(w, h, conf);

        page_bitmap.eraseColor(Color.WHITE);//draw background

        com.radaee.pdf.Matrix mat = new com.radaee.pdf.Matrix(scale, -scale, 0, h);

        page.ObjsStart();

        if(!mPrintAnnotations) {
            for(int index = 0; index < page.GetAnnotCount(); index++) {
                com.radaee.pdf.Page.Annotation annotation = page.GetAnnot(index);
                annotation.SetHide(true);
            }
        } else {
            for(int index = 0; index < page.GetAnnotCount(); index++) {
                com.radaee.pdf.Page.Annotation annotation = page.GetAnnot(index);
                annotation.SetHide(false);
            }
        }


        page.RenderToBmp(page_bitmap, mat);

        mat.Destroy();

        page.Close();
        document.Close();
        stream.close();

        return page_bitmap;
    }


    public static Comparator<PageDocument> PageComparator = new Comparator<PageDocument>() {
        @Override
        public int compare(PageDocument lhs, PageDocument rhs) {
            return Integer.compare(lhs.mPageNumber, rhs.mPageNumber);
        }
    };

    private PageDocument(Builder build) {
        mApp                = build.mApp;
        mBook               = build.mBook;
        mId                 = build.mId;
        mPageNumber         = build.mPageNumber;
        mOrientation        = build.mOrientation;
        mNotesColumn1       = build.mNotesColumn1;
        mNotesColumn2       = build.mNotesColumn2;
        mPageWidth          = build.mPageWidth;
        mPageHeight         = build.mPageHeight;
        mNoteWidth          = build.mNoteWidth;
        mNoteHeight         = build.mNoteHeight;
        mPrintAnnotations   = build.mPrintAnnotations;
        mPrintColor         = build.mPrintColor;
    }

    public static class Builder {
        private MainApplication mApp;
        private Book            mBook;
        private String          mId = "";
        private int             mPageNumber = 0;
        private NoteOrientation mOrientation = NoteOrientation.PORTRAIT_SIDE_BY_SIDE;
        private ArrayList<Note> mNotesColumn1      = new ArrayList<Note>();
        private ArrayList<Note> mNotesColumn2      = new ArrayList<Note>();
        private int             mPageWidth = 0;
        private int             mPageHeight = 0;
        private int             mNoteWidth = 0;
        private int             mNoteHeight = 0;
        private boolean         mPrintAnnotations = false;
        private boolean         mPrintColor = true;

        public Builder(MainApplication app, Book book, int pageNumber) {
            mApp = app;
            mBook =  book;
            mId = UUID.randomUUID().toString();
            mPageNumber = pageNumber;
        }

        public Builder noteOrientation(NoteOrientation orientation) { mOrientation =  orientation; return this; }
        public Builder pageWidth(int width) { mPageWidth = width; return this; }
        public Builder pageHeight(int height) { mPageHeight = height; return this; }
        public Builder noteWidth(int width) { mNoteWidth = width; return this; }
        public Builder noteHeight(int height) { mNoteHeight = height; return this; }

        public Builder notesColumn1(ArrayList<Note> notes) {
            mNotesColumn1.addAll(notes);

            return this;
        }

        public Builder noteColumn1(Note note) {
            mNotesColumn1.add(note);

            return this;
        }

        public Builder notesColumn2(ArrayList<Note> notes) {
            mNotesColumn2.addAll(notes);

            return this;
        }

        public Builder noteColumn2(Note note) {
            mNotesColumn2.add(note);

            return this;
        }

        public Builder printAnnotations(boolean annotations) { mPrintAnnotations = annotations; return this; }
        public Builder printColor(boolean color) { mPrintColor = color; return this; }

        public PageDocument build() { return new PageDocument(this); }
    }

    public static enum NoteOrientation {
        NO_NOTES,
        PORTRAIT_SIDE_BY_SIDE,
        PORTRAIT_NOTES_ONLY,
        LANDSCAPE_SIDE_BY_SIDE,
        LANDSCAPE_NOTES_ONLY,
        PORTRAIT_TOP_TO_BOTTOM;
    }
}
