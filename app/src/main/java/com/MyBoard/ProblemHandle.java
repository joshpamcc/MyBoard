package com.MyBoard;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import java.io.*;
import java.util.*;
public class ProblemHandle extends AppCompatActivity
{
    static private RandomAccessFile file;
    static private String Image;
    static private int numberOfProblems, imageLength;
    static private ArrayList<Problem> problems = new ArrayList<>();
    static private ArrayList<Problem> sortedProblems = new ArrayList<>();
    static public Problem loadedProblem;
    static private Context context;
    static public Problem EditingProblem;

    static final private Integer emtpyInt = null;
    static final private Character emptyString = null;
    static final private Byte emptyByte = null;

    public ProblemHandle (String filename, Context c)
    {
        problems.clear();
        context = c;
        OpenFile(filename);
        ReadHeader();
        ReadProblems();
    }

    public void CreateProblem(String name, boolean isFavourate, boolean isFont, int grade, boolean isTechnical, boolean isPowerful, boolean isSustained, boolean isCrimpy, boolean isComplete, int numberOfHolds, int nameLength, ArrayList<Hold> holds)
    {
        Problem problem = new Problem(name, isFavourate, isFont, grade, isTechnical, isPowerful, isSustained, isCrimpy, isComplete, numberOfHolds, nameLength);

        for (int i = 0; i < numberOfHolds; i++)
        {
            Hold hold = holds.get(i);
            int[] coOrds = new int[2];
            byte[] hd = new byte[2];
            coOrds[0] = hold.drawable.getBounds().left;
            coOrds[1] = hold.drawable.getBounds().top;
            byte holdData = 0;
            holdData += (hold.issh) ? 1 << 7: 0 << 7;
            holdData += (hold.isfh) ? 1 << 6: 0 << 6;
            hd[0] = holdData;
            hd[1] = hold.scaling;
            problem.holdCoOrds.add(coOrds);
            problem.holdData.add(hd);
        }

        byte data = 0;
        data += (isFavourate) ? 1 << 7: 0 << 7;
        data += (isFont) ? 1 << 6: 0 << 6;
        data += (isTechnical) ? 1 << 5: 0 << 5;
        data += (isPowerful) ? 1 << 4: 0 << 4;
        data += (isSustained) ? 1 << 3: 0 << 3;
        data += (isCrimpy) ? 1 << 2: 0 << 2;
        data += (isComplete) ? 1 << 1: 0 << 1;
        problem.problemDescriptors = data;
        WriteProblem(problem, problems.size(), false);
        problems.add(problem);
        problem.holdCoOrds.clear();
        problem.holdData.clear();
        updateNumberOfProblems();
    }

    public void CreateProblem(Problem p)
    {
        WriteProblem(p, problems.size(), false);
        problems.add(p);
        p.holdCoOrds.clear();
        p.holdData.clear();
        updateNumberOfProblems();
    }

    public void CopyProblems(ArrayList<Problem> problemList)
    {
        problems.clear();
        for (Problem p: problemList)
        {
            WriteProblem(p, problems.size(), false);
            problems.add(p);
            p.holdCoOrds.clear();
            p.holdData.clear();
            updateNumberOfProblems();
        }
    }

    public void LoadProblem(String ProblemName)
    {
        for (int index = 0; index < problems.size(); index++)
        {
            if (problems.get(index).Name.equals(ProblemName))
            {
                loadedProblem = problems.get(index);
                ReadHolds(loadedProblem);
            }
        }
    }

    private static void OpenFile(String filename)
    {
        try
        {
            File f = new File(context.getFilesDir(), filename);
            file = new RandomAccessFile(f, "rw"); //opens in read mode
        }
        catch(FileNotFoundException e)
        {
        }
    }

