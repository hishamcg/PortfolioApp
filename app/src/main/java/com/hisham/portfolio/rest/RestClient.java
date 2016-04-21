package com.hisham.portfolio.rest;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hisham.portfolio.Config;
import com.hisham.portfolio.rest.service.RouteService;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class RestClient {
    private static RouteService routeService;
    private static SessionRequestInterceptor sessionInterceptor;

    public static void init(String token) {
        Gson gson = new GsonBuilder().create();
        sessionInterceptor = new SessionRequestInterceptor();


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(Config.BASE_URL)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(sessionInterceptor)
                .build();
        sessionInterceptor.setAuthToken(token);
        routeService = restAdapter.create(RouteService.class);
    }

    public static void setAuth_token(String token){
        sessionInterceptor.setAuthToken(token);

    }
    public static RouteService getRouteService() {

        return routeService;
    }

}
