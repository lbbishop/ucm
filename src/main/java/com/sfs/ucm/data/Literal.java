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
 * Typesafe enum pattern used to store application literal references
 * 
 * @author lbbishop
 * 
 */

public final class Literal {

	private final String name;

	private Literal(String name) {
		this.name = name;
	}

	/**
	 * toString
	 * 
	 * @return name
	 */
	public String toString() {
		return name;
	}

	/**
	 * resource message bundle
	 */
	public static final Literal RESOURCE_BUNDLE = new Literal("com.sfs.ucm.i18n.messages");
	
	/**
	 *  authorization group types
	 */
	public static final Literal GROUPTYPE_SECURITY_LEVEL = new Literal("SECURITY_LEVEL");

	/**
	 * navigation outcomes
	 */
	public static final Literal NAV_SUCCESS = new Literal("success");
	public static final Literal NAV_FAILURE = new Literal("failure");
	public static final Literal NAV_HOME = new Literal("/home.jsf?faces-redirect=true");

	public static final Literal APPNAME = new Literal("UCM");

	// survival test
	public static final Literal SURVIVALTEST_NOTREALLY = new Literal("Not Really");
	public static final Literal SURVIVALTEST_PROBABLY = new Literal("Probably");
	public static final Literal SURVIVALTEST_YES = new Literal("Yes");
	public static final Literal SURVIVALTEST_NOTAPPLICABLE = new Literal("Not Applicable");

	// flow types
	public static final Literal FLOW_BASIC = new Literal("Basic");
	public static final Literal FLOW_ALTERNATE = new Literal("Alternate");

	// member roles
	public static final Literal MEMBERROLE_ANALYST = new Literal("Analyst");
	public static final Literal MEMBERROLE_ARCHITECT = new Literal("Architect");
	public static final Literal MEMBERROLE_CONFIGURATIONMANAGER = new Literal("Configuration Manager");
	public static final Literal MEMBERROLE_DEVELOPER = new Literal("Developer");
	public static final Literal MEMBERROLE_CUSTOMER = new Literal("Customer");
	public static final Literal MEMBERROLE_GUEST = new Literal("Guest User");
	public static final Literal MEMBERROLE_PROCESSENGINEER = new Literal("Process Engineer");
	public static final Literal MEMBERROLE_PRODUCTOWNER = new Literal("Product Owner");
	public static final Literal MEMBERROLE_PROJECTMANAGER = new Literal("Project Manager");
	public static final Literal MEMBERROLE_QUALITYASSURANCE = new Literal("Quality Assurance");
	public static final Literal MEMBERROLE_SCRUMMASTER = new Literal("Scrum Master");
	public static final Literal MEMBERROLE_SUBJECTMATTEREXPERT = new Literal("Subject Matter Expert");
	public static final Literal MEMBERROLE_TECHNICALWRITER = new Literal("Technical Writer");
	public static final Literal MEMBERROLE_TESTER = new Literal("Tester");
	public static final Literal MEMBERROLE_USERINTERFACEDESIGNER = new Literal("User Interface Designer");
	public static final Literal MEMBERROLE_USER = new Literal("User");

	// roles
	public static final Literal ROLE_ADMIN = new Literal("Admin");
	public static final Literal ROLE_MANAGER = new Literal("Manager");
	public static final Literal ROLE_USER = new Literal("User");
	public static final Literal ROLE_GUEST = new Literal("Guest");

	// role groups
	public static final Literal ROLEGROUP_ADMINS = new Literal("Admins");
	public static final Literal ROLEGROUP_MANAGERS = new Literal("Managers");
	public static final Literal ROLEGROUP_USERS = new Literal("Users");

	// requirement, actor, use case complexity
	public static final Literal COMPLEXITY_SIMPLE = new Literal("Simple");
	public static final Literal COMPLEXITY_AVERAGE = new Literal("Average");
	public static final Literal COMPLEXITY_COMPLEX = new Literal("Complex");

