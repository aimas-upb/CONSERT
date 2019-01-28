package org.aimas.consert.unittest;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aimas.consert.engine.core.EventTracker;
import org.aimas.consert.model.annotations.DefaultAnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.tests.hla.assertions.Position;
import org.aimas.consert.tests.hla.assertions.SittingLLA;
import org.aimas.consert.tests.hla.entities.Area;
import org.aimas.consert.tests.hla.entities.Person;
import org.aimas.consert.utils.TestSetup;
import org.drools.core.base.BaseEvaluator;
import org.drools.core.base.ValueType;
import org.drools.core.base.evaluators.EvaluatorDefinition;
import org.drools.core.base.evaluators.Operator;
import org.drools.core.base.evaluators.TimeIntervalParser;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.VariableRestriction.VariableContextEntry;
import org.drools.core.spi.Evaluator;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.time.Interval;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.EvaluatorOption;

public class AnnIntersectsOperatorTest extends TestSetup {

    public static class AnnIntersectsEvaluatorDefinition implements EvaluatorDefinition {

        public static final String          intersectsOp = "annIntersects";

        public static Operator              INTERSECTS;

        public static Operator              INTERSECTS_NOT;

        private static String[]             SUPPORTED_IDS;

        private Map<String, AnnIntersectsEvaluator> cache       = Collections.emptyMap();

        { init(); }

        static void init() {
            if ( Operator.determineOperator( intersectsOp, false ) == null ) {
                INTERSECTS = Operator.addOperatorToRegistry( intersectsOp, false );
                INTERSECTS_NOT = Operator.addOperatorToRegistry( intersectsOp, true );
                SUPPORTED_IDS = new String[] { intersectsOp };
            }
        }

        @SuppressWarnings("unchecked")
        public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {
            cache = (Map<String, AnnIntersectsEvaluator>) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( cache );
        }

        /**
         * @inheridDoc
         */
        public Evaluator getEvaluator(ValueType type, Operator operator) {
            return this.getEvaluator( type, operator.getOperatorString(), operator.isNegated(), null );
        }

        public Evaluator getEvaluator(ValueType type, Operator operator, String parameterText) {
            return this.getEvaluator( type, operator.getOperatorString(), operator.isNegated(), parameterText );
        }

        public Evaluator getEvaluator(final ValueType type, final String operatorId, final boolean isNegated, final String parameterText) {
            return this.getEvaluator( type, operatorId, isNegated, parameterText, Target.HANDLE, Target.HANDLE );
        }

        public Evaluator getEvaluator(final ValueType type, final String operatorId, final boolean isNegated, final String parameterText,
                                      final Target left, final Target right ) {
            if ( this.cache == Collections.EMPTY_MAP ) {
                this.cache = new HashMap<String, AnnIntersectsEvaluator>();
            }
            String key = isNegated + ":" + parameterText;
            AnnIntersectsEvaluator eval = this.cache.get( key );
            if ( eval == null ) {
                long[] params = TimeIntervalParser.parse( parameterText );
                eval = new AnnIntersectsEvaluator( type,
                        isNegated,
                        params,
                        parameterText );
                this.cache.put( key,
                        eval );
            }
            return eval;
        }

        public String[] getEvaluatorIds() {
            return SUPPORTED_IDS;
        }

        public boolean isNegatable() {
            return true;
        }

        public Target getTarget() {
            return Target.HANDLE;
        }

        public boolean supportsType(ValueType type) {
            // supports all types, since it operates over fact handles
            // Note: should we change this interface to allow checking of event classes only?
            return true;
        }
    }


    /**
     * Implements the 'intersects' evaluator itself
     */
    public static class AnnIntersectsEvaluator extends BaseEvaluator {

        private long              minDev, maxDev;
        private String            paramText;

        {
            AnnIntersectsEvaluatorDefinition.init();
        }


        private static class TimestampPair {
            long start;
            long end;

            public TimestampPair(long start, long end) {
                this.start = start;
                this.end = end;
            }

            public long getStart() {
                return start;
            }

            public long getEnd() {
                return end;
            }
        }

        public AnnIntersectsEvaluator() {
        }

        public AnnIntersectsEvaluator(final ValueType type, final boolean isNegated, final long[] parameters, final String paramText) {
            super( type, isNegated ? AnnIntersectsEvaluatorDefinition.INTERSECTS_NOT : AnnIntersectsEvaluatorDefinition.INTERSECTS );
            this.paramText = paramText;
            this.setParameters( parameters );
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            super.readExternal( in );
            minDev = in.readLong();
            maxDev = in.readLong();
            paramText = (String) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal( out );
            out.writeLong( minDev );
            out.writeLong( maxDev );
            out.writeObject( paramText );
        }

