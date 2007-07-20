package com.mojang.joxsi.loader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Parses dotXSI file from an InputStream and returns a RootTemplate containing the entire scene.
 * 
 * <p>While the DotXSILoader and the templates can be used directly, it's probably easier to use the Scene class instead.
 * @see com.mojang.joxsi.Scene
 */
public class DotXSILoader
{
    /**
     * Prefixed to the template name to load the class to parse the file:
     * com.mojang.joxsi.loader.
     */
    private static final String JOXSI_LOADER_PACKAGE_PREFIX = "com.mojang.joxsi.loader.";
    /** Full stop character used in parsing the dotXSI files. */
    private static final char FULL_STOP = '.';
    /** Comma character used in parsing the dotXSI files. */
    private static final char COMMA = ',';
    /** Close brace character used in parsing the dotXSI files. */
    private static final char CLOSE_BRACE = '}';
    /** Open brace character used in parsing the dotXSI files. */
    private static final char OPEN_BRACE = '{';
    /** Double quotes character used in parsing the dotXSI files. */
    private static final char DOUBLE_QUOTES = '"';
    
    private Header header;
    private InputStream inputStream;
    private InputStreamReader reader;
    private StringBuilder stringbBuilder;

    /** Buffer used when reading from the stream. Used to avoid calling stream.read(). */
    private char buffer[];
    /** Current position in the buffer. */
    private int bufferPos;
    /** Current length of valid data in the buffer. */
    private int bufferLength;
    /** Max capacity of the buffer. 16 KB seems to be enough. */
    private static final int INPUT_BUFFER_SIZE = 1024 * 16;

    /**
     * Private constructor. Use the public static load method.
     * 
     * @param in an InputStream containing a dotXSI file
     */
    private DotXSILoader(InputStream inputStream)
    {
        this.inputStream = inputStream;
        stringbBuilder = new StringBuilder(1024);
    }

   /**
    * Reads the header from the dotXSI file, and makes sure it's valid.
    * 
    * @return the header.
    * @throws IOException if there's an io error.
    * @throws ParseException if the parsing fails for any reason
    *  Standard Headerformat  (program) (export version)(format) (xsi bit version)
    */
    private Header readHeader() throws IOException, ParseException
    {
        byte[] buf = new byte[4];
        int read = 0;
        while (read<4) read+=inputStream.read(buf, read, 4-read);
        String magicNumber = new String(buf, 0, 3);

        // "xsi" in the start of the file means it's a dotXSI file.
        if (!magicNumber.equals("xsi"))
            throw new ParseException("Corrupt .xsi file: Bad magic number");

        Header header = new Header();

		read = 0;
		while (read<2) read+=inputStream.read(buf, read, 2-read);
        header.majorVersion = Integer.parseInt(new String(buf, 0, 2));

		read = 0;
		while (read<2) read+=inputStream.read(buf, read, 2-read);
        header.minorVersion = Integer.parseInt(new String(buf, 0, 2));

		read = 0;
		while (read<4) read+=inputStream.read(buf, read, 4-read);
        header.formatType = new String(buf, 0, 3);

        if (header.formatType.equals("com"))
        {
			read = 0;
			while (read<4) read+=inputStream.read(buf, read, 4-read);
            header.compressionType = new String(buf, 0, 3);
        }

		read = 0;
		while (read<4) read+=inputStream.read(buf, read, 4-read);
        header.floatSize = Integer.parseInt(new String(buf, 0, 4));

        return header;
    }

    /**
     * Reads a string from the inputstream (until the next DOUBLE_QUOTES).
     * Ensures that a COMMA follows the DOUBLE_QUOTES.
     * 
     * @return the parsed string
     * @throws IOException if there was an IO error.
     * @throws ParseException if the parsing fails for any reason
     */
    private String readUntilEndOfString() throws IOException, ParseException
    {
        stringbBuilder.setLength(0);
   	    char ch;
   	    boolean stop = false;

   	    do
   	    {
   	        while(bufferPos<bufferLength)
       	    {
       	        ch = buffer[bufferPos++];

       	        if (ch != DOUBLE_QUOTES)
       	        {
       	            stringbBuilder.append(ch);
       	        }
       	        else
       	        {
       	            stop = true;
       	            break;
       	        }
       	    }
   	    }while(!stop && refillBuffer());
   	    
   	    if (!stop)
            throw new ParseException("Corrupt .xsi file: Unexpected EOF in string");

   	    if (bufferPos >= bufferLength)
   	        if (!refillBuffer())
   	            throw new ParseException("Corrupt .xsi file: Unexpected EOF in string");
   	    if ((ch = buffer[bufferPos++]) != COMMA)
            throw new ParseException("Corrupt .xsi file: Expected \",\", got \"" + ch + "\"");

        return stringbBuilder.toString();
    }

