/**
 * Class to store all of the timer related registers
 */

package com.szymon.gameboy.memory;

import com.szymon.gameboy.utils.Unsigned16;
import com.szymon.gameboy.utils.Unsigned8;

public class Timer
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	private final int iDIV_ADDRESS = 0xFF04;
	private final int iTIMA_ADDRESS = 0xFF05;
	private final int iTMA_ADDRESS = 0xFF06;
	private final int iTAC_ADDRESS = 0xFF07;
	
	private Unsigned16 u16DIV;	//incremented at 16384Hz, writing any value resets it to 0x00. Internally, it is a 16 bit register, but when read, the high byte is read
	private Unsigned8 u8TIMA;	//incremented at the clock frequency specified by TAC, resets to value in TMA when it overflows
	private Unsigned8 u8TMA;	//when TIMA overflows, reset to this value and an interrupt is requested
	private Unsigned8 u8TAC;	//bit 2 controls whether tima is incremented, and bits 1 and 0 decide on the frequency
	
	// ---------------------------------------------
	// constructors
	// ---------------------------------------------
	//must be created with the same memory used by the CPU
	public Timer()
	{
		reset();
	}
	
	// ---------------------------------------------
	// setters
	// ---------------------------------------------
	public void writeTimer(Unsigned16 u16Address, Unsigned8 u8Data)
	{
		if (u16Address.getValue() == iDIV_ADDRESS)
		{
			u16DIV.setValue(0);
		}
		else if (u16Address.getValue() == iTIMA_ADDRESS)
		{
			u8TIMA.setValue(u8Data);
		}
		else if (u16Address.getValue() == iTMA_ADDRESS)
		{
			u8TMA.setValue(u8Data);
		}
		else if (u16Address.getValue() == iTAC_ADDRESS)
		{
			u8TAC.setValue(u8Data);
		}
	}
	
	// ---------------------------------------------
	// getters
	// ---------------------------------------------
	public Unsigned8 readTimer(Unsigned16 u16Address)
	{
		if (u16Address.getValue() == iDIV_ADDRESS)
		{
			return u16DIV.getHighByte();
		}
		else if (u16Address.getValue() == iTIMA_ADDRESS)
		{
			return u8TIMA;
		}
		else if (u16Address.getValue() == iTMA_ADDRESS)
		{
			return u8TMA;
		}
		else if (u16Address.getValue() == iTAC_ADDRESS)
		{
			return u8TAC;
		}
		
		return new Unsigned8();
	}
	
	// ---------------------------------------------
	// methods
	// ---------------------------------------------
	public void reset()
	{
		u16DIV = new Unsigned16(0xABCC);
		u8TIMA = new Unsigned8();
		u8TMA = new Unsigned8();
		u8TAC = new Unsigned8(0xF8);
	}
	
	//allows for the div register to be incremented from the cycle class
	public void incrementDIV()
	{
		u16DIV.increment();
	}
}
