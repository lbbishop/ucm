package com.sfs.ucm.view;

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

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import com.sfs.ucm.data.Literal;

@Named
@RequestScoped
public class FacesContextMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	private FacesContext facesContext;

	@Inject
	public void init() {
		this.facesContext = FacesContext.getCurrentInstance();
	}

	/**
	 * Get resource message from resource bundle using key
	 * 
	 * @param key
	 * @return resource bundle message (null if not found)
	 */
	public String resourceMessage(final String key) {
		String msg = null;
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(Literal.RESOURCE_BUNDLE.toString());
			msg = bundle.getString(key);
		}
		catch (Exception e) {
			System.out.println("Error retrieving resource bundle " + Literal.RESOURCE_BUNDLE.toString());
		}
		return msg;
	}

	/**
	 * Add faces info message
	 * 
	 * @param msg
	 *            can be simple message or resource bundle message key of the form {key}
	 */
	public void infoMessage(final String msg) {
		String s = msg;
		int ib = msg.indexOf("{");
		int ie = msg.indexOf("}");
		if (ib != -1 && ie != -1) {
			if (ie - ib > 1) {
				s = resourceMessage(msg.substring(ib + 1, ie));
			}
		}
		this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, s, null));
	}

	/**
	 * Add faces error message
	 * 
	 * @param msg
	 *            can be simple message or resource bundle message key of the form {key}
	 */
	public void errorMessage(final String msg) {
		String s = msg;
		int ib = msg.indexOf("{");
		int ie = msg.indexOf("}");
		if (ib != -1 && ie != -1) {
			if (ie - ib > 1) {
				s = resourceMessage(msg.substring(ib + 1, ie));
			}
		}
		this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, s, null));
	}

	/**
	 * Add faces warning message
	 * 
	 * @param msg
	 *            can be simple message or resource bundle message key of the form {key}
	 */
	public void warningMessage(final String msg) {
		String s = msg;
		int ib = msg.indexOf("{");
		int ie = msg.indexOf("}");
		if (ib != -1 && ie != -1) {
			if (ie - ib > 1) {
				s = resourceMessage(msg.substring(ib + 1, ie));
			}
		}
		this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, s, null));
	}

	/**
	 * Add faces fatal message
	 * 
	 * @param msg
	 *            can be simple message or resource bundle message key of the form {key}
	 */
	public void fatalMessage(final String msg) {
		String s = msg;
		int ib = msg.indexOf("{");
		int ie = msg.indexOf("}");
		if (ib != -1 && ie != -1) {
			if (ie - ib > 1) {
				s = resourceMessage(msg.substring(ib + 1, ie));
			}
		}
		this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, s, null));
	}

	/**
	 * Add faces info message
	 * 
	 * @param msg
	 *            can be simple message or resource bundle message key of the form {key}
	 * @param param1
	 */
	public void infoMessage(final String msg, final Object param1) {
		String s = msg;
		if (s.indexOf("{0}") != -1) {
			String param = null;
			if (param1 != null) {
				param = (String) param1.toString();
				s = s.replace("{0}", param);
			}
		}
		else {
			int ib = msg.indexOf("{");
			int ie = msg.indexOf("}");
			if (ib != -1 && ie != -1) {
				if (ie - ib > 1) {
					s = resourceMessage(msg.substring(ib + 1, ie));
					String param = null;
					if (param1 != null) {
						param = (String) param1.toString();
						s = s.replace("{0}", param);
					}
				}
			}
		}
		this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, s, null));
	}

	/**
	 * Add faces info message
	 * 
	 * @param clientId
	 * @param msg
	 *            can be simple message or resource bundle message key of the form {key}
	 * @param param1
	 */
	public void infoMessage(final String clientId, final String msg, final Object param1) {
		String s = msg;
		if (s.indexOf("{0}") != -1) {
			String param = null;
			if (param1 != null) {
				param = (String) param1.toString();
				s = s.replace("{0}", param);
			}
		}
		else {
			int ib = msg.indexOf("{");
			int ie = msg.indexOf("}");
			if (ib != -1 && ie != -1) {
				if (ie - ib > 1) {
					s = resourceMessage(msg.substring(ib + 1, ie));
					String param = null;
					if (param1 != null) {
						param = (String) param1.toString();
						s = s.replace("{0}", param);
					}
				}
			}
		}
		this.facesContext.addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_INFO, s, null));
	}

	/**
	 * Add faces error message
	 * 
	 * @param msg
	 *            can be simple message or resource bundle message key of the form {key}
	 * @param param1
	 */
	public void errorMessage(final String msg, final Object param1) {
		String s = msg;
		if (s.indexOf("{0}") != -1) {
			String param = null;
			if (param1 != null) {
				param = (String) param1.toString();
				s = s.replace("{0}", param);
			}
		}
		else {
			int ib = msg.indexOf("{");
			int ie = msg.indexOf("}");
			if (ib != -1 && ie != -1) {
				if (ie - ib > 1) {
					s = resourceMessage(msg.substring(ib + 1, ie));
					String param = null;
					if (param1 != null) {
						param = (String) param1.toString();
						s = s.replace("{0}", param);
					}
				}
			}
		}
		this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, s, null));
	}

	/**
	 * Add faces error message
	 * 
	 * @param clientId
	 * @param msg
	 *            can be simple message or resource bundle message key of the form {key}
	 * @param param1
	 */
	public void errorMessage(final String clientId, final String msg, final Object param1) {
		String s = msg;
		if (s.indexOf("{0}") != -1) {
			String param = null;
			if (param1 != null) {
				param = (String) param1.toString();
				s = s.replace("{0}", param);
			}
		}
		else {
			int ib = msg.indexOf("{");
			int ie = msg.indexOf("}");
			if (ib != -1 && ie != -1) {
				if (ie - ib > 1) {
					s = resourceMessage(msg.substring(ib + 1, ie));
					String param = null;
					if (param1 != null) {
						param = (String) param1.toString();
						s = s.replace("{0}", param);
					}
				}
			}
		}
		this.facesContext.addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, s, null));
	}

	/**
	 * Add faces warning message
	 * 
	 * @param msg
	 *            can be simple message or resource bundle message key of the form {key}
	 * @param param1
	 */
	public void warningMessage(final String msg, final Object param1) {
		String s = msg;
		if (s.indexOf("{0}") != -1) {
			String param = null;
			if (param1 != null) {
				param = (String) param1.toString();
				s = s.replace("{0}", param);
			}
		}
		else {
			int ib = msg.indexOf("{");
			int ie = msg.indexOf("}");
			if (ib != -1 && ie != -1) {
				if (ie - ib > 1) {
					s = resourceMessage(msg.substring(ib + 1, ie));
					String param = null;
					if (param1 != null) {
						param = (String) param1.toString();
						s = s.replace("{0}", param);
					}
				}
			}
		}
		this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, s, null));
	}

	/**
	 * Add faces warning message
	 * 
	 * @param clientId
	 * @param msg
	 *            can be simple message or resource bundle message key of the form {key}
	 * @param param1
	 */
	public void warningMessage(final String clientId, final String msg, final Object param1) {
		String s = msg;
		if (s.indexOf("{0}") != -1) {
			String param = null;
			if (param1 != null) {
				param = (String) param1.toString();
				s = s.replace("{0}", param);
			}
		}
		else {
			int ib = msg.indexOf("{");
			int ie = msg.indexOf("}");
			if (ib != -1 && ie != -1) {
				if (ie - ib > 1) {
					s = resourceMessage(msg.substring(ib + 1, ie));
					String param = null;
					if (param1 != null) {
						param = (String) param1.toString();
						s = s.replace("{0}", param);
					}
				}
			}
		}
		this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, s, null));
	}

	/**
	 * Add faces fatal message
	 * 
	 * @param msg
	 *            can be simple message or resource bundle message key of the form {key}
	 * @param param1
	 */
	public void fatalMessage(final String msg, final Object param1) {
		String s = msg;
		if (s.indexOf("{0}") != -1) {
			String param = null;
			if (param1 != null) {
				param = (String) param1.toString();
				s = s.replace("{0}", param);
			}
		}
		else {
			int ib = msg.indexOf("{");
			int ie = msg.indexOf("}");
			if (ib != -1 && ie != -1) {
				if (ie - ib > 1) {
					s = resourceMessage(msg.substring(ib + 1, ie));
					String param = null;
					if (param1 != null) {
						param = (String) param1.toString();
						s = s.replace("{0}", param);
					}
				}
			}
		}
		this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, s, null));
	}

	/**
	 * Add faces fatal message
	 * 
	 * @param clientId
	 * @param msg
	 *            can be simple message or resource bundle message key of the form {key}
	 * @param param1
	 */
	public void fatalMessage(final String clientId, final String msg, final Object param1) {
		String s = msg;
		if (s.indexOf("{0}") != -1) {
			String param = null;
			if (param1 != null) {
				param = (String) param1.toString();
				s = s.replace("{0}", param);
			}
		}
		else {
			int ib = msg.indexOf("{");
			int ie = msg.indexOf("}");
			if (ib != -1 && ie != -1) {
				if (ie - ib > 1) {
					s = resourceMessage(msg.substring(ib + 1, ie));
					String param = null;
					if (param1 != null) {
						param = (String) param1.toString();
						s = s.replace("{0}", param);
					}
				}
			}
		}
		this.facesContext.addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_FATAL, s, null));
	}

	/**
	 * Build faces info message
	 * 
	 * @param msg
	 * @param param1
	 * @param param2
	 */
	public void infoMessage(final String msg, final Object param1, final Object param2) {
		String s = msg;
		if (s.indexOf("{0}") != -1) {
			String param = null;
			if (param1 != null) {
				param = (String) param1.toString();
				s = s.replace("{0}", param);
			}
		}
		if (s.indexOf("{1}") != -1) {
			String param = null;
			if (param2 != null) {
				param = (String) param2.toString();
				s = s.replace("{1}", param);
			}
		}
		this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, s, null));
	}

	/**
	 * Build faces info message
	 * 
	 * @param clientId
	 * @param msg
	 * @param param1
	 * @param param2
	 */
	public void infoMessage(final String clientId, final String msg, final Object param1, final Object param2) {
		String s = msg;
		if (s.indexOf("{0}") != -1) {
			String param = null;
			if (param1 != null) {
				param = (String) param1.toString();
				s = s.replace("{0}", param);
			}
		}
		if (s.indexOf("{1}") != -1) {
			String param = null;
			if (param2 != null) {
				param = (String) param2.toString();
				s = s.replace("{1}", param);
			}
		}
		this.facesContext.addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_INFO, s, null));
	}

	/**
	 * Build faces error message
	 * 
	 * @param msg
	 * @param param1
	 * @param param2
	 */
	public void errorMessage(final String msg, final Object param1, final Object param2) {
		String s = msg;
		if (s.indexOf("{0}") != -1) {
			String param = null;
			if (param1 != null) {
				param = (String) param1.toString();
				s = s.replace("{0}", param);
			}
		}
		if (s.indexOf("{1}") != -1) {
			String param = null;
			if (param2 != null) {
				param = (String) param2.toString();
				s = s.replace("{1}", param);
			}
		}
		this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, s, null));
	}

	/**
	 * Build faces error message
	 * 
	 * @param clientId
	 * @param msg
	 * @param param1
	 * @param param2
	 */
	public void errorMessage(final String clientId, final String msg, final Object param1, final Object param2) {
		String s = msg;
		if (s.indexOf("{0}") != -1) {
			String param = null;
			if (param1 != null) {
				param = (String) param1.toString();
				s = s.replace("{0}", param);
			}
		}
		if (s.indexOf("{1}") != -1) {
			String param = null;
			if (param2 != null) {
				param = (String) param2.toString();
				s = s.replace("{1}", param);
			}
		}
		this.facesContext.addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, s, null));
	}

	/**
	 * Build faces warning message
	 * 
	 * @param msg
	 * @param param1
	 * @param param2
	 */
	public void warningMessage(final String msg, final Object param1, final Object param2) {
		String s = msg;
		if (s.indexOf("{0}") != -1) {
			String param = null;
			if (param1 != null) {
				param = (String) param1.toString();
				s = s.replace("{0}", param);
			}
		}
		if (s.indexOf("{1}") != -1) {
			String param = null;
			if (param2 != null) {
				param = (String) param2.toString();
				s = s.replace("{1}", param);
			}
		}
		this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, s, null));
	}

	/**
	 * Build faces warning message
	 * 
	 * @param clientId
	 * @param msg
	 * @param param1
	 * @param param2
	 */
	public void warningMessage(final String clientId, final String msg, final Object param1, final Object param2) {
		String s = msg;
		if (s.indexOf("{0}") != -1) {
			String param = null;
			if (param1 != null) {
				param = (String) param1.toString();
				s = s.replace("{0}", param);
			}
		}
		if (s.indexOf("{1}") != -1) {
			String param = null;
			if (param2 != null) {
				param = (String) param2.toString();
				s = s.replace("{1}", param);
			}
		}
		this.facesContext.addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_WARN, s, null));
	}

	/**
	 * Build faces fatal message
	 * 
	 * @param msg
	 * @param param1
	 * @param param2
	 */
	public void fatalMessage(final String msg, final Object param1, final Object param2) {
		String s = msg;
		if (s.indexOf("{0}") != -1) {
			String param = null;
			if (param1 != null) {
				param = (String) param1.toString();
				s = s.replace("{0}", param);
			}
		}
		if (s.indexOf("{1}") != -1) {
			String param = null;
			if (param2 != null) {
				param = (String) param2.toString();
				s = s.replace("{1}", param);
			}
		}
		this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, s, null));
	}

	/**
	 * Build faces fatal message
	 * 
	 * @param clientId
	 * @param msg
	 * @param param1
	 * @param param2
	 */
	public void fatalMessage(final String clientId, final String msg, final Object param1, final Object param2) {
		String s = msg;
		if (s.indexOf("{0}") != -1) {
			String param = null;
			if (param1 != null) {
				param = (String) param1.toString();
				s = s.replace("{0}", param);
			}
		}
		if (s.indexOf("{1}") != -1) {
			String param = null;
			if (param2 != null) {
				param = (String) param2.toString();
				s = s.replace("{1}", param);
			}
		}
		this.facesContext.addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_FATAL, s, null));
	}
}
