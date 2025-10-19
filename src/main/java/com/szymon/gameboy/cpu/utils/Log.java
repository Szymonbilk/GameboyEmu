/**
 * @author 18bilkiewiczs
 * This class, if enabled, outputs the state of the CPU every cycle to a txt file
 * This is done in the format specified by Gameboy Doctor to enable the use of that tool
 */

package com.szymon.gameboy.cpu.utils;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import com.szymon.gameboy.cpu.CPU;
import com.szymon.gameboy.cpu.CPURegisters;
import com.szymon.gameboy.memory.Memory;
import com.szymon.gameboy.utils.Unsigned16;
import com.szymon.gameboy.utils.Unsigned8;

public class Log 
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	private final String szFILE_PATH = "rsc/debug/log/log1.txt";
	private final int iMAX_LINES = 10000000;
	
	private CPU cpu;
	private Memory memory;
	private CPURegisters registers;
	private boolean bCheck;
	private FileWriter writer;
	private int iCpuTicks;
	
	// ---------------------------------------------
	// constructors
	// ---------------------------------------------
	public Log(CPU processor, boolean bLog)
	{
		iCpuTicks = 0;
		
		cpu = processor;
		memory = cpu.getMemory();
		registers = cpu.getCPURegisters();
		
		bCheck = bLog;
		
		if (bLog)
		{
			//create file and clear it
			try 
			{
				File myFile = new File(szFILE_PATH);
				
				writer = new FileWriter(myFile);
				writer.write("");
				writer.close();
				
				writer = new FileWriter(myFile, true);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	// ---------------------------------------------
	// methods
	// ---------------------------------------------
	//logs current cpu state in specific format
	public void log()
	{
		//only write to the file for a certain number of lines, otherwise stop writing
		//this is to stop the files from becoming too big, as the file size will grow
		//continuously as the CPU runs
		if (bCheck && iCpuTicks < iMAX_LINES)
		{
			iCpuTicks++;
			try 
			{
				Unsigned8 u8A = new Unsigned8((Unsigned8) registers.getRegValue(RegType.A));
				Unsigned8 u8F = new Unsigned8((Unsigned8) registers.getRegValue(RegType.F));
				Unsigned8 u8B = new Unsigned8((Unsigned8) registers.getRegValue(RegType.B));
				Unsigned8 u8C = new Unsigned8((Unsigned8) registers.getRegValue(RegType.C));
				Unsigned8 u8D = new Unsigned8((Unsigned8) registers.getRegValue(RegType.D));
				Unsigned8 u8E = new Unsigned8((Unsigned8) registers.getRegValue(RegType.E));
				Unsigned8 u8H = new Unsigned8((Unsigned8) registers.getRegValue(RegType.H));
				Unsigned8 u8L = new Unsigned8((Unsigned8) registers.getRegValue(RegType.L));
				
				Unsigned16 u16SP = new Unsigned16((Unsigned16) registers.getRegValue(RegType.SP));
				Unsigned16 u16PC = new Unsigned16((Unsigned16) registers.getRegValue(RegType.PC));
				
				Unsigned16 u16PCForMem = new Unsigned16(u16PC);
				
				Unsigned8 u8Mem1 = new Unsigned8(memory.readMemory(u16PCForMem));
				u16PCForMem.increment();
				Unsigned8 u8Mem2 = new Unsigned8(memory.readMemory(u16PCForMem));
				u16PCForMem.increment();
				Unsigned8 u8Mem3 = new Unsigned8(memory.readMemory(u16PCForMem));
				u16PCForMem.increment();
				Unsigned8 u8Mem4 = new Unsigned8(memory.readMemory(u16PCForMem));
				
				
				String out = String.format("A:%02x F:%02x B:%02x C:%02x D:%02x E:%02x H:%02x L:%02x SP:%04x PC:%04x PCMEM:%02x,%02x,%02x,%02x", u8A.getValue(), u8F.getValue(), u8B.getValue(), u8C.getValue(), u8D.getValue(), u8E.getValue(), u8H.getValue(), u8L.getValue(), u16SP.getValue(), u16PC.getValue(), u8Mem1.getValue(), u8Mem2.getValue(), u8Mem3.getValue(), u8Mem4.getValue());
				out += "\n";
				
				writer.write(out);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		else if (iCpuTicks == iMAX_LINES)
		{
			this.close();
		}
	}
	
	//close the writer
	public void close()
	{
		if (bCheck)
		{
			try 
			{
				writer.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
}

