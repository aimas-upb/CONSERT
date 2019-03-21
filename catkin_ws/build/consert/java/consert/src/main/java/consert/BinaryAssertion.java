package consert;

public interface BinaryAssertion extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "consert/BinaryAssertion";
  static final java.lang.String _DEFINITION = "# A representation of a binary ContextAssertion from the CONSERT context model\n\n# The URI ID of this unary ContextAssertion instance\nstring id\n\n# The class type of this unary ContextEntity instance (e.g. cleans)\nstring type\n\n# The acquisition type of the ContextAssertion\nstring acquisitionType\n\n# The subject ContextEntity instance for which this assertion is applied (e.g. Alex - Person)\nEntityRole subject\n\n# The object ContextEntity instance for which this assertion is applied (e.g. Kitchen - Room)\nEntityRole object\n\n# The annotations of this ContextAssertion instance\nContextAnnotation[] annotations\n";
  java.lang.String getId();
  void setId(java.lang.String value);
  java.lang.String getType();
  void setType(java.lang.String value);
  java.lang.String getAcquisitionType();
  void setAcquisitionType(java.lang.String value);
  consert.EntityRole getSubject();
  void setSubject(consert.EntityRole value);
  consert.EntityRole getObject();
  void setObject(consert.EntityRole value);
  java.util.List<consert.ContextAnnotation> getAnnotations();
  void setAnnotations(java.util.List<consert.ContextAnnotation> value);
}
