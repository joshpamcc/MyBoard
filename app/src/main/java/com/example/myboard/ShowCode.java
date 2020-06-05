package com.example.myboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;

public class ShowCode extends AppCompatActivity {

    private ImageButton forward = (ImageButton) findViewById(R.id.NextCode);
    private ImageButton backward = (ImageButton) findViewById(R.id.LastQRcode);
    private ImageView QRcode = (ImageView) findViewById(R.id.QRcode);
    private ArrayList<Bitmap> codelist = new ArrayList<>();
    private int currentCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_code);
        codelist = ExportProblems.codeList;

        if (codelist.size() == 1)
        {
            forward.setVisibility(View.INVISIBLE);
            backward.setVisibility(View.INVISIBLE);
        }

        else
        {
            forward.setVisibility(View.VISIBLE);
            backward.setVisibility(View.VISIBLE);
        }

        QRcode.setImageBitmap(codelist.get(currentCode));
    }

    public void nextCode(View view)
    {
        currentCode++;
        if(currentCode >= codelist.size())
        {
            currentCode = 0;
        }
        QRcode.setImageBitmap(codelist.get(currentCode));
    }

    public void lastCode(View view)
    {
        currentCode--;
        if(currentCode < 0)
        {
            currentCode = (codelist.size()-1);
        }
        QRcode.setImageBitmap(codelist.get(currentCode));
    }

    public void done(View view)
    {
        codelist.clear();
        ExportProblems.codeList.clear();
        Intent intent = new Intent(this, Problems.class);
        startActivity(intent);
    }
}
