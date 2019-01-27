package com.example.arbaelbarca.projectolxsederhana.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arbaelbarca.projectolxsederhana.Model.ItemJual;
import com.example.arbaelbarca.projectolxsederhana.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

public class AdapterHomeIklan extends RecyclerView.Adapter<AdapterHomeIklan.ViewHolder> {
    Context context;
    List<ItemJual> jualList;
    onAction action;
    FirebaseUser user;
    public AdapterHomeIklan(Context context, List<ItemJual> jualList) {
        this.context = context;
        this.jualList = jualList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_homeiklna,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ItemJual itemJual = jualList.get(position);

        Locale locale = new Locale("in", "ID");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        int getRupiah = Integer.parseInt(itemJual.getTxtHarga());

        holder.txtTitle.setText(itemJual.getTxtTitle());
        holder.txtHarga.setText(numberFormat.format(getRupiah));
        holder.txtNamePenjua.setText(itemJual.getNameProfile());

        Glide.with(context)
                .load(itemJual.getImageUpload())
                .into(holder.imgIklan);


        Glide.with(context)
                .load(itemJual.getImageProfile())
                .into(holder.imgPenjual);


        holder.llClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action.onActionClik(v, position);
            }
        });



    }

    @Override
    public int getItemCount() {
        return jualList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle ,txtNamePenjua,txtHarga;
        ImageView imgIklan, imgPenjual;
        LinearLayout llClick, llUpdate;
        public ViewHolder(View itemView) {
            super(itemView);

            llUpdate = itemView.findViewById(R.id.linearUpdatedelte);
            llClick = itemView.findViewById(R.id.linearIklanSendiir);
            imgPenjual = itemView.findViewById(R.id.imgNamePenjual);
            txtNamePenjua = itemView.findViewById(R.id.namaJual);
            txtHarga = itemView.findViewById(R.id.hargajual);
            txtTitle = itemView.findViewById(R.id.titleIklan);
            imgIklan = itemView.findViewById(R.id.imgIklan);
        }
    }

    public interface onAction{
        void onActionClik(View view, int position);
    }

    public void ActionClick(onAction onAction){
        action = onAction;
    }
}
