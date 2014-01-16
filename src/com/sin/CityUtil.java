/*
 * create By liuzhi at Dec 23, 2013
 * Copyright HiSupplier.com
 */
package com.sin;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;



import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.junit.Test;

import sun.tools.jar.resources.jar;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
@SuppressWarnings("unchecked")
public class CityUtil {
	public final static String COUNTY_CODE = "http://www.stats.gov.cn/tjsj/tjbz/xzqhdm/201301/t20130118_38316.html";
	public final static Map cityDataMap = doth();
	public final static Map<String, String> provinceMap = (Map<String, String>) cityDataMap.get("province");;
	public final static Map<String, String> cityMap = (Map<String, String>) cityDataMap.get("city");
	public final static Map<String, String> noChildCityMap = (Map<String, String>) cityDataMap.get("noChildCity");
	public final static Map<String, String> countyMap = (Map<String, String>) cityDataMap.get("county");
	public final static List<String> provinces_key = (List<String>) cityDataMap.get("provinces_key");
	public final static Map<String, List<String>> prov_citys_key = (Map<String, List<String>>) cityDataMap.get("prov_citys_key");
	public final static Map<String, List<String>> city_countys_key = (Map<String, List<String>>) cityDataMap.get("city_countys_key");
	
	/**
	 * 是否省级市
	 * @param provinceCode 省份编码
	 * @return boolean 
	 */
	public static boolean isProvincialCity(String provinceCode) {
		boolean con = false;
		String provincialCites [] = {"11", "12", "31", "50"};
		for (int i = 0; i < provincialCites.length; i++) {
			if (provincialCites[i].equals(provinceCode)) {
				con = true;
				break;
			}
		}
		return con;
	}
	
	/**
	 * 获取省份编码
	 * @param code 六位完整地区编码
	 * @return provinceCode 
	 */
	public static String getProvinceCode (String code) {
		return code.substring(0, 2);
	}

	/**
	 * 获取省份名称
	 * @param code 六位完整地区编码,或者省份编码
	 * @return provinceName 无此编码则返回""
	 */
	public static String getProvinceName(String code) {
		String provinceName = provinceMap.get(getProvinceCode(code));
		return provinceName == null ? "" : provinceName;
	}

	/**
	 * 获取市编码,包含一些特殊的市,州（例如：广东省东莞市 是没有区县的）
	 * @param code 六位完整地区编码
	 * @return cityCode 
	 */
	public static String getCityCode(String code) {
		if (isProvincialCity(code.substring(0, 2))) {
			return code.substring(0, 3);
		} else {
			return noChildCityMap.containsKey(code) ? code : code.substring(0, 4);
		}
	}

	/**
	 * 获取市名称,包含一些特殊的市,州（例如：广东省东莞市 是没有区县的）
	 * @param code 六位完整地区编码,或者市编码
	 * @return cityName 无此编码则返回""
	 */
	public static String getCityName(String code) {
		String cityCode = getCityCode(code);
		String cityName = cityCode.length() > 4 ? noChildCityMap.get(cityCode) : cityMap.get(cityCode);
		return cityName == null ? "" : cityName;
	}

	/**
	 * 获取区县编码
	 * @param code 六位完整地区编码
	 * @return countyCode
	 */
	public static String getCountyCode(String code) {
		return countyMap.containsKey(code) ? code : "";
	}

	/**
	 * 获取区县名称
	 * @param code 六位完整地区编码
	 * @return countyName 无区县的市返回""
	 */
	public static String getCountyName(String code) {
		String countyCode = getCountyCode(code);
		return countyCode != "" ? countyMap.get(countyCode) : "";
	}

	/**
	 * 获取指定编码的市编码集合
	 * @param code 六位完整地区编码,或者指定省份编码
	 * @return cityCodeList
	 */
	public static List<String> getCityCodeList (String code) {
		return prov_citys_key.get(getProvinceCode(code));
	}

