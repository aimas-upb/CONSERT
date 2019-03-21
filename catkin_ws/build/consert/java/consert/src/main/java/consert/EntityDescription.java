package consert;

public interface EntityDescription extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "consert/EntityDescription";
  static final java.lang.String _DEFINITION = "# A representation of a Context from the CONSERT context model\n\n# The URI ID of this EntityDescription instance\nstring id\n\n# The class type of this EntityDescription instance (e.g. includes - one room includes another, hasAge)\nstring type\n\n# The subject ContextEntity instance of this EntityDescription\nContextEntity subject\n\n# The object ContextEntity instance of this EntityDescription\nContextEntity object";
  java.lang.String getId();
  void setId(java.lang.String value);
  java.lang.String getType();
  void setType(java.lang.String value);
  consert.ContextEntity getSubject();
  void setSubject(consert.ContextEntity value);
  consert.ContextEntity getObject();
  void setObject(consert.ContextEntity value);
}
