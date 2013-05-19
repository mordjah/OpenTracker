/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lifespeedtechnologies.OpenTracker;

import org.OpenNI.IObservable;
import org.OpenNI.IObserver;
import org.OpenNI.StatusException;
import org.OpenNI.UserEventArgs;

/**
 *
 * @author dean
 */
class NewUserObserver implements IObserver<UserEventArgs> {

	private final Driver parent;

	NewUserObserver ( final Driver parent ) {
		this.parent = parent;
	}

	@Override
	public void update (
			IObservable<UserEventArgs> observable,
			UserEventArgs args ) {
		System.out.println( "New user " + args.getId() );
		try {
			if ( parent.skeletonCap.needPoseForCalibration() ) {
				parent.poseDetectionCap.startPoseDetection( parent.calibPose,
															args.getId() );
			} else {
				parent.skeletonCap.requestSkeletonCalibration( args.getId(),
															   true );
			}
		} catch ( StatusException e ) {
			e.printStackTrace();
		}
	}

}
