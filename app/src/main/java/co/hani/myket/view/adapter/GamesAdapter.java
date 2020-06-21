package co.hani.myket.view.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import co.hani.myket.R;
import co.hani.myket.model.GameModel;

public class GamesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<GameModel> gameModelArrayList;

    public GamesAdapter(Context context, ArrayList<GameModel> gameModelArrayList) {

        this.context = context;
        this.gameModelArrayList = gameModelArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recycle_game, parent, false);
        return new GamesViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        GamesViewHolder viewholder = (GamesViewHolder) holder;
        viewholder.onBind();

    }


    @Override
    public int getItemCount() {
        return gameModelArrayList.size();

    }


    public class GamesViewHolder extends RecyclerView.ViewHolder {

        ImageView imgIcon;
        TextView txtTitle;
        TextView txtCategory;
        TextView txtRate;

        public GamesViewHolder(View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.img_icon);
            txtTitle = itemView.findViewById(R.id.txt_title);
            txtCategory = itemView.findViewById(R.id.txt_category);
            txtRate = itemView.findViewById(R.id.txt_rate);

        }

        void onBind() {

            Picasso.with(context).load(gameModelArrayList.get(getAdapterPosition()).getIconPath()).into(imgIcon);
            txtTitle.setText(gameModelArrayList.get(getAdapterPosition()).getTitle());
            txtCategory.setText(gameModelArrayList.get(getAdapterPosition()).getCategoryName());
            txtRate.setText(String.valueOf(gameModelArrayList.get(getAdapterPosition()).getRating()));

        }

    }
    public void update(ArrayList<GameModel> list) { // تابعی اضافی که خودمان برای تغییر داده های اداپتر ساخته ایم.
        gameModelArrayList = list; // مقدارهای جدید را به اداپتر ست می کند.
        notifyDataSetChanged(); // به اداپتر می گوید داده ها تغییر یافته است.
    }

}