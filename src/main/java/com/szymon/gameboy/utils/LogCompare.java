/**
 * @author 18bilkiewiczs
 * Class for comparing the log file outputs between two log files
 * Going to be used to compare old code logs with the new code logs
 * due to any issues that have arisen when rewriting the code
 */

package com.szymon.gameboy.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class LogCompare {
	
	final private String szPATH = "rsc/debug/log/";
	
	private Scanner scannerLog1;
	private Scanner scannerLog2;
	
	private String szLineLog1;
	private String szLineLog2;
	private String szPrevLine;
	
	private long lLineNo;
	
	public LogCompare()
	{
		szLineLog1 = "";
		szLineLog2 = "";
		szPrevLine = "";
		lLineNo = 0;
		
		try 
		{
			scannerLog1 = new Scanner(new File(szPATH + "log1.txt"));
			scannerLog2 = new Scanner(new File(szPATH + "log2.txt"));
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	
	private void compareLines()
	{
		try 
		{
			if (scannerLog1.hasNext())
			{
				szLineLog1 = scannerLog1.nextLine();
			}
			else 
			{
				exitNoIssue();
			}
			if (scannerLog2.hasNext())
			{
				szLineLog2 = scannerLog2.nextLine();
			}
			else 
			{
				exitNoIssue();
			}
			lLineNo++;
			
			if (!szLineLog1.equals(szLineLog2))
			{
				exitMismatch();
			}
			else 
			{
				szPrevLine = szLineLog1;
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private void exitMismatch()
	{
		closeScanners();
		System.out.println("Mismatch on line: " + lLineNo);
		System.out.println("LOG1: " + szLineLog1);
		System.out.println("LOG2: " + szLineLog2);
		System.out.println();
		System.out.println("PREV: " + szPrevLine);
		System.exit(1);
	}
	
	private void exitNoIssue()
	{
		closeScanners();
		System.out.println("Logs matched for: " + lLineNo + " lines!");
		System.exit(0);
	}
	
	private void closeScanners()
	{
		scannerLog1.close();
		scannerLog2.close();
	}
	
	public void run()
	{
		while(true)
		{
			compareLines();
		}
	}
	
	public static void main(String[] args) 
	{
		LogCompare compare = new LogCompare();
		compare.run();
	}
}
