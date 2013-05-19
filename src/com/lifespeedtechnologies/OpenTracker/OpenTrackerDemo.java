
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

import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.OpenNI.Context;
import org.OpenNI.GeneralException;
import org.OpenNI.OutArg;
import org.OpenNI.ScriptNode;

public class OpenTrackerDemo extends Component {

	/**
	 *
	 */
	protected static final long serialVersionUID = 1L;
	protected OutArg<ScriptNode> scriptNode;
	protected Context context;
	protected DepthDriver skel;
	protected VideoDriver vid;
	protected IrDriver ir;
	protected HWDriver hw;
	protected niteDriver ni;
	protected boolean shouldRun = true;
	protected final String SAMPLE_XML_FILE = "SamplesConfig.xml";

	public OpenTrackerDemo () {
		try {
			init();
		} catch ( GeneralException ex ) {
			Logger.getLogger( OpenTrackerDemo.class.getName() ).
					log( Level.SEVERE, null, ex );
		}
	}

	protected void init () throws GeneralException {

		scriptNode = new OutArg<>();
		context = Context.createFromXmlFile( SAMPLE_XML_FILE, scriptNode );

		skel = new DepthDriver( context, this );
		skel.init();

		vid = new VideoDriver( context );
		vid.init();

		ni = new niteDriver( context );
		ni.init();

		//ir = new IrDriver( context );
		//ir.init();

		hw = new HWDriver( this );

		context.startGeneratingAll();

	}

	public void update () throws GeneralException {
		skel.updateDepth();
		vid.updateImage();
		ni.update();

	}

}
