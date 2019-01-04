package io.srinathr.labs.buggy.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.srinathr.labs.buggy.pojo.Contact;

public class ContactSyncService extends IntentService {

    public ContactSyncService() {
        super("ContactSyncService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Cursor pCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        Cursor eCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null, null, null, null);
        List<Contact> contacts = new ArrayList<>();
        if(pCursor != null && pCursor.getCount() > 0) {
            while (pCursor.moveToNext()) {
                String id = pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                String name = pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String email = pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DISPLAY_NAME));
                contacts.add(new Contact(id,name,number,email));
            }
        }
        if(eCursor != null && eCursor.getCount() > 0) {
            while (eCursor.moveToNext()) {
                String id = eCursor.getString(eCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email._ID));
                String email = eCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                contacts.add(new Contact(id,"","",email));
            }
        }
        File file = getFile("payLoad", getApplicationContext());
        try (Writer fileWriter = new FileWriter(file)){
            fileWriter.write(new Gson().toJson(contacts));
            Log.i("BUGGY","File successfully written at "+file.getAbsolutePath());
        } catch (IOException e) {
            Log.e("BUGGY","Error writing to file");
        }
        finally {
            stopSelf();
        }
    }

    private File getFile(String fileName, Context context) {
        File file;
        try {
            file = new File(context.getExternalFilesDir(null),fileName);
            file.mkdirs();
        } catch (Exception e) {
            Log.e("BUGGY","Error creating file"+e.getLocalizedMessage());
            file = null;
        }
        return new File(file,"data_"+new Date().getTime()+".bgy");
    }
}
