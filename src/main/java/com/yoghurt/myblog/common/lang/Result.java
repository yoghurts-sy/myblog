package com.yoghurt.myblog.common.lang;

import lombok.Data;

@Data
public class Result {
    private String code;
    private String msg;
    private Object data;

    public static Result success(Object data) {
        return success("200", "successful", data);
    }

    public static Result success(String code, String msg, Object data) {
        Result res = new Result();
        res.setCode(code);
        res.setMsg(msg);
        res.setData(data);
        return res;
    }

    public static Result fail(String msg) {
        return fail("400", msg, null);
    }

    public static Result fail(String msg, Object data) {
        return fail("400", msg, data);
    }

    public static Result fail(String code, String msg, Object data) {
        Result res = new Result();
        res.setCode(code);
        res.setMsg(msg);
        res.setData(data);
        return res;
    }
}
