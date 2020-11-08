package ca.cmpt276.charcoal.practicalparent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ca.cmpt276.charcoal.practicalparent.model.Child;
import ca.cmpt276.charcoal.practicalparent.model.ChildManager;
import ca.cmpt276.charcoal.practicalparent.model.Record;


public class CoinFlipActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String PREFS_NAME = "RecordData";
    public static final int TAILS = 0;
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
    int currentIndex;
    String currentUser = "";

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

        // Choose user if users are entered
        chooseUser();
    }


    public void chooseUser() {

        currentIndex = manager.getIndex();

        if (childrenExist())
        {
            // get the list of users
            ChildManager manager = ChildManager.getInstance();
            List<Child> children = manager.getChildren();


            if (currentIndex < children.size() && children.size()!=0)
            {

                currentUser = children.get(currentIndex).getName();
            }
            else if (currentIndex == children.size()  && children.size()!=0)
            {
                currentIndex = 0;
                currentUser = children.get(currentIndex).getName();
            }

            setUserText();

        }


    }

    private void setUserText() {
        if (childrenExist())
        {
            // set the textview for current User
            TextView current = findViewById(R.id.userToChoose_TextView);
            current.setText(currentUser+getString(R.string.chooses));

        }
    }


    private boolean childrenExist() {

        ChildManager manager = ChildManager.getInstance();

        List<Child> children = manager.getChildren();

        if (children.size()!=0)
        {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectHeads:
                userDecision = getString(R.string.userChooseHeads);
                updateHeadTailSelectorButtons(userDecision);
                break;
            case R.id.priorRecordsBtn:
                userDecision = getString(R.string.userChooseTails);
                updateHeadTailSelectorButtons(userDecision);
                break;
        }
    }

    private void updateHeadTailSelectorButtons(String flag) {
        heads = findViewById(R.id.selectHeads);
        tails = findViewById(R.id.priorRecordsBtn);
        if (userDecision == getString(R.string.userChooseHeads)) {

            heads.setBackgroundColor(getColor(R.color.selectedHeadTail));
            tails.setBackgroundColor(getColor(R.color.unselectedHeadTail));
            Toast.makeText(this, "Heads selected", Toast.LENGTH_SHORT).show();
        } else if (userDecision == getString(R.string.userChooseTails)) {

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

    @Override
    protected void onResume() {

        super.onResume();

    }

    private void flipCoin(int randomChoice) {
        playSound();
        final View currentCoin = coin;

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


                // set the user who is choosing as last user for next turn
                manager.setIndex(currentIndex+1);
                chooseUser();

                if (randomChoice == TAILS) {
                    result.setText(R.string.tailsString);
                    if (userDecision == getString(R.string.userChooseTails)) {
                        setResultText(getString(R.string.tailsOutcome),getString(R.string.tailsChoice));
                    } else if (userDecision == getString(R.string.userChooseHeads)){
                        setResultText(getString(R.string.tailsOutcome),getString(R.string.headsOutcome));
                    }
                } else {
                    result.setText(R.string.headsString);
                    if (userDecision == getString(R.string.userChooseHeads)) {
                        setResultText(getString(R.string.headsOutcome),getString(R.string.headsOutcome));
                    } else if (userDecision == getString(R.string.userChooseTails)){
                        setResultText(getString(R.string.headsOutcome),getString(R.string.tailsOutcome));
                    }
                }
            }, DURATION*2);


    }

    // Sets the TextViews on Result
    private void setResultText(String outcome, String choice) {


        TextView result = findViewById(R.id.coinFlipResultText);
        if (outcome == getString(R.string.tailsOutcome))
        {
            result.setText(R.string.tailsString);
        }
        else
        {
            result.setText(R.string.headsString);
        }

        TextView showWinOrLoss = findViewById(R.id.resultWinOrLoss);
        if (outcome == choice)
        {
            addRecord(true,choice);
            showWinOrLoss.setText(R.string.winnerResult);
            showWinOrLoss.setTextColor(getColor(R.color.correct_green));
        }
        else
        {
            addRecord(false,choice);
            showWinOrLoss.setText(R.string.loserResult);
            showWinOrLoss.setTextColor(getColor(R.color.incorrect_red));
        }

    }

    private void playSound() {
        final MediaPlayer coinFlip = MediaPlayer.create(this, R.raw.coinflip);
        coinFlip.start();
    }

    private void addRecord(boolean b, String Choice) {

        if (currentUser != "")
        {
            manager.addChoice(Choice);
            manager.addUser(currentUser);
            Date currentTime = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            String convertedTime = dateFormat.format(currentTime);
            manager.addDateTime(convertedTime);
            manager.addResult(b);
            saveRecord();
        }

    }

    private void saveRecord() {

        List<String> users = manager.getUsers();
        List<Boolean> result = manager.getResults();
        List<String> choices = manager.getChoices();
        List<String> dateTimes = manager.getDateTimes();
        List<Integer> img = manager.getImages();

        RecordsConfig.writeDateInPref(getApplicationContext(),dateTimes);
        RecordsConfig.writeImageInPref(getApplicationContext(), img);
        RecordsConfig.writeNameInPref(getApplicationContext(),users);
        RecordsConfig.writeChoiceInPref(getApplicationContext(),choices);

    }


}