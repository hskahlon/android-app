package ca.cmpt276.charcoal.practicalparent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ca.cmpt276.charcoal.practicalparent.model.Record;


public class CoinFlipActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String PREFS_NAME = "RecordData";
    private Button btn;
    private Button heads;
    private Button tails;
    private String userDecision;
    private ImageView coin;
    private static final int YROTATE = 1800;
    private static final int DURATION = 300;
    private static final float SCALEX = 0.5f;
    private static final float SCALEY = 0.5f;
    Record manager = Record.getInstance();


    /*
            TO DO:
            allow user to select user, and change the user automatically
     */
    private String currentUser = "Steve Jobs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);

        setupCoinButton();

        coin = findViewById(R.id.coinImageView);

        heads = findViewById(R.id.selectHeads);
        tails = findViewById(R.id.priorRecordsBtn);

        // Start both buttons appearing "greyed" out:
        heads.setBackgroundColor(getColor(R.color.unselectedHeadTail));
        tails.setBackgroundColor(getColor(R.color.unselectedHeadTail));

        heads.setOnClickListener(this);
        tails.setOnClickListener(this);

        // Enable "up" on toolbar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectHeads:
                userDecision = "User chose heads!";
                updateHeadTailSelectorButtons(userDecision);
                break;
            case R.id.priorRecordsBtn:
                userDecision = "User chose tails!";
                updateHeadTailSelectorButtons(userDecision);
                break;
        }
    }

    private void updateHeadTailSelectorButtons(String flag) {
        heads = findViewById(R.id.selectHeads);
        tails = findViewById(R.id.priorRecordsBtn);
        if (userDecision == "User chose heads!") {


            heads.setBackgroundColor(getColor(R.color.selectedHeadTail));
            tails.setBackgroundColor(getColor(R.color.unselectedHeadTail));
            Toast.makeText(this, "Heads selected", Toast.LENGTH_SHORT).show();
        } else if (userDecision == "User chose tails!") {

            heads.setBackgroundColor(getColor(R.color.unselectedHeadTail));
            tails.setBackgroundColor(getColor(R.color.selectedHeadTail));
            Toast.makeText(this, "Tails selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupCoinButton() {
        btn = findViewById(R.id.flipBtn);
        btn.setOnClickListener(v -> {
            int randomChoice = getRandom();
            flipCoin(randomChoice);
        });
    }

    private int getRandom() {
        Random randomGenerator = new Random();
        return randomGenerator.nextInt(2);
    }

    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, CoinFlipActivity.class);
    }

    private void flipCoin(int randomChoice) {
        final View currentCoin = coin;
        TextView showWinOrLoss = findViewById(R.id.resultWinOrLoss);

        // Rotates by 1800 degrees -> changes view at last flip
            currentCoin.animate().withLayer()
                    .rotationYBy(YROTATE)
                    .setDuration(DURATION)
                    .scaleXBy(SCALEX)
                    .scaleYBy(SCALEY)
                    .withEndAction(
                            () -> {
                                TextView result = findViewById(R.id.coinFlipResultText);
                                if (randomChoice == 0) {
                                    coin.setImageResource(R.drawable.ic_tails);
                                } else {
                                    coin.setImageResource(R.drawable.ic_heads);
                                }

                                //second quarter turn
                                currentCoin.setRotationY(-YROTATE);
                                currentCoin.animate().withLayer()
                                        .rotationY(0)
                                        .scaleXBy(-SCALEY)
                                        .scaleYBy(-SCALEY)
                                        .setDuration(DURATION)
                                        .start();
                            }
                    ).start();
            new Handler().postDelayed(() -> {
                TextView result = findViewById(R.id.coinFlipResultText);
                if (randomChoice==0) {
                    result.setText(R.string.tailsString);
                    if (userDecision == "User chose tails!") {
                        addRecord(true,"Tails");
                        showWinOrLoss.setText(R.string.winnerResult);
                        showWinOrLoss.setTextColor(getColor(R.color.correct_green));
                    } else if (userDecision == "User chose heads!"){
                        addRecord(false,"Heads");
                        showWinOrLoss.setText(R.string.loserResult);
                        showWinOrLoss.setTextColor(getColor(R.color.incorrect_red));
                    }
                } else {
                    result.setText(R.string.headsString);
                    if (userDecision == "User chose heads!") {
                        addRecord(true,"Heads");
                        showWinOrLoss.setText(R.string.winnerResult);
                        showWinOrLoss.setTextColor(getColor(R.color.correct_green));
                    } else if (userDecision == "User chose tails!"){
                        addRecord(false,"Tails");
                        showWinOrLoss.setText(R.string.loserResult);
                        showWinOrLoss.setTextColor(getColor(R.color.incorrect_red));
                    }
                }
            }, DURATION*2);
    }

    private void addRecord(boolean b, String Choice) {
        manager.addChoice(Choice);
        manager.addUser(currentUser);
        // find current time
        Date currentTime = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String convertedTime = dateFormat.format(currentTime);
        manager.addDateTime(convertedTime);
        manager.addResult(b);

        saveRecord();
    }

    private void saveRecord() {

//        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
        List<String> users = manager.getUsers();
        List<Boolean> result = manager.getResults();
        List<String> choices = manager.getChoices();
        List<String> dateTimes = manager.getDateTimes();
        List<Integer> img = manager.getImages();

        RecordsConfig.writeDateInPref(getApplicationContext(),dateTimes);
        RecordsConfig.writeImageInPref(getApplicationContext(), img);
        RecordsConfig.writeNameInPref(getApplicationContext(),users);
        RecordsConfig.writeChoiceInPref(getApplicationContext(),choices);
        //RecordsConfig.writeResultInPref(getApplicationContext(),result);

//        Gson gson = new Gson();
//        String json = gson.toJson(result);
//        editor.putString("RESULT PREFS", json);
//        editor.apply();
//
//        json = gson.toJson(users);
//        editor.putString("USER PREFS", users.toString());
//        editor.apply();
//
//        json = gson.toJson(choices);
//        editor.putString("CHOICE PREFS", json);
//        editor.apply();
//
//        json = gson.toJson(dateTimes);
//        editor.putString("DATE PREFS", json);
//        editor.apply();
//
//        json = gson.toJson(img);
//        editor.putString("IMG PREFS", json);
//        editor.apply();
    }
    public static List<String> getSavedUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        List<String> users;
        String serializedUsers = prefs.getString("USER_PREFS", null);
        if (serializedUsers != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>(){}.getType();
            users = gson.fromJson(serializedUsers, type);
            return users;
        }
        else {
            return null;
        }
    }

}