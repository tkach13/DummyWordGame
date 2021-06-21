package com.adjarabet.user.common.helpers

import android.content.pm.PackageManager

fun isPackageInstalled(packageName:String, packageManager: PackageManager):Boolean {
    return try {
        packageManager.getPackageInfo(packageName,0)
        true
    } catch (packageManager: PackageManager.NameNotFoundException){
        false
    }
}