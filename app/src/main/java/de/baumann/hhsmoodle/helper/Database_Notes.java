/*
    This file is part of the HHS Moodle WebApp.

    HHS Moodle WebApp is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    HHS Moodle WebApp is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the Diaspora Native WebApp.

    If not, see <http://www.gnu.org/licenses/>.
 */

package de.baumann.hhsmoodle.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.baumann.hhsmoodle.R;

public class Database_Notes extends SQLiteOpenHelper {

    public Database_Notes(Context context)
            throws NameNotFoundException { super(context,
            "notes_v2.db",
            null,
            context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newViewsion) {
    }

    private void createTable(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE bookmarks (" +
                        "seqno NUMBER NOT NULL, " +
                        "title TEXT NOT NULL, " +
                        "cont TEXT NOT NULL, " +
                        "icon TEXT NOT NULL, " +
                        "attachment TEXT NOT NULL, " +
                        "createDate TEXT NOT NULL, " +
                        "PRIMARY KEY(seqno))"
        );
    }

    public void loadInitialData() {

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String dateCreate = format.format(date);

        int seqno = 0;

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        SQLiteStatement stmt = db.compileStatement("INSERT INTO bookmarks VALUES(?, ?, ?, ?, ?, ?)");
        stmt.bindLong(1, seqno);
        stmt.bindString(2, "Default entry | Standardeintrag");
        stmt.bindString(3, "Click to open | Anklicken zum Öffnen | Long click for more options | Lange drücken für mehr Optionen");
        stmt.bindString(4, "1");
        stmt.bindString(5, "");
        stmt.bindString(6, dateCreate);
        stmt.executeInsert();

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public int getRecordCount() {
        SQLiteDatabase db = getReadableDatabase();

        int ret = 0;

        String sql = "SELECT COUNT(*) FROM bookmarks";
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            ret = c.getInt(0);
        }
        c.close();
        db.close();

        return ret;
    }

    public void getBookmarks(ArrayList<String[]> data, Context context) {
        SQLiteDatabase db = getReadableDatabase();

        PreferenceManager.setDefaultValues(context, R.xml.user_settings, false);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        if (sp.getString("sortDB", "title").equals("title")) {
            String sql = "SELECT seqno,title,cont,icon,attachment,createDate FROM bookmarks ORDER BY title";
            Cursor c = db.rawQuery(sql, null);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                String[] strAry = {c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5)};
                data.add(strAry);
                c.moveToNext();
            }
            c.close();
            db.close();
        } else if (sp.getString("sortDB", "title").equals("seqno")) {
            String sql = "SELECT seqno,title,cont,icon,attachment,createDate FROM bookmarks ORDER BY seqno";
            Cursor c = db.rawQuery(sql, null);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                String[] strAry = {c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5)};
                data.add(strAry);
                c.moveToNext();
            }
            c.close();
            db.close();
        } else if (sp.getString("sortDB", "title").equals("icon")) {
            String sql = "SELECT seqno,title,cont,icon,attachment,createDate FROM bookmarks ORDER BY icon";
            Cursor c = db.rawQuery(sql, null);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                String[] strAry = {c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5)};
                data.add(strAry);
                c.moveToNext();
            }
            c.close();
            db.close();
        } else if (sp.getString("sortDB", "title").equals("create")) {
            String sql = "SELECT seqno,title,cont,icon,attachment,createDate FROM bookmarks ORDER BY createDate";
            Cursor c = db.rawQuery(sql, null);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                String[] strAry = {c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5)};
                data.add(strAry);
                c.moveToNext();
            }
            c.close();
            db.close();
        } else if (sp.getString("sortDB", "title").equals("attachment")) {
            String sql = "SELECT seqno,title,cont,icon,attachment,createDate FROM bookmarks ORDER BY attachment";
            Cursor c = db.rawQuery(sql, null);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                String[] strAry = {c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5)};
                data.add(strAry);
                c.moveToNext();
            }
            c.close();
            db.close();
        }
    }

    public void addBookmark(String title, String cont, String icon, String attachment, String create) {
        int seqno;

        SQLiteDatabase db = getWritableDatabase();

        String sql = "SELECT MAX(seqno) FROM bookmarks";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        seqno = c.getInt(0) + 1;

        db.beginTransaction();

        SQLiteStatement stmt = db.compileStatement("INSERT INTO bookmarks VALUES(?, ?, ?, ?, ?, ?)");
        stmt.bindLong(1, seqno);
        stmt.bindString(2, title);
        stmt.bindString(3, cont);
        stmt.bindString(4, icon);
        stmt.bindString(5, attachment);
        stmt.bindString(6, create);
        stmt.executeInsert();

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        c.close();
    }

    public void deleteNote(int seqno) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        SQLiteStatement stmt = db.compileStatement("DELETE FROM bookmarks WHERE seqno = ?");
        stmt.bindLong(1, seqno);
        stmt.execute();

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }
}