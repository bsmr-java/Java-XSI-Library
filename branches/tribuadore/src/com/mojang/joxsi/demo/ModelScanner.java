package com.mojang.joxsi.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.mojang.joxsi.Scene;
import com.mojang.joxsi.loader.ParseException;

/**
 * Main class for bulk scanning XSI models.
 * 
 * Recursively searches a directory for XSI files. Also searches inside JAR
 * files found during the search. The wanted root directory can be given on the
 * command line, otherwise defaults to the working directory.
 * 
 * System.err is written to a file called scanner.log in the root directory.
 * This file will not include model names, so it can't be used to debug the
 * errors, but it will show how many errors are present.
 * 
 * Ideally the file should be empty. :)
 * 
 * @author magnusrk
 * 
 */
public class ModelScanner
{
    public static void main(String[] args)
    {
        String rootDir = ".";
        System.out.println(rootDir);
        if (args.length > 0)
        {
            rootDir = args[0];
        }
        File modelDir = new File(rootDir);
        if (modelDir.isFile())
        {
            rootDir = ".";
            if (modelDir.getName().endsWith(".xsi"))
            {
                // TODO
            }
            else if (modelDir.getName().endsWith(".jar"))
            {
                System.err.println("Please specify a dotXSI model or a model directory.");
                System.exit(2);
            }
        }
        else if (!modelDir.isDirectory())
        {
            System.err.println("Could not find model directory, giving up.");
            System.exit(1);
        }

        try
        {
            File logFile = new File(rootDir, "scanner.log");
            FileOutputStream fos = new FileOutputStream(logFile);
            OutputStream dos = new DuplicateOutputStream(System.err, fos);
            System.setErr(new PrintStream(dos));
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Could not open output log, giving up.");
            e.printStackTrace();
            System.exit(3);
        }

        System.out.println("Starting model scan...");
        final long startTime = System.nanoTime();

        if (modelDir.isFile())
        {
            if (modelDir.getName().endsWith(".xsi"))
            {
                scanOneModel(new FileModelSource(modelDir));
                // TODO
            }
            else if (modelDir.getName().endsWith(".jar"))
            {
                // TODO not yet handled
            }
        }
        else
        {
            ModelSourceLocator modelList = new ModelSourceLocator(modelDir);
    
            for (ModelSource model : modelList)
            {
                if (!model.toString().endsWith(".xsi")) 
                    continue;
    
                scanOneModel(model);
            }
        }

        // Get elapsed time in milliseconds
        final float elapsedTime = (System.nanoTime() - startTime)/1000000F;
        System.out.println("Scan complete.");
        System.out.println("Models were scanned in " + elapsedTime + " milliseconds.");

    }

    /**
     * Load and parse one dotXSI model using {@link Scene#load(InputStream)}.
     * 
     * @param model
     *            the {@link ModelSource} describing the dotXSI Model to be
     *            scanned.
     */
    private static void scanOneModel(ModelSource model)
    {
        // stdout for progress display on the console.
        System.out.println("Scanning " + model);
        // stderr so we can see the model names in the log
        // TODO System.err.println("Scanning " + model);
        //logger.warning("Scanning " + model);

        try
        {
            InputStream lResourceAsStream = model.getStream();
            Scene.load(lResourceAsStream, model.getBasePath());
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Problem accessing model " + model);
            e.printStackTrace();
        }
        catch (ParseException e)
        {
            System.err.println("ParseException in model " + model);
            e.printStackTrace();
        }
        catch (IOException e)
        {
            System.err.println("IOException in model " + model);
            e.printStackTrace();
        }
    }

}

/**
 * Common interface to allow uniform handling of files in directories and files
 * inside JARs.
 * 
 * @author magnusrk
 * 
 */
interface ModelSource
{
    /**
     * @return The base path of the model source's backing.
     */
    public String getBasePath();

    /**
     * @return InputStream for accessing the model source.
     * @throws IOException
     *             if the source cannot be accessed.
     */
    public InputStream getStream() throws IOException;
}

