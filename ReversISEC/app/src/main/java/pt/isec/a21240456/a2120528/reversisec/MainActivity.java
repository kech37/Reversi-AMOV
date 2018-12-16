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
	private EditText etInputText, etServerIP;
	private ImageView ivClosePopupLose, ivClosePopupLoseMultiplayer, ivClosePopupConnectionType, ivClosePopupServerIP;
	private String player2Name;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dialog = new Dialog(this);
	}
	
	public void onSinglePlayer(View view) {
		Intent intent = new Intent(this, PlayGame.class);
		if(!checkConfigFile(intent)) {
			showProfileWarningDialog();
		} else {
			intent.putExtra("mode", PlayGame.SINGLE_PLAYER);
			startActivity(intent);
		}
	}
	
	public void onMultiplayer(View view) {
		dialog.setContentView(R.layout.dialog_choose_multiplayer);
		ivClosePopupLoseMultiplayer = dialog.findViewById(R.id.ivClosePopupLoseMultiplayer);
		ivClosePopupLoseMultiplayer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		btnLocalMultiplayer = dialog.findViewById(R.id.btnLocalMultiplayer);
		btnLocalMultiplayer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
				showPlayername2Input();
			}
		});
		btnNetworkMultiplayer = dialog.findViewById(R.id.btnNetworkMultiplayer);
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
	
	private void showPlayername2Input() {
		dialog.setContentView(R.layout.dialog_input_playername);
		ivClosePopupLose = dialog.findViewById(R.id.ivClosePopupLose);
		dialogOkbtn = dialog.findViewById(R.id.dialogOkbtn);
		etInputText = dialog.findViewById(R.id.etInputText);
		
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
				if(!checkConfigFile(intent)) {
					showProfileWarningDialog();
				} else {
					intent.putExtra("mode", PlayGame.LOCAL_MULTYPLAYER);
					intent.putExtra("player2Name", player2Name);
					startActivity(intent);
				}
			}
		});
		
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.show();
	}
	
	private void showConnectionType() {
		dialog.setContentView(R.layout.dialog_choose_server_client);
		ivClosePopupConnectionType = dialog.findViewById(R.id.ivClosePopupConnectionType);
		ivClosePopupConnectionType.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		btnCreateServer = dialog.findViewById(R.id.btnCreateServer);
		btnCreateServer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
				multiplayerActivityCreate(true);
			}
		});
		btnJoinServer = dialog.findViewById(R.id.btnJoinServer);
		btnJoinServer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
				startClientDialog();
			}
		});
		
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.show();
	}

	private void startClientDialog() {
		dialog.setContentView(R.layout.dialog_input_server_ip);
		ivClosePopupServerIP = dialog.findViewById(R.id.ivClosePopupServerIP);
		Button btnConnect = dialog.findViewById(R.id.btnConnect);
		etServerIP = dialog.findViewById(R.id.etServerIP);

		ivClosePopupServerIP.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
				finish();
			}
		});

		btnConnect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
				multiplayerActivityCreate(false);
			}
		});

		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.show();
	}
	
	private void multiplayerActivityCreate(boolean createServer) {
		Intent intent = new Intent(getApplicationContext(), PlayGame.class);
		if(!checkConfigFile(intent)) {
			showProfileWarningDialog();
		} else {
			intent.putExtra("mode", PlayGame.NETWORK_MULTYPLAYER);
			intent.putExtra("createServer", createServer);
			startActivity(intent);
		}
	}
	
	private void showProfileWarningDialog() {
		dialog.setContentView(R.layout.dialog_warning_profile);
		Button btnWarningProfile = dialog.findViewById(R.id.btnWarningProfile);
		
		btnWarningProfile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		
		
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.show();
	}
	
	public void onProfile(View view) {
		Intent intent = new Intent(this, ConfigProfile.class);
		checkConfigFile(intent);
		startActivity(intent);
	}
	
	private boolean checkConfigFile(Intent intent) {
		File configFile = new File(getApplicationContext().getFilesDir().toString() + "/configs");
		if(configFile.exists()) {
			try {
				InputStream in = new FileInputStream(configFile);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder out = new StringBuilder();
				String line;
				while((line = reader.readLine()) != null) {
					out.append(line);
				}
				String[] temp = out.toString().split(";");
				
				intent.putExtra("playername", temp[0]);
				intent.putExtra("profilepicturepath", temp[1]);
				
				in.close();
				reader.close();
				return true;
			} catch(IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}
	
	public void onOlderGames(View view) {
		Intent intent = new Intent(this, GameHistory.class);
		startActivity(intent);
	}
}
