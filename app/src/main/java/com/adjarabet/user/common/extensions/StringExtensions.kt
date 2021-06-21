package com.adjarabet.user.common.extensions

fun String.getLastWord():String{
    return split(' ').last()
}