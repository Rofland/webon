package webon;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

class PseudoQueue implements Runnable {
	public PseudoQueue(String name) {
		new Thread(this, name).start();
	}

	private Queue<String> queue = new LinkedList<String>();

	@Expose
	public long totalCustomer;

	Random rand = new Random(System.currentTimeMillis());

	public void run() {
		while (true)
			try {
				step();
				Thread.sleep((rand.nextInt(3) + 1) * 1000);
			} catch (Exception e) {
			}
	}

	protected void step() {
		if (rand.nextBoolean()) {
			// new customer in
			totalCustomer++;
			@SuppressWarnings("unused")
			byte[] letsWasteSomeHeap = new byte[1 << 20];
			queue.add("Customer No." + totalCustomer);
		} else if (!queue.isEmpty())
			// existing customer out
			queue.poll();
	}

	@Expose
	public String currentlyServing() {
		return queue.peek();
	}

	@Expose
	public int queueLength() {
		return queue.isEmpty() ? 0 : queue.size() - 1;
	}
}
