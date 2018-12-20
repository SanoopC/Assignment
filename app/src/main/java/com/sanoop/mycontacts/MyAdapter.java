package com.sanoop.mycontacts;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private final RecyclerTouchListener.ItemClickListener recyclerViewClickListener;
    private ArrayList<ContactModel> mDataset;
    Context mContext;

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView profilePic;
        private final TextView name, number;
        private final ConstraintLayout container;

        private WeakReference<RecyclerTouchListener.ItemClickListener> listener;

        public MyViewHolder(final View view, RecyclerTouchListener.ItemClickListener clickListener) {
            super(view);
            listener = new WeakReference<>(clickListener);
            container = view.findViewById(R.id.container);
            profilePic = view.findViewById(R.id.profile_pic);
            name = view.findViewById(R.id.name);
            number = view.findViewById(R.id.number);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.get().onPositionClicked(getAdapterPosition());
        }
    }

    public MyAdapter(ArrayList<ContactModel> myDataset, Context context, RecyclerTouchListener.ItemClickListener clickListener) {
        mDataset = myDataset;
        mContext = context;
        recyclerViewClickListener = clickListener;
    }

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);
        return new MyViewHolder(itemView, recyclerViewClickListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position % 2 == 1) {
            holder.container.setBackgroundColor(mContext.getResources().getColor(R.color.recyclerRowColor1));
        } else {
            holder.container.setBackgroundColor(mContext.getResources().getColor(R.color.recyclerRowColor2));
        }
        holder.name.setText(mDataset.get(position).getName());
        holder.number.setText(mDataset.get(position).getNumber());
        Glide.with(mContext)
                .load("")
                .apply(RequestOptions.circleCropTransform())
                .into(holder.profilePic);


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
