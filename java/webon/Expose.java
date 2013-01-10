package webon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate {@link Schematizer#exposed} to include this (public) field or
 * value-method to be included in schema. Value-method is a method that takes no
 * argument and returns non-void types.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Expose {
}
