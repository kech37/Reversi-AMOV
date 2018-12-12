package pt.isec.a21240456.a2120528.reversisec;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GameHistoric extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_historic);
		
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
}
