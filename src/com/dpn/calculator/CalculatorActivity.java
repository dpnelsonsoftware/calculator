package com.dpn.calculator;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class CalculatorActivity extends Activity {

	private static List<CalculatorTableRow> calcRows;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.calc_layout);
        LinearLayout prevOpps = (LinearLayout)this.findViewById(R.id.previousOperations);
        final ScrollView scrollView = (ScrollView)this.findViewById(R.id.scrollView1);
        if(calcRows == null){
	        prevOpps.removeAllViews();
	        scrollView.setVerticalScrollBarEnabled(false);
	        for(int i=0;i<25;i++){
				calculateAndDisplay("", false);
	        }
        }else{
        	for(CalculatorTableRow row : calcRows){
         	 prevOpps.addView(row);
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
        
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        calcRows = new ArrayList<CalculatorTableRow>();
        LinearLayout prevOpps = (LinearLayout)this.findViewById(R.id.previousOperations);
        for(int i=0; i<prevOpps.getChildCount();i++){
        	calcRows.add((CalculatorTableRow)prevOpps.getChildAt(i));
        }
    }
    
    private void calculateAndDisplay(String text, boolean backgroundPattern){
    	 LinearLayout prevOpps = (LinearLayout)this.findViewById(R.id.previousOperations);
    	 View viewToAdd;
    	 viewToAdd = new CalculatorTableRow(getApplicationContext(),text, this);
    	 if(backgroundPattern){
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
					//FIXME: repopulate last result.
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
}