    /**
     * Reads more data from the stream reader.
     * 
     * @return true if it read data, false on EOF.
     * @throws IOException if there's an IO error.
     */
    private boolean refillBuffer() throws IOException
    {
        bufferLength = reader.read(buffer, 0, INPUT_BUFFER_SIZE);
        bufferPos = 0;
        return(bufferLength > 0);
    }
    
    /**
     * Builds a tree of RawTemplates, representing the dotXSI file.
     * 
     * @return the root RawTemplate
     * @throws IOException if there's an IO error.
     * @throws ParseException if the parsing fails for any reason
     */
    private RawTemplate parseRawTemplates() throws IOException, ParseException
    {
        // Horrible way of parsing the tags one byte at the time.
        // Needs to be profiled and optimized.
        // TODO: Optimize template parsing in the XSILoader

        stringbBuilder.setLength(0);

        // Create an empty raw template for the root of the file.
        RawTemplate currentTemplate = new RawTemplate(RawTemplate.ROOT_TEMPLATE, "");
        
        // Templates are kept on a stack during the parsing, and pushed/popped as {'s or }'s are encountered.
        List<Object> templateStack = new ArrayList<Object>();
        templateStack.add(currentTemplate);
        char ch;
        String str;
        StringTokenizer stt;
        String name ;
        String info = "";
        
        StringBuilder tmpStorage = new StringBuilder(1024);
        int marker = bufferPos;
        int len = 0;

        while(refillBuffer())
        {
            while(bufferPos < bufferLength)
            {
                ch = buffer[bufferPos++];
                  
                switch(ch)
                {
                
                    case DOUBLE_QUOTES:
                        stringbBuilder.append(tmpStorage);
                        tmpStorage.setLength(0);
                        stringbBuilder.append(buffer, marker, len);
                        
                        // Start of a string. Read until the end and add to the list of values for the current template.
                        str = readUntilEndOfString();
                        currentTemplate.values.add(str);
                        
                        marker = bufferPos;
                        len = 0;

                        stringbBuilder.setLength(0);
                        break;
                
                    case COMMA:
                        stringbBuilder.append(tmpStorage);
                        tmpStorage.setLength(0);
                        stringbBuilder.append(buffer, marker, len);
                        marker = bufferPos;
                        len = 0;
                        str = stringbBuilder.toString().trim();
                        
                        // Field separator. Find out if it's a string, a float or an integer, then add to the template.
                        if (str.indexOf(FULL_STOP) >= 0)   // it's float
                            currentTemplate.values.add(new Float(str));
                        else                                    // it's int
                            currentTemplate.values.add(new Integer(str));

                        stringbBuilder.setLength(0);
                        break;
                        
                    case OPEN_BRACE:
                        stringbBuilder.append(tmpStorage);
                        tmpStorage.setLength(0);
                        stringbBuilder.append(buffer, marker, len);
                        marker = bufferPos;
                        len = 0;
                        str = stringbBuilder.toString().trim();
                        
                        // Start a new template. Parse template name and template info.
                        stt = new StringTokenizer(str);
                        name = stt.nextToken();
                        info = "";
                        if (stt.hasMoreTokens())
                            info = stt.nextToken();

                        // Push the new template to the stack.
                        currentTemplate = new RawTemplate(name, info);
                        templateStack.add(currentTemplate);
                        
                        stringbBuilder.setLength(0);
                        break;
                        
                    case CLOSE_BRACE:
                        stringbBuilder.append(tmpStorage);
                        tmpStorage.setLength(0);
                        stringbBuilder.append(buffer, marker, len);
                        marker = bufferPos;
                        len = 0;
                        str = stringbBuilder.toString().trim();

                        // End of a template. Pop it from the stack, and add it to the parent template as a value.
                        RawTemplate template = currentTemplate;
                        templateStack.remove(templateStack.size() - 1);
                        currentTemplate = (RawTemplate)templateStack.get(templateStack.size() - 1);
                        currentTemplate.values.add(template);
                        
                        stringbBuilder.setLength(0);
                        break;
                        
                    default:
                        // Not the start of a string, start or end of a template, or separator between fields,
                        // so add to the current stringbuffer.
                        //stringbBuilder.append(ch);
                        len++;
                        break;
                }
            }
            tmpStorage.append(buffer, marker, len);
            marker = 0;
            len = 0;
        }

        return currentTemplate;
    }

