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
class CalibrationCompleteObserver implements
		IObserver<CalibrationProgressEventArgs> {

	private final Driver parent;

	CalibrationCompleteObserver ( final Driver parent ) {
		this.parent = parent;
	}

	@Override
	public void update (
			IObservable<CalibrationProgressEventArgs> observable,
			CalibrationProgressEventArgs args ) {
		System.out.println( "Calibraion complete: " + args.getStatus() );
		try {
			if ( args.getStatus() == CalibrationProgressStatus.OK ) {
				System.out.println( "starting tracking " + args.getUser() );
				parent.skeletonCap.startTracking( args.getUser() );
				parent.joints.put( new Integer( args.getUser() ),
								   new HashMap<SkeletonJoint, SkeletonJointPosition>() );
			} else if ( args.getStatus() !=
						CalibrationProgressStatus.MANUAL_ABORT ) {
				if ( parent.skeletonCap.needPoseForCalibration() ) {
					parent.poseDetectionCap.
							startPoseDetection( parent.calibPose,
												args.getUser() );
				} else {
					parent.skeletonCap.requestSkeletonCalibration( args.
							getUser(),
																   true );
				}
			}
		} catch ( StatusException e ) {
			e.printStackTrace();
		}
	}

}
