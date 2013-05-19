/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lifespeedtechnologies.OpenTracker;

/**
 *
 * @author dean
 */
import java.awt.event.KeyEvent;
import org.openkinect.freenect.Freenect.NativeContext;
import org.openkinect.freenect.*;

public class HWDriver implements java.awt.event.KeyListener {

	private NativeContext nc;
	private Context c;
	private Device dev;
	private OpenTrackerDemo parent;

	public HWDriver ( OpenTrackerDemo c ) {
		init();
		parent = c;
	}

	@Override
	public void keyTyped ( KeyEvent e ) {
	}

	@Override
	public void keyPressed ( KeyEvent e ) {
		int k = e.getKeyCode();
		e.consume();
		if ( k == KeyEvent.VK_ESCAPE ) {
			parent.shouldRun = false;
		}
		if ( k == KeyEvent.VK_W ) {
			dev.setTiltAngle( 5.0 );
		}
		if ( k == KeyEvent.VK_X ) {
			dev.setTiltAngle( -5.0 );
		}
		if ( k == KeyEvent.VK_1 ) {
			dev.setLed( LedStatus.fromInt( 1 ) );
		}
		if ( k == KeyEvent.VK_2 ) {
			dev.setLed( LedStatus.fromInt( 2 ) );
		}
		if ( k == KeyEvent.VK_3 ) {
			dev.setLed( LedStatus.fromInt( 3 ) );
		}
		if ( k == KeyEvent.VK_4 ) {
			dev.setLed( LedStatus.fromInt( 4 ) );
		}
		if ( k == KeyEvent.VK_5 ) {
			dev.setLed( LedStatus.fromInt( 5 ) );
		}
		if ( k == KeyEvent.VK_6 ) {
			dev.setLed( LedStatus.fromInt( 6 ) );
		}

	}

	@Override
	public void keyReleased ( KeyEvent e ) {
//
	}

	protected void init () {
		nc = Freenect.createNativeContext();
		Freenect.freenect_select_subdevices( nc, DeviceFlags.MOTOR.intValue() );
		c = (Context) nc;
		dev = c.openDevice( 0 );
		dev.setLed( org.openkinect.freenect.LedStatus.GREEN );
	}

}
