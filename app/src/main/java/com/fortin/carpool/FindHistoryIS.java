package com.fortin.carpool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fortin.carpool.Model.FindHistoryPojo;
import com.fortin.carpool.helper.DBhelper;

import java.util.ArrayList;

/**
 * Created by SWIFT-3 on 24/12/15.
 */
public class FindHistoryIS {

    String TAG="CuisineIUDS";

    private SQLiteDatabase mDb;
    ArrayList<FindHistoryPojo> historylist = new ArrayList<FindHistoryPojo>();

    //Insert Find History Detail
    public void insert(FindHistoryPojo history,Context ctx) {
        ContentValues cv = new ContentValues();
        DBhelper d=new DBhelper(ctx);
        mDb=d.open();

        cv.put("source", history.getSource());
        cv.put("source_lat",history.getSource_lat());
        cv.put("source_long",history.getSource_long());
        cv.put("destination",history.getDestination());
        cv.put("dest_lat",history.getDest_lat());
        cv.put("dest_long", history.getDest_long());

        mDb.insert("tblhistory", null, cv);
    }
    public void droptable(Context ctx)
    {
        DBhelper d=new DBhelper(ctx);
        mDb=d.open();

        mDb.execSQL("DROP TABLE IF EXISTS tblhistory");
        Log.d("delete", "Succefully delete");
    }

    //Check History
    public Boolean check(String source,String dest,Context ctx) throws SQLException
    {
        DBhelper d = new DBhelper(ctx);
        mDb = d.open();
        Boolean result = null;
        Cursor cur = mDb.rawQuery("select count(*) from tblhistory where source='"+source+"' and destination='"+dest+"'", null);

        if(cur.getCount() != 0)
        {
            cur.moveToFirst();

            while (!cur.isAfterLast()) {
                Log.d("FindHistory ", "count:"+cur.getCount() + "");
                if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
                    int cnt=cur.getInt(0);
                    if(cnt>0)
                        result=false;
                    else if(cnt<=0)
                        result=true;
                } else {
                    result=false;
                    Log.d("Data", "No Data Found");
                }
                cur.moveToNext();
            }
        }
        return result;
    }

    //Retrive All History
    public ArrayList<FindHistoryPojo> getHistory(Context ctx) throws SQLException
    {
        DBhelper d = new DBhelper(ctx);
        mDb = d.open();

        Cursor cur = mDb.rawQuery("select * from tblhistory order by _id DESC limit 3", null);

        if(cur.getCount() != 0)
        {
            cur.moveToFirst();

            while (!cur.isAfterLast()) {
                Log.d("FindHistory ", "count:"+cur.getCount() + "");
                if (cur.getCount() > 0 && cur.getColumnCount() > 0) {

                    int id=cur.getInt(cur.getColumnIndex("_id"));
                    String source=cur.getString(cur.getColumnIndex("source"));
                    String slat=cur.getString(cur.getColumnIndex("source_lat"));
                    String slong=cur.getString(cur.getColumnIndex("source_long"));
                    String dest=cur.getString(cur.getColumnIndex("destination"));
                    String dlat=cur.getString(cur.getColumnIndex("dest_lat"));
                    String dlong=cur.getString(cur.getColumnIndex("dest_long"));

                    historylist.add(new FindHistoryPojo(id, source, slat, slong, dest, dlat, dlong));

                } else {
                    Log.d("Data", "No Data Found");
                }
                cur.moveToNext();
            }
        }
        return historylist;
    }

}
