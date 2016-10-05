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

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import de.baumann.hhsmoodle.R;

public class helpers {

    public static void switchToActivity(Activity from, Class to, String Extra, boolean finishFromActivity) {
        Intent intent = new Intent(from, to);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("url", Extra);
        from.startActivity(intent);
        if (finishFromActivity) {
            from.finish();
        }
    }

    public static void isOpened (Activity from) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(from);
        sharedPref.edit()
                .putBoolean("isOpened", false)
                .apply();
    }

    public static void isClosed (Activity from) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(from);
        sharedPref.edit()
                .putBoolean("isOpened", true)
                .apply();
    }

    public static void editNote (final Activity from) {

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(from);
        final EditText titleInput;
        final EditText textInput;
        final String priority = sharedPref.getString("handleTextIcon", "");

        LayoutInflater inflater = from.getLayoutInflater();

        final ViewGroup nullParent = null;
        View dialogView = inflater.inflate(R.layout.dialog_editnote, nullParent);

        titleInput = (EditText) dialogView.findViewById(R.id.note_title_input);
        textInput = (EditText) dialogView.findViewById(R.id.note_text_input);
        titleInput.setText(sharedPref.getString("handleTextTitle", ""));
        titleInput.setSelection(titleInput.getText().length());
        textInput.setText(sharedPref.getString("handleTextText", ""));
        textInput.setSelection(textInput.getText().length());

        final ImageButton be = (ImageButton) dialogView.findViewById(R.id.imageButtonPri);
        assert be != null;

        switch (priority) {
            case "":
                be.setImageResource(R.drawable.pr_green);
                sharedPref.edit()
                        .putString("handleTextIcon", "")
                        .apply();
                break;
            case "!":
                be.setImageResource(R.drawable.pr_yellow);
                sharedPref.edit()
                        .putString("handleTextIcon", "!")
                        .apply();
                break;
            case "!!":
                be.setImageResource(R.drawable.pr_red);
                sharedPref.edit()
                        .putString("handleTextIcon", "!!")
                        .apply();
                break;
        }

        be.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                final helpers.Item[] items = {
                        new Item(from.getString(R.string.note_priority_0), R.drawable.pr_green_1),
                        new Item(from.getString(R.string.note_priority_1), R.drawable.pr_yellow_1),
                        new Item(from.getString(R.string.note_priority_2), R.drawable.pr_red_1),
                };

                ListAdapter adapter = new ArrayAdapter<helpers.Item>(
                        from,
                        android.R.layout.select_dialog_item,
                        android.R.id.text1,
                        items){
                    public View getView(int position, View convertView, ViewGroup parent) {
                        //Use super class to create the View
                        View v = super.getView(position, convertView, parent);
                        TextView tv = (TextView)v.findViewById(android.R.id.text1);
                        tv.setTextSize(18);
                        tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);
                        //Add margin between image and text (support various screen densities)
                        int dp5 = (int) (5 * from.getResources().getDisplayMetrics().density + 0.5f);
                        tv.setCompoundDrawablePadding(dp5);

                        return v;
                    }
                };

                new android.app.AlertDialog.Builder(from)
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if (item == 0) {
                                    be.setImageResource(R.drawable.pr_green);
                                    sharedPref.edit()
                                            .putString("handleTextIcon", "")
                                            .apply();
                                } else if (item == 1) {
                                    be.setImageResource(R.drawable.pr_yellow);
                                    sharedPref.edit()
                                            .putString("handleTextIcon", "!")
                                            .apply();
                                } else if (item == 2) {
                                    be.setImageResource(R.drawable.pr_red);
                                    sharedPref.edit()
                                            .putString("handleTextIcon", "!!")
                                            .apply();
                                }
                            }
                        }).show();
            }
        });

        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(from)
                .setView(dialogView)
                .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        String seqno = sharedPref.getString("handleTextSeqno", "");

                        try {

                            final Database_Notes db = new Database_Notes(from);
                            String inputTitle = titleInput.getText().toString().trim();
                            String inputContent = textInput.getText().toString().trim();

                            db.addBookmark(inputTitle, inputContent, sharedPref.getString("handleTextIcon", ""));
                            db.close();

                            if (seqno.length() > 0) {
                                db.deleteNote((Integer.parseInt(seqno)));
                                sharedPref.edit()
                                        .putString("handleTextSeqno", "")
                                        .apply();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sharedPref.edit()
                                .putString("handleTextTitle", "")
                                .putString("handleTextText", "")
                                .putString("handleTextIcon", "")
                                .apply();
                        helpers.setNotesList(from);
                    }
                })
                .setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        sharedPref.edit()
                                .putString("handleTextTitle", "")
                                .putString("handleTextText", "")
                                .putString("handleTextIcon", "")
                                .apply();
                        dialog.cancel();
                    }
                });
        dialog.show();
    }

    private static class Item{
        public final String text;
        public final int icon;
        Item(String text, Integer icon) {
            this.text = text;
            this.icon = icon;
        }
        @Override
        public String toString() {
            return text;
        }
    }

    public static SpannableString textSpannable (String text) {
        SpannableString s;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            s = new SpannableString(Html.fromHtml(text,Html.FROM_HTML_MODE_LEGACY));
        } else {
            //noinspection deprecation
            s = new SpannableString(Html.fromHtml(text));
        }

        Linkify.addLinks(s, Linkify.WEB_URLS);
        return s;
    }

    private static void setNotesList(final Activity from) {

        final ListView listView = (ListView)from.findViewById(R.id.notes);

        ArrayList<HashMap<String,String>> mapList = new ArrayList<>();

        try {
            Database_Notes db = new Database_Notes(from);
            ArrayList<String[]> bookmarkList = new ArrayList<>();
            db.getBookmarks(bookmarkList);
            if (bookmarkList.size() == 0) {
                db.loadInitialData();
                db.getBookmarks(bookmarkList);
            }
            db.close();

            for (String[] strAry : bookmarkList) {
                HashMap<String, String> map = new HashMap<>();
                map.put("seqno", strAry[0]);
                map.put("title", strAry[1]);
                map.put("cont", strAry[2]);
                map.put("icon", strAry[3]);
                mapList.add(map);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(
                    from,
                    mapList,
                    R.layout.list_item_notes,
                    new String[] {"title", "cont"},
                    new int[] {R.id.textView_title, R.id.textView_des}
            ) {
                @Override
                public View getView (final int position, View convertView, ViewGroup parent) {

                    @SuppressWarnings("unchecked")
                    HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);
                    final String title = map.get("title");
                    final String cont = map.get("cont");
                    final String seqnoStr = map.get("seqno");
                    final String icon = map.get("icon");

                    View v = super.getView(position, convertView, parent);
                    ImageView i=(ImageView) v.findViewById(R.id.icon);

                    switch (icon) {
                        case "":
                            i.setImageResource(R.drawable.pr_green);
                            break;
                        case "!":
                            i.setImageResource(R.drawable.pr_yellow);
                            break;
                        case "!!":
                            i.setImageResource(R.drawable.pr_red);
                            break;
                    }

                    i.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {

                            final helpers.Item[] items = {
                                    new Item(from.getString(R.string.note_priority_0), R.drawable.pr_green_1),
                                    new Item(from.getString(R.string.note_priority_1), R.drawable.pr_yellow_1),
                                    new Item(from.getString(R.string.note_priority_2), R.drawable.pr_red_1),
                            };

                            ListAdapter adapter = new ArrayAdapter<helpers.Item>(
                                    from,
                                    android.R.layout.select_dialog_item,
                                    android.R.id.text1,
                                    items){
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    //Use super class to create the View
                                    View v = super.getView(position, convertView, parent);
                                    TextView tv = (TextView)v.findViewById(android.R.id.text1);
                                    tv.setTextSize(18);
                                    tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);
                                    //Add margin between image and text (support various screen densities)
                                    int dp5 = (int) (5 * from.getResources().getDisplayMetrics().density + 0.5f);
                                    tv.setCompoundDrawablePadding(dp5);

                                    return v;
                                }
                            };

                            new AlertDialog.Builder(from)
                                    .setAdapter(adapter, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int item) {
                                            if (item == 0) {
                                                try {
                                                    final Database_Notes db = new Database_Notes(from);
                                                    db.deleteNote((Integer.parseInt(seqnoStr)));
                                                    db.addBookmark(title, cont, "");
                                                    db.close();
                                                    setNotesList(from);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            } else if (item == 1) {
                                                try {
                                                    final Database_Notes db = new Database_Notes(from);
                                                    db.deleteNote((Integer.parseInt(seqnoStr)));
                                                    db.addBookmark(title, cont, "!");
                                                    db.close();
                                                    setNotesList(from);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            } else if (item == 2) {
                                                try {
                                                    final Database_Notes db = new Database_Notes(from);
                                                    db.deleteNote((Integer.parseInt(seqnoStr)));
                                                    db.addBookmark(title, cont, "!!");
                                                    db.close();
                                                    setNotesList(from);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }).show();
                        }
                    });
                    return v;
                }
            };

            if (listView != null) {
                listView.setAdapter(simpleAdapter);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
