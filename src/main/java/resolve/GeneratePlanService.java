package resolve;

import support.util.CollectUtil;
import support.util.file.FileUtil;
import support.util.java.SystemProperty;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.Set;



public class GeneratePlanService {

	private static int getRandom(int bound) {
		if (bound == 0) {
			return 1;
		}
		return new Random().nextInt(bound) + 1;
	}

	private static List<Integer> getItemPool(int start, int end) {
		List<Integer> list = CollectUtil.getList();
		for (int i = start; i <= end; i++) {
			list.add(i);
		}
		return list;
	}

	private static List<List<Integer>> officialList = OfficialDataService.readByFile();
	private static List<List<Integer>> generateFrontList = CollectUtil.getList();
	private static List<List<Integer>> generateAfterList = CollectUtil.getList();

	private static List<Integer> generate_one_front() {
		List<Integer> pool1 = getItemPool(1, 35);
		List<Integer> front = CollectUtil.getList();
		for (int i = 0; i < 5; i++) {
			Integer item = pool1.get(getRandom(pool1.size()) - 1);
			CollectUtil.opt.CollectSubtract(pool1, item);
			front.add(item);
		}

		return CollectUtil.sort.NumberSort(front);
	}
	private static List<Integer> generate_one_after() {
		List<Integer> pool2 = getItemPool(1, 12);
		List<Integer> after = CollectUtil.getList();
		for (int i = 6; i <= 7; i++) {
			int item = pool2.get(getRandom(pool2.size()) - 1);
			CollectUtil.opt.CollectSubtract(pool2, item);
			after.add(item);
		}

		return CollectUtil.sort.NumberSort(after);
	}

	private static boolean checkFront(List<Integer> front) {
		if (front.get(4) / 10 == 3 && (front.get(3) / 10 == 0 || front.get(3) / 10 == 1)) {
			return false;// 第五位是3X 且 第四位是0X或第四位是1X 的概率>>>0.06765604539502401
		}
		if (front.get(1) / 10 == 1 && front.get(2) / 10 == 2 && new Random().nextDouble() < (0.223 / 2)) {
			return false;// 第二位1X 且 第三位是2X 的概率>>>0.22304670449585334
		}
		if (front.get(0) >= 10 && front.get(4) / 10 != 3) {
			return false;// 第一位不是0X 且 第五位不是3X 的概率>>>0.04321257092972501
		}
		if (front.get(1) / 10 == 1 && front.get(3) / 10 == 3) {
			return false;// 第二位1X 且 第四位3X 的概率>>> 0.11217808817110432
		}
		if (front.get(0) / 10 != 0 && new Random().nextDouble() < (1 - 0.78) / 2) {
			return false;// 第一位0X 的概率>>>0.7800087298123091
		}

		Set<Integer> collectIntersection = CollectUtil.opt.CollectIntersection(officialList.get(0).subList(0, 5), front);
		if (collectIntersection.size() > 0) {
			return false;
		}

		for (int i = 1; i < 4; i++) {
			Integer index = front.get(i);
			if (officialList.get(1).get(i).equals(index)) {
				return false;
			}
			if (officialList.get(2).get(i).equals(index)) {
				return false;
			}
		}

		String string1 = front.toString();
		for (int i = 0; i < officialList.size(); i++) {
			String string2 = officialList.get(i).subList(0, 5).toString();
			if (string1.equals(string2)) {
				return false;
			}
		}

		for (int i = 1; i < 7; i++) {
			Set<Integer> alikeItem = CollectUtil.opt.CollectIntersection(officialList.get(i).subList(0, 5), front);
			if (alikeItem.size() > 2) {
				return false;
			}
		}

		return true;
	}

	private static boolean checkAfter(List<Integer> after) {
		if (!((after.get(0) + after.get(1)) > 8)) {
			return false;// 后两位和大于8 的概率>>>0.8319511130510694
		}
		if (!((after.get(0) + after.get(1)) < 18)) {
			return false;// 后两位和小于18 的概率>>>0.8123090353557398
		}
		if (!(Math.abs(after.get(0) - after.get(1)) < 8)) {
			return false;// 后两位差小于8 的概率>>>0.8559580969009166
		}
		if (!(Math.abs(after.get(0) - after.get(1)) > 1)) {
			return false;// 后两位差大于1 的概率>>>0.8262767350501964
		}
		
		for (int i = 0; i < 5; i++) {
			Set<Integer> alikeItem = CollectUtil.opt.CollectIntersection(officialList.get(i).subList(5, 7), after);
			if (alikeItem.size() > 1) {
				return false;
			}
		}

		return true;
	}

	private static void generatePlan(int gnum) throws Exception {
		for (int i = 0; i < gnum; i++) {
			List<Integer> one_front = generate_one_front();
			if (!checkFront(one_front)) {
				i--;
			} else {
				generateFrontList.add(one_front);
			}
		}
		for (int i = 0; i < gnum; i++) {
			List<Integer> one_after = generate_one_after();
			if (!checkAfter(one_after)) {
				i--;
			} else {
				generateAfterList.add(one_after);
			}
		}

		StringBuilder content = new StringBuilder();
		for (int i = 0; i < gnum; i++) {
			List<Integer> one_front = generateFrontList.get(i);
			List<Integer> one_after = generateAfterList.get(i);

			content.append("第" + (i + 1 < 10 ? "0" + (i + 1) : (i + 1)) + "组: ");
			content.append("[ ");
			for (Integer item : one_front) {
				content.append(item < 10 ? "0" + item : item);
				content.append(" , ");
			}
			content.delete(content.length() - 2, content.length());
			content.append("] ");
			content.append("[ ");
			for (Integer item : one_after) {
				content.append(item < 10 ? "0" + item : item);
				content.append(" , ");
			}
			content.delete(content.length() - 2, content.length());
			content.append("] ");
			content.append("\n");
		}

		System.out.println(content);

		File file = new File(SystemProperty.getUserDir() + File.separator + "购买计划" + ".txt");
		if (FileUtil.isExists(file)) {
			FileUtil.opt.delete(file);
		}
		FileUtil.opt.create(file);
		FileUtil.content.writeStrToFile1(file.getPath(), content.toString());
	}

	public static void main(String[] args) throws Exception {
		generatePlan(50);
	}
}
