package com.example.myboard;
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

    public String toString()
    {
        return("Problem: "+Name+" fp: "+startPos+" length: "+NameLength+" grade: "+Grade+" isFav: "+IsFavourate+" isFont: "+IsFont+" isTechnical: "+IsTechnical+" IsPowerful: "+IsPowerful+" isSustained: "+IsSustained+" IsCrimpy: "+IsCrimpy+ " IsComplete: "+IsComplete);
    }
}
