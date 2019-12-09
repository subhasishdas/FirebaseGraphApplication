package com.example.firebasegraphapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends AppCompatActivity {

    EditText xValue,yValue;
    Button btn_insert;

    FirebaseDatabase database;
    DatabaseReference reference;

    GraphView graphView;
    LineGraphSeries series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xValue = (EditText) findViewById(R.id.x_value);
        yValue = (EditText) findViewById(R.id.y_value);
        btn_insert = (Button) findViewById(R.id.btn_insert);
        graphView = (GraphView) findViewById(R.id.graph);

        series = new LineGraphSeries();
        graphView.addSeries(series);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("chartTable");

        setListeners();

    }

    private void setListeners() {
        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = reference.push().getKey();

                int x = Integer.parseInt(xValue.getText().toString());
                int y = Integer.parseInt(yValue.getText().toString());

                PointValue pointValue = new PointValue(x, y);

                reference.child(id).setValue(pointValue);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataPoint[] dp = new DataPoint[(int)dataSnapshot.getChildrenCount()];
                int index = 0;

                for(DataSnapshot myDataSnapshot : dataSnapshot.getChildren()){
                    PointValue pointValue = myDataSnapshot.getValue(PointValue.class);
                    dp[index] = new DataPoint(pointValue.getxValue(), pointValue.getyValue());
                    Log.d("X Val",String.valueOf(pointValue.getxValue()));
                    Log.d("Y Val",String.valueOf(pointValue.getyValue()));
                    index++;
                }
                series.resetData(dp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
