package consert;

public interface ContextAnnotation extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "consert/ContextAnnotation";
  static final java.lang.String _DEFINITION = "# A representation of a ContextAnnotation instance from the CONSERT context model\n\n# The URI ID of this unary ContextAnnotation instance\nstring id\n\n# The class type of this ContextAnnotation instance (e.g. Timestamp, ValdityInterval, Certainty)\nstring type\n\n# A string serialized value of the ContextAnnotation instance\nstring value";
  java.lang.String getId();
  void setId(java.lang.String value);
  java.lang.String getType();
  void setType(java.lang.String value);
  java.lang.String getValue();
  void setValue(java.lang.String value);
}
