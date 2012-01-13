import java.io.File;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;

/**
 * @author Michael Leo
 * @version 2010/08/12
 */
public class Weather {

	private static final String ENCODE = "GB2312";

	private static final String NO_DATA = "暂无数据";

	private static final String NO_LIVE_DATA = "暂无实况";

	private static StringBuffer file_data = new StringBuffer();

	public static void main(String[] args) throws Exception {
		// Weather forecast url
		String url = "http://www.weather.com.cn/html/weather/101070201.shtml";

		// Live data url
		String liveDataHtml = "http://www.weather.com.cn/data/sk/101070201.html";
		String liveJson = ParseUtils.getContent(liveDataHtml);

		// Get live data bean
		@SuppressWarnings("unchecked")
		Map<String, Map<String, String>> rootMap = JSONObject
				.fromObject(liveJson);
		Map<String, String> liveMap = rootMap.get("weatherinfo");
		WeatherInfoBean wib = new WeatherInfoBean();
		wib.setCity(liveMap.get("city"));
		wib.setTime(liveMap.get("time"));

		String temp = liveMap.get("temp");
		if (!NO_LIVE_DATA.equals(liveMap.get("temp"))) {
			temp += "℃";
		}
		wib.setTemperature(temp);
		wib.setHumidity(liveMap.get("SD"));
		if (null == liveMap.get("AP")) {
			wib.setAtmosphericPressure(NO_DATA);
		} else {
			wib.setAtmosphericPressure(liveMap.get("AP"));
		}
		wib.setWindDirection(liveMap.get("WD"));
		wib.setWindForce(liveMap.get("WS"));

		if (null == liveMap.get("sm")) {
			wib.setWindSpeed(NO_DATA);
		} else {
			wib.setWindSpeed(liveMap.get("sm") + "m/s");
		}

		// Title
		temp = "================ " + wib.getCity() + "天气预报 ================";
		System.out.println(temp);
		file_data.append(temp + "\r\n");
		temp = "================  中国气象局  ================\r\n";
		System.out.println(temp);
		file_data.append(temp + "\r\n");

		// Display live data
		StringBuffer sb = new StringBuffer();
		temp = ParseUtils.getElementByTagName(url, "DT")[0];
		System.out.println(temp);
		file_data.append(temp + "\r\n");
		sb.append("最新实况: ");
		sb.append(wib.getCity()).append(" (").append(wib.getTime()).append(")");
		System.out.println(sb.toString());
		file_data.append(sb.toString() + "\r\n");

		temp = "---------------------------------------------";
		System.out.println(temp);
		file_data.append(temp + "\r\n");

		sb = new StringBuffer();
		sb.append("气温： ").append(wib.getTemperature()).append('\t');
		sb.append("温度： ").append(wib.getHumidity()).append('\t');
		sb.append("气压： ").append(wib.getAtmosphericPressure());
		System.out.println(sb.toString());
		file_data.append(sb.toString() + "\r\n");

		sb = new StringBuffer();
		sb.append("风向： ").append(wib.getWindDirection()).append('\t');
		sb.append("风力： ").append(wib.getWindForce()).append('\t');
		sb.append("风速： ").append(wib.getWindSpeed());
		System.out.println(sb.toString());
		file_data.append(sb.toString() + "\r\n");

		temp = "---------------------------------------------\r\n";
		System.out.println(temp);
		file_data.append(temp + "\r\n");

		// -----------------------------------------------------

		// Get weather forecast information

		String futureHtml = ParseUtils.getElementByAttr(url, "class",
				"weatherYubaoBox");

		String tds = ParseUtils.getTableInfo(futureHtml, ENCODE);

		String[] tdArray = tds.split("\r\n");
		for (int i = 0; i < tdArray.length; i++) {
			if (tdArray[i].contains("级")) {
				System.out.print(tdArray[i]);
				System.out.println();
				file_data.append(tdArray[i] + "\r\n");
				continue;
			} else if (tdArray[i].contains("星期")) {
				if (i != 0) {
					System.out
							.println("---------------------------------------------");
					file_data
							.append("---------------------------------------------\r\n");
				}
				System.out.print(tdArray[i]);
				System.out.println();
				file_data.append(tdArray[i] + "\r\n");
				continue;
			}
			System.out.print(tdArray[i]);
			System.out.print('\t');
			file_data.append(tdArray[i] + "\t");
		}

		FileUtils.writeStringToFile(new File(System.getProperty("user.dir")
				+ "/weatherforecast.txt"), file_data.toString(), "UTF-8");
	}
}
