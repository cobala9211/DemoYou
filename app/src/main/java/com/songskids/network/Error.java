package com.songskids.network;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor(suppressConstructorProperties = true)
@Getter
@Setter
public class Error {
    private int stateCode;
    private String message;
}
