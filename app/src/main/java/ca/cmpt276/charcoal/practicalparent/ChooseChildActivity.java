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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.charcoal.practicalparent.model.Child;
import ca.cmpt276.charcoal.practicalparent.model.ChildManager;


public class ChooseChildActivity extends AppCompatActivity {

    ListView listView;
    String ChildNames[] = {"Harjot", "Jason", "Tomas", "Bryan"};
    String Position[] = {"1","2","3","4"};
    int qPortraits[] = {R.drawable.ic_won,R.drawable.ic_lost,R.drawable.ic_won, R.drawable.ic_lost};

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
        // Get the list of users
        ChildManager manager = ChildManager.getInstance();
        List<Child> children = manager.getChildren();
        List<String> childs = new ArrayList<>();
        
        for (Child c : children)
        {
            Toast.makeText(ChooseChildActivity.this, "hi",Toast.LENGTH_SHORT);

        }

//        currentUser = children.get(currentIndex).getName();
//        List<String> childList = RecordsConfig.readNameFromPref(this);
//        int[] range = IntStream.rangeClosed(1,childList.size()).toArray();

        if (children != null)
        {
            listView = findViewById(R.id.queue_listView);
            // create adapter class
            MyAdapter adapter = new MyAdapter(this,ChildNames, Position, qPortraits);

            // set on click listener
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(ChooseChildActivity.this, "You clicked "+position, Toast.LENGTH_SHORT).show();
                }
            });

            listView.setAdapter(adapter);
        }

    }

   class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String rChildName[];
        String rPosition[];
        int rPortrait[];

        MyAdapter (Context c, String childName[], String qPosition[], int imgs[] ) {
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
           portraits.setImageResource(rPortrait[position]);
           childName.setText(rChildName[position]);
           turnNumber.setText(rPosition[position]);
//           Toast.makeText(ChooseChildActivity.this, rChild.get(position).getName(),Toast.LENGTH_SHORT).show();
////           turnNumber.setText(qPosition.get(position));
           return row;

        }

   }


}