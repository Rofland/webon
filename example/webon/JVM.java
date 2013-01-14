package webon;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.TimerTask;

class JVM extends TimerTask {
	public long timestamp = 0;
	public double avgLoad = 0; // System load average for the last minute.
	public long memory = 0; // Java heap in use
	public int classes = 0; // number of loaded classes

	static MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
	static OperatingSystemMXBean osBean = ManagementFactory
			.getOperatingSystemMXBean();
	static ClassLoadingMXBean classBean = ManagementFactory
			.getClassLoadingMXBean();

	public void run() {
		timestamp = System.currentTimeMillis();
		memory = memoryBean.getHeapMemoryUsage().getUsed() >> 20;
		avgLoad = osBean.getSystemLoadAverage();
		classes = classBean.getLoadedClassCount();
	}
}