package org.aimas.consert.unittest;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.aimas.consert.engine.core.EventTracker;
import org.aimas.consert.model.annotations.DefaultAnnotationData;
import org.aimas.consert.model.operators.AnnAfterOperator;
import org.aimas.consert.model.operators.AnnBeforeOperator;
import org.aimas.consert.model.operators.AnnIncludesOperator;
import org.aimas.consert.model.operators.AnnIntersectsOperator;
import org.aimas.consert.model.operators.AnnOverlappedByOperator;
import org.aimas.consert.model.operators.AnnOverlapsOperator;
import org.aimas.consert.model.operators.AnnStartsAfterOperator;
import org.aimas.consert.model.operators.AnnAfterOperator.AnnAfterEvaluatorDefinition;
import org.aimas.consert.model.operators.AnnBeforeOperator.AnnBeforeEvaluatorDefinition;
import org.aimas.consert.tests.hla.assertions.Position;
import org.aimas.consert.tests.hla.assertions.SittingLLA;
import org.aimas.consert.tests.hla.entities.Area;
import org.aimas.consert.tests.hla.entities.Person;
import org.aimas.consert.utils.TestSetup;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.EvaluatorOption;


public class AnnBeforeOperatorTest extends TestSetup {
	
	@Test
	public void testAnnBeforeOperator() {
		// setup KnowledgeBuilderConfiguration to add operator
		KnowledgeBuilderConfiguration builderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
		builderConf.setOption(EvaluatorOption.get("annOverlaps", new AnnOverlapsOperator.AnnOverlapsEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annOverlappedBy", new AnnOverlappedByOperator.AnnOverlappedByEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annHappensBefore", new AnnBeforeOperator.AnnBeforeEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annHappensAfter", new AnnAfterOperator.AnnAfterEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annIncludes", new AnnIncludesOperator.AnnIncludesEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annIntersects", new AnnIntersectsOperator.AnnIntersectsEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annStartsAfter", new AnnStartsAfterOperator.AnnStartsAfterEvaluatorDefinition()));
        
		// setup KieSession
		KieSession kSession = getKieSessionFromResources(builderConf, null, 
				"operator_test_rules/happensBeforeTest.drl",
				"casas_interwoven_constraints/PersonLocation_constraints.drl");
		EventTracker eventTracker = new EventTracker(kSession);
		TrackingAgendaEventListener agendaEvListener = new TrackingAgendaEventListener();
		kSession.addEventListener(agendaEvListener);
		
		// create a Person
		Person person = new Person("Alex");
		
		// create a Position ContextAssertion
		long posStart = kSession.getSessionClock().getCurrentTime();
		long posEnd = posStart + 5000;
		
		Position pos = new Position(person, new Area("WORK_AREA"), 
				new DefaultAnnotationData(posEnd, 1.0, new Date(posStart), new Date(posEnd)));
		
		// create a SittingLLA ContextAssertion
		long llaStart = posEnd + 5000;
		long llaEnd = llaStart + 5000;
		
		SittingLLA lla = new SittingLLA(person, new DefaultAnnotationData(llaEnd, 1.0, new Date(llaStart), new Date(llaEnd)));
		
		// insert both assertions in session
		eventTracker.insertDerivedEvent(pos);
		eventTracker.insertDerivedEvent(lla);
		
		// fire all rules
		int nrFired = kSession.fireAllRules();
		
		List<Match> activations = agendaEvListener.getMatchList();
		System.out.println(activations);
		
		// there should be 2 rules, the one that triggers the HLA insertion and the one that prints the HLA
		Assert.assertEquals("Number of rules fired different than expected.", 2, nrFired);
		
		// see if derived WorkingHLA exists in internal memory 
		Collection<FactHandle> hlaFacts = kSession.getEntryPoint("ExtendedWorkingHLAStream").getFactHandles();
		
		Assert.assertEquals("Number of derived HLA facts different than expected", 1, hlaFacts.size()); 
	}
	
	@Test
	public void testNotAnnBeforeOperator() {
		// setup KnowledgeBuilderConfiguration to add operator
		KnowledgeBuilderConfiguration builderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
		builderConf.setOption(EvaluatorOption.get("annOverlaps", new AnnOverlapsOperator.AnnOverlapsEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annOverlappedBy", new AnnOverlappedByOperator.AnnOverlappedByEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annHappensBefore", new AnnBeforeOperator.AnnBeforeEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annHappensAfter", new AnnAfterOperator.AnnAfterEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annIncludes", new AnnIncludesOperator.AnnIncludesEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annIntersects", new AnnIntersectsOperator.AnnIntersectsEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annStartsAfter", new AnnStartsAfterOperator.AnnStartsAfterEvaluatorDefinition()));
        
		// setup KieSession
		KieSession kSession = getKieSessionFromResources(builderConf, null, 
				"operator_test_rules/happensBeforeTest.drl",
				"casas_interwoven_constraints/PersonLocation_constraints.drl");
		EventTracker eventTracker = new EventTracker(kSession);
		TrackingAgendaEventListener agendaEvListener = new TrackingAgendaEventListener();
		kSession.addEventListener(agendaEvListener);
		
		// create a Person
		Person person = new Person("Alex");
		
		// create a Position ContextAssertion
		long posStart = kSession.getSessionClock().getCurrentTime();
		long posEnd = posStart + 5000;
		
		Position pos = new Position(person, new Area("WORK_AREA"), 
				new DefaultAnnotationData(posEnd, 1.0, new Date(posStart), new Date(posEnd)));
		
		// create a SittingLLA ContextAssertion
		long llaStart = posStart - 10000;
		long llaEnd = llaStart + 5000;
		
		SittingLLA lla = new SittingLLA(person, new DefaultAnnotationData(llaEnd, 1.0, new Date(llaStart), new Date(llaEnd)));
		
		// insert both assertions in session
		eventTracker.insertDerivedEvent(pos);
		eventTracker.insertDerivedEvent(lla);
		
		// fire all rules
		int nrFired = kSession.fireAllRules();
		
		List<Match> activations = agendaEvListener.getMatchList();
		System.out.println(activations);
		
		
		// there should be one rule, because only the one with the NOT operator should trigger
		Assert.assertEquals("Number of rules fired different than expected.", 1, nrFired);
		
		// see if derived WorkingHLA exists in internal memory 
		Collection<FactHandle> hlaFacts = kSession.getEntryPoint("ExtendedWorkingHLAStream").getFactHandles();
		
		Assert.assertEquals("Number of derived HLA facts different than expected", 0, hlaFacts.size()); 
	}

}