    public void sort(int sortid)
    {
        sortedProblems.clear();
        ArrayList<Problem> temp = new ArrayList<>(problems);
        ArrayList<Problem> favs = new ArrayList<>();
        ArrayList<Problem> tempFav = new ArrayList<>();
        ArrayList<Problem> tempNorm = new ArrayList<>();
        favs.clear();
        tempFav.clear();
        tempNorm.clear();
        favs = getFavs();

        for (Problem p: favs)
        {
            if (temp.contains(p))
            {
                temp.remove(p);
            }
        }

        int[] favGrades = new int[favs.size()];
        int[] favGradesIndexs = new int[favs.size()];
        int[] normalGrades = new int[temp.size()];
        int[] normalGradeIndexs = new int[temp.size()];

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

        if (sortid > 1)
        {

            for (int i = 0; i < favGrades.length; i++)
            {
                favGrades[i] = favs.get(i).Grade;
                if (favs.get(i).IsFont && favGrades[i] < 70)
                {
                    favGrades[i] -= 10;
                }
                if (favs.get(i).IsFont && favGrades[i] >= 75)
                {
                    int sf = (int) Math.floor((favGrades[i] - 70)/10);
                    favGrades[i] += (10 + 10*sf);
                }
                favGradesIndexs[i] = i;
            }
            for (int i = 0; i < normalGrades.length; i++)
            {
                normalGrades[i] = temp.get(i).Grade;
                if (temp.get(i).IsFont && normalGrades[i] < 70)
                {
                    normalGrades[i] -= 10;
                }
                if (temp.get(i).IsFont && normalGrades[i] >= 75)
                {
                    int sf = (int) Math.floor((normalGrades[i] - 70)/10);
                    normalGrades[i] += (10 + 10*sf);
                }
                normalGradeIndexs[i] = i;
            }
            quicksort(favGrades, 0, (favGrades.length-1),favGradesIndexs);
            quicksort(normalGrades, 0, (normalGrades.length-1),normalGradeIndexs);
        }

        if (sortid == 2)
        {
            for (int i = 0; i < normalGrades.length; i++)
            {
                tempNorm.add(temp.get(normalGradeIndexs[i]));
            }
            for (int i = 0; i < favGrades.length; i++)
            {
                tempFav.add(favs.get(favGradesIndexs[i]));
            }
            sortByGrade(tempFav, tempNorm);
        }

        if (sortid == 3)
        {
            for (int i = normalGrades.length-1;  i >= 0; i--)
            {
                tempNorm.add(temp.get(normalGradeIndexs[i]));
            }
            for (int i = favGrades.length-1; i >= 0; i--)
            {
                tempFav.add(favs.get(favGradesIndexs[i]));
            }
            sortByGrade(tempFav, tempNorm);
        }
    }

    private void sortByName(ArrayList<String> NormalNames,ArrayList<String> FavNames)
    {
        for (int i = 0; i < problems.size(); i++)
        {
            if (i<FavNames.size())
            {
                sortedProblems.add(problems.get(getIndexFromName(FavNames.get(i))));
            }
            else
            {
                sortedProblems.add(problems.get(getIndexFromName(NormalNames.get(i - FavNames.size()))));
            }
        }
    }

    /* This function takes last element as pivot,
       places the pivot element at its correct
       position in sorted array, and places all
       smaller (smaller than pivot) to left of
       pivot and all greater elements to right
       of pivot */
    private int partition(int arr[], int low, int high , int[] indexs)
    {
        int pivot = arr[high];
        int i = (low-1); // index of smaller element
        for (int j=low; j<high; j++)
        {
            // If current element is smaller than the pivot
            if (arr[j] < pivot)
            {
                i++;

                // swap arr[i] and arr[j]
                int temp = arr[i];
                int tempindex = indexs[i];
                arr[i] = arr[j];
                indexs[i] = indexs[j];
                arr[j] = temp;
                indexs[j] = tempindex;
            }
        }

        // swap arr[i+1] and arr[high] (or pivot)
        int temp = arr[i+1];
        int tempindex = indexs[i+1];
        arr[i+1] = arr[high];
        indexs[i+1] = indexs[high];
        arr[high] = temp;
        indexs[high] = tempindex;
        return i+1;
    }


    /* The main function that implements QuickSort()
      arr[] --> Array to be sorted,
      low  --> Starting index,
      high  --> Ending index */
    private void quicksort(int arr[], int low, int high, int indexs[])
    {
        if (low < high)
        {
            /* pi is partitioning index, arr[pi] is
              now at right place */
            int pi = partition(arr, low, high, indexs);

            // Recursively sort elements before
            // partition and after partition
            quicksort(arr, low, pi-1, indexs);
            quicksort(arr, pi+1, high, indexs);
        }
    }


