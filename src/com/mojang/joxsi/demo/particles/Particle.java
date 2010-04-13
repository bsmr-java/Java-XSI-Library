package com.mojang.joxsi.demo.particles;

import java.io.IOException;

/**
 * Class for particle usage
 * @author Ilkka
 */
public class Particle
{	
	//particle state
	private float fade;
	private float life;
	private boolean active;
	
	//current location
	private float x;
	private float y;
	private float z;
	
	//particle direction
	private float directionX;
	private float directionY;
	private float directionZ;
	
	//particle gravitation
	private float gravityX;
	private float gravityY;
	private float gravityZ;
	
	//particle rotation
	private float rotationSpeed;
	private float rotateX;
	private float rotateY;
	private float rotateZ;
	
	public Particle(){};
	
	// Insert the values for particle when particle is created
	public Particle(float pfade,	float plife,	boolean pactive,
					float px,	float py,	float pz,
					float pdirectionX,	float pdirectionY,	float pdirectionZ,
					float pgravityX,		float pgravityY,		float pgravityZ,
					float protationSpeed, float protateX, float protateY, float protateZ) throws IOException
	{	
		fade = pfade;
		life = plife;
		active = pactive;
		x = px;
		y = py;
		z = pz;
		directionX = pdirectionX;
		directionY = pdirectionY;
		directionZ = pdirectionZ;
		gravityX = pgravityX;
		gravityY = pgravityY;
		gravityZ = pgravityZ;
		rotateX = protateX;
		rotateY = protateY;
		rotateZ = protateZ;
		rotationSpeed = protationSpeed;
	//	scene = Scene.load(ModelDisplayer.class.getResourceAsStream(ppath));
	}
	
	// Set values after particle is created, used when in example active goes to false
	// and we want to set it active again.
	public void setValues(float pfade,	float plife,	boolean pactive,
			float px,	float py,	float pz,
			float pdirectionX,	float pdirectionY,	float pdirectionZ,
			float pgravityX,		float pgravityY,		float pgravityZ,
			float protationSpeed, float protateX, float protateY, float protateZ)
	{	
		fade = pfade;
		life = plife;
		active = pactive;
		x = px;
		y = py;
		z = pz;
		directionX = pdirectionX;
		directionY = pdirectionY;
		directionZ = pdirectionZ;
		gravityX = pgravityX;
		gravityY = pgravityY;
		gravityZ = pgravityZ;
		rotateX = protateX;
		rotateY = protateY;
		rotateZ = protateZ;
		rotationSpeed = protationSpeed;
	//	scene = Scene.load(ModelDisplayer.class.getResourceAsStream(ppath));
	}
	
	
	// Used to move the particle
	public void move()
	{
		// Move the particle by the value of directions
		x += directionX;
		y += directionY;
		z += directionZ;
		
		// Change the direction values by the value of gravities
		directionX += gravityX;
		directionY += gravityY;
		directionZ += gravityZ;
		
		// Reduce the particle life by the value of fade
		life -= fade;
		if (life < 0)
		{
			active = false;
		}
	}
	
	// GETTERS START
	public float getX()
	{
		return x;
	}
	
	public float getY()
	{
		return y;
	}
	
	public float getZ()
	{
		return z;
	}
	public float getRotateX()
	{
		return rotateX;
	}
	public float getRotateY()
	{
		return rotateY;
	}
	public float getRotateZ()
	{
		return rotateZ;
	}
	public float getRotationSpeed()
	{
		return rotationSpeed;
	}
	public boolean isActive()
	{
		return active;
	}
	
	public float getLife()
	{
		return life;
	}
	// GETTERS END
}