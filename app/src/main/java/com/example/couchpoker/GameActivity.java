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

import com.example.couchpoker.networking.TCPDataExchanger;

import java.net.Socket;

public class GameActivity extends AppCompatActivity implements CardsFragment.OnFragmentInteractionListener {

    private Socket connectedSocket;

    private int checkValue=10, totalBallance=980, bigBlindValue=20;
    private int currentBet=10;
    private int minimalValueToRaise=0;
    private int selectedRaiseValue;

    Button check, raise, fold;
    TextView label1, label2, label3;
    TextView figure, currentBetText, ballance;
    View cards;
    ImageView card1;
    ImageView card2;
    SeekBar raiseSelect;

    TCPDataExchanger exchanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        connectedSocket = MainActivity.connectedSocket;

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
        cards = findViewById(R.id.fragment_cards);
        raiseSelect = findViewById(R.id.seekBar_raiseValue);
        check = findViewById(R.id.button_check);
        raise = findViewById(R.id.button_raise);
        fold = findViewById(R.id.button_fold);


        figure.setVisibility(View.INVISIBLE);
        label1.setVisibility(View.INVISIBLE);

        cards.setOnLongClickListener((View v)->{
            onShowCardsLongPress();
            return true;
        });
        cards.setOnTouchListener(new View.OnTouchListener() {
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
                selectedRaiseValue = (progress*10)+minimalValueToRaise;
                raise.setText(new String("raise\n$ "+selectedRaiseValue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        check.setOnClickListener(this::onCheck);
        raise.setOnClickListener(this::onRaise);
        fold.setOnClickListener(this::onFold);

        onMyTurn();
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
        minimalValueToRaise = currentBet+checkValue+bigBlindValue;
        raiseSelect.setMax((totalBallance-checkValue-currentBet)/10);
        raiseSelect.setProgress(minimalValueToRaise/10);

        check.setText("check\n$ "+(checkValue+currentBet));
        raise.setText("raise\n$ "+ minimalValueToRaise);

        currentBetText.setText("$ "+currentBet);
        ballance.setText("$ "+totalBallance);
    }

    private void onDataFromServerReceived(TCPDataExchanger.ReceivedMessageEventArgs args){
        System.out.println("Received from server: "+args.msg);
    }

    private void onShowCardsLongPress(){
        System.out.println("long pressed");
        card1.setImageResource(R.drawable.ha);
        card2.setImageResource(R.drawable.hk);
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
