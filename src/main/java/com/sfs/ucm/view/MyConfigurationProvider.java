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
package com.sfs.ucm.view;

import javax.faces.event.PhaseId;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.config.Invoke;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.el.El;
import org.ocpsoft.rewrite.faces.annotation.config.IgnorePostbackOperation;
import org.ocpsoft.rewrite.faces.config.PhaseBinding;
import org.ocpsoft.rewrite.faces.config.PhaseOperation;
import org.ocpsoft.rewrite.servlet.config.HttpCondition;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * Rewrite configuration provider
 * 
 * @author lbishop
 * 
 */
public class MyConfigurationProvider extends HttpConfigurationProvider {

	Condition loggedIn = new HttpCondition() {
		@Override
		public boolean evaluateHttp(HttpServletRewrite event, EvaluationContext context) {

			HttpServletRequest request = event.getRequest();
			String username = (String) request.getSession().getAttribute("User");
			return (username != null);
		}
	};

	Condition isAdmin = new HttpCondition() {
		@Override
		public boolean evaluateHttp(HttpServletRewrite event, EvaluationContext context) {

			HttpServletRequest request = event.getRequest();
			String username = (String) request.getSession().getAttribute("Admin");
			return (username != null);
		}
	};

	@Override
	public Configuration getConfiguration(ServletContext context) {

		return ConfigurationBuilder.begin()

		.addRule().when(Direction.isInbound().andNot(loggedIn))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("authenticator.login")))).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/home").to("/home.jsf"))

				.addRule(Join.path("/logout").to("/logout.jsf"))

				.addRule(Join.path("/help/about").to("/help/about.jsf"))

				.addRule(Join.path("/admin/users").to("/admin/users.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("userAction.load")))).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/admin/settings").to("/admin/settings.jsf"))

				.addRule(Join.path("/user/preferences").to("/user/preferences.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("preferencesAction.load")))).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/user/viewset").to("/user/viewset.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("viewSetAction.load")))).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/reports/activity").to("/reports/activity.jsf"))

				.addRule(Join.path("/reports/backlog").to("/reports/backlog.jsf"))

				.addRule(Join.path("/reports/roadmap").to("/reports/roadmap.jsf"))

				.addRule(Join.path("/reports/testcoverage").to("/reports/testcoverage.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("testCoverageAction.load")))).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/reports/changes").to("/reports/changes.jsf"))

				.addRule(Join.path("/reports/testcaseresults").to("/reports/testcaseresults.jsf"))

				.addRule(Join.path("/reports/issues").to("/reports/issues.jsf"))

				.addRule(Join.path("/reports/traceability").to("/reports/traceability.jsf"))

				.addRule(Join.path("/reports/inprogress").to("/reports/inprogress.jsf"))

				.addRule(Join.path("/reports/estimator").to("/reports/estimator.jsf"))

				.addRule(Join.path("/projects").to("/projects.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("projectAction.load")))).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/info").to("/project.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("projectInfoManager.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("projectInfoManager.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/productvision").to("/productvision.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("productVisionAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("productVisionAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/techfactors").to("/techfactors.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("technicalFactorsAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("technicalFactorsAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/envfactors").to("/envfactors.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("environmentalFactorsAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("environmentalFactorsAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/members").to("/members.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("memberAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("memberAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/actors").to("/actors.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("actorAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("actorAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/risks").to("/risks.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("riskAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("riskAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/actorlist").to("/actorlist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("actorAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("actorAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/productreleases").to("/productreleases.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("productReleaseAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("productReleaseAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/businessprocesses").to("/businessprocesses.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("processAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("processAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/stakeholders").to("/stakeholders.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("stakeholderAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("stakeholderAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/stakeholderlist").to("/stakeholderlist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("stakeholderAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("stakeholderAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/stakeholderrequests").to("/stakeholderrequests.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("stakeholderRequestAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("stakeholderRequestAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/stakeholderrequestlist").to("/stakeholderrequestlist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("stakeholderRequestAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("stakeholderRequestAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/tasks").to("/tasks.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("taskAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("taskAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/tasklist").to("/tasklist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("taskAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("taskAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/notes").to("/notes.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("noteAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("noteAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/notelist").to("/notelist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("noteAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("noteAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/constraints").to("/constraints.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("constraintAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("constraintAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/constraintlist").to("/constraintlist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("constraintAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("constraintAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/features").to("/features.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("featureAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("featureAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/featurelist").to("/featurelist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("featureAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("featureAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/requirements").to("/requirements.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("requirementAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("requirementAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/requirementlist").to("/requirementlist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("requirementAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("requirementAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/testset{id}/requirementtests").to("/requirementtests.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("requirementTestAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("requirementTestAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/requirementrules").to("/requirementrules.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("requirementRuleAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("requirementRuleAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/requirementrulelist").to("/requirementrulelist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("requirementRuleAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("requirementRuleAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/requirementruletests").to("/requirementruletests.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("requirementRuleTestAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("requirementRuleTestAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/packages").to("/packages.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("projectPackageAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("projectPackageAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/usecases").to("/usecases.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("useCaseAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("useCaseAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/usecaselist").to("/usecaselist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("useCaseAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("useCaseAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/usecaserules").to("/usecaserules.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("useCaseRuleAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("useCaseRuleAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/usecaserulelist").to("/usecaserulelist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("useCaseRuleAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("useCaseRuleAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/usecaseruletests").to("/usecaseruletests.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("useCaseRuleTestAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("useCaseRuleTestAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/usecase/{id}/alternativeflows").to("/alternativeflows.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("alternativeFlowAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("alternativeFlowAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/alternativeflow/{id}").to("/alternativeflow.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("alternativeFlowAction.loadFlow")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("alternativeFlowAction.flowId")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/risklist").to("/risklist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("riskAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("riskAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/estimates").to("/estimates.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("estimateAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("estimateAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/estimatelist").to("/estimatelist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("estimateAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("estimateAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/userstories").to("/userstories.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("userStoryAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("userStoryAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/userstorylist").to("/userstorylist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("userStoryAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("userStoryAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/glossary").to("/glossary.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("glossaryTermAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("glossaryTermAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/glossarylist").to("/glossarylist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("glossaryTermAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("glossaryTermAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/resources").to("/resources.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("resourceAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("resourceAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/businessrules").to("/businessrules.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("businessRuleAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("businessRuleAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/iterations").to("/iterations.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("iterationAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("iterationAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/iteration{id}/activities").to("/activities.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("activityAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("activityAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/iteration/{id}/activitylist").to("/activitylist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("activityAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("activityAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/testplan").to("/testplan.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("testPlanAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("testPlanAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/testplan/{id}/unittests").to("/unittests.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("unitTestAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("unitTestAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/testplan/{id}/testsets").to("/testsets.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("testSetAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("testSetAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/testset/{id}").to("/testset.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("testSetReporter.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("testSetReporter.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/testset/{id}/testcases").to("/testcases.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("testCaseAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("testCaseAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/testcase/{id}").to("/testcase.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("testCaseFlowAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("testCaseFlowAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/basicflow/{id}").to("/basicflow.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("useCaseFlowAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("useCaseFlowAction.id")).after(PhaseId.RESTORE_VIEW))

		;
	}

	@Override
	public int priority() {
		return 10;
	}
}
