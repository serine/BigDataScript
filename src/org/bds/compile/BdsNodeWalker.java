package org.bds.compile;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bds.lang.BdsNode;
import org.bds.lang.BdsNodeFactory;
import org.bds.lang.statement.StatementInclude;

/**
 * Walks through all the BdsNodes
 *
 * IMPORTANT: Nodes are ALPHABETICALLY sorted
 *
 * @author pcingola
 */
public class BdsNodeWalker implements Iterable<BdsNode> {

	boolean recurse; // If true, perform recursive search
	boolean recurseInclude; // If true, perform recursive search within 'StatementInclide' nodes. Note: If 'recurse' is set, the value of 'recurseInclude' is irrelevant
	BdsNode bdsNode;
	Class clazz; // Class to find (all nodes if null)
	HashSet<Object> visited = new HashSet<>();

	/**
	 * Find all nodes of a given type
	 * @param clazz : If null, all nodes are added
	 * @param fieldObj
	 */
	List<BdsNode> addFields(Object fieldObj) {
		List<BdsNode> list = new ArrayList<>();

		// If it is a BigDataScriptNode then we can recurse into it
		if ((fieldObj != null) && (fieldObj instanceof BdsNode)) {
			BdsNode bdsnode = ((BdsNode) fieldObj);

			// Found the requested type?
			if ((clazz == null) || (fieldObj.getClass() == clazz)) list.add((BdsNode) fieldObj);

			// Recurse into this field?
			if (recurse || (recurseInclude && bdsnode instanceof StatementInclude)) {
				list.addAll(addFields(bdsnode));
			}
		}

		return list;
	}

	public List<BdsNode> findNodes() {
		return findNodes(bdsNode);
	}

	/**
	 * Find all nodes
	 */
	protected List<BdsNode> findNodes(BdsNode bdsNode) {
		List<BdsNode> list = new ArrayList<>();

		// Iterate over fields
		for (Field field : getAllClassFields(bdsNode)) {
			try {
				field.setAccessible(true);
				Object fieldObj = field.get(this);

				// Does the field have a map?
				if (fieldObj != null && !visited.contains(fieldObj)) {
					visited.add(fieldObj);

					// If it's an array, iterate on all objects
					if (fieldObj.getClass().isArray()) {
						for (Object fieldObjSingle : (Object[]) fieldObj)
							list.addAll(addFields(fieldObjSingle));
					} else {
						list.addAll(addFields(fieldObj));
					}

				}
			} catch (Exception e) {
				throw new RuntimeException("Error getting field '" + field.getName() + "' from class '" + this.getClass().getCanonicalName() + "'", e);
			}
		}

		return list;
	}

	List<Field> getAllClassFields(BdsNode bdsNode) {
		return getAllClassFields(bdsNode, false, true, true, true, true, false, false);
	}

	/**
	 * Get all fields from this class
	 *
	 * IMPORTANT: Nodes are returned ALPHABETICALLY sorted
	 */
	@SuppressWarnings("rawtypes")
	List<Field> getAllClassFields(BdsNode bdsNode, boolean addParent, boolean addNode, boolean addPrimitive, boolean addClass, boolean addArray, boolean addStatic, boolean addPrivate) {
		// Top class (if we are looking for 'parent' field, we need to include BdsNode, otherwise we don't
		Class topClass = (addParent ? Object.class : BdsNode.class);

		// Get all fields for each parent class
		ArrayList<Field> fields = new ArrayList<>();

		for (Class clazz = bdsNode.getClass(); clazz != topClass; clazz = clazz.getSuperclass()) {
			for (Field f : clazz.getDeclaredFields()) {
				// Add field?
				if (Modifier.isPrivate(f.getModifiers())) {
					if (addPrivate) fields.add(f);
				} else if (Modifier.isStatic(f.getModifiers())) {
					if (addStatic) fields.add(f);
				} else if (f.getName().equals("parent")) {
					if (addParent) fields.add(f);
				} else if (f.getType().getCanonicalName().startsWith(BdsNodeFactory.get().packageName())) {
					if (addNode) fields.add(f);
				} else if (f.getType().isPrimitive() || (f.getType() == String.class)) {
					if (addPrimitive) fields.add(f);
				} else if (f.getType().isArray()) {
					if (addArray) fields.add(f);
				} else if (!f.getType().isPrimitive()) {
					if (addClass) fields.add(f);
				}
			}
		}

		// Sort by name
		Collections.sort(fields, new Comparator<Field>() {
			@Override
			public int compare(Field o1, Field o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		return fields;
	}

	@Override
	public Iterator<BdsNode> iterator() {
		return findNodes().iterator();
	}

	public Stream<BdsNode> stream() {
		return StreamSupport.stream(findNodes().spliterator(), false);
	}
}