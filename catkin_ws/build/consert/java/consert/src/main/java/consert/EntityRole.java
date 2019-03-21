package consert;

public interface EntityRole extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "consert/EntityRole";
  static final java.lang.String _DEFINITION = "string role\nContextEntity entity";
  java.lang.String getRole();
  void setRole(java.lang.String value);
  consert.ContextEntity getEntity();
  void setEntity(consert.ContextEntity value);
}
