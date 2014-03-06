package com.lonelyplanet.android.test;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/** This Activity shows a list of places */
public class MainActivity extends ListActivity {

    /** Literals of database fields */
    String _ID = "_id";
    String TITLE = "title";
    String DESCRIPTION = "description";

    /** List of places */
    ArrayList<Place> places = new ArrayList<Place>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new LoadPlaces().execute();
    }

    /**
     * AsyncTask that loads 30 places in the ArrayList places
     */
    private class LoadPlaces extends AsyncTask<Void, Void, ArrayList<Place>> {
        @Override
        protected ArrayList<Place> doInBackground(Void... voids) {
            /** This code just simulates a call to an API that returns 30 places in 5 seconds */
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }

            ArrayList<Place> list = new ArrayList<Place>();
            for (int i = 0; i < 30; i++) {
                list.add(new Place(i, "Place " + i, "Description " + i));
            }

            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<Place> list) {
            places = list;
            fillList();
            insertPlaces(list);
        }

    }

    /**
     * This method fills the list with the places
     */
    private void fillList() {
        PlacesAdapter adapter = new PlacesAdapter(this, R.layout.place_row, places);
        setListAdapter(adapter);
    }

    /**
     * This method saves places in the database, WE DON'T NEED TO UPDATE THEM
     */
    private void insertPlaces(ArrayList<Place> placesToSave) {
        DatabaseHelper mOpenHelper = new DatabaseHelper(this);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        if (db != null) {
            for (Place p : placesToSave) {
                ContentValues values = new ContentValues();
                values.put(_ID, p.getId());
                values.put(TITLE, p.getTitle());
                values.put(DESCRIPTION, p.getDescription());
                db.insert("Places", null, values);
            }
        }
    }

    /**
     * The adapter to fill the list of places
     */
    private class PlacesAdapter extends ArrayAdapter<Place> {

        Context mContext;
        int resource;
        ArrayList<Place> places;

        public PlacesAdapter(Context context, int resource, ArrayList<Place> places) {
            super(context, resource);
            this.mContext = context;
            this.resource = resource;
            this.places = places;
        }

        @Override
        public int getCount() {
            return places.size();
        }

        @Override
        public Place getItem(int position) {
            return places.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Place place = getItem(position);
            View v = LayoutInflater.from(mContext).inflate(resource, null);

            ((TextView) v.findViewById(R.id.tv_title)).setText(place.getTitle());
            ((TextView) v.findViewById(R.id.tv_description)).setText(place.getDescription());

            return v;
        }
    }

    /**
     * Entity Place with an Id, a Title and a Description
     */
    class Place {
        private long id;
        private String title;
        private String description;

        public Place() {
        }

        public Place(long id, String title, String description) {
            this.id = id;
            this.title = title;
            this.description = description;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    /** Database helper */
    class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context ctx) {
            super(ctx, "androidTest.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE Places ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "title TEXT NOT NULL, "
                    + "description TEXT NOT NULL )");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int preVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS Places");
            onCreate(db);
        }
    }
}
