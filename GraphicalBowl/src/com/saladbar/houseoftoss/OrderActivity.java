package com.saladbar.houseoftoss;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.speech.RecognizerIntent;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ResolveInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.speech.RecognizerIntent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class OrderActivity extends Activity {
	public static String EXTRA_SALAD = "salad";
	
	private final String FILE_NAME = "orders.txt";
	
	private String[] toppings =  new String[]{"Spinach",
            "Lettuce",  
			"Egg",
            "Ham",
            "Bacon",
            "Kidney Beans",
            "Shrimp",
            "Garbanzo Beans",
            "Chicken",
            "Applesauce",
            "Artichokes",
            "Olives",
            "Broccoli",
            "Cauliflower",
            "Cherries",
            "Tomatoes",
            "Corn",
            "Cottage Cheese",
            "Craisins",
            "Noodles",
            "Croutons",
            "Feta Cheese",
            "Gelatin",
            "Hummus",
            "Jalapenos",
            "Carrots",
            "Mushrooms",
            "Parmesan",
            "Peas",
            "Pepperoncini",
            "Beets",
            "Radishes",
            "Raisins",
            "Peppers",
            "Mozzarella",
            "Monterey Jack",
            "Cucumbers",
            "Peaches",
            "Red Onion",
            "Prunes",
            "Strawberry Whip",
            "Sunflower Seeds"};
	
	private static final int MENU_VOICE = Menu.FIRST;
	private static final int VOICE_REQUEST_CODE = 1;
	private static final int SALAD_REQUEST_CODE = 2;
	
	public SaladArrayAdapter mAdapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Order History");
        setContentView(R.layout.activity_main);
        this.mAdapter = new SaladArrayAdapter(this);
        ListView view = (ListView) findViewById(R.id.orders);
        view.setAdapter(this.mAdapter);
        view.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
        	public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final Salad salad = (Salad)mAdapter.getItem(position);
        		AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderActivity.this);
        		alertDialog.setTitle("Delete")
        		.setMessage("Do you want to remove this order?")
        		.setCancelable(false)
        		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mAdapter.remove(salad);
					}
				})
        		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int id) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});
        		
        		AlertDialog alert = alertDialog.create();
        		alert.show();
        	}
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if(hasVoiceRecognition()){ /*if voice recognition is available add ability to order by voice*/
        	MenuItem item = menu.add(0, MENU_VOICE, Menu.NONE, "Voice");
            item.setIcon(R.drawable.ic_action_microphone);
            item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
            	    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            	    intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
            	    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            	    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            	    startActivityForResult(intent, VOICE_REQUEST_CODE);
                    return true;
                }
            });
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        
        MenuItem item = menu.add(0, MENU_VOICE, Menu.NONE, "Voice");
        item.setIcon(R.drawable.plus);
        item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
            	LaunchSaladCreator(new Salad());
                return true;
            }
        });
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        
        return super.onPrepareOptionsMenu(menu);
    }
    
    private void LaunchSaladCreator(Salad salad){
	    Intent intent = new Intent(this, AssemblyActivity.class);
	    intent.putExtra(EXTRA_SALAD, salad.getToppings());
	    startActivityForResult(intent, SALAD_REQUEST_CODE);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_REQUEST_CODE){
        	Salad salad = new Salad();
        	if(resultCode == RESULT_OK) {
	        	String sent = "";
	        	ArrayList<String> sentence = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	        	for(int i =0; i < sentence.size(); i++){
	        		String word = sentence.get(i);
	        		sent += word + " ";
	        	}
	        	for(int i =0; i < toppings.length; i++){
	        		String topping = toppings[i];
	        		if(sent.contains(topping.toLowerCase())){
	        			salad.toggleSaladTopping(topping);
	        		}
	        	}
	        	LaunchSaladCreator(salad);
	        	
        	}
	        else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){ }
	        else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){ }
	        else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){ }
	        else if(resultCode == RecognizerIntent.RESULT_NO_MATCH){ }
	        else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR){ }
        }
        
        if (requestCode == SALAD_REQUEST_CODE){
        	if(resultCode == RESULT_OK) {
        	    ArrayList<String> tops = data.getStringArrayListExtra(EXTRA_SALAD);
        	    Bitmap icon = (Bitmap) data.getParcelableExtra("image");
        	    
        	    mAdapter.add(new Salad(tops, icon));
        	    
        	    // todo save image
        	    
        	}
        }
        
	    super.onActivityResult(requestCode, resultCode, data);
    }
    		
    public Boolean hasVoiceRecognition() {
	    PackageManager pm = getPackageManager();
	    List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
	    if (activities.size() == 0) {
	       return false;
        }
	    return true;
    }
    
    @Override
    public void onResume() {
            super.onResume();
            // Load saved ToDoItems, if necessary
            if (mAdapter.getCount() == 0)
                    loadItems();
    }

    @Override
    protected void onPause() {
            super.onPause();

            // Save ToDoItems
            saveItems();
    }
    
    private void loadItems() {
        BufferedReader reader = null;
        try {
                FileInputStream fis = openFileInput(FILE_NAME);
                reader = new BufferedReader(new InputStreamReader(fis));

                String salad = null;
                String time = null;
                int idx = 0;
                while (null != (salad = reader.readLine())) {
                	    time = reader.readLine();
                	    Salad salObj = new Salad();
                	    salObj.setDate(Long.parseLong(time));
                	    String[] temp = salad.split(", ");
                	    for(int i =0; i < temp.length; i++){
                	    	salObj.toggleSaladTopping(temp[i]);
                	    }
                	    String filename = "bitmap" + idx + ".png";
                	    FileInputStream is = this.openFileInput(filename);
                	    Bitmap bitmap = BitmapFactory.decodeStream(is);
                	    salObj.setIcon(bitmap);
                	    
                        mAdapter.add(salObj);
                        idx++;
                }

        } catch (FileNotFoundException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        } finally {
                if (null != reader) {
                        try {
                                reader.close();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
        }
    }
    
    private void saveItems() {
        PrintWriter writer = null;
        try {
                FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
                writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                                fos)));

                for (int idx = 0; idx < mAdapter.getCount(); idx++) {
                        writer.println(mAdapter.getItem(idx));
            		    //Write file
                        Bitmap toWrite = ((Salad)mAdapter.getItem(idx)).getIcon();
            		    String filename = "bitmap" + idx + ".png";
            		    FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            		    toWrite.compress(Bitmap.CompressFormat.PNG, 100, stream);
            		    stream.close();
                }
        } catch (IOException e) {
                e.printStackTrace();
        } finally {
                if (null != writer) {
                        writer.close();
                }
        }
    }
}
