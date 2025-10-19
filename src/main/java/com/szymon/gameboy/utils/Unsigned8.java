/**
 * @author 18bilkiewiczs
 * Class to be used for unsigned 8 bit numbers
 */

package com.szymon.gameboy.utils;

public class Unsigned8 implements Unsigned
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	private int iNum;
	//upper and lower values
	final private int iUPPER_VALUE = 255;
	final private int iLOWER_VALUE = 0;

	// ---------------------------------------------
	// constructors
	// ---------------------------------------------
	public Unsigned8()
	{
		setValue(0);
	}
	
	public Unsigned8(int iNum)
	{
		setValue(iNum);
	}
	
	public Unsigned8(Unsigned8 u8Num)
	{
		setValue(u8Num.getValue());
	}

	// ---------------------------------------------
	// setters
	// ---------------------------------------------
	//when setting, only consider the first 8 bits
	public void setValue(int iValue)
	{
		this.iNum = (iValue & 0xFF);
	}

	public void setValue(Unsigned uNum)
	{
		setValue(uNum.getValue());
	}

	// ---------------------------------------------
	// getters
	// ---------------------------------------------
	public int getValue()
	{
		return this.iNum;
	}

	//returns the signed 8 bit value of the number
	public int getSignedValue()
	{
		return (byte) this.iNum;
	}

	// ---------------------------------------------
	// methods
	// ---------------------------------------------
	public void add(int iValue)
	{
		int iNum = (iValue & 0xFF) + this.iNum;
		//deal with wrapping of the values
		if (iNum > iUPPER_VALUE)
		{
			iNum -= (iUPPER_VALUE + 1);
		}
		
		setValue(iNum);
	}

	public void add(Unsigned uNum)
	{
		add(uNum.getValue());
	}
	
	public void sub(int iValue)
	{
		int iNum = this.iNum - (iValue & 0xFF);
		//deal with wrapping of the values
		if (iNum < iLOWER_VALUE)
		{
			iNum += (iUPPER_VALUE + 1);
		}
		
		setValue(iNum);
	}

	public void sub(Unsigned uNum)
	{
		sub(uNum.getValue());
	}
	
	public void increment()
	{
		add(1);
	}
	
	public void decrement()
	{
		sub(1);
	}
	
	//add the specified value, as a signed value (the specified value is assumed to be 8 bit)
	public void addAsSigned8(int iValue)
	{
		byte sValue = (byte) iValue;
		int iNum = this.iNum + sValue;
		
		//deal with wrapping for both sides (as could be negative or positive)
		if (iNum > iUPPER_VALUE)
		{
			iNum -= (iUPPER_VALUE + 1);
		}
		else if (iNum < iLOWER_VALUE)
		{
			iNum += (iUPPER_VALUE + 1);
		}
		
		setValue(iNum);
	}
	
	public void addAsSigned8(Unsigned uNum)
	{
		addAsSigned8(uNum.getValue());
	}
	
	//bit operations
	public boolean getBit(int iBit)
	{
		return BitOps.getBit(getValue(), iBit);
	}
		
	public void setBit(int iBit)
	{
		setValue(BitOps.setBit(getValue(), iBit));
	}
		
	public void clearBit(int iBit)
	{
		setValue(BitOps.clearBit(getValue(), iBit));
	}
	
	
	public static void main(String[] args)
	{
		Unsigned8 u8Num = new Unsigned8(37);
		System.out.println(u8Num.getSignedValue());
	}
}
