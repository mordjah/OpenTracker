/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lifespeedtechnologies.OpenTracker;

import java.awt.Graphics;
import org.OpenNI.*;

import static com.lifespeedtechnologies.OpenTracker.Driver.drawSkeleton;

/**
 *
 * @author dean
 */
public class IrDriver extends Driver {

	protected static IRGenerator irgen;
	protected IRMap irMap;
	protected java.awt.image.BufferedImage bimg;
	protected int width, height;
	protected byte[] irbites;
	private boolean drawColor = true;
	protected IRMetaData imd;
	protected java.nio.ByteBuffer buf;
	protected int BPP;

	public IrDriver ( Context c ) {
		super( c );
	}

	@Override
	public void init () throws GeneralException {
		initIr();

	}

	protected void initIr ()
			throws GeneralException {
		irbites = new byte[width * height * 3];
		irgen = IRGenerator.create( context );
		imd = irgen.getMetaData();
		width = imd.getFullXRes();
		height = imd.getFullYRes();
		irgen.startGenerating();


	}

	public void updateImage () throws GeneralException {
		context.waitAndUpdateAll();
		irMap = irgen.getIRMap();
		java.nio.ByteBuffer b = irgen.createDataByteBuffer();
		b = b.get( irbites );
		repaint();
	}

	@Override
	public void paint ( Graphics g ) {

		java.awt.image.DataBufferByte dbb = new java.awt.image.DataBufferByte(
				irbites, width * height * 3 );

		java.awt.image.WritableRaster raster = java.awt.image.Raster.
				createInterleavedRaster( dbb,
										 width,
										 height,
										 width * 3,
										 3,
										 new int[] {
			0, 1, 2 }, null );
		java.awt.image.ColorModel colorModel =
								  new java.awt.image.ComponentColorModel(
				java.awt.color.ColorSpace.getInstance(
				java.awt.color.ColorSpace.CS_sRGB ),
				new int[] { 8, 8, 8 }, false, false,
				java.awt.image.ComponentColorModel.OPAQUE,
				java.awt.image.DataBuffer.TYPE_BYTE );

		bimg = new java.awt.image.BufferedImage( colorModel, raster, false,
												 null );
		g.drawImage( bimg, 0, 0, this );

		try {
			int[] users = userGen.getUsers();
			for ( int i = 0; i < users.length; ++i ) {
				java.awt.Color c = colors[users[i] % colors.length];
				c = new java.awt.Color( 255 - c.getRed(), 255 - c.getGreen(),
										255 - c.
						getBlue() );

				g.setColor( c );
				if ( drawSkeleton && skeletonCap.isSkeletonTracking( users[i] ) ) {
					VideoDriver.drawSkeleton( g, users[i] );
				}

			}
		} catch ( StatusException e ) {
			e.printStackTrace();
		}
	}

}
