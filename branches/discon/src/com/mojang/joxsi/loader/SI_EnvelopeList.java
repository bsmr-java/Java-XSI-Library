package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Defines a list of envelopes.
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class SI_EnvelopeList extends Template
{
    /** Number of envelopes in the list. */
	public int nEnvelopes;
    /** Array of {@link SI_Envelope }s. */
	public SI_Envelope[] envelopes;

	public void parse(RawTemplate block)
	{
		Iterator<Object> it = block.values.iterator();
		nEnvelopes = ((Integer)it.next()).intValue();
		envelopes = new SI_Envelope[nEnvelopes];
		for (int i=0; i<nEnvelopes; i++)
		{
			envelopes[i] = (SI_Envelope)it.next();
		}
	}
}