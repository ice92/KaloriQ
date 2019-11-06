package com.lidapp.kaloriq.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lidapp.kaloriq.Interface.ItemClickListener;
import com.lidapp.kaloriq.R;

public class HariIniViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView txtNama, txtNpm, txtNoHp;
    public ImageButton delBt,editBt;
    private ItemClickListener itemClickListener;

    public HariIniViewHolder(View itemView) {
        super(itemView);
        txtNama = (TextView) itemView.findViewById(R.id.txt_nama_mahasiswa);
        txtNpm = (TextView) itemView.findViewById(R.id.txt_npm_mahasiswa);
        delBt=(ImageButton)itemView.findViewById(R.id.delBt);
        editBt=(ImageButton)itemView.findViewById(R.id.editBt);


    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);

    }
}
