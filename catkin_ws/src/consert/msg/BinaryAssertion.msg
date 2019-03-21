# A representation of a binary ContextAssertion from the CONSERT context model

# The URI ID of this unary ContextAssertion instance
string id

# The class type of this unary ContextEntity instance (e.g. cleans)
string type

# The acquisition type of the ContextAssertion
string acquisitionType

# The subject ContextEntity instance for which this assertion is applied (e.g. Alex - Person)
EntityRole subject

# The object ContextEntity instance for which this assertion is applied (e.g. Kitchen - Room)
EntityRole object

# The annotations of this ContextAssertion instance
ContextAnnotation[] annotations
