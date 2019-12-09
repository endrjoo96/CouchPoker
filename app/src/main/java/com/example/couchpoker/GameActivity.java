package com.example.couchpoker;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.couchpoker.cards.Card;
import com.example.couchpoker.networking.TCPDataExchanger;
import com.example.couchpoker.string_keys.KEYWORD;

import java.net.Socket;

public class GameActivity extends AppCompatActivity implements CardsFragment.OnFragmentInteractionListener, WaitingFragment.OnFragmentInteractionListener {

    private Socket connectedSocket;

    private int checkValue=0, totalBallance=0, bigBlindValue=0;
    private int currentBet=0;
    private int minimalValueToRaise=0;
    private int selectedRaiseValue;

    Button check, raise, fold;
    TextView label1, label2, label3;
    TextView figure, currentBetText, ballance;
    View fragment_cards, fragment_waiting;
    ImageView card1;
    ImageView card2;
    SeekBar raiseSelect;

    Card[] cards;

    TCPDataExchanger exchanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        connectedSocket = MainActivity.connectedSocket;
        cards = new Card[2];

        exchanger = new TCPDataExchanger(connectedSocket);
        exchanger.ReceivedMessage = this::onDataFromServerReceived;
        exchanger.runReceiver();

        label1 = findViewById(R.id.textView_label1);
        label2 = findViewById(R.id.textView_label2);
        label3 = findViewById(R.id.textView_label3);
        currentBetText = findViewById(R.id.textView_currentBet);
        figure = findViewById(R.id.textView_figure);
        ballance = findViewById(R.id.textView_ballance);
        card1 = findViewById(R.id.imageView_card1);
        card2 = findViewById(R.id.imageView_card2);
        fragment_cards = findViewById(R.id.fragment_cards);
        fragment_waiting = findViewById(R.id.fragment_waiting);
        raiseSelect = findViewById(R.id.seekBar_raiseValue);
        check = findViewById(R.id.button_check);
        raise = findViewById(R.id.button_raise);
        fold = findViewById(R.id.button_fold);


        figure.setVisibility(View.INVISIBLE);
        label1.setVisibility(View.INVISIBLE);

        fragment_cards.setOnLongClickListener((View v)->{
            onShowCardsLongPress();
            return true;
        });
        fragment_cards.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                onShowCardsRelease();
                return false;
                }
            return GameActivity.super.onTouchEvent(event);
            }
        });

        raiseSelect.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedRaiseValue = (progress*10)+currentBet+bigBlindValue;
                raise.setText(new String("raise\n$ "+selectedRaiseValue));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        check.setOnClickListener(this::onCheck);
        raise.setOnClickListener(this::onRaise);
        fold.setOnClickListener(this::onFold);


    }

    private int cardsToReceive=-1;
    private int incrementor=0;
    private void onDataFromServerReceived(TCPDataExchanger.ReceivedMessageEventArgs args){
        String message =args.msg;
        String value="";
        if(message.contains("|")){
            message = args.msg.substring(0, args.msg.indexOf("|"));
            value = args.msg.substring(args.msg.indexOf("|")+1);
        }
        switch (message){
            case KEYWORD.SERVER_RECEIVED_MESSAGE.STARTED_NEW_ROUND:{
                runOnUiThread(()->{
                    fragment_waiting.setVisibility(View.INVISIBLE);
                    figure.setText("NONE");
                });
                break;
            }
            case KEYWORD.SERVER_RECEIVED_MESSAGE.SENDING_CARDS:{
                cardsToReceive=Integer.parseInt(value);
                incrementor=0;
                break;
            }
            case KEYWORD.SERVER_RECEIVED_MESSAGE.YOUR_BALLANCE:{
                totalBallance = Integer.parseInt(value);
                break;
            }
            case KEYWORD.SERVER_RECEIVED_MESSAGE.YOUR_BET:{
                currentBet = Integer.parseInt(value);
                break;
            }
            case KEYWORD.SERVER_RECEIVED_MESSAGE.CHECK_VALUE:{
                checkValue = Integer.parseInt(value);
                break;
            }
            case KEYWORD.SERVER_RECEIVED_MESSAGE.BIG_BLIND:{
                bigBlindValue = Integer.parseInt(value);
                break;
            }
            case KEYWORD.SERVER_RECEIVED_MESSAGE.YOUR_FIGURE:{
                final String val = value;
                runOnUiThread(()->{figure.setText(val);});
                break;
            }
            case KEYWORD.SERVER_RECEIVED_MESSAGE.WAITING_FOR_YOUR_MOVE:{
                runOnUiThread(()->{onMyTurn();});
                break;
            }
            default:{
                if(incrementor<cardsToReceive){
                    cards[incrementor] = new Card(value, message);
                    incrementor++;
                }
            }
        }
    }

    private void onCheck(View v){
        new Thread(()->{exchanger.sendMessage("CHECK");}).start();

    }

    private void onRaise(View v){
        new Thread(()->{exchanger.sendMessage("RAISE"+selectedRaiseValue);}).start();
    }

    private void onFold(View v){
        new Thread(()->{exchanger.sendMessage("FOLD");}).start();
    }

    private void onMyTurn(){
        minimalValueToRaise = checkValue+bigBlindValue;
        raiseSelect.setMax((totalBallance-checkValue-currentBet)/10);
        raiseSelect.setProgress(minimalValueToRaise/10);

        check.setText("check\n$ "+(checkValue));
        raise.setText("raise\n$ "+ minimalValueToRaise);

        currentBetText.setText("$ "+currentBet);
        ballance.setText("$ "+totalBallance);
    }

    private void onShowCardsLongPress(){
        System.out.println("long pressed");
        card1.setImageResource(cards[0].getDrawableID());
        card2.setImageResource(cards[1].getDrawableID());
        figure.setVisibility(View.VISIBLE);
        label1.setVisibility(View.VISIBLE);
    }

    private void onShowCardsRelease(){
        System.out.println("released");
        ImageView card1 = findViewById(R.id.imageView_card1);
        card1.setImageResource(R.drawable.red_back);
        ImageView card2 = findViewById(R.id.imageView_card2);
        card2.setImageResource(R.drawable.red_back);
        figure.setVisibility(View.INVISIBLE);
        label1.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
