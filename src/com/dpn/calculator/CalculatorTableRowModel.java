package com.dpn.calculator;

import java.io.Serializable;
import java.math.BigDecimal;

public class CalculatorTableRowModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public final String mOperation;
	public final BigDecimal mResult;
	public final boolean mIsValid;

	public CalculatorTableRowModel(String pOperation){
		BigDecimal result = new BigDecimal(0);
		boolean valid = false;
		if(!"".equals(pOperation)){
			ExpressionTree tree = new ExpressionTree(pOperation);
			if(tree.isValid()){
				valid = true;
				result = BigDecimal.valueOf(tree.evaluate());
				if(result.scale() > 0){
					//if we have fraction digits, strip trailing zeros.
					result = result.stripTrailingZeros();
					if(result.scale()< 0 ){
						result = result.setScale(0);
					}
				}
			}
		}
		mResult = result;
		mOperation = pOperation;
		mIsValid = valid;
	}
}
