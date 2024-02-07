package resolve;

import resolve.OfficialDataService.OfficialDataResp.LotteryValueItem.LotteryValueListItem;
import support.util.CollectUtil;
import support.util.JsonUtil;
import support.util.StringUtil;
import support.util.file.FileUtil;
import support.util.java.SystemProperty;
import support.util.net.HttpClient;

import java.io.File;
import java.util.List;
import java.util.Map;

public class OfficialDataService {
	private static String officialDataFilePath = SystemProperty.getUserDir() + File.separator + "前期数据.txt";

	public static List<List<Integer>> readByFile() {
		List<List<Integer>> officialList = CollectUtil.getList();
		String officialLotteryStr = FileUtil.content.readFileToStr(officialDataFilePath);
		String[] officialLotteryArray = officialLotteryStr.split("\n");
		for (String officialOneStr : officialLotteryArray) {
			if (StringUtil.isEmpty(officialOneStr)) {
				continue;
			}
			String[] officialOneArray = officialOneStr.replace("\r", "").split(" ");
			List<String> officialOne = CollectUtil.to.ArrayToList(officialOneArray);
			officialList.add(toIntegerList(officialOne));
		}
		return officialList;
	}

	public static void writeToFile() throws Exception {
		Map<String, Object> paramMap = CollectUtil.getMap(String.class, Object.class);
		paramMap.put("gameNo", 85);
		paramMap.put("provinceId", 0);
		paramMap.put("isVerify", 1);
		paramMap.put("termLimits", 0);
		paramMap.put("pageNo", 1);
		paramMap.put("pageSize", 9999);

		String lotteryRespJson = new HttpClient().getString(HttpClient.RequestType.GET, "https://webapi.sporttery.cn/gateway/lottery/getHistoryPageListV1.qry", paramMap);
		OfficialDataResp lotteryResp = JsonUtil.toObject(lotteryRespJson, OfficialDataResp.class);

		List<LotteryValueListItem> list = lotteryResp.getValue().getList();

		List<String> totalStrList = CollectUtil.item.getPropertyList(list, "lotteryDrawResult");

		StringBuilder content = new StringBuilder();
		for (int i = 0; i < totalStrList.size(); i++) {
			content.append(totalStrList.get(i) + "\n");
		}

		File file = new File(officialDataFilePath);
		System.out.println(file.getPath());
		if (FileUtil.isExists(file)) {
			FileUtil.opt.delete(file);
		}
		FileUtil.opt.create(file);
		FileUtil.content.writeStrToFile1(file.getPath(), content.toString());
	}

	public static List<Integer> toIntegerList(List<String> theOne) {
		List<Integer> list = CollectUtil.getList();
		for (String item : theOne) {
			list.add(Integer.parseInt(item));
		}
		return list;
	}

	public static class OfficialDataResp {
		private LotteryValueItem value;

		public LotteryValueItem getValue() {
			return value;
		}
		public void setValue(LotteryValueItem value) {
			this.value = value;
		}

		public static class LotteryValueItem {
			private List<LotteryValueListItem> list;

			public List<LotteryValueListItem> getList() {
				return list;
			}
			public void setList(List<LotteryValueListItem> list) {
				this.list = list;
			}

			public static class LotteryValueListItem {
				private String lotteryDrawResult;
				public String getLotteryDrawResult() {
					return lotteryDrawResult;
				}
				public void setLotteryDrawResult(String lotteryDrawResult) {
					this.lotteryDrawResult = lotteryDrawResult;
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		writeToFile();
	}
}
