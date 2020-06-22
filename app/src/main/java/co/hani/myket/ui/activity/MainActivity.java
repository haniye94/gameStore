package co.hani.myket.ui.activity;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import co.hani.myket.EndlessRecyclerViewScrollListener;
import co.hani.myket.R;
import co.hani.myket.model.GameModel;
import co.hani.myket.model.GameResponse;
import co.hani.myket.network.RequestInterface;
import co.hani.myket.network.calls.GameApi;
import co.hani.myket.view.MyLoading;
import co.hani.myket.view.adapter.GamesAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {


    private ImageView imgIcon;
    private TextView txtTitle;
    private TextView txtCategory;
    private TextView txtRating;

    private MyLoading myLoading;

    RecyclerView recyclerView;
    GamesAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<GameModel> gameModelList;
    SwipeRefreshLayout swipeRefreshLayout;
    EndlessRecyclerViewScrollListener scrollListener;
    RequestInterface requestInterface;
    GameApi gameApi = new GameApi();
    int offset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swr);
        recyclerView = (RecyclerView) findViewById(R.id.game_recycler);
        linearLayoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        gameModelList = new ArrayList<>();
        recyclerView.setLayoutManager(linearLayoutManager);

        initUi();
        loadData(0);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) { // مقدار دهی اسکرول لیسنتر
            @Override
            public void onLoadMore(int offset, int totalItemsCount, RecyclerView view) {
                loadData(offset); // کارهایی که باید بعد از اسکرول اتفاق بیافتد. در اینجا لود دادهای دیگر می باشد.
            }
        };
        recyclerView.addOnScrollListener(scrollListener); // ست کردن اسکرول لیسنر به ریسایکلر ویو


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gameModelList = new ArrayList<GameModel>();
                        loadData(offset += 20);
                        scrollListener.resetState();
                    }
                }, 3000);

            }
        });
    }


    private void initUi() {
        myLoading = new MyLoading(this);
        myLoading.showDialog();
        imgIcon = findViewById(R.id.img_icon);
        txtTitle = findViewById(R.id.txt_app_name);
        txtCategory = findViewById(R.id.txt_category);
        txtRating = findViewById(R.id.txt_rate);
    }

    public void loadData(int offset) {

        myLoading.hideDialog();
        requestInterface = gameApi.doGetGame();
        requestInterface.getGameList(String.valueOf(offset), "20", "fa").enqueue(new Callback<GameResponse>() {
            @Override
            public void onResponse(Call<GameResponse> call, Response<GameResponse> response) {

                ArrayList<GameModel> tmpArrayList = new ArrayList<>();
                tmpArrayList = response.body().getAppPlusMetaDataList();


                if (tmpArrayList.size() != 0) {
                    gameModelList.addAll(tmpArrayList);
                    adapter = new GamesAdapter(MainActivity.this, tmpArrayList);
                    recyclerView.setAdapter(adapter);
                    adapter.update(gameModelList);
                    setTopGame(getTopGame(gameModelList));
                    swipeRefreshLayout.setRefreshing(false);

                } else
                    Toast.makeText(getBaseContext(), "finish", Toast.LENGTH_LONG).show();


            }

            @Override
            public void onFailure(Call<GameResponse> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Error on request", Toast.LENGTH_LONG).show();

            }
        });

    }


    private void setTopGame(GameModel topGameModel) {
        Picasso.with(this).load(topGameModel.getIconPath()).into(imgIcon);
        txtTitle.setText(topGameModel.getTitle());
        txtCategory.setText(topGameModel.getCategoryName());
        txtRating.setText(String.valueOf(topGameModel.getRating()));
    }

    private GameModel getTopGame(ArrayList<GameModel> gameModelList) {
        int tmpGame = 0;
        float topRating;

        Collections.sort(gameModelList, new Comparator<GameModel>() {
            @Override
            public int compare(GameModel lhs, GameModel rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return lhs.getRating() > rhs.getRating() ? -1 : (lhs.getRating() < rhs.getRating()) ? 1 : 0;
            }
        });

        for (int i = 0; i < gameModelList.size() - 1; i++) {
            topRating = gameModelList.get(0).getRating();
            if (gameModelList.get(0).getRating() == gameModelList.get(i + 1).getRating()) {
                if (gameModelList.get(i + 1).getRating() >= topRating)
                    tmpGame = i + 1;

            }

        }
        return gameModelList.get(tmpGame);
    }

}

