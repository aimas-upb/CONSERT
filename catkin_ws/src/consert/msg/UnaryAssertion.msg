# A representation of a unary ContextAssertion from the CONSERT context model

# The URI ID of this unary ContextAssertion instance
string id

# The class type of this unary ContextEntity instance (e.g. Cooking)
string type

# The acquisition type of the ContextAssertion
string acquisitionType

# The ContextEntity instance for which this assertion is applied (e.g. Alex - Person)
EntityRole entity

# The annotations of this ContextAssertion instance
ContextAnnotation[] annotations