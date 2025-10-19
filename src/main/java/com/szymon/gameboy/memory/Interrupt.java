/**
 * @author 18bilkiewiczs
 * Class to handle the interrupt registers
 */

package com.szymon.gameboy.memory;

import com.szymon.gameboy.utils.Unsigned8;

/*
 * Interrupt Enable (IE) Register: Controls whether the respective interrupt handler may be called
 * Interrupt Flag (IF) Register: Controls whether the respective interrupt handler is being requested
 * For both:
 * Bit 4: Joypad
 * Bit 3: Serial
 * Bit 2: Timer
 * Bit 1: LCD
 * Bit 0: VBlank
 */

public class Interrupt
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	//the two relevant interrupt registers
	private Unsigned8 u8InterruptFlag;
	private Unsigned8 u8InterruptEnable;
	
	// ---------------------------------------------
	// constructors
	// ---------------------------------------------
	public Interrupt()
	{
		u8InterruptEnable = new Unsigned8();
		u8InterruptFlag = new Unsigned8();
	}
	
	// ---------------------------------------------
	// setters
	// ---------------------------------------------
	public void setIntFlag(Unsigned8 u8Data)
	{
		u8InterruptFlag.setValue(u8Data);
	}
	
	public void setIntEnable(Unsigned8 u8Data)
	{
		u8InterruptEnable.setValue(u8Data);
	}
	
	// ---------------------------------------------
	// getters
	// ---------------------------------------------
	public Unsigned8 getIntFlag()
	{
		return u8InterruptFlag;
	}
	
	public Unsigned8 getIntEnable()
	{
		return u8InterruptEnable;
	}
}
