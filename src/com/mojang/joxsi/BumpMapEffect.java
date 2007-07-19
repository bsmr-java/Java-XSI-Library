package com.mojang.joxsi;

import java.util.logging.Logger;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import com.mojang.joxsi.loader.XSI_Image;



/**
 * 
 * @author Milbo
 *
 */

public class BumpMapEffect 
{
    /** logger - Logging instance. */
    private static Logger logger = Logger.getLogger(BumpMapEffect.class.getName());
   // Attribute
   private XSI_Image   img;
   private Image   bump;
   private int     lichtX = 50;
   private int     lichtY = 70;
   private int[][] map;
   private int[][] bumpX;
   private int[][] bumpY;
   private int     w;
   private int     h;
   
   public BumpMapEffect(XSI_Image img)
   {

       this.img = img;

       w = img.imageX;
       h = img.imageY;

       environMap();
       bumpMap();
   }

   // Normalenvektor calculating for light should be already somewhere
   // this should be calculated on graphiccard if I understand that piece of code right
 //is called every time the light is changed
   public void environMap()
   {
       map = new int[256][256];
       for (int y = 0; y < 256; y++)
       {
           for (int x = 0; x < 256; x++)
           {
               double nX = (x - 128) / 128f;
               double nY = (y - 128) / 128f;
               double nZ = Math.max(0, 1 - Math.sqrt(nX * nX + nY * nY));
               map[x][y] = (int) (nZ * 255);
           }
       }
   }

   // Calculating normals for the bumpMapped surface
   //maybe on graphiccard too, later profiles could support it
  // it is called only one time
   public void bumpMap()
   {
  
	    BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	    bi.getGraphics().drawImage(bump, 0, 0, null);
       bumpX = new int[w][h];
       bumpY = new int[w][h];
       int[] rgb = bi.getRGB(0, 0, w, h, null, 0, w);
       for (int y = 0; y < h; y++)
       {
           for (int x = 0; x < w; x++)
           {
               bumpX[x][y] = (rgb[Math.min(w - 1, x + 1) + y * w] & 255)
                       - (rgb[Math.max(0, x - 1) + w * y] & 255);
               bumpY[x][y] = (rgb[x + w * Math.min(h - 1, y + 1)] & 255)
                       - (rgb[x + w * Math.max(0, y - 1)] & 255);
           }
       }
   }

   //is called every time the view is changed, this code shouldnt be here
   //is just to understand and get some oversight,... should be in the client
   int[] erg = new int[w * h];
   int pos = 0;
   
   int px = 0;
   int py = 0;
   int f;
   public void paintComponent(Graphics graphics)
   {
//  	 timer.resetTime();
//  	 super.paintComponent(graphics);
  	 logger.info("painComponent");
  	 
       // Scene is here  
//       graphics.drawImage(img, 0, 0, null);
       erg = new int[w * h];
       pos = 0;

       // setting of points calculated with light and viewpos should be on card
       for (int y = 0; y < h; y++)
       {
           for (int x = 0; x < w; x++)
           {
               px = Math.max(0, Math.min(255, -bumpX[x][y] + 127
                       + (x - lichtX) / 3));
               py = Math.max(0, Math.min(255, -bumpY[x][y] + 127
                       + (y - lichtY) / 3));
               f = map[px][py];
               erg[pos++] = f | (f << 8) | (f << 16) | f << 24;
           }
       }
       // Drawing Image
       BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
       bi.setRGB(0, 0, w, h, erg, 0, w);
       graphics.drawImage(bi, 0, 0, null);
//       time = timer.getTime();
//       System.out.println("mouseMovedTime: "+time);
   }
   
	public Image generateBumpMap() {
		
		return bump;
	}
  
}