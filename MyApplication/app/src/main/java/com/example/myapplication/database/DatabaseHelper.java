package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.model.BloodBank;
import com.example.myapplication.model.Donor;

import java.util.ArrayList;
import java.util.List;

/**
 * Blood Connect - SQLite database (MCA Lab demo).
 * Database: bloodbank.db, version 1
 * Tables: donors, blood_banks
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "bloodbank.db";
    public static final int DATABASE_VERSION = 1;

    // donors table
    public static final String TABLE_DONORS = "donors";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_AGE = "age";
    public static final String COL_BLOOD_GROUP = "blood_group";
    public static final String COL_PHONE = "phone";
    public static final String COL_CITY = "city";
    public static final String COL_AVAILABLE = "available";

    // blood_banks table
    public static final String TABLE_BLOOD_BANKS = "blood_banks";
    public static final String COL_BB_NAME = "name";
    public static final String COL_BB_ADDRESS = "address";
    public static final String COL_BB_PHONE = "phone";
    public static final String COL_BB_LAT = "latitude";
    public static final String COL_BB_LNG = "longitude";

    private static final String CREATE_DONORS =
            "CREATE TABLE " + TABLE_DONORS + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_NAME + " TEXT NOT NULL, "
                    + COL_AGE + " INTEGER NOT NULL, "
                    + COL_BLOOD_GROUP + " TEXT NOT NULL, "
                    + COL_PHONE + " TEXT NOT NULL, "
                    + COL_CITY + " TEXT NOT NULL, "
                    + COL_AVAILABLE + " INTEGER NOT NULL DEFAULT 1)";

    private static final String CREATE_BLOOD_BANKS =
            "CREATE TABLE " + TABLE_BLOOD_BANKS + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_BB_NAME + " TEXT NOT NULL, "
                    + COL_BB_ADDRESS + " TEXT NOT NULL, "
                    + COL_BB_PHONE + " TEXT NOT NULL, "
                    + COL_BB_LAT + " REAL, "
                    + COL_BB_LNG + " REAL)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DONORS);
        db.execSQL(CREATE_BLOOD_BANKS);
        insertSampleBloodBanks(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Version 1 only - no destructive migration needed.
        // Future versions: use ALTER TABLE here (do NOT blindly DROP).
    }

    // ---------- DONORS ----------

    /** Insert a donor. Returns row id or -1 on error. */
    public long insertDonor(String name, int age, String bloodGroup,
                            String phone, String city, boolean available) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, name);
        cv.put(COL_AGE, age);
        cv.put(COL_BLOOD_GROUP, bloodGroup);
        cv.put(COL_PHONE, phone);
        cv.put(COL_CITY, city);
        cv.put(COL_AVAILABLE, available ? 1 : 0);
        return db.insert(TABLE_DONORS, null, cv);
    }

    /** Get available donors matching blood group. */
    public List<Donor> getDonorsByBloodGroup(String bloodGroup) {
        List<Donor> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_DONORS, null,
                    COL_BLOOD_GROUP + "=? AND " + COL_AVAILABLE + "=1",
                    new String[]{bloodGroup}, null, null, COL_NAME + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(cursorToDonor(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public List<Donor> getAllDonors() {
        List<Donor> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_DONORS, null, null, null, null, null,
                    COL_NAME + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(cursorToDonor(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    private Donor cursorToDonor(Cursor c) {
        Donor d = new Donor();
        d.setId(c.getLong(c.getColumnIndexOrThrow(COL_ID)));
        d.setName(c.getString(c.getColumnIndexOrThrow(COL_NAME)));
        d.setAge(c.getInt(c.getColumnIndexOrThrow(COL_AGE)));
        d.setBloodGroup(c.getString(c.getColumnIndexOrThrow(COL_BLOOD_GROUP)));
        d.setPhone(c.getString(c.getColumnIndexOrThrow(COL_PHONE)));
        d.setCity(c.getString(c.getColumnIndexOrThrow(COL_CITY)));
        d.setAvailable(c.getInt(c.getColumnIndexOrThrow(COL_AVAILABLE)) == 1);
        return d;
    }

    // ---------- BLOOD BANKS ----------

    public List<BloodBank> getAllBloodBanks() {
        List<BloodBank> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_BLOOD_BANKS, null, null, null,
                    null, null, COL_BB_NAME + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    BloodBank b = new BloodBank();
                    b.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
                    b.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_BB_NAME)));
                    b.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(COL_BB_ADDRESS)));
                    b.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COL_BB_PHONE)));
                    b.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_BB_LAT)));
                    b.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_BB_LNG)));
                    list.add(b);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        // Safety net: if table empty (e.g. pre-existing db), seed samples once.
        if (list.isEmpty()) {
            SQLiteDatabase wdb = getWritableDatabase();
            insertSampleBloodBanks(wdb);
            return getAllBloodBanksGuarded(wdb);
        }
        return list;
    }

    private List<BloodBank> getAllBloodBanksGuarded(SQLiteDatabase db) {
        List<BloodBank> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_BLOOD_BANKS, null, null, null,
                    null, null, COL_BB_NAME + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    BloodBank b = new BloodBank();
                    b.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
                    b.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_BB_NAME)));
                    b.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(COL_BB_ADDRESS)));
                    b.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COL_BB_PHONE)));
                    b.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_BB_LAT)));
                    b.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_BB_LNG)));
                    list.add(b);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public BloodBank getBloodBank(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_BLOOD_BANKS, null, COL_ID + "=?",
                    new String[]{String.valueOf(id)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                BloodBank b = new BloodBank();
                b.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
                b.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_BB_NAME)));
                b.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(COL_BB_ADDRESS)));
                b.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COL_BB_PHONE)));
                b.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_BB_LAT)));
                b.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_BB_LNG)));
                return b;
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    /** Insert sample blood banks (only if table is empty - no duplicates). */
    private void insertSampleBloodBanks(SQLiteDatabase db) {
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_BLOOD_BANKS, null);
            if (c != null && c.moveToFirst() && c.getInt(0) > 0) return;
        } finally {
            if (c != null) c.close();
        }
        insertBloodBank(db, "Municipal Blood Bank",
                "123 MG Road, Near Civil Hospital", "02225410010",
                19.0760, 72.8777);           // Mumbai
        insertBloodBank(db, "Red Cross Blood Bank",
                "Red Cross Bhawan, Sansad Marg, New Delhi", "01123359379",
                28.6176, 77.2033);           // Delhi
        insertBloodBank(db, "City Blood Centre",
                "45 FC Road, Shivajinagar, Pune", "02025531234",
                18.5314, 73.8446);           // Pune
        insertBloodBank(db, "Government Blood Bank",
                "GH Campus, Koti, Hyderabad", "04024740245",
                17.3850, 78.4867);           // Hyderabad
    }

    private void insertBloodBank(SQLiteDatabase db, String name, String address,
                                 String phone, double lat, double lng) {
        ContentValues cv = new ContentValues();
        cv.put(COL_BB_NAME, name);
        cv.put(COL_BB_ADDRESS, address);
        cv.put(COL_BB_PHONE, phone);
        cv.put(COL_BB_LAT, lat);
        cv.put(COL_BB_LNG, lng);
        db.insert(TABLE_BLOOD_BANKS, null, cv);
    }
}
