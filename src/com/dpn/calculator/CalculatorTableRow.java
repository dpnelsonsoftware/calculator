package com.dpn.calculator;

import java.math.BigDecimal;
import java.text.NumberFormat;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CalculatorTableRow extends LinearLayout {
	
	private BigDecimal mResult;
	private final CalculatorActivity mActivity;
	
	static final NumberFormat FORMAT;
	
	private boolean isValid = false;
	
	static{
		FORMAT = NumberFormat.getNumberInstance();
		FORMAT.setMaximumFractionDigits(15);
		FORMAT.setMinimumFractionDigits(0);
		FORMAT.setGroupingUsed(false);
	}
	
	public CalculatorTableRow(Context context, String pString, CalculatorActivity calculatorActivity) {
		super(context);
		mActivity = calculatorActivity;
		setOrientation(HORIZONTAL);
		setGravity(Gravity.RIGHT);
		setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		TextView textView = new TextView(context);
		textView.setTextSize(30);
		if(!"".equals(pString)){
			BigDecimal result;
			ExpressionTree tree = new ExpressionTree(pString);
			if(tree.isValid()){
				isValid = true;
				result = BigDecimal.valueOf(tree.evaluate());
				if(result.scale() > 0){
					//if we have fraction digits, strip trailing zeros.
					result = result.stripTrailingZeros();
					if(result.scale()< 0 ){
						result = result.setScale(0);
					}
				}
				mResult = result;
				System.out.println("bigDec : "+mResult);
//				textView.setText(pString+" = "+(FORMAT.format(mResult.doubleValue())));
				String stringToShow = Double.toString(mResult.doubleValue());
				//strip trailing zeros
				
				while (stringToShow.endsWith("0") || stringToShow.endsWith(".")){
					stringToShow = stringToShow.substring(0,stringToShow.length()-1);
				}
				textView.setText(pString+" = "+result);
				textView.setOnClickListener(new View.OnClickListener() {
		             public void onClick(View v) {
		            	 mActivity.addToText(mResult.toString());
		             }
		         });
			}
			else{
				textView.setText(pString+" = error");
				textView.setTextColor(Color.RED);
			}

		}
		textView.setGravity(Gravity.RIGHT);
		
		addView(textView);
		
	}
	public boolean isValid() {
		return isValid;
	}
	public String getResult(){
		return mResult.toString();
	}
}