	/**
	 * 获取指定编码的县区编码集合
	 * @param code 六位完整地区编码,或者指定市编码
	 * @return countyCodeList
	 */
	public static List<String> getCountyCodeList(String code) {
		return city_countys_key.get(getCityCode(code));
	}

	/**
	 * 获取完整地址
	 * @param code 六位完整地区编码
	 * @return address
	 */
	public static String getAddress(String code){
		return getProvinceName(code) + getCityName(code) + getCountyName(code);
	}

	private static String getUserAgent() {
		String[] userAgentList = {"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/22.0.1207.1 Safari/537.1",
								  "Mozilla/5.0 (Windows NT 6.2; Win64; x64; rv:16.0.1) Gecko/20121011 Firefox/16.0.1",
								  "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)"};
		Random r = new Random();
		return userAgentList[1];
	}
	/**
	 * 在线解析统计局发布的最新行政区划代码
	 * 主要逻辑：区分出 省级节点、市级节点、地区节点、还有一些特殊的节点。然后结算各节点之间的数据
	 * @return Map
	 */
	public static Map doth() {
		Map map = new HashMap();
		Map<String, String> provinceMap = new TreeMap<String, String>();//省级（直辖市）code-name； 例（11 ：北京）
		Map<String, String> cityMap = new TreeMap<String, String>();//市级code-name，code一般为4位，省直辖市为3位；例（110 ：北京市，1301：石家庄市）
		Map<String, String> countyMap = new TreeMap<String, String>();//地区code-name； 例（110101 ：东城区）
		Map<String, String> noChildCityMap = new TreeMap<String, String>();//无地区的市级code-name； 例(442000 : 中山市)
		
		List<String> provincialCity = new ArrayList<String>();//3位直辖市code
		List<String> provinces_key = new ArrayList<String>();//省级code（包括直辖市）
		Map<String,List<String>> prov_citys_key = new TreeMap<String, List<String>>();//省（包括直辖市）,市（包括noChildCity）关联code集合
		Map<String, List<String>> city_countys_key = new TreeMap<String, List<String>>();//市，地区关联code集合
		
		String countyParentKey = "";//缓存 地区的父级code(即 市级code，包括直辖市)
		String cityParentKey = "";//缓存 市级的父级code(即 省级code，包括直辖市)
		
		List<String> countyCodeList = new ArrayList<String>();//缓存同一父级市code 的地区code集合
		List<String> cityCodeList = new ArrayList<String>();//缓存同一父级省code 的市级code集合
		List<String> noChildCityCodeList = new ArrayList<String>();//无地区的市级code集合
		
		try {
			Document document = Jsoup.connect(CityUtil.COUNTY_CODE).userAgent(getUserAgent()).get();
			Elements elements = document.select("span");
			//删除不必要的元素
			int j = elements.size();
			for (int i = 0; i < j; i++) {
				String str = Jsoup.clean(elements.get(i).text(), Whitelist.simpleText());
				if (str.equals("") || str.equals("&nbsp;")) {
					elements.remove(i);
					i--;j--;
				}
			} 
			//开始解析
			j = elements.size();
			int countyType = 0;//操作标示
			for (int i = 0; i < j-1; i+=2) {
				Element etCode = elements.get(i);
				Element etName = elements.get(i+1);
				String code = Jsoup.clean(etCode.text(), Whitelist.simpleText());
				String name = Jsoup.clean(etName.text(), Whitelist.simpleText());
				if (i == j-3) {
					noChildCityMap.put(code, name);
					city_countys_key.put(countyParentKey, countyCodeList);
					prov_citys_key.put(cityParentKey, cityCodeList);
				} else {
					if (name.indexOf("市辖区")>-1){
						continue;
					} else {
						if (cityParentKey.equals("")) {//读取第一条信息（默认为北京市）
							cityParentKey = code.substring(0,2);
							countyParentKey = code.substring(0,3);
							cityMap.put(countyParentKey, name);
							cityCodeList.add(countyParentKey);
							provincialCity.add(countyParentKey);
							provinceMap.put(cityParentKey, name);
							provinces_key.add(cityParentKey);
							countyType = 2;
						} else if (code.indexOf("0000") > -1 && !cityParentKey.equals("")) {//省级节点 proviceMap
							city_countys_key.put(countyParentKey, new ArrayList<String>(countyCodeList));
							countyCodeList.clear();

							prov_citys_key.put(cityParentKey, new ArrayList<String>(cityCodeList));
							cityCodeList.clear();
								
							cityParentKey = code.substring(0,2);
							provinceMap.put(cityParentKey, name);
							provinces_key.add(cityParentKey);

							if (name.indexOf("市") > -1) {//直辖市
								countyType = 2;
								countyParentKey = code.substring(0,3);
								provincialCity.add(countyParentKey);
								cityMap.put(countyParentKey, name);
								cityCodeList.add(countyParentKey);
							} else {
								countyType = 0;
								countyParentKey = code.substring(0,4);
							}
						} else if (name.indexOf("行政区划") > -1 ) {//无效数据，但是这条数据标示，此节点到下一省节点之间的数据都为noChildCityMap内
							countyType = 1;
							continue;
						} else if (code.endsWith("00")) {//市级节点 cityMap
							if (name.equals("县")) {
								continue;
							} else {
								if ( Jsoup.clean(elements.get(i+2).text(), Whitelist.simpleText()).endsWith("00") || 
										(Jsoup.clean(elements.get(i+3).text(), Whitelist.simpleText()).indexOf("市辖区") > -1 && (Jsoup.clean(elements.get(i+4).text(), Whitelist.simpleText()).endsWith("00")))) {
									//在市级节点与市级节点之间的特殊情况 数据为noChildCityMap内
									noChildCityMap.put(code, name);
									noChildCityCodeList.add(code);
								} else {
									if (countyCodeList.size() > 0) {
										city_countys_key.put(countyParentKey, new ArrayList<String>(countyCodeList));
										countyCodeList.clear();
									}
									countyParentKey = code.substring(0,4);
									cityMap.put(countyParentKey, name);
									cityCodeList.add(countyParentKey);
									countyType = 0;
								}
							}
						//前面代码块已经过滤掉 市级节点、省级节点，无效的数据，剩下的只有地区节点 和 属于noChildCityMap内的数据
						//按操作标示（counyType)放入不同集合
						} else if (countyType == 1) {
							noChildCityMap.put(code, name);
							noChildCityCodeList.add(code);
						} else {
							countyCodeList.add(code);
							countyMap.put(code, name);
						}
					}	
				}
			}
			//把noChildCity 加入到prov_citys_key中去
			for (int i = 0; i < noChildCityCodeList.size(); i++) {
				List<String> codeList= prov_citys_key.get(noChildCityCodeList.get(i).substring(0,2));
					codeList.add(noChildCityCodeList.get(i));
			}
			//生成对应的JS数据文件 provinces，citys，noChildCitys，countys，prov_city_key，city_county_key，prov_key，provincialCity
			
			map.put("province", provinceMap);
			map.put("city", cityMap);
			map.put("county", countyMap);
			map.put("noChildCity", noChildCityMap);
			map.put("provinces_key", provinces_key);
			map.put("city_countys_key", city_countys_key);
			map.put("prov_citys_key", prov_citys_key);
			//System.out.println(JSONArray.toJSONString(provinces_key));
			//System.out.println(JSONArray.toJSONString(city_countys_key));
			System.out.println(JSONArray.toJSONString(prov_citys_key));
			//System.out.println(JSONArray.toJSONString(noChildCityMap));
			//System.out.println(JSONArray.toJSONString(cityMap));
			//System.out.println(JSONArray.toJSONString(provinceMap));
			//System.out.println(JSONArray.toJSONString(countyMap));
			//System.out.println(JSONArray.toJSONString(provincialCity));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	public static void main(String[] args) {
		System.out.println(CityUtil.getAddress("442000"));
	}
}
