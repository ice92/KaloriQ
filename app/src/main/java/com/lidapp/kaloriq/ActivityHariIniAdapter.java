package com.lidapp.kaloriq;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lidapp.kaloriq.Common.Common;
import com.lidapp.kaloriq.Model.Konsumsi;
import com.lidapp.kaloriq.ViewHolder.HariIniViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActivityHariIniAdapter extends RecyclerView.Adapter<HariIniViewHolder>{
    private ArrayList<Konsumsi> dataList;
    ViewGroup parent;
    String jumlah = "";
    public ActivityHariIniAdapter(ArrayList<Konsumsi> dataList) {
        this.dataList = dataList;
    }

    @Override
    public HariIniViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent=parent;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_konsumsihariini, parent, false);
        return new HariIniViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HariIniViewHolder holder, final int position) {

        holder.txtNama.setText(dataList.get(position).getNama());
        holder.txtNpm.setVisibility(View.GONE);
        holder.editBt.setVisibility(View.GONE);
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_konsumsitemp=database.getReference(dataList.get(position).getDate());
        Log.d("Isin",dataList.get(position).getDate());
        final AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
        final AlertDialog.Builder builderdel = new AlertDialog.Builder(parent.getContext());
        // Set up the input
        final EditText input = new EditText(parent.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                jumlah = input.getText().toString();
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
                DatabaseReference hopperRef = table_konsumsitemp.child(""+Common.currentUser.getId()+"x"+df.format(currentTime)+"x"+dataList.get(position).getDate());
                Map<String, Object> hopperUpdates = new HashMap<>();
                hopperUpdates.put(dataList.get(position).getIdmakanan(), jumlah+"x"+dataList.get(position).getNama().split(" ")[0]);
                hopperRef.updateChildren(hopperUpdates);
                Toast.makeText(parent.getContext(),dataList.get(position).getNama()+" telah diUpdate, Buka konsumsi hari ini kembali untuk melihat perubahan", Toast.LENGTH_SHORT).show();
                parent.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
            }
        });
        builder.setTitle("Update Jumlah Porsi");

        ///////////////////////////////////////////
        builderdel.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
                DatabaseReference hopperRef = table_konsumsitemp.child(""+Common.currentUser.getId()+"x"+df.format(currentTime));
                Map<String, Object> hopperUpdates = new HashMap<>();
                hopperUpdates.put(dataList.get(position).getIdmakanan(), null);
                hopperRef.child(dataList.get(position).getIdmakanan()).removeValue();
                Toast.makeText(parent.getContext(),dataList.get(position).getNama()+" telah dihapus, Buka "+dataList.get(position).getDate()+" hari ini kembali untuk melihat perubahan", Toast.LENGTH_SHORT).show();

            }
        });
        builderdel.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
            }
        });
        builderdel.setTitle("Hapus data diary");
        holder.delBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builderdel.show();
            }
        });
        holder.editBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public void updateList(ArrayList<Konsumsi> newList){
        dataList=new ArrayList<>();
        dataList.addAll(newList);
        notifyDataSetChanged();
    }
}
