package com.songskids.network;


import com.songskids.models.Songs;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface Api {
    @GET("/getSongListPage/494527/{id}/15")
    void getSongListPage(@Path("id") String id, Callback<List<Songs>> response);
}
