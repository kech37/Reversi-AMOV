package pt.isec.a21240456.a2120528.reversisec;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GameHistory extends AppCompatActivity {
	private boolean res = false;
	private Dialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_historic);
		
		
		dialog = new Dialog(this);
		
		List<GameRegist> gameScoresList = new ArrayList<>();
		
		File configFile = new File(getApplicationContext().getFilesDir().toString() + "/historicFile");
		if(configFile.exists()) {
			try {
				InputStream in = new FileInputStream(configFile);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder out = new StringBuilder();
				String line;
				while((line = reader.readLine()) != null) {
					String[] temp = line.toString().split(";");
					gameScoresList.add(new GameRegist(Integer.parseInt(temp[0]), temp[1], Integer.parseInt(temp[2]), temp[3], Integer.parseInt(temp[4])));
				}
				
				in.close();
				reader.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		
		ListView gameHistoricList = findViewById(R.id.gameHistoricList);
		
		AdapterGameHistoricList adapter = new AdapterGameHistoricList(this, gameScoresList);
		
		gameHistoricList.setAdapter(adapter);
	}
	
	public void onBtnDelete(View view){
		File file = new File(getApplicationContext().getFilesDir() + "/historicFile");
		if(file.exists()){
			showConfirmDeleteDialog(file);
		}else{
			Toast.makeText(getApplicationContext(), "Therens't nothing to delete!", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void showConfirmDeleteDialog(final File file){
		dialog.setContentView(R.layout.dialog_confirm_delete_history);
		
		Button yes = dialog.findViewById(R.id.btnDeleteYes);
		yes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				file.delete();
				dialog.dismiss();
				finish();
			}
		});
		
		Button no = dialog.findViewById(R.id.btnDeleteNo);
		no.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.show();
	}
}
