package com.example.myboard;
import android.content.Context;

import java.io.*;

public class Board
{
    public int nameLength;
    public byte data;
    public String name;
    public long fileStartPos = 0;
    public boolean isFavourate = false;
    public boolean toBeDeleted = false;


    public Board(int NameLength, byte Data, String Name)
    {
        nameLength = NameLength;
        data = Data;
        name = Name;
        if ((Data & 128) >= 1) isFavourate = true;
    }

    public String toString()
    {
        return("Board: "+name+" length: "+nameLength+" isFav: "+isFavourate);
    }

    public void WriteHeader(String imageUrl, Context context)
    {
        try
        {
            File f = new File(context.getFilesDir(), name);
            RandomAccessFile file = new RandomAccessFile(f, "rw"); //opens in read mode
            try
            {
                file.seek(0);
                file.writeInt(0);
                file.writeInt(imageUrl.length());
                file.writeChars(imageUrl);
            }
            catch (IOException e)
            {
            }
        }
        catch(FileNotFoundException e)
        {
        }
    }

    public void RemoveFile(Context context)
    {
        try
        {
            File f = new File(context.getFilesDir(), name);
            RandomAccessFile file = new RandomAccessFile(f, "rw"); //opens in read mode
            file.close();
        }
        catch(FileNotFoundException e)
        {
        } catch (IOException e)
        {
        }
    }
}
