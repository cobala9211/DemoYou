package com.songskids.network.apis;


import com.songskids.models.Songs;
import com.songskids.network.core.ApiClient;
import com.songskids.network.core.Callback;

import java.util.List;

import retrofit.RetrofitError;

public class AuthApi {
    /**
     * getSongListPage
     */
    public static void getSongListPage(String id, final Callback<List<Songs>> callback) {
        ApiClient.call().getSongListPage(id, new Callback<List<Songs>>() {
            @Override
            public void success(List<Songs> datas) {
                callback.success(datas);
            }

            @Override
            public void failure(RetrofitError error, com.songskids.network.Error myError) {
                callback.failure(error, myError);
            }
        });
    }
}
