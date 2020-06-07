package com.MyBoard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class ImportProblems extends AppCompatActivity {

    private ArrayList<Problem> problems = new ArrayList<>();
    private TextView pa;

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scan_options);
    }

    public void scan(View view)
    {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    public void fromFile(View view)
    {
        try
        {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, 1);
        }

        catch (Exception ex)
        {
        }
        showBoards();
    }

    public void scanQRImage(Bitmap bMap)
    {
        String contents = null;

        int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();
        try
        {
            Result result = reader.decode(bitmap);
            contents = result.getText();

            if ((byte) contents.charAt(0) != Byte.MAX_VALUE)
            {
                Toast.makeText(getApplicationContext(), "QR code not valid!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                decodeQRcode(contents);
            }
        }

        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "ERROR READING QRCODE!", Toast.LENGTH_LONG).show();
        }
    }

    private void showBoards()
    {
        setContentView(R.layout.activity_import_problems);
        pa = (TextView) findViewById(R.id.problemsAdding);
        pa.setText("Problems adding: "+problems.size());
        if (Boards.boardHandle == null)
        {
            Boards.boardHandle = new BoardHandle(this.getApplicationContext());
        }
        Boards.boardHandle.sort(0);
        updateBoardsList();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                try
                {
                    final Uri imageUri = intent.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    scanQRImage(selectedImage);
                }
                catch (FileNotFoundException e)
                {
                }
            }

            else
            {
            }
        }
        else
        {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

            if (scanResult != null)
            {

                String output = scanResult.getContents();

                if ((byte) output.charAt(0) != Byte.MAX_VALUE)
                {
                    Toast.makeText(getApplicationContext(), "QR code not valid!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    decodeQRcode(output);
                }
            }
        }

        showBoards();
    }

    public void decodeQRcode(String input)
    {
        int numOfProblems = (int) ((byte) input.charAt(1));
        int ProblemOffset = 2;
        for (int i = 0; i < numOfProblems; i++)
        {
            Problem p = ReadProblems(input, ProblemOffset);
            problems.add(p);
            ProblemOffset += 13 + (p.NameLength * 2) + (p.NumberOfHolds * 10);
        }
    }

    private void updateBoardsList()
    {
        ListView lv = (ListView) findViewById(R.id.List);

        if (Boards.boardHandle.getSize() > 0)
        {
            final String[] names = new String[Boards.boardHandle.getSize()];

            for (int index = 0; index < Boards.boardHandle.getSize(); index++)
            {
                names[index] = Boards.boardHandle.getSortedNameAt(index);
            }
            CustomListLayout layout = new CustomListLayout(this, names);
            lv.setAdapter(layout);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Board board = Boards.boardHandle.getSortedBoardAt(position);
                    if  (!board.toImport)
                    {
                        board.toImport = true;
                        view.findViewById(R.id.layout).setBackgroundColor(0xff808588);
                    }
                    else
                    {
                        board.toImport = false;
                        view.findViewById(R.id.layout).setBackgroundColor(0xffEC407A);
                    }
            }
            });

        }
    }

    private Problem ReadProblems(String input, int pos)
    {
        int nameLen = getIntAt(pos, input);
        int grade = getIntAt((pos + 4), input);
        byte data = getByteAt((pos + 8), input);
        String name = getStringAt((pos + 9), input, nameLen);
        int noOfHolds = getIntAt((pos + 9 + (nameLen*2)), input);
        int startOfHolds = pos + 13 + (nameLen*2);
        Problem p = new Problem(name, grade, noOfHolds, nameLen, data);
        for (int i = 0; i < noOfHolds; i++)
        {
            int[] coOrds = new int[2];
            byte[] hd = new byte[2];
            coOrds[0] = getIntAt(startOfHolds + (i*10), input);
            coOrds[1] = getIntAt(startOfHolds + 4 + (i*10), input);
            hd[0] = getByteAt(startOfHolds + 8 + (i*10), input);
            hd[1] = getByteAt(startOfHolds + 9 + (i*10), input);
            p.holdCoOrds.add(coOrds);
            p.holdData.add(hd);
        }
        return p;
    }

    private int getIntAt(int pos, String input)
    {
        int integer = 0;
        byte[] data = new byte[4];
        data[0] = getByteAt(pos, input);
        data[1] = getByteAt((pos + 1), input);
        data[2] = getByteAt((pos + 2), input);
        data[3] = getByteAt((pos + 3), input);
        integer = (int) ((data[0] & 0xff) << 24 | (data[1] & 0xff) << 16 | (data[2] & 0xff) << 8 | (data[3] & 0xff) << 0);
        return integer;
    }

    private byte getByteAt(int pos, String input)
    {
        return (byte) input.charAt(pos);
    }

    private char getCharAt(int pos, String input)
    {
        char character;
        byte[] data = new byte[2];
        data[0] = getByteAt(pos, input);
        data[1] = getByteAt((pos + 1), input);
        character = (char) ((data[0] & 0xff) << 8 | (data[1] & 0xff) << 0);
        return character;
    }

    private String getStringAt(int pos, String input, int length)
    {
        String string = "";
        for (int i = 0; i < length; i++)
        {
           string += getCharAt((pos + (2*i)), input);
        }
        return string;
    }

    public void confirm(View view)
    {
        ArrayList<Board> boardslist = new ArrayList<>();

        for (int i = 0; i < Boards.boardHandle.getSortedBoards().size(); i++)
        {
            if (Boards.boardHandle.getSortedBoardAt(i).toImport)
            {
                boardslist.add(Boards.boardHandle.getSortedBoardAt(i));
            }
        }

        for (Board b: boardslist)
        {
            Problems.problemHandle = new ProblemHandle(b.name, this.getApplicationContext());
            for (Problem p: problems)
            {
                if (Problems.problemHandle.nameExists(p.Name))
                {
                    p.Name += "(copy)";
                    p.NameLength += 6;
                }
                Problems.problemHandle.CreateProblem(p);
            }
            Problems.problemHandle.sort(0);
        }

        Intent intent = new Intent(this, Boards.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        Problems.problemHandle.resetProblemFlags();
        Boards.boardHandle.resetBoardFlags();
        super.onBackPressed();
    }
}
