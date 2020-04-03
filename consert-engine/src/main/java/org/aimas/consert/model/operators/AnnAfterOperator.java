package org.aimas.consert.model.operators;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.aimas.consert.model.annotations.DefaultAnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.utils.TestSetup;
import org.drools.core.base.ValueType;
import org.drools.core.base.evaluators.EvaluatorDefinition;
import org.drools.core.base.evaluators.Operator;
import org.drools.core.base.evaluators.PointInTimeEvaluator;
import org.drools.core.base.evaluators.TimeIntervalParser;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.VariableRestriction;
import org.drools.core.spi.Evaluator;
import org.drools.core.time.Interval;


public class AnnAfterOperator extends TestSetup {

    public static class AnnAfterEvaluatorDefinition implements EvaluatorDefinition {

        protected static final String afterOp = "annHappensAfter";

        public static Operator HAPPENS_AFTER;
        public static Operator NOT_HAPPENS_AFTER;

        private static String[] SUPPORTED_IDS;

        private Map<String, AnnAfterEvaluator> cache = Collections.emptyMap();

        static {
            if ( Operator.determineOperator( afterOp, false ) == null ) {
                HAPPENS_AFTER = Operator.addOperatorToRegistry( afterOp, false );
                NOT_HAPPENS_AFTER = Operator.addOperatorToRegistry( afterOp, true );
                SUPPORTED_IDS = new String[]{afterOp};
            }
        }

        @SuppressWarnings("unchecked")
        public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
            cache = (Map<String, AnnAfterEvaluator>) in.readObject();
        }

        public void writeExternal( ObjectOutput out ) throws IOException {
            out.writeObject( cache );
        }

        /**
         * @inheridDoc
         */
        public Evaluator getEvaluator( ValueType type,
                                       Operator operator ) {
            return this.getEvaluator( type,
                    operator.getOperatorString(),
                    operator.isNegated(),
                    null,
                    Target.HANDLE,
                    Target.HANDLE );
        }

        /**
         * @inheridDoc
         */
        public Evaluator getEvaluator( ValueType type,
                                       Operator operator,
                                       String parameterText ) {
            return this.getEvaluator( type,
                    operator.getOperatorString(),
                    operator.isNegated(),
                    parameterText,
                    Target.HANDLE,
                    Target.HANDLE );
        }

        /**
         * @inheritDoc
         */
        public Evaluator getEvaluator( final ValueType type,
                                       final String operatorId,
                                       final boolean isNegated,
                                       final String parameterText ) {
            return this.getEvaluator( type,
                    operatorId,
                    isNegated,
                    parameterText,
                    Target.HANDLE,
                    Target.HANDLE );

        }

        /**
         * @inheritDoc
         */
        public Evaluator getEvaluator( final ValueType type,
                                       final String operatorId,
                                       final boolean isNegated,
                                       final String parameterText,
                                       final Target left,
                                       final Target right ) {
            if ( this.cache == Collections.EMPTY_MAP ) {
                this.cache = new HashMap<String, AnnAfterEvaluator>();
            }
            String key = left + ":" + right + ":" + isNegated + ":" + parameterText;
            AnnAfterEvaluator eval = this.cache.get( key );
            if ( eval == null ) {
                long[] params = TimeIntervalParser.parse( parameterText );
                eval = new AnnAfterEvaluator( type,
                        isNegated,
                        params,
                        parameterText,
                        left == Target.FACT,
                        right == Target.FACT );
                this.cache.put( key,
                        eval );
            }
            return eval;
        }

        /**
         * @inheritDoc
         */
        public String[] getEvaluatorIds() {
            return SUPPORTED_IDS;
        }

        /**
         * @inheritDoc
         */
        public boolean isNegatable() {
            return true;
        }

        /**
         * @inheritDoc
         */
        public Target getTarget() {
            return Target.BOTH;
        }

