/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lifespeedtechnologies.OpenTracker;

import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.nio.ByteBuffer;
import org.OpenNI.*;

import static com.lifespeedtechnologies.OpenTracker.Driver.skeletonCap;

/**
 *
 * @author dean
 */
public class VideoDriver extends Driver {

	protected static ImageGenerator igen;
	protected ImageMap imageMap;
	protected BufferedImage bimg;
	protected int width, height;
	protected byte[] imagebites;
	private boolean drawColor = true;
	protected ImageMetaData imd;
	protected java.nio.ByteBuffer buf;
	protected int BPP;

	@Override
	public java.awt.Dimension getPreferredSize () {
		return new java.awt.Dimension( width, height );
	}

	public VideoDriver ( Context c ) {
		super( c );
	}

	@Override
	public void init () throws GeneralException {
		initImage();
		depthGen = DepthGenerator.create( context );
		super.initUser();
		AlternativeViewpointCapability altViewCap =
									   new AlternativeViewpointCapability( igen );
		altViewCap = userGen.getAlternativeViewpointCapability();
	}

	public void updateImage () throws GeneralException {
		context.waitAndUpdateAll();
		if ( igen.isGenerating() && igen.isDataNew() ) {
			imageMap = igen.getImageMap();
			ByteBuffer b = igen.createDataByteBuffer();
			b = b.get( imagebites );
		} else {
			System.err.println( "gen: " + igen.isGenerating() + ", avail: " +
								igen.isNewDataAvailable() );
		}

		repaint();
	}

	@Override
	public void paint ( java.awt.Graphics g ) {

		DataBufferByte dbb = new java.awt.image.DataBufferByte(
				imagebites, width * height * 3 );

		WritableRaster raster = java.awt.image.Raster.
				createInterleavedRaster( dbb,
										 width,
										 height,
										 width * 3,
										 3,
										 new int[] {
			0, 1, 2 }, null );
		ColorModel colorModel = new ComponentColorModel(
				ColorSpace.getInstance( ColorSpace.CS_sRGB ),
				new int[] { 8, 8, 8 }, false, false,
				ComponentColorModel.OPAQUE,
				DataBuffer.TYPE_BYTE );

		bimg = new BufferedImage( colorModel, raster, false,
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

	protected void initImage ()
			throws GeneralException {
		igen = ImageGenerator.create( context );
		igen.setPixelFormat( PixelFormat.RGB24 );
		imd = igen.getMetaData();
		width = imd.getFullXRes();
		height = imd.getFullYRes();
		imagebites = new byte[width * height * 3];
		igen.startGenerating();

	}

}
