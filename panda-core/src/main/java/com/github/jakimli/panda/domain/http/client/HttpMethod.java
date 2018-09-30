package com.github.jakimli.panda.domain.http.client;

import com.github.jakimli.panda.domain.http.HttpContext;

import java.util.function.Supplier;

public enum HttpMethod {
    GET(HttpMethods::get),
    POST(HttpMethods::post),
    PUT(HttpMethods::put),
    DELETE(HttpMethods::delete);

    private Supplier<Method> method;

    HttpMethod(Supplier<Method> method) {
        this.method = method;
    }

    public void send(HttpContext context) {
        method.get().send(context);
    }

    public interface Method {
        void send(HttpContext context);
    }
}