package pt.isec.a21240456.a2120528.reversisec;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

public class AdapterGameHistoricList extends BaseAdapter {

    private final List<GameRegist> list;
    private final Activity act;

    public AdapterGameHistoricList(Activity act, List<GameRegist> list) {
        this.act = act;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View myView = act.getLayoutInflater().inflate(R.layout.list_item_game_historic, viewGroup, false);
        GameRegist gameRegist = list.get(i);
        LinearLayout llList = myView.findViewById(R.id.llList);

        if (i % 2 == 0) {
            llList.setBackgroundColor(Color.parseColor("#90CAF9"));
        }else{
            llList.setBackgroundColor(Color.parseColor("#BBDEFB"));
        }


        TextView player1Name = myView.findViewById(R.id.tvPlayer1Name);
        TextView player1Score = myView.findViewById(R.id.tvPlayer1Score);
        TextView player2Name = myView.findViewById(R.id.tvPlayer2Name);
        TextView player2Score = myView.findViewById(R.id.tvPlayer2Score);
        TextView tvGameMode = myView.findViewById(R.id.tvGameMode);

        if(gameRegist.getPlayer1Score() > gameRegist.getPlayer2Score()){
            player1Name.setTypeface(null, Typeface.BOLD);
            player1Score.setTypeface(null, Typeface.BOLD);
        }else if(gameRegist.getPlayer1Score() < gameRegist.getPlayer2Score()){
            player2Name.setTypeface(null, Typeface.BOLD);
            player2Score.setTypeface(null, Typeface.BOLD);
        }

        switch (gameRegist.getGameMode()){
            case GameRegist.SINGLE_PLAY:
                tvGameMode.setText(myView.getResources().getText(R.string.result_single_player));
                break;
            case GameRegist.LOCAL_MULTIPLAYER:
                tvGameMode.setText(myView.getResources().getText(R.string.result_multiplayer_local));
                break;
            case GameRegist.NETWORK_MULTIPLAYER:
                tvGameMode.setText(myView.getResources().getText(R.string.result_multiplayer_network));
                break;
        }

        player1Name.setText(gameRegist.getPlayer1Name());
        player1Score.setText("" + gameRegist.getPlayer1Score());
        player2Name.setText(gameRegist.getPlayer2Name());
        player2Score.setText("" + gameRegist.getPlayer2Score());

        return myView;
    }
}
