package com.dpn.calculator;


import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CalculatorTableRow extends LinearLayout {
	
	private final CalculatorActivity mActivity;
	private final CalculatorTableRowModel mModel;
	
	public CalculatorTableRow(Context context, CalculatorTableRowModel pModel, CalculatorActivity calculatorActivity) {
		super(context);
		mModel = pModel;
		mActivity = calculatorActivity;
		setOrientation(HORIZONTAL);
		setGravity(Gravity.RIGHT);
		setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		TextView textView = new TextView(context);
		textView.setTextSize(30);
		if(!"".equals(mModel.mOperation)){
			if(mModel.mIsValid){
				textView.setText(mModel.mOperation+" = "+mModel.mResult.toString());
				textView.setOnClickListener(new View.OnClickListener() {
		             public void onClick(View v) {
		            	 mActivity.addToText(mModel.mResult.toString());
		             }
		             
		             
		             
		         });
			}
			else{
				textView.setText(mModel.mOperation+" = error");
				textView.setTextColor(Color.RED);
			}

		}
		textView.setGravity(Gravity.RIGHT);
		
		addView(textView);
		
	}
	public boolean isValid() {
		return mModel.mIsValid;
	}
	public String getResult(){
		return mModel.mResult.toString();
	}
	public CalculatorTableRowModel getModel() {
		return mModel;
	}
}
