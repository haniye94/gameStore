package co.hani.myket.ui.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import co.hani.myket.EndlessRecyclerViewScrollListener;
import co.hani.myket.R;
import co.hani.myket.model.GameModel;
import co.hani.myket.model.GameResponse;
import co.hani.myket.network.RequestInterface;
import co.hani.myket.network.calls.GameApi;
import co.hani.myket.view.adapter.GamesAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    GamesAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<GameModel> gameModelList;
    SwipeRefreshLayout swipeRefreshLayout;
    EndlessRecyclerViewScrollListener scrollListener;

    RequestInterface requestInterface;
    GameApi gameApi = new GameApi();


    int offset = 0;
    //    private MyLoading myLoading;

    //    ArrayList<> arrayList=new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swr);
        recyclerView = (RecyclerView) findViewById(R.id.game_recycler);
        linearLayoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        gameModelList = new ArrayList<>();
        recyclerView.setLayoutManager(linearLayoutManager);


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

                gameModelList = new ArrayList<GameModel>();
                loadData(offset += 20);
                scrollListener.resetState();
            }
        });
    }

//        Collections.sort(arrayList);


    public void loadData(int offset) {

        requestInterface = gameApi.doGetGame();
        requestInterface.getGameList(String.valueOf(offset), "20", "fa").enqueue(new Callback<GameResponse>() {
            @Override
            public void onResponse(Call<GameResponse> call, Response<GameResponse> response) {

                ArrayList<GameModel> tmpArrayList = new ArrayList<>();
                tmpArrayList = response.body().getAppPlusMetaDataList();
//                tmpArrayList=ParsArrayToModel(jsonArray);
                adapter = new GamesAdapter(MainActivity.this, tmpArrayList);
                recyclerView.setAdapter(adapter);
                adapter.update(tmpArrayList);
                swipeRefreshLayout.setRefreshing(false);

//                Collections.sort(tmpArrayList, new Comparator() {
//                    @Override
//                    public int compare(Object g1, Object g2) {
//                        float r1 = ((GameModel) g1).getRating();
//                        float r2 = ((GameModel) g2).getRating();
//
//                        int retval = Float.compare(f1, f2);
//
//                        if(retval > 0) {
//                            System.out.println("f1 is greater than f2");
//                        } else if(retval < 0) {
//                            System.out.println("f1 is less than f2");
//                        } else {
//                            System.out.println("f1 is equal to f2");
//                        }
//
//
//
//                        // ascending order
//                        return Float.compare(r1,r2);
//                    }
//
//                });
            }

            @Override
            public void onFailure(Call<GameResponse> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Error on request", Toast.LENGTH_LONG).show();

            }
        });

    }


    private ArrayList<GameModel> ParsArrayToModel(JSONArray jsonArray) {

        final ArrayList<GameModel> gameModelList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObjectData = jsonArray.getJSONObject(i);
                GameModel gameModel = new GameModel();
                gameModel.setTitle(jsonObjectData.getString(GameModel.KEY.TITLE));
                gameModel.setCategoryName(jsonObjectData.getString(GameModel.KEY.CATEGORY_NAME));
                gameModel.setRating(jsonObjectData.getInt(GameModel.KEY.RATING));
                gameModel.setIconPath(jsonObjectData.getString(GameModel.KEY.ICON_PATH));
                gameModelList.add(gameModel);

            } catch (Exception ex) {

            }
        }

        return gameModelList;
    }

}

