package ca.mcgill.mcb.pcingola.bigDataScript.lang;

import org.antlr.v4.runtime.tree.ParseTree;

import ca.mcgill.mcb.pcingola.bigDataScript.compile.CompilerMessage.MessageType;
import ca.mcgill.mcb.pcingola.bigDataScript.compile.CompilerMessages;
import ca.mcgill.mcb.pcingola.bigDataScript.run.BigDataScriptThread;
import ca.mcgill.mcb.pcingola.bigDataScript.run.RunState;
import ca.mcgill.mcb.pcingola.bigDataScript.scope.Scope;

/**
 * A "checkpoint" statement
 *
 * @author pcingola
 */
public class Checkpoint extends Statement {

	Expression expr;

	public Checkpoint(BigDataScriptNode parent, ParseTree tree) {
		super(parent, tree);
	}

	@Override
	protected void parse(ParseTree tree) {
		int idx = 0;
		if (isTerminal(tree, idx, "checkpoint")) idx++; // 'checkpoint'
		if (tree.getChildCount() > idx) expr = (Expression) factory(tree, idx);
	}

	/**
	 * Run the program
	 */
	@Override
	protected RunState runStep(BigDataScriptThread bdsThread) {
		// Get filename
		String file = null;
		if (expr != null) file = expr.evalString(bdsThread);

		if (file != null) bdsThread.checkpoint(file);
		else bdsThread.checkpoint(this);

		return RunState.OK;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName().toLowerCase() + expr + "\n";
	}

	@Override
	protected void typeCheck(Scope scope, CompilerMessages compilerMessages) {
		if (expr != null) {
			expr.returnType(scope);
			if (!expr.getReturnType().isString()) compilerMessages.add(this, "Checkpoint argument should be a string (file name)", MessageType.ERROR);
		}
	}
}
