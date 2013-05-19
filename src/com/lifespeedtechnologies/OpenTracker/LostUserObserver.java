/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lifespeedtechnologies.OpenTracker;

import org.OpenNI.IObservable;
import org.OpenNI.IObserver;
import org.OpenNI.UserEventArgs;

/**
 *
 * @author dean
 */
class LostUserObserver implements IObserver<UserEventArgs> {

	private final Driver parent;

	LostUserObserver ( final Driver parent ) {
		this.parent = parent;
	}

	@Override
	public void update (
			IObservable<UserEventArgs> observable,
			UserEventArgs args ) {
		System.out.println( "Lost user " + args.getId() );
		parent.joints.remove( args.getId() );
	}

}
