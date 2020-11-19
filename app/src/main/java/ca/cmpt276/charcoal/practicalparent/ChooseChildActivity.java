package ca.cmpt276.charcoal.practicalparent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import ca.cmpt276.charcoal.practicalparent.model.Child;
import ca.cmpt276.charcoal.practicalparent.model.ChildManager;

import static ca.cmpt276.charcoal.practicalparent.CoinFlipActivity.getCurrentIndex;
/**
 *  Sets up overRide activity, and listview for selection
 */

public class ChooseChildActivity extends AppCompatActivity {
    Button saveChoice;
    int currentIndex;
    int newIndex;
    ListView listView;


    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, ChooseChildActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_child);



        setUpListView();

        setupChooseButton();
    }


    private void setupChooseButton() {
        saveChoice = findViewById(R.id.skipQueue_button);
        saveChoice.setOnClickListener(v -> {

            Intent returnIntent = CoinFlipActivity.makeLaunchIntent(this);
            returnIntent.putExtra("newIndex",newIndex);

            if (newIndex == currentIndex)
            {
                setResult(Activity.RESULT_CANCELED,returnIntent);
            }
            else
            {
                setResult(Activity.RESULT_OK,returnIntent);
            }

            finish();
        });
    }


    private void setUpListView() {

        // Get the list of users
        ChildManager manager = ChildManager.getInstance();

        // Lists to be populated for queue
        List<Child> children = manager.getChildren();
        List<String> childs = new ArrayList<>();
        List<String> Position = new ArrayList<>();
        List<Integer> qPortraits = new ArrayList<>();

        currentIndex  = getCurrentIndex(this);

        int[] range = IntStream.rangeClosed(0,children.size()-1).toArray();


        // Create range equivalent to the number of children
        for (int i: range)
        {
            Position.add(""+(i+1));

            // TEMP ADD CHECKMARK TO PHOTOS
            qPortraits.add(R.drawable.ic_won);

            childs.add(manager.getChild(currentIndex).getName());

            if (currentIndex < range.length-1)
            {
                currentIndex++;
            }
            else
            {
                currentIndex = 0;
            }


        }


        if (children != null)
        {
            listView = findViewById(R.id.queue_listView);
            // create adapter class
            MyAdapter adapter = new MyAdapter(this, (ArrayList<String>) childs, (ArrayList<String>) Position, (ArrayList<Integer>) qPortraits);

            // set on click listener
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    view.setSelected(true);
                    setNewIndex(children,childs.get(position));


                }
            });

            listView.setAdapter(adapter);
        }

    }

    private void setNewIndex(List<Child> children, String s) {

        for (int i=0; i < children.size(); i++)
        {
            if (children.get(i).getName()==s)
            {
                newIndex = i;
            }
        }


    }


    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<String> rChildName;
        ArrayList<String> rPosition;
        ArrayList<Integer> rPortrait;


        MyAdapter (Context c, ArrayList<String> childName, ArrayList<String> qPosition, ArrayList<Integer> imgs) {
            super(c, R.layout.queue_row, R.id.childNameQueue_TextView, childName);
            this.context = c;
            this.rChildName = childName;
            this.rPosition = qPosition;
            this.rPortrait = imgs;

        }

       @NonNull
       @Override
       public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
           LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

           View row = layoutInflater.inflate(R.layout.queue_row, parent, false);
           ImageView portraits = row.findViewById(R.id.childPortrait_ImageView);
           TextView turnNumber = row.findViewById(R.id.queuePosition_textView);
           TextView childName = row.findViewById(R.id.childNameQueue_TextView);


           // Now set our resources on views
           portraits.setImageResource(rPortrait.get(position));
           childName.setText(rChildName.get(position));
           turnNumber.setText(rPosition.get(position));

           return row;

        }

   }


}