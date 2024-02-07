package support.util.java;

import support.util.CollectUtil;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author zhuangly
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ExecutorUtil {
	public static void executeRunnable(int corePoolSize, int maxPoolSize, long timeoutSeconds, Runnable... runnables) {
		if (corePoolSize <= 0 || maxPoolSize <= 0 || CollectUtil.isEmpty(runnables)) {
			return;
		}
		ExecutorService executorService = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());

		for (Runnable runnable : runnables) {
			executorService.execute(runnable);
		}
		//等待全部完成后关闭，但不是立即关闭
		executorService.shutdown();
		try {
			//最长执行时间，吊用此方法会被阻塞
			//执行Callable时不会生效，因为Callable。get会阻塞，线程池会在全部get完之后才会执行关闭
			if (executorService.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
				executorService.shutdownNow();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			executorService = null;
		}
	}

	public static <T> List<T> executeCallable(int corePoolSize, int maxPoolSize, long timeoutSeconds, Callable... callables) {
		if (corePoolSize <= 0 || maxPoolSize <= 0 || CollectUtil.isEmpty(callables)) {
			return CollectUtil.getEmptyList();
		}
		ExecutorService executorService = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
		List<T> result = CollectUtil.getList();
		try {
			//最长执行时间
			List<Future<T>> invokeAll = executorService.invokeAll((List) CollectUtil.to.ArrayToList(callables), timeoutSeconds, TimeUnit.SECONDS);
			for (Future<T> future : invokeAll) {
				result.add(future.get());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			//等待全部完成后关闭，但不是立即关闭
			executorService.shutdown();
			executorService = null;
		}
		return result;
	}
}
