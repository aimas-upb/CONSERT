package consert;

public interface ContextAssertion extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "consert/ContextAssertion";
  static final java.lang.String _DEFINITION = "# A generic representation of a ContextAssertion from the CONSERT context model\n\n# The URI ID of this ContextAssertion instance\nstring id\n\n# The class type of this ContextAssertion instance\nstring type\n\n# Arity of the ContextAssertion: 1, 2 or 3 (for n-ary ContextAssertions)\nuint8 arity\n\n# The acquisition type of the ContextAssertion\nstring acquisitionType\n\n# The key-value dictionary of entities and their roles in this ContextAssertion instance\nEntityRole[] entities \n\n# The annotations of this ContextAssertion instance\nContextAnnotation[] annotations";
  java.lang.String getId();
  void setId(java.lang.String value);
  java.lang.String getType();
  void setType(java.lang.String value);
  byte getArity();
  void setArity(byte value);
  java.lang.String getAcquisitionType();
  void setAcquisitionType(java.lang.String value);
  java.util.List<consert.EntityRole> getEntities();
  void setEntities(java.util.List<consert.EntityRole> value);
  java.util.List<consert.ContextAnnotation> getAnnotations();
  void setAnnotations(java.util.List<consert.ContextAnnotation> value);
}
