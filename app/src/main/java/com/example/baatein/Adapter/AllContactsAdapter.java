package com.example.baatein.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baatein.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.example.baatein.Model.UserObject;

public class AllContactsAdapter extends RecyclerView.Adapter<AllContactsAdapter.ViewHolder> {
    ArrayList<UserObject> contactList;
    Context context;
    public AllContactsAdapter(Context context,ArrayList<UserObject> contactList){
        this.contactList=contactList;
        this.context=context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.users_recycler,null,false);
        RecyclerView.LayoutParams lp=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        ViewHolder vh=new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(contactList.get(position).getName());
        holder.phone.setText(contactList.get(position).getPhone());

    }



    @Override
    public int getItemCount() {


        return contactList.size();
    }
    public void filterlist(ArrayList<UserObject> searchlist){

        contactList=searchlist;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name,phone;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.nametextview);
            phone=itemView.findViewById(R.id.numberTextView);

        }
    }
}
