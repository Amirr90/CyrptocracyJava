package com.e.cryptocracy.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.e.cryptocracy.databinding.CoinNewsBinding;
import com.e.cryptocracy.models.TweetModel;


public class TweetAdapter extends ListAdapter<TweetModel, TweetAdapter.TweetVH> {

    public TweetAdapter() {
        super(itemCallback);
    }

    @NonNull
    @Override
    public TweetVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CoinNewsBinding binding = CoinNewsBinding.inflate(layoutInflater, parent, false);
        return new TweetVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TweetVH holder, int position) {
        holder.binding.setTweet(getItem(position));

        holder.binding.textView30.setText(getItem(position).getDate());
    }

    public class TweetVH extends RecyclerView.ViewHolder {
        CoinNewsBinding binding;

        public TweetVH(@NonNull CoinNewsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

  /*  @Override
    public int getItemCount() {
        return Math.min(super.getItemCount(), 5);
    }*/

    public static DiffUtil.ItemCallback<TweetModel> itemCallback = new DiffUtil.ItemCallback<TweetModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull TweetModel oldItem, @NonNull TweetModel newItem) {
            return oldItem.getStatus_id().equals(newItem.getStatus_id());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull TweetModel oldItem, @NonNull TweetModel newItem) {
            return oldItem.equals(newItem);
        }
    };
}
