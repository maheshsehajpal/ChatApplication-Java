package com.example.chatapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<ChatMessage> {
    public MessageAdapter(Context context, int resource, List<ChatMessage> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.message_item_view, parent, false);
        }

        ImageView photoImageView = convertView.findViewById(R.id.photoImageView);
        TextView messageTextView = convertView.findViewById(R.id.messageTextView);
        TextView authorTextView = convertView.findViewById(R.id.nameTextView);

        ChatMessage message = getItem(position);

        // Messages of the current user will shown on right side
        if (message.getName().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) messageTextView.getLayoutParams();
            params.gravity=Gravity.END;
            messageTextView.setLayoutParams(params);
            messageTextView.setGravity(Gravity.END);
            messageTextView.setTextColor(Color.GRAY);
        }else {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) messageTextView.getLayoutParams();
            params.gravity=Gravity.START;
            messageTextView.setLayoutParams(params);
            messageTextView.setGravity(Gravity.START);
            messageTextView.setTextColor(Color.BLACK);
        }

        boolean isPhoto = message.getPhotoUrl() != null;
        if (isPhoto) {
            messageTextView.setVisibility(View.GONE);
            photoImageView.setVisibility(View.VISIBLE);
            authorTextView.setVisibility(View.VISIBLE);


            Glide.with(photoImageView.getContext())
                    .load(message.getPhotoUrl())
                    .into(photoImageView);

            authorTextView.setText(message.getName());

        } else {
            messageTextView.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.GONE);
            authorTextView.setVisibility(View.GONE);

            messageTextView.setText(message.getText());

        }

        return convertView;
    }
}
