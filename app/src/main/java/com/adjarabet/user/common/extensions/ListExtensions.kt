package com.adjarabet.user.common.extensions

fun List<String>.getTrimmedList():MutableList<String>{
  return  toMutableList().apply {
        removeAll {
            it == ""
        }
    }
}