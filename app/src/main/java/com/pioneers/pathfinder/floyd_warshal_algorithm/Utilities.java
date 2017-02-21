package com.pioneers.pathfinder.floyd_warshal_algorithm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Utilities{
	//parses csv file. returns list array of lines
	public static ArrayList<String> readCsv(String fileName)
	{
		ArrayList<String> busInfo= new ArrayList<String>();
		
		BufferedReader br = null;
        String line = "";

        try {

            br = new BufferedReader(new FileReader(fileName));
            while ((line = br.readLine()) != null) 
            {
            	busInfo.add(line);
            }

        } catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            if (br != null) 
            {
                try 
                {
                    br.close();
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
            }
        }
		
		return busInfo;
	}
}
