package org.sigmah.server.conf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 * Defines the period in seconds to reload the properties.
 * 
 * @author Guerline Jean-Baptiste(gjbaptiste@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
					ElementType.FIELD,
					ElementType.PARAMETER
})
@BindingAnnotation
public @interface ReloadPeriod {
}
