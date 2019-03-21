# generated from genmsg/cmake/pkg-genmsg.cmake.em

message(STATUS "consert: 8 messages, 0 services")

set(MSG_I_FLAGS "-Iconsert:/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg;-Istd_msgs:/opt/ros/indigo/share/std_msgs/cmake/../msg")

# Find all generators
find_package(gencpp REQUIRED)
find_package(genjava REQUIRED)
find_package(genlisp REQUIRED)
find_package(genpy REQUIRED)

add_custom_target(consert_generate_messages ALL)

# verify that message/service dependencies have not changed since configure



get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/NaryAssertion.msg" NAME_WE)
add_custom_target(_consert_generate_messages_check_deps_${_filename}
  COMMAND ${CATKIN_ENV} ${PYTHON_EXECUTABLE} ${GENMSG_CHECK_DEPS_SCRIPT} "consert" "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/NaryAssertion.msg" "consert/ContextEntity:consert/EntityRole:consert/ContextAnnotation"
)

get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg" NAME_WE)
add_custom_target(_consert_generate_messages_check_deps_${_filename}
  COMMAND ${CATKIN_ENV} ${PYTHON_EXECUTABLE} ${GENMSG_CHECK_DEPS_SCRIPT} "consert" "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg" "consert/ContextEntity"
)

get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg" NAME_WE)
add_custom_target(_consert_generate_messages_check_deps_${_filename}
  COMMAND ${CATKIN_ENV} ${PYTHON_EXECUTABLE} ${GENMSG_CHECK_DEPS_SCRIPT} "consert" "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg" ""
)

get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/BinaryAssertion.msg" NAME_WE)
add_custom_target(_consert_generate_messages_check_deps_${_filename}
  COMMAND ${CATKIN_ENV} ${PYTHON_EXECUTABLE} ${GENMSG_CHECK_DEPS_SCRIPT} "consert" "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/BinaryAssertion.msg" "consert/ContextEntity:consert/EntityRole:consert/ContextAnnotation"
)

get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityDescription.msg" NAME_WE)
add_custom_target(_consert_generate_messages_check_deps_${_filename}
  COMMAND ${CATKIN_ENV} ${PYTHON_EXECUTABLE} ${GENMSG_CHECK_DEPS_SCRIPT} "consert" "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityDescription.msg" "consert/ContextEntity"
)

get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/UnaryAssertion.msg" NAME_WE)
add_custom_target(_consert_generate_messages_check_deps_${_filename}
  COMMAND ${CATKIN_ENV} ${PYTHON_EXECUTABLE} ${GENMSG_CHECK_DEPS_SCRIPT} "consert" "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/UnaryAssertion.msg" "consert/ContextEntity:consert/EntityRole:consert/ContextAnnotation"
)

get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAssertion.msg" NAME_WE)
add_custom_target(_consert_generate_messages_check_deps_${_filename}
  COMMAND ${CATKIN_ENV} ${PYTHON_EXECUTABLE} ${GENMSG_CHECK_DEPS_SCRIPT} "consert" "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAssertion.msg" "consert/ContextEntity:consert/EntityRole:consert/ContextAnnotation"
)

get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg" NAME_WE)
add_custom_target(_consert_generate_messages_check_deps_${_filename}
  COMMAND ${CATKIN_ENV} ${PYTHON_EXECUTABLE} ${GENMSG_CHECK_DEPS_SCRIPT} "consert" "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg" ""
)

#
#  langs = gencpp;genjava;genlisp;genpy
#

### Section generating for lang: gencpp
### Generating Messages
_generate_msg_cpp(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/NaryAssertion.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg"
  ${CATKIN_DEVEL_PREFIX}/${gencpp_INSTALL_DIR}/consert
)
_generate_msg_cpp(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg"
  ${CATKIN_DEVEL_PREFIX}/${gencpp_INSTALL_DIR}/consert
)
_generate_msg_cpp(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg"
  "${MSG_I_FLAGS}"
  ""
  ${CATKIN_DEVEL_PREFIX}/${gencpp_INSTALL_DIR}/consert
)
_generate_msg_cpp(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/BinaryAssertion.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg"
  ${CATKIN_DEVEL_PREFIX}/${gencpp_INSTALL_DIR}/consert
)
_generate_msg_cpp(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityDescription.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg"
  ${CATKIN_DEVEL_PREFIX}/${gencpp_INSTALL_DIR}/consert
)
_generate_msg_cpp(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/UnaryAssertion.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg"
  ${CATKIN_DEVEL_PREFIX}/${gencpp_INSTALL_DIR}/consert
)
_generate_msg_cpp(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAssertion.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg"
  ${CATKIN_DEVEL_PREFIX}/${gencpp_INSTALL_DIR}/consert
)
_generate_msg_cpp(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg"
  "${MSG_I_FLAGS}"
  ""
  ${CATKIN_DEVEL_PREFIX}/${gencpp_INSTALL_DIR}/consert
)

