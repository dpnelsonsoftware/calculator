package com.dpn.calculator;

import java.util.StringTokenizer;


public class ExpressionTree {
	
	Node mHeader = new HeaderNode();
	
	public ExpressionTree (String expression){
		
		
		String reformattedExpression = expression.replace("E+", "Ep").replace("E-", "Em");
		
		StringTokenizer tokenizer = new StringTokenizer(reformattedExpression, "+-/x", true);
		Node latestNode = mHeader;
		
		while(tokenizer.hasMoreElements()){
			Node nextNode = parseToNode((String)tokenizer.nextElement());
			latestNode.successor = nextNode;
			nextNode.predecessor = latestNode;
			latestNode = nextNode;
		}
		
		buildTree();
	}
	
	private void buildTree() {
		pullInNegatives();
		doMultDiv();
		doAddSub();
	}

	private void pullInNegatives() {
		Node currentNode = mHeader;
		while(currentNode !=null){
			if(currentNode instanceof SubtractionNode){
				((SubtractionNode)currentNode).pullInNegative();
			}
			currentNode = currentNode.successor;
		}
	}

	private void doAddSub() {
		Node currentNode = mHeader;
		while(currentNode !=null){
			if(currentNode instanceof AdditionNode ||
					currentNode instanceof SubtractionNode){
				((BinaryOperationNode)currentNode).pullInChildren();
			}
			currentNode = currentNode.successor;
		}
	}

	private void doMultDiv() {
		Node currentNode = mHeader;
		while(currentNode !=null){
			if(currentNode instanceof MultiplicationNode ||
					currentNode instanceof DivisionNode){
				((BinaryOperationNode)currentNode).pullInChildren();
			}
			currentNode = currentNode.successor;
		}
	}

	private static Node parseToNode(String pAsString){
		System.out.println(pAsString);
		if("+".equals(pAsString)){
			return new AdditionNode();
		}
		else if("-".equals(pAsString)){
			return new SubtractionNode();
		}
		else if("x".equals(pAsString)){
			return new MultiplicationNode();
		}
		else if("/".equals(pAsString)){
			return new DivisionNode();
		}
		else{
			Node node;
			try{
				node = new NumericNode(pAsString);
			}
			catch(NumberFormatException e){
				node = new MalformedNumberNode(pAsString);
			}
			return node;
		}
	}
	
	@Override
	public String toString(){
		return mHeader.toString();
	}
	
	private static abstract class Node{
		Node predecessor;
		Node successor;
		abstract double evaluate();
		abstract boolean isValidSubtree();
	}
	
	private static class NumericNode extends Node{

		private double mValue;
		
		private NumericNode(String pValue){
			System.out.println(pValue);
			if(pValue.contains("E")){
				String[] parts = pValue.split("E");
				//strip off the sign
				double exponent = Double.parseDouble(parts[1].substring(1));
				if(parts[1].startsWith("m")){
					exponent = -exponent;
				}
				mValue = Double.parseDouble(parts[0])*Math.pow(10, exponent);

			}
			else{
				mValue=Double.parseDouble(pValue);
			}
			
		}
		@Override
		double evaluate() {
			return mValue;
		}
		public void negate() {
			mValue = mValue*-1;
		}
		@Override
		boolean isValidSubtree() {
			return (predecessor == null || predecessor instanceof HeaderNode)
					&& successor == null;
		}
	}
	
	private static class MalformedNumberNode extends NumericNode{
		private MalformedNumberNode(String pValue){
			super("0");
		}
		@Override
		boolean isValidSubtree() {
			return false;
		}
	}
	
	private static abstract class BinaryOperationNode extends Node{
		Node mLeftChild;
		Node mRightChild;
		
		private BinaryOperationNode(){
		}
		void pullInChildren(){
			if(!(predecessor instanceof HeaderNode)){
				mLeftChild = predecessor;
				predecessor = predecessor.predecessor;
				predecessor.successor =this;
				mLeftChild.predecessor = null;
				mLeftChild.successor = null;
			}
			mRightChild = successor;
			if(successor !=null){
				successor = successor.successor;
				mRightChild.predecessor = null;
				mRightChild.successor = null;
			}
			if(successor !=null){
				successor.predecessor = this;
			}
		}
		@Override
		boolean isValidSubtree() {
			return (predecessor == null || predecessor instanceof HeaderNode) && 
					successor == null &&
					mLeftChild != null && mLeftChild.isValidSubtree() && 
					mRightChild != null && mRightChild.isValidSubtree();
		}
	}
	
	private static class AdditionNode extends BinaryOperationNode{
		@Override
		double evaluate() {
			return mLeftChild.evaluate()+mRightChild.evaluate();
		}
	}
	private static class SubtractionNode extends BinaryOperationNode{
		@Override
		double evaluate() {
			return mLeftChild.evaluate()-mRightChild.evaluate();
		}

		public void pullInNegative() {
			if(!(predecessor instanceof NumericNode) && successor instanceof NumericNode){
				((NumericNode)successor).negate();
				
				predecessor.successor = this.successor;
				successor.predecessor =this.predecessor;
			}
		}
	}
	private static class MultiplicationNode extends BinaryOperationNode{
		@Override
		double evaluate() {
			return mLeftChild.evaluate()*mRightChild.evaluate();
		}
	}
	private static class DivisionNode extends BinaryOperationNode{
		@Override
		double evaluate() {
			return mLeftChild.evaluate()/mRightChild.evaluate();
		}
	}
	
	private static class HeaderNode extends Node{
		
		@Override
		double evaluate() {
			if(successor == null){
				return 0;
			}
			return successor.evaluate();
		}

		@Override
		boolean isValidSubtree() {
			return successor.isValidSubtree();
		}
	}

	public double evaluate() {
		return mHeader.evaluate();
	}

	public boolean isValid() {
		return mHeader.isValidSubtree();
	}
}
