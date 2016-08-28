package es.uc3m.manager.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Created by Snapster on 5/2/2016.
 */
public class ContactUtils {

    public static String getContactNumber(String uriContact, ContentResolver contentResolver) {
        if (uriContact == null || uriContact.isEmpty()) {
            return "";
        }
        String contactNumber = null;
        String contactID = null;
        // getting contacts ID
        Cursor cursorID = contentResolver.query(Uri.parse(uriContact),
                new String[]{ContactsContract.Contacts._ID}, null, null, null);
        if (cursorID != null && cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
            cursorID.close();
        }

        Log.d("TAG", "Contact ID: " + contactID);
        // Using the contact ID now we will get contact phone number
        //
        //  Get all phone numbers.
        //
        Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactID, null, null);
        while (phones.moveToNext()) {
            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            switch (type) {
                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                    // do something with the Home number here...
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                    // do something with the Mobile number here...
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                    // do something with the Work number here...
                    break;
            }
        }
        phones.close();
        Log.d("TAG", "Contact Phone Number: " + contactNumber);
        return contactNumber;
    }

    public static String getContactName(String uriContact, ContentResolver contentResolver) {
        if (uriContact == null) {
            return "";
        }
        String contactName = null;
        // querying contact data store
        Cursor cursor = contentResolver.query(Uri.parse(uriContact), null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            cursor.close();
        }
        Log.d("TAG", "Contact Name: " + contactName);
        return contactName;
    }

    private static Cursor getPhone (ContentResolver contentResolver, int phoneType, String contactID){
        return contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        phoneType,
                new String[]{contactID},
                null);
    }
}
