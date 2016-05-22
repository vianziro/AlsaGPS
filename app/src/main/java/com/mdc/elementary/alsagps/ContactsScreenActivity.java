package com.mdc.elementary.alsagps;

/*
   AlsaGPS is a panic button app for Android
   Copyright (C) 2016 Moisés Díaz

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software Foundation,
   Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA

*/

import android.app.Activity;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ContactsScreenActivity extends Activity{

    private ContactList contact_list=null;

    public ContactsScreenActivity(){
        this.contact_list= new ContactList(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.contact_list.loadAllContacts();

        setContentView(R.layout.contacts_screen);

        boolean contact_list_empty =this.contact_list.isContactListEmpty();

        this.visibilityFirstTimeLayout(contact_list_empty);

        this.activateBottomBar();

        if(!contact_list_empty){
            this.activeAllFeatures();
            fillListView();
        }
    }

    private void fillListView() {
        ListView lv;

        List<String> list_names = this.getContactListNames();

        lv = (ListView) findViewById(R.id.list_view_contacts);

        if(list_names != null && lv!= null) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    this,
                    R.layout.list_contacts_remove, R.id.contact_name,
                    list_names);

            lv.setAdapter(arrayAdapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selected_contact = (String) parent.getAdapter().getItem(position);
                    removeContact(selected_contact);
                }
            });
        }
    }

    private ArrayList<String> getContactListNames(){
        ArrayList<String> list_names = new ArrayList<String>();
        HashMap your_array_list = this.contact_list.getAllContacts();

        Iterator it = your_array_list.entrySet().iterator();
        String nameContact = null;

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            try {
                nameContact = (String)pair.getKey();
            }catch (ClassCastException e){
                e.printStackTrace();
            }
            list_names.add(nameContact);

            it.remove();
        }

        return list_names;
    }

    private void activeAllFeatures(){
        LinearLayout lyt_add_contact = (LinearLayout) findViewById(R.id.layout_add_contact_button);


        lyt_add_contact.setOnClickListener(initialScreenHandler);

    }

    private void activateBottomBar(){
        LinearLayout lyt_back_button = (LinearLayout) findViewById(R.id.bottom_bar_back);
        LinearLayout lyt_about = (LinearLayout) findViewById(R.id.bottom_bar_about);

        lyt_back_button.setOnClickListener(initialScreenHandler);
        lyt_about.setOnClickListener(initialScreenHandler);
    }

    private void removeContact(String contact_name){
        this.contact_list.deleteContact(contact_name);
        Log.e("CREATION", "Invalidado listview");
        finish();
        startActivity(getIntent());

    }

    private void visibilityFirstTimeLayout(Boolean contact_list_empty){
        LinearLayout lyt_contacts_first_time = (LinearLayout) findViewById(R.id.layout_contacts_first_time);
        TextView lyt_add_contact_first_time = (TextView) findViewById(R.id.add_contact_button_first_time);

        lyt_add_contact_first_time.setOnClickListener(initialScreenHandler);

        if(contact_list_empty){
            lyt_contacts_first_time.setVisibility(View.VISIBLE);
        }else{
            lyt_contacts_first_time.setVisibility(View.GONE);
        }
    }

    View.OnClickListener initialScreenHandler = new View.OnClickListener(){

        public void onClick(View v) {

            switch(v.getId()) {
                case R.id.bottom_bar_back:
                    Intent intentMainSettings = new Intent(ContactsScreenActivity.this ,SettingsScreenActivity.class);
                    ContactsScreenActivity.this.startActivity(intentMainSettings);
                    break;
                case R.id.bottom_bar_about:
                    Intent intentMainAbout = new Intent(ContactsScreenActivity.this ,AboutScreenActivity.class);
                    ContactsScreenActivity.this.startActivity(intentMainAbout);
                    break;
                case R.id.layout_add_contact_button:
                case R.id.add_contact_button_first_time:
                    Intent intentMainAddContact = new Intent(ContactsScreenActivity.this ,AddContactActivity.class);
                    ContactsScreenActivity.this.startActivity(intentMainAddContact);
                    break;
            }
        }
    };

}
