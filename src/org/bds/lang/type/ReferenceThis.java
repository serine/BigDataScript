package org.bds.lang.type;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.compile.CompilerMessages;
import org.bds.lang.BdsNode;
import org.bds.lang.statement.ClassDeclaration;
import org.bds.symbol.SymbolTable;

/**
 * A reference to a field in 'this' object
 *
 * @author pcingola
 */
public class ReferenceThis extends ReferenceVar {

	public ReferenceThis(BdsNode parent, ParseTree tree) {
		super(parent, tree);
		name = ClassDeclaration.THIS;
	}

	public ReferenceThis(BdsNode parent, TypeClass typeClass) {
		super(parent, null);
		name = ClassDeclaration.THIS;
		returnType = typeClass;
	}

	@Override
	protected void parse(ParseTree tree) {
		// Nothing to do
	}

	@Override
	public Type returnType(SymbolTable symtab) {
		if (returnType != null) return returnType;
		returnType = symtab.getType(ClassDeclaration.THIS);
		return returnType;
	}

	@Override
	public void typeCheck(SymbolTable symtab, CompilerMessages compilerMessages) {
		// Calculate return type
		returnType(symtab);
	}

}
