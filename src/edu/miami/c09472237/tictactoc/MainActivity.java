package edu.miami.c09472237.tictactoc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends Activity
implements View.OnClickListener {
	
	private static final int ACTIVITY_SELECT_PICTURE = 0;
	ImageView image1;
	ImageView image2;
	String playerName1;
	String playerName2;
	ImageView selectedImage;
	String playerUri1 = null;
	String playerUri2 = null;
	EditText editText1;
	EditText editText2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Assigning variables their corresponding xml element
		
		image1 = (ImageView)findViewById(R.id.playerImage1);
		image2 = (ImageView)findViewById(R.id.playerImage2);
		editText1 = (EditText)findViewById(R.id.playerName1);
		editText2 = (EditText)findViewById(R.id.playerName2);
		image1.setOnClickListener(this);
		image2.setOnClickListener(this);
		
	}
	
	public void myClickHandler(View view) {
		
		//Start button sends names from edit texts and uri of pics chosen, if chosen.
		
		switch(view.getId()) {
		case R.id.start_button:
			playerName1 = editText1.getText().toString();
			playerName2 = editText2.getText().toString();
			Intent nextActivity = new Intent();
			nextActivity.setClassName("edu.miami.c09472237.tictactoc", "edu.miami.c09472237.tictactoc.ScoreActivity");
			
			nextActivity.putExtra(
					"edu.miami.c09472237.tictactoc.player_image_1",playerUri1);
			nextActivity.putExtra(
					"edu.miami.c09472237.tictactoc.player_image_2",playerUri2);
			nextActivity.putExtra(
					"edu.miami.c09472237.tictactoc.player_name_1",playerName1);
			nextActivity.putExtra(
					"edu.miami.c09472237.tictactoc.player_name_2",playerName2);
			startActivity(nextActivity);
			break;
		default:
			break;
		}
	}
	
	//Method that is called when image clicked on. Brings up gallery intent.
	public void changeImage(View view) {
		Intent galleryIntent;
		
		selectedImage = (ImageView)view;
		
        galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,ACTIVITY_SELECT_PICTURE);
	}
	
	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data) {
		
		//Gets pic uri from gallery and assigns them to ImageView through bitmap.
				
		switch(requestCode) {
		case ACTIVITY_SELECT_PICTURE:
			if (resultCode == RESULT_CANCELED) {
				break;
			}
			ImageView pictureView;
			Bitmap selectedPicture;
			Uri selectedUri;
			
			pictureView = selectedImage;
			
			if (selectedImage == image1) {
				selectedUri = data.getData();
				playerUri1 = data.toUri(0);
			} else {
				selectedUri = data.getData();
				playerUri2 = data.toUri(0);
			}
			
			selectedUri = data.getData();
			try {
	            selectedPicture = MediaStore.Images.Media.getBitmap(
	this.getContentResolver(),selectedUri);
	            pictureView.setImageBitmap(selectedPicture);
	        } catch (Exception e) {
	                    
	        }
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		changeImage(v);
	}
}
