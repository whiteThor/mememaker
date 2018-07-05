package com.teamtreehouse.mememaker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.teamtreehouse.mememaker.models.Meme;
import com.teamtreehouse.mememaker.models.MemeAnnotation;

import java.util.ArrayList;

public class MemeDataSource {

    private Context mContext;
    private  MemeSQLiteHelper mMemeSQLiteHelper;

    public MemeDataSource(Context context) {

        mContext = context;
        mMemeSQLiteHelper = new MemeSQLiteHelper(context);
        SQLiteDatabase database = mMemeSQLiteHelper.getReadableDatabase();
        database.close();

    }

    private  SQLiteDatabase open(){
        return mMemeSQLiteHelper.getWritableDatabase();
    }

    private void close(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.close();
    }

    public ArrayList<Meme> read(){
        ArrayList<Meme> memes = readMemes();
        addMemeAnnotations(memes);
        return memes;
    }
    public ArrayList<Meme> readMemes(){
        SQLiteDatabase database = open();

        Cursor cursor = database.query(MemeSQLiteHelper.MEMES_TABLE,
                new String[] {MemeSQLiteHelper.COLUMN_MEME_NAME, BaseColumns._ID, MemeSQLiteHelper.COLUMN_MEME_ASSET},
                null,
                null,
                null,
                null,
                null);

        ArrayList<Meme> memes = new ArrayList<Meme>();
        if(cursor.moveToFirst()){
            do{
                Meme meme = new Meme(getIntFromColumnName(cursor, BaseColumns._ID),
                                     getStringFromColumnName(cursor, MemeSQLiteHelper.COLUMN_MEME_ASSET) ,
                                     getStringFromColumnName(cursor, MemeSQLiteHelper.COLUMN_MEME_NAME) ,
                                    null);
                memes.add(meme);
            }while(cursor.moveToNext());
        }

        cursor.close();
        close(database);

        return memes;
    }

    public void  addMemeAnnotations(ArrayList<Meme> memes){
        SQLiteDatabase database = open();

        for (Meme meme : memes) {
                ArrayList<MemeAnnotation> memeAnnotations = new ArrayList<MemeAnnotation>();
                Cursor cursor = database.rawQuery("SELECT * FROM " + MemeSQLiteHelper.ANNOTATIONS_TABLE + " WHERE MEME_ID = " + meme.getId(),null);
                if(cursor.moveToFirst()){
                    do {
                        MemeAnnotation memeAnnotation = new MemeAnnotation(
                                getIntFromColumnName(cursor, BaseColumns._ID),
                                getStringFromColumnName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_COLOR),
                                getStringFromColumnName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_TITLE),
                                getIntFromColumnName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_X),
                                getIntFromColumnName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_Y));
                        memeAnnotations.add(memeAnnotation);

                    }while(cursor.moveToNext());
                }
                meme.setAnnotations(memeAnnotations);
                cursor.close();
        }
        database.close();
    }

    public void update(Meme meme){
        SQLiteDatabase database = open();
        database.beginTransaction();
        ContentValues updateValue = new ContentValues();
        updateValue.put(MemeSQLiteHelper.COLUMN_MEME_NAME, meme.getName());
        database.update(MemeSQLiteHelper.MEMES_TABLE, updateValue, String.format("%s=%d", BaseColumns._ID, meme.getId()),null);

        for(MemeAnnotation memeAnnotation : meme.getAnnotations()){
            ContentValues updateAnnotations = new ContentValues();
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_ANNOTATION_TITLE, memeAnnotation.getTitle());
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_ANNOTATION_X, memeAnnotation.getLocationX());
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_ANNOTATION_Y, memeAnnotation.getLocationY());
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME, meme.getId());
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_ANNOTATION_COLOR, memeAnnotation.getColor());

            if(memeAnnotation.hasBeenSaved()){
                database.update(MemeSQLiteHelper.ANNOTATIONS_TABLE,updateAnnotations, String.format("%s=%d", BaseColumns._ID, memeAnnotation.getId()),null);
            }else{
                database.insert(MemeSQLiteHelper.ANNOTATIONS_TABLE,null,updateAnnotations);
            }
        }

        database.setTransactionSuccessful();
        close(database);

    }
    private int getIntFromColumnName(Cursor cursor, String columnName){
        int columnIndex = cursor.getColumnIndex(columnName);
        return  cursor.getInt(columnIndex);
    }

    private String getStringFromColumnName(Cursor cursor, String columnName){
        int columnIndex = cursor.getColumnIndex(columnName);
        return  cursor.getString(columnIndex);
    }

    public void create (Meme meme){
        SQLiteDatabase database = open();
        database.beginTransaction();

        ContentValues memeValues = new ContentValues();
        memeValues.put(MemeSQLiteHelper.COLUMN_MEME_NAME, meme.getName());
        memeValues.put(MemeSQLiteHelper.COLUMN_MEME_ASSET, meme.getAssetLocation());
        long memeID = database.insert(MemeSQLiteHelper.MEMES_TABLE, null, memeValues);

        for (MemeAnnotation  memeAnnotation: meme.getAnnotations()) {
            ContentValues annotationValues = new ContentValues();
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_COLOR,memeAnnotation.getColor() );
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_TITLE, memeAnnotation.getTitle());
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_X,memeAnnotation.getLocationX());
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_Y,memeAnnotation.getLocationY());
            annotationValues.put(MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME, memeID);

            database.insert(MemeSQLiteHelper.ANNOTATIONS_TABLE, null, annotationValues);
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);

    }

}













