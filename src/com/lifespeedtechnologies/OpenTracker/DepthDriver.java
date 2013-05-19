/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lifespeedtechnologies.OpenTracker;

import java.util.HashMap;
import org.OpenNI.*;

/**
 *
 * @author dean
 */
public class DepthDriver extends Driver {

	private OpenTrackerDemo parent;

	public DepthDriver ( Context c, OpenTrackerDemo p ) {
		super( c );
		parent = p;
	}

	public void init () throws GeneralException {
		initDepth();
		initUser();
	}

	public void updateDepth () {
		try {

			context.waitAnyUpdateAll();

			DepthMetaData depthMD = depthGen.getMetaData();
			SceneMetaData sceneMD = userGen.getUserPixels( 0 );

			java.nio.ShortBuffer scene = sceneMD.getData().createShortBuffer();
			java.nio.ShortBuffer depth = depthMD.getData().createShortBuffer();
			calcHist( depth );
			depth.rewind();

			while ( depth.remaining() > 0 ) {
				int pos = depth.position();
				short pixel = depth.get();
				short user = scene.get();

				dimgbytes[3 * pos] = 0;
				dimgbytes[3 * pos + 1] = 0;
				dimgbytes[3 * pos + 2] = 0;

				if ( drawBackground || pixel != 0 ) {
					int colorID = user % ( colors.length - 1 );
					if ( user == 0 ) {
						colorID = colors.length - 1;
					}
					if ( pixel != 0 ) {
						float histValue = dhistogram[pixel];
						dimgbytes[3 * pos] = (byte) ( histValue *
													  colors[colorID].getRed() );
						dimgbytes[3 * pos + 1] = (byte) ( histValue *
														  colors[colorID].
														 getGreen() );
						dimgbytes[3 * pos + 2] = (byte) ( histValue *
														  colors[colorID].
														 getBlue() );
					}
				}
			}
			repaint();
		} catch ( GeneralException e ) {
			e.printStackTrace();
		}
	}

	protected void initUser () {
		try {
			userGen = UserGenerator.create( context );
			skeletonCap = userGen.getSkeletonCapability();
			poseDetectionCap = userGen.getPoseDetectionCapability();
			userGen.getNewUserEvent().addObserver( new NewUserObserver( this ) );
			userGen.getLostUserEvent().
					addObserver( new LostUserObserver( this ) );
			skeletonCap.getCalibrationCompleteEvent().addObserver(
					new CalibrationCompleteObserver( this ) );
			poseDetectionCap.getPoseDetectedEvent().addObserver(
					new PoseDetectedObserver( this ) );
			calibPose = skeletonCap.getSkeletonCalibrationPose();
			joints = new HashMap<>();
			skeletonCap.setSkeletonProfile( SkeletonProfile.ALL );
		} catch ( GeneralException e ) {
			e.printStackTrace();
			System.exit( 1 );
		}
	}

	protected void initDepth () throws GeneralException {
		depthGen = DepthGenerator.create( context );
		DepthMetaData depthMD = depthGen.getMetaData();
		dhistogram = new float[10000];
		dwidth = depthMD.getFullXRes();
		dheight = depthMD.getFullYRes();
		dimgbytes = new byte[dwidth * dheight * 3];
	}

