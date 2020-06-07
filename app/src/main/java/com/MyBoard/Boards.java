package com.MyBoard;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;


public class Boards extends AppCompatActivity
{
    public static BoardHandle boardHandle;
    public static String loadedBoard = "";
    public static int sortId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boards);
        boardHandle = new BoardHandle(this.getApplicationContext());
        boardHandle.sort(sortId);
        updateBoardsList();
        if (!loadedBoard.equals(""))
        {
            Intent i = new Intent(Boards.this, Problems.class);
            i.putExtra("board", loadedBoard);
            startActivity(i);
        }
    }

    private void updateBoardsList()
    {
        ListView lv = (ListView) findViewById(R.id.List);

        if (boardHandle.getSize() > 0)
        {
            final String[] names = new String[boardHandle.getSize()];

            for (int index = 0; index < boardHandle.getSize(); index++)
            {
                names[index] = boardHandle.getSortedNameAt(index);
            }
            CustomListLayout layout = new CustomListLayout(this, names);
            lv.setAdapter(layout);

            lv.setOnItemClickListener(new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Intent i = new Intent(Boards.this, Problems.class);
                    String message = names[position];
                    i.putExtra("board", message);
                    loadedBoard = message;
                    startActivity(i);
                }
            });

        }
    }

    public void UpdateSort(View view)
    {
        Button sort = (Button) findViewById(R.id.confirm);
        sortId++;
        if(sortId > 1)
        {
            sortId = 0;
        }
        if (sortId == 0)
        {
            sort.setText("Names (ASC)");
        }
        if (sortId == 1)
        {
            sort.setText("Names (DESC)");
        }
        boardHandle.sort(sortId);
        updateBoardsList();
    }

    @Override
    public boolean onNavigateUp() {
        loadedBoard = "";
        return super.onNavigateUp();
    }

    @Override
    public void onBackPressed() {
        loadedBoard = "";
        super.onBackPressed();
    }

    public void AddBoard(View view)
    {
        Intent intent = new Intent(this, AddBoard.class);
        startActivity(intent);
    }

    public void DeleteBoard(View view)
    {
        Intent intent = new Intent(this, DeleteBoard.class);
        startActivity(intent);
    }

    public BoardHandle getBoardHandle()
    {
        return boardHandle;
    }

    public void selectedBaord(View view)
    {
        Intent intent = new Intent(this, Problems.class);
        startActivity(intent);
    }
}
