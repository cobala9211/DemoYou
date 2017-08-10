package com.songskids.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

import lombok.Data;

@Data
public class Songs implements Serializable{

    @Expose
    private String id;
    @Expose
    private String names;
    @Expose
    private String youtubeid;
}
