package com.MyBoard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;


import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;

public class ShowCode extends AppCompatActivity {

    private ImageButton forward;
    private ImageButton backward;
    private ImageView QRcode;
    private ArrayList<Bitmap> codelist = new ArrayList<>();
    private int currentCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_code);
        forward = (ImageButton) findViewById(R.id.NextCode);
        backward = (ImageButton) findViewById(R.id.LastQRcode);
        QRcode = (ImageView) findViewById(R.id.QRcode);
        codelist = ExportProblems.codeList;
        if (codelist.size() == 1)
        {
            forward.setVisibility(View.INVISIBLE);
            backward.setVisibility(View.INVISIBLE);
        }

        else
        {
            forward.setVisibility(View.VISIBLE);
            backward.setVisibility(View.VISIBLE);
        }

        QRcode.setImageBitmap(codelist.get(currentCode));
    }

    public void nextCode(View view)
    {
        currentCode++;
        if(currentCode >= codelist.size())
        {
            currentCode = 0;
        }
        QRcode.setImageBitmap(codelist.get(currentCode));
    }

    public void lastCode(View view)
    {
        currentCode--;
        if(currentCode < 0)
        {
            currentCode = (codelist.size()-1);
        }
        QRcode.setImageBitmap(codelist.get(currentCode));
    }

    public void done(View view)
    {
        codelist.clear();
        ExportProblems.codeList.clear();
        Intent intent = new Intent(this, Problems.class);
        intent.putExtra("board", Boards.loadedBoard);
        startActivity(intent);
    }

    public void share(View view)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, getImageUri(this.getApplicationContext(), codelist.get(currentCode)));
        try {
            startActivity(Intent.createChooser(intent, "qr codes.."));
        } catch (android.content.ActivityNotFoundException ex) {

            ex.printStackTrace();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onBackPressed()
    {
        Problems.problemHandle.resetProblemFlags();
        super.onBackPressed();
    }
}
