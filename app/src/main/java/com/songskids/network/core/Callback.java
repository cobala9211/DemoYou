package com.songskids.network.core;
import com.songskids.network.Error;
import retrofit.RetrofitError;
import retrofit.client.Response;


public abstract class Callback<T> implements retrofit.Callback<T> {

    private static final String TAG = Callback.class.getSimpleName();

    public Callback() {
    }

    public abstract void success(T t);

    public abstract void failure(RetrofitError error, Error myError);

    @Override
    public void success(T t, Response response) {
        success(t);
    }

    public void failure(RetrofitError error) {
        if (error.getResponse() != null) {
//            BaseModel baseModel = (BaseModel) error.getBodyAs(BaseModel.class);
//            failure(error, new Error(baseModel.getMeta().getCode(), baseModel.getMeta().getMessages()));
        }
        failure(error, new Error(0, ""));
    }

}