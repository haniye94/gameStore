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
import java.util.Collections;
import java.util.Comparator;

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

    public void update(ArrayList<GameModel> list) {
        gameModelArrayList = list;
        notifyDataSetChanged();
    }

    public GameModel getTopGame(ArrayList<GameModel> gameModelArrayList) {
        int tmpGame = 0;
        float topRating;

        Collections.sort(gameModelArrayList, new Comparator<GameModel>() {
            @Override
            public int compare(GameModel lhs, GameModel rhs) {
                return lhs.getRating() > rhs.getRating() ? -1 : (lhs.getRating() < rhs.getRating()) ? 1 : 0;
            }
        });

        for (int i = 0; i < gameModelArrayList.size() - 1; i++) {
            topRating = gameModelArrayList.get(0).getRating();
            if (gameModelArrayList.get(0).getRating() == gameModelArrayList.get(i + 1).getRating()) {
                if (gameModelArrayList.get(i + 1).getRating() >= topRating)
                    tmpGame = i + 1;

            }

        }
        return gameModelArrayList.get(tmpGame);
    }

}