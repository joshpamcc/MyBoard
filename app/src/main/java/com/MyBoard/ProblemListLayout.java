package com.MyBoard;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

public class ProblemListLayout extends ArrayAdapter {
    LayoutInflater layoutInflator;
    ArrayList<Problem> problemlist;
    private Activity context;

    public ProblemListLayout(Activity c, String[] content)
    {
        super(c, R.layout.list, content);
        context = c;
        problemlist = Problems.problemHandle.getSortedProblems();
    }

    @Override
    public int getCount()
    {
        return problemlist.size();
    }

    @Override
    public Object getItem(int i)
    {
        return null;
    }

    @Override
    public long getItemId(int i)
    {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        LayoutInflater inflater = context.getLayoutInflater();
        View v = inflater.inflate(R.layout.problem_list, null, true);
        TextView name = (TextView) v.findViewById(R.id.problemname);
        TextView grade = (TextView) v.findViewById(R.id.grade);
        ImageView crimpy = (ImageView) v.findViewById(R.id.crimpy);
        ImageView tech = (ImageView) v.findViewById(R.id.technical);
        ImageView sus = (ImageView) v.findViewById(R.id.sustained);
        ImageView power = (ImageView) v.findViewById(R.id.powerful);
        String gadeval = "";
        int gradeValue = problemlist.get(i).Grade;
        if (problemlist.get(i).IsFavourate)
        {
            ImageView icon = (ImageView) v.findViewById(R.id.favroute);
            icon.setImageResource(R.drawable.button_star_on);
        }
        else
        {
            ImageView icon = (ImageView) v.findViewById(R.id.favroute);
            icon.setImageResource(R.drawable.button_star_off);
        }
        ImageView completed = (ImageView) v.findViewById(R.id.completed);
        if (problemlist.get(i).IsComplete)
        {
            completed.setBackgroundColor(0xE621BC28);
        }
        else
        {
            completed.setBackgroundColor(0xFFFF0000);
        }
        if (problemlist.get(i).toBeDeleted || problemlist.get(i).toBeExported)
        {
            v.findViewById(R.id.layout).setBackgroundColor(0xff808588);
        }
        else
        {
            v.findViewById(R.id.layout).setBackgroundColor(0xffEC407A);
        }
        if (problemlist.get(i).IsCrimpy)
        {
            crimpy.setVisibility(View.VISIBLE);
        }
        if (problemlist.get(i).IsPowerful)
        {
            power.setVisibility(View.VISIBLE);
        }
        if (problemlist.get(i).IsSustained)
        {
            sus.setVisibility(View.VISIBLE);
        }
        if (problemlist.get(i).IsTechnical)
        {
            tech.setVisibility(View.VISIBLE);
        }
        int val = (int) Math.floor(gradeValue/10);
        if (problemlist.get(i).IsFont)
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
        name.setText(problemlist.get(i).Name);
        return v;
    }
}
