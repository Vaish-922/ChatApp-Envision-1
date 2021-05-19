package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;

import com.example.chatapp.user.UserListAdapter;
import com.example.chatapp.user.UserObject;
import com.example.chatapp.util.CountryToPhonePrefix;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FindUserActivity extends AppCompatActivity {

    private RecyclerView mUserList;
    private RecyclerView.Adapter mUserListAdapter;
    private RecyclerView.LayoutManager mUserListLayoutManager;

    ArrayList<UserObject> userList,contactList;


    Button mCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        mCreate=findViewById(R.id.create);
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChat();
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("chatID", key);
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        });

        contactList = new ArrayList<>();
        userList = new ArrayList<>();
        initializeRecyclerview();
        getContactList();
    }
    String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();

    public void createChat(){


        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("user");
        DatabaseReference chatInfoDb = FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("info");


        HashMap newChatMap = new HashMap();
        newChatMap.put("id",key);
        newChatMap.put("users/"+ FirebaseAuth.getInstance().getUid(),true);

        Boolean validChat = false;

        for(UserObject mUser: userList){
            if(mUser.getSelected()){
                validChat=true;
                newChatMap.put("users/"+ mUser.getUid(),true);

                userDb.child((mUser.getUid())).child("chat").child(key).setValue(true);
            }
        }


        if(validChat){
            chatInfoDb.updateChildren(newChatMap);

        userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
        }



    }

    private void getContactList(){
        String ISOPrefix = getCountryIso();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);
        while(phones.moveToNext()){
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phone = phone.replace(" ","");
            phone = phone.replace("-","");
            phone = phone.replace("(","");
            phone = phone.replace(")","");

            if(!String.valueOf(phone.charAt(0)).equals("+"))
                phone = ISOPrefix + phone;

            UserObject mContact = new UserObject("", name, phone);
            contactList.add(mContact);
            getUserDetails(mContact);
        }
    }

    private void getUserDetails(UserObject mContact) {
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user");
        Query query = mUserDB.orderByChild("phone").equalTo(mContact.getPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists()){
                   String phone = "",
                           name = "";
                   for(DataSnapshot childSnapshot: snapshot.getChildren()){
                       if(childSnapshot.child("phone").getValue()!= null)
                           phone = childSnapshot.child("phone").getValue().toString();
                       if(childSnapshot.child("name").getValue()!= null)
                           name = childSnapshot.child("name").getValue().toString();

                       UserObject mUser = new UserObject(childSnapshot.getKey(),name, phone);

                           for(UserObject mContactIterator: contactList){
                               if(mContactIterator.getPhone().equals(mUser.getPhone())){
                                   mUser.setName(mContactIterator.getName());
                               }
                           }
                       Map<String, Object> userMap = new HashMap<>();
                       userMap.put("phone", mUser.getPhone());
                       userMap.put("name", mUser.getName());
                       DatabaseReference mUserUpdate = mUserDB.child(mUser.getUid());
                       System.out.println(mUserUpdate);

                           mUserUpdate.updateChildren((userMap));



                       userList.add(mUser);
                       mUserListAdapter.notifyDataSetChanged();
                   }

               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private String getCountryIso(){
        String iso =null;

        TelephonyManager telephonyManager =(TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if(telephonyManager.getNetworkCountryIso()!=null)
            if(!telephonyManager.getNetworkCountryIso().toString().equals(""))
                iso=telephonyManager.getNetworkCountryIso().toString();
        return CountryToPhonePrefix.getPhone(iso);
    }

    private void initializeRecyclerview() {
       mUserList = findViewById(R.id.userList);
       mUserList.setNestedScrollingEnabled(false);
       mUserList.setHasFixedSize(false);
       mUserListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
       mUserList.setLayoutManager(mUserListLayoutManager);
       mUserListAdapter = new UserListAdapter(userList);
       mUserList.setAdapter(mUserListAdapter);

    }
}