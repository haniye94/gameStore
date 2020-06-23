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

import co.hani.myket.EndlessRecyclerViewScrollListener;
import co.hani.myket.R;
import co.hani.myket.model.GameModel;
import co.hani.myket.model.GameModelResponse;
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
    private RecyclerView recyclerView;
    private GamesAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<GameModel> gameModelList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EndlessRecyclerViewScrollListener scrollListener;
    private RequestInterface requestInterface;
    private GameApi gameApi = new GameApi();
    private int offset = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();
        loadData(0);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                    @Override
                    public void onLoadMore(int offset, int totalItemsCount, RecyclerView view) {
                        loadData(offset);
                    }
                };
                recyclerView.addOnScrollListener(scrollListener);

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
        });

    }

    private void initUi() {
        swipeRefreshLayout = findViewById(R.id.swr);
        recyclerView = findViewById(R.id.game_recycler);
        linearLayoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        gameModelList = new ArrayList<>();
        recyclerView.setLayoutManager(linearLayoutManager);
        myLoading = new MyLoading(this);
        myLoading.showDialog();
        myLoading.setText(getString(R.string.waiting));
        imgIcon = findViewById(R.id.img_icon);
        txtTitle = findViewById(R.id.txt_app_name);
        txtCategory = findViewById(R.id.txt_category);
        txtRating = findViewById(R.id.txt_rate);
    }

    public void loadData(int offset) {

        requestInterface = gameApi.doGetGame();
        requestInterface.getGameList(String.valueOf(offset), "20", "fa").enqueue(new Callback<GameModelResponse>() {
            @Override
            public void onResponse(Call<GameModelResponse> call, Response<GameModelResponse> response) {

                ArrayList<GameModel> newLoadedArrayList;
                ArrayList<GameModel> sortedArrayList = new ArrayList<>();
                newLoadedArrayList = response.body().getAppPlusMetaDataList();


                if (newLoadedArrayList.size() != 0) {
                    gameModelList.addAll(newLoadedArrayList);
                    sortedArrayList.addAll(gameModelList);
                    adapter = new GamesAdapter(MainActivity.this, newLoadedArrayList);
                    myLoading.hideDialog();
                    recyclerView.setAdapter(adapter);
                    adapter.update(gameModelList);
                    setTopGame(adapter.getTopGame(sortedArrayList));
                    swipeRefreshLayout.setRefreshing(false);

                } else
                    Toast.makeText(getBaseContext(), "لیست به پایان رسید!", Toast.LENGTH_LONG).show();


            }

            @Override
            public void onFailure(Call<GameModelResponse> call, Throwable t) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);

    }
}

