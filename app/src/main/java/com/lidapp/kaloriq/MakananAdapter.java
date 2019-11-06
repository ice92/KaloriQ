package com.lidapp.kaloriq;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lidapp.kaloriq.Model.Contact;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MakananAdapter extends RecyclerView.Adapter<MakananAdapter.ViewHolder> {
    private List<Contact> contacts;
    ArrayList<HashMap<String, String>> hasillist;
    private Context context;

    //TODO Step 6: Update the constructor
    public MakananAdapter(Context context, List<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
    }

    //TODO Step 7: Override onCreateViewHolder method
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater
                        .from(context)
                        .inflate(R.layout.makanan_item, parent, false)
        );
    }
    //TODO Step 8: Override onBindViewHolder method
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Contact contact = contacts.get(position);

        holder.nameTextView.setText(contact.getLastName());
        holder.mobileTextView.setText(contact.getLandline());
//        holder.landlineTextView.setText(contact.getLandline());
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("waktu",HomeDetail.EXTRA_TIME);
                intent.putExtra(DetailsActivity.EXTRA_CONTACT, contact);
                context.startActivity(intent);
            }
        });

    }
    //TODO Step 9: Modify `getCount()` method
    @Override
    public int getItemCount() {
        return this.contacts.size();
    }

    //TODO Step 5: Update the ViewHolder
    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTextView;


        private TextView mobileTextView;
//        private TextView landlineTextView;
        private View parentView;
        public ViewHolder(@NonNull View view) {
            super(view);

            this.parentView=view;
            this.nameTextView = (TextView)view
                    .findViewById(R.id.name_text_view);
            this.mobileTextView = (TextView)view
                    .findViewById(R.id.mobile_text_view);
//            this.landlineTextView = (TextView)view
//                    .findViewById(R.id.landline_text_view);

        }
    }
    public void updateList(List <Contact> newList){
        contacts=new ArrayList<>();
        contacts.addAll(newList);
        notifyDataSetChanged();
    }
}
