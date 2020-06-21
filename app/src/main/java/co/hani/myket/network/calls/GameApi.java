package co.hani.myket.network.calls;

import co.hani.myket.network.ApiClient;
import co.hani.myket.network.RequestInterface;

public class GameApi {

    public static RequestInterface doGetGame() {
        return ApiClient.getClient().create(RequestInterface.class);
    }

}
