package org.aimas.consert.tests.casas.utils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.aimas.consert.model.annotations.DefaultAnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
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

public class AnnIncludesOperator extends TestSetup {

    public static class AnnIncludesEvaluatorDefinition implements EvaluatorDefinition {

        public static final String          includesOp = "annIncludes";

        public static Operator              INCLUDES;

        public static Operator              INCLUDES_NOT;

        private static String[]             SUPPORTED_IDS;

        private Map<String, AnnIncludesEvaluator> cache       = Collections.emptyMap();

        { init(); }

        static void init() {
            if ( Operator.determineOperator( includesOp, false ) == null ) {
                INCLUDES = Operator.addOperatorToRegistry( includesOp, false );
                INCLUDES_NOT = Operator.addOperatorToRegistry( includesOp, true );
                SUPPORTED_IDS = new String[] { includesOp };
            }
        }

        @SuppressWarnings("unchecked")
        public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {
            cache = (Map<String, AnnIncludesEvaluator>) in.readObject();
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
                this.cache = new HashMap<String, AnnIncludesEvaluator>();
            }
            String key = isNegated + ":" + parameterText;
            AnnIncludesEvaluator eval = this.cache.get( key );
            if ( eval == null ) {
                long[] params = TimeIntervalParser.parse( parameterText );
                eval = new AnnIncludesEvaluator( type,
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
     * Implements the 'includes' evaluator itself
     */
    public static class AnnIncludesEvaluator extends BaseEvaluator {

        private long              startMinDev, startMaxDev;
        private long              endMinDev, endMaxDev;
        private String            paramText;

        {
            AnnIncludesEvaluatorDefinition.init();
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

        public AnnIncludesEvaluator() {
        }

        public AnnIncludesEvaluator(final ValueType type, final boolean isNegated, final long[] parameters, final String paramText) {
            super( type, isNegated ? AnnIncludesEvaluatorDefinition.INCLUDES_NOT : AnnIncludesEvaluatorDefinition.INCLUDES );
            this.paramText = paramText;
            this.setParameters( parameters );
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            super.readExternal( in );
            startMinDev = in.readLong();
            startMaxDev = in.readLong();
            endMinDev = in.readLong();
            endMaxDev = in.readLong();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal( out );
            out.writeLong( startMinDev );
            out.writeLong( startMaxDev );
            out.writeLong( endMinDev );
            out.writeLong( endMaxDev );
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
            throw new RuntimeException( "The 'includes' operator can only be used to compare one event to another, and never to compare to literal constraints." );
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

            long distStart = interval2.start - interval1.start;
            long distEnd = interval1.end - interval2.end;

            return this.getOperator().isNegated() ^ (distStart >= this.startMinDev && distStart <= this.startMaxDev
                    && distEnd >= this.endMinDev && distEnd <= this.endMaxDev);
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

            long distStart = interval2.start - interval1.start;
            long distEnd = interval1.end - interval2.end;

            return this.getOperator().isNegated() ^ (distStart >= this.startMinDev && distStart <= this.startMaxDev
                    && distEnd >= this.endMinDev && distEnd <= this.endMaxDev);
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

            long distStart = interval2.start - interval1.start;
            long distEnd = interval1.end - interval2.end;

            return this.getOperator().isNegated() ^ (distStart >= this.startMinDev
                    && distStart <= this.startMaxDev && distEnd >= this.endMinDev && distEnd <= this.endMaxDev);
        }


        private TimestampPair getTemporalInterval(ContextAssertion assertion) {
            DefaultAnnotationData ann = (DefaultAnnotationData)assertion.getAnnotations();
            long start = 0;
            long end = Long.MAX_VALUE;
            
            if (ann.getStartTime() != null)
            	start = ann.getStartTime().getTime();
            
            if (ann.getEndTime() != null)
            	end = ann.getEndTime().getTime();
            
            return new TimestampPair(start, end);
        }

        private TimestampPair getTemporalInterval(EventFactHandle handle) {
            if (handle.getObject() instanceof ContextAssertion) {
                ContextAssertion assertion = (ContextAssertion)handle.getObject();
                return getTemporalInterval(assertion);
            }
            else {
                return new TimestampPair(handle.getStartTimestamp(), handle.getEndTimestamp());
            }
        }


        public String toString() {
            return "annIncludes[" + startMinDev + ", " + startMaxDev + ", " + endMinDev + ", " + endMaxDev + "]";
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = super.hashCode();
            result = PRIME * result + (int) (endMaxDev ^ (endMaxDev >>> 32));
            result = PRIME * result + (int) (endMinDev ^ (endMinDev >>> 32));
            result = PRIME * result + (int) (startMaxDev ^ (startMaxDev >>> 32));
            result = PRIME * result + (int) (startMinDev ^ (startMinDev >>> 32));
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
            final AnnIncludesEvaluator other = (AnnIncludesEvaluator) obj;
            return endMaxDev == other.endMaxDev && endMinDev == other.endMinDev && startMaxDev == other.startMaxDev && startMinDev == other.startMinDev;
        }

        /**
         * This methods sets the parameters appropriately.
         *
         * @param parameters
         */
        private void setParameters(long[] parameters) {
            if ( parameters == null || parameters.length == 0 ) {
                // open bounded range
                this.startMinDev = 0;
                this.startMaxDev = Long.MAX_VALUE;
                this.endMinDev = 0;
                this.endMaxDev = Long.MAX_VALUE;
            } else if ( parameters.length == 1 ) {
                // open bounded ranges
                this.startMinDev = 0;
                this.startMaxDev = parameters[0];
                this.endMinDev = 0;
                this.endMaxDev = parameters[0];
            } else if ( parameters.length == 2 ) {
                // open bounded ranges
                this.startMinDev = parameters[0];
                this.startMaxDev = parameters[1];
                this.endMinDev = parameters[0];
                this.endMaxDev = parameters[1];
            } else if ( parameters.length == 4 ) {
                // open bounded ranges
                this.startMinDev = parameters[0];
                this.startMaxDev = parameters[1];
                this.endMinDev = parameters[2];
                this.endMaxDev = parameters[3];
            } else {
                throw new RuntimeException( "[During Evaluator]: Not possible to use " + parameters.length + " parameters: '" + paramText + "'" );
            }
        }

    }

}
