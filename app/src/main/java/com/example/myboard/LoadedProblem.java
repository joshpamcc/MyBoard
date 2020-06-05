package com.example.myboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class LoadedProblem extends AppCompatActivity {

    public static Problem lp;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loaded_problem);
        Intent intent = getIntent();
        String problemName = intent.getStringExtra("problem");
        ZoomableImageView Image = (ZoomableImageView) findViewById((R.id.bi));
        TextView name = (TextView) findViewById(R.id.problemName);
        Image.setImageBitmap(Problems.image);
        ImageButton fav = (ImageButton) findViewById(R.id.isFav);

        Problems.problemHandle.LoadProblem(problemName);
        lp = Problems.problemHandle.loadedProblem;
        displayHolds();
        name.setText(problemName);
        setGrade();
        if (lp.IsFavourate)
        {
            fav.setImageResource(R.drawable.button_star_on);
        }
        else
        {
            fav.setImageResource(R.drawable.button_star_off);
        }
        Button completed = (Button) findViewById(R.id.problemCompleted);
        if (lp.IsComplete)
        {
            completed.setBackgroundColor(0xE621BC28);
        }
        else
        {
            completed.setBackgroundColor(0xFFFF0000);
        }
    }

    private void updateFav()
    {
        ImageButton FavButton = (ImageButton) findViewById(R.id.isFav);
        if(!lp.IsFavourate)
        {
            FavButton.setImageResource(R.drawable.button_star_on);
            lp.IsFavourate = true;
        }
        else
        {
            FavButton.setImageResource(R.drawable.button_star_off);
            lp.IsFavourate = false;
        }
    }

    private void setGrade()
    {
        TextView grade = (TextView) findViewById(R.id.grade);
        String gadeval = "";
        int gradeValue = lp.Grade;
        int val = (int) Math.floor(gradeValue/10);
        if (lp.IsFont)
        {
            gadeval += "f";
            gadeval += val;
            boolean plus = false;
            int rem = gradeValue%10;
            if (rem%2 == 0)
            {
                plus = true;
                rem--;
            }
            if (rem == 1)
            {
                gadeval += "a";
            }
            if (rem == 3)
            {
                gadeval += "b";
            }
            if (rem == 5)
            {
                gadeval += "c";
            }
            if(plus)
            {
                gadeval += "+";
            }
        }
        else
        {
            gadeval += "v";
            gadeval += val;
            if (gradeValue%10 != 0)
            {
                gadeval += "+";
            }
        }
        grade.setText(gadeval);
    }

    private void displayHolds()
    {
        CustomLayout viewGroup = (CustomLayout) findViewById(R.id.vg);
        for (int i = 0; i < lp.NumberOfHolds; i++)
        {
            int[] coords = lp.holdCoOrds.get(i);
            byte[] data = lp.holdData.get(i);
            Hold h = new Hold(this.getApplicationContext(),coords[0], coords[1], scaleReturn(data[1]), scaleReturn(data[1]), 8);
            boolean startHold = false;
            boolean finishHold = false;
            if ((data[0] & 128) >= 1) startHold = true; // extracts the desired bit form the byte using a bit mask
            if ((data[0] & 64) >= 1) finishHold = true;
            h.issh = startHold;
            h.isfh = finishHold;
            h.updateColour();
            viewGroup.addView(h, viewGroup.getLayoutParams());
        }
    }

    public int scaleReturn(byte scaling)
    {
        int Scale = 100 * scaling/Byte.MAX_VALUE;
        if (Scale < 50)
        {
            Scale = 50;
        }
        if (Scale > 100)
        {
            Scale = 100;
        }
        return Scale;
    }

    public void updateIsCompleted(View view)
    {
        Button completed = (Button) findViewById(R.id.problemCompleted);
        if (lp.IsComplete)
        {
            completed.setBackgroundColor(0xFFFF0000);
            lp.IsComplete = false;
        }
        else
        {
            completed.setBackgroundColor(0xE621BC28);
            lp.IsComplete = true;
        }
    }

    public void editRoute(View view)
    {
        Problems.problemHandle.EditingProblem = lp;
        Problems.problemHandle.ReadHolds(Problems.problemHandle.EditingProblem);
        Intent intent = new Intent(this, AddProblem.class);
        startActivity(intent);
    }

    public void updateRoute(View view)
    {
        Problems.problemHandle.updateProblemData(lp);
        Intent intent = new Intent(this, Boards.class);
        startActivity(intent);
    }

    public void updateFavourateButton(View view)
    {
        updateFav();
    }
}
