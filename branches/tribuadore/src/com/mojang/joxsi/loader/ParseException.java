
package com.mojang.joxsi.loader;

/**
 * Simple Exception that is thrown when the .XSI cannot be parsed.
 * 
 * @author Notch
 */
public class ParseException extends Exception
{
    
    public ParseException(String string)
    {
        super(string);
    }
}
