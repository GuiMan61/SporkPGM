package io.sporkpgm.module;

import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.builder.BuilderResult;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleInfo {

	public Class<? extends Builder> builder();

	public abstract String description();

	public abstract Class<? extends Module>[] requires() default {};

	public boolean multiple() default true;

	public boolean listener() default true;

}
