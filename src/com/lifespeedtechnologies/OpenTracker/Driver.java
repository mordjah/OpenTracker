/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lifespeedtechnologies.OpenTracker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.nio.ShortBuffer;
import java.util.HashMap;
import org.OpenNI.*;

/**
 *
 * @author dean
 */
public abstract class Driver extends Component {

	protected static String calibPose = null;
	protected static DepthGenerator depthGen;
	protected static boolean drawBackground = true;
	protected static boolean drawPixels = true;
	protected static boolean drawSkeleton = true;
	protected static HashMap<Integer, HashMap<SkeletonJoint, SkeletonJointPosition>> joints;
	protected static PoseDetectionCapability poseDetectionCap;
	protected static boolean printID = true;
	protected static boolean printState = true;
	protected static SkeletonCapability skeletonCap;
	protected Color[] colors = { Color.RED, Color.BLUE, Color.CYAN, Color.GREEN,
								 Color.MAGENTA, Color.PINK, Color.YELLOW,
								 Color.WHITE };
	protected Context context;
	protected BufferedImage dbimg;
	protected int dheight;
	protected float[] dhistogram;
	protected byte[] dimgbytes;
	protected int dwidth;
	protected UserGenerator userGen;

	protected Driver ( Context c ) {
		context = c;
	}

	public static void drawSkeleton ( Graphics g, int user ) throws
			org.OpenNI.StatusException {
		getJoints( user );
		HashMap<SkeletonJoint, SkeletonJointPosition> dict =
													  joints.get( new Integer(
				user ) );
		drawLine( g, dict, SkeletonJoint.HEAD, SkeletonJoint.NECK );
		drawLine( g, dict, SkeletonJoint.LEFT_SHOULDER, SkeletonJoint.TORSO );
		drawLine( g, dict, SkeletonJoint.RIGHT_SHOULDER, SkeletonJoint.TORSO );
		drawLine( g, dict, SkeletonJoint.NECK, SkeletonJoint.LEFT_SHOULDER );
		drawLine( g, dict, SkeletonJoint.LEFT_SHOULDER, SkeletonJoint.LEFT_ELBOW );
		drawLine( g, dict, SkeletonJoint.LEFT_ELBOW, SkeletonJoint.LEFT_HAND );
		drawLine( g, dict, SkeletonJoint.NECK, SkeletonJoint.RIGHT_SHOULDER );
		drawLine( g, dict, SkeletonJoint.RIGHT_SHOULDER,
				  SkeletonJoint.RIGHT_ELBOW );
		drawLine( g, dict, SkeletonJoint.RIGHT_ELBOW, SkeletonJoint.RIGHT_HAND );
		drawLine( g, dict, SkeletonJoint.LEFT_HIP, SkeletonJoint.TORSO );
		drawLine( g, dict, SkeletonJoint.RIGHT_HIP, SkeletonJoint.TORSO );
		drawLine( g, dict, SkeletonJoint.LEFT_HIP, SkeletonJoint.RIGHT_HIP );
		drawLine( g, dict, SkeletonJoint.LEFT_HIP, SkeletonJoint.LEFT_KNEE );
		drawLine( g, dict, SkeletonJoint.LEFT_KNEE, SkeletonJoint.LEFT_FOOT );
		drawLine( g, dict, SkeletonJoint.RIGHT_HIP, SkeletonJoint.RIGHT_KNEE );
		drawLine( g, dict, SkeletonJoint.RIGHT_KNEE, SkeletonJoint.RIGHT_FOOT );
	}

	@Override
	public Dimension getPreferredSize () {
		return new Dimension( dwidth, dheight );
	}

	public abstract void init () throws GeneralException;

	public abstract void paint ( Graphics g );

