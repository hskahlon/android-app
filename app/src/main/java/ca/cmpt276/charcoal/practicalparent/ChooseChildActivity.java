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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import ca.cmpt276.charcoal.practicalparent.model.RecordsConfig;


public class ChooseChildActivity extends AppCompatActivity {

    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, ChooseChildActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_child);
        setUpListView();
    }




    /**
     *  Sets up overRide activity, and listview
     */


    private void setUpListView() {
        List<String> childList = RecordsConfig.readNameFromPref(this);
//        int[] range = IntStream.rangeClosed(1,childList.size()).toArray();
        ListView listView;
        if (childList != null)
        {
            listView = findViewById(R.id.queue_listView);
            // create adapter class
            MyAdapter adapter = new MyAdapter(this, (ArrayList<String>) childList);

            listView.setAdapter(adapter);
        }

    }

   class MyAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<String> rName;
        ArrayList<Integer> qPosition;


        MyAdapter (Context c, ArrayList<String> childName) {
            super(c, R.layout.queue_row, R.id.whoWillPick, childName);
            this.context = c;
            this.rName = childName;
//            this.qPosition = queuePosition;
        }

       @NonNull
       @Override
       public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
           LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           View row = layoutInflater.inflate(R.layout.queue_row, parent, false);
//           ImageView portraits = row.findViewById(R.id.childPortrait);
           TextView turnNumber = row.findViewById(R.id.queuePosition_textView);
           TextView childName = row.findViewById(R.id.whoWillPick);


           // Now set our resources on views
//           portraits.setImageResource(findViewById());
           childName.setText(rName.get(position));
//           turnNumber.setText(qPosition.get(position));
           return row;
       }

   }


}