package consert;

public interface ContextEntity extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "consert/ContextEntity";
  static final java.lang.String _DEFINITION = "# A representation of a ContextEntity from the CONSERT context model\n\n# The URI ID of this ContextEntity instance\nstring id\n\n# The class type of this ContextEntity instance (e.g. Person, Room, CookingActivity)\nstring type\n\n# Specify whether the entity encapsulates a literal or a concept\nbool isLiteral\n\n# The value of this ContextEntity instance (e.g. name of the person, name of the room, category+instance of the CookingActivity)\nstring value";
  java.lang.String getId();
  void setId(java.lang.String value);
  java.lang.String getType();
  void setType(java.lang.String value);
  boolean getIsLiteral();
  void setIsLiteral(boolean value);
  java.lang.String getValue();
  void setValue(java.lang.String value);
}
