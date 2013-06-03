package org.sigmah.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 * Defines the period in seconds to reload the configurator.
 * 
 * @author Guerline Jean-Baptiste(gjbaptiste@ideia.fr)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
         ElementType.FIELD,
         ElementType.PARAMETER })
@BindingAnnotation
public @interface ReloadConfiguratorPeriod {

}
