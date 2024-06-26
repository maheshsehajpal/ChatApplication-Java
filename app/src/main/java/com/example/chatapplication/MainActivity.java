package com.example.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    public static final String PHONE_NUMBER = "phone_number";
    private static final int RC_SIGN_IN = 1;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase userDatabase;
    DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance();
        userReference = userDatabase.getReference().child("user");

        Button joinChatButton = findViewById(R.id.join_chat_button);
        joinChatButton.setOnClickListener(view -> {

            // Getting phone number from phoneNumberEditText
            EditText phoneNumberEditText = findViewById(R.id.phone_number_edit_text);
            String phoneNumber = "+91" + phoneNumberEditText.getText().toString();

            // checking weather user with phone number exist or not
            userReference.child(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists())
                        // user with phone number not exist
                        Toast.makeText(MainActivity.this, "User Not found", Toast.LENGTH_SHORT).show();
                    else {
                        // user with phone number exist
                        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                        intent.putExtra(PHONE_NUMBER, phoneNumber);

                        // starting chat activity
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        // To check user is currently signed in or not
        firebaseAuth.addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                // User not signed in
                // launching sign in UI
                Intent signInIntent =
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(Collections.singletonList(
                                        new AuthUI.IdpConfig.PhoneBuilder().build()))
                                .build();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    /**
     * To check user has signed in successfully or not
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // user signed in
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // checking user is new user or existing user
                if (user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()) {
                    // storing phone number into database
                    userReference.child(user.getPhoneNumber()).setValue(user.getPhoneNumber());
                }
            } else {
                Toast.makeText(this, "Sign in cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sign_out_menu) {
            AuthUI.getInstance().signOut(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}