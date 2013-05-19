
/**
 * **************************************************************************
 *                                                                           *
 * OpenNI 1.x Alpha *
 * Copyright (C) 2011 PrimeSense Ltd. *
 *                                                                           *
 * This file is part of OpenNI. *
 *                                                                           *
 * OpenNI is free software: you can redistribute it and/or modify *
 * it under the terms of the GNU Lesser General Public License as published *
 * by the Free Software Foundation, either version 3 of the License, or *
 * (at your option) any later version. *
 *                                                                           *
 * OpenNI is distributed in the hope that it will be useful, *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the *
 * GNU Lesser General Public License for more details. *
 *                                                                           *
 * You should have received a copy of the GNU Lesser General Public License *
 * along with OpenNI. If not, see <http://www.gnu.org/licenses/>. *
 *                                                                           *
 ***************************************************************************
 */
package com.lifespeedtechnologies.OpenTracker;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.OpenNI.GeneralException;

public class OpenTrackerDemoUI {

	/**
	 *
	 */
	public OpenTrackerDemo viewer;
	private JFrame frame;

	public OpenTrackerDemoUI ( JFrame frame ) {
		this.frame = frame;
	}

	public static void main ( String s[] ) {
		JFrame f = new JFrame( "OpenNI User Tracker" );
		f.addWindowListener( new WindowAdapter() {

			public void windowClosing ( WindowEvent e ) {
				System.exit( 0 );
			}

		} );
		OpenTrackerDemoUI app = new OpenTrackerDemoUI( f );

		app.viewer = new OpenTrackerDemo();
		app.viewer.skel.setVisible( true );
		app.viewer.vid.setVisible( true );
		app.viewer.ni.setVisible( true );

		f.add( app.viewer.skel, "West" );
		f.add( app.viewer.vid, "Center" );
		f.add( app.viewer.ni, "East" );
		f.pack();
		f.setVisible( true );
		f.addKeyListener( app.viewer.hw );
		app.run();
	}

	void run () {
		while ( viewer.shouldRun ) {
			try {
				viewer.update();
			} catch ( GeneralException ex ) {
				Logger.getLogger( OpenTrackerDemoUI.class.getName() ).
						log( Level.SEVERE, null, ex );
			}
		}
		frame.dispose();
	}

}
