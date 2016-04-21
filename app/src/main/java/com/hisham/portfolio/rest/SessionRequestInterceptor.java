package com.hisham.portfolio.rest;

import retrofit.RequestInterceptor;

class SessionRequestInterceptor implements RequestInterceptor
{
    private String authToken = "";
    public void setAuthToken(String auth){
        this.authToken = auth;
    }

    @Override
    public void intercept(RequestFacade request)
    {
        if(!this.authToken.isEmpty()){
            request.addHeader("auth-token", this.authToken);
        }

    }
}
