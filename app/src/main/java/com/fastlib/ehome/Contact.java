package com.fastlib.ehome;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 17/7/3.
 */
public class Contact {
    public String name;
    public String address;
    public String phone;
    public String email;
    public String description;

    /**
     * 获取所有联系人,建议增加loading
     * @param context
     * @return
     */
    public static List<Contact> getContacts(Context context){
        List<Contact> list=new ArrayList<>();
        Cursor cursor=context.getContentResolver().query(Uri.parse("content://com.android.contacts/raw_contacts"),
                new String[]{ContactsContract.Profile._ID},
                null,
                null,
                null);
        if(cursor!=null){
            while(cursor.moveToNext()){
                int id=cursor.getInt(cursor.getColumnIndex("_id"));
                Cursor dataCursor=context.getContentResolver().query(Uri.withAppendedPath(Uri.parse("content://com.android.contacts/raw_contacts"), File.separator+id+File.separator+"data")
                        ,null,null,null,null);
                Contact contact=new Contact();
                if(dataCursor!=null){
                    while(dataCursor.moveToNext()){
                        String data=dataCursor.getString(dataCursor.getColumnIndex("data1"));
                        String type=dataCursor.getString(dataCursor.getColumnIndex("mimeType"));
                        switch (type){
                            case ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE:
                                contact.name=data;
                                break;
                            case ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE:
                                contact.address=data;
                                break;
                            case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                                contact.phone=data;
                                break;
                            case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE:
                                contact.email=data;
                                break;
                            case ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE:
                                contact.description=data;
                                break;
                            default:break;
                        }
                    }
                    dataCursor.close();
                }
                list.add(contact);
            }
            cursor.close();
        }
        return list;
    }

    public static void insertContact(Context context,Contact contact){
        ContentResolver cr=context.getContentResolver();
        ContentValues cv=new ContentValues();
        Uri uri=context.getContentResolver().insert(Uri.parse("content://com.android.contacts/raw_contacts"),cv);
        long id= ContentUris.parseId(uri);

        cv.put(ContactsContract.Data.RAW_CONTACT_ID,id);
        cv.put(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        cv.put(ContactsContract.RawContacts.Data.DATA1,contact.phone);
        cr.insert(ContactsContract.Data.CONTENT_URI,cv);
        cv.clear();

        cv.put(ContactsContract.Data.RAW_CONTACT_ID,id);
        cv.put(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);
        cv.put(ContactsContract.RawContacts.Data.DATA1,contact.address);
        cr.insert(ContactsContract.Data.CONTENT_URI,cv);
        cv.clear();

        cv.put(ContactsContract.Data.RAW_CONTACT_ID,id);
        cv.put(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        cv.put(ContactsContract.RawContacts.Data.DATA1,contact.name);
        cr.insert(ContactsContract.Data.CONTENT_URI,cv);
        cv.clear();

        cv.put(ContactsContract.Data.RAW_CONTACT_ID,id);
        cv.put(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
        cv.put(ContactsContract.RawContacts.Data.DATA1,contact.email);
        cr.insert(ContactsContract.Data.CONTENT_URI,cv);
        cv.clear();

        cv.put(ContactsContract.Data.RAW_CONTACT_ID,id);
        cv.put(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE);
        cv.put(ContactsContract.RawContacts.Data.DATA1,contact.description);
        cr.insert(ContactsContract.Data.CONTENT_URI,cv);
    }
}