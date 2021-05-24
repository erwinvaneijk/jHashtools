package nl.minjus.nfi.dt.jhashtools.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Optional {

    /**
     * Specify a Throwable, to cause a test method to succeed even if an exception
     * of the specified class is thrown by the method.
     */
    Class<? extends Throwable>[] exception();
}