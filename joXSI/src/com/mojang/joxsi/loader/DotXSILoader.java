package com.mojang.joxsi.loader;

import java.io.*;
import java.util.*;

/**
 * Parses dotXSI file from an InputStream and returns a RootTemplate containing the entire scene.
 * 
 * <p>While the DotXSILoader and the templates can be used directly, it's probably easier to use the Scene class instead.
 * @see com.mojang.joxsi.Scene
 */
public class DotXSILoader
{
    private Header header;
    private InputStream in;
    private LineNumberReader reader;
    private StringTokenizer st;

    /**
     * Private constructor. Use the public static load method.
     * 
     * @param in an InputStream containing a dotXSI file
     */
    private DotXSILoader(InputStream in)
    {
        this.in = in;
    }

    /**
     * Reads the header from the dotXSI file, and makes sure it's valid.
     * 
     * @return the header.
     * @throws IOException if there's an io error, or if the parsing fails
     */
    private Header readHeader() throws IOException
    {
        byte[] buf = new byte[4];
        int read = 0;
        while (read<4) read+=in.read(buf, read, 4-read);
        String magicNumber = new String(buf, 0, 3);

        // "xsi" in the start of the file means it's a dotXSI file.
        if (!magicNumber.equals("xsi"))
            throw new IOException("Corrupt .xsi file: Bad magic number");

        Header header = new Header();

		read = 0;
		while (read<2) read+=in.read(buf, read, 2-read);
        header.majorVersion = Integer.parseInt(new String(buf, 0, 2));

		read = 0;
		while (read<2) read+=in.read(buf, read, 2-read);
        header.minorVersion = Integer.parseInt(new String(buf, 0, 2));

		read = 0;
		while (read<4) read+=in.read(buf, read, 4-read);
        header.formatType = new String(buf, 0, 3);

        if (header.formatType.equals("com"))
        {
			read = 0;
			while (read<4) read+=in.read(buf, read, 4-read);
            header.compressionType = new String(buf, 0, 3);
        }

		read = 0;
		while (read<4) read+=in.read(buf, read, 4-read);
        header.floatSize = Integer.parseInt(new String(buf, 0, 4));

        return header;
    }

    /**
     * Reads a string from the inputstream.
     * 
     * @return the parsed string
     * @throws IOException if there was an io error, or if the parsing failed.
     */
    private String readUntilEndOfString() throws IOException
    {
        // Horribly slow method of reading strings..
        
        StringBuffer st = new StringBuffer();
        boolean keepReading = true;

        while (keepReading)
        {
            int i = reader.read();
            if (i == -1)
                throw new IOException("Corrupt .xsi file: Unexpected EOF in string");

            char ch = (char)i;

            if (ch != '"') // add to the stringbuffer as long as the current character isn't '"' 
            {
                st.append(ch);
            }
            else
            {
                // If it was '"', read another character and make sure it's a ','
                ch = (char)reader.read();
                if (ch != ',')
                    throw new IOException("Corrupt .xsi file: Expected \",\", got \"" + ch + "\"");
                keepReading = false;
            }
        }

        return st.toString();
    }

