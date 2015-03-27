/*
 * South Face Software
 * Copyright 2012, South Face Software, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.sfs.ucm.data;

/**
 * Constants (not typesafe).
 * 
 * @author lbbisho
 * 
 */

public final class Constants {

	// -- Type Enumerations

	/**
	 * Identifier
	 */
	public static final long ID_UNDEFINED = -1L;
	
	/**
	 * Conversation timeout (in milliseconds)
	 */
	public static final long CONVERSATION_TIMEOUT = 3600000;
	
	/**
	 * page params
	 */
	public static final int PAGE_WIDTH = 950;
	public static final int SIDEBAR_WIDTH = 300;
	public static final int GROWL_LIFE = 1500;
	public static final int MENU_HEIGHT = 70;
	public static final int TABLE_SCROLL_HEIGHT = 420;
	public static final int LIST_SCROLL_HEIGHT = 550;

	/**
	 * logging constants
	 */
	public static final int DEBUG = 1;
	public static final int NORMAL = 0;

	// abbreviated string lengths
	public static final int ABBRV_IDENT_LEN = 30;
	public static final int ABBRV_NAME_LEN = 50;
	public static final int ABBRV_DESC_LEN = 60;
	
	// identifier padding width
	public static final int IDENT_SIZE = 4; 

	// PF is the ratio of development man-hours need per use case point
	public static final double UCP_PF = 20; // good for new team

	// technical/environmental factor weights
	public static final int ECF_IMPACT_NOIMPACT = 0;
	public static final int ECF_IMPACT_STRONGNEGATIVE = 1;
	public static final int ECF_IMPACT_NEGATIVE = 2;
	public static final int ECF_IMPACT_AVERAGE = 3;
	public static final int ECF_IMPACT_POSITIVE = 4;
	public static final int ECF_IMPACT_STRONGPOSITIVE = 5;

	public static final int TCF_COMPLEXITY_IRRELEVANT = 0;
	public static final int TCF_COMPLEXITY_VERYLOWIMPORTANCE = 1;
	public static final int TCF_COMPLEXITY_LOWIMPORTANCE = 2;
	public static final int TCF_COMPLEXITY_AVERAGE = 3;
	public static final int TCF_COMPLEXITY_HIGHIMPORTANCE = 4;
	public static final int TCF_COMPLEXITY_VERYHIGHIMPORTANCE = 5;

	// Technical factor constants
	public static final double TCF_T1 = 2.0;
	public static final double TCF_T2 = 1.0;
	public static final double TCF_T3 = 1.0;
	public static final double TCF_T4 = 1.0;
	public static final double TCF_T5 = 1.0;
	public static final double TCF_T6 = 0.5;
	public static final double TCF_T7 = 0.5;
	public static final double TCF_T8 = 2.0;
	public static final double TCF_T9 = 1.0;
	public static final double TCF_T10 = 1.0;
	public static final double TCF_T11 = 1.0;
	public static final double TCF_T12 = 1.0;
	public static final double TCF_T13 = 1.0;

	public static final double TCF_C1 = 0.6;
	public static final double TCF_C2 = 0.01;

	// Environmental factor constants
	public static final double ECF_E1 = 1.5;
	public static final double ECF_E2 = -1.0;
	public static final double ECF_E3 = 0.5;
	public static final double ECF_E4 = 0.5;
	public static final double ECF_E5 = 1.0;
	public static final double ECF_E6 = 1.0;
	public static final double ECF_E7 = -1.0;
	public static final double ECF_E8 = 2.0;
	
	public static final double ECF_C1 = 1.4;
	public static final double ECF_C2 = -0.03;

}
