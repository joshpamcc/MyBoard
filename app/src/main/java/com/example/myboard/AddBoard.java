package com.example.myboard;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class AddBoard extends AppCompatActivity
{
    private static int RESULT_LOAD_IMAGE = 1;
    private static String ImageURI = "";
    private static boolean isFav = false;
    private ProblemHandle ph;
    private ArrayList<Problem> problemList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_board);
        ph = null;
        problemList = null;
        if (Boards.boardHandle.EditingBoard != null)
        {
            edit();
        }
    }

    public void CreateBoard(View view)
    {
        Intent intent = new Intent(this, Boards.class);
        EditText editText = (EditText) findViewById(R.id.AddBoardName);
        String BoardName = editText.getText().toString();

        if (ImageURI.length() > 1)
        {
            byte data = 0;
            data += (isFav) == true ? 1 << 7: 0 << 7;

            if (BoardName.length() < 32 && BoardName.length() > 0)
            {
                if (Boards.boardHandle.nameExists(BoardName) && Boards.boardHandle.EditingBoard == null)
                {
                    Toast.makeText(getApplicationContext(), "A board already exists with that name!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (Boards.boardHandle.EditingBoard != null)
                    {
                        Boards.boardHandle.deleteBoard(Boards.boardHandle.EditingBoard.name);
                    }
                    Boards.boardHandle.CreateBoard(BoardName.length(), data, BoardName, ImageURI);
                    Boards.loadedBoard = BoardName;
                    if (Boards.boardHandle.EditingBoard != null)
                    {
                        Problems.problemHandle.CopyProblems(problemList);
                        Boards.boardHandle.EditingBoard = null;
                    }
                    startActivity(intent);
                }
            }
            else
            {
                if (BoardName.length() == 0)
                {
                    Toast.makeText(getApplicationContext(), "Enter a board name!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Board names must be under 32 characters!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        else
        {
            Toast.makeText(getApplicationContext(),"Pick an image for the board!", Toast.LENGTH_SHORT).show();
        }

    }

    public void AddImage(View view)
    {
        try
        {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }

        catch (Exception ex)
        {
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data)
    {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            try
            {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ImageView Image =  (ImageView) this.findViewById(R.id.BoardImage);
                Image.setImageBitmap(selectedImage);
                ImageURI = imageUri.toString();
            }
            catch (FileNotFoundException e)
            {
            }
        }

        else
        {
        }
    }

    private void edit()
    {
        ph = new ProblemHandle(Boards.boardHandle.EditingBoard.name, getApplicationContext());
        loadImage();
        for (int i = 0; i < ph.getNumberOfProblems(); i++)
        {
            ph.ReadHolds(ph.getProblemAt(i));
        }
        problemList = new ArrayList<>(ph.getProblems());
        EditText editText = (EditText) this.findViewById(R.id.AddBoardName);
        editText.setText(Boards.boardHandle.EditingBoard.name);
        ImageButton FavButton = (ImageButton) this.findViewById(R.id.IsFavourateBoard);
        if(Boards.boardHandle.EditingBoard.isFavourate)
        {
            FavButton.setImageResource(R.drawable.button_star_on);
            isFav = true;
        }
        else
        {
            FavButton.setImageResource(R.drawable.button_star_off);
            isFav = false;
        }
    }

    private void loadImage()
    {
        ImageView Image =  (ImageView) this.findViewById(R.id.BoardImage);
        try
        {
            Uri imageUri = Uri.parse(ph.getImage());
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap image = BitmapFactory.decodeStream(imageStream);
            Image.setImageBitmap(image);
        }
        catch (FileNotFoundException e)
        {
        }
    }

    public void updateFavourateButton(View view)
    {
        ImageButton FavButton = (ImageButton) findViewById(R.id.IsFavourateBoard);
        if(!isFav)
        {
            FavButton.setImageResource(R.drawable.button_star_on);
            isFav = true;
        }
        else
        {
            FavButton.setImageResource(R.drawable.button_star_off);
            isFav = false;
        }
    }

    @Override
    public void onBackPressed() {
        Boards.boardHandle.EditingBoard = null;
        super.onBackPressed();
    }
}
