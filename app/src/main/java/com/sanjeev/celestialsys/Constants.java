package com.sanjeev.celestialsys;

import java.util.regex.Pattern;

public interface Constants {

    //String BASE_URL = "http://chandigarhbrush.com:8080/celestialsys";
    String BASE_URL = "http://192.168.1.4:8080/celestialsys";
    String LOGIN_URL = BASE_URL + "/login";
    String FACEBOOK_LOGIN_URL = BASE_URL + "/login/fb";
    String USERS_URL = BASE_URL + "/users/";

    Pattern EMAIL_ADDRESS = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
}
