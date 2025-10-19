/**
 * @author 18bilkiewiczs
 * Interface to represent unsigned numbers (8 bit and 16 bit to be used)
 */

package com.szymon.gameboy.utils;

public abstract interface Unsigned 
{
	//---------------------------------------------
	//setters
	//---------------------------------------------
	public void setValue(int iValue);
	public void setValue(Unsigned uNum);
	
	//---------------------------------------------
	//getters
	//---------------------------------------------
	public int getValue();
	
	//---------------------------------------------
	//methods
	//---------------------------------------------
	//basic arithmetic operations
	public void add(int iValue);
	public void add(Unsigned uNum);
	public void sub(int iValue);
	public void sub(Unsigned uNum);
	public void increment();
	public void decrement();
	//add the specified value, as a signed value (the specified value is assumed to be 8 bit)
	public void addAsSigned8(int iValue);	
	public void addAsSigned8(Unsigned uNum);
	//bit operations
	public boolean getBit(int iBit);	//get the value of a specific bit
	public void setBit(int iBit);	//set a specific bit (make it a 1)
	public void clearBit(int iBit);	//clear a specific bit (make it a 0)
}