    /**
     * Builds a tree of RawTemplates, representing the dotXSI file.
     * 
     * @return the root RawTemplate
     * @throws IOException if there's an io error, or if the parsing fails.
     */
    private RawTemplate parseRawTemplates() throws IOException
    {
        // Horrible way of parsing the tags one byte at the time.
        // Needs to be profiled and optimised.
        // TODO: Optimize template parsing in the XSILoader
        
        StringBuffer st = new StringBuffer();
        boolean keepReading = true;

        // Create an empty raw template for the root of the file.
        RawTemplate currentTemplate = new RawTemplate("RootTemplate", "");
        
        // Templates are kept on a stack during the parsing, and pushed/popped as {'s or }'s are encountered.
        List templateStack = new ArrayList();
        templateStack.add(currentTemplate);

        while (keepReading)
        {
            int i = reader.read();
            if (i >= 0) // Not end of stream
            {
                char ch = (char)i;
                
                if (ch != '"' && ch != '{' && ch != '}' && ch != ',')
                {
                    // Not the start of a string, start or end of a template, or separator between fields,
                    // so add to the current stringbuffer.
                    st.append(ch);
                }
                else
                {
                    if (ch == '"')
                    {
                        // Start of a string. Read until the end and add to the list of values for the current template.
                        String str = readUntilEndOfString();
                        currentTemplate.values.add(str);
                    }
                    else
                    {
                        String str = st.toString().trim();
                        if (ch == ',')
                        {
                            // Field separator. Find out if it's a float or an int, then add to the template.
                            if (str.indexOf('.') >= 0) // is float
                                currentTemplate.values.add(new Float(str));
                            else // is int
                                currentTemplate.values.add(new Integer(str));
                        }

                        if (ch == '{')
                        {
                            // Start a new template. Parse template name and template info.
                            StringTokenizer stt = new StringTokenizer(str);
                            String name = stt.nextToken();
                            String info = "";
                            if (stt.hasMoreTokens())
                                info = stt.nextToken();

                            // Push the new template to the stack.
                            currentTemplate = new RawTemplate(name, info);
                            templateStack.add(currentTemplate);
                        }

                        if (ch == '}')
                        {
                            // End of a template. Pop it from the stack, and add it to the parent template as a value.
                            RawTemplate template = currentTemplate;
                            templateStack.remove(templateStack.size() - 1);
                            currentTemplate = (RawTemplate)templateStack.get(templateStack.size() - 1);

                            currentTemplate.values.add(template);
                        }
                    }
                    st = new StringBuffer();
                }
            }
            else
            {
                keepReading = false;
            }
        }

        return currentTemplate;
    }

    /**
     * Builds a Template from a RawTemplate by finding the class that implements the template type,
     * creating a new instance, and running the build method.
     * 
     * <p>This is a recursive method that also builds all sub templates.
     * 
     * @param rawTemplate the RawTemplate that should be converted to a Template
     * @return the Template representing the RawTemplate
     */
    private Template buildTemplate(RawTemplate rawTemplate)
    {
        // Depth first. Iterate over all values and replace all RawTemplates with them with the real Templates
        for (int i=0; i<rawTemplate.values.size(); i++)
        {
            Object o = rawTemplate.values.get(i);
            if (o instanceof RawTemplate)
            {
                Template template = buildTemplate((RawTemplate)o);
                rawTemplate.values.set(i, template);
            }
        }

        try
        {
            // The template classes are named the same as the templates, so do a class lookup.
            // (If this is slow, cache the classes after they are found)
            Class c = Class.forName("com.mojang.joxsi.loader."+rawTemplate.name);
            Template template = (Template)c.newInstance();
            template.parseBlock(header, rawTemplate);
            return template;
        }
        catch (InstantiationException e)
        {
            // TODO: Deal with the exceptions when instantiating a template class in a better way.
            e.printStackTrace();
            throw new RuntimeException("Failed to instantiate class: " + e);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Failed to access class: " + e);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Couldn't find class: " + e);
        }
    }

    /**
     * Parses the input stream by first reading all the templates into a tree of RawTemplates,
     * then converting them into instances of their respective classes.
     * 
     * @return the RootTemplate that holds the dotXSI file
     * @throws IOException if there's an io error, or if the parsing failed
     */
    private RootTemplate parse() throws IOException
    {
        try
        {
            header = readHeader();

            // TODO: Add support for binary and compressed formats
            if (!header.formatType.equals("txt"))
                throw new IOException("Failed to read dotXSI: Only txt format supported");
            if (!(header.majorVersion == 3))
                throw new IOException("Failed to read dotXSI: Only 3.x files supported");
            
            reader = new LineNumberReader(new InputStreamReader(in));

            RawTemplate root = parseRawTemplates();
            return (RootTemplate)buildTemplate(root);
        }
        catch (Exception e)
        {
            int line = 1;
            if (reader!=null) line = reader.getLineNumber()+1;

            System.out.println("Error occured on line "+line);
            e.printStackTrace();
        	throw new IOException("Failed to read file: "+e);
        }
    }

    /**
     * Parses an InputStream and returns a RootTemplate containing the entire scene.
     * 
     * @param in an input stream that contains a dotXSI file
     * @return a RootTemplate containing the entire scene
     * @throws IOException if there's an io error, or if the parsing fails for any reason
     */
    public static RootTemplate load(InputStream in) throws IOException
    {
        // TODO: Add a ParseException or something.. throwing them as ioexceptions isn't right.
        return new DotXSILoader(in).parse();
    }
}