### Generating Services

### Generating Module File
_generate_module_cpp(consert
  ${CATKIN_DEVEL_PREFIX}/${gencpp_INSTALL_DIR}/consert
  "${ALL_GEN_OUTPUT_FILES_cpp}"
)

add_custom_target(consert_generate_messages_cpp
  DEPENDS ${ALL_GEN_OUTPUT_FILES_cpp}
)
add_dependencies(consert_generate_messages consert_generate_messages_cpp)

# add dependencies to all check dependencies targets
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/NaryAssertion.msg" NAME_WE)
add_dependencies(consert_generate_messages_cpp _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg" NAME_WE)
add_dependencies(consert_generate_messages_cpp _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg" NAME_WE)
add_dependencies(consert_generate_messages_cpp _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/BinaryAssertion.msg" NAME_WE)
add_dependencies(consert_generate_messages_cpp _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityDescription.msg" NAME_WE)
add_dependencies(consert_generate_messages_cpp _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/UnaryAssertion.msg" NAME_WE)
add_dependencies(consert_generate_messages_cpp _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAssertion.msg" NAME_WE)
add_dependencies(consert_generate_messages_cpp _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg" NAME_WE)
add_dependencies(consert_generate_messages_cpp _consert_generate_messages_check_deps_${_filename})

# target for backward compatibility
add_custom_target(consert_gencpp)
add_dependencies(consert_gencpp consert_generate_messages_cpp)

# register target for catkin_package(EXPORTED_TARGETS)
list(APPEND ${PROJECT_NAME}_EXPORTED_TARGETS consert_generate_messages_cpp)

### Section generating for lang: genjava
### Generating Messages
_generate_msg_java(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/NaryAssertion.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg"
  ${CATKIN_DEVEL_PREFIX}/${genjava_INSTALL_DIR}/consert
)
_generate_msg_java(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg"
  ${CATKIN_DEVEL_PREFIX}/${genjava_INSTALL_DIR}/consert
)
_generate_msg_java(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg"
  "${MSG_I_FLAGS}"
  ""
  ${CATKIN_DEVEL_PREFIX}/${genjava_INSTALL_DIR}/consert
)
_generate_msg_java(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/BinaryAssertion.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg"
  ${CATKIN_DEVEL_PREFIX}/${genjava_INSTALL_DIR}/consert
)
_generate_msg_java(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityDescription.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg"
  ${CATKIN_DEVEL_PREFIX}/${genjava_INSTALL_DIR}/consert
)
_generate_msg_java(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/UnaryAssertion.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg"
  ${CATKIN_DEVEL_PREFIX}/${genjava_INSTALL_DIR}/consert
)
_generate_msg_java(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAssertion.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg"
  ${CATKIN_DEVEL_PREFIX}/${genjava_INSTALL_DIR}/consert
)
_generate_msg_java(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg"
  "${MSG_I_FLAGS}"
  ""
  ${CATKIN_DEVEL_PREFIX}/${genjava_INSTALL_DIR}/consert
)

### Generating Services

### Generating Module File
_generate_module_java(consert
  ${CATKIN_DEVEL_PREFIX}/${genjava_INSTALL_DIR}/consert
  "${ALL_GEN_OUTPUT_FILES_java}"
)

add_custom_target(consert_generate_messages_java
  DEPENDS ${ALL_GEN_OUTPUT_FILES_java}
)
add_dependencies(consert_generate_messages consert_generate_messages_java)

# add dependencies to all check dependencies targets
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/NaryAssertion.msg" NAME_WE)
add_dependencies(consert_generate_messages_java _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg" NAME_WE)
add_dependencies(consert_generate_messages_java _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg" NAME_WE)
add_dependencies(consert_generate_messages_java _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/BinaryAssertion.msg" NAME_WE)
add_dependencies(consert_generate_messages_java _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityDescription.msg" NAME_WE)
add_dependencies(consert_generate_messages_java _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/UnaryAssertion.msg" NAME_WE)
add_dependencies(consert_generate_messages_java _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAssertion.msg" NAME_WE)
add_dependencies(consert_generate_messages_java _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg" NAME_WE)
add_dependencies(consert_generate_messages_java _consert_generate_messages_check_deps_${_filename})

# target for backward compatibility
add_custom_target(consert_genjava)
add_dependencies(consert_genjava consert_generate_messages_java)

# register target for catkin_package(EXPORTED_TARGETS)
list(APPEND ${PROJECT_NAME}_EXPORTED_TARGETS consert_generate_messages_java)

### Section generating for lang: genlisp
### Generating Messages
_generate_msg_lisp(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/NaryAssertion.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg"
  ${CATKIN_DEVEL_PREFIX}/${genlisp_INSTALL_DIR}/consert
)
_generate_msg_lisp(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg"
  ${CATKIN_DEVEL_PREFIX}/${genlisp_INSTALL_DIR}/consert
)
_generate_msg_lisp(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg"
  "${MSG_I_FLAGS}"
  ""
  ${CATKIN_DEVEL_PREFIX}/${genlisp_INSTALL_DIR}/consert
)
_generate_msg_lisp(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/BinaryAssertion.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg"
  ${CATKIN_DEVEL_PREFIX}/${genlisp_INSTALL_DIR}/consert
)
_generate_msg_lisp(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityDescription.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg"
  ${CATKIN_DEVEL_PREFIX}/${genlisp_INSTALL_DIR}/consert
)
_generate_msg_lisp(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/UnaryAssertion.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg"
  ${CATKIN_DEVEL_PREFIX}/${genlisp_INSTALL_DIR}/consert
)
_generate_msg_lisp(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAssertion.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg"
  ${CATKIN_DEVEL_PREFIX}/${genlisp_INSTALL_DIR}/consert
)
_generate_msg_lisp(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg"
  "${MSG_I_FLAGS}"
  ""
  ${CATKIN_DEVEL_PREFIX}/${genlisp_INSTALL_DIR}/consert
)

### Generating Services

### Generating Module File
_generate_module_lisp(consert
  ${CATKIN_DEVEL_PREFIX}/${genlisp_INSTALL_DIR}/consert
  "${ALL_GEN_OUTPUT_FILES_lisp}"
)

add_custom_target(consert_generate_messages_lisp
  DEPENDS ${ALL_GEN_OUTPUT_FILES_lisp}
)
add_dependencies(consert_generate_messages consert_generate_messages_lisp)

# add dependencies to all check dependencies targets
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/NaryAssertion.msg" NAME_WE)
add_dependencies(consert_generate_messages_lisp _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg" NAME_WE)
add_dependencies(consert_generate_messages_lisp _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg" NAME_WE)
add_dependencies(consert_generate_messages_lisp _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/BinaryAssertion.msg" NAME_WE)
add_dependencies(consert_generate_messages_lisp _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityDescription.msg" NAME_WE)
add_dependencies(consert_generate_messages_lisp _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/UnaryAssertion.msg" NAME_WE)
add_dependencies(consert_generate_messages_lisp _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAssertion.msg" NAME_WE)
add_dependencies(consert_generate_messages_lisp _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg" NAME_WE)
add_dependencies(consert_generate_messages_lisp _consert_generate_messages_check_deps_${_filename})

# target for backward compatibility
add_custom_target(consert_genlisp)
add_dependencies(consert_genlisp consert_generate_messages_lisp)

# register target for catkin_package(EXPORTED_TARGETS)
list(APPEND ${PROJECT_NAME}_EXPORTED_TARGETS consert_generate_messages_lisp)

### Section generating for lang: genpy
### Generating Messages
_generate_msg_py(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/NaryAssertion.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg"
  ${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/consert
)
_generate_msg_py(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg"
  ${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/consert
)
_generate_msg_py(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg"
  "${MSG_I_FLAGS}"
  ""
  ${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/consert
)
_generate_msg_py(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/BinaryAssertion.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg"
  ${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/consert
)
_generate_msg_py(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityDescription.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg"
  ${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/consert
)
_generate_msg_py(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/UnaryAssertion.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg"
  ${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/consert
)
_generate_msg_py(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAssertion.msg"
  "${MSG_I_FLAGS}"
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg;/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg"
  ${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/consert
)
_generate_msg_py(consert
  "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg"
  "${MSG_I_FLAGS}"
  ""
  ${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/consert
)

### Generating Services

### Generating Module File
_generate_module_py(consert
  ${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/consert
  "${ALL_GEN_OUTPUT_FILES_py}"
)

add_custom_target(consert_generate_messages_py
  DEPENDS ${ALL_GEN_OUTPUT_FILES_py}
)
add_dependencies(consert_generate_messages consert_generate_messages_py)

# add dependencies to all check dependencies targets
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/NaryAssertion.msg" NAME_WE)
add_dependencies(consert_generate_messages_py _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityRole.msg" NAME_WE)
add_dependencies(consert_generate_messages_py _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextEntity.msg" NAME_WE)
add_dependencies(consert_generate_messages_py _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/BinaryAssertion.msg" NAME_WE)
add_dependencies(consert_generate_messages_py _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/EntityDescription.msg" NAME_WE)
add_dependencies(consert_generate_messages_py _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/UnaryAssertion.msg" NAME_WE)
add_dependencies(consert_generate_messages_py _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAssertion.msg" NAME_WE)
add_dependencies(consert_generate_messages_py _consert_generate_messages_check_deps_${_filename})
get_filename_component(_filename "/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/catkin_ws/src/consert/msg/ContextAnnotation.msg" NAME_WE)
add_dependencies(consert_generate_messages_py _consert_generate_messages_check_deps_${_filename})

# target for backward compatibility
add_custom_target(consert_genpy)
add_dependencies(consert_genpy consert_generate_messages_py)

# register target for catkin_package(EXPORTED_TARGETS)
list(APPEND ${PROJECT_NAME}_EXPORTED_TARGETS consert_generate_messages_py)



if(gencpp_INSTALL_DIR AND EXISTS ${CATKIN_DEVEL_PREFIX}/${gencpp_INSTALL_DIR}/consert)
  # install generated code
  install(
    DIRECTORY ${CATKIN_DEVEL_PREFIX}/${gencpp_INSTALL_DIR}/consert
    DESTINATION ${gencpp_INSTALL_DIR}
  )
endif()
if(TARGET std_msgs_generate_messages_cpp)
  add_dependencies(consert_generate_messages_cpp std_msgs_generate_messages_cpp)
endif()

if(genjava_INSTALL_DIR AND EXISTS ${CATKIN_DEVEL_PREFIX}/${genjava_INSTALL_DIR}/consert)
  # install generated code
  install(
    DIRECTORY ${CATKIN_DEVEL_PREFIX}/${genjava_INSTALL_DIR}/consert
    DESTINATION ${genjava_INSTALL_DIR}
  )
endif()
if(TARGET std_msgs_generate_messages_java)
  add_dependencies(consert_generate_messages_java std_msgs_generate_messages_java)
endif()

if(genlisp_INSTALL_DIR AND EXISTS ${CATKIN_DEVEL_PREFIX}/${genlisp_INSTALL_DIR}/consert)
  # install generated code
  install(
    DIRECTORY ${CATKIN_DEVEL_PREFIX}/${genlisp_INSTALL_DIR}/consert
    DESTINATION ${genlisp_INSTALL_DIR}
  )
endif()
if(TARGET std_msgs_generate_messages_lisp)
  add_dependencies(consert_generate_messages_lisp std_msgs_generate_messages_lisp)
endif()

if(genpy_INSTALL_DIR AND EXISTS ${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/consert)
  install(CODE "execute_process(COMMAND \"/usr/bin/python\" -m compileall \"${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/consert\")")
  # install generated code
  install(
    DIRECTORY ${CATKIN_DEVEL_PREFIX}/${genpy_INSTALL_DIR}/consert
    DESTINATION ${genpy_INSTALL_DIR}
  )
endif()
if(TARGET std_msgs_generate_messages_py)
  add_dependencies(consert_generate_messages_py std_msgs_generate_messages_py)
endif()
