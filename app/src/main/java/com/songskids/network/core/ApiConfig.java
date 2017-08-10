package com.songskids.network.core;

import android.content.Context;
import android.support.annotation.NonNull;


public class ApiConfig {
    Context mContext;
    String baseUrl;

    public ApiConfig(Builder builder) {
        this.mContext = builder.mContext;
        this.baseUrl = builder.baseUrl;
    }

    public static Builder builder(Context context){
        return new Builder(context);
    }

    /**
     * buider class
     */
    public static class Builder {
        Context mContext;
        String baseUrl;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder baseUrl(@NonNull String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public ApiConfig build() {
            return new ApiConfig(this);
        }
    }

}
