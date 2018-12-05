package pt.isec.a21240456.a2120528.reversisec;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onSinglePlayer(View view){
        Intent intent = new Intent(this, PlayGame.class);
        checkConfigFile(intent);
        intent.putExtra("mode", PlayGame.SINGLE_PLAYER);
        startActivity(intent);
    }

    public void onProfile(View view){
        Intent intent = new Intent(this, ConfigProfile.class);
        checkConfigFile(intent);
        startActivity(intent);
    }

    private void checkConfigFile(Intent intent){
        File configFile = new File(getApplicationContext().getFilesDir().toString() + "/configs");
        if(configFile.exists()){
            try{
                InputStream in = new FileInputStream(configFile);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder out = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
                String[] temp = out.toString().split(";");

                intent.putExtra("playername", temp[0]);
                intent.putExtra("profilepicturepath", temp[1]);

                in.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
