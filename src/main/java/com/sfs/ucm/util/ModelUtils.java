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
package com.sfs.ucm.util;

import java.util.List;

import com.sfs.ucm.model.EntityBase;

public class ModelUtils {

	/**
	 * Compute the next object identifier
	 * 
	 * @param list
	 * @return next id value
	 */
	public static int getNextIdentifier(List<?> list) {
		int id = 0;
		if (list != null) {
			for (Object obj : list) {
				EntityBase eb = (EntityBase) obj;
				Integer ival = eb.getIdentifier();
				if (ival.intValue() > id) {
					id = ival;
				}
			}
			id++;
		}

		return id;
	}

	/**
	 * Build artifact identifier of the form: <prefix><zeropadding><identifier>. Example: TSK0001
	 * 
	 * @param prefix
	 * @param identifier
	 * @return artifact identifier
	 */
	public static String buildArtifactIdentifier(final String prefix, final Integer identifier) {
		String artf = null;

		if (identifier != null) {
			if (identifier.intValue() < 10) {
				artf = prefix + "00" + identifier;
			}
			else if (identifier.intValue() < 100) {
				artf = prefix + "0" + identifier;
			}
			else {
				artf = prefix + identifier;
			}
		}
		return artf;
	}

}
