package com.hisham.portfolio;

public interface Config {
    // Google Project Number
    String GOOGLE_PROJECT_ID = "1009861386315";
    String SERVER_BASE_URL = "192.168.11.6:3000";


    String BASE_URL = "http://"+SERVER_BASE_URL+"/api/v1";
    String GCM_URL = "http://"+SERVER_BASE_URL+"/api/v1/gcm_register";
    String LIB_URL = "http://"+SERVER_BASE_URL+"/api/v1";

}