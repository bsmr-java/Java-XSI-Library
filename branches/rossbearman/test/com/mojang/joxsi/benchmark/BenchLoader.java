package com.mojang.joxsi.benchmark;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import com.mojang.joxsi.loader.DotXSILoader;
import com.mojang.joxsi.loader.ParseException;

/**
 * Simple benchmarker Parses the model(s) 1,000 times and outputs the speed
 *
 * @author Cheater
 * @author egal
 */
public class BenchLoader
{
    /** The number of loops to perform */
    private static final long tries = 1000;

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
            runTest(path);
        }
        else
        {
            long overallStartTime = System.currentTimeMillis();
            // Just the possibility to load more Models at start for better time measurement
            for (int i = 0; i < args.length; i++)
            {
                logger.info("Going to load '" + args[i] + "' as a model");
                path = "/" + args[i];
                runTest(path);
            }
            long time = System.currentTimeMillis() - overallStartTime;
            logger.info("It took " + time + " milliseconds to load all the models.");
        }
        System.exit(0);
    }

    /**
     * Run the tests by loading the model from the given path.<br>
     * Warm up the JVM by loading the model 10 times before running the timed
     * test.
     *
     * @param path
     *            the path of the model to load
     * @throws IOException
     * @throws ParseException
     */
    private static void runTest(String path) throws IOException, ParseException
    {
        // Warming up
        for (int x = 0; x < 10; x++)
        {
            DotXSILoader.load(BenchLoader.class.getResourceAsStream(path));
        }
        logger.info("Finished warming up with model " + path);
        long starttime = System.currentTimeMillis();
        for (int x = 0; x < tries; x++)
        {
            DotXSILoader.load(BenchLoader.class.getResourceAsStream(path));
        }
        long time = System.currentTimeMillis() - starttime;
        logger.info("It took " + time + " milliseconds to load " + path + " as a model " + tries + " models.");
        logger.info((time / tries) + " milliseconds per model to load " + path);
    }
}
