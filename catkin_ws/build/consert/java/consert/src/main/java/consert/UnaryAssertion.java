package consert;

public interface UnaryAssertion extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "consert/UnaryAssertion";
  static final java.lang.String _DEFINITION = "# A representation of a unary ContextAssertion from the CONSERT context model\n\n# The URI ID of this unary ContextAssertion instance\nstring id\n\n# The class type of this unary ContextEntity instance (e.g. Cooking)\nstring type\n\n# The acquisition type of the ContextAssertion\nstring acquisitionType\n\n# The ContextEntity instance for which this assertion is applied (e.g. Alex - Person)\nEntityRole entity\n\n# The annotations of this ContextAssertion instance\nContextAnnotation[] annotations";
  java.lang.String getId();
  void setId(java.lang.String value);
  java.lang.String getType();
  void setType(java.lang.String value);
  java.lang.String getAcquisitionType();
  void setAcquisitionType(java.lang.String value);
  consert.EntityRole getEntity();
  void setEntity(consert.EntityRole value);
  java.util.List<consert.ContextAnnotation> getAnnotations();
  void setAnnotations(java.util.List<consert.ContextAnnotation> value);
}
