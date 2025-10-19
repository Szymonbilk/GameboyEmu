/**
 * @author 18bilkiewiczs
 * Class to be used for unsigned 16 bit numbers
 */

package com.szymon.gameboy.utils;

public class Unsigned16 implements Unsigned
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	private int iNum;
	//upper and lower values
	final private int iUPPER_VALUE = 65535;
	final private int iLOWER_VALUE = 0;

	// ---------------------------------------------
	// constructors
	// ---------------------------------------------
	public Unsigned16()
	{
		setValue(0);
	}
	
	public Unsigned16(int iNum)
	{
		setValue(iNum);
	}
	
	public Unsigned16(Unsigned16 u16Num)
	{
		setValue(u16Num.getValue());
	}

	// ---------------------------------------------
	// setters
	// ---------------------------------------------
	//when setting, only consider the first 16 bits
	public void setValue(int iValue)
	{
		this.iNum = (iValue & 0xFFFF);
	}

	public void setValue(Unsigned uNum)
	{
		setValue(uNum.getValue());;
	}

	//set the high/low bytes of the value
	public void setHighByte(Unsigned8 u8High)
	{
		setValue(BitOps.setHighByte(iNum, u8High.getValue()));
	}
	
	public void setLowByte(Unsigned8 u8Low)
	{
		setValue(BitOps.setLowByte(iNum, u8Low.getValue()));
	}
	
	// ---------------------------------------------
	// getters
	// ---------------------------------------------
	public int getValue()
	{
		return this.iNum;
	}
	
	//get the high/low bytes of the value
	public Unsigned8 getHighByte()
	{
		Unsigned8 u8Num = new Unsigned8(BitOps.getHighByte(getValue()));
		return u8Num;
	}
	
	public Unsigned8 getLowByte()
	{
		Unsigned8 u8Num = new Unsigned8(BitOps.getLowByte(getValue()));
		return u8Num;
	}

	// ---------------------------------------------
	// methods
	// ---------------------------------------------
	//combine two bytes 
	public void combineBytes(Unsigned8 u8High, Unsigned8 u8Low)
	{
		setHighByte(u8High);
		setLowByte(u8Low);
	}
	
	//basic arithmetic operations
	public void add(int iValue)
	{
		int iNum = (iValue & 0xFFFF) + this.iNum;
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
		int iNum = this.iNum - (iValue & 0xFFFF);
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
}
