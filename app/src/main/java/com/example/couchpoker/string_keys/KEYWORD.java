package com.example.couchpoker.string_keys;

public abstract class KEYWORD {
    public static class FILE{
        public static final String ID = "ID";
        public static final String USERNAME = "USERNAME";
    }
    public static class SERVER_RECEIVED_MESSAGE {
        public static final String STARTED_NEW_ROUND = "STARTED_NEW_ROUND";
        public static final String SENDING_CARDS = "SENDING_CARDS";
        public static final String YOUR_BALANCE = "YOUR_BALANCE";
        public static final String YOUR_BET = "YOUR_BET";
        public static final String CHECK_VALUE = "CHECK_VALUE";
        public static final String BIG_BLIND = "BIG_BLIND";
        public static final String YOUR_FIGURE = "YOUR_FIGURE";
        public static final String WAITING_FOR_YOUR_MOVE = "WAITING_FOR_YOUR_MOVE";
        public static final String EOT = "EOT";
        public static final String DISCONNECT = "DISCONNECT";
    }
}
