package com.example.android.androidskeletonapp.data.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Exercise {
    String exerciseNumber() default "ex00";
    int version() default 1;
    String title();
    String tips();
    String solutionBranch() default "sol00";
}