	public static void drawSkeleton ( java.awt.Graphics g, int user ) throws
			org.OpenNI.StatusException {
		getJoints( user );
		java.util.HashMap<org.OpenNI.SkeletonJoint, org.OpenNI.SkeletonJointPosition> dict =
																					  joints.
				get(
				new Integer( user ) );

		drawLine( g, dict, org.OpenNI.SkeletonJoint.HEAD,
				  org.OpenNI.SkeletonJoint.NECK );

		drawLine( g, dict, org.OpenNI.SkeletonJoint.LEFT_SHOULDER,
				  org.OpenNI.SkeletonJoint.TORSO );
		drawLine( g, dict, org.OpenNI.SkeletonJoint.RIGHT_SHOULDER,
				  org.OpenNI.SkeletonJoint.TORSO );

		drawLine( g, dict, org.OpenNI.SkeletonJoint.NECK,
				  org.OpenNI.SkeletonJoint.LEFT_SHOULDER );
		drawLine( g, dict, org.OpenNI.SkeletonJoint.LEFT_SHOULDER,
				  org.OpenNI.SkeletonJoint.LEFT_ELBOW );
		drawLine( g, dict, org.OpenNI.SkeletonJoint.LEFT_ELBOW,
				  org.OpenNI.SkeletonJoint.LEFT_HAND );

		drawLine( g, dict, org.OpenNI.SkeletonJoint.NECK,
				  org.OpenNI.SkeletonJoint.RIGHT_SHOULDER );
		drawLine( g, dict, org.OpenNI.SkeletonJoint.RIGHT_SHOULDER,
				  org.OpenNI.SkeletonJoint.RIGHT_ELBOW );
		drawLine( g, dict, org.OpenNI.SkeletonJoint.RIGHT_ELBOW,
				  org.OpenNI.SkeletonJoint.RIGHT_HAND );

		drawLine( g, dict, org.OpenNI.SkeletonJoint.LEFT_HIP,
				  org.OpenNI.SkeletonJoint.TORSO );
		drawLine( g, dict, org.OpenNI.SkeletonJoint.RIGHT_HIP,
				  org.OpenNI.SkeletonJoint.TORSO );
		drawLine( g, dict, org.OpenNI.SkeletonJoint.LEFT_HIP,
				  org.OpenNI.SkeletonJoint.RIGHT_HIP );

		drawLine( g, dict, org.OpenNI.SkeletonJoint.LEFT_HIP,
				  org.OpenNI.SkeletonJoint.LEFT_KNEE );
		drawLine( g, dict, org.OpenNI.SkeletonJoint.LEFT_KNEE,
				  org.OpenNI.SkeletonJoint.LEFT_FOOT );

		drawLine( g, dict, org.OpenNI.SkeletonJoint.RIGHT_HIP,
				  org.OpenNI.SkeletonJoint.RIGHT_KNEE );
		drawLine( g, dict, org.OpenNI.SkeletonJoint.RIGHT_KNEE,
				  org.OpenNI.SkeletonJoint.RIGHT_FOOT );

	}

	protected static void drawLine ( java.awt.Graphics g,
									 HashMap<SkeletonJoint, SkeletonJointPosition> jointHash,
									 SkeletonJoint joint1, SkeletonJoint joint2 ) {
		org.OpenNI.Point3D pos1 = jointHash.get( joint1 ).getPosition();
		org.OpenNI.Point3D pos2 = jointHash.get( joint2 ).getPosition();

		if ( jointHash.get( joint1 ).getConfidence() == 0 || jointHash.get(
				joint2 ).getConfidence() == 0 ) {
			return;
		}

		g.drawLine( (int) pos1.getX(), (int) pos1.getY(), (int) pos2.getX(),
					(int) pos2.getY() );
	}

	@Override
	public void paint ( java.awt.Graphics g ) {
		if ( drawPixels ) {
			java.awt.image.DataBufferByte dataBuffer =
										  new java.awt.image.DataBufferByte(
					dimgbytes, dwidth *
							   dheight *
							   3 );

			java.awt.image.WritableRaster raster = java.awt.image.Raster.
					createInterleavedRaster( dataBuffer,
											 dwidth,
											 dheight,
											 dwidth * 3,
											 3,
											 new int[] {
				0, 1, 2 }, null );

			java.awt.image.ColorModel colorModel =
									  new java.awt.image.ComponentColorModel(
					java.awt.color.ColorSpace.
					getInstance( java.awt.color.ColorSpace.CS_sRGB ),
					new int[] { 8, 8, 8 },
					false, false,
					java.awt.image.ComponentColorModel.OPAQUE,
					java.awt.image.DataBuffer.TYPE_BYTE );

			dbimg = new java.awt.image.BufferedImage( colorModel, raster, false,
													  null );

			g.drawImage( dbimg, 0, 0, null );
		}
		try {
			int[] users = userGen.getUsers();
			for ( int i = 0; i < users.length; ++i ) {
				java.awt.Color c = colors[users[i] % colors.length];
				c = new java.awt.Color( 255 - c.getRed(), 255 - c.getGreen(),
										255 - c.
						getBlue() );

				g.setColor( c );
				if ( drawSkeleton && skeletonCap.isSkeletonTracking( users[i] ) ) {
					DepthDriver.drawSkeleton( g, users[i] );
				}

				if ( printID ) {
					Point3D com = depthGen.convertRealWorldToProjective(
							userGen.getUserCoM( users[i] ) );
					String label = null;
					if ( !printState ) {
						label = new String( "" + users[i] );
					} else if ( skeletonCap.isSkeletonTracking( users[i] ) ) {
						// Tracking
						label = new String( users[i] + " - Tracking" );
					} else if ( skeletonCap.isSkeletonCalibrating( users[i] ) ) {
						// Calibrating
						label = new String( users[i] + " - Calibrating" );
					} else {
						// Nothing
						label = new String( users[i] + " - Looking for pose (" +
											calibPose + ")" );
					}

					g.drawString( label, (int) com.getX(), (int) com.getY() );
				}
			}
		} catch ( StatusException e ) {
			e.printStackTrace();
		}
	}

}
