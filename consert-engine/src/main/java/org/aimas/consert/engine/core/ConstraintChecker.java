package org.aimas.consert.engine.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.aimas.consert.model.constraint.IConstraintViolation;
import org.aimas.consert.model.constraint.IGeneralConstraintViolation;
import org.aimas.consert.model.constraint.IUniquenessConstraintViolation;
import org.aimas.consert.model.constraint.IValueConstraintViolation;
import org.aimas.consert.model.constraint.UniquenessConstraintViolation;
import org.aimas.consert.model.content.ContextAssertion;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.kie.api.definition.rule.Query;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

public class ConstraintChecker {
    private static final String CONSTRAINT_TYPE = "constraintType";
    private static final String ASSERTION_TYPE = "assertionType";

    public static class ConstraintResult {
        ContextAssertion evaluatedAssertion;

        List<IValueConstraintViolation> valueViolations = new LinkedList<>();
        List<IUniquenessConstraintViolation> uniquenessViolations = new LinkedList<>();
        List<IGeneralConstraintViolation> generalViolations = new LinkedList<>();

        ConstraintResult(ContextAssertion evaluatedAssertion) {
            this.evaluatedAssertion = evaluatedAssertion;
        }

       public boolean isClear() {
            return valueViolations.isEmpty() && uniquenessViolations.isEmpty() && generalViolations.isEmpty();
        }

        public void addViolation(IValueConstraintViolation violation) {
            valueViolations.add(violation);
        }

        public void setUniquenessViolation(IUniquenessConstraintViolation violation) {
            if (uniquenessViolations.isEmpty()) {
                uniquenessViolations.add(violation);
            }
        }

        public void addViolation(IGeneralConstraintViolation violation) {
            generalViolations.add(violation);
        }

        public List<IValueConstraintViolation> getValueViolations() {
            return valueViolations;
        }

        // List<IUniquenessConstraintViolation> getUniquenessViolations() { return uniquenessViolations; }
        public IUniquenessConstraintViolation getUniquenessViolation() {
            if (uniquenessViolations.isEmpty())
                return null;

            return uniquenessViolations.get(0);
        }


        public List<IGeneralConstraintViolation> getGeneralViolations() { return generalViolations; }

        public List<IConstraintViolation> getAllViolations() {

            List<IConstraintViolation> allViolations = new LinkedList<>();
            if (!isClear()) {
                allViolations.addAll(valueViolations);
                allViolations.addAll(uniquenessViolations);
                allViolations.addAll(generalViolations);
            }

            return allViolations;
        }

        public boolean hasValueViolations() {
            return !valueViolations.isEmpty();
        }

        public boolean hasUniquenessViolations() {
            return !uniquenessViolations.isEmpty();
        }

        public boolean hasGeneralViolations() {
            return !generalViolations.isEmpty();
        }

        public String toString() {
            String res = "";
            res += "==== Value Violations ====\n";
            res += "" + valueViolations + "\n\n";

            res += "==== Uniqueness Violations ====\n";
            res += "" + uniquenessViolations + "\n\n";

            res += "==== General Violations ====\n";
            res += "" + generalViolations + "\n";

            return res;
        }
    }
    
    private KieSession kSession;

    public ConstraintChecker(EventTracker eventTracker) {
        this.kSession = eventTracker.getKnowledgeSession();
    }


    public ConstraintResult check(ContextAssertion contextAssertion) {
//        KieServices ks = KieServices.Factory.get();
//        KieRepository kr = ks.getRepository();
//        KieFileSystem kfs = ks.newKieFileSystem();
//        kfs.write("src/main/resources/my/rules/therule.drl", "The source code of the rule");

        Collection<Query> allConstraintQueries = kSession.getKieBase()
                .getKiePackage("org.aimas.consert.casas_interwoven_constraints").getQueries();


        List<Query> constraintQueries = allConstraintQueries.stream()
                .filter(query -> query.getMetaData().get("assertionType").equals(contextAssertion.getClass().getSimpleName()))
                .collect(Collectors.toList());

        // create the result
        ConstraintResult result = new ConstraintResult(contextAssertion);

        if (constraintQueries == null || constraintQueries.isEmpty()) {
            return result;
        }

        System.out.println("[CONSTRAINT CHECKER] Applicable constraint queries found for ContextAssertion: "
                + contextAssertion);

        for (Query q : constraintQueries) {
            String conditionType = q.getMetaData().get("conditionType").toString();
            QueryResults constraintResults = ((StatefulKnowledgeSessionImpl)kSession).getQueryResultsFromRHS(q.getName(), contextAssertion);


            System.out.println("QUERY RESULTS RETRIEVED");

            if (constraintResults.size() != 0) {
                // We make the assumption that the newAssertion can only be in conflict with a single
                // existing assertion. This assumption DEFINITELY holds true for Value and Uniqueness Constraints.
                // For general constraints, we either introduce a different management procedure, or we ensure
                // from the query formulation that there will only be a single match of the output variables (e.g. by
                // using the "latest instance" criteria)
                QueryResultsRow res = constraintResults.iterator().next();
                ContextAssertion existingAssertion = (ContextAssertion)res.get("existingAssertion");

                UniquenessConstraintViolation ucv = new UniquenessConstraintViolation(
                        q.getName(), conditionType, existingAssertion, contextAssertion);
                result.setUniquenessViolation(ucv);
            }
        }

        return result;
    }
}
