/*
 * 
 */
package wblut.core;

/**
 * 
 */
public class WB_ProgressReporter extends Thread {

	/**
	 * 
	 */
	WB_ProgressTracker tracker;

	/**
	 * 
	 */
	String status;

	/**
	 * 
	 *
	 * @param millis
	 */
	public WB_ProgressReporter() {
		super();
		tracker = WB_ProgressTracker.instance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#start()
	 */
	@Override
	public void start() {
		super.start();
		System.out.println("Starting WB_ProgressTracker");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {

				while (tracker.isUpdated()) {
					status = tracker.getStatus();
					if (status != null) {
						System.out.println(status);
					}
				}
			} catch (final Exception e) {

				e.printStackTrace();
			}
		}
	}
}
