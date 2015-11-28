/*
 * 
 */
package wblut.core;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 
 */
public class WB_ProgressTracker {

	protected Queue<Status> statuses;
	protected volatile int depth;
	private static int indent = 3;

	/**
	 * 
	 */
	protected WB_ProgressTracker() {
		statuses = new ConcurrentLinkedQueue<Status>();
		depth = 0;
	}

	/**
	 * 
	 */
	private static final WB_ProgressTracker tracker = new WB_ProgressTracker();

	/**
	 * 
	 *
	 * @return
	 */
	public static WB_ProgressTracker instance() {
		return tracker;
	}

	public void setIndent(int indent) {
		WB_ProgressTracker.indent = Math.max(0, indent);

	}

	/**
	 * 
	 *
	 * @return
	 */
	protected String getStatus() {
		if (statuses.size() > 0) {
			return statuses.poll().getStatus();
		}
		return "";

	}

	/*
	 * public void setStatus(String caller, final String status) {
	 * statuses.add(new Status(caller, status, depth)); }
	 */
	public void setStatus(Object caller, final String status, int inc) {
		if (inc < 0)
			depth = Math.max(0, depth + inc);
		statuses.add(new Status(caller.getClass().getSimpleName(), status, depth));
		if (inc > 0)
			depth = Math.max(0, depth + inc);
	}

	/**
	 * 
	 *
	 * @param status
	 */
	public void setStatus(Object caller, final String status, WB_ProgressCounter counter) {

		if (counter.getLimit() > 0) {
			counter.caller = caller.getClass().getSimpleName();
			counter.text = status;
			statuses.add(new Status(caller.getClass().getSimpleName(), status, counter, depth));
		}
	}

	protected void setStatusByString(String caller, final String status, WB_ProgressCounter counter) {
		if (counter.getLimit() > 0) {
			statuses.add(new Status(caller, status, counter, depth));
		}
	}

	public boolean isUpdated() {

		return statuses.size() > 0;

	}

	class Status {
		String caller;
		String text;
		String counter;
		String depth;

		Status(String caller, String text, WB_ProgressCounter counter, int depth) {
			this.caller = caller;
			this.text = text;
			StringBuffer outputBuffer = new StringBuffer(depth);
			for (int i = 0; i < depth * indent; i++) {
				outputBuffer.append(" ");
			}
			this.depth = outputBuffer.toString();

			this.counter = (counter.getLimit() > 0) ? " (" + counter.getCount() + " of " + counter.getLimit() + ")"
					: "";
		}

		Status(String caller, String text, int depth) {
			this.caller = caller;
			this.text = text;
			StringBuffer outputBuffer = new StringBuffer(depth);
			for (int i = 0; i < depth * indent; i++) {
				outputBuffer.append(" ");
			}
			this.depth = outputBuffer.toString();
			this.counter = null;
		}

		String getStatus() {
			if (caller == null)
				return null;
			if (text == " ")
				return "";
			if (counter == null)
				return depth + caller + ": " + text;
			return depth + caller + ": " + text + counter;

		}

	}

}
