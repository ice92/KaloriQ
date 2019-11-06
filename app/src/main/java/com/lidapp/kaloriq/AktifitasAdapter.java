package com.lidapp.kaloriq;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lidapp.kaloriq.Model.Aktifitas;
import com.lidapp.kaloriq.Model.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AktifitasAdapter extends RecyclerView.Adapter<AktifitasAdapter.ViewHolder> {
    private List<Aktifitas> contacts;
    ArrayList<HashMap<String, String>> hasillist;
    private Context context;

    //TODO Step 6: Update the constructor
    public AktifitasAdapter(Context context, List<Aktifitas> contacts) {
        this.context = context;
        this.contacts = contacts;
    }

    //TODO Step 7: Override onCreateViewHolder method
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater
                        .from(context)
                        .inflate(R.layout.contact_item, parent, false)
        );
    }
    //TODO Step 8: Override onBindViewHolder method
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Aktifitas contact = contacts.get(position);

        holder.nameTextView.setText(contact.getNama());
        holder.landlineTextView.setText(contact.getDurasi());
        holder.mobileTextView.setText(contact.getKalori());
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailsActivityAct.class);
                intent.putExtra(DetailsActivityAct.EXTRA_CONTACT, contact);
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
        private TextView landlineTextView;        ;
        private View parentView;
        public ViewHolder(@NonNull View view) {
            super(view);
            this.parentView=view;
            this.nameTextView = (TextView)view
                    .findViewById(R.id.name_text_view);
            this.mobileTextView = (TextView)view
                    .findViewById(R.id.mobile_text_view);
            this.landlineTextView = (TextView)view
                    .findViewById(R.id.landline_text_view);


        }
    }
    public void updateList(List <Aktifitas> newList){
        contacts=new ArrayList<>();
        contacts.addAll(newList);
        notifyDataSetChanged();
    }
}
