package com.MyBoard;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;


import java.util.ArrayList;


public class AddProblem extends AppCompatActivity
{
    private static boolean isFav = false;
    private static boolean isCom = false;
    private static ArrayList<Hold> holdList = new ArrayList<>();
    public static ZoomableImageView Image;
    public static Switch isStartHold, isFinishHold;
    public static Button increaseScale, decreaseScale, delete;
    private static Hold touchedHold;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        holdList.clear();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_problem);
        Image = (ZoomableImageView) findViewById((R.id.BoardImage));
        Image.setImageBitmap(Problems.image);

        isFinishHold = (Switch) findViewById(R.id.isFinishHold);
        isStartHold = (Switch) findViewById(R.id.isStartHold);
        increaseScale = (Button) findViewById(R.id.IncreaseScale);
        decreaseScale = (Button) findViewById(R.id.DecreaseScale);
        delete = (Button) findViewById(R.id.delete);

        isFinishHold.setVisibility(View.INVISIBLE);
        isStartHold.setVisibility(View.INVISIBLE);
        increaseScale.setVisibility(View.INVISIBLE);
        decreaseScale.setVisibility(View.INVISIBLE);
        delete.setVisibility(View.INVISIBLE);
        final CustomLayout viewGroup = (CustomLayout) findViewById(R.id.vg);

        findViewById(R.id.vg).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                boolean holdTouched = false;
                for (int i = 0; i < holdList.size(); i++)
                {
                    if (holdList.get(i).drawable.getBounds().contains((int) event.getX(), (int) event.getY()) && holdList.get(i).isInBounds)
                    {
                        touchedHold = holdList.get(i);
                        holdTouched = true;
                        holdList.get(i).holdHandleTouch(event);
                        holdList.get(i).onSelect();
                    }

                    if (!holdTouched)
                    {
                        isFinishHold.setVisibility(View.INVISIBLE);
                        isStartHold.setVisibility(View.INVISIBLE);
                        increaseScale.setVisibility(View.INVISIBLE);
                        decreaseScale.setVisibility(View.INVISIBLE);
                        delete.setVisibility(View.INVISIBLE);
                    }
                }
                return true;
            }
        });

        if (Problems.problemHandle.EditingProblem != null)
        {
            edit();
        }
    }

    public void CreateProblem(View view)
    {
        Intent intent = new Intent(this, Boards.class);

        EditText problemName = (EditText) findViewById(R.id.ProblemName);
        EditText grade = (EditText) findViewById(R.id.ProblemGrade);
        Switch isCrimpyS = (Switch) findViewById(R.id.IsCrimpy);
        Switch isSustainedS = (Switch) findViewById(R.id.IsSustained);
        Switch isPowerfulS = (Switch) findViewById(R.id.IsPowerful);
        Switch isTechnicalS = (Switch) findViewById(R.id.IsTechnical);
        String ProblemName = problemName.getText().toString();
        String Grade = grade.getText().toString();

        boolean isCrimpy = isCrimpyS.isChecked();
        boolean isSustained = isSustainedS.isChecked();
        boolean isTechnical = isTechnicalS.isChecked();
        boolean isPowerful = isPowerfulS.isChecked();
        boolean isFont = false;
        int gradevalue = 0;
        boolean hasPlus = false;
        int subgrade = 0;

        String strippedGrade = "";

        if (Grade.contains("v") || Grade.contains("V"))
        {
            if (Grade.contains("+")) hasPlus = true;
        }
        else
        {
            isFont = true;
            if (Grade.contains("a") || Grade.contains("A")) subgrade = 1;
            if (Grade.contains("b") || Grade.contains("B")) subgrade = 3;
            if (Grade.contains("c") || Grade.contains("C")) subgrade = 5;
            if (Grade.contains("+")) subgrade++;
        }

        for (int index = 0; index < Grade.length(); index++)
        {
            if ((int) Grade.charAt(index) < 58 && (int) Grade.charAt(index) > 47)
            {
                strippedGrade += Grade.charAt(index);
            }
        }

        int count = 1;

        for (int index = strippedGrade.length() - 1; index >= 0; index--)
        {
            gradevalue += ((int) strippedGrade.charAt(index) - 48) * (int) Math.pow(10, count);
            count++;
        }

        gradevalue += subgrade;

        if (hasPlus && !isFont) gradevalue += 5;

        if (ProblemName.length() <= 32 && ProblemName.length() > 0)
        {
            if (gradevalue == 0 && isFont)
            {
                Toast.makeText(getApplicationContext(), "Enter a problem grade!", Toast.LENGTH_SHORT).show();
            }

            else
            {
                boolean startExists = false;
                boolean endExists = false;

                for (Hold h: holdList)
                {
                    if (h.issh)
                    {
                        startExists = true;
                    }
                    if (h.isfh)
                    {
                        endExists = true;
                    }
                    if (endExists && startExists)
                    {
                        break;
                    }
                }

                if (endExists && startExists)
                {
                    if (Problems.problemHandle.nameExists(ProblemName) && Problems.problemHandle.EditingProblem == null)
                    {
                        Toast.makeText(getApplicationContext(), "A problem already exists with that name!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if (Problems.problemHandle.EditingProblem != null)
                        {
                            Problems.problemHandle.deleteProblem(Problems.problemHandle.EditingProblem.Name);
                            Problems.problemHandle.EditingProblem = null;
                        }
                        Problems.problemHandle.CreateProblem(ProblemName, isFav, isFont, gradevalue, isTechnical, isPowerful, isSustained, isCrimpy, isCom, holdList.size(), problemName.length(), holdList);
                        startActivity(intent);
                    }
                }

                else
                {
                    Toast.makeText(getApplicationContext(), "You must have at least one start hold and one finish hold!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else
        {
            if (ProblemName.length() == 0)
            {
                Toast.makeText(getApplicationContext(), "Enter a problem name!", Toast.LENGTH_SHORT).show();
            }

            else
            {
                Toast.makeText(getApplicationContext(), "Problem names must be under 32 characters!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void edit()
    {
        Problem problem = Problems.problemHandle.EditingProblem;
        EditText problemName = (EditText) findViewById(R.id.ProblemName);
        Switch isCrimpyS = (Switch) findViewById(R.id.IsCrimpy);
        Switch isSustainedS = (Switch) findViewById(R.id.IsSustained);
        Switch isPowerfulS = (Switch) findViewById(R.id.IsPowerful);
        Switch isTechnicalS = (Switch) findViewById(R.id.IsTechnical);
        ImageButton FavButton = (ImageButton) findViewById(R.id.IsFav);
        problemName.setText(problem.Name);
        isCrimpyS.setChecked(problem.IsCrimpy);
        isPowerfulS.setChecked(problem.IsPowerful);
        isSustainedS.setChecked(problem.IsSustained);
        isTechnicalS.setChecked(problem.IsTechnical);
        isFav = problem.IsFavourate;
        isCom = problem.IsComplete;
        if(problem.IsFavourate)
        {
            FavButton.setImageResource(R.drawable.button_star_on);
        }
        else
        {
            FavButton.setImageResource(R.drawable.button_star_off);
        }
        setGrade(problem);
        displayHolds(problem);
    }

    private void setGrade(Problem problem)
    {
        EditText grade = (EditText) findViewById(R.id.ProblemGrade);
        String gadeval = "";
        int gradeValue = problem.Grade;
        int val = (int) Math.floor(gradeValue/10);
        if (problem.IsFont)
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

    public void updateFavourateButton(View view)
    {
        ImageButton FavButton = (ImageButton) findViewById(R.id.IsFav);

        if(!isFav)
        {
            FavButton.setImageResource(R.drawable.button_star_on);
            isFav = true;
        }

        else
        {
            FavButton.setImageResource(R.drawable.button_star_off);
            isFav = false;
        }
    }

    private void displayHolds(Problem problem)
    {
        CustomLayout viewGroup = (CustomLayout) findViewById(R.id.vg);
        for (int i = 0; i < problem.NumberOfHolds; i++)
        {
            int[] coords = problem.holdCoOrds.get(i);
            byte[] data = problem.holdData.get(i);
            Hold h = new Hold(this.getApplicationContext(),coords[0], coords[1], scaleReturn(data[1]), scaleReturn(data[1]), 8);
            boolean startHold = false;
            boolean finishHold = false;
            if ((data[0] & 128) >= 1) startHold = true; // extracts the desired bit form the byte using a bit mask
            if ((data[0] & 64) >= 1) finishHold = true;
            h.issh = startHold;
            h.isfh = finishHold;
            h.updateColour();
            viewGroup.addView(h, viewGroup.getLayoutParams());
            h.scaling = data[1];
            holdList.add(h);
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

    public void addHold(View view)
    {
        CustomLayout viewGroup = (CustomLayout) findViewById(R.id.vg);
        int xpos = Image.getWidth()/2 - 100;
        int ypos = Image.getHeight()/2 - 100;
        Hold h = new Hold(this, xpos, ypos, 100, 100, 8);
        holdList.add(h);
        viewGroup.addView(h, viewGroup.getLayoutParams());
    }

    public void onTouchIsSH(View view)
    {
        if (touchedHold.isfh && isStartHold.isChecked())
        {
            touchedHold.isfh = false;
        }
        touchedHold.issh = isStartHold.isChecked();
        touchedHold.onSelect();
    }

    public void onTouchIsFH(View view)
    {
        if (touchedHold.issh && isFinishHold.isChecked())
        {
            touchedHold.issh = false;
        }
        touchedHold.isfh = isFinishHold.isChecked();
        touchedHold.onSelect();
    }

    public void onTouchScaleUp(View view)
    {
        touchedHold.scaling += 3;
        if (touchedHold.scaling < 0)
        {
            touchedHold.scaling = Byte.MAX_VALUE;
        }
        touchedHold.setSize(touchedHold.scaleReturn());
    }

    public void onTouchScaleDown(View view)
    {
        touchedHold.scaling -= 3;
        if (touchedHold.scaling < 50)
        {
            touchedHold.scaling = 50;
        }
        touchedHold.setSize(touchedHold.scaleReturn());
    }

    public void deleteHold(View view)
    {
        CustomLayout viewGroup = (CustomLayout) findViewById(R.id.vg);
        holdList.remove(touchedHold);
        viewGroup.removeView(touchedHold);
    }

    @Override
    public void onBackPressed() {
        Problems.problemHandle.EditingProblem = null;
        super.onBackPressed();
    }
}

