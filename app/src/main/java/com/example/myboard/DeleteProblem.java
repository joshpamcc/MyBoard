package com.example.myboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DeleteProblem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_problem);

        final ProblemHandle problemHandle = Problems.problemHandle;

        ListView lv = (ListView) findViewById(R.id.List);

        final ArrayList<Problem> tempProbs = new ArrayList<>(Problems.problemHandle.getSortedProblems());

        if (problemHandle.getNumberOfProblems() > 0)
        {
            final String[] names = new String[problemHandle.getNumberOfProblems()];

            for (int index = 0; index < problemHandle.getNumberOfProblems(); index++)
            {
                names[index] = tempProbs.get(index).Name;
            }
            ProblemListLayout layout = new ProblemListLayout(this, names);
            lv.setAdapter(layout);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Problem problem = tempProbs.get(position);
                    if  (!problem.toBeDeleted)
                    {
                        problem.toBeDeleted = true;
                        view.findViewById(R.id.layout).setBackgroundColor(0xff808588);
                    }
                    else
                    {
                        problem.toBeDeleted = false;
                        view.findViewById(R.id.layout).setBackgroundColor(0xffEC407A);
                    }
                }
            });
        }
    }

    public void Confirm(View view){
        Intent intent = new Intent(this, Boards.class);
        ArrayList<Problem> toDelete = new ArrayList<>();

        for (Problem p: Problems.problemHandle.getProblems())
        {
            if (p.toBeDeleted)
            {
                toDelete.add(p);
            }
        }

        for (int i = 0; i < toDelete.size(); i++)
        {
            Problems.problemHandle.deleteProblem(toDelete.get(i).Name);
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Problems.problemHandle.resetProblemFlags();
        super.onBackPressed();
    }
}
