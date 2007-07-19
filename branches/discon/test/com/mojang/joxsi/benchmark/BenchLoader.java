package com.mojang.joxsi.benchmark;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import com.mojang.joxsi.loader.DotXSILoader;
import com.mojang.joxsi.loader.ParseException;

/**
 * Simple benchmarker Parses the model 1,000 times and outputs the speed
 * 
 * @author Cheater
 */
public class BenchLoader
{
    /** The number of loops to perform */
    private static final long tries = 100;

    private static Logger logger = Logger.getLogger(BenchLoader.class.getName());
    private static ConsoleHandler ch = new ConsoleHandler();

    /**
     * Main entry point of the application.
     * 
     * @param args
     *            the arguments passed from the commandline
     * @throws IOException
     *             if the model can't be loaded.
     * @throws ParseException
     *             if the parsing fails for any reason
     */
    public static void main(String[] args) throws IOException, ParseException
    {
        logger.addHandler(ch);
        String path = "";

        if (args.length == 0)
        {
            logger.info("No arguments. We're probably run from webstart, so load the default model");
            // No arguments. We're probably run from webstart, so load the
            // default model
            path = "/DanceMagic.xsi";
        }
        else
        {
            logger.info("Going to load '" + args[0] + "' as a model");
            path = "/" + args[0];
        }
        // Warming up
        for (int x = 0; x < 10; x++)
        {
            DotXSILoader.load(BenchLoader.class.getResourceAsStream(path));
        }

        long starttime = System.currentTimeMillis();
        for (int x = 0; x < tries; x++)
        {
            DotXSILoader.load(BenchLoader.class.getResourceAsStream(path));
        }
        long time = System.currentTimeMillis() - starttime;
        logger.info("It took " + time + " milliseconds to load " + tries + " models.");
        logger.info((time / tries) + " milliseconds per model.");
        System.exit(0);
    }

}
