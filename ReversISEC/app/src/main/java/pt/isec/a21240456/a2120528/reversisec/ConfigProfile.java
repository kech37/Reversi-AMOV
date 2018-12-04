package pt.isec.a21240456.a2120528.reversisec;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ConfigProfile extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView ivProfilePicture;
    Bitmap profileBitmap;
    private EditText etPlayerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_profile);

        ivProfilePicture = findViewById(R.id.ivProfile);
        etPlayerName = findViewById(R.id.etPlayerName);

        File file = new File(getApplicationContext().getFilesDir().toString() + "/configs");
        if(file.exists()){
            try{
                InputStream in = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder out = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
                String[] temp = out.toString().split(";");
                etPlayerName.setText(temp[0]);

                File imgFile = new File(temp[1]);
                if(imgFile.exists()){
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ivProfilePicture.setImageBitmap(bitmap);
                }
                in.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            profileBitmap = (Bitmap) data.getExtras().get("data");
            ivProfilePicture.setImageBitmap(profileBitmap);
        }
    }

    public void onTakePicture(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void onSaveProfile(View view){
        try {
            File picture = new File(getApplicationContext().getFilesDir(), "profilePicture.png");

            OutputStream stream = new FileOutputStream(picture);
            profileBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            stream.flush();
            stream.close();

            File configFile = new File(getApplicationContext().getFilesDir(), "configs");
            stream = new FileOutputStream(configFile);
            stream.write((etPlayerName.getText().toString() + ";" + picture.getAbsolutePath()).getBytes());
            stream.flush();
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
