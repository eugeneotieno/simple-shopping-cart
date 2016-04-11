package com.phoenix.shoppingcart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

public class StoreDatabase {

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_PRICE = "price";
    public static final String KEY_STATUS = "status";

    private static final String TAG = "ShopDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "PhoenixShop";
    private static final String SHOP_TABLE = "shop";
    private static final int DATABASE_VERSION = 1;

    private final Context mContext;

    private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + SHOP_TABLE + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_NAME + " TEXT,"+
            KEY_DESCRIPTION + " TEXT," +
            KEY_PRICE + " INTEGER," +
            KEY_STATUS + " TEXT" +");";

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS "+SHOP_TABLE);
            onCreate(db);
        }
    }

    public StoreDatabase(Context context){
        this.mContext = context;
    }

    public StoreDatabase open() throws SQLException {
        mDbHelper = new DatabaseHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        if (mDbHelper != null){
            mDbHelper.close();
        }
    }

    public long createItem(String name, String description, int price, String status) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_DESCRIPTION, description);
        initialValues.put(KEY_PRICE, price);
        initialValues.put(KEY_STATUS, status);

        return mDb.insert(SHOP_TABLE, null, initialValues);
    }

    public boolean deleteAllItems() {

        int doneDelete = 0;
        doneDelete = mDb.delete(SHOP_TABLE, null , null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;
    }

    public int getCartItemsRowCount(int type){
        mDb = mDbHelper.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(mDb, SHOP_TABLE, "status= ? ", new String[]{Integer.toString(type)});
    }

    public boolean addToCart (Integer id, String val){
        mDb = mDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_STATUS, val);
        mDb.update(SHOP_TABLE, contentValues, "_id= ? ", new String[]{Integer.toString(id)});
        mDb.close();
        return true;
    }

    public int getTotalItemsCount() {
        String countQuery = "SELECT  * FROM " + SHOP_TABLE;
        mDb = mDbHelper.getReadableDatabase();
        Cursor cursor = mDb.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public int getAmount() {
        mDb = mDbHelper.getReadableDatabase();
        Cursor cursor = mDb.rawQuery("SELECT SUM(" + KEY_PRICE + ") FROM " + SHOP_TABLE +" WHERE status=1", null);
        int total = 0;
        if(cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        return total;
    }

    public Cursor fetchAllItems(String status) {

        Cursor mCursor = mDb.query(SHOP_TABLE, new String[] {KEY_ID, KEY_NAME, KEY_DESCRIPTION, KEY_PRICE, KEY_STATUS},
                KEY_STATUS + " like '%" + status + "%'",null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public void insertMyShopItems() {
        createItem("San Andreas (Special Edition DVD)", "Think of this movie as every disaster movie rolled into one. I enjoyed San Andreas a lot and they certainly managed to cram almost every survival scenario into one movie.\n" +
                "\n" +
                "When the movie started I thought it would be insanely cheesy with some of the lines Dwayne Johnson was saying, but luckily it got much better. Don't get me wrong, there were certainly cheesy parts (certain romances, and cliches) but when you see the first 5 minutes you'll know what I mean about expecting it to be way cheesier.", 999, "0");
        createItem("Spectre 007 (Blu-ray)", "The movie opens with a terrific action sequence in Mexico, set on the Day of the Dead. Bond is off on a private mission, while MI-6, in the fallout from \"Skyfall\", is facing termination. With help from Moneypenny (Naomie Harris) and Q (Ben Whishaw), Bond will doggedly pursue a trail of clues to a young woman who may have some of his answers. Together, they will find and face an old acquaintance who is the head of a very evil and very powerful organization", 1989, "0");
        createItem("The Martian [Blu-ray]", "Matt Damon's Everyman quality and wry sense of humor makes him the perfect actor to portray Mark Watney, astronaut, in Ridley Scott's \"The Martian,\" written by Drew Goddard, based on the book by Andy Weir. When a violent storm forces the Hermes crew, led by Commander Melissa Lewis (a steely Jessica Chastain), to abort the Ares III mission to Mars, the crew reluctantly leaves without Mark Watney, who was injured and presumed dead. Somehow, Watney survives, and starts keeping a log of his experiences on the Red Planet. He is a realist. It could take years for a rescue team to retrieve him, so he gets down to business: rationing and growing food, making water, repairing damaged equipment, and establishing communication with Mission Control.", 1799, "0");
        createItem("Hitman: Agent 47 [Blu-ray]", "Less talk, more action and finally sexy! That's the new Hitman! While critics beat the movie down with their \"professional\" reviews I applaud it since finally, after a long long time (see Riddick, 2013) there's an action movie which focuses more on the action and less on empty mono/dialogues and talks about who's the bigger baddie and who's gonna rule the world (see Gotham, 2014; Jupiter Ascending, 2015; and many, many others). And so while \"movie experts\" compete to find the most nonsense in the movie I will gladly continue to take delight in this stunning glorification of violence with breathtaking photography and luxurious futuristic shots.", 1799, "0");
        createItem("The Intern (Blu-ray + DVD + ULTRAVIOLET)", "There's something delightfully old-fashioned about Nancy Meyers' The Intern that makes it more entertaining than it has any right to be. It's best exemplified in her lead actor Robert De Niro, one of the kings of old school cool, and the character he plays, Ben, the last of a dying breed of real gentlemen. In Meyers' hands the story of a 70-year-old widower venturing into the fast-paced, energetic tech world is every bit as breezy as one might expect, but she still finds a little room to hit on a few salient points that make it more engaging than her usual trifles.", 2099, "0");
        createItem("Star Wars: The Complete Saga (Episodes I-VI) [Blu-ray]", "Simply sensational!", 8999, "0");
    }
}
