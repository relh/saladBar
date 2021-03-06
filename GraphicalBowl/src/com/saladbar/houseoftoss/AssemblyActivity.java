package com.saladbar.houseoftoss;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class AssemblyActivity extends Activity implements SensorEventListener {

    public HashMap<String, ImageView> items;
    public GridLayout layout;
    int calories = 0;
    TextView calorieView;
    double price = 0;
    TextView priceView;
    
    //DISPLAY REQUEST CODE
    public static final int DISPLAY_REQUEST_CODE = 10;
    
    // Shake variables
    private static final int SHAKE_THRESHOLD = 800;
    private float x, y, z, last_x, last_y, last_z;
    long curTime;
    long lastUpdate = 0;
    boolean shakeStarted = false;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Shake your phone to place an order");
        setContentView(R.layout.activity_assembly);
 
        // HEADERS        
        calories = 0;
        calorieView = (TextView) findViewById(R.id.calorieView);
        calorieView.setText("0 calories");
        
        price = 0;
        priceView = (TextView) findViewById(R.id.priceView);
        priceView.setText("$0.00");
        
        // IMAGES
        layout = (GridLayout) findViewById(R.id.graphicLayout);
        
        // LISTVIEWS
        final ListView baseList = (ListView) findViewById(R.id.base);
        final ListView proteinList = (ListView) findViewById(R.id.protein);
        final ListView toppingList = (ListView) findViewById(R.id.toppings);

        // Defined Array values to show in ListView
        String[] bases = new String[]{
                "Spinach",
                "Lettuce"
        };
        String[] proteins = new String[]{
                "Egg",
                "Ham",
                "Bacon",
                "Kidney Beans",
                "Shrimp",
                "Garbanzo Beans",
                "Chicken"
        };
        String[] toppings = new String[]{
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
                "Sunflower Seeds"
        };

        ArrayAdapter<String> baseAdapter = new ArrayAdapter<String>(this,
                R.layout.salad_item, android.R.id.text1, bases);
        ArrayAdapter<String> proteinAdapter = new ArrayAdapter<String>(this,
                R.layout.salad_item, android.R.id.text1, proteins);
        ArrayAdapter<String> toppingAdapter = new ArrayAdapter<String>(this,
                R.layout.salad_item, android.R.id.text1, toppings);

        // Assign adapter to ListView
        baseList.setAdapter(baseAdapter);
        proteinList.setAdapter(proteinAdapter);
        toppingList.setAdapter(toppingAdapter);

        //int text = Color.rgb(42, 186, 26);
        int background = Color.rgb(194, 155, 29);
        
        // Add headers
        TextView baseHeader = (TextView) findViewById(R.id.baseHeader);
        baseHeader.setText("Bases:");
        baseHeader.setTextColor(Color.GREEN);
        baseHeader.setBackgroundColor(background);

        TextView proteinHeader = (TextView) findViewById(R.id.proteinHeader);
        proteinHeader.setText("Protein:");
        proteinHeader.setTextColor(Color.GREEN);
        proteinHeader.setBackgroundColor(background);

        TextView toppingHeader = (TextView) findViewById(R.id.toppingHeader);
        toppingHeader.setText("Toppings:");
        toppingHeader.setTextColor(Color.GREEN);
        toppingHeader.setBackgroundColor(background);

        items = new HashMap<String, ImageView>();
        
        // SPOKEN TOPPINGS
        Intent intent = getIntent();
        ArrayList<String> spokenToppings = (ArrayList<String>) intent.getSerializableExtra(OrderActivity.EXTRA_SALAD);
        
        for (int i = 0; i < spokenToppings.size(); i++) {
            String itemKey = spokenToppings.get(i).substring(0,1).toUpperCase() + spokenToppings.get(i).substring(1);
        	String imageSource = itemKey.replaceAll(" ", "_").toLowerCase();
        	
        	ImageView item = new ImageView(getApplicationContext());
            item.setBackgroundResource(getResources().getIdentifier(imageSource, "drawable", getApplicationContext().getPackageName()));

            addToppingToSalad(itemKey, imageSource);
            
            // Highlights the voice selected options - should simplify this in the future
            for (int j = 0; j < 3; j++) {
            	switch(j) {
            	case 0:
            		for (int k = 0; k < bases.length; k++) {
            			if (bases[k].equals(spokenToppings.get(i))) {
            				baseList.setItemChecked(k, true);
            				break;
            			}
            		}
            	case 1:
            		for (int k = 0; k < proteins.length; k++) {
            			if (proteins[k].equals(spokenToppings.get(i))) {
            				proteinList.setItemChecked(k, true);
            				break;
            			}
            		}
            	case 2:
            		for (int k = 0; k < toppings.length; k++) {
            			if (toppings[k].equals(spokenToppings.get(i))) {
            				toppingList.setItemChecked(k, true);
            				break;
            			}
            		}
            	}
            }
        }
        
        saladItemClickListener listener = new saladItemClickListener();

        // ListView Item Click Listener
        baseList.setOnItemClickListener(listener);
        proteinList.setOnItemClickListener(listener);
        toppingList.setOnItemClickListener(listener);
        
    }
    
    public void addToppingToSalad(String itemKey, String imageSource) {
    	ImageView item = new ImageView(getApplicationContext());
        item.setBackgroundResource(getResources().getIdentifier(imageSource, "drawable", getApplicationContext().getPackageName()));

        calories += new SaladTopping(itemKey).getCalories();
        calorieView.setText(calories + " calories");
        
        price += new SaladTopping(itemKey).getPrice();
        priceView.setText("$" + String.format("%2.2f", price));
        
        layout.addView(item);
        items.put(itemKey, item);
    }
    
    @Override
    public void onResume() {	
    	super.onResume();
    	shakeStarted = false;
    	
        // SENSOR MANAGER
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    	mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    public void onPause() {	
    	super.onPause();
    	shakeStarted = true;
    	
    	mSensorManager.unregisterListener(this, mSensor);
    	mSensorManager = null;
    }
    
    @Override
    public void onSensorChanged(SensorEvent event) {
        curTime = System.currentTimeMillis();
        if (lastUpdate == 0) { lastUpdate = curTime; }

        // only allow one update every 100ms.
        if ((curTime - lastUpdate) > 100) {
            long diffTime = (curTime - lastUpdate);
            lastUpdate = curTime;

            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

            if (speed > SHAKE_THRESHOLD && !shakeStarted) {
                shakeStarted = true;
            	Log.d("sensor", "shake detected w/ speed: " + speed);
            	//Add empty order control
            	if (items.keySet().isEmpty()) {
            		Toast.makeText(getApplicationContext(),"Your salad bowl is empty.", Toast.LENGTH_SHORT).show();
            		setResult(Activity.RESULT_CANCELED, null);
            		mSensorManager.unregisterListener(this, mSensor);
            		finish();
            	} else {
            		ArrayList<String> toppings = new ArrayList<String>(items.keySet());
                    
                	// Display graphical salad toss
                	Intent display = new Intent(this, DisplayActivity.class);
                	display.putExtra(OrderActivity.EXTRA_SALAD, toppings);
                	startActivityForResult(display, DISPLAY_REQUEST_CODE);
                	
                	mSensorManager.unregisterListener(this, mSensor);
            	}
            }
            last_x = x;
            last_y = y;
            last_z = z;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    public class saladItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {

            // ListView Clicked item value
            String itemKey    = (String) parent.getItemAtPosition(position);
            String imageSource = itemKey.replaceAll(" ", "_").toLowerCase();

            if (!items.containsKey(itemKey)) {
                if (items.size() > layout.getColumnCount()*layout.getRowCount()-1) { //Limit components to columns*rows-1 (one spot for price display
                	Toast.makeText(getApplicationContext(), "You have reached your topping limit", Toast.LENGTH_SHORT).show();
                } else { // Add topping to salad
	                ImageView item = new ImageView(getApplicationContext());
	                item.setBackgroundResource(getResources().getIdentifier(imageSource, "drawable", getApplicationContext().getPackageName()));
	
	                addToppingToSalad(itemKey, imageSource);
                }
            } else { // Remove topping from salad
                calories -= new SaladTopping(itemKey).getCalories();
                calorieView.setText(calories + " calories");
                
                price -= new SaladTopping(itemKey).getPrice();
                priceView.setText("$" + String.format("%2.2f", price));
            	
                layout.removeView(items.get(itemKey));
                items.remove(itemKey);
            }
        }
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {       
    	Bitmap bmp = null;
    	
    	if (requestCode == DISPLAY_REQUEST_CODE && resultCode == RESULT_OK){
        	bmp = null;
        	int midX = data.getIntExtra("x", 360);
        	int midY = data.getIntExtra("y", 640);
        	String filename = data.getStringExtra("image");
        	
        	System.out.println("File: " + filename + " x " + midX + " y " + midY);
        	
        	try {
        	    FileInputStream is = this.openFileInput(filename);
				BitmapFactory.Options options = new BitmapFactory.Options();
				BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
				
				Rect tileBounds = new Rect();
				tileBounds.top = midY-25;
				tileBounds.bottom = midY+275;
				tileBounds.left = midX-50;
				tileBounds.right = midX+250;
				
				// load tile
				bmp = decoder.decodeRegion(tileBounds, options);
        	    is.close();
        	} catch (Exception e) {
        	    e.printStackTrace();
        	}
        	
        	//BMP holds file
        	System.out.println("Assembly " + bmp);
        }
    	
		ArrayList<String> toppings = new ArrayList<String>(items.keySet());
        
       	// Create intent to deliver salad result data
    	Intent result = new Intent();
    	result.putExtra(OrderActivity.EXTRA_SALAD, toppings);
    	result.putExtra("image", bmp);
    	setResult(Activity.RESULT_OK, result);
    	finish();
    }
}
