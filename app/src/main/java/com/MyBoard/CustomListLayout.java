package com.MyBoard;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

public class CustomListLayout extends ArrayAdapter
{
    LayoutInflater layoutInflator;
    ArrayList<Board> boardNames;
    private Activity context;

    public CustomListLayout(Activity c, String[] content)
    {
        super(c, R.layout.list, content);
        context = c;
        boardNames = Boards.boardHandle.getSortedBoards();
    }

    @Override
    public int getCount()
    {
        return boardNames.size();
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
        View v = inflater.inflate(R.layout.list, null, true);
        TextView name = (TextView) v.findViewById(R.id.textname);
        if (boardNames.get(i).isFavourate)
        {
            ImageView icon = (ImageView) v.findViewById(R.id.favboard);
            icon.setImageResource(R.drawable.button_star_on);
        }
        if (boardNames.get(i).toImport || boardNames.get(i).toBeDeleted)
        {
            v.findViewById(R.id.layout).setBackgroundColor(0xff808588);
        }
        else
        {
            v.findViewById(R.id.layout).setBackgroundColor(0xffEC407A);
        }

        name.setText(boardNames.get(i).name);
        return v;
    }
}
