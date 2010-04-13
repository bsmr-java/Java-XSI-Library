package com.mojang.joxsi.renderer.shaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Notch
 * @author Egal
 */
public class ShaderSourceCode
{
    /** logger - Logging instance. */
    private static Logger logger = Logger.getLogger(ShaderSourceCode.class.getName());

    /** String that marks the start of a multi-line comment. */
    private static final String COMMENT_START = "/*";

    /** String that marks the end of a multi-line comment. */
    private static final String COMMENT_END = "*/";

    /** String that marks the start of a single-line comment. */
    private static final String COMMENT_SINGLE_LINE = "//";

    int stringCount;

    String[] strings;

    int[] stringLengths;

    private boolean blockSkip = false;

    // private int lineNum;

    /**
     * 
     * @param in
     * @throws IOException
     */
    public ShaderSourceCode(InputStream in) throws IOException
    {
        this(new BufferedReader(new InputStreamReader(in)));
    }

    /**
     * 
     * @param resourceName
     * @return ShaderSourceCode
     * @throws IOException
     */
    public static ShaderSourceCode fromResource(String resourceName) throws IOException
    {
        if (logger.isLoggable(Level.FINER))
        {
            logger.entering(ShaderSourceCode.class.getName(), "fromResource", resourceName);
        }
        return new ShaderSourceCode(ShaderSourceCode.class.getResourceAsStream(resourceName));
    }

    /**
     * 
     * @param br
     * @throws IOException
     */
    public ShaderSourceCode(BufferedReader br) throws IOException
    {
        String line = null;
        List<String> lines = new ArrayList<String>();

        blockSkip = false;
        // lineNum = 0;
        while ((line = br.readLine()) != null)
        {
            addLine(line, lines);
        }
        br.close();

        stringCount = lines.size();
        strings = new String[stringCount];
        stringLengths = new int[stringCount];
        for (int i = 0; i < stringCount; i++)
        {
            strings[i] = lines.get(i);
            stringLengths[i] = strings[i].length();
        }
        if (logger.isLoggable(Level.FINE))
        {
            logger.fine("Number of lines in shader source code: " + stringCount);
            // Log the shader source code
            if (logger.isLoggable(Level.FINEST))
            {
                StringBuilder sourceCode = new StringBuilder(1000);
                sourceCode.append("Shader source code: \n");
                for (String sourceLine : lines)
                {
                    sourceCode.append(sourceLine).append('\n');
                }
                logger.finest(sourceCode.toString());
            }
        }
    }

    /**
     * 
     * @param line
     * @param lines
     */
    private void addLine(String line, List<String> lines)
    {
        int i1 = line.indexOf(COMMENT_START);
        if (i1 < 0)
            i1 = 99999999;

        if (!blockSkip && line.indexOf(COMMENT_SINGLE_LINE) >= 0 && line.indexOf(COMMENT_SINGLE_LINE) < i1)
        {
            addLine(line.substring(0, line.indexOf(COMMENT_SINGLE_LINE)), lines);
        }
        else if (!blockSkip && line.indexOf(COMMENT_START) >= 0)
        {
            String pre = line.substring(0, line.indexOf(COMMENT_START));
            addLine(pre, lines);
            blockSkip = true;
            addLine(line.substring(line.indexOf(COMMENT_START) + 2), lines);
        }
        else if (blockSkip && line.indexOf(COMMENT_END) >= 0)
        {
            blockSkip = false;
            addLine(line.substring(line.indexOf(COMMENT_END) + 2), lines);
        }
        else
        {
            if (!blockSkip)
            {
                lines.add(line);
            }
        }
    }
}