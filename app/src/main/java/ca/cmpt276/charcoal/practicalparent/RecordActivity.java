package ca.cmpt276.charcoal.practicalparent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.charcoal.practicalparent.model.ChildManager;
import ca.cmpt276.charcoal.practicalparent.model.Record;

public class RecordActivity extends AppCompatActivity {
    private Button currentRecords;
    private Button priorRecords;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        setUpIntialButton();
        setUpRecordSelectorButtons();

        setUpListView();
    }

    private void setUpListView() {
        List<String> childList = RecordsConfig.readNameFromPref(this);
        List<String> choices   = RecordsConfig.readChoiceFromPref(this);
        List<String> dateTimes= RecordsConfig.readDateFromPref(this);
        List<Integer> resultImages = RecordsConfig.readImageFromPref(this);

        ListView listView;

        if (childList != null && choices != null && dateTimes != null && resultImages != null )
        {
            listView = findViewById(R.id.recordListView);
            // create adapter class
            MyAdapter adapter = new MyAdapter(this, (ArrayList<String>) childList, (ArrayList<String>) choices,(ArrayList<Integer>) resultImages, (ArrayList<String>) dateTimes);
            listView.setAdapter(adapter);
        }




    }
    class MyAdapter extends ArrayAdapter<String>
    {

        Context context;
        ArrayList<String> rName;
        ArrayList<String> rResults;
        ArrayList<String> rDateTime;
        ArrayList<Integer> rImgs;

        MyAdapter (Context c, ArrayList<String> childName, ArrayList<String> results, ArrayList<Integer> imgs, ArrayList<String> rDateTime) {
            super(c, R.layout.record_row, R.id.childNameTextBox, childName);
            this.context = c;
            this.rName = childName;
            this.rResults = results;
            this.rImgs = imgs;
            this.rDateTime = rDateTime;

        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.record_row, parent, false);
            ImageView images = row.findViewById(R.id.resultImageView);
            TextView whoPicked = row.findViewById(R.id.whoPicked_TextView);
            TextView flipResult = row.findViewById(R.id.flipResult_TextView);
            TextView date = row.findViewById(R.id.dateTimeFlip_TextView);

            // now set our resources on views
            images.setImageResource(rImgs.get(position));
            whoPicked.setText(rName.get(position));
            flipResult.setText(rResults.get(position));
            date.setText(rDateTime.get(position));

            return row;
        }
    }
    private void setUpIntialButton() {
        currentRecords = findViewById(R.id.currentRecordBtn);
        currentRecords.setBackgroundColor(getColor(R.color.unSelectedRecord));
        priorRecords = findViewById(R.id.priorRecordsBtn);
        priorRecords.setBackgroundColor(getColor(R.color.selectedRecord));

    }


    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, RecordActivity.class);
    }
    private void setUpRecordSelectorButtons() {
        currentRecords = findViewById(R.id.currentRecordBtn);
        priorRecords = findViewById(R.id.priorRecordsBtn);
        currentRecords.setOnClickListener(v -> {
            currentRecords.setBackgroundColor(getColor(R.color.selectedRecord));
            showCurrentRecord();
            priorRecords.setBackgroundColor(getColor(R.color.unSelectedRecord));
        });
        priorRecords.setOnClickListener(v -> {
            setUpListView();
            priorRecords.setBackgroundColor(getColor(R.color.selectedRecord));
            currentRecords.setBackgroundColor(getColor(R.color.unSelectedRecord));
        });

    }

    private void showCurrentRecord() {
        int currentIndex = CoinFlipActivity.getCurrentIndex(this);

            List<String> childList = RecordsConfig.readNameFromPref(this);
            List<String> choices = RecordsConfig.readChoiceFromPref(this);
            List<String> dateTimes = RecordsConfig.readDateFromPref(this);
            List<Integer> resultImages = RecordsConfig.readImageFromPref(this);

            ArrayList<String> filteredChildNames = new ArrayList<>();
            ArrayList<String> filteredChoices = new ArrayList<>();
            ArrayList<String> filteredDateTimes = new ArrayList<>();
            ArrayList<Integer> filteredResultImages = new ArrayList<>();

            ChildManager childrenManager = ChildManager.getInstance();
            String currentChild = childrenManager.getChild(currentIndex).getName();


            if (childList != null && choices != null && dateTimes != null && resultImages != null) {
                for (int i = 0; i < childList.size(); i++) {
                    if (childList.get(i).equals(currentChild)) {
                        filteredChildNames.add(0, currentChild);
                        filteredChoices.add(0, choices.get(i));
                        filteredDateTimes.add(0, dateTimes.get(i));
                        filteredResultImages.add(0, resultImages.get(i));
                    }
                }

                ListView listView = findViewById(R.id.recordListView);
                // create adapter class
                MyAdapter adapter = new MyAdapter(this, filteredChildNames, filteredChoices, filteredResultImages, filteredDateTimes);
                listView.setAdapter(adapter);



            }

    }
}