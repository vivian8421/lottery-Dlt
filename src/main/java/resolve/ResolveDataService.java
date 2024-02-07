package resolve;

import java.util.List;

public class ResolveDataService {
	private static List<List<Integer>> officialList = OfficialDataService.readByFile();

	public static void main(String[] args) throws Exception {
		int hot1 = 0;
		int hot2 = 0;
		int hot4 = 0;
		int hot3 = 0;
		int hot5 = 0;
		int hot6 = 0;
		int hot7 = 0;
		int hot8 = 0;
		int hot9 = 0;
		int hot10 = 0;
		int hot11 = 0;
		int hot12 = 0;
		int hot13 = 0;
		int hot14 = 0;
		int hot15 = 0;
		int hot16 = 0;
		int hot17 = 0;
		int hot18 = 0;
		for (int i = 0; i < officialList.size(); i++) {
			List<Integer> officialOne = officialList.get(i);
			
			if (officialOne.get(0) < 10) {
				hot1++;
			}
			if (officialOne.get(4) / 10 == 3) {
				hot2++;
			}
			if (officialOne.get(2) / 10 == 2) {
				hot4++;
			}
			if (officialOne.get(0) < 10 && officialOne.get(4) / 10 == 3) {
				hot3++;
			}
			if (officialOne.get(0) < 10 && officialOne.get(2) / 10 == 2) {
				hot5++;
			}
			if (officialOne.get(2) / 10 == 2 && officialOne.get(4) / 10 == 3) {
				hot6++;
			}
			if (officialOne.get(1) / 10 == 1) {
				hot7++;
			}
			if (officialOne.get(0) < 10 && officialOne.get(1) / 10 == 1) {
				hot8++;
			}
			if (officialOne.get(1) / 10 == 1 && officialOne.get(2) / 10 == 2) {
				hot9++;
			}
			if (officialOne.get(0) < 10 || officialOne.get(4) / 10 == 3) {
				hot10++;
			}
			if ((officialOne.get(5) + officialOne.get(6)) > 8) {
				hot11++;
			}
			if ((officialOne.get(5) + officialOne.get(6)) < 18) {
				hot12++;
			}
			if (Math.abs(officialOne.get(5) - officialOne.get(6)) < 8) {
				hot13++;
			}
			if (Math.abs(officialOne.get(5) - officialOne.get(6)) > 1) {
				hot14++;
			}
			if (officialOne.get(4) / 10 == 3 && (officialOne.get(3) / 10 == 0 || officialOne.get(3) / 10 == 1)) {
				hot15++;
			}
			if (!(officialOne.get(0) < 10 || officialOne.get(4) / 10 == 3)) {
				hot16++;
			}

			String string1 = officialOne.subList(0, 5).toString();
			for (int j = i + 1; j < officialList.size(); j++) {
				String string2 = officialList.get(j).subList(0, 5).toString();
				if (string1.equals(string2)) {
					hot17++;
				}
			}

			if (officialOne.get(1) / 10 == 1 && officialOne.get(3) / 10 == 3) {
				hot18++;
			}

		}
		System.out.println("第一位0X 的概率>>>" + new Double(hot1) / officialList.size());
		System.out.println("第五位3X 的概率>>>" + new Double(hot2) / officialList.size());
		System.out.println("第一位0X 且 第五位3X 的概率>>>" + new Double(hot3) / officialList.size());
		System.out.println("第三位2X 的概率>>>" + new Double(hot4) / officialList.size());
		System.out.println("第一位0X 且 第三位2X 的概率>>>" + new Double(hot5) / officialList.size());
		System.out.println("第三位2X 且 第五位3X 的概率>>>" + new Double(hot6) / officialList.size());
		System.out.println("第二位1X 的概率>>>" + new Double(hot7) / officialList.size());
		System.out.println("第一位0X 且 第二位1X 的概率>>>" + new Double(hot8) / officialList.size());
		System.out.println("第二位1X 且 第三位2X 的概率>>>" + new Double(hot9) / officialList.size());
		System.out.println("第一位是0X 或 第五位3X 的概率>>>" + new Double(hot10) / officialList.size());
		System.out.println("后两位和大于8 的概率>>>" + new Double(hot11) / officialList.size());
		System.out.println("后两位和小于18 的概率>>>" + new Double(hot12) / officialList.size());
		System.out.println("后两位差小于8 的概率>>>" + new Double(hot13) / officialList.size());
		System.out.println("后两位差大于1 的概率>>>" + new Double(hot14) / officialList.size());
		System.out.println("第五位是3X 且 第四位是0X或第四位是1X 的概率>>>" + new Double(hot15) / officialList.size());
		System.out.println("第一位不是0X 且 第五位不是3X 的概率>>>" + new Double(hot16) / officialList.size());
		System.out.println("前五位跟之前任何一期一样的概率>>> " + new Double(hot17) / officialList.size());
		System.out.println("第二位1X 且 第四位3X 的概率>>> " + new Double(hot18) / officialList.size());
	}
}
