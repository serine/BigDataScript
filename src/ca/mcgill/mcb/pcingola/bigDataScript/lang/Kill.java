package ca.mcgill.mcb.pcingola.bigDataScript.lang;

import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;

import ca.mcgill.mcb.pcingola.bigDataScript.run.BigDataScriptThread;
import ca.mcgill.mcb.pcingola.bigDataScript.run.RunState;

/**
 * A "Kill" statement.
 *
 * Kill a task/thread (or a list of tasks/threads) and
 * wait until all tasks/threads finished (i.e. died)
 *
 * @author pcingola
 */
public class Kill extends Statement {

	Expression taskId;

	public Kill(BigDataScriptNode parent, ParseTree tree) {
		super(parent, tree);
	}

	@Override
	protected void parse(ParseTree tree) {
		// child[0] = 'Kill'
		if (tree.getChildCount() > 1) taskId = (Expression) factory(tree, 1);
	}

	/**
	 * Run the program
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected RunState runStep(BigDataScriptThread bdsThread) {
		// No arguments? Kill for all tasks
		Object val = taskId.eval(bdsThread);

		// Are we Killing for one task or a list of tasks?
		if (val instanceof List) {
			bdsThread.kill((List) val);
			bdsThread.wait((List) val);
		} else {
			bdsThread.kill(val.toString());
			bdsThread.wait(val.toString());
		}

		return RunState.OK;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName().toLowerCase() + (taskId != null ? taskId : "") + "\n";
	}

}
