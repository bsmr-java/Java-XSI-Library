package com.mojang.joxsi;

import com.mojang.joxsi.loader.SI_Envelope.VertexWeight;

/**
 * An Envelope is used for skinned animation. It maps a bone ("deformer" in XSI) to a target mesh ("envelope").
 * 
 * <p>It also includes a float[16] for storing the deformation matrix.<br> 
 */
public class Envelope
{
    public VertexWeight[] vertexWeights;
    public Model envelopeModel;
    public Model deformerModel;
    public float[] deformationMatrix = new float[16];

    /**
     * Creates a new Envelope.
     * 
     * <p>This is called automatically when the Scene is created, so there's rarely any need to call this manually.
     * 
     * @param vertexWeights the array of weights
     * @param envelopeModel the model that should be used as the skin
     * @param deformerModel the model whos transform should be used as the bone
     */
    public Envelope(VertexWeight[] vertexWeights, Model envelopeModel, Model deformerModel)
    {
        this.vertexWeights = vertexWeights;
        this.envelopeModel = envelopeModel;
        this.deformerModel = deformerModel;
    }
}