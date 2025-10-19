/**
 * @author 18bilkiewiczs
 * Enum to define the types of interrupts that can be requested
 */

package com.szymon.gameboy.cpu.utils;

public enum InterruptType
{
	//the value stored by each interrupt indicates which bit it is in the interrupt registers
	VBLANK(0b00001),
	LCD_STAT(0b00010),
	TIMER(0b00100),
	SERIAL(0b01000),
	JOYPAD(0b10000);
	
	public final int value;

    private InterruptType(int val) 
    {
        this.value = val;
    }
}