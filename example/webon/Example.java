package webon;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Timer;

public class Example {
	public static void main(String[] args) throws Exception {
		File webRoot = new File("web");
		if (args.length > 0)
			webRoot = new File(args[0]);

		Branch root = new Branch(); // This is the root of all webon data.
		Jetty.initWeb("/", webRoot);
		Jetty.initServlet("/webon", root); // This call exposes all nodes under
											// 'root' to Jetty

		int port = 8300;
		if (args.length > 1)
			port = Integer.parseInt(args[1]);
		Jetty.start(port);

		exposeJVM(root);
		exposeOS(root);
		exposePseudoQueue(root);
	}

	/**
	 * This method shows how to expose application state by defining and
	 * mounting plain object to webon.
	 */
	public static void exposeJVM(Branch root) {
		Timer timer = new Timer("Timer for JVM statisitics");
		JVM jvm = new JVM();
		timer.schedule(jvm, 0, 2000);

		Valuator[] shema = Schematizer.plain.get(JVM.class);
		Leaf leaf = new Leaf("Simple JVM", shema, jvm);
		root.mount("jvm", leaf);
	}

	/**
	 * This method shows how to expose existing JavaBean to webon.
	 */
	public static void exposeOS(Branch root) {
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
		Valuator[] schema = Schematizer.bean.get(os.getClass());
		Leaf leaf = new Leaf("OSType", schema, os);
		root.mount("OS", leaf);
	}
//	public static void exposeMemoryUsage(Branch root) {
//		MemoryUsage mu = ManagementFactory.getMemoryMXBean()
//				.getHeapMemoryUsage();
//		Valuator[] schema = Schematizer.bean.get(mu.getClass());
//		Leaf leafHeap = new Leaf("Heap Usage", schema, mu);
//
//		mu = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
//		Leaf leafNonHeap = new Leaf("Non-Heap Usage", schema, mu);
//
//		Branch mem = root.extend("memory");
//		mem.mount("heap", leafHeap);
//		mem.mount("nonheap", leafNonHeap);
//	}

	/**
	 * This method shows how to expose objects with selective annotation.
	 */
	public static void exposePseudoQueue(Branch root) throws InterruptedException {
		Branch banks = root.extend("bank");
		PseudoQueue q = new PseudoQueue("ICBC-ATM");
		Valuator[] schema = Schematizer.exposed.get(PseudoQueue.class);
		Leaf leaf = new Leaf("PseudoQueue", schema, q);
		banks.mount("ICBC", leaf);
		
		Thread.sleep(1000);
		q = new PseudoQueue("HSBC-ATM");
		schema = Schematizer.exposed.get(PseudoQueue.class);
		leaf = new Leaf("PseudoQueue", schema, q);
		banks.mount("HSBC", leaf);
		
		Thread.sleep(1000);
		q = new PseudoQueue("CCB-ATM");
		schema = Schematizer.exposed.get(PseudoQueue.class);
		leaf = new Leaf("PseudoQueue", schema, q);
		banks.mount("CCB", leaf);
		
	}
}
