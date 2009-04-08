package com.mojang.joxsi.renderer.shaders;

/**
 * 
 * @author Notch
 * @author Egal
 */
public class ShaderCompilationError extends Exception
{
    /**
     * @param aMessage
     * @param aCause
     */
    public ShaderCompilationError(String aMessage)
    {
        super(aMessage);
    }

    /**
     * @param aMessage
     * @param aCause
     */
    public ShaderCompilationError(String aMessage, Throwable aCause)
    {
        super(aMessage, aCause);
    }
}