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
import android.widget.ToggleButton;

import com.example.couchpoker.cards.Card;
import com.example.couchpoker.networking.TCPDataExchanger;
import com.example.couchpoker.string_keys.KEYWORD;

import java.io.IOException;
import java.net.Socket;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Arrays;

public class GameActivity extends AppCompatActivity implements CardsFragment.OnFragmentInteractionListener, WaitingFragment.OnFragmentInteractionListener {

    private Socket connectedSocket;

    private int checkValue=0, totalBallance=0, bigBlindValue=0;
    private int currentBet=0;
    private int minimalValueToRaise=0;
    private int selectedRaiseValue;
    private boolean inGame=false;
    private boolean foldOnNextTurn=false;

    Button check, raise, fold;
    TextView label1, label2, label3;
    TextView figure, currentBetText, ballance;
    View fragment_cards, fragment_waiting;
    ImageView card1;
    ImageView card2;
    SeekBar raiseSelect;

    ToggleButton tggle_checkfold, tggle_check, tggle_fold, tggle_raise;
    ArrayList<ToggleButton> toggles;

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

        tggle_check = findViewById(R.id.toggleButton_check);
        tggle_checkfold = findViewById(R.id.toggleButton_check_fold);
        tggle_fold = findViewById(R.id.toggleButton_fold);
        tggle_raise= findViewById(R.id.toggleButton_raise);
        toggles = new ArrayList(Arrays.asList(tggle_check, tggle_checkfold, tggle_fold, tggle_raise));

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
            if(inGame) onShowCardsLongPress(); return true; });
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
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { onSeekBarValueChanged(progress); }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {  }
            @Override public void onStopTrackingTouch(SeekBar seekBar) {  }
        });

        for (ToggleButton toggle:toggles) {
            toggle.setOnClickListener(this::onToggle);
        }


        check.setOnClickListener(this::onCheck);
        raise.setOnClickListener(this::onRaise);
        fold.setOnClickListener(this::onFold);


        fragment_waiting.bringToFront();
        fragment_waiting.requestLayout();

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
                    inGame=true;
                    switchAppState(false);
                    fragment_waiting.setVisibility(View.GONE);
                    figure.setText("NONE");
                });
                break;
            }
            case KEYWORD.SERVER_RECEIVED_MESSAGE.SENDING_CARDS:{
                cardsToReceive=Integer.parseInt(value);
                incrementor=0;
                cards = new Card[cardsToReceive];
                break;
            }
            case KEYWORD.SERVER_RECEIVED_MESSAGE.YOUR_BALLANCE:{
                totalBallance = Integer.parseInt(value);
                runOnUiThread(()->{updateUI();});
                break;
            }
            case KEYWORD.SERVER_RECEIVED_MESSAGE.YOUR_BET:{
                currentBet = Integer.parseInt(value);
                runOnUiThread(()->{updateUI();});
                break;
            }
            case KEYWORD.SERVER_RECEIVED_MESSAGE.CHECK_VALUE:{
                if(checkValue<Integer.parseInt(value)){
                    foldOnNextTurn=true;
                    if(Integer.parseInt(value) == selectedRaiseValue &&
                    tggle_raise.isChecked())
                        tggle_check.setChecked(true);
                    else tggle_check.setChecked(false);
                    tggle_raise.setChecked(false);
                }
                else foldOnNextTurn=false;
                checkValue = Integer.parseInt(value);
                runOnUiThread(()->{updateUI();});
                break;
            }
            case KEYWORD.SERVER_RECEIVED_MESSAGE.BIG_BLIND:{
                bigBlindValue = Integer.parseInt(value);
                runOnUiThread(()->{updateUI();});
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
            case KEYWORD.SERVER_RECEIVED_MESSAGE.EOT:{
                runOnUiThread(()->{updateUI();});
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

    private void onToggle(View v){
        for (ToggleButton toggle : toggles) {
            if(toggle != (ToggleButton)v){
                toggle.setChecked(false);
            }
        }
    }

    private void refreshToggles() {
        for (ToggleButton toggle : toggles) {
            toggle.setChecked(toggle.isChecked());
        }
    }

    private void updateUI(){
        minimalValueToRaise = checkValue+bigBlindValue;
        raiseSelect.setMax((totalBallance-checkValue-currentBet)/10);

        check.setText("check\n$ "+(checkValue));
        raise.setText("raise\n$ "+ minimalValueToRaise);
        //selectedRaiseValue = minimalValueToRaise;

        currentBetText.setText("$ "+currentBet);
        ballance.setText("$ "+totalBallance);

        tggle_check.setTextOff(check.getText());
        tggle_check.setTextOn(check.getText());

        tggle_raise.setTextOff(raise.getText());
        tggle_raise.setTextOn(raise.getText());

        refreshToggles();
    }

    private void onCheck(View v){
        new Thread(()->{exchanger.sendMessage("CHECK");}).start();
        totalBallance -= checkValue;
        updateUI();
        switchAppState(false);
    }

    private void onRaise(View v){
        new Thread(()->{exchanger.sendMessage("RAISE"+selectedRaiseValue);}).start();
        totalBallance -= selectedRaiseValue;
        updateUI();
        switchAppState(false);
    }

    private void onFold(View v){
        new Thread(()->{exchanger.sendMessage("FOLD");}).start();
        switchAppState(false);
        for(ToggleButton tggle : toggles){
            tggle.setChecked(false);
            tggle.setVisibility(View.INVISIBLE);
        }
    }

    private void onMyTurn(){
        raiseSelect.setProgress(0);
        updateUI();

        int toggled=-1;
        if((toggled=preActionToggled())==-1) {
            switchAppState(true);
        }
        else {
            executeActionFromToggle(toggled);
        }
    }

    private int preActionToggled(){
        for(int i=0; i<toggles.size();i++){
            if(toggles.get(i).isChecked()) return i;
        }
        return -1;
    }

    private void executeActionFromToggle(int index){
        switch (index){
            case 0:{    // toggle_check
                onCheck(null);
                break;
            }
            case 1:{    // toggle_checkfold
                if(!foldOnNextTurn) onCheck(null);
                else onFold(null);
                foldOnNextTurn=false;
                break;
            }
            case 2:{    // toggle_fold
                onFold(null);
                break;
            }
            case 3:{    // toggle_raise
                onRaise(null);
                break;
            }
        }
        toggles.get(index).setChecked(false);
    }

    private void onSeekBarValueChanged(int progress){
        selectedRaiseValue = (progress*10)+checkValue+bigBlindValue;

        raise.setText("raise\n$ "+ selectedRaiseValue);

        tggle_raise.setTextOff(raise.getText());
        tggle_raise.setTextOn(raise.getText());
        tggle_raise.setEnabled(true);
        refreshToggles();
    }

    private void onShowCardsLongPress(){
        card1.setImageResource(cards[0].getDrawableID());
        card2.setImageResource(cards[1].getDrawableID());
        figure.setVisibility(View.VISIBLE);
        label1.setVisibility(View.VISIBLE);
    }

    private void onShowCardsRelease(){
        ImageView card1 = findViewById(R.id.imageView_card1);
        card1.setImageResource(R.drawable.red_back);
        ImageView card2 = findViewById(R.id.imageView_card2);
        card2.setImageResource(R.drawable.red_back);
        figure.setVisibility(View.INVISIBLE);
        label1.setVisibility(View.INVISIBLE);
    }

    private void switchAppState(boolean isMyTurn){
        runOnUiThread(()->{
            check.setEnabled(isMyTurn);
            raise.setEnabled(isMyTurn);
            fold.setEnabled(isMyTurn);
            int visibility;
            if(isMyTurn){
                visibility = View.INVISIBLE;
            }
            else visibility = View.VISIBLE;
            for(ToggleButton btn : toggles){
                btn.setVisibility(visibility);
            }
            /*tggle_raise.setVisibility(visibility);
            tggle_fold.setVisibility(visibility);
            tggle_checkfold.setVisibility(visibility);
            tggle_check.setVisibility(visibility);*/
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) { }

    @Override
    public void onBackPressed() {
        try {
            connectedSocket.close();
        } catch (IOException ioex){
            ioex.printStackTrace();
        }
        super.onBackPressed();
    }
}
