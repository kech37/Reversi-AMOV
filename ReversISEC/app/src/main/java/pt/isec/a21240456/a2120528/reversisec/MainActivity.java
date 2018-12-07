package pt.isec.a21240456.a2120528.reversisec;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private Dialog dialog;
    private Button dialogOkbtn, btnLocalMultiplayer, btnNetworkMultiplayer, btnCreateServer, btnJoinServer;
    private EditText etInputText;
    private ImageView ivClosePopupLose, ivClosePopupLoseMultiplayer, ivClosePopupConnectionType;
    private String player2Name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new Dialog(this);
    }

    public void onSinglePlayer(View view){
        Intent intent = new Intent(this, PlayGame.class);
        checkConfigFile(intent);
        intent.putExtra("mode", PlayGame.SINGLE_PLAYER);
        startActivity(intent);
    }

    public void onMultiplayer(View view){
        dialog.setContentView(R.layout.dialog_choose_multiplayer);
        ivClosePopupLoseMultiplayer = (ImageView) dialog.findViewById(R.id.ivClosePopupLoseMultiplayer);
        ivClosePopupLoseMultiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnLocalMultiplayer  = (Button) dialog.findViewById(R.id.btnLocalMultiplayer);
        btnLocalMultiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showPlayername2Input();
            }
        });
        btnNetworkMultiplayer = (Button) dialog.findViewById(R.id.btnNetworkMultiplayer);
        btnNetworkMultiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showConnectionType();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void showPlayername2Input(){
        dialog.setContentView(R.layout.dialog_input_playername);
        ivClosePopupLose = (ImageView) dialog.findViewById(R.id.ivClosePopupLose);
        dialogOkbtn = (Button) dialog.findViewById(R.id.dialogOkbtn);
        etInputText = (EditText)  dialog.findViewById(R.id.etInputText);

        ivClosePopupLose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialogOkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player2Name = etInputText.getText().toString();
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), PlayGame.class);
                checkConfigFile(intent);
                intent.putExtra("mode", PlayGame.LOCAL_MULTYPLAYER);
                intent.putExtra("player2Name", player2Name);
                startActivity(intent);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void showConnectionType(){
        dialog.setContentView(R.layout.dialog_choose_server_client);
        ivClosePopupConnectionType = (ImageView) dialog.findViewById(R.id.ivClosePopupConnectionType);
        ivClosePopupConnectionType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnCreateServer  = (Button) dialog.findViewById(R.id.btnCreateServer);
        btnCreateServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                multiplayerActivityCreate(true);
            }
        });
        btnJoinServer = (Button) dialog.findViewById(R.id.btnJoinServer);
        btnJoinServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                multiplayerActivityCreate(false);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void multiplayerActivityCreate(boolean createServer){
        Intent intent = new Intent(getApplicationContext(), PlayGame.class);
        checkConfigFile(intent);
        intent.putExtra("mode", PlayGame.NETWORK_MULTYPLAYER);
        intent.putExtra("createServer", createServer);
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
