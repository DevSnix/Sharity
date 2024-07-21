package com.test.sharity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CharityAdapter extends RecyclerView.Adapter<CharityAdapter.CharityViewHolder> {

    private Context context;
    private List<Charity> charityList;

    public CharityAdapter(Context context, List<Charity> charityList) {
        this.context = context;
        this.charityList = charityList;
    }

    @NonNull
    @Override
    public CharityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.charity_item, parent, false);
        return new CharityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CharityViewHolder holder, int position) {
        Charity charity = charityList.get(position);
        holder.textViewCharityName.setText(charity.getCharityName());
        holder.textViewCharityType.setText(charity.getCharityType());
        holder.textViewCharityRating.setText(String.valueOf(charity.getRating()));

        Glide.with(context)
                .load(charity.getImgUrl())
                .into(holder.imageViewCharity);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CharityProfileActivity.class);
            intent.putExtra("licenseNumber", charity.getLicenseNumber());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return charityList.size();
    }

    static class CharityViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewCharity;
        TextView textViewCharityName;
        TextView textViewCharityType;
        TextView textViewCharityRating;

        public CharityViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCharity = itemView.findViewById(R.id.imageViewCharity);
            textViewCharityName = itemView.findViewById(R.id.textViewCharityName);
            textViewCharityType = itemView.findViewById(R.id.textViewCharityType);
            textViewCharityRating = itemView.findViewById(R.id.textViewCharityRating);
        }
    }
}
