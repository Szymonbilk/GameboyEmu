/**
 * @author 18bilkiewiczs
 * Class to allow for access to all of the Game Boy's memory
 */

package com.szymon.gameboy.memory;

import com.szymon.gameboy.joypad.Joypad;
import com.szymon.gameboy.ppu.DMA;
import com.szymon.gameboy.ppu.LCD;
import com.szymon.gameboy.utils.Unsigned16;
import com.szymon.gameboy.utils.Unsigned8;

/**
 * General Memory Map (courtesy of https://gbdev.io/pandocs/Memory_Map.html)
 * 0000-3FFF ROM bank 00 (from cartridge, usually a fixed bank)
 * 4000-7FFF ROM bank 01-NN (from cartridge, switchable bank if memory mapper present)
 * 8000-9FFF Video RAM (VRAM)
 * A000-BFFF External Ram (From cartridge, switchable bank if any)
 * C000-CFFF Work RAM (WRAM) (Bank 0)
 * D000-DFFF Work RAM (WRAM) (Bank 1-7, switchable on Color only)
 * E000-FDFF Echo RAM (mirror of C000-DDFF, use of this area is prohibited)
 * FE00-FE9F Object attribute memory (OAM)
 * FEA0-FEFF Not usable (prohibited)
 * FF00-FF7F I/0 Registers (Game Boy uses memory mapped I/O)
 * FF80-FFFE High RAM (HRAM)
 * FFFF-FFFF Interrupt Enable register (IE)
 */

public class Memory 
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	//Memory will have all of the relevant memory classes, and simply handles access to the values stored in these classes
	private Cart cart;
	private RAM ram;
	private IO io;
	private VRAM vram;
	private DMA dma;
	
	// ---------------------------------------------
	// constructors
	// ---------------------------------------------
	//When loading memory, it must be loaded with a game
	public Memory(Cart game, DMA dmaIn, LCD lcd, Joypad joypadIn)
	{
		loadGame(game, dmaIn, lcd, joypadIn);
	}
	
	// ---------------------------------------------
	// setters
	// ---------------------------------------------
	//handles the access for memory writes to all addresses
	public void writeMemory(Unsigned16 u16Address, Unsigned8 u8Data)
	{
		if (u16Address.getValue() < 0x8000)
		{
			//ROM Data
			cart.writeCart(u16Address, u8Data);
		}
		else if (u16Address.getValue() < 0xA000)
		{
			//VRAM
			vram.writeVRAM(u16Address, u8Data);
		}
		else if (u16Address.getValue() < 0xC000)
		{
			//Cart RAM
			cart.writeCart(u16Address, u8Data);
		}
		else if (u16Address.getValue() < 0xE000)
		{
			//WRAM
			ram.writeWRAM(u16Address, u8Data);
		}
		else if (u16Address.getValue() < 0xFE00)
		{
			//Echo RAM
			Unsigned16 u16MaskAddress = new Unsigned16(u16Address.getValue() - 0x2000);
			ram.writeWRAM(u16MaskAddress, u8Data);
		}
		else if (u16Address.getValue() < 0xFEA0)
		{
			//OAM
			vram.writeOAM(u16Address, u8Data);
		}
		else if (u16Address.getValue() < 0xFF00)
		{
			//not usable
		}
		else if (u16Address.getValue() < 0xFF80)
		{
			//IO Registers
			io.writeIO(u16Address, u8Data);
		}
		else if (u16Address.getValue() < 0xFFFF)
		{
			//HRAM
			ram.writeHRAM(u16Address, u8Data);
		}
		else if (u16Address.getValue() == 0xFFFF)
		{
			//Interrupt Enable Register
			io.writeIO(u16Address, u8Data);
		}
		else
		{
			System.err.println("Unsuported Write!");
			System.exit(0);
		}
	}
	
	// ---------------------------------------------
	// getters
	// ---------------------------------------------
	//handles memory reads from all addresses
	public Unsigned8 readMemory(Unsigned16 u16Address)
	{
		if (u16Address.getValue() < 0x8000)
		{
			//ROM Data
			return cart.readCart(u16Address);
		}
		else if (u16Address.getValue() < 0xA000)
		{
			//VRAM
			return vram.readVRAM(u16Address);
		}
		else if (u16Address.getValue() < 0xC000)
		{
			//Cart RAM
			return cart.readCart(u16Address);
		}
		else if (u16Address.getValue() < 0xE000)
		{
			//WRAM
			return ram.readWRAM(u16Address);
		}
		else if (u16Address.getValue() < 0xFE00)
		{
			//Echo RAM
			Unsigned16 u16MaskAddress = new Unsigned16(u16Address.getValue() - 0x2000);
			return ram.readWRAM(u16MaskAddress);
		}
		else if (u16Address.getValue() < 0xFEA0)
		{
			//OAM
			if (dma.getActive())
			{
				return new Unsigned8(0xFF);
			}
			return vram.readOAM(u16Address);
		}
		else if (u16Address.getValue() < 0xFF00)
		{
			//not usable
		}
		else if (u16Address.getValue() < 0xFF80)
		{
			//IO Registers
			return io.readIO(u16Address);
		}
		else if (u16Address.getValue() < 0xFFFF)
		{
			//HRAM
			return ram.readHRAM(u16Address);
		}
		else if (u16Address.getValue() == 0xFFFF)
		{
			//Interrupt Enable Register
			return io.readIO(u16Address);
		}
		else
		{
			System.err.println("Unsuported Read!");
			System.exit(0);
		}
		
		return new Unsigned8();
	}
	
	public boolean getCartNeedSave()
	{
		return cart.getNeedSave();
	}
	
	// ---------------------------------------------
	// methods
	// ---------------------------------------------
	//allows for a new game/cart to be loaded
	//TODO - this should act as a reset method, resetting the values of all memory locations
	public void loadGame(Cart game, DMA dmaIn, LCD lcd, Joypad joypad)
	{
		cart = game;
		ram = new RAM();
		io = new IO(lcd, dmaIn, joypad);
		vram = new VRAM();
		dma = dmaIn;
	}
	
	//allows for the cart memory to be saved
	public void saveCartBattery()
	{
		cart.saveCartBattery();
	}
	
	//allows for the div register to be incremented, as necessary from the cycle class
	public void incrementDIV()
	{
		io.incrementDIV();
	}
	
	//allows for 2 bytes to be read from memory in one go
	public Unsigned16 readMemory16(Unsigned16 u16Address)
	{
		Unsigned16 u16Num = new Unsigned16();
		Unsigned16 u16NewAddress = new Unsigned16(u16Address);
		u16Num.setLowByte(readMemory(u16NewAddress));
		u16NewAddress.increment();
		u16Num.setHighByte(readMemory(u16NewAddress));
		
		return u16Num;
	}
	
	//allows for 2 bytes to be written to memory in one go
	public void writeMemory16(Unsigned16 u16Address, Unsigned16 u16Data)
	{
		Unsigned16 u16NewAddress = new Unsigned16(u16Address);
		writeMemory(u16NewAddress, u16Data.getLowByte());
		u16NewAddress.increment();
		writeMemory(u16NewAddress, u16Data.getHighByte());
	}
}
