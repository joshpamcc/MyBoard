package com.MyBoard;
import java.util.ArrayList;
public class Problem
{
    public ArrayList<int[]> holdCoOrds = new ArrayList<>();
    public ArrayList<byte[]> holdData = new ArrayList<>();
    public byte problemDescriptors;
    public boolean IsFavourate, IsFont, IsCrimpy, IsPowerful, IsTechnical, IsSustained, IsComplete;
    public boolean toBeDeleted = false;
    public boolean toBeExported = false;
    public String Name;
    public int NumberOfHolds, NameLength, Grade;
    public long startPos = 0;

    public Problem(String name, boolean isFavourate, boolean isFont, int grade, boolean isTechnical, boolean isPowerful, boolean isSustained, boolean isCrimpy,boolean isComplete, int numberOfHolds, int nameLength)
    {
        Name = name;
        Grade = grade;
        IsFavourate = isFavourate;
        IsFont = isFont;
        IsCrimpy = isCrimpy;
        IsPowerful = isPowerful;
        IsTechnical = isTechnical;
        IsSustained = isSustained;
        IsComplete = isComplete;
        NumberOfHolds = numberOfHolds;
        NameLength = nameLength;
    }

    public Problem(String name, int grade, int numberOfHolds, int nameLength, byte data)
    {
        Name = name;
        Grade = grade;
        NumberOfHolds = numberOfHolds;
        NameLength = nameLength;
        updateRouteDesc(data);
    }

    public void updateRouteDesc(byte data)
    {
        problemDescriptors = data;
        IsFavourate = false;
        IsFont = false;
        IsTechnical = false;
        IsPowerful = false;
        IsSustained = false;
        IsCrimpy = false;
        IsComplete = false;
        if ((data & 128) >= 1) IsFavourate = true; // extracts the desired bit form the byte using a bit mask
        if ((data & 64) >= 1) IsFont = true;
        if ((data & 32) >= 1) IsTechnical = true;
        if ((data & 16) >= 1) IsPowerful = true;
        if ((data & 8) >= 1) IsSustained = true;
        if ((data & 4) >= 1) IsCrimpy = true;
        if ((data & 2) >= 1) IsComplete = true;
    }

    public String toString()
    {
        return("Problem: "+Name+" fp: "+startPos+" length: "+NameLength+" grade: "+Grade+" number of holds: "+NumberOfHolds+" isFav: "+IsFavourate+" isFont: "+IsFont+" isTechnical: "+IsTechnical+" IsPowerful: "+IsPowerful+" isSustained: "+IsSustained+" IsCrimpy: "+IsCrimpy+ " IsComplete: "+IsComplete);
    }
}
