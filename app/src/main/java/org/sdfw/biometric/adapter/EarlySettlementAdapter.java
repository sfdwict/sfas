package org.sdfw.biometric.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sdfw.biometric.BR;
import org.sdfw.biometric.R;
import org.sdfw.biometric.adapter.decor.ItemViewHolder;
import org.sdfw.biometric.delegate.BindableAdapter;
import org.sdfw.biometric.delegate.ItemClickListener;
import org.sdfw.biometric.model.EarlySettlement;

import java.util.ArrayList;
import java.util.List;


public class EarlySettlementAdapter extends RecyclerView.Adapter<ItemViewHolder> implements BindableAdapter<List<EarlySettlement>> {

    private List<EarlySettlement> list;
    private ItemClickListener<EarlySettlement> mListener;

    public EarlySettlementAdapter(ItemClickListener<EarlySettlement> listener) {
        this.list = new ArrayList<>();
        this.mListener = listener;
    }

    @Override
    public void setData(List<EarlySettlement> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_early_settlement, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(itemView);
        itemView.setOnClickListener(view -> mListener.onItemClick(getItem(viewHolder.getAdapterPosition())));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        EarlySettlement earlySettlement = list.get(i);
        itemViewHolder.getBinding().setVariable(BR.earlySettlement, earlySettlement);
        itemViewHolder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private EarlySettlement getItem(int position) {
        return list.get(position);
    }

}
