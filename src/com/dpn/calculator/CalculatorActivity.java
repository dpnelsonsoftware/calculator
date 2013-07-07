package com.dpn.calculator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class CalculatorActivity extends Activity {

	private static final String LOG_FILENAME = "CalculatorLog";
	private List<CalculatorTableRowModel> mPreviousOps;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.calc_layout);
        
    	loadTable();
    }
    
    private void loadTable() {
    	FileInputStream inputStream;
    	try {
    		inputStream = openFileInput(LOG_FILENAME);
      	  ObjectInputStream objStream = new ObjectInputStream(inputStream);
      	  mPreviousOps = (List<CalculatorTableRowModel>)objStream.readObject();
      	} catch (Exception e) {
      		mPreviousOps  = new ArrayList<CalculatorTableRowModel>();
      		e.printStackTrace();
      	} 
    	
        LinearLayout prevOpps = (LinearLayout)this.findViewById(R.id.previousOperations);
        final ScrollView scrollView = (ScrollView)this.findViewById(R.id.scrollView1);
        prevOpps.removeAllViews();
        prevOpps.removeAllViewsInLayout();
        scrollView.setVerticalScrollBarEnabled(false);
        
        //make sure we start with 25 operations
        for(int i=mPreviousOps.size();i<25;i++){
				calculateAndDisplay("", false);
        }
        for(CalculatorTableRowModel row : mPreviousOps){
        	View viewToAdd = new CalculatorTableRow(getApplicationContext(), row, this);
        	viewToAdd.setBackgroundResource(R.drawable.scroll_item_shape);
        	prevOpps.addView(viewToAdd);
        }
        scrollView.requestLayout();
        scrollView.fullScroll(View.FOCUS_DOWN);
        	 
        scrollView.postDelayed(new Runnable(){
        	@Override
        	public void run(){
        		scrollView.fullScroll(View.FOCUS_DOWN);
        	}
        }, 100);
	}
    private void saveTable(){
    	FileOutputStream outputStream;
    	try {
    	  outputStream = openFileOutput(LOG_FILENAME, Context.MODE_PRIVATE);
    	  ObjectOutputStream objStream = new ObjectOutputStream(outputStream);
    	  objStream.writeObject(mPreviousOps);
   			objStream.close();
    	  outputStream.close();
    	} catch (Exception e) {
    	  e.printStackTrace();
    	}
    }
    
    private void calculateAndDisplay(String text, boolean pKeepRecord){
    	 LinearLayout prevOpps = (LinearLayout)this.findViewById(R.id.previousOperations);
    	 View viewToAdd;
    	 CalculatorTableRowModel rowModel = new CalculatorTableRowModel(text);
    	 if(pKeepRecord){
    		 mPreviousOps.add(rowModel);
    		 saveTable();
    	 }
    	 viewToAdd = new CalculatorTableRow(getApplicationContext(),rowModel, this);
    	 if(!"".equals(text)){
    		 viewToAdd.setBackgroundResource(R.drawable.scroll_item_shape);
    	 }
    	 prevOpps.addView(viewToAdd);
    	 final ScrollView scrollView = (ScrollView)this.findViewById(R.id.scrollView1);
    	 scrollView.requestLayout();
    	 scrollView.fullScroll(View.FOCUS_DOWN);
    	 
    	 scrollView.postDelayed(new Runnable(){
    		  @Override
    		  public void run(){
    			  scrollView.fullScroll(View.FOCUS_DOWN);
    		  }
    		}, 100);
    }
    
	public void button_onClick(View view) {
		int buttonID = view.getId();
		
		EditText text = (EditText)this.findViewById(R.id.currentOperation);
		
		switch (buttonID){
			case R.id.button_equal:
				calculateAndDisplay(text.getText().toString(), true);
				text.setText("");
				break;
			case R.id.button_delete:
				if(text.getText().toString().length()>0)
					text.setText(text.getText().toString().substring(0,text.getText().toString().length()-1));
				break;
			case R.id.button_clear:
				text.setText("");
				break;
			case R.id.button_minus:
			case R.id.button_plus:
			case R.id.button_divide:
			case R.id.button_multiply:
				if(text.getText().toString().equals("")){
			    	 LinearLayout prevOpps = (LinearLayout)this.findViewById(R.id.previousOperations);
			    	 View lastResult= prevOpps.getChildAt(prevOpps.getChildCount()-1);
			    	 if(lastResult instanceof CalculatorTableRow){
			    		 CalculatorTableRow lastRow = (CalculatorTableRow) lastResult;
			    		 if(lastRow.isValid()){
			    			 text.setText(lastRow.getResult());
			    		 }
			    	 }
				}
		    //intentionally falls through
			default:
				Button button = (Button)view;
				addToText(button.getText().toString());
				break;
		}
		
		ScrollView sv = (ScrollView)this.findViewById(R.id.scrollView1);
		sv.fullScroll(View.FOCUS_DOWN);
	}
	
	public void addToText(String toAdd){
		EditText text = (EditText)this.findViewById(R.id.currentOperation);
		text.setText(text.getText().toString()+toAdd);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.reset_log){
			resetLog();
			return true;
		}
		return false;
	}
	private void resetLog() {
		mPreviousOps  = new ArrayList<CalculatorTableRowModel>();
		saveTable();
		loadTable();
	}
	
	public boolean onCreateOptionsMenu(Menu pMenu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.calc_menu, pMenu);
	    return true;
	}
	
}