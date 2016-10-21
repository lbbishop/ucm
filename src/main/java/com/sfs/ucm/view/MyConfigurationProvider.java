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

import com.sfs.ucm.model.AuthUser;

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
			String userId = (String) request.getSession().getAttribute("USERID");
			return (userId != null);
		}
	};

	@Override
	public Configuration getConfiguration(ServletContext context) {

		return ConfigurationBuilder.begin()

				.addRule().when(Direction.isInbound().andNot(loggedIn))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("authenticator.login")))).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/home").to("/home.jsf"))

				.addRule(Join.path("/logout").to("/logout.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("authenticator.logout")))).after(PhaseId.RESTORE_VIEW))

				// =========== help ==============
				.addRule(Join.path("/help/overview").to("/help/overview.jsf")).addRule(Join.path("/help/workflow").to("/help/workflow.jsf")).addRule(Join.path("/help/about").to("/help/about.jsf"))

				// =========== admin ==============
				.addRule(Join.path("/admin/users").to("/admin/users.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("userAction.load")))).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/admin/settings").to("/admin/settings.jsf"))

				// ================= user ================
				.addRule(Join.path("/user/preferences").to("/user/preferences.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("preferencesAction.load")))).after(PhaseId.RESTORE_VIEW))

				// =========== reports ==============
				.addRule(Join.path("/reports/testcoverage").to("/reports/testcoverage.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("testCoverageAction.load")))).after(PhaseId.RESTORE_VIEW))

				// =========== project ==============
				.addRule(Join.path("/projects").to("/project/projects.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("projectAction.load")))).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/techfactors").to("/project/techfactors.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("technicalFactorsAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("technicalFactorsAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/envfactors").to("/project/envfactors.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("environmentalFactorsAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("environmentalFactorsAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/members").to("/project/members.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("memberAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("memberAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/packages").to("/project/packages.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("projectPackageAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("projectPackageAction.id")).after(PhaseId.RESTORE_VIEW))

				// =========== requirement ==============
				.addRule(Join.path("/project/{id}/productvision").to("/requirement/productvision.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("productVisionAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("productVisionAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/actors").to("/requirement/actors.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("actorAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("actorAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/risks").to("/requirement/risks.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("riskAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("riskAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/actorlist").to("/requirement/actorlist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("actorAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("actorAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/stakeholders").to("/requirement/stakeholders.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("stakeholderAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("stakeholderAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/stakeholderlist").to("/requirement/stakeholderlist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("stakeholderAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("stakeholderAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/stakeholderrequests").to("/requirement/stakeholderrequests.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("stakeholderRequestAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("stakeholderRequestAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/stakeholderrequestlist").to("/requirement/stakeholderrequestlist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("stakeholderRequestAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("stakeholderRequestAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/constraints").to("/requirement/constraints.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("constraintAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("constraintAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/constraintlist").to("/requirement/constraintlist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("constraintAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("constraintAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/features").to("/requirement/features.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("featureAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("featureAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/featurelist").to("/requirement/featurelist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("featureAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("featureAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/specificatons").to("/requirement/specifications.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("requirementAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("requirementAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/specificationlist").to("/requirement/specificationlist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("specificationAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("specificationAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/basicflow/{id}").to("/requirement//basicflow.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("useCaseFlowAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("useCaseFlowAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/usecases").to("/requirement/usecases.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("useCaseAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("useCaseAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/usecaselist").to("/requirement/usecaselist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("useCaseAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("useCaseAction.id")).after(PhaseId.RESTORE_VIEW))
				
				.addRule(Join.path("/usecase/{id}/alternativeflows").to("/requirement/alternativeflows.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("alternativeFlowAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("alternativeFlowAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/alternativeflow/{id}").to("/requirement/alternativeflow.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("alternativeFlowAction.loadFlow")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("alternativeFlowAction.flowId")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/risklist").to("/requirement/risklist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("riskAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("riskAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/glossary").to("/requirement/glossary.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("glossaryTermAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("glossaryTermAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/glossarylist").to("/requirement/glossarylist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("glossaryTermAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("glossaryTermAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/businessrules").to("/requirement/businessrules.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("businessRuleAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("businessRuleAction.id")).after(PhaseId.RESTORE_VIEW))

				// =========== test ==============
				.addRule(Join.path("/testset{id}/specificationtests").to("/test/specificationtests.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("specificationTestAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("specificationTestAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/testplan").to("/test/testplan.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("testPlanAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("testPlanAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/testplan/{id}/unittests").to("/test/unittests.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("unitTestAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("unitTestAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/testplan/{id}/testsets").to("/test/testsets.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("testSetAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("testSetAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/testset/{id}").to("/test/testset.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("testSetReporter.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("testSetReporter.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/testset/{id}/testcases").to("/test/testcases.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("testCaseAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("testCaseAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/testcase/{id}").to("/test/testcase.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("testCaseFlowAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("testCaseFlowAction.id")).after(PhaseId.RESTORE_VIEW))

				// =========== issues ==============
				.addRule(Join.path("/project/{id}/issues").to("/issue/issues.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("issueAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("issueAction.id")).after(PhaseId.RESTORE_VIEW))

				.addRule(Join.path("/project/{id}/issuelist").to("/issue/issuelist.jsf"))
				.perform(PhaseOperation.enqueue(new IgnorePostbackOperation(Invoke.binding(El.retrievalMethod("issueAction.load")))).after(PhaseId.RESTORE_VIEW)).where("id")
				.bindsTo(PhaseBinding.to(El.property("issueAction.id")).after(PhaseId.RESTORE_VIEW))

		;
	}

	@Override
	public int priority() {
		return 10;
	}
}
