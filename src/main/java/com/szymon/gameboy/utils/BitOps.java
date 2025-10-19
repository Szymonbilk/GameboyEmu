/**
 * @author 18bilkiewiczs
 * Contains various static methods for dealing with binary numbers
 * Static methods are justified as all values are destroyed upon exit
 */

package com.szymon.gameboy.utils;

public class BitOps 
{
	//checks if a number is zero, and returns true if zero
	public static boolean checkZero(int iNum)
	{
		if (iNum == 0)
		{
			return true;
		}
		else 
		{
			return false;
		}
	}
	
	//converts an int to a boolean (i.e. if 0, false, else true)
	public static boolean convertIntToBool(int iNum)
	{
		if (iNum == 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	//converts a boolean to an int (i.e. if true, 1, if false, 0)
	public static int convertBoolToInt(boolean bBool)
	{
		if (bBool == true)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
	
	//return the upper 8 bits of a 16 bit number
	public static int getHighByte(int iNum)
	{
		int iHighByte = (iNum >> 8) & 0xFF;
		return iHighByte;
	}
	
	//return the lower 8 bits of a 16 bit number
	public static int getLowByte(int iNum)
	{
		int iLowByte = iNum & 0xFF;
		return iLowByte;
	}
	
	//return a specific bit from a number as a boolean
	public static boolean getBit(int iNum, int iBitPos)
	{
		int iBit = (iNum >> iBitPos) & 0x1;
		
		if (iBit == 1)
		{
			return true;
		}
		else 
		{
			return false;
		}
	}
	
	
	//set the upper 8 bits of a 16 bit number
	public static int setHighByte(int iNum, int iHighByte)
	{
		int iNewNum = ((iHighByte & 0xFF) << 8) | (iNum & 0xFF);
		return iNewNum;
	}
	
	//set the lower 8 bits of a 16 bit number
	public static int setLowByte(int iNum, int iLowByte)
	{
		int iNewNum = (iNum & 0xFF00) | (iLowByte & 0xFF);
		return iNewNum;
	}
	
	//set a specific bit in a number (makes it a 1)
	public static int setBit(int iNum, int iBitPos)
	{
		int iNewNum = iNum | (1 << iBitPos);
		return iNewNum;
	}
	
	//clear a specific bit in a number (makes it a 0)
	public static int clearBit(int iNum, int iBitPos)
	{
		int iNewNum = iNum & ~(1 << iBitPos);
		return iNewNum;
	}
}
