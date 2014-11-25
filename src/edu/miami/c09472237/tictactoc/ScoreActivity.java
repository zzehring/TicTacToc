package edu.miami.c09472237.tictactoc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class ScoreActivity extends Activity {
	
	private final int RED_WINS = 1;
	private final int GREEN_WINS = 2;
	private final int TIE = 3;
	
	String playerName1;
	String playerName2;
	String playerUri1;
	String playerUri2;
	Uri selectedURI;
	int time = 5000;
	int player1Score = 0;
	int player2Score = 0;
	double dividingLine = 0.5;
	RatingBar rate;
	RatingBar rate2;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.score_screen_layout);
		
		//Loads images. If uris null, put red and green drawable.
		loadImages();
        
		//Get names
        playerName1 = this.getIntent().getStringExtra("edu.miami.c09472237.tictactoc.player_name_1");
        playerName2 = this.getIntent().getStringExtra("edu.miami.c09472237.tictactoc.player_name_2");
        
        //Set names
        TextView playerText1 = (TextView)findViewById(R.id.playerName1);
        TextView playerText2 = (TextView)findViewById(R.id.playerName2);
        
        //Set up rating bars
        rate = (RatingBar)findViewById(R.id.playerScore1);
        rate2 = (RatingBar)findViewById(R.id.playerScore2);
        if (playerName1.equals("")) {
        	playerName1 = "No one";
        }
        if (playerName2.equals("")) {
        	playerName2 = "No one";
        }
        playerText1.setText(playerName1);
        playerText2.setText(playerName2);
	}
	
	private void loadImages() {
		
		ImageView playerImage1;
		ImageView playerImage2;
        Bitmap selectedPicture;
        
		playerImage1 = (ImageView)findViewById(R.id.playerImage1);
		playerImage2 = (ImageView)findViewById(R.id.playerImage2);
		
		if (this.getIntent().getStringExtra("edu.miami.c09472237.tictactoc.player_image_1") != null) {
			playerUri1 = this.getIntent().getStringExtra("edu.miami.c09472237.tictactoc.player_image_1");
	        selectedURI = Uri.parse(playerUri1);
	        
	        try {
	            selectedPicture = MediaStore.Images.Media.getBitmap(
	this.getContentResolver(),selectedURI);
	            playerImage1.setImageBitmap(selectedPicture);
	        } catch (Exception e) {
	        	Log.e(DISPLAY_SERVICE, "Pic not displaying");
	        	finish();
	        }
		}
        
		if (this.getIntent().getStringExtra("edu.miami.c09472237.tictactoc.player_image_2") != null) {
			playerUri2 = this.getIntent().getStringExtra("edu.miami.c09472237.tictactoc.player_image_2");
	        selectedURI = Uri.parse(playerUri2);
	        
	        try {
	            selectedPicture = MediaStore.Images.Media.getBitmap(
	this.getContentResolver(),selectedURI);
	            playerImage2.setImageBitmap(selectedPicture);
	        } catch (Exception e) {
	        	Log.e(DISPLAY_SERVICE, "Pic not displaying");
	        	finish();                  
	        }
		}
		
	}

	public void myClickHandler(View v) {
		
		boolean redStart;
		
		//On press start, start game activity for result and attach necessary info.
		switch(v.getId()) {
		case R.id.start_button:
			redStart = greenGoFirst();
			Intent nextActivity = new Intent();
			nextActivity.setClassName("edu.miami.c09472237.tictactoc", "edu.miami.c09472237.tictactoc.Game");
			nextActivity.putExtra(
					"edu.miami.c09472237.tictactoc.player_image_1",playerUri1);
			nextActivity.putExtra(
					"edu.miami.c09472237.tictactoc.player_image_2",playerUri2);
			nextActivity.putExtra(
					"edu.miami.c09472237.tictactoc.player_name_1",playerName1);
			nextActivity.putExtra(
					"edu.miami.c09472237.tictactoc.player_name_2",playerName2);
			nextActivity.putExtra(
					"edu.miami.c09472237.tictactoc.red_start", redStart);
			nextActivity.putExtra("edu.miami.c09472237.tictactoc.time", time);
			startActivityForResult(nextActivity, 1);
		default:
			break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		//Loading menu

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout,menu);
        return(true);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		
		//Menu items. Reset and set times for game.
                
        switch (item.getItemId()) {
        case R.id.one_second:
            time = 1000;
            return(true);
        case R.id.two_seconds:
            time = 2000;
            return(true);
        case R.id.five_seconds:
            time = 5000;
            return(true);
        case R.id.ten_seconds:
            time = 10000;
            return(true);
        case R.id.reset:
        	
        	//Basically initializes the game except for names and pics
        	
            player1Score = 0;
            player2Score = 0;
            time = 10000;
            dividingLine = 0.5;
            rate.setRating(player1Score);
            rate2.setRating(player2Score);
            Button startButton = (Button)findViewById(R.id.start_button);
			startButton.setVisibility(Button.VISIBLE);
			startButton.setClickable(true);
            return(true);
        default:
            return(super.onOptionsItemSelected(item));
        }
    }
	
	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data) {
		
		//Updating rating bar after a win or nothing after a tie. If RatingBar full, hide start button.
		
		switch(resultCode) {
		case RED_WINS:
			if (player1Score <= 4) {
				rate.setRating(++player1Score);
			}
			break;
		case GREEN_WINS:
			if (player2Score <= 4) {
				rate2.setRating(++player2Score);
			}
			break;
		case TIE:
			break;
		default:
			break;
		}
		if (player1Score >= 5 || player2Score >= 5) {
			Button startButton = (Button)findViewById(R.id.start_button);
			startButton.setVisibility(Button.INVISIBLE);
			startButton.setClickable(false);
		}
	}
	
	//Method that picks who goes first. Designed so 1 person can't keep being chosen.
	private boolean greenGoFirst() {
		double number = Math.random();
		
		if (number < dividingLine) {
			dividingLine =- 0.1;
			return true;
		} else {
			dividingLine =+ 0.1;
			return false;
		}
	}
	
}
