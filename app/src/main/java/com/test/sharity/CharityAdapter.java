package com.test.sharity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CharityAdapter extends RecyclerView.Adapter<CharityAdapter.CharityViewHolder> {
    private List<Charity> charityList;

    public CharityAdapter(List<Charity> charityList) {
        this.charityList = charityList;
    }

    @Override
    public CharityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_charity, parent, false);
        return new CharityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CharityViewHolder holder, int position) {
        Charity charity = charityList.get(position);
        holder.textViewTitle.setText(charity.getCharityName());
        holder.textViewDescription.setText(charity.getCharityDescription());
        holder.textViewRating.setText(String.valueOf(charity.getRating()));
        Picasso.get().load(charity.getImgUrl()).into(holder.imageViewCharity);
    }

    @Override
    public int getItemCount() {
        return charityList.size();
    }

    public static class CharityViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCharity;
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewRating;

        public CharityViewHolder(View itemView) {
            super(itemView);
            imageViewCharity = itemView.findViewById(R.id.imageViewCharity);
            textViewTitle = itemView.findViewById(R.id.textViewName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
        }
    }
}


