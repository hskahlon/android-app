package ca.cmpt276.charcoal.practicalparent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
        Record recManager = Record.getInstance();
        // get the childrens
        ChildManager manager = ChildManager.getInstance();
//        List<String> childList = recManager.getUsers();
//        List<String> choices = recManager.getChoices();
//        List<String> dateTimes = recManager.getDateTimes();
//        List<Integer> resultImages = recManager.getImages();

        List<String> childList = RecordsConfig.readNameFromPref(this);
        List<String> choices   = RecordsConfig.readChoiceFromPref(this);
        List<String> dateTimes= RecordsConfig.readDateFromPref(this);
        List<Integer> resultImages = RecordsConfig.readImageFromPref(this);

        ListView listView;

        listView = findViewById(R.id.recordListView);
        // create adapter class
        MyAdapter adapter = new MyAdapter(this, (ArrayList<String>) childList, (ArrayList<String>) choices,(ArrayList<Integer>) resultImages, (ArrayList<String>) dateTimes);
        listView.setAdapter(adapter);


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
        currentRecords.setBackgroundColor(getColor(R.color.selectedRecord));
        // create view

    }


    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, RecordActivity.class);
    }
    private void setUpRecordSelectorButtons() {
        currentRecords = findViewById(R.id.currentRecordBtn);
        priorRecords = findViewById(R.id.priorRecordsBtn);
        currentRecords.setOnClickListener(v -> {
            currentRecords.setBackgroundColor(getColor(R.color.selectedRecord));
            priorRecords.setBackgroundColor(getColor(R.color.unSelectedRecord));
        });
        priorRecords.setOnClickListener(v -> {
            priorRecords.setBackgroundColor(getColor(R.color.selectedRecord));
            currentRecords.setBackgroundColor(getColor(R.color.unSelectedRecord));
        });

    }
}