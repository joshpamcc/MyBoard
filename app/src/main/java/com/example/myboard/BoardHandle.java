package com.example.myboard;
import android.content.Context;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;
import java.util.*;

public class BoardHandle extends AppCompatActivity
{
    static private RandomAccessFile file;
    static private ArrayList<Board> boards = new ArrayList<>();
    static private ArrayList<Board> sortedBoards = new ArrayList<>();
    static public Board loadedBoard;
    static private int numberOfBoards;
    static public Board EditingBoard;
    static private Context context;

    public BoardHandle(Context c)
    {
        boards.clear();
        context = c;
        try
        {
            File f = new File(context.getFilesDir(),"Boards");
            file = new RandomAccessFile(f, "rw"); //opens in read mode
            if (f.length() > 0)
            {
                ReadHeader();
                ReadBoards();
            }
        }
        catch(IOException e)
        {
        }

    }

    public void LoadBoard(String BoardName)
    {
        for (int index = 0; index < boards.size(); index++)
        {
            if (boards.get(index).name.equals(BoardName))
            {
                loadedBoard = boards.get(index);
            }
        }
    }

    public void CreateBoard(int NameLength, byte Data, String Name, String URI)
    {
        Board board = new Board(NameLength, Data, Name);
        int totalNameLength = 0;

        for (int index = 0; index < boards.size(); index++)
        {
            totalNameLength += boards.get(index).nameLength;
        }

        board.fileStartPos = 5 + totalNameLength*2 + 5*numberOfBoards;
        WriteBoard(board);
        boards.add(board);
        board.WriteHeader(URI, context);
        updateNumberOfBoards();
    }

    private void ReadHeader()
    {
        try
        {
            file.seek(0);
            numberOfBoards = file.readInt();
            file.seek(file.getFilePointer()+1);
        }
        catch (IOException e)
        {
        }
    }

    private void ReadBoards()
    {
        try
        {
            for (int index = 0; index < numberOfBoards; index++)
            {
                long BoardStartPos = file.getFilePointer();
                int nameLength = file.readInt();
                byte boardDesc = file.readByte();

                String BoardName = "";

                for (int count = 0; count < nameLength; count++) // gets route name
                {
                    BoardName = BoardName + file.readChar();
                }

                Board board = new Board(nameLength, boardDesc, BoardName);
                board.fileStartPos = BoardStartPos;
                boards.add(board);
            }
        }
        catch (IOException e)
        {
        }
    }

    private void WriteBoard(Board board)
    {
        try
        {
            if(boards.size() > 0)
            {
                file.seek(board.fileStartPos); //finds the byte after the last byte of the last problem
            }
            else
            {
                file.seek(5);
            }
            file.writeInt(board.nameLength);
            file.writeByte(board.data);
            file.writeChars(board.name);
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void updateNumberOfBoards()
    {
        try
        {
            file.seek(0);
            file.writeInt(boards.size());
        }
        catch (IOException e)
        {
        }
    }

    public void UpdateBoards()
    {
        if(numberOfBoards > 0)
        {
            boards.clear();
            ReadHeader();
            ReadBoards();
        }
    }

    public void deleteBoard(String boardname)
    {
        for (int index = 0; index < boards.size(); index++)
        {
            if (boards.get(index).name.equals(boardname))
            {
                Board b = boards.get(index);
                DeleteBoard(b);
            }
        }
    }

    private void DeleteBoard(Board board)
    {
        int indexOfBoardToDelete = boards.indexOf(board);
        board.RemoveFile(context);
        try
        {
            file.seek(board.fileStartPos);
            long startPos = board.fileStartPos;
            if(boards.size() > 0)
            {
                boards.remove(board);
                for (int i = indexOfBoardToDelete; i < boards.size(); i++)
                {
                    boards.get(i).fileStartPos = startPos;
                    WriteBoard(boards.get(i));
                    startPos = file.getFilePointer();
                }
                file.setLength(file.getFilePointer());
                updateNumberOfBoards();
            }
        }
        catch (IOException e)
        {

        }
    }

    public void sort(int sortid)
    {
        sortedBoards.clear();
        ArrayList<Board> temp = new ArrayList<>(boards);
        ArrayList<Board> favs = new ArrayList<>();
        favs = getFavs();

        for (Board b: favs)
        {
            if (temp.contains(b))
            {
                temp.remove(b);
            }
        }

        if (sortid == 0) //name asc (default)
        {
            ArrayList<String> NormalNames = getStrings(temp);
            ArrayList<String> FavNames = getStrings(favs);
            Collections.sort(FavNames);
            Collections.sort(NormalNames);
            sortByName(NormalNames, FavNames);
        }
        if (sortid == 1) //name desc
        {
            ArrayList<String> NormalNames = getStrings(temp);
            ArrayList<String> FavNames = getStrings(favs);
            Collections.sort(FavNames, Collections.<String>reverseOrder());
            Collections.sort(NormalNames, Collections.<String>reverseOrder());
            sortByName(NormalNames, FavNames);
        }
    }

    private void sortByName(ArrayList<String> NormalNames,ArrayList<String> FavNames)
    {
        for (int i = 0; i < boards.size(); i++)
        {
            if (i<FavNames.size())
            {
                sortedBoards.add(boards.get(getIndexFromName(FavNames.get(i))));
            }
            else
            {
                sortedBoards.add(boards.get(getIndexFromName(NormalNames.get(i - FavNames.size()))));
            }
        }
    }

    private ArrayList<String> getStrings(ArrayList<Board> problemlist)
    {
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < problemlist.size(); i++)
        {
            names.add(problemlist.get(i).name);
        }
        return names;
    }

    private ArrayList<Board> getFavs()
    {
        ArrayList<Board> favs = new ArrayList<>();
        for (int i = 0; i < boards.size(); i++)
        {
            if (boards.get(i).isFavourate)
            {
                favs.add(boards.get(i));
            }
        }
        return favs;
    }

    public boolean nameExists(String boardName)
    {
        boolean exists = false;
        for (Board b: boards)
        {
            if (b.name.equals(boardName))
            {
                exists = true;
                break;
            }
        }
        return  exists;
    }

    public Board getBoardWithName(String boardName)
    {
        Board b = null;
        for (int i = 0; i < boards.size(); i++)
        {
            if (boards.get(i).name.equals(boardName))
            {
                b = boards.get(i);
            }
        }
        return b;
    }

    public ArrayList<Board> getBoards()
    {
        return boards;
    }

    public ArrayList<Board> getSortedBoards()
    {
        return sortedBoards;
    }

    public int getSize()
    {
        return boards.size();
    }

    public int getNumberOfBoards(){ return numberOfBoards;}

    public String getNameAt(int index)
    {
        return boards.get(index).name;
    }

    public String getSortedNameAt(int index)
    {
        return sortedBoards.get(index).name;
    }

    public int getIndexFromName(String name)
    {
        int index = 0;
        for (int i = 0; i < boards.size(); i++)
        {
            if (boards.get(i).name.equals(name))
            {
                index = i;
                break;
            }
        }
        return index;
    }
}
