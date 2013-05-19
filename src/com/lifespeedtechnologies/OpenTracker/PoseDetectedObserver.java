/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lifespeedtechnologies.OpenTracker;

import org.OpenNI.IObservable;
import org.OpenNI.IObserver;
import org.OpenNI.PoseDetectionEventArgs;
import org.OpenNI.StatusException;

/**
 *
 * @author dean
 */
class PoseDetectedObserver implements IObserver<PoseDetectionEventArgs> {

	private final Driver parent;

	PoseDetectedObserver ( final Driver parent ) {
		this.parent = parent;
	}

	@Override
	public void update (
			IObservable<PoseDetectionEventArgs> observable,
			PoseDetectionEventArgs args ) {
		System.out.println( "Pose " + args.getPose() + " detected for " +
							args.getUser() );
		try {
			parent.poseDetectionCap.stopPoseDetection( args.getUser() );
			parent.skeletonCap.
					requestSkeletonCalibration( args.getUser(), true );
		} catch ( StatusException e ) {
			e.printStackTrace();
		}
	}

}