	// artifact prefixes
	public static final Literal PREFIX_ACTOR = new Literal("ACT");
	public static final Literal PREFIX_CATEGORY = new Literal("CAT");
	public static final Literal PREFIX_ENVIRONMENT = new Literal("ENV");
	public static final Literal PREFIX_ESTIMATE = new Literal("EST");
	public static final Literal PREFIX_FEATURE = new Literal("FEAT");
	public static final Literal PREFIX_FLOWSTEPRULE = new Literal("FSRL");
	public static final Literal PREFIX_BASICFLOW = new Literal("BFLW");
	public static final Literal PREFIX_ALTERNATIVEFLOW = new Literal("AFLW");
	public static final Literal PREFIX_SUBFLOW = new Literal("SFLW");
	public static final Literal PREFIX_HLP = new Literal("HLP");
	public static final Literal PREFIX_ITERATION = new Literal("ITR");
	public static final Literal PREFIX_ITERATIONACTIVITY = new Literal("IACT");
	public static final Literal PREFIX_NOTE = new Literal("NOTE");
	public static final Literal PREFIX_PRODUCTVISION = new Literal("PV");
	public static final Literal PREFIX_PROJECTPACKAGE = new Literal("PKG");
	public static final Literal PREFIX_PROJECTMEMBER = new Literal("PM");
	public static final Literal PREFIX_SPECIFICATION = new Literal("SPEC");
	public static final Literal PREFIX_SPECIFICATIONTEST = new Literal("SPT");
	public static final Literal PREFIX_SPECIFICATIONRULE = new Literal("SPRL");
	public static final Literal PREFIX_SPECIFICATIONRULETEST = new Literal("SPRT");
	public static final Literal PREFIX_RISK = new Literal("RSK");
	public static final Literal PREFIX_SCENARIO = new Literal("SCN");
	public static final Literal PREFIX_STAKEHOLDER = new Literal("STK");
	public static final Literal PREFIX_STAKEHOLDERREQUEST = new Literal("STRQ");
	public static final Literal PREFIX_ISSUE = new Literal("ISS");
	public static final Literal PREFIX_BUSINESSRULE = new Literal("BR");
	public static final Literal PREFIX_CONSTRAINT = new Literal("CST");
	public static final Literal PREFIX_GLOSSARY = new Literal("GLS");
	public static final Literal PREFIX_TESTCASE = new Literal("TC");
	public static final Literal PREFIX_TESTSET = new Literal("TSET");
	public static final Literal PREFIX_UNITTEST = new Literal("UT");
	public static final Literal PREFIX_USECASE = new Literal("UC");
	public static final Literal PREFIX_USECASERULE = new Literal("UCRL");
	public static final Literal PREFIX_USECASERULETEST = new Literal("UCRT");
	public static final Literal PREFIX_USER = new Literal("USR");

	// environmental perceived impact
	public static final Literal ECF_IMPACT_NOIMPACT = new Literal("No Impact"); // 0
	public static final Literal ECF_IMPACT_STRONGNEGATIVE = new Literal("Strong Negative Impact"); // 1
	public static final Literal ECF_IMPACT_NEGATIVE = new Literal("Negative Impact"); // 2
	public static final Literal ECF_IMPACT_AVERAGE = new Literal("Average Impact"); // 3
	public static final Literal ECF_IMPACT_POSITIVE = new Literal("Positive Impact"); // 4
	public static final Literal ECF_IMPACT_STRONGPOSITIVE = new Literal("Strong Positive Impact"); // 5

	// technical perceived complexity
	public static final Literal TCF_COMPLEXITY_IRRELEVANT = new Literal("Irrelevant"); // 0
	public static final Literal TCF_COMPLEXITY_VERYLOWIMPORTANCE = new Literal("Very Low Importance"); // 1
	public static final Literal TCF_COMPLEXITY_LOWIMPORTANCE = new Literal("Low Importance"); // 1
	public static final Literal TCF_COMPLEXITY_AVERAGE = new Literal("Average Importance"); // 3
	public static final Literal TCF_COMPLEXITY_HIGHIMPORTANCE = new Literal("High Importance"); // 4
	public static final Literal TCF_COMPLEXITY_VERYHIGHIMPORTANCE = new Literal("Very High Importance"); // 5

}