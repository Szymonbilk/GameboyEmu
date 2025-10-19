/**
 * @author 18bilkiewiczs
 * Class to emulate the cartridge to be loaded
 */

package com.szymon.gameboy.memory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.szymon.gameboy.utils.Unsigned16;
import com.szymon.gameboy.utils.Unsigned8;

/**
 * Courtesy of https://gbdev.io/pandocs/The_Cartridge_Header.html
 * The Cartridge Header (0100 - 014F) provides information about the game and the hardware
 * 0100-0103 Entry point (Should jump to actual main program)
 * 0104-0133 Nintendo logo (Bitmap image of the logo, must match to allow the cart to run)
 * 0134-0143 Title (Title of the game in upper case ASCII)
 * 013F-0142 Manufacturer code (Part of title in older cartridges, in newer onese contains a 4 character manufacturer code)
 * 0143 CGB flag (Part of title in older cartridges, CGB and later models interpret this byte to enable CGB mode or Non-CGB Mode)
 * 0144-0135 New licensee code (Two character ASCII code, indicates game's publisher. Only meaningful in old licensee is exactly 0x33)
 * 0146 SGB flag (Specifies whether the game supports SGB functions)
 * 0147 - Cartridge type (Indicates what kind of hardware is present on the cartridge, most notably its mapper)
 * 0148 - ROM size (indicates how much ROM is present on the cartridge)
 * 0149 - RAM size (indicates how much RAM is present on the cartridge, if any)
 * 014A - Destination code (Specifies whether this version of the game is intended to be sold in Japan or elsewhere)
 * 014B - Old licensee code (Used in pre-SGB cartridges to specify the game's publisher)
 * 014C - Mask ROM version number (Version number of the game, usually 0x00)
 * 014D - Header checksum (8-bit checksum computed from the cartridge header bytes 0134-014C)
 * 014E-014F - Global checksum (16-bit (big-endian) checksum computed as the sum of all bytes of the cartridge ROM, except these two checksum bytes)
 */

