package com.sanoop.mycontacts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity {
    private ArrayList<ContactModel> contactModelArrayList = new ArrayList<>();
    final private int PERMISSION_REQUEST_CONTACT = 124;
    private RecyclerView mRecyclerView;
    private MyAdapter myAdapter;
    private ProgressBar progressBar;
    private TextView noContactText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        mRecyclerView = findViewById(R.id.my_recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        noContactText = findViewById(R.id.no_contact);
        myAdapter = new MyAdapter(contactModelArrayList, ContactListActivity.this, new RecyclerTouchListener.ItemClickListener() {
            @Override
            public void onPositionClicked(int position) {
                Intent navigationIntent = new Intent(ContactListActivity.this, ContactDetailsActivity.class);
                navigationIntent.putExtra("NAME", contactModelArrayList.get(position).getName());
                navigationIntent.putExtra("IMAGE", contactModelArrayList.get(position).getImage());
                navigationIntent.putExtra("MOBILE", contactModelArrayList.get(position).getNumber());
                navigationIntent.putExtra("EMAIL", contactModelArrayList.get(position).getEmail());
                startActivity(navigationIntent);
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(myAdapter);

        askForContactPermission();

    }

//    private void getContact() {
//        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                null, null, null,
//                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
//        while (phones.moveToNext()) {
//            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//
//            ContactModel contactModel = new ContactModel();
//            contactModel.setName(name);
//            contactModel.setNumber(phoneNumber);
//            contactModelArrayList.add(contactModel);
//        }
//        phones.close();
//        myAdapter.notifyDataSetChanged();
//
//    }

    //    private void getContact() {
//        ContentResolver cr = getContentResolver();
////        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, FILTER, null, ORDER);
//        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ORDER);
//        if (cursor != null && cursor.moveToFirst()) {
//
//            do {
//                // get the contact's information
//                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
//                Integer hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
//
//                // get the user's email address
//                String email = null;
//                Cursor ce = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
//                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
//                if (ce != null && ce.moveToFirst()) {
//                    email = ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//                    ce.close();
//                }
//
//                // get the user's phone number
//                String phone = null;
//                if (hasPhone > 0) {
//                    Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
//                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
//                    if (cp != null && cp.moveToFirst()) {
//                        phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        cp.close();
//                    }
//                }
//
//                // if the user user has an email or phone then add it to contacts
//                if ((!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
//                        && !email.equalsIgnoreCase(name)) || (!TextUtils.isEmpty(phone))) {
//                    ContactModel contactModel = new ContactModel();
//                    contactModel.setName(name);
//                    contactModel.setNumber(phone);
//                    contactModel.setEmail(email);
//                    contactModelArrayList.add(contactModel);
//                }
//
//            } while (cursor.moveToNext());
//
//            cursor.close();
//            myAdapter.notifyDataSetChanged();
//        }
//    }

    private void getContact() {
        progressBar.setVisibility(View.VISIBLE);
        new FetchContacts().execute();
    }

    private class FetchContacts extends AsyncTask<Void, Void, ArrayList<ContactModel>> {

        private final String DISPLAY_NAME = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME;

        private final String FILTER = DISPLAY_NAME + " NOT LIKE '%@%'";

        private final String ORDER = String.format("%1$s COLLATE NOCASE", DISPLAY_NAME);

        @SuppressLint("InlinedApi")
        private final String[] PROJECTION = {
                ContactsContract.Contacts._ID,
                DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
        };

        @Override
        protected ArrayList<ContactModel> doInBackground(Void... params) {
            try {
                ArrayList<ContactModel> contacts = new ArrayList<>();

                ContentResolver cr = getContentResolver();
                Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, FILTER, null, ORDER);
                if (cursor != null && cursor.moveToFirst()) {

                    do {
                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                        Integer hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        String email = null;
                        Cursor ce = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id},
                                null);
                        if (ce != null && ce.moveToFirst()) {
                            email = ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            ce.close();
                        }

                        String phone = null;
                        if (hasPhone > 0) {
                            Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                            if (cp != null && cp.moveToFirst()) {
                                phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                cp.close();
                            }
                        }

                        if ((!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                                && !email.equalsIgnoreCase(name)) || (!TextUtils.isEmpty(phone))) {
                            ContactModel contact = new ContactModel();
                            contact.setName(name);
                            contact.setEmail(email);
                            contact.setNumber(phone);
                            contactModelArrayList.add(contact);
                        }

                    } while (cursor.moveToNext());

                    cursor.close();
                }

                return contacts;
            } catch (Exception ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<ContactModel> contacts) {
            if (contacts != null) {
                myAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            } else {
                noContactText.setVisibility(View.VISIBLE);
            }
        }
    }

    public void askForContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.contact_access_title);
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage(R.string.contact_access_info);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_CONTACTS}
                                    , PERMISSION_REQUEST_CONTACT);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSION_REQUEST_CONTACT);
                }
            } else {
                getContact();
            }
        } else {
            getContact();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CONTACT: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContact();

                } else {
                    askForContactPermission();
                }
                return;
            }
        }
    }

}
