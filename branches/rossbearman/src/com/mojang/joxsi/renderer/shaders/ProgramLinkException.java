package com.mojang.joxsi.renderer.shaders;

/**
 * 
 * @author Notch
 * @author Egal
 */
public class ProgramLinkException extends Exception
{
    /**
     * @param aMessage
     */
    public ProgramLinkException(String aMessage)
    {
        super(aMessage);
    }

    /**
     * @param aMessage
     * @param aCause
     */
    public ProgramLinkException(String aMessage, Throwable aCause)
    {
        super(aMessage, aCause);
    }
}