/**
 * JAR file-based implementation of ModelSource.
 * 
 * @author magnusrk
 * 
 */
class JarModelSource implements ModelSource
{

    private JarFile jar;
    private JarEntry entry;

    public JarModelSource(JarFile jar, JarEntry entry)
    {
        this.jar = jar;
        this.entry = entry;
    }

    public String getBasePath()
    {
        String name = this.entry.getName();
        String basePath = null;
        int lastSlash = name.lastIndexOf(File.separatorChar);
        if (lastSlash != -1) basePath = name.substring(0, lastSlash + 1);
        return basePath;
    }

    public InputStream getStream() throws IOException
    {
        return this.jar.getInputStream(this.entry);
    }

    @Override
    public String toString()
    {
        return this.jar.getName() + " :: " + this.entry.getName();
    }
}

/**
 * File-based implementation of ModelSource.
 * 
 * @author magnusrk
 * 
 */
class FileModelSource implements ModelSource
{

    private File file;

    public FileModelSource(File file)
    {
        this.file = file;
    }

    public String getBasePath()
    {
        return this.file.getParent();
    }

    public InputStream getStream() throws FileNotFoundException
    {
        return new FileInputStream(this.file);
    }

    @Override
    public String toString()
    {
        return this.file.toString();
    }
}

/**
 * Iterable wrapper of RecursiveFileIterator for easy looping.
 * 
 * @author magnusrk
 * 
 */
class ModelSourceLocator implements Iterable<ModelSource>
{

    private File rootDirectory;

    public ModelSourceLocator(File rootDirectory)
    {
        this.rootDirectory = rootDirectory;
    }

    public Iterator<ModelSource> iterator()
    {
        return new ModelSourceLocatorIterator(this.rootDirectory);
    }

}

/**
 * Iterator producing ModelSource objects by scanning a given directory
 * recursively, and also looking inside JAR files.
 * 
 * @author magnusrk
 * 
 */
class ModelSourceLocatorIterator implements Iterator<ModelSource>
{

    private Queue<ModelSource> sources;
    private Queue<File> directories;

    public ModelSourceLocatorIterator(File rootDirectory)
    {
        this.sources = new LinkedList<ModelSource>();
        this.directories = new LinkedList<File>();
        this.directories.add(rootDirectory);
    }

    private void findMoreFiles()
    {
        File dir = directories.remove();
        for (File subfile : dir.listFiles())
        {
            // ModelScanner.log("Adding " + subfile + " (" +
            // subfile.isDirectory() + ")");
            if (subfile.isDirectory())
            {
                directories.add(subfile);
            }
            else if (subfile.getName().endsWith(".jar"))
            {
                JarFile jar;
                try
                {
                    jar = new JarFile(subfile);
                }
                catch (IOException e)
                {
                    System.err.println("Problem accessing " + subfile);
                    e.printStackTrace();
                    continue;
                }
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements())
                {
                    JarEntry entry = entries.nextElement();
                    if (!entry.isDirectory()) sources.add(new JarModelSource(jar, entry));
                }
            }
            else
            {
                sources.add(new FileModelSource(subfile));
            }
        }
    }

    public boolean hasNext()
    {
        while (this.sources.isEmpty())
        {
            if (this.directories.isEmpty()) break;
            this.findMoreFiles();
        }
        return this.sources.peek() != null;
    }

    public ModelSource next()
    {
        if (this.hasNext()) return this.sources.remove();
        throw new NoSuchElementException();
    }

    public void remove()
    {
        /* do nothing */
    }

}

/**
 * Forks input given to two different streams.
 * 
 * @author magnusrk
 * 
 */
class DuplicateOutputStream extends OutputStream
{

    private OutputStream one;
    private OutputStream two;

    public DuplicateOutputStream(OutputStream one, OutputStream two)
    {
        super();
        this.one = one;
        this.two = two;
    }

    @Override
    public void write(int b) throws IOException
    {
        this.one.write(b);
        this.two.write(b);
    }
}
