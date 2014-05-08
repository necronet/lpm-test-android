package com.lonelyplanet.android.test;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/** This Activity shows a list of places */
public class MainActivity extends ListActivity {

    private PlacesAdapter mAdapter ;

    /** Literals of database fields */
    String _ID = "_id";
    String TITLE = "title";
    String DESCRIPTION = "description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            ArrayList<Place> places = savedInstanceState.getParcelableArrayList("places");
            mAdapter = new PlacesAdapter(this, R.layout.place_row, places);
        } else {
            mAdapter = new PlacesAdapter(this, R.layout.place_row, new ArrayList<Place>());
            new LoadPlaces().execute();
        }

        setListAdapter(mAdapter);
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

            DatabaseHelper mOpenHelper = new DatabaseHelper(MainActivity.this);
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();

            ArrayList<Place> list = new ArrayList<Place>();
            ContentValues values = new ContentValues();

            db.beginTransaction();
            for (int i = 0; i < 30; i++) {
                Place p = new Place(i, "Place " + i, "Description " + i);

                values.put(_ID, p.getId());
                values.put(TITLE, p.getTitle());
                values.put(DESCRIPTION, p.getDescription());
                if (db != null)
                    db.insertWithOnConflict("Places", null, values, SQLiteDatabase.CONFLICT_REPLACE);

                list.add(p);
            }

            db.endTransaction();

            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<Place> list) {
            mAdapter.setItems(list);

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("places", mAdapter.getItems());
        super.onSaveInstanceState(outState);
    }

    /**
     * The adapter to fill the list of places
     */
    class PlacesAdapter extends BaseAdapter {

        Context mContext;
        int mResource;
        ArrayList<Place> mPlaces;

        public PlacesAdapter(Context context, int resource, ArrayList<Place> places) {
            this.mContext = context;
            this.mResource = resource;
            this.mPlaces = places;
        }

        public PlacesAdapter setItems(ArrayList<Place> places) {
            mPlaces = places;
            notifyDataSetChanged();
            return this;
        }

        @Override
        public int getCount() {
            return mPlaces.size();
        }

        @Override
        public Place getItem(int position) {
            return mPlaces.get(position);
        }

        public ArrayList<Place> getItems() {
            return mPlaces;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Place place = getItem(position);

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
                holder = new ViewHolder();
                holder.textTitle = ((TextView) convertView.findViewById(R.id.tv_title));
                holder.textDescription = (TextView) convertView.findViewById(R.id.tv_description);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.textTitle.setText(place.getTitle());
            holder.textDescription.setText(place.getDescription());

            return convertView;
        }

        class ViewHolder {
            TextView textTitle, textDescription;
        }
    }

    /**
     * Entity Place with an Id, a Title and a Description
     */
    static class Place implements Parcelable {
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


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.id);
            dest.writeString(this.title);
            dest.writeString(this.description);
        }

        private Place(Parcel in) {
            this.id = in.readLong();
            this.title = in.readString();
            this.description = in.readString();
        }

        public static Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
            public Place createFromParcel(Parcel source) {
                return new Place(source);
            }

            public Place[] newArray(int size) {
                return new Place[size];
            }
        };
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
