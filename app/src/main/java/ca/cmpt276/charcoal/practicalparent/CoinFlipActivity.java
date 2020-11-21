package ca.cmpt276.charcoal.practicalparent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ca.cmpt276.charcoal.practicalparent.model.Child;
import ca.cmpt276.charcoal.practicalparent.model.ChildManager;
import ca.cmpt276.charcoal.practicalparent.model.Record;
import ca.cmpt276.charcoal.practicalparent.model.RecordsConfig;

/**
 *  Sets up coin flip activity and allows for saving and recalling current child to flip
 */
public class CoinFlipActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String PREFS_NAME = "CoinFlipData";
    public static final String USER_INDEX = "CurrentUser";
    public static final int TAILS = 0;
    public static final int HEADS = 1;
    private Button changeChild;
    private Button flipBtn;
    private Button selectNobody;
    private Button heads;
    private Button tails;
    private String userDecision;
    private ImageView coin;
    private static final int YROTATE = 1800;
    private static final int DURATION = 300;
    private static final float SCALEX = 0.5f;
    private static final float SCALEY = 0.5f;
    private final Record manager = Record.getInstance();
    private int currentIndex;
    private String currentUser = "";
    private Boolean chooseNobody = false;
    int OVERRIDE_CHILD = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERRIDE_CHILD) {
            if (resultCode == Activity.RESULT_OK) {
                currentIndex = data.getIntExtra("newIndex",currentIndex);
                setCurrentIndex(currentIndex);
                chooseUser();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);

        setupChangeChildButton();
        setupCoinButton();

        coin = findViewById(R.id.image_coin);

        heads = findViewById(R.id.button_select_heads);
        tails = findViewById(R.id.button_prior_records);

        // Start both buttons appearing "greyed" out:
        heads.setBackgroundColor(getColor(R.color.unselected_head_tail));
        tails.setBackgroundColor(getColor(R.color.unselected_head_tail));

        heads.setOnClickListener(this);
        tails.setOnClickListener(this);

        // Enable "up" on toolbar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // Choose user if users are entered
        chooseUser();

        setupChooseNobodyButton();

    }

    private void setupChooseNobodyButton() {
        selectNobody = findViewById(R.id.button_nobody_picks);
        selectNobody.setOnClickListener(v -> {

            TextView current = findViewById(R.id.text_user_to_choose);
            current.setText("");
            chooseNobody = true;
            chooseUser();

        });
    }


    private void setupChangeChildButton() {
        changeChild = findViewById(R.id.button_change_child);
        changeChild.setOnClickListener(v -> {
            Intent i = ChooseChildActivity.makeLaunchIntent(CoinFlipActivity.this);
            startActivityForResult(i,OVERRIDE_CHILD);
        });
    }

    public void chooseUser() {
        currentIndex = getCurrentIndex(this);

        if (childrenExist()) {
            heads.setVisibility(View.VISIBLE);
            tails.setVisibility(View.VISIBLE);

            // Get the list of users
            ChildManager manager = ChildManager.getInstance();
            List<Child> children = manager.getChildren();

            if (chooseNobody)
            {
                currentIndex--;
                currentUser = "";
                chooseNobody = false;

            }
            else
            {
                currentUser = children.get(currentIndex).getName();

                setUserText();
            }


        }
        else
        {
            heads.setVisibility(View.INVISIBLE);
            tails.setVisibility(View.INVISIBLE);
        }


    }

    public static int getCurrentIndex(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(USER_INDEX, 0);
    }

    private void setCurrentIndex(int i) {
        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        ChildManager manager = ChildManager.getInstance();
        List<Child> children = manager.getChildren();

        if (i >= children.size()) {
            i = 0;
        }

        editor.putInt(USER_INDEX,i);
        editor.apply();
    }

    private void setUserText() {
        if (childrenExist()) {
            // Set the textview for current User
            TextView current = findViewById(R.id.text_user_to_choose);
            current.setText(currentUser+getString(R.string.chooses));
        }

    }

    private boolean childrenExist() {
        ChildManager manager = ChildManager.getInstance();
        List<Child> children = manager.getChildren();
        return children.size() != 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_select_heads:
                userDecision = getString(R.string.userChooseHeads);
                updateHeadTailSelectorButtons();
                break;
            case R.id.button_prior_records:
                userDecision = getString(R.string.userChooseTails);
                updateHeadTailSelectorButtons();
                break;
        }
    }

    private void updateHeadTailSelectorButtons() {
        heads = findViewById(R.id.button_select_heads);
        tails = findViewById(R.id.button_prior_records);
        if (userDecision.equals(getString(R.string.userChooseHeads))) {
            heads.setBackgroundColor(getColor(R.color.selected_head_tail));
            tails.setBackgroundColor(getColor(R.color.unselected_head_tail));
        } else if (userDecision.equals(getString(R.string.userChooseTails))) {
            heads.setBackgroundColor(getColor(R.color.unselected_head_tail));
            tails.setBackgroundColor(getColor(R.color.selected_head_tail));
        }
    }

    private void setupCoinButton() {
        flipBtn = findViewById(R.id.button_flip);
        flipBtn.setOnClickListener(v -> {
            int randomChoice = getRandom();
            flipCoin(randomChoice);
            flipBtn.setVisibility(View.INVISIBLE);
            new Handler().postDelayed(() -> flipBtn.setVisibility(View.VISIBLE), DURATION*2);
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
                            if (randomChoice == 0) {
                                coin.setImageResource(R.drawable.ic_tails);
                            } else {
                                coin.setImageResource(R.drawable.ic_heads);
                            }

                            // Second quarter turn
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
                // set the user who is choosing as last user for next turn
                resetButtons();

                if (randomChoice == TAILS) {
                    if (userDecision == null) {
                        setResultText(getString(R.string.tailsString), null);
                    } else if (userDecision.equals(getString(R.string.userChooseTails))) {
                        setResultText(getString(R.string.tailsString),getString(R.string.tailsString));
                    } else if (userDecision.equals(getString(R.string.userChooseHeads))) {
                        setResultText(getString(R.string.tailsString),getString(R.string.headsString));
                    }
                } else if (randomChoice == HEADS){
                    if (userDecision == null) {
                        setResultText(getString(R.string.headsString), null);
                    } else if (userDecision.equals(getString(R.string.userChooseHeads))) {
                        setResultText(getString(R.string.headsString),getString(R.string.headsString));
                    } else if (userDecision.equals(getString(R.string.userChooseTails))) {
                        setResultText(getString(R.string.headsString),getString(R.string.tailsString));
                    }
                }
                userDecision = null;
                setCurrentIndex(currentIndex+1);
                chooseUser();
            }, DURATION*2);
    }

    private void resetButtons() {
        heads.setBackgroundColor(getColor(R.color.unselected_head_tail));
        tails.setBackgroundColor(getColor(R.color.unselected_head_tail));
    }

    private void setResultText(String outcome, String choice) {
        TextView result = findViewById(R.id.text_coinflip_result);
        if (outcome.equals(getString(R.string.tailsString))) {
            result.setText(getString(R.string.tailsString));
        } else {
            result.setText(getString(R.string.headsString));
        }

        TextView showWinOrLoss = findViewById(R.id.text_result_win_or_loss);
        if (choice == null) {
            showWinOrLoss.setText(R.string.noResult);
        } else if (outcome.equals(choice)) {
            addRecord(true,choice);
            showWinOrLoss.setText(getString(R.string.winnerResult));
            showWinOrLoss.setTextColor(getColor(R.color.correct_green));
        } else {
            addRecord(false,choice);
            showWinOrLoss.setText(getString(R.string.loserResult));
            showWinOrLoss.setTextColor(getColor(R.color.incorrect_red));
        }
    }

    private void playSound() {
        final MediaPlayer coinFlip = MediaPlayer.create(this, R.raw.coinflip);
        coinFlip.start();
    }

    private void addRecord(boolean b, String Choice) {
        if (!currentUser.equals("")) {
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
        List<String> choices = manager.getChoices();
        List<String> dateTimes = manager.getDateTimes();
        List<Integer> img = manager.getImages();

        RecordsConfig.writeDateInPref(getApplicationContext(),dateTimes);
        RecordsConfig.writeImageInPref(getApplicationContext(), img);
        RecordsConfig.writeNameInPref(getApplicationContext(),users);
        RecordsConfig.writeChoiceInPref(getApplicationContext(),choices);
    }
}