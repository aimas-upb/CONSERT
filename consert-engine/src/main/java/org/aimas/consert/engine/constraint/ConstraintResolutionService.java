package org.aimas.consert.engine.constraint;

import org.aimas.consert.model.constraint.IGeneralConstraintViolation;
import org.aimas.consert.model.constraint.IUniquenessConstraintViolation;
import org.aimas.consert.model.constraint.IValueConstraintViolation;

public interface ConstraintResolutionService {

    ValueConflictDecision resolveConflict(IValueConstraintViolation violation);

    UniquenessConflictDecision resolveConflict(IUniquenessConstraintViolation violation);

    GeneralConflictDecision resolveConflict(IGeneralConstraintViolation violation);

}
