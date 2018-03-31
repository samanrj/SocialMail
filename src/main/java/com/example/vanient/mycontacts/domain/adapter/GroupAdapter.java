package com.example.vanient.mycontacts.domain.adapter;

import java.util.List;

import com.example.vanient.contacts.R;
import com.example.vanient.mycontacts.domain.entity.Group;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ItemViewHolder> {

    private List<Group> groupList;
    private Context mContext;

    public GroupAdapter(List<Group> groupList, Context mContext) {
        this.groupList = groupList;
        this.mContext = mContext;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_group_view, null);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.tvGroupName.setText(group.getGroupName());
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvGroupName;

        ItemViewHolder(View itemView) {
            super(itemView);
            tvGroupName = (TextView) itemView.findViewById(R.id.tvGroupName);
        }
    }
}