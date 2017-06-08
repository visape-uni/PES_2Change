package pes.twochange.presentation.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import pes.twochange.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView field = (TextView)findViewById(R.id.aboutField);
        field.setText("Project 2Change\n\nDeveloped by:" +
                "\nVictor Sánchez" +
                "\nFèlix Arribas" +
                "\nGuillermo Martínez" +
                "\nAdrián Muñoz" +
                "\nAndrés Insaurralde");
    }
}
