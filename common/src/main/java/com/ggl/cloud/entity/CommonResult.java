package com.ggl.cloud.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonResult implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int SUCCESS=200;
    public static final int ERROR=404;
    public static final int UNKNOWN=500;
    private int code;
    private String detail;
    private Object result;
}