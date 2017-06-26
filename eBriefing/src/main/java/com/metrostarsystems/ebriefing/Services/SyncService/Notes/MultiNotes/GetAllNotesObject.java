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

package com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes;

import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes.GetAllNotesObject.GetAllNoteObject;
import com.metrostarsystems.ebriefing.Tags;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

/**
 * Created by jhyde on 2/18/2015.
 */
public class GetAllNotesObject extends AbstractSyncObject<GetAllNoteObject> {


    @Override
    public boolean isValid() {
        return mObjects != null;
    }

    public GetAllNotesObject(boolean valid) {
        super(valid);
    }

    private GetAllNotesObject(Builder build) {
        mObjects = build.mObjects;
    }



    public static class Builder extends AbstractSyncObject.Builder<GetAllNoteObject> {

        public Builder() { }

        @Override
        public Builder generate(ServerConnection connection, SoapObject object) {
            if(object == null) {
                return this;
            }

            int count = object.getPropertyCount();

            if(count > 0) {

                if(mObjects == null) {
                    mObjects = new ArrayList<GetAllNoteObject>();
                }

                for(int index = 0; index < object.getPropertyCount(); index++) {
                    add(connection, (SoapObject) object.getProperty(index));
                }
            }

            return this;
        }

        @Override
        protected void add(ServerConnection connection, SoapObject object) {

            SoapObject note = (SoapObject) object.getProperty(Tags.MULTINOTES_GET_ALL_NOTES_RESPONSE_NOTE);

            String note_id =
                    note.getPrimitiveProperty(Tags.MULTINOTES_GET_ALL_NOTES_RESPONSE_NOTE_ID).toString();

            String book_id =
                    note.getPrimitiveProperty(Tags.MULTINOTES_GET_ALL_NOTES_RESPONSE_BOOK_ID).toString();

            int book_version = 0;
            boolean is_deleted = false;

            try {
                book_version = Integer.valueOf(
                        note.getProperty(Tags.MULTINOTES_GET_ALL_NOTES_RESPONSE_BOOK_VERSION).toString());

                is_deleted = Boolean.valueOf(
                        note.getProperty(Tags.MULTINOTES_GET_ALL_NOTES_RESPONSE_IS_DELETED).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            String page_id =
                    note.getPrimitiveProperty(Tags.MULTINOTES_GET_ALL_NOTES_RESPONSE_PAGE_ID).toString();

            String date_created =
                    note.getPrimitiveProperty(Tags.MULTINOTES_GET_ALL_NOTES_RESPONSE_DATE_CREATED).toString();

            String date_modified =
                    note.getPrimitiveProperty(Tags.MULTINOTES_GET_ALL_NOTES_RESPONSE_DATE_MODIFIED).toString();

            String text =
                    note.getPrimitiveProperty(Tags.MULTINOTES_GET_ALL_NOTES_RESPONSE_NOTE_TEXT).toString();

            Page page = connection.app().data().database().pagesDatabase().page(page_id);

            if(page == null) {
                return;
            }

            int page_number = page.pageNumber();

            String chapter_id = connection.app().data().database().pagesDatabase().chapterIdByPage(page_id);

            mObjects.add(new GetAllNoteObject.Builder()
                                    .noteId(note_id)
                                    .bookId(book_id)
                                    .bookVersion(book_version)
                                    .chapterId(chapter_id)
                                    .pageId(page_id)
                                    .pageNumber(page_number)
                                    .dateCreated(date_created)
                                    .dateModified(date_modified)
                                    .text(text)
                                    .isDeleted(is_deleted)
                                    .build());
        }

        @Override
        public GetAllNotesObject build() {
            return new GetAllNotesObject(this);
        }
    }



    public static class GetAllNoteObject {
        private String 	mNoteId			= "";
        private String 	mBookId			= "";
        private int 	mBookVersion	= 0;
        private String	mChapterId		= "";
        private String	mPageId			= "";
        private int		mPageNumber		= 0;
        private String 	mDateCreated	= "";
        private String 	mDateModified	= "";
        private String 	mText		    = "";
        private boolean mIsDeleted      = false;

        public String noteId() { return mNoteId; }
        public String bookId() { return mBookId; }
        public int bookVersion() { return mBookVersion; }
        public String chapterId() { return mChapterId; }
        public String pageId() { return mPageId; }
        public int pageNumber() { return mPageNumber; }
        public String dateCreated() { return mDateCreated; }
        public String dateModified() { return mDateModified; }
        public String text() { return mText; }
        public boolean isDeleted() { return mIsDeleted; }

        public GetAllNoteObject(Builder build) {
            mNoteId			= build.mNoteId;
            mBookId			= build.mBookId;
            mBookVersion	= build.mBookVersion;
            mChapterId		= build.mChapterId;
            mPageId			= build.mPageId;
            mPageNumber		= build.mPageNumber;
            mDateCreated	= build.mDateCreated;
            mDateModified	= build.mDateModified;
            mText		    = build.mText;
            mIsDeleted      = build.mIsDeleted;
        }

        public static class Builder {
            private String 	mNoteId			= "";
            private String 	mBookId			= "";
            private int 	mBookVersion	= 0;
            private String	mChapterId		= "";
            private String	mPageId			= "";
            private int		mPageNumber		= 0;
            private String 	mDateCreated	= "";
            private String 	mDateModified	= "";
            private String 	mText		    = "";
            private boolean mIsDeleted      = false;

            public Builder() { }

            public Builder noteId(String id) { mNoteId = id; return this; }
            public Builder bookId(String id) { mBookId = id; return this; }
            public Builder bookVersion(int version) { mBookVersion = version; return this; }
            public Builder chapterId(String id) { mChapterId = id; return this; }
            public Builder pageId(String id) { mPageId = id; return this; }
            public Builder pageNumber(int number) { mPageNumber = number; return this; }
            public Builder dateCreated(String date) { mDateCreated = date; return this; }
            public Builder dateModified(String date) { mDateModified = date; return this; }
            public Builder text(String content) { mText = content; return this; }
            public Builder isDeleted(boolean deleted) { mIsDeleted = deleted; return this; }

            public GetAllNoteObject build() {
                return new GetAllNoteObject(this);
            }
        }
    }
}
