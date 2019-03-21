# A generic representation of a ContextAssertion from the CONSERT context model

# The URI ID of this ContextAssertion instance
string id

# The class type of this ContextAssertion instance
string type

# Arity of the ContextAssertion: 1, 2 or 3 (for n-ary ContextAssertions)
uint8 arity

# The acquisition type of the ContextAssertion
string acquisitionType

# The key-value dictionary of entities and their roles in this ContextAssertion instance
EntityRole[] entities 

# The annotations of this ContextAssertion instance
ContextAnnotation[] annotations