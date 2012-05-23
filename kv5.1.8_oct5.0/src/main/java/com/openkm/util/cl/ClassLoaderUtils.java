package com.openkm.util.cl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassLoaderUtils {
	private static Logger log = LoggerFactory.getLogger(ClassLoaderUtils.class);
	private static final String MAIN = "main";
	
	/**
	 * Invoke static void main method from className.
	 */
	public static Object invokeMainMethodFromClass(String className, String[] args, ClassLoader classLoader) throws 
			ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
		Class<?> c = classLoader.loadClass(className);
		return invokeMainMethodFromClass(c, args);
	}
	
	/**
	 * Invoke static methodName from className. 
	 */
	public static Object invokeMethodFromClass(String className, String methodName,
			ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException,
			InvocationTargetException {
		Class<?> c = classLoader.loadClass(className);
		return invokeMethodFromClass(c, methodName);
	}
	
	/**
	 * Invoke static methodName from className with args arguments.
	 */
	public static Object invokeMethodFromClass(String className, String methodName, Object args,
			ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException,
			InvocationTargetException {
		Class<?> c = classLoader.loadClass(className);
		return invokeMethodFromClass(c, methodName, args);
	}
	
	/**
	 * Invoke static void main method from class.
	 */
	public static Object invokeMainMethodFromClass(Class<?> c, String[] args) throws ClassNotFoundException,
			NoSuchMethodException, InvocationTargetException {
		log.debug("invokeMainMethodFromClass({}, {})", c, args);
		Method m = c.getMethod(MAIN, new Class[] { args.getClass() });
		m.setAccessible(true);
		int mods = m.getModifiers();
		
		if (m.getReturnType() != void.class || !Modifier.isStatic(mods) || !Modifier.isPublic(mods)) {
			throw new NoSuchMethodException(MAIN);
		}
		
		try {
			return m.invoke(null, new Object[] { args });
		} catch (IllegalAccessException e) {
			// This should not happen, as we have disabled access checks
		}
		
		return null;
	}
	
	
	
	/**
	 * Invoke static methodName from class.
	 */
	public static Object invokeMethodFromClass(Class<?> c, String methodName) throws
			ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
		log.debug("invokeMethodFromClass({}, {})", new Object[] { c, methodName });
		Method m = c.getMethod(methodName);
		m.setAccessible(true);
		int mods = m.getModifiers();
		
		if (!Modifier.isStatic(mods) || !Modifier.isPublic(mods)) {
			throw new NoSuchMethodException(methodName);
		}
		
		try {
			return m.invoke(null);
		} catch (IllegalAccessException e) {
			// This should not happen, as we have disabled access checks
		}
		
		return null;
	}
	
	/**
	 * Invoke static methodName with arguments from class.
	 */
	public static Object invokeMethodFromClass(Class<?> c, String methodName, Object args) throws
			ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
		log.debug("invokeMethodFromClass({}, {}, {})", new Object[] { c, methodName, args });
		Method m = c.getMethod(methodName, args.getClass());
		m.setAccessible(true);
		int mods = m.getModifiers();
		
		if (!Modifier.isStatic(mods) || !Modifier.isPublic(mods)) {
			throw new NoSuchMethodException(methodName);
		}
		
		try {
			return m.invoke(null, new Object[] { args });
		} catch (IllegalAccessException e) {
			// This should not happen, as we have disabled access checks
		}
		
		return null;
	}
}
