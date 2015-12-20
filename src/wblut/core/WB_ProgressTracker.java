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
	protected volatile int maxdepth;

	/**
	 *
	 */
	protected WB_ProgressTracker() {
		statuses = new ConcurrentLinkedQueue<Status>();
		depth = 0;
		maxdepth=3;
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

	public void setIndent(final int indent) {
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
	public void setStatus(final Object caller, final String status, final int inc) {
		if (inc < 0) {
			depth = Math.max(0, depth + inc);
		}
		if(depth<=maxdepth){
			statuses.add(new Status(caller.getClass().getSimpleName(), status, depth));
		}
		if (inc > 0) {
			depth = Math.max(0, depth + inc);
		}
	}

	/**
	 *
	 *
	 * @param status
	 */
	public void setStatus(final Object caller, final String status, final WB_ProgressCounter counter) {

		if (counter.getLimit() > 0) {
			counter.caller = caller.getClass().getSimpleName();
			counter.text = status;
			if(depth<=maxdepth){
				statuses.add(new Status(caller.getClass().getSimpleName(), status, counter, depth));
			}
		}
	}

	protected void setStatusByString(final String caller, final String status, final WB_ProgressCounter counter) {
		if (counter.getLimit() > 0) {
			if(depth<=maxdepth){
				statuses.add(new Status(caller, status, counter, depth));
			}
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
		int level;

		Status(final String caller, final String text, final WB_ProgressCounter counter, final int depth) {
			this.caller = caller;
			this.text = text;
			StringBuffer outputBuffer = new StringBuffer(depth);
			for (int i = 0; i < (depth * indent); i++) {
				outputBuffer.append(" ");
			}
			this.depth = outputBuffer.toString();

			this.counter = (counter.getLimit() > 0) ? " (" + counter.getCount() + " of " + counter.getLimit() + ")"
					: "";
			level=depth;
		}

		Status(final String caller, final String text, final int depth) {
			this.caller = caller;
			this.text = text;
			StringBuffer outputBuffer = new StringBuffer(depth);
			for (int i = 0; i < (depth * indent); i++) {
				outputBuffer.append(" ");
			}
			this.depth = outputBuffer.toString();
			this.counter = null;
		}

		String getStatus() {
			if (caller == null) {
				return null;
			}
			if (text == " ") {
				return "";
			}
			if (counter == null) {
				return depth + caller + ": " + text;
			}
			return depth + caller + ": " + text + counter;

		}

	}

}
