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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.model.SelectItem;
import javax.inject.Named;

import com.sfs.ucm.model.Actor;
import com.sfs.ucm.model.UseCase;

/**
 * Reference data producers
 * 
 * @author lbishop
 * 
 */
@ApplicationScoped
public class ReferenceDataProducer {

	private static List<SelectItem> ecfImpactItems;
	static {
		ecfImpactItems = new ArrayList<SelectItem>();
		ecfImpactItems.add(new SelectItem(Constants.ECF_IMPACT_NOIMPACT, Literal.ECF_IMPACT_NOIMPACT.toString()));
		ecfImpactItems.add(new SelectItem(Constants.ECF_IMPACT_STRONGNEGATIVE, Literal.ECF_IMPACT_STRONGNEGATIVE.toString()));
		ecfImpactItems.add(new SelectItem(Constants.ECF_IMPACT_NEGATIVE, Literal.ECF_IMPACT_NEGATIVE.toString()));
		ecfImpactItems.add(new SelectItem(Constants.ECF_IMPACT_AVERAGE, Literal.ECF_IMPACT_AVERAGE.toString()));
		ecfImpactItems.add(new SelectItem(Constants.ECF_IMPACT_POSITIVE, Literal.ECF_IMPACT_POSITIVE.toString()));
		ecfImpactItems.add(new SelectItem(Constants.ECF_IMPACT_STRONGPOSITIVE, Literal.ECF_IMPACT_STRONGPOSITIVE.toString()));
	}

	private static List<SelectItem> tcfComplexityItems;
	static {
		tcfComplexityItems = new ArrayList<SelectItem>();
		tcfComplexityItems.add(new SelectItem(Constants.TCF_COMPLEXITY_IRRELEVANT, Literal.TCF_COMPLEXITY_IRRELEVANT.toString()));
		tcfComplexityItems.add(new SelectItem(Constants.TCF_COMPLEXITY_VERYLOWIMPORTANCE, Literal.TCF_COMPLEXITY_VERYLOWIMPORTANCE.toString()));
		tcfComplexityItems.add(new SelectItem(Constants.TCF_COMPLEXITY_LOWIMPORTANCE, Literal.TCF_COMPLEXITY_LOWIMPORTANCE.toString()));
		tcfComplexityItems.add(new SelectItem(Constants.TCF_COMPLEXITY_AVERAGE, Literal.TCF_COMPLEXITY_AVERAGE.toString()));
		tcfComplexityItems.add(new SelectItem(Constants.TCF_COMPLEXITY_HIGHIMPORTANCE, Literal.TCF_COMPLEXITY_HIGHIMPORTANCE.toString()));
		tcfComplexityItems.add(new SelectItem(Constants.TCF_COMPLEXITY_VERYHIGHIMPORTANCE, Literal.TCF_COMPLEXITY_VERYHIGHIMPORTANCE.toString()));
	}

	private static List<SelectItem> useCaseComplexityItems;
	static {
		useCaseComplexityItems = new ArrayList<SelectItem>();
		useCaseComplexityItems.add(new SelectItem(UseCase.SIMPLE, Literal.COMPLEXITY_SIMPLE.toString()));
		useCaseComplexityItems.add(new SelectItem(UseCase.AVERAGE, Literal.COMPLEXITY_AVERAGE.toString()));
		useCaseComplexityItems.add(new SelectItem(UseCase.COMPLEX, Literal.COMPLEXITY_COMPLEX.toString()));
	}

	private static List<SelectItem> actorComplexityItems;
	static {
		actorComplexityItems = new ArrayList<SelectItem>();
		actorComplexityItems.add(new SelectItem(Actor.SIMPLE, Literal.COMPLEXITY_SIMPLE.toString()));
		actorComplexityItems.add(new SelectItem(Actor.AVERAGE, Literal.COMPLEXITY_AVERAGE.toString()));
		actorComplexityItems.add(new SelectItem(Actor.COMPLEX, Literal.COMPLEXITY_COMPLEX.toString()));
	}

	/**
	 * 
	 * @return List of ModelResourceType
	 */
	@Produces
	@Named
	public ModelResourceType[] getModelResourceTypes() {
		return ModelResourceType.values();
	}

	@Produces
	@Named
	private SelectItem[] getResourceSelectItems() {
		SelectItem[] options = new SelectItem[ModelResourceType.values().length + 1];
		options[0] = new SelectItem("", "Any");
		for (int i = 0; i < ModelResourceType.values().length; i++) {
			String value = ModelResourceType.values()[i].toString();
			options[i + 1] = new SelectItem(value, value);
		}
		return options;
	}

