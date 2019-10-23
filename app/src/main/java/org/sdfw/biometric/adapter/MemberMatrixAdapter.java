package org.sdfw.biometric.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sdfw.biometric.BR;
import org.sdfw.biometric.R;
import org.sdfw.biometric.adapter.decor.ItemViewHolder;
import org.sdfw.biometric.delegate.BindableAdapter;
import org.sdfw.biometric.delegate.ItemClickListener;
import org.sdfw.biometric.model.MemberLite;

import java.util.ArrayList;
import java.util.List;

public class MemberMatrixAdapter extends RecyclerView.Adapter<ItemViewHolder> implements BindableAdapter<List<MemberLite>> {

    private static final int TYPE_EXISTING = 1;
    private static final int TYPE_NEW = 2;

    private List<MemberLite> mData;
    private ItemClickListener<MemberLite> mListener;

    public MemberMatrixAdapter(ItemClickListener<MemberLite> itemClickListener) {
        mListener = itemClickListener;
        mData = new ArrayList<>();
        setHasStableIds(true);
    }

    @Override
    public void setData(List<MemberLite> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_EXISTING) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false);
            ItemViewHolder viewHolder = new ItemViewHolder(itemView);
            itemView.setOnClickListener(view -> {
                if (viewHolder.getAdapterPosition() != -1) {
                    mListener.onItemClick(getItem(viewHolder.getAdapterPosition()));
                }
            });
            return viewHolder;
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_member, parent, false);
            ItemViewHolder viewHolder = new ItemViewHolder(itemView);
            itemView.setOnClickListener(view -> {
                if (viewHolder.getAdapterPosition() != -1) {
                    mListener.onItemClick(getItem(viewHolder.getAdapterPosition()));
                }
            });
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(ItemViewHolder viewHolder, int position) {
        MemberLite member = getItem(position);
        viewHolder.getBinding().setVariable(BR.member, member);
        viewHolder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isNewMember()) {
            return TYPE_NEW;
        } else {
            return TYPE_EXISTING;
        }
    }

    private MemberLite getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }
}