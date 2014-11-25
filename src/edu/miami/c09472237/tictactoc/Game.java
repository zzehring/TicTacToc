package edu.miami.c09472237.tictactoc;

import java.lang.reflect.Array;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

public class Game extends Activity {
	
	private int RED_WINS = 1;
	private int GREEN_WINS = 2;
	private int TIE = 3;
	
	String playerName1;
	String playerName2;
	Uri playerUri1;
	Uri playerUri2;
	boolean redTurn;
	Uri currentUri;
	String currentName;
	
	private int moveCount;
	
	private int[][] board;
	
	private int barTime;
	private final int CLICK_TIME = 100;
	ProgressBar myProgressBar;
	private boolean playerHasPlayed = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_layout);
		
		//Getting image uris
		if (this.getIntent().getStringExtra("edu.miami.c09472237.tictactoc.player_image_1") != null) {
			playerUri1 = Uri.parse(this.getIntent().getStringExtra("edu.miami.c09472237.tictactoc.player_image_1"));
		}
		if (this.getIntent().getStringExtra("edu.miami.c09472237.tictactoc.player_image_2") != null) {
			playerUri2 = Uri.parse(this.getIntent().getStringExtra("edu.miami.c09472237.tictactoc.player_image_2"));
		}
		
		//Getting player names
		playerName1 = this.getIntent().getStringExtra("edu.miami.c09472237.tictactoc.player_name_1");
        playerName2 = this.getIntent().getStringExtra("edu.miami.c09472237.tictactoc.player_name_2");
        
        //Getting turn and time to play
        barTime = this.getIntent().getIntExtra("edu.miami.c09472237.tictactoc.time", 10000);
        redTurn = this.getIntent().getBooleanExtra("edu.miami.c09472237.tictactoc.red_start", true);
        
        //Set up progress bar
        myProgressBar = (ProgressBar)findViewById(R.id.progress_bar);
        myProgressBar.setMax(barTime);
        myProgressBar.setProgress(myProgressBar.getMax());
        
        //Initialize board for victory check
        board = new int[3][3];
        for(int i=0; i < 3; i++) {
        	for (int j=0; j < 3; j++) {
        		board[i][j] = 0;
        	}
        }
        
        loadImages();
        
        startPlayer();
		
	}
	
	private void startPlayer() {
		
		ImageView image1 = (ImageView)findViewById(R.id.player_image_1);
		ImageView image2 = (ImageView)findViewById(R.id.player_image_2);
		
		playerHasPlayed = false;
		
		//Show name and picture of current player. Hide ImageView of other player.
		if (redTurn) {
			currentUri = playerUri1;
			currentName = playerName1;
			image1.setVisibility(ImageView.VISIBLE);
			image2.setVisibility(ImageView.INVISIBLE);
		} else {
			currentUri = playerUri2;
			currentName = playerName2;
			image1.setVisibility(ImageView.INVISIBLE);
			image2.setVisibility(ImageView.VISIBLE);
		}
		
		TextView text = (TextView)findViewById(R.id.player_name);
		text.setText(currentName);
		
		//Run progresser
		myProgresser.run();
	}
	
	//Load images. If no uri, display red and green drawables.
	private void loadImages() {
		
		ImageView playerImage1;
		ImageView playerImage2;
        Bitmap selectedPicture;
        
		playerImage1 = (ImageView)findViewById(R.id.player_image_1);
		playerImage2 = (ImageView)findViewById(R.id.player_image_2);
		
		if (playerUri1 != null) {
	        
	        try {
	            selectedPicture = MediaStore.Images.Media.getBitmap(
	this.getContentResolver(),playerUri1);
	            playerImage1.setImageBitmap(selectedPicture);
	        } catch (Exception e) {
	        	Log.e(DISPLAY_SERVICE, "Pic not displaying");
	        	finish();
	        }
		} else {
			playerImage1.setImageResource(R.drawable.red);
		}
        
		if (playerUri2 != null) {	        
	        try {
	            selectedPicture = MediaStore.Images.Media.getBitmap(
	this.getContentResolver(),playerUri2);
	            playerImage2.setImageBitmap(selectedPicture);
	        } catch (Exception e) {
	        	Log.e(DISPLAY_SERVICE, "Pic not displaying");
	        	finish();                  
	        }
		} else {
			playerImage2.setImageResource(R.drawable.green);
		}
	}
	
	public void myClickHandler(View v) {
		
		//Move count to check tie condition
		moveCount++;
		
		Bitmap selectedPicture;
		Bitmap resizePicture;
		TableRow parent;
		int row,column;
		
		ImageButton button = (ImageButton)v;
		
		parent = (TableRow)v.getParent();
		
		//Get location of play and put in board to check win condition. Use tags for position
		column = Integer.parseInt(v.getTag().toString());
		row = Integer.parseInt(parent.getTag().toString());
		
		//1 for red and -1 for green player. Can check +3 and -3 for win condition
		if (redTurn) {
			board[row][column] = 1;
		} else {
			board[row][column] = -1;
		}
		
		
		playerHasPlayed = true;
		
		if (currentUri != null) {
	        
	        try {
	            selectedPicture = MediaStore.Images.Media.getBitmap(
	this.getContentResolver(),currentUri);
	            resizePicture = Bitmap.createScaledBitmap(selectedPicture, 90, 90, true);
	            button.setImageBitmap(resizePicture);
	        } catch (Exception e) {
	        	Log.e(DISPLAY_SERVICE, "Pic not displaying");
	        	finish();
	        }
		} else if (redTurn) {
			button.setImageResource(R.drawable.red);
		} else {
			button.setImageResource(R.drawable.green);
		}
		button.setClickable(false);
		
		//Check end of game. If true, return winner or tie to Score Activity.
		if (endOfGame()) {
			Intent returnIntent = new Intent();
			returnIntent.setClassName("edu.miami.c09472237.tictactoc", "edu.miami.c09472237.tictactoc.ScoreActivity");
			if (moveCount >= 9) {
				setResult(TIE, returnIntent);
				finish();
			}
			if (redTurn) {
				setResult(RED_WINS, returnIntent);
				finish();
			} else {
				setResult(GREEN_WINS, returnIntent);
				finish();
			}
		}
	}
	
	private boolean endOfGame() {
		int i,j;
		int total = 0;
		
		//Check rows
		for (i = 0; i < 3; i++) {
	        total = 0;
	        for (j = 0; j < 3; j++) {
	            total += board[i][j];
	        }
	        if (total >= 3 || total <= -3) {
	            return true;
	        }
	    }
	 
	    // check columns
	    for (j = 0; j < 3; j++) {
	        total = 0;
	        for (i = 0; i < 3; i++) {
	            total += board[i][j];
	        }
	        if (total >= 3 || total <= -3) {
	            return true;
	        }
	    }
	 
	    // forward diagonal
	    total = 0;
	    for (i = 0; i < 3; i++) {
	        for (j = 0; j < 3; j++) {
	            if (i == j) {
	                total += board[i][j];
	            }
	        }
	    }
	    if (total >= 3 || total <= -3) {
            return true; // they win
        }
	 
	    // backward diagonal
	    total = 0;
	    for (i = 0; i < 3; i++) {
	        for (j = 0; j < 3; j++) {
	            if (i + j == 2) {
	                total += board[i][j];
	            }
	        }
	    }
	    if (total >= 3 || total <= -3) {
            return true; // they win
        }
	    
	    //Board filled. Tie
	    if (moveCount >= 9) {
	    	return true;
	    }
	    
	    return false;
		
	}
	
	@Override
	public void onBackPressed() {
		//So person can't cheat and press back if losing.
	}
	
//=============================================================================
	private final Runnable myProgresser = new Runnable() {
        
        private Handler myHandler = new Handler();
        
        public void run() {
            
        	//If player played, remove callbacks and reset bar. Else update bar.
        	if (playerHasPlayed) {
        		myHandler.removeCallbacksAndMessages(null);
        		myProgressBar.setProgress(0);
        	} else {
                myProgressBar.setProgress(myProgressBar.getProgress()-CLICK_TIME);
        	}
        	//If time runs out, switch turns, reset progress bar, and start player turn
        	if (myProgressBar.getProgress() == 0) {
        		redTurn = !redTurn;
        		myProgressBar.setProgress(myProgressBar.getMax());
        		startPlayer();
        		
        	} else {
        		myHandler.postDelayed(myProgresser,CLICK_TIME);
        	}
            
        }
    };
}
