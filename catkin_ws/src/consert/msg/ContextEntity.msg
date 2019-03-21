# A representation of a ContextEntity from the CONSERT context model

# The URI ID of this ContextEntity instance
string id

# The class type of this ContextEntity instance (e.g. Person, Room, CookingActivity)
string type

# Specify whether the entity encapsulates a literal or a concept
bool isLiteral

# The value of this ContextEntity instance (e.g. name of the person, name of the room, category+instance of the CookingActivity)
string value