        @Override
        public boolean isTemporal() {
            return true;
        }

        @Override
        public Interval getInterval() {
            if ( this.getOperator().isNegated() ) {
                return new Interval( Interval.MIN, Interval.MAX );
            }
            return new Interval( Interval.MIN, 0 );
        }

        @Override
        public boolean evaluate(InternalWorkingMemory workingMemory, final InternalReadAccessor extractor,
                                final InternalFactHandle object1, final FieldValue object2) {
            throw new RuntimeException( "The 'intersects' operator can only be used to compare one event to another, and never to compare to literal constraints." );
        }

        @Override
        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory, final VariableContextEntry context, final InternalFactHandle left) {
            if ( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left.getObject() )) {
                return false;
            }

            // get the ContextAssertion object and cast its annotations to DefaultAnnotationData for this test
            ContextAssertion cachedAssertion = (ContextAssertion)context.getObject();
            if (cachedAssertion == null) {
                cachedAssertion = (ContextAssertion)context.declaration.getExtractor().getValue( workingMemory,
                        context.getTuple().getObject( context.declaration ) );

                if (cachedAssertion == null) {
                    return false;
                }
            }

            TimestampPair interval1 = getTemporalInterval(cachedAssertion);
            TimestampPair interval2 = getTemporalInterval((EventFactHandle) left);

            long distStart = interval1.end - interval2.start;
            long distEnd = interval2.end - interval1.start;

