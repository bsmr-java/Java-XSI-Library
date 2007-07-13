package com.mojang.joxsi.benchmark;

import java.io.IOException;
import java.io.InputStream;

import com.mojang.joxsi.Scene;
import com.mojang.joxsi.demo.TimeIt;
import com.mojang.joxsi.loader.*;

public class BenchLoader
{
	private static final long tries = 1000;

	/**
     * Main entry point of the application.
     *  
     * @param args the arguments passed from the commandline
     * @throws IOException if the model can't be loaded.
     * @throws ParseException if the parsing fails for any reason
	 */
	public static void main(String[] args) throws IOException, ParseException
	{
		String path = "";
		
		if (args.length == 0)
        {
            System.out.println("No arguments. We're probably run from webstart, so load the default model");
            // No arguments. We're probably run from webstart, so load the default model
            path = "/DanceMagic.xsi";
        }
        else
        {
            System.out.println("Going to load '" + args[0] + "' as a model");
            path = "/" + args[0];
        }
		
		long starttime = System.currentTimeMillis();
		for (int x = 0; x < tries; x++)
		{
			DotXSILoader.load(BenchLoader.class.getResourceAsStream(path));
		}
		long time = System.currentTimeMillis() - starttime;
		System.out.println("It took " + time + " milliseconds to load " + tries + " models."); 
		System.out.println((time / tries) + " milliseconds per model.");
		System.exit(0);
	}

}