	/**
	 * 
	 * @return List of BusinessRuleType
	 */
	@Produces
	@Named
	public RuleType[] getRuleTypes() {
		return RuleType.values();
	}

	/**
	 * 
	 * @return List of UserRoleType
	 */
	@Produces
	@Named
	public UserRoleType[] getUserRoleTypes() {
		return UserRoleType.values();
	}

	/**
	 * 
	 * @return List of RiskLevelType
	 */
	@Produces
	@Named
	public RiskLevelType[] getRiskLevelTypes() {
		return RiskLevelType.values();
	}

	/**
	 * 
	 * @return Array of PriorityType
	 */
	@Produces
	@Named
	public PriorityType[] getPriorityTypes() {
		return PriorityType.values();
	}

	/**
	 * 
	 * @return Array of DifficultyType
	 */
	@Produces
	@Named
	public DifficultyType[] getDifficultyTypes() {
		return DifficultyType.values();
	}

	/**
	 * 
	 * @return Array of BaselineType
	 */
	@Produces
	@Named
	public BaselineType[] getBaselineTypes() {
		return BaselineType.values();
	}

	/**
	 * 
	 * @return List of ProjectStatusType
	 */
	@Produces
	@Named
	public ProjectStatusType[] getProjectStatusTypes() {
		return ProjectStatusType.values();
	}

	/**
	 * 
	 * @return List of TestStatusType
	 */
	@Produces
	@Named
	public TestStatusType[] getTestStatusTypes() {
		return TestStatusType.values();
	}

	/**
	 * 
	 * @return List of TestResultType
	 */
	@Produces
	@Named
	public List<TestResultType> getTestResultTypes() {
		return new ArrayList<TestResultType>(Arrays.asList(TestResultType.Passed, TestResultType.Failed));
	}

	/**
	 * 
	 * @return List of StabilityType
	 */
	@Produces
	@Named
	public StabilityType[] getStabilityTypes() {
		return StabilityType.values();
	}

	/**
	 * 
	 * @return Array of StatusType
	 */
	@Produces
	@Named
	public StatusType[] getStatusTypes() {
		return StatusType.values();
	}

	@Produces
	@Named
	private SelectItem[] getStatusSelectItems() {
		SelectItem[] options = new SelectItem[StatusType.values().length + 1];
		options[0] = new SelectItem("", "Any");
		for (int i = 0; i < StatusType.values().length; i++) {
			String value = StatusType.values()[i].toString();
			options[i + 1] = new SelectItem(value, value);
		}
		return options;
	}

	/**
	 * 
	 * @return List of QualityType
	 */
	@Produces
	@Named
	public QualityType[] getQualityTypes() {
		return QualityType.values();
	}

	/**
	 * 
	 * @return List of IssueType
	 */
	@Produces
	@Named
	public IssueType[] getIssueTypes() {
		return IssueType.values();
	}

	@Produces
	@Named
	private SelectItem[] getIssueTypeSelectItems() {
		SelectItem[] options = new SelectItem[IssueType.values().length + 1];
		options[0] = new SelectItem("", "Any");
		for (int i = 0; i < IssueType.values().length; i++) {
			String value = IssueType.values()[i].toString();
			options[i + 1] = new SelectItem(value, value);
		}
		return options;
	}

	/**
	 * 
	 * @return List of EnvironmentType
	 */
	@Produces
	@Named
	public EnvironmentType[] getEnvironmentTypes() {
		return EnvironmentType.values();
	}

	/**
	 * 
	 * @return List of DevelopmentPhaseType
	 */
	@Produces
	@Named
	public DevelopmentPhaseType[] getDevelopmentPhaseTypes() {
		return DevelopmentPhaseType.values();
	}

	private static List<String> memberRoleItems;
	static {
		memberRoleItems = new ArrayList<String>();

		memberRoleItems.add(Literal.MEMBERROLE_ANALYST.toString());
		memberRoleItems.add(Literal.MEMBERROLE_ARCHITECT.toString());
		memberRoleItems.add(Literal.MEMBERROLE_CONFIGURATIONMANAGER.toString());
		memberRoleItems.add(Literal.MEMBERROLE_CUSTOMER.toString());
		memberRoleItems.add(Literal.MEMBERROLE_DEVELOPER.toString());
		memberRoleItems.add(Literal.MEMBERROLE_GUEST.toString());
		memberRoleItems.add(Literal.MEMBERROLE_PROCESSENGINEER.toString());
		memberRoleItems.add(Literal.MEMBERROLE_PRODUCTOWNER.toString());
		memberRoleItems.add(Literal.MEMBERROLE_PROJECTMANAGER.toString());
		memberRoleItems.add(Literal.MEMBERROLE_QUALITYASSURANCE.toString());
		memberRoleItems.add(Literal.MEMBERROLE_SCRUMMASTER.toString());
		memberRoleItems.add(Literal.MEMBERROLE_SUBJECTMATTEREXPERT.toString());
		memberRoleItems.add(Literal.MEMBERROLE_TECHNICALWRITER.toString());
		memberRoleItems.add(Literal.MEMBERROLE_TESTER.toString());
		memberRoleItems.add(Literal.MEMBERROLE_USERINTERFACEDESIGNER.toString());
		memberRoleItems.add(Literal.MEMBERROLE_USER.toString());
	}

