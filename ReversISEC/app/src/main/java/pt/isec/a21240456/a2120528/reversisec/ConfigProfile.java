package pt.isec.a21240456.a2120528.reversisec;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
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
    private Bitmap profileBitmap;
    private EditText etPlayerName;
    private boolean changedPicture;

    private Dialog dialog;
    private ImageView ivClosePopupCopyright;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_profile);

        ivProfilePicture = findViewById(R.id.ivProfile);
        etPlayerName = findViewById(R.id.etPlayerName);

        changedPicture = false;

        if(getIntent().hasExtra("playername") && getIntent().hasExtra("profilepicturepath")){
            etPlayerName.setText(getIntent().getStringExtra("playername"));
            if(!getIntent().getStringExtra("profilepicturepath").equalsIgnoreCase("<none>")){
                File imgFile = new File(getIntent().getStringExtra("profilepicturepath"));
                if(imgFile.exists()){
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ivProfilePicture.setImageBitmap(bitmap);
                }
            }
        }

        dialog = new Dialog(this);
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
            changedPicture = true;
        }
    }

    public void onSaveProfile(View view){
        try {
            OutputStream stream;
            String picturePath;
            if(changedPicture){
                File picture = new File(getApplicationContext().getFilesDir(), "profilePicture.png");
                stream = new FileOutputStream(picture);
                profileBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                picturePath = picture.getAbsolutePath();
                stream.flush();
                stream.close();
            }else if(getIntent().hasExtra("profilepicturepath")){
                picturePath = getIntent().getStringExtra("profilepicturepath");
            }else{
                picturePath = "<none>";
            }
            File configFile = new File(getApplicationContext().getFilesDir(), "configs");
            stream = new FileOutputStream(configFile);
            stream.write((etPlayerName.getText().toString() + ";" + picturePath).getBytes());
            stream.flush();
            stream.close();
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.profile_saved_successfully) + "!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.something_went_wrong_while_saving_the_profile) + "!", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public void onCopyright(View view){
        dialog.setContentView(R.layout.dialog_credits);
        ivClosePopupCopyright = (ImageView) dialog.findViewById(R.id.ivClosePopupCopyright);

        ivClosePopupCopyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
