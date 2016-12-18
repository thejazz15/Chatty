package com.thejazz.chatty.util;

public class Endpoints {

    public static String TOKEN_KEY = "?token=";
    public static String BASE_URL = "http://192.168.0.4:8494/api/v1/";
    public static String SIGNUP_URL = BASE_URL+"user/signup";
    public static String SIGNIN_URL = BASE_URL+"user/signin";
    public static String USER_INFO_URL = BASE_URL+"user/show"+TOKEN_KEY;
    public static String CHAT_ROOMS_URL = BASE_URL+"user/chatrooms"+TOKEN_KEY;
    public static String CHAT_ROOM_JOIN_URL = BASE_URL+"user/chatrooms/_ID_/join"+TOKEN_KEY;
    public static String CHAT_ROOM_LEAVE_URL = BASE_URL+"user/chatrooms/_ID_/leave"+TOKEN_KEY;
    public static String MESSAGE_CREATE_URL = BASE_URL+"message/create"+TOKEN_KEY;
    public static String CHAT_ROOM_CREATE_URL = BASE_URL+"chatroom/create"+TOKEN_KEY;
    public static String CHAT_ROOM_INFO_URL = BASE_URL+"chatroom/_ID_"+TOKEN_KEY;
    public static String CHAT_ROOM_SEARCH_URL = BASE_URL+"chatroom/search/_ID_"+TOKEN_KEY;
}
