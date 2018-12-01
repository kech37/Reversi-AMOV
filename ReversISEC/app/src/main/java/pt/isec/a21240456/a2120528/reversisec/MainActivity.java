package pt.isec.a21240456.a2120528.reversisec;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onSinlgePlayer(View view){
        Intent intent = new Intent(this, SinglePlayer.class);
        startActivity(intent);
    }
}
