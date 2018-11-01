import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;

public class app {

	static final String serverHost = "151.101.65.69";
	static final int numberOfConcurrentConnections = 3000;
	static final int maxopenSocketCall = 60000;
	static int openSocketCallCount = 0;
	static public Semaphore smp = new Semaphore(numberOfConcurrentConnections);
	static ThreadPoolExecutor tp = (ThreadPoolExecutor) Executors.newFixedThreadPool(2000);
	
	public static void main(String[] args) {
		try {
			for(int i  = 0 ; i < maxopenSocketCall ; i++) {
				openConnectionAndDoStuff();
			}
			System.in.read();
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}
	
	public static void openConnectionAndDoStuff() throws UnknownHostException, IOException {
		tp.submit(() -> {
			try {
				app.smp.acquire();
				Socket socketOfClient = new Socket(serverHost, 80);
				socketOfClient.close();
				increaseOpenSocketCallCountAndPrint();
			} catch (InterruptedException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			finally {
				smp.release();
			}
		});
	}
	
	public static void simulateOpenConnectionAndDoStuff() throws InterruptedException {
		tp.submit(() -> {
			try {
				app.smp.acquire();
				Thread.sleep(5000);
				app.increaseOpenSocketCallCountAndPrint();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				smp.release();	
			}
		});
	}
	
	static Object lockObj = new Object();
	public static void increaseOpenSocketCallCountAndPrint() {
		synchronized (lockObj) {
			openSocketCallCount++;
			System.out.println(openSocketCallCount);	
		}
	}
	
}
