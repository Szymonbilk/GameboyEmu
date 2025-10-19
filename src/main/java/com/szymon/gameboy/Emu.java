/**
 * @author 18bilkiewiczs
 * This is the main class that links together all of the other classes
 */
package com.szymon.gameboy;

import java.io.File;

import com.szymon.gameboy.cpu.CPU;
import com.szymon.gameboy.cpu.CPUInterrupts;
import com.szymon.gameboy.joypad.Joypad;
import com.szymon.gameboy.memory.Cart;
import com.szymon.gameboy.memory.Memory;
import com.szymon.gameboy.output.Screen;
import com.szymon.gameboy.output.TileViewer;
import com.szymon.gameboy.ppu.DMA;
import com.szymon.gameboy.ppu.LCD;
import com.szymon.gameboy.ppu.PPU;

public class Emu 
{
    // ---------------------------------------------
 	// class variables
 	// ---------------------------------------------
	private boolean bPaused;
	private boolean bRunning;
	
	private String szFile;
	
	private Cart cart;
	private Memory memory;
	private CPU cpu;
	private CPUInterrupts interrupts;
	private Cycle cycle;
	private PPU ppu;
	
	private DMA dma;
	private LCD lcd;
	
	private Joypad joypad;
	
	private TileViewer tileViewer;
	private Screen screen;
	
	private int iScaleFactor;
	
    // ---------------------------------------------
 	// constructors
 	// ---------------------------------------------
	public Emu(File file, int iSF)
	{
		reset(file, iSF);
	}
	
	public void reset(File file, int iSF) 
	{	
		bRunning = true;
		
		szFile = file.getName();
		
		iScaleFactor = iSF;
		
		cart = new Cart(file);
		joypad = new Joypad();
		
		dma = new DMA();
		lcd = new LCD();
		
		memory = new Memory(cart, dma, lcd, joypad);
		dma.initDMA(memory);
		
		cpu = new CPU(memory);
		interrupts = cpu.getCPUInterrupts();
		
		ppu = new PPU(memory, interrupts, lcd);
		
		cycle = new Cycle(memory, dma, interrupts, ppu);
		
		cpu.loadCycle(cycle);
		
		tileViewer = new TileViewer(memory, iScaleFactor);
		
		if (screen != null)
		{
			screen.reset(this, ppu, joypad, tileViewer);
		}
		else
		{
			screen = new Screen(this, ppu, joypad, tileViewer, iScaleFactor);
		}
	}
	
    // ---------------------------------------------
 	// setters
 	// ---------------------------------------------
	public void setPaused(boolean paused)
	{
		bPaused = paused;
	}
	
    // ---------------------------------------------
 	// getters
 	// ---------------------------------------------
	public Memory getMemory()
	{
		return memory;
	}
	
    // ---------------------------------------------
 	// methods
 	// ---------------------------------------------
	public void emuRun()
	{
		//set the title of the screen to the title of the ROM
		screen.setName(szFile);
		
		while (bRunning)
		{	
			if (bPaused)
			{
				continue;
			}
			
			//this runs the CPU, and if false is returned, it stops
			if (!cpu.stepCPU())
			{
				System.out.println("CPU Stopped");
				System.exit(0);
			}
			
			screen.updateJoypad();
			
			if (cart.getNeedSave())
			{
				cart.saveCartBattery();
			}
		}
	}
	
	public static void main(String[] args)
	{
		File file = new File("roms/tetris.gb");
		Emu emu = new Emu(file, 3);
		emu.emuRun();
	}
}