	private static List<String> survivalTestItems;
	static {
		survivalTestItems = new ArrayList<String>();
		survivalTestItems.add(Literal.SURVIVALTEST_NOTAPPLICABLE.toString());
		survivalTestItems.add(Literal.SURVIVALTEST_NOTREALLY.toString());
		survivalTestItems.add(Literal.SURVIVALTEST_PROBABLY.toString());
		survivalTestItems.add(Literal.SURVIVALTEST_YES.toString());
	}

	private static Map<String, String> themes;
	static {
		themes = new TreeMap<String, String>();
		themes.put("Aristo", "aristo");
		themes.put("Twitter-Bootstrap", "bootstrap");
		// themes.put("After Noon", "afternoon");
		themes.put("After Work", "afterwork");
		// themes.put("Black-Tie", "black-tie");
		// themes.put("Blitzer", "blitzer");
		themes.put("Bluesky", "bluesky");
		// themes.put("Casablanca", "casablanca");
		// themes.put("Cruze", "cruze");
		themes.put("Cupertino", "cupertino");
		themes.put("Delta", "delta");
		// themes.put("Dark-Hive", "dark-hive");
		// themes.put("Dot-Luv", "dot-luv");
		// themes.put("Eggplant", "eggplant");
		themes.put("Home", "home");
		// themes.put("Excite-Bike", "excite-bike");
		// themes.put("Flick", "flick");
		themes.put("Glass-X", "glass-x");
		// themes.put("Hot-Sneaks", "hot-sneaks");
		// themes.put("Humanity", "humanity");
		// themes.put("Le-Frog", "le-frog");
		themes.put("Metro", "metroui");
		// themes.put("Midnight", "midnight");
		// themes.put("Mint-Choc", "mint-choc");
		// themes.put("Overcast", "overcast");
		// themes.put("Pepper-Grinder", "pepper-grinder");
		themes.put("Redmond", "redmond");
		themes.put("Rocket", "rocket");
		// themes.put("Sam", "sam");
		themes.put("Smoothness", "smoothness");
		themes.put("Sentinel", "sentinel");
	}

	private static String editorControls = "bold italic font size style color highlight bullets numbering alignleft center justify undo redo cut copy paste pastetext";

	private static String toolbarControls = "[[ 'Source','-','Save','NewPage','DocProps','Preview','Print','-','Templates' ],[ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ],[ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ],[ 'Table','HorizontalRule' ],[ 'Styles','Format','Font','FontSize' ],[ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote','CreateDiv','-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock','-','BidiLtr','BidiRtl' ]]";

	/**
	 * @return the ECF Impact Items
	 */
	@Produces
	@Named
	public static List<SelectItem> getEcfImpactItems() {
		return ecfImpactItems;
	}

	/**
	 * @return the TCF Complexity Items
	 */
	@Produces
	@Named
	public static List<SelectItem> getTcfComplexityItems() {
		return tcfComplexityItems;
	}

	/**
	 * @return the Actor Complexity Items
	 */
	@Produces
	@Named
	public static List<SelectItem> getActorComplexityItems() {
		return actorComplexityItems;
	}

	/**
	 * @return the UseCase Complexity Items
	 */
	@Produces
	@Named
	public static List<SelectItem> getUseCaseComplexityItems() {
		return useCaseComplexityItems;
	}

	/**
	 * @return the memberRoleItems
	 */
	@Produces
	@Named
	public static List<String> getMemberRoleItems() {
		return memberRoleItems;
	}

	/**
	 * @return the survivalTestItems
	 */
	@Produces
	@Named
	public static List<String> getSurvivalTestItems() {
		return survivalTestItems;
	}

	/**
	 * @return the themes
	 */
	@Produces
	@Named
	public static Map<String, String> getThemes() {
		return themes;
	}

	/**
	 * @return the editor controls string
	 */
	@Produces
	@Named
	public static String getEditorControls() {
		return editorControls;
	}

	/**
	 * @return the editor controls string
	 */
	@Produces
	@Named
	public static String getToolbarControls() {
		return toolbarControls;
	}
}
