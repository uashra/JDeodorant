package gr.uom.java.ast.inheritance;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.ListIterator;
import java.util.Map;

import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.SystemObject;

public class CompleteInheritanceDetection {
	private Map<String, LinkedHashSet<String>> subclassMap;
	
	public CompleteInheritanceDetection(SystemObject system) {
		this.subclassMap = new LinkedHashMap<String, LinkedHashSet<String>>();
		generateInheritanceHierarchies(system);
	}
	
	private void addSubclassToSuperclass(String superclass, String subclass) {
		if(subclassMap.containsKey(superclass)) {
			LinkedHashSet<String> subclasses = subclassMap.get(superclass);
			subclasses.add(subclass);
		}
		else {
			LinkedHashSet<String> subclasses = new LinkedHashSet<String>();
			subclasses.add(subclass);
			subclassMap.put(superclass, subclasses);
		}
	}
	
	private void generateInheritanceHierarchies(SystemObject system) {
		ListIterator<ClassObject> classIterator = system.getClassListIterator();
        while(classIterator.hasNext()) {
            ClassObject classObject = classIterator.next();
            String superclass = classObject.getSuperclass();
            if(system.getClassObject(superclass) != null) {
            	addSubclassToSuperclass(superclass, classObject.getName());
            }
            ListIterator<String> interfaceIterator = classObject.getInterfaceIterator();
            while(interfaceIterator.hasNext()) {
            	String superInterface = interfaceIterator.next();
            	if(system.getClassObject(superInterface) != null) {
                	addSubclassToSuperclass(superInterface, classObject.getName());
                }
            }
        }
	}
	
	public InheritanceTree getTree(String className) {
		if(subclassMap.containsKey(className)) {
			LinkedHashSet<String> subclasses = subclassMap.get(className);
			InheritanceTree tree = new InheritanceTree();
			for(String subclass : subclasses) {
				tree.addChildToParent(subclass, className);
			}
			return tree;
		}
		else {
			return null;
		}
	}
	
	public InheritanceTree getMatchingTree(String className) {
		for(String superclass : subclassMap.keySet()) {
			if((superclass.contains(".") && superclass.endsWith("." + className)) || superclass.equals(className)) {
				LinkedHashSet<String> subclasses = subclassMap.get(superclass);
				InheritanceTree tree = new InheritanceTree();
				for(String subclass : subclasses) {
					tree.addChildToParent(subclass, superclass);
				}
				return tree;
			}
		}
		return null;
	}
}
