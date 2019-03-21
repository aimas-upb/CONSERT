FILE(REMOVE_RECURSE
  "std_msgs"
  "CMakeFiles/consert_generate_messages_java"
  "java/consert/build.gradle"
)

# Per-language clean rules from dependency scanning.
FOREACH(lang)
  INCLUDE(CMakeFiles/consert_generate_messages_java.dir/cmake_clean_${lang}.cmake OPTIONAL)
ENDFOREACH(lang)
