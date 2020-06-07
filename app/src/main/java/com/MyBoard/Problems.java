package com.MyBoard;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import java.io.FileNotFoundException;
import java.io.InputStream;

public class Problems extends AppCompatActivity
{
    public static ProblemHandle problemHandle;
    public static String ImageURI;
    public static Bitmap image;
    public static int sortingId = 0;
    public static Board loadedBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problems);
        Intent intent = getIntent();

        String filename = intent.getStringExtra("board");
        loadedBoard = Boards.boardHandle.getBoardWithName(filename);

        TextView tv = (TextView) findViewById(R.id.boradname);
        tv.setText(filename);

        problemHandle = new ProblemHandle(filename, this.getApplicationContext());
        ImageURI = problemHandle.getImage();
        loadImage();
        problemHandle.sort(0);
        problemHandle.resetProblemFlags();
        updateListDisplay();
    }

    private void updateListDisplay()
    {
        ListView lv = (ListView) findViewById(R.id.List);
        if (problemHandle.getNumberOfProblems() > 0)
        {
            final String[] names = new String[problemHandle.getNumberOfProblems()];

            for (int index = 0; index < problemHandle.getNumberOfProblems(); index++)
            {
                names[index] = problemHandle.getSortedProblemAt(index).Name;
            }
            ProblemListLayout layout = new ProblemListLayout(this, names);
            lv.setAdapter(layout);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Intent i = new Intent(Problems.this, LoadedProblem.class);
                    String message = names[position];
                    i.putExtra("problem", message);
                    startActivity(i);
                }
            });
        }
    }

    public void AddProblem(View view)
    {
        Intent intent = new Intent(this, AddProblem.class);
        startActivity(intent);
    }

    public void deleteProblem(View view)
    {
        Intent intent = new Intent(this, DeleteProblem.class);
        startActivity(intent);
    }

    public void exportProblems(View view)
    {
        Intent intent = new Intent(this, ExportProblems.class);
        startActivity(intent);
    }

    private void loadImage()
    {
        try
        {
            Uri imageUri = Uri.parse(ImageURI);
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            image = BitmapFactory.decodeStream(imageStream);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public void edit(View view)
    {
        Boards.boardHandle.EditingBoard = loadedBoard;
        Intent intent = new Intent(this, AddBoard.class);
        startActivity(intent);
    }

    public void UpdateSort(View view)
    {
        Button sort = (Button) findViewById(R.id.confirm);
        sortingId++;
        if(sortingId > 3)
        {
            sortingId = 0;
        }
        if (sortingId == 0)
        {
            sort.setText("Names (ASC)");
        }
        if (sortingId == 1)
        {
            sort.setText("Names (DESC)");
        }
        if (sortingId == 2)
        {
            sort.setText("Grade (ASC)");
        }
        if (sortingId == 3)
        {
            sort.setText("Grade (DESC)");
        }
        problemHandle.sort(sortingId);
        updateListDisplay();
    }

    @Override
    public void onBackPressed() {
        Boards.loadedBoard = "";
        super.onBackPressed();
    }

    @Override
    public boolean onNavigateUp() {
        Boards.loadedBoard = "";
        return super.onNavigateUp();
    }
}
