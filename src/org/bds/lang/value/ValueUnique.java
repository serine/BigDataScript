package org.bds.lang.value;

import java.util.HashMap;
import java.util.Map;

import org.bds.lang.type.Type;

public class ValueUnique extends Value {

	private static Map<Type, ValueUnique> valueUniqueByType = new HashMap<>();

	public static ValueUnique get(Type type) {
		if (!valueUniqueByType.containsKey(type)) valueUniqueByType.put(type, new ValueUnique(type));
		return valueUniqueByType.get(type);
	}

	private ValueUnique(Type type) {
		super(type);
	}

	@Override
	public Object get() {
		return null;
	}

	@Override
	public void set(Object v) {
		// This value cannot be set
	}

}
