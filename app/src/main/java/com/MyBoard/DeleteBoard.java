package com.MyBoard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import java.util.ArrayList;

public class DeleteBoard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_board);

        final BoardHandle boardHandle = Boards.boardHandle;

        ListView lv = (ListView) findViewById(R.id.List);

        final ArrayList<Board> tempBoards = new ArrayList<>(boardHandle.getBoards());

        if (boardHandle.getNumberOfBoards() > 0)
        {
            final String[] names = new String[boardHandle.getNumberOfBoards()];

            for (int index = 0; index < boardHandle.getNumberOfBoards(); index++)
            {
                names[index] = tempBoards.get(index).name;
            }
            CustomListLayout layout = new CustomListLayout(this, names);
            lv.setAdapter(layout);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Board board = tempBoards.get(position);
                    if  (!board.toBeDeleted)
                    {
                        board.toBeDeleted = true;
                        view.findViewById(R.id.layout).setBackgroundColor(0xff808588);
                    }
                    else
                    {
                        board.toBeDeleted = false;
                        view.findViewById(R.id.layout).setBackgroundColor(0xffEC407A);
                    }
                }
            });
        }

    }

    public void Confirm(View view)
    {
        Intent intent = new Intent(this, Boards.class);
        ArrayList<Board> toDelete = new ArrayList<>();

        for (Board b: Boards.boardHandle.getBoards())
        {
            if (b.toBeDeleted)
            {
                toDelete.add(b);
            }
        }

        for (int i = 0; i < toDelete.size(); i++)
        {
            Boards.boardHandle.deleteBoard(toDelete.get(i).name);
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        Boards.boardHandle.resetBoardFlags();
        super.onBackPressed();
    }

}
