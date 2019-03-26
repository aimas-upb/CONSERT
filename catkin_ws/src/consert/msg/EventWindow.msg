# A representation of an EventWindow that supports a possible ContextAssertion

# The ContextAssertionContent that the EventWindow is thought to support
ContextAssertionContent possibleContextAssertion

# The list of ContextAssertions in the EventWindow that are thought to support the possibleAssertion
ContextAssertion[] supportingAssertions

# The event window start timestamp, equal to the lastUpdate timestamp of the first ContextAssertion in the window
float64 start_timestamp

# The event window stop timestamp, equal to the lastUpdate timestamp of the last ContextAssertion in the window
float64 end_timestamp