        /**
         * @inheritDoc
         */
        public boolean supportsType( ValueType type ) {
            // supports all types, since it operates over fact handles
            // Note: should we change this interface to allow checking of event classes only?
            return true;
        }
    }


    public static class AnnAfterEvaluator extends PointInTimeEvaluator {
        private static final long serialVersionUID = 2l;

        public AnnAfterEvaluator() {
        }

        public AnnAfterEvaluator( final ValueType type,
                                  final boolean isNegated,
                                  final long[] parameters,
                                  final String paramText,
                                  final boolean unwrapLeft,
                                  final boolean unwrapRight ) {
            super( type,
                    isNegated ? AnnAfterEvaluatorDefinition.NOT_HAPPENS_AFTER : AnnAfterEvaluatorDefinition.HAPPENS_AFTER,
                    parameters,
                    paramText,
                    unwrapLeft,
                    unwrapRight );
        }

        @Override
        public Interval getInterval() {
            long init = this.initRange;
            long end = this.finalRange;
            if ( this.getOperator().isNegated() ) {
                if ( init == Interval.MIN && end != Interval.MAX ) {
                    init = finalRange + 1;
                    end = Interval.MAX;
                } else if ( init != Interval.MIN && end == Interval.MAX ) {
                    init = Interval.MIN;
                    end = initRange - 1;
                } else if ( init == Interval.MIN ) {
                    init = 0;
                    end = -1;
                } else {
                    init = Interval.MIN;
                    end = Interval.MAX;
                }
            }
            return new Interval( init, end );
        }



        @Override
        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableRestriction.VariableContextEntry context,
                                          final InternalFactHandle right) {
            if ( context.leftNull ||
                    context.extractor.isNullValue( workingMemory, right.getObject() ) ) {
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

            DefaultAnnotationData ann = (DefaultAnnotationData)cachedAssertion.getAnnotations();
            long leftTS = ann.getEndTime() != null ? ann.getEndTime().getTime() : Long.MAX_VALUE;

            //long leftTS = ((VariableRestriction.TimestampedContextEntry)context).timestamp;
            long rightTS = context.getFieldExtractor().isSelfReference() ?
                    getRightTimestamp(right) :
                    context.getFieldExtractor().getLongValue( workingMemory, right.getObject() );

            return evaluate(rightTS, leftTS);
        }

        @Override
        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableRestriction.VariableContextEntry context,
                                           final InternalFactHandle left) {
            if ( context.rightNull ||
                    context.declaration.getExtractor().isNullValue( workingMemory, left.getObject() )) {
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

            DefaultAnnotationData ann = (DefaultAnnotationData)cachedAssertion.getAnnotations();
            long rightTS = ann.getStartTime() != null ? ann.getStartTime().getTime() : 0;

            //long rightTS = ((VariableRestriction.TimestampedContextEntry)context).timestamp;
            long leftTS = context.declaration.getExtractor().isSelfReference() ?
                    getLeftTimestamp( left ) :
                    context.declaration.getExtractor().getLongValue( workingMemory, left.getObject() );

            return evaluate(rightTS, leftTS);
        }


        @Override
        protected boolean evaluate( long rightTS, long leftTS ) {
            long dist = rightTS - leftTS;
            return this.getOperator().isNegated() ^ ( dist >= this.initRange && dist <= this.finalRange );
        }


        @Override
        protected long getLeftTimestamp( InternalFactHandle handle ) {
            ContextAssertion leftAssertion = (ContextAssertion) handle.getObject();

            // Cast to DefaultAnnotationData for this test
            // when merging with updated annotation processing, check will have to be made whether temporal annotation exists
            // if not, timestamp annotation will be checked, if not fact handle endTimestamp (i.e. Drools processing) will be returned
            DefaultAnnotationData ann = (DefaultAnnotationData)leftAssertion.getAnnotations();
            return ann.getEndTime() != null ? ann.getEndTime().getTime() : Long.MAX_VALUE;

            //return ( (EventFactHandle) handle ).getEndTimestamp();
        }

        @Override
        protected long getRightTimestamp( InternalFactHandle handle ) {
            ContextAssertion rightAssertion = (ContextAssertion) handle.getObject();

            // Cast to DefaultAnnotationData for this test
            // when merging with updated annotation processing, check will have to be made whether temporal annotation exists
            // if not, timestamp annotation will be checked, if not fact handle startTimestamp (i.e. Drools processing) will be returned
            DefaultAnnotationData ann = (DefaultAnnotationData)rightAssertion.getAnnotations();
            return ann.getStartTime() != null ? ann.getStartTime().getTime() : 0;
            //return ( (EventFactHandle) handle ).getStartTimestamp();
        }
    }

}
