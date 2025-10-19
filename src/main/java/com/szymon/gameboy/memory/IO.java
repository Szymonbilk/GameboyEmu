/**
 * @author 18bilkiewiczs
 * Class to contain all of the IO registers
 */

package com.szymon.gameboy.memory;

import com.szymon.gameboy.joypad.Joypad;
import com.szymon.gameboy.ppu.DMA;
import com.szymon.gameboy.ppu.LCD;
import com.szymon.gameboy.utils.Unsigned16;
import com.szymon.gameboy.utils.Unsigned8;

public class IO 
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	//used for debugging for Blargg test ROMs, not actually implemented
	private Unsigned8[] u8SerialData;
	//all of the areas where register values are stored
	private Interrupt interrupt;
	private Timer timer;
	private DMA dma;
	private LCD lcd;
	private Joypad joypad;
	
	// ---------------------------------------------
	// constructors
	// ---------------------------------------------
	//LCD must be passed in so that the same LCD can be used throughout the code
	public IO(LCD lcdIn, DMA dmaIn, Joypad joypadIn)
	{
		u8SerialData = new Unsigned8[2];
		for (int i = 0; i < 2; i++)
		{
			u8SerialData[i] = new Unsigned8();
		}
		
		interrupt = new Interrupt();
		timer = new Timer();
		dma = dmaIn;
		lcd = lcdIn;
		joypad = joypadIn;
	}
	
	// ---------------------------------------------
	// setters
	// ---------------------------------------------
	public void writeIO(Unsigned16 u16Address, Unsigned8 u8Data)
	{
		if (u16Address.getValue() == 0xFF00)
		{
			joypad.setJoypad(u8Data);
		}
		else if (u16Address.getValue() == 0xFF01)
		{
			u8SerialData[0] = u8Data;
		}
		else if (u16Address.getValue() == 0xFF02)
		{
			u8SerialData[1] = u8Data;
		}
		else if (u16Address.getValue() >= 0xFF04 && u16Address.getValue() <= 0xFF07)
		{
			timer.writeTimer(u16Address, u8Data);
		}
		else if (u16Address.getValue() == 0xFF0F)
		{
			interrupt.setIntFlag(u8Data);
		}
		else if (u16Address.getValue() >= 0xFF10 && u16Address.getValue() <= 0xFF3F)
		{
			//sound registers - not implemented
			return;
		}
		else if (u16Address.getValue() >= 0xFF40 && u16Address.getValue() <= 0xFF4B)
		{
			lcd.writeRegisters(u16Address, u8Data);
			
			if (u16Address.getValue() == 0xFF46)
			{
				dma.startDMA(u8Data);
			}
		}
		else if (u16Address.getValue() == 0xFFFF)
		{
			interrupt.setIntEnable(u8Data);
		}
		else
		{
			System.err.printf("IO WRITE TO DO: %04x%n", u16Address.getValue());
		}
	}
	
	// ---------------------------------------------
	// getters
	// ---------------------------------------------
	public Unsigned8 readIO(Unsigned16 u16Address)
	{
		if (u16Address.getValue() == 0xFF00)
		{
			return joypad.getJoypad();
		}
		else if (u16Address.getValue() == 0xFF01)
		{
			return (u8SerialData[0]);
		}
		else if (u16Address.getValue() == 0xFF02)
		{
			return (u8SerialData[1]);
		}
		else if (u16Address.getValue() >= 0xFF04 && u16Address.getValue() <= 0xFF07)
		{
			return timer.readTimer(u16Address);
		}
		else if (u16Address.getValue() == 0xFF0F)
		{
			return interrupt.getIntFlag();
		}
		else if (u16Address.getValue() >= 0xFF10 && u16Address.getValue() <= 0xFF3F)
		{
			//sound registers - not implemented
			return new Unsigned8(0xFF);
		}
		else if (u16Address.getValue() >= 0xFF40 && u16Address.getValue() <= 0xFF4B)
		{
			return lcd.readRegisters(u16Address);
		}
		else if (u16Address.getValue() == 0xFFFF)
		{
			return interrupt.getIntEnable();
		}
		else
		{
			System.err.println("IO READ TO DO");
			return new Unsigned8(0xFF);
		}
	}

	// ---------------------------------------------
	// methods
	// ---------------------------------------------
	//this method served as a pass-through method to allow the DIV register to be incremented
	//as writing to it through memory resets its value to 0
	public void incrementDIV()
	{
		timer.incrementDIV();
	}
}
