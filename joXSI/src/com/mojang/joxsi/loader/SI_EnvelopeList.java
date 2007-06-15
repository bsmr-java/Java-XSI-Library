package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class SI_EnvelopeList extends Template
{
	public int nEnvelopes;
	public SI_Envelope[] envelopes;

	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		nEnvelopes = ((Integer)it.next()).intValue();
		envelopes = new SI_Envelope[nEnvelopes];
		for (int i=0; i<nEnvelopes; i++)
		{
			envelopes[i] = (SI_Envelope)it.next();
		}
	}
}