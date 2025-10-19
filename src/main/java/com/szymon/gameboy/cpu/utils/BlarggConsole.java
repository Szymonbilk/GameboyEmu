/**
 * @author 18bilkiewiczs
 * This class is used for running Blargg CPU test ROMs without visual output
 * The ROMs can be run on the CPU independently of visual output, with text being
 * outputted to the serial port in a specific manner:
 * The ROM writes 0x81 to address 0xFF02 (Serial transfer control)
 * and an ASCII character to 0xFF01 (Serial transfer data)
 * This class checks if 0x81 has been written to 0xFF02, and if so it adds the character
 * from 0xFF01 to the msg, and resets 0xFF02 back to 0x00
 */

package com.szymon.gameboy.cpu.utils;

import com.szymon.gameboy.memory.Memory;
import com.szymon.gameboy.utils.Unsigned16;
import com.szymon.gameboy.utils.Unsigned8;

public class BlarggConsole 
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	private String szDebugMsg;
	private Memory memory;
	private boolean bEnabled;
	
	private final Unsigned16 u16ADDRESS1 = new Unsigned16(0xFF02);
	private final Unsigned16 u16ADDRESS2 = new Unsigned16(0xFF01);
	
	// ---------------------------------------------
	// constructors
	// ---------------------------------------------
	public BlarggConsole(Memory mem, boolean enabled)
	{
		szDebugMsg = "";
		memory = mem;
		bEnabled = enabled;
	}
	
	// ---------------------------------------------
	// methods
	// ---------------------------------------------
	//updates the msg every CPU cycle according to the before mentioned method
	public void updateDebug()
	{
		if (memory.readMemory(u16ADDRESS1).getValue() == 0x81 && bEnabled)
		{
			int iChar = memory.readMemory(u16ADDRESS2).getValue();
			
			szDebugMsg += Character.toString((char) iChar);
			
			memory.writeMemory(u16ADDRESS1, new Unsigned8());
		}
	}
	
	//prints the msg if it is not empty (so that it is only output by the Blargg ROMs)
	public void printDebug()
	{
		if (!szDebugMsg.equals("") && bEnabled)
		{
			System.out.println("Debug: " + szDebugMsg);
		}
	}
}