    /*
     * Milbo's method for building a tree of RawTemplates, representing the dotXSI file.
     * 
     * @return the root RawTemplate
     * @throws IOException if there's an io error.
     * @throws ParseException if the parsing fails for any reason
     *
    
    private RawTemplate parseRawTemplates_MilboMethodTest() throws IOException, ParseException
    {
        stringbBuilder.setLength(0);
//       StringBuffer st = new StringBuffer();
       boolean keepReading = true;
       
       // Create an empty raw template for the root of the file.
       RawTemplate currentTemplate = new RawTemplate("RootTemplate", "");
       
       // Templates are kept on a stack during the parsing, and pushed/popped as {'s or }'s are encountered.
       List<RawTemplate> templateStack = new ArrayList<RawTemplate>();
       templateStack.add(currentTemplate);
       
       String str;
       int i;
       char ch ;
       while (keepReading)
       {
           i = in.read();
           if (i >= 0) // Not end of stream
           {
               ch = (char)i;
               
               if (ch != DOUBLE_QUOTES && ch != OPEN_BRACE && ch != CLOSE_BRACE && ch != COMMA)
               {
                   // Not the start of a string, start or end of a template, or separator between fields,
                   // so add to the current stringbuffer.
                   stringbBuilder.append(ch);
               }
               else
               {
                   if (ch == DOUBLE_QUOTES)
                   {
                       // Start of a string. Read until the end and add to the list of values for the current template.
//                       String str = readUntilEndOfString();
                      str =in.readLine();
                      str.replaceAll("\"", "");
                      currentTemplate.values.add(str);
                   }
                   else
                   {
                       str = stringbBuilder.toString().trim();
                       if (ch == COMMA)
                       {
                           // Field separator. Find out if it's a float or an int, then add to the template.
                           if (str.indexOf('.') >= 0) // is float
                               currentTemplate.values.add(new Float(str));
                           else // is int
                               currentTemplate.values.add(new Integer(str));
                       }

                       if (ch == OPEN_BRACE)
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

                       if (ch == CLOSE_BRACE)
                       {
                           // End of a template. Pop it from the stack, and add it to the parent template as a value.
                           RawTemplate template = currentTemplate;
                           templateStack.remove(templateStack.size() - 1);
                           currentTemplate = templateStack.get(templateStack.size() - 1);

                           currentTemplate.values.add(template);
                       }
                   }
                   stringbBuilder.setLength(0);
               }
           }
           else
           {
               keepReading = false;
           }
       }
       
   	 return currentTemplate;
    }
    */
    
    /**
     * Builds a Template from a RawTemplate by finding the class that implements the template type,
     * creating a new instance, and running the build method.
     * 
     * <p>This is a recursive method that also builds all sub templates.
     * 
     * @param rawTemplate the RawTemplate that should be converted to a Template
     * @return the Template representing the RawTemplate
     * @throws ParseException if the parsing fails for any reason
     */
    private Template buildTemplate(RawTemplate rawTemplate) throws ParseException
    {
   	    Template template;
   	    Class<?> c;
        // Depth first. Iterate over all values and replace all RawTemplates with them with the real Templates
        for (int i=0; i<rawTemplate.values.size(); i++)
        {
            Object o = rawTemplate.values.get(i);
            if (o instanceof RawTemplate)
            {
                template = buildTemplate((RawTemplate)o);
                rawTemplate.values.set(i, template);
            }
        }

        try
        {
            // The template classes are named the same as the templates, so do a class lookup.
            // (If this is slow, cache the classes after they are found)
            c = Class.forName(JOXSI_LOADER_PACKAGE_PREFIX + rawTemplate.name);
            template = (Template)c.newInstance();
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
        catch (ParseException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Couldn't parse file: "+e);
        }
    }

    /**
     * Parses the input stream by first reading all the templates into a tree of RawTemplates,
     * then converting them into instances of their respective classes.
     * 
     * @return the RootTemplate that holds the dotXSI file
     * @throws IOException if there's an IO error
     * @throws ParseException if the parsing fails for any reason
     */
    private RootTemplate parse() throws IOException, ParseException
    {
        try
        {
            header = readHeader();

            // TODO: Add support for dotXSI formats newer than 3.x
            if (!header.formatType.equals(Header.TEXT_FORMAT_TYPE))
                throw new ParseException("Failed to read dotXSI: Only txt format supported");
            if (!(header.majorVersion == 3))
                throw new ParseException("Failed to read dotXSI: Only 3.x files supported");

            reader = new InputStreamReader(inputStream);
            
            buffer = new char[INPUT_BUFFER_SIZE];
            bufferLength = 0;

            RawTemplate root = parseRawTemplates();
            return (RootTemplate)buildTemplate(root);
        }
        catch (IOException ioe) 
        {
            System.out.println("An I/O error occured.");
            ioe.printStackTrace();
        	throw ioe;
        }
        catch (Exception e)
        {
            System.out.println("An unhandled error occured.");
        	e.printStackTrace();
        	throw new ParseException("Failed to read file: "+e);
        }
    }

    /**
     * Parses an InputStream and returns a RootTemplate containing the entire scene.
     * 
     * @param in an input stream that contains a dotXSI file
     * @return a RootTemplate containing the entire scene
     * @throws IOException if there's an IO error
     * @throws ParseException if the parsing fails for any reason
     */
    public static RootTemplate load(InputStream in) throws IOException, ParseException
    {
        return new DotXSILoader(in).parse();
    }
}