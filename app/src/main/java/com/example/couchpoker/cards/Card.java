package com.example.couchpoker.cards;

import com.example.couchpoker.R;

public class Card{

    private String color;
    private String value;
    private int drawableID;

    public Card(String color, String value){
        this.color=color;
        this.value=value;
        drawableID = getDrawableIdFromString(value+color);
    }

    public static int getDrawableIdFromString(String cardString){
        String card = cardString;
        if(cardString.contains("|")){
            card = cardString.substring(0, cardString.indexOf("|")) + cardString.substring(cardString.indexOf("|"));
        }
        switch (card){
            case "2H": return R.drawable.h2;
            case "2D": return R.drawable.d2;
            case "2C": return R.drawable.c2;
            case "2S": return R.drawable.s2;
            case "3H": return R.drawable.h3;
            case "3D": return R.drawable.d3;
            case "3C": return R.drawable.c3;
            case "3S": return R.drawable.s3;
            case "4H": return R.drawable.h4;
            case "4D": return R.drawable.d4;
            case "4C": return R.drawable.c4;
            case "4S": return R.drawable.s4;
            case "5H": return R.drawable.h5;
            case "5D": return R.drawable.d5;
            case "5C": return R.drawable.c5;
            case "5S": return R.drawable.s5;
            case "6H": return R.drawable.h6;
            case "6D": return R.drawable.d6;
            case "6C": return R.drawable.c6;
            case "6S": return R.drawable.s6;
            case "7H": return R.drawable.h7;
            case "7D": return R.drawable.d7;
            case "7C": return R.drawable.c7;
            case "7S": return R.drawable.s7;
            case "8H": return R.drawable.h8;
            case "8D": return R.drawable.d8;
            case "8C": return R.drawable.c8;
            case "8S": return R.drawable.s8;
            case "9H": return R.drawable.h9;
            case "9D": return R.drawable.d9;
            case "9C": return R.drawable.c9;
            case "9S": return R.drawable.s9;
            case "10H": return R.drawable.h10;
            case "10D": return R.drawable.d10;
            case "10C": return R.drawable.c10;
            case "10S": return R.drawable.s10;
            case "JH": return R.drawable.hj;
            case "JD": return R.drawable.dj;
            case "JC": return R.drawable.cj;
            case "JS": return R.drawable.sj;
            case "QH": return R.drawable.hq;
            case "QD": return R.drawable.dq;
            case "QC": return R.drawable.cq;
            case "QS": return R.drawable.sq;
            case "KH": return R.drawable.hk;
            case "KD": return R.drawable.dk;
            case "KC": return R.drawable.ck;
            case "KS": return R.drawable.sk;
            case "AH": return R.drawable.ha;
            case "AD": return R.drawable.da;
            case "AC": return R.drawable.ca;
            case "AS": return R.drawable.sa;
        }
        return R.drawable.red_back;
    }

    public int getDrawableID(){
        return drawableID;
    }

    public String getColor() {
        return color;
    }

    public String getValue() {
        return value;
    }
}