    private void sortByGrade(ArrayList<Problem> fav, ArrayList<Problem> normal)
    {
        for (int i = 0; i < problems.size(); i++)
        {
            if (i<fav.size())
            {
                sortedProblems.add(fav.get(i));
            }
            else
            {
                sortedProblems.add(normal.get(i - fav.size()));
            }
        }
    }

    private ArrayList<String> getStrings(ArrayList<Problem> problemlist)
    {
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < problemlist.size(); i++)
        {
            names.add(problemlist.get(i).Name);
        }
        return names;
    }

    private ArrayList<Problem> getFavs()
    {
        ArrayList<Problem> favs = new ArrayList<>();
        for (int i = 0; i < problems.size(); i++)
        {
            if (problems.get(i).IsFavourate)
            {
                favs.add(problems.get(i));
            }
        }
        return favs;
    }

    private void ReadHeader()
    {
        try
        {
            file.seek(0);
            numberOfProblems = file.readInt();
            imageLength = file.readInt();
            String imageUrl = "";

            for (int index = 0; index < imageLength; index++)
            {
                imageUrl += file.readChar();
            }

            Image = imageUrl;
            file.seek(file.getFilePointer()+1);
        }
        catch (IOException e)
        {
        }
    }

    public void ReadHolds(Problem problem)
    {
        try
        {
            int offset = 13 + problem.NameLength*2;
            file.seek((problem.startPos + offset));

            for (int index = 0; index < problem.NumberOfHolds; index++)
            {
                int[] holdCoOrds = new int[2];
                byte[] holdData = new byte[2];

                holdCoOrds[0] = file.readInt();
                holdCoOrds[1] = file.readInt();
                holdData[0] = file.readByte();
                holdData[1] = file.readByte();

                problem.holdCoOrds.add(holdCoOrds);
                problem.holdData.add(holdData);
            }
        }
        catch (IOException e)
        {
        }
    }

    /**
     * This method reads the all data about every route of the wall apart from the holds of the route
     */
    private void ReadProblems()
    {
        try
        {
            for (int index = 0; index < numberOfProblems; index++)
            {
                long problemStartPos = file.getFilePointer();
                int nameLength = file.readInt();
                int grade = file.readInt();
                byte routeDesc = file.readByte();

                boolean isFavourate = false;
                boolean isFont = false;
                boolean isTechnical = false;
                boolean isPowerful = false;
                boolean isSustained = false;
                boolean isCrimpy = false;
                boolean isComplete = false;
                String routeName = "";

                for (int count = 0; count < nameLength; count++) // gets route name
                {
                    routeName = routeName + file.readChar();
                }

                if ((routeDesc & 128) >= 1) isFavourate = true; // extracts the desired bit form the byte using a bit mask
                if ((routeDesc & 64) >= 1) isFont = true;
                if ((routeDesc & 32) >= 1) isTechnical = true;
                if ((routeDesc & 16) >= 1) isPowerful = true;
                if ((routeDesc & 8) >= 1) isSustained = true;
                if ((routeDesc & 4) >= 1) isCrimpy = true;
                if ((routeDesc & 2) >= 1) isComplete = true;

                int numberOfHolds = file.readInt();

                Problem p = new Problem(routeName, isFavourate, isFont, grade, isTechnical, isPowerful, isSustained, isCrimpy, isComplete, numberOfHolds, nameLength);
                p.startPos = problemStartPos;
                p.problemDescriptors = routeDesc;
                problems.add(p);
                file.seek((file.getFilePointer() + (numberOfHolds*10)));
            }
        }
        catch (IOException e)
        {
        }
    }

    public void deleteProblem(String problemName)
    {
        for (int index = 0; index < problems.size(); index++)
        {
            if (problems.get(index).Name.equals(problemName))
            {
                DeleteProblem(problems.get(index));
            }
        }
    }

    public void resetProblemFlags()
    {
        for (Problem p : sortedProblems)
        {
            p.toBeExported = false;
            p.toBeDeleted = false;
        }
    }

    private void DeleteProblem(Problem problem)
    {
        int indexOfProblem = problems.indexOf(problem);
        try
        {
            file.seek(problem.startPos);
            long startPos = problem.startPos;
            if(problems.size() > 0)
            {
                problems.remove(problem);
                for (int i = indexOfProblem; i < problems.size(); i++)
                {
                    ReadHolds(problems.get(i));
                    problems.get(i).startPos = startPos;
                    WriteProblem(problems.get(i), i, true);
                    startPos = file.getFilePointer();
                }
                file.setLength(file.getFilePointer());
                updateNumberOfProblems();
            }

        }
        catch (IOException e)
        {

        }
    }

    private void WriteProblem(Problem problem, int startindex, boolean isDelete)
    {
        try
        {
            if(!isDelete)
            {
                if (startindex > 0) //only needed for getting the fp to after the last hold on the last route (if after that is nothing)
                {
                    int offset = 13 + problems.get(startindex - 1).NameLength * 2 + (problems.get(startindex - 1).NumberOfHolds * 10);
                    long lastHoldStart = problems.get(startindex - 1).startPos;
                    file.seek(lastHoldStart + offset); //finds the byte after the last byte of the last problem
                }
                else
                {
                    file.seek(9 + 2 * imageLength);
                }
            }
            if (isDelete)
            {
                file.seek(problem.startPos);
            }
            if (problem.startPos == 0)
            {
                problem.startPos = file.getFilePointer();
            }
            file.writeInt(problem.NameLength);
            file.writeInt(problem.Grade);
            file.writeByte(problem.problemDescriptors);
            file.writeChars(problem.Name);
            file.writeInt(problem.NumberOfHolds);

            for (int index = 0; index < problem.NumberOfHolds; index++)
            {
                file.writeInt(problem.holdCoOrds.get(index)[0]);
                file.writeInt(problem.holdCoOrds.get(index)[1]);
                file.writeByte(problem.holdData.get(index)[0]);
                file.writeByte(problem.holdData.get(index)[1]);
            }
        }
        catch (IOException e)
        {
        }
    }

    public boolean nameExists(String problemName)
    {
        boolean exists = false;
        for (Problem p: problems)
        {
            if (p.Name.equals(problemName))
            {
                exists = true;
                break;
            }
        }
        return  exists;
    }

    private void updateNumberOfProblems()
    {
        try
        {
            file.seek(0);
            file.writeInt(problems.size());
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public void updateProblemData(Problem problem)
    {
        byte data = 0;
        data += (problem.IsFavourate) ? 1 << 7: 0 << 7;
        data += (problem.IsFont) ? 1 << 6: 0 << 6;
        data += (problem.IsTechnical) ? 1 << 5: 0 << 5;
        data += (problem.IsPowerful) ? 1 << 4: 0 << 4;
        data += (problem.IsSustained) ? 1 << 3: 0 << 3;
        data += (problem.IsCrimpy) ? 1 << 2: 0 << 2;
        data += (problem.IsComplete) ? 1 << 1: 0 << 1;
        problem.problemDescriptors = data;
        WriteProblem(problem, problems.indexOf(problem), true);
    }

    public void ClearProblems()
    {
        problems.clear();
    }

    public int getNumberOfProblems()
    {
        return numberOfProblems;
    }

    public ArrayList<Problem> getProblems()
    {
        return problems;
    }

    public ArrayList<Problem> getSortedProblems()
    {
        return sortedProblems;
    }

    public String getNameAt(int index)
    {
        return  problems.get(index).Name;
    }

    public String getImage()
    {
        return  Image;
    }

    public Problem getProblemAt(int index)
    {
        return problems.get(index);
    }

    public Problem getSortedProblemAt(int index)
    {
        return sortedProblems.get(index);
    }

    public int getIndexFromName(String name)
    {
        int index = 0;
        for (int i = 0; i < problems.size(); i++)
        {
            if (problems.get(i).Name.equals(name))
            {
                index = i;
                break;
            }
        }
        return index;
    }

}