	protected void calcHist ( ShortBuffer depth ) {
		// reset
		for ( int i = 0; i < dhistogram.length;
			  ++i ) {
			dhistogram[i] = 0;
		}
		depth.rewind();
		int points = 0;
		while ( depth.remaining() > 0 ) {
			short depthVal = depth.get();
			if ( depthVal != 0 ) {
				dhistogram[depthVal]++;
				points++;
			}
		}
		for ( int i = 1; i < dhistogram.length;
			  i++ ) {
			dhistogram[i] += dhistogram[i - 1];
		}
		if ( points > 0 ) {
			for ( int i = 1; i < dhistogram.length;
				  i++ ) {
				dhistogram[i] = 1.0f - ( dhistogram[i] / (float) points );
			}
		}
	}

	protected static void drawLine ( Graphics g,
									 HashMap<SkeletonJoint, SkeletonJointPosition> jointHash,
									 SkeletonJoint joint1, SkeletonJoint joint2 ) {
		Point3D pos1 = jointHash.get( joint1 ).getPosition();
		Point3D pos2 = jointHash.get( joint2 ).getPosition();
		if ( jointHash.get( joint1 ).getConfidence() == 0 ||
			 jointHash.get( joint2 ).getConfidence() == 0 ) {
			return;
		}
		g.drawLine( (int) pos1.getX(), (int) pos1.getY(), (int) pos2.getX(),
					(int) pos2.getY() );
	}

	protected static void getJoint ( int user, SkeletonJoint joint ) throws
			org.OpenNI.StatusException {
		SkeletonJointPosition pos =
							  skeletonCap.
				getSkeletonJointPosition( user, joint );
		if ( pos.getPosition().getZ() != 0 ) {
			joints.get( user ).
					put( joint,
						 new SkeletonJointPosition( depthGen.
					convertRealWorldToProjective( pos.getPosition() ),
													pos.getConfidence() ) );
		} else {
			joints.get( user ).
					put( joint, new SkeletonJointPosition( new Point3D(), 0 ) );
		}
	}

	protected static void getJoints ( int user ) throws
			org.OpenNI.StatusException {
		getJoint( user, SkeletonJoint.HEAD );
		getJoint( user, SkeletonJoint.NECK );
		getJoint( user, SkeletonJoint.LEFT_SHOULDER );
		getJoint( user, SkeletonJoint.LEFT_ELBOW );
		getJoint( user, SkeletonJoint.LEFT_HAND );
		getJoint( user, SkeletonJoint.RIGHT_SHOULDER );
		getJoint( user, SkeletonJoint.RIGHT_ELBOW );
		getJoint( user, SkeletonJoint.RIGHT_HAND );
		getJoint( user, SkeletonJoint.TORSO );
		getJoint( user, SkeletonJoint.LEFT_HIP );
		getJoint( user, SkeletonJoint.LEFT_KNEE );
		getJoint( user, SkeletonJoint.LEFT_FOOT );
		getJoint( user, SkeletonJoint.RIGHT_HIP );
		getJoint( user, SkeletonJoint.RIGHT_KNEE );
		getJoint( user, SkeletonJoint.RIGHT_FOOT );
	}

	protected void initUser () {
		try {
			userGen = UserGenerator.create( context );
			skeletonCap = userGen.getSkeletonCapability();
			poseDetectionCap = userGen.getPoseDetectionCapability();
			userGen.getNewUserEvent().addObserver( new NewUserObserver( this ) );
			userGen.getLostUserEvent().
					addObserver( new LostUserObserver( this ) );
			skeletonCap.getCalibrationCompleteEvent().
					addObserver( new CalibrationCompleteObserver( this ) );
			poseDetectionCap.getPoseDetectedEvent().
					addObserver( new PoseDetectedObserver( this ) );
			calibPose = skeletonCap.getSkeletonCalibrationPose();
			joints = new HashMap<>();
			skeletonCap.setSkeletonProfile( SkeletonProfile.ALL );
		} catch ( GeneralException e ) {
			e.printStackTrace();
			System.exit( 1 );
		}
	}

}