            return this.getOperator().isNegated() ^ (distStart >= this.minDev && distStart <= this.maxDev
                    && distEnd >= this.minDev && distEnd <= this.maxDev);
        }


        @Override
        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory, final VariableContextEntry context, final InternalFactHandle right) {
            if ( context.leftNull || context.extractor.isNullValue( workingMemory, right.getObject() ) ) {
                return false;
            }

            // get the ContextAssertion object and cast its annotations to DefaultAnnotationData for this test
            ContextAssertion cachedAssertion = (ContextAssertion)context.getObject();
            if (cachedAssertion == null) {
                cachedAssertion = (ContextAssertion)context.declaration.getExtractor().getValue( workingMemory,
                        context.getTuple().getObject( context.declaration ) );

                if (cachedAssertion == null) {
                    return false;
                }
            }

            TimestampPair interval1 = getTemporalInterval((EventFactHandle) right);
            TimestampPair interval2 = getTemporalInterval(cachedAssertion);

            long distStart = interval1.end - interval2.start;
            long distEnd = interval2.end - interval1.start;

            return this.getOperator().isNegated() ^ (distStart >= this.minDev && distStart <= this.maxDev
                    && distEnd >= this.minDev && distEnd <= this.maxDev);
        }


        @Override
        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final InternalFactHandle handle1,
                                final InternalReadAccessor extractor2,
                                final InternalFactHandle handle2) {
            if ( extractor1.isNullValue( workingMemory, handle1.getObject() ) ||
                    extractor2.isNullValue( workingMemory, handle2.getObject() ) ) {
                return false;
            }

            // if both events are non-null, retrieve the TimestampPairs
            TimestampPair interval1 = getTemporalInterval((EventFactHandle)handle1);
            TimestampPair interval2 = getTemporalInterval((EventFactHandle)handle2);

            long distStart = interval1.end - interval2.start;
            long distEnd = interval2.end - interval1.start;

            return this.getOperator().isNegated() ^ (distStart >= this.minDev && distStart <= this.maxDev
                    && distEnd >= this.minDev && distEnd <= this.maxDev);
        }


        private TimestampPair getTemporalInterval(ContextAssertion assertion) {
            DefaultAnnotationData ann = (DefaultAnnotationData)assertion.getAnnotations();
            return new TimestampPair(ann.getStartTime().getTime(), ann.getEndTime().getTime());
        }

        private TimestampPair getTemporalInterval(EventFactHandle handle) {
            if (handle.getObject() instanceof ContextAssertion) {
                ContextAssertion assertion = (ContextAssertion)handle.getObject();
                DefaultAnnotationData ann = (DefaultAnnotationData)assertion.getAnnotations();

                return new TimestampPair(ann.getStartTime().getTime(), ann.getEndTime().getTime());
            }
            else {
                return new TimestampPair(handle.getStartTimestamp(), handle.getEndTimestamp());
            }
        }


        public String toString() {
            return "annIntersects[" + minDev + ", " + maxDev  + "]";
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = super.hashCode();
            result = PRIME * result + (int) (maxDev ^ (maxDev >>> 32));
            result = PRIME * result + (int) (minDev ^ (minDev >>> 32));
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( !super.equals( obj ) ) return false;
            if ( getClass() != obj.getClass() ) return false;
            final AnnIntersectsEvaluator other = (AnnIntersectsEvaluator) obj;
            return maxDev == other.maxDev && minDev == other.minDev ;
        }

        /**
         * This methods sets the parameters appropriately.
         *
         * @param parameters
         */
        private void setParameters(long[] parameters) {
            if ( parameters == null || parameters.length == 0 ) {
                // open bounded range
                this.minDev = 0;
                this.maxDev = Long.MAX_VALUE;
            } else if ( parameters.length == 1 ) {
                // open bounded ranges
                this.minDev = 0;
                this.maxDev = parameters[0];
            } else if ( parameters.length == 2 ) {
                // open bounded ranges
                this.minDev = parameters[0];
                this.maxDev = parameters[1];
            } else {
                throw new RuntimeException( "[Intersects Evaluator]: Not possible to use " + parameters.length + " parameters: '" + paramText + "'" );
            }
        }

    }

    @Test
    public void testAnnIntersectsOperator() {
        // setup KnowledgeBuilderConfiguration to add operator
        KnowledgeBuilderConfiguration builderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        builderConf.setOption(EvaluatorOption.get("annIntersects", new AnnIntersectsEvaluatorDefinition()));

        // setup KieSession
        KieSession kSession = getKieSessionFromResources(builderConf, null, "operator_test_rules/intersectsTest.drl" );
        EventTracker eventTracker = new EventTracker(kSession);
        TrackingAgendaEventListener agendaEvListener = new TrackingAgendaEventListener();
        kSession.addEventListener(agendaEvListener);

        // create a Person
        Person person = new Person("Alex");

        // create a Position ContextAssertion
        long posStart = kSession.getSessionClock().getCurrentTime() - 20000;
        long posEnd = posStart + 20000;

        Position pos = new Position(person, new Area("WORK_AREA"),
                new DefaultAnnotationData(posEnd, 1.0, new Date(posStart), new Date(posEnd)));

        // create a SittingLLA ContextAssertion
        long llaStart = posStart + 10000;
        long llaEnd = posEnd+10000;

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
    public void testNotAnnIntersectsOperator() {
        // setup KnowledgeBuilderConfiguration to add operator
        KnowledgeBuilderConfiguration builderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        builderConf.setOption(EvaluatorOption.get("annIntersects", new AnnIntersectsEvaluatorDefinition()));

        // setup KieSession
        KieSession kSession = getKieSessionFromResources(builderConf, null, "operator_test_rules/intersectsTest.drl" );
        EventTracker eventTracker = new EventTracker(kSession);
        TrackingAgendaEventListener agendaEvListener = new TrackingAgendaEventListener();
        kSession.addEventListener(agendaEvListener);

        // create a Person
        Person person = new Person("Alex");

        // create a Position ContextAssertion
        long posStart = kSession.getSessionClock().getCurrentTime() - 5000;
        long posEnd = posStart + 5000;

        Position pos = new Position(person, new Area("WORK_AREA"),
                new DefaultAnnotationData(posEnd, 1.0, new Date(posStart), new Date(posEnd)));

        // create a SittingLLA ContextAssertion
        long llaStart = posEnd + 5000;
        long llaEnd = posEnd + 10000;

        SittingLLA lla = new SittingLLA(person, new DefaultAnnotationData(llaEnd, 1.0, new Date(llaStart), new Date(llaEnd)));

        // insert both assertions in session
        eventTracker.insertDerivedEvent(pos);
        eventTracker.insertDerivedEvent(lla);

        // fire all rules
        int nrFired = kSession.fireAllRules();

        List<Match> activations = agendaEvListener.getMatchList();
        System.out.println(activations);

        // there should be 2 rules, the one that triggers the HLA insertion and the one that prints the HLA
        Assert.assertEquals("Number of rules fired different than expected.", 1, nrFired);

        // see if derived WorkingHLA exists in internal memory
        Collection<FactHandle> hlaFacts = kSession.getEntryPoint("ExtendedWorkingHLAStream").getFactHandles();

        Assert.assertEquals("Number of derived HLA facts different than expected", 0, hlaFacts.size());
    }
}
