package co.hani.myket.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import co.hani.myket.R;
import co.hani.myket.model.GameModel;
import co.hani.myket.network.RequestInterface;
import co.hani.myket.network.calls.GameApi;

public class SplashActivity extends AppCompatActivity {


    RequestInterface requestInterface;
    GameApi gameApi = new GameApi();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        requestInterface = gameApi.doGetGame();

//        requestInterface.getGameList(String.valueOf()).enqueue(new Callback<List<GameModel>>() {
//            @Override
//            public void onResponse(Call<List<GameModel>> call, Response<List<GameModel>> response) {
//
//                JSONArray jsonArray = new JSONArray(response.body());
//                ParsArrayToModel(jsonArray);
//
//            }
//
//            @Override
//            public void onFailure(Call<List<GameModel>> call, Throwable t) {
//            }
//        });

    }


    private void ParsArrayToModel(JSONArray jsonArray) {

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

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                finish();
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("gameList", gameModelList);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();

            }
        }, 4000);
    }

}