public class Cart 
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	//final private String szFILE_PATH = "Resources/ROMs/";
	private Unsigned8[] u8ROMData;	//the actual data of the ROM
	
	private String szFile;	//the file name of the ROM
	private String szTitle;	//the title of the ROM
	private String szType;	//the hardware type of the cart
	private int iROMSize;	//the number of bytes of ROM
	private int iRAMSize;	//the number of bytes of RAM
	private String szLicCode;	//the licensee code of the creator of the cart
	private int iVersion;	//the version of the cart
	private int iChecksum;	//the checksum (to check the cart is valid)
	
	//mbc1 related data
	private boolean bRAMEnable;
	private int iROMBank;
	private int iRAMBank;
	private boolean bMode;
	
	private int iROMBitMask;
	
	private Unsigned8[] u8RAMData;	
	
	//for battery
	private boolean bBattery;
	private boolean bNeedSave;
	
	// ---------------------------------------------
	// constructors
	// ---------------------------------------------
	public Cart(File file)
	{
		resetCart();
		loadCart(file);
	}
	
	// ---------------------------------------------
	// setters
	// ---------------------------------------------
	//write values to the cart (used for MBC switching, and for RAM (if present)
	public void writeCart(Unsigned16 u16Address, Unsigned8 u8Data)
	{		
		if (!getMBC1())
		{
			return;
		}
		
		//set RAM enable
		if (u16Address.getValue() < 0x2000)
		{
			if ((u8Data.getValue() & 0xF) == 0xA)
			{
				bRAMEnable = true;
			}
			else
			{
				bRAMEnable = false;
			}
		}
		//sets the ROM bank
		else if (u16Address.getValue() < 0x4000)
		{
			if ((u8Data.getValue() & 0b11111) == 0)
			{
				iROMBank = 1;
			}
			else
			{
				iROMBank = u8Data.getValue() & iROMBitMask;
			}
		}
		//sets the RAM bank
		else if (u16Address.getValue() < 0x6000)
		{
			iRAMBank = u8Data.getValue() & 0b11;
		}
		//sets the mode
		else if (u16Address.getValue() < 0x8000)
		{
			bMode = u8Data.getBit(0);
		}
		//writes to RAM
		else if (u16Address.getValue() >= 0xA000 && u16Address.getValue() < 0xC000)
		{
			if (bRAMEnable && u8RAMData.length != 0)
			{
				int iIndex = 0;
				
				if (iRAMSize == 32)
				{
					if (bMode)
					{
						iIndex = 0x2000 * iRAMBank + (u16Address.getValue() - 0xA000);
					}
					else
					{
						iIndex = u16Address.getValue() - 0xA000;
					}
				}
				else
				{
					iIndex = u16Address.getValue() - 0xA000;
				}
				
				bNeedSave = true;
				u8RAMData[iIndex].setValue(u8Data);
			}
		}
	}
	
	// ---------------------------------------------
	// getters
	// ---------------------------------------------
	//read values from the cart
	public Unsigned8 readCart(Unsigned16 u16Address)
	{
		int iIndex = 0;
		int iZeroBank = 0;
		int iHighBank = 0;
		
		if (u16Address.getValue() < 0x4000)
		{
			if (bMode)
			{
				if (iROMSize == 1024)
				{
					iZeroBank = (iRAMBank & 0b1) << 5;
				}
				else if (iROMSize == 2048)
				{
					iZeroBank = iRAMBank << 5;
				}
				
				iIndex = 0x4000 * iZeroBank + u16Address.getValue();
				
				return u8ROMData[iIndex];
			}
			else
			{
				return u8ROMData[u16Address.getValue()];
			}
		}
		else if (u16Address.getValue() < 0x8000)
		{
			iHighBank = iROMBank;
			if (iROMSize == 1024)
			{
				iHighBank |= (iRAMBank & 0b1) << 5;
			}
			else if (iROMSize == 2048)
			{
				iHighBank |= iRAMBank << 5;
			}
			
			iIndex = 0x4000 * iHighBank + (u16Address.getValue() - 0x4000);
			
			return u8ROMData[iIndex];
		}
		else if (u16Address.getValue() >= 0xA000 && u16Address.getValue() < 0xC000)
		{
			if (bRAMEnable && u8RAMData.length != 0)
			{
				if (iRAMSize == 32)
				{
					if (bMode)
					{
						iIndex = 0x2000 * iRAMBank + (u16Address.getValue() - 0xA000);
					}
					else
					{
						iIndex = u16Address.getValue() - 0xA000;
					}
				}
				else
				{
					iIndex = u16Address.getValue() - 0xA000;
				}
				
				return u8RAMData[iIndex];
			}
		}
		
		return new Unsigned8(0xFF);
	}
	
	//checks if the cart needs to be saved
	public boolean getNeedSave()
	{
		return bNeedSave;
	}
	
	//checks if the cart is MBC1
	public boolean getMBC1()
	{
		if (u8ROMData[0x0147].getValue() >= 1 && u8ROMData[0x0147].getValue() <= 3)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	//checks if there is a battery
	public boolean getBattery()
	{
		//MBC1 only for now
		if (u8ROMData[0x0147].getValue() == 3)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	//return the RAM size for a given byte
	public int getRAMSize(int iNum)
	{
		int iSize;
		switch (iNum)
		{
		case 0x00:
		case 0x01:
			iSize = 0;
			break;
		case 0x02:
			iSize = 8;
			break;
		case 0x03:
			iSize = 32;
			break;
		case 0x04:
			iSize = 128;
			break;
		case 0x05:
			iSize = 64;
			break;
		default:
			iSize = 0;	
		}
		return iSize;
	}
	
	//return the cart type for a given byte
	public String getCartType(int iNum)
	{
		String szCart = "";
		switch (iNum)
		{
		case 0x00:
			szCart = "ROM ONLY";
			break;
		case 0x01:
			szCart = "MBC1";
			break;
		case 0x02:
			szCart = "MBC1+RAM";
			break;
		case 0x03:
			szCart = "MBC1+RAM+BATTERY";
			break;
		case 0x05:
			szCart = "MBC2";
			break;
		case 0x06:
			szCart = "MBC2+BATTERY";
			break;
		case 0x08:
			szCart = "ROM+RAM";
			break;
		case 0x09:
			szCart = "ROM+RAM+BATTERY";
			break;
		case 0x0B:
			szCart = "MMM01";
			break;
		case 0x0C:
			szCart = "MMM01+RAM";
			break;
		case 0x0D:
			szCart = "MMM01+RAM+BATTERY";
			break;
		case 0x0F:
			szCart = "MBC3+TIMER+BATTERY";
			break;
		case 0x10:
			szCart = "MBC3+TIMER+RAM+BATTERY";
			break;
		case 0x11:
			szCart = "MBC3";
			break;
		case 0x12:
			szCart = "MBC3+RAM";
			break;
		case 0x13:
			szCart = "MBC3+RAM+BATTERY";
			break;
		case 0x19:
			szCart = "MBC5";
			break;
		case 0x1A:
			szCart = "MBC5+RAM";
			break;
		case 0x1B:
			szCart = "MBC5+RAM+BATTERY";
			break;
		case 0x1C:
			szCart = "MBC5+RUMBLE";
			break;
		case 0x1D:
			szCart = "MBC5+RUMBLE+RAM";
			break;
		case 0x1E:
			szCart = "MBC5+RUMBLE+RAM+BATTERY";
			break;
		case 0x20:
			szCart = "MBC6";
			break;
		case 0x22:
			szCart = "MBC7+SENSOR+RUMBLE+RAM+BATTERY";
			break;
		case 0xFC:
			szCart = "POCKET CAMERA";
			break;
		case 0xFD:
			szCart = "BANDAI TAMA5";
			break;
		case 0xFE:
			szCart = "HuC3";
			break;
		case 0xFF:
			szCart = "HuC1+RAM+BATTERY";
			break;
		default:
			szCart = "Unknown";
		}
		return szCart;
	}
	
	//return the licensee code for a given byte
	public String getLicCode(int iNum)
	{
		String szLic = "";
		switch (iNum)
		{
		case 0x00:
			szLic = "None";
			break;
		case 0x01:
			szLic = "Nintendo";
			break;
		case 0x33:
			String code = Character.toString((char) u8ROMData[0x0144].getValue()) + Character.toString((char) u8ROMData[0x0145].getValue());
			getNewLicCode(code);
			break;
		default:
			szLic = "Unkown/Unimplemented";
		}
		return szLic;
	}
	
	//return the new licensee code for a given two-byte code
	public String getNewLicCode(String code)
	{
		String szLic = "";
		switch(code)
		{
		case "00":
			szLic = "None";
			break;
		case "01":
			szLic = "Nintendo Research & Development 1";
			break;
		case "08":
			szLic = "Capcom";
			break;
		case "13":
			szLic = "EA (Electronic Arts)";
			break;
		case "18":
			szLic = "Hudson Soft";
			break;
		case "19":
			szLic = "B-AI";
			break;
		case "20":
			szLic = "KSS";
			break;
		case "22":
			szLic = "Planning Office WADA";
			break;
		case "24":
			szLic = "PCM Complete";
			break;
		case "25":
			szLic = "San-X";
			break;
		case "28":
			szLic = "Kemco";
			break;
		case "29":
			szLic = "SETA Corporation";
			break;
		case "30":
			szLic = "Viacom";
			break;
		case "31":
			szLic = "Nintendo";
			break;
		case "32":
			szLic = "Bandai";
			break;
		case "33":
			szLic = "Ocean Software/Acclaim Entertainment";
			break;
		case "34":
			szLic = "Konami";
			break;
		case "35":
			szLic = "HectorSoft";
			break;
		case "37":
			szLic = "Taito";
			break;
		case "38":
			szLic = "Hudson Soft";
			break;
		case "39":
			szLic = "Banpresto";
			break;
		case "41":
			szLic = "Ubi Soft";
			break;
		case "42":
			szLic = "Atlus";
			break;
		case "44":
			szLic = "Malibu Interactive";
			break;
		case "46":
			szLic = "Angel";
			break;
		case "47":
			szLic = "Bullet-Proof Software";
			break;
		case "49":
			szLic = "Irem";
			break;
		case "50":
			szLic = "Absolute";
			break;
		case "51":
			szLic = "Acclaim Entertainment";
			break;
		case "52":
			szLic = "Activision";
			break;
		case "53":
			szLic = "Sammy USA Corporation";
			break;
		case "54":
			szLic = "Konami";
			break;
		case "55":
			szLic = "Hi Tech Expressions";
			break;
		case "56":
			szLic = "LJN";
			break;
		case "57":
			szLic = "Matchbox";
			break;
		case "58":
			szLic = "Mattel";
			break;
		case "59":
			szLic = "Milton Bradley Company";
			break;
		case "60":
			szLic = "Titus Interactive";
			break;
		case "61":
			szLic = "Virgin Games Ltd.";
			break;
		case "64":
			szLic = "Lucasfilm Games";
			break;
		case "67":
			szLic = "Ocean Software";
			break;
		case "69":
			szLic = "EA (Electronic Arts)";
			break;
		case "70":
			szLic = "Infogrames";
			break;
		case "71":
			szLic = "Interplay Entertainment";
			break;
		case "72":
			szLic = "Broderbund";
			break;
		case "73":
			szLic = "Sculptured Software";
			break;
		case "75":
			szLic = "The Sales Curve Limited";
			break;
		case "78":
			szLic = "THQ";
			break;
		case "79":
			szLic = "Accolade";
			break;
		case "80":
			szLic = "Misawa Entertainment";
			break;
		case "83":
			szLic = "lozc";
			break;
		case "86":
			szLic = "Tokuma Shoten";
			break;
		case "87":
			szLic = "Tsukuda Original";
			break;
		case "91":
			szLic = "Chunsoft Co.";
			break;
		case "92":
			szLic = "Video System";
			break;
		case "93":
			szLic = "Ocean Software/Acclaim Entertainment";
			break;
		case "95":
			szLic = "Varie";
			break;
		case "96":
			szLic = "Yonezawa/s'pal";
			break;
		case "97":
			szLic = "Kaneko";
			break;
		case "99":
			szLic = "Pack-In-Video";
			break;
		case "9H":
			szLic = "Bottom Up";
			break;
		case "A4":
			szLic = "Konami (Yu-Gi-Oh!)";
			break;
		case "BL":
			szLic = "MTO";
			break;
		case "DK":
			szLic = "Kodansha";
			break;
		default:
			szLic = "Unknown";
		}
		return szLic;
	}
	
	// ---------------------------------------------
	// methods
	// ---------------------------------------------
	//resets the cart to base values (no game loaded)
	private void resetCart()
	{
		szTitle = "";
		szType = "";
		iROMSize = 0;
		iRAMSize = 0;
		szLicCode = "";
		iVersion = 0;
		iChecksum = 0;
		u8ROMData = new Unsigned8[1];
	}
	
	//setups cart banking, currently only for MBC1
	private void setupCartBanking()
	{
		bRAMEnable = false;
		iROMBank = 1;
		iRAMBank = 0;
		bMode = false;
		
		if (iROMSize == 32)
		{
			iROMBitMask = 0b1;
		}
		else if (iROMSize == 64)
		{
			iROMBitMask = 0b11;
		}
		else if (iROMSize == 128)
		{
			iROMBitMask = 0b111;
		}
		else if (iROMSize == 256)
		{
			iROMBitMask = 0b1111;
		}
		else
		{
			iROMBitMask = 0b11111;
		}
		
		bBattery = getBattery();
		bNeedSave = false;
		
		//check the ram size to determine how many banks
		if (iRAMSize > 0)
		{
			int iRAMMultipler = iRAMSize / 8;
			u8RAMData = new Unsigned8[0x2000 * iRAMMultipler];
			
			//initialise the values
			for (int i = 0; i < u8RAMData.length; i++)
			{
				u8RAMData[i] = new Unsigned8();
			}
		}
		
		if (bBattery)
		{
			loadCartBattery();
		}
	}
	
	//loads any relevant save file for the game
	private void loadCartBattery()
	{
		//remove the file extension from the file (.gb), and add .sav for a save
		String szSaveFile = szFile.substring(0, (szFile.length() - 3));
		szSaveFile += ".sav";
		
		try 
		{
			//get the size of the file in bytes
			Path path = Paths.get(szSaveFile);
			//read all byte data of the file
			//and load from start address
			byte[] bytes = Files.readAllBytes(path);
			
			int i = 0;
			while (i < bytes.length && i < u8RAMData.length)
			{
				u8RAMData[i].setValue(bytes[i]);
				i++;
			}
		} 
		catch (IOException e) 
		{
			System.out.println("No save file!");
		}
	}
	
	//stores the RAM data to a file
	public void saveCartBattery()
	{
		bNeedSave = false;
		//remove the file extension from the file (.gb), and add .sav for a save
		String szSaveFile = szFile.substring(0, (szFile.length() - 3));
		szSaveFile += ".sav";
		
		FileWriter writer;
		
		//create file and clear it
		try 
		{
			File myFile = new File(szSaveFile);
			
			writer = new FileWriter(myFile);
			writer.write("");
			writer.close();
			
			writer = new FileWriter(myFile, true);
			
			//write all ram data to the file
			for (int i = 0; i < u8RAMData.length; i++)
			{
				writer.write(u8RAMData[i].getValue());
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	//loads a file into the cartridge
	public boolean loadCart(File file)
	{
		try 
		{
			szFile = file.getAbsolutePath();
			//get the size of the file in bytes
			Path path = Paths.get(file.getAbsolutePath());
			//read all byte data of the file
			//and load from start address
			byte[] bytes = Files.readAllBytes(path);
			u8ROMData = new Unsigned8[bytes.length];
			for (int i = 0; i < bytes.length; i++)
			{
				u8ROMData[i] = new Unsigned8(bytes[i]);
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		}
		
		//get the title
		szTitle = "";
		for (int i = 0x0134; i <= 0x0143; i++)
		{
			String character = Character.toString((char) u8ROMData[i].getValue());
			if (character.matches("[a-zA-Z0-9 ]"))
			{
				szTitle = szTitle + character;
			}
		}
		//get the type
		szType = getCartType(u8ROMData[0x0147].getValue());
		//get the ROM size
		iROMSize = 32 * (1 << u8ROMData[0x0148].getValue());
		//get the RAM size
		iRAMSize = getRAMSize(u8ROMData[0x0149].getValue());
		//get the LIC code
		szLicCode = getLicCode(u8ROMData[0x014B].getValue());
		//get the ROM version
		iVersion = u8ROMData[0x014C].getValue();
		
		//output data
		System.out.println("Cartridge Loaded:"
				+ "\n Title 		: " + szTitle
				+ "\n Type	 	: " + szType
				+ "\n ROM Size 	: " + iROMSize + " KiB"
				+ "\n RAM Size 	: " + iRAMSize + " KiB"
				+ "\n LIC Code 	: " + szLicCode
				+ "\n ROM Vers 	: " + iVersion);
		
		//check that the checksum is valid
		iChecksum = 0;
		for (int i = 0x0134; i <= 0x014C; i++)
		{
			iChecksum = iChecksum - u8ROMData[i].getValue() - 1;
		}
		System.out.print(" Checksum	: ");
		if (u8ROMData[0x014D].getValue() != (iChecksum & 0xFF))
		{
			System.out.println("Failed");
			return false;
		}
		else
		{
			System.out.println("Passed");
		}

		setupCartBanking();
		
		return true;
	}
}
