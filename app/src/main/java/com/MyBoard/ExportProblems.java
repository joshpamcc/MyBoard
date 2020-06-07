package com.MyBoard;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import net.glxn.qrgen.android.QRCode;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ExportProblems extends AppCompatActivity
    {

    private static String output = "";
    public static ArrayList<Bitmap> codeList = new ArrayList<>();
    public static ArrayList<File> codeFiles = new ArrayList<>();
    private boolean selectAll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        codeFiles.clear();
        codeList.clear();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_problems);
        updateListDisplay();
    }

        private void updateListDisplay()
        {
            ListView lv = (ListView) findViewById(R.id.List);
            final ProblemHandle problemHandle = Problems.problemHandle;
            final ArrayList<Problem> tempProbs = Problems.problemHandle.getSortedProblems();
            problemHandle.sort(0);
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

                        if  (!problem.toBeExported)
                        {
                            problem.toBeExported = true;
                            view.findViewById(R.id.layout).setBackgroundColor(0xff808588);
                        }
                        else
                        {
                            problem.toBeExported = false;
                            view.findViewById(R.id.layout).setBackgroundColor(0xffEC407A);
                        }
                    }
                });
            }
        }

    public void Confirm(View view)
    {
        final int MAX_CHAR_LIM = 2948;
        int CHARS_USED = 0;
        int CodesToSend = 1;

        ArrayList<Integer> cutOffs = new ArrayList<>();
        ArrayList<Problem> problems = new ArrayList<>();
        ProblemHandle ph = Problems.problemHandle;

        int counter = 0;
        for (Problem p : ph.getSortedProblems())
        {
            if (p.toBeExported)
            {
                problems.add(p);
                p.toBeExported = false;
                ph.ReadHolds(p);
                CHARS_USED += (13 + (2*p.NameLength) + (10*p.NumberOfHolds));

                if (CHARS_USED > MAX_CHAR_LIM)
                {
                    cutOffs.add(counter);
                    CodesToSend++;
                    CHARS_USED = (13 + (2*p.NameLength) + (10*p.NumberOfHolds));
                }
            }
            counter++;
        }

        for (int i = 0; i < CodesToSend; i++)
        {
            if (CodesToSend > 1)
            {
                if (i == 0)
                {
                    WriteOutput(getProblemList(problems, i, cutOffs.get(i)), 1);
                }
                else
                {
                    int b = 0;
                    if (i < cutOffs.size())
                    {
                        b = cutOffs.get(i);
                    }
                    WriteOutput(getProblemList(problems, cutOffs.get(i-1), b), 1);
                }
            }
            else
            {
                WriteOutput(problems, 1);
            }
        }
        Intent intent = new Intent(this, ShowCode.class);
        startActivity(intent);
    }

    private ArrayList<Problem> getProblemList(ArrayList<Problem> problemSet, int a, int b)
    {
        ArrayList<Problem> sending = new ArrayList<>();
        if (b == 0)
        {
            b = problemSet.size();
        }

        for (int i = a; i < b; i++)
        {
            sending.add(problemSet.get(i));
        }

        return sending;
    }

    private void WriteOutput(ArrayList<Problem> problems, int n)
    {
        AddQRHeader(problems.size());
        for (Problem p: problems)
        {
            FormatInt(p.NameLength);
            FormatInt(p.Grade);
            FormatData(p);
            FormatString(p.Name);
            FormatInt(p.NumberOfHolds);
            FormatHolds(p);
        }
        String filename = "";
        filename += "QRCOde:"+Build.MODEL + Boards.loadedBoard + n+".png";
        File parent = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyBoard");
        if (!parent.exists())
        {
            parent.mkdirs();
        }

        try
        {
            File f =  new File(parent, filename);
            ByteArrayOutputStream bs = QRCode.from(output).withCharset("UTF-8").stream();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bs.toByteArray());
            codeFiles.add(f);
            fo.close();
        }
        catch(IOException e)
        {
        }
        Bitmap im = QRCode.from(output).withCharset("UTF-8").bitmap();
        codeList.add(im);
    }

    private void AddQRHeader(int numOfProblems)
    {
        output = "";
        byte checksum = Byte.MAX_VALUE;
        output += ((char) checksum);
        output += ((char) ((byte) numOfProblems));
    }

    private void FormatData(Problem p)
    {
        byte data = 0;
        data += (p.IsFavourate) ? 0 << 7: 0 << 7;
        data += (p.IsFont) ? 1 << 6: 0 << 6;
        data += (p.IsTechnical) ? 1 << 5: 0 << 5;
        data += (p.IsPowerful) ? 1 << 4: 0 << 4;
        data += (p.IsSustained) ? 1 << 3: 0 << 3;
        data += (p.IsCrimpy) ? 1 << 2: 0 << 2;
        data += (p.IsComplete) ? 0 << 1: 0 << 1;
        output += ((char) data);
    }

    private void FormatInt(int data)
    {
        byte[] formatted = ByteBuffer.allocate(4).putInt(data).array();
        for (int i = 0; i < 4; i++)
        {
            output += ((char) formatted[i]);
        }
    }

    private void FormatString(String data)
    {
        for (int i = 0; i < data.length(); i++)
        {
            byte[] formatted = new byte[2];
            formatted[0] = (byte) (data.charAt(i) & 0xff00);
            formatted[1] = (byte) (data.charAt(i) & 0x00ff);
            for (int c = 0; c < 2; c++)
            {
                output += ((char) formatted[c]);
            }
        }
    }

    private void FormatHolds(Problem p)
    {
        for (int i = 0; i < p.NumberOfHolds; i++)
        {
            FormatInt(p.holdCoOrds.get(i)[0]);
            FormatInt(p.holdCoOrds.get(i)[1]);
            output += ((char) p.holdData.get(i)[0]);
            output += ((char) p.holdData.get(i)[1]);
        }
    }

    public void SelectAll (View view)
    {
        if (selectAll)
        {
            selectAll = false;
        }
        else
        {
            selectAll = true;
        }
        for (Problem p: Problems.problemHandle.getProblems())
        {
            p.toBeExported = selectAll;
        }
        updateListDisplay();
    }

    @Override
    public void onBackPressed()
    {
        Problems.problemHandle.resetProblemFlags();
        super.onBackPressed();
    }
}