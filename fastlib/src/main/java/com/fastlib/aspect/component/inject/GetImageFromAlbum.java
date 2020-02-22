package com.fastlib.aspect.component.inject;

import android.Manifest;

import com.fastlib.annotation.Permission;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by sgfb on 2020\01\13.
 */
@Permission(Manifest.permission.READ_EXTERNAL_STORAGE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GetImageFromAlbum {
}
