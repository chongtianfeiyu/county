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
	 * �Ƿ�ʡ����
	 * @param provinceCode ʡ�ݱ���
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
	 * ��ȡʡ�ݱ���
	 * @param code ��λ������������
	 * @return provinceCode 
	 */
	public static String getProvinceCode (String code) {
		return code.substring(0, 2);
	}

	/**
	 * ��ȡʡ������
	 * @param code ��λ������������,����ʡ�ݱ���
	 * @return provinceName �޴˱����򷵻�""
	 */
	public static String getProvinceName(String code) {
		String provinceName = provinceMap.get(getProvinceCode(code));
		return provinceName == null ? "" : provinceName;
	}

	/**
	 * ��ȡ�б���,����һЩ�������,�ݣ����磺�㶫ʡ��ݸ�� ��û�����صģ�
	 * @param code ��λ������������
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
	 * ��ȡ������,����һЩ�������,�ݣ����磺�㶫ʡ��ݸ�� ��û�����صģ�
	 * @param code ��λ������������,�����б���
	 * @return cityName �޴˱����򷵻�""
	 */
	public static String getCityName(String code) {
		String cityCode = getCityCode(code);
		String cityName = cityCode.length() > 4 ? noChildCityMap.get(cityCode) : cityMap.get(cityCode);
		return cityName == null ? "" : cityName;
	}

	/**
	 * ��ȡ���ر���
	 * @param code ��λ������������
	 * @return countyCode
	 */
	public static String getCountyCode(String code) {
		return countyMap.containsKey(code) ? code : "";
	}

	/**
	 * ��ȡ��������
	 * @param code ��λ������������
	 * @return countyName �����ص��з���""
	 */
	public static String getCountyName(String code) {
		String countyCode = getCountyCode(code);
		return countyCode != "" ? countyMap.get(countyCode) : "";
	}

	/**
	 * ��ȡָ��������б��뼯��
	 * @param code ��λ������������,����ָ��ʡ�ݱ���
	 * @return cityCodeList
	 */
	public static List<String> getCityCodeList (String code) {
		return prov_citys_key.get(getProvinceCode(code));
	}

	/**
	 * ��ȡָ��������������뼯��
	 * @param code ��λ������������,����ָ���б���
	 * @return countyCodeList
	 */
	public static List<String> getCountyCodeList(String code) {
		return city_countys_key.get(getCityCode(code));
	}

	/**
	 * ��ȡ������ַ
	 * @param code ��λ������������
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
	 * ���߽���ͳ�ƾַ���������������������
	 * ��Ҫ�߼������ֳ� ʡ���ڵ㡢�м��ڵ㡢�����ڵ㡢����һЩ����Ľڵ㡣Ȼ�������ڵ�֮�������
	 * @return Map
	 */
	public static Map doth() {
		Map map = new HashMap();
		Map<String, String> provinceMap = new TreeMap<String, String>();//ʡ����ֱϽ�У�code-name�� ����11 ��������
		Map<String, String> cityMap = new TreeMap<String, String>();//�м�code-name��codeһ��Ϊ4λ��ʡֱϽ��Ϊ3λ������110 �������У�1301��ʯ��ׯ�У�
		Map<String, String> countyMap = new TreeMap<String, String>();//����code-name�� ����110101 ����������
		Map<String, String> noChildCityMap = new TreeMap<String, String>();//�޵������м�code-name�� ��(442000 : ��ɽ��)
		
		List<String> provincialCity = new ArrayList<String>();//3λֱϽ��code
		List<String> provinces_key = new ArrayList<String>();//ʡ��code������ֱϽ�У�
		Map<String,List<String>> prov_citys_key = new TreeMap<String, List<String>>();//ʡ������ֱϽ�У�,�У�����noChildCity������code����
		Map<String, List<String>> city_countys_key = new TreeMap<String, List<String>>();//�У���������code����
		
		String countyParentKey = "";//���� �����ĸ���code(�� �м�code������ֱϽ��)
		String cityParentKey = "";//���� �м��ĸ���code(�� ʡ��code������ֱϽ��)
		
		List<String> countyCodeList = new ArrayList<String>();//����ͬһ������code �ĵ���code����
		List<String> cityCodeList = new ArrayList<String>();//����ͬһ����ʡcode ���м�code����
		List<String> noChildCityCodeList = new ArrayList<String>();//�޵������м�code����
		
		try {
			Document document = Jsoup.connect(CityUtil.COUNTY_CODE).userAgent(getUserAgent()).get();
			Elements elements = document.select("span");
			//ɾ������Ҫ��Ԫ��
			int j = elements.size();
			for (int i = 0; i < j; i++) {
				String str = Jsoup.clean(elements.get(i).text(), Whitelist.simpleText());
				if (str.equals("") || str.equals("&nbsp;")) {
					elements.remove(i);
					i--;j--;
				}
			} 
			//��ʼ����
			j = elements.size();
			int countyType = 0;//������ʾ
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
					if (name.indexOf("��Ͻ��")>-1){
						continue;
					} else {
						if (cityParentKey.equals("")) {//��ȡ��һ����Ϣ��Ĭ��Ϊ�����У�
							cityParentKey = code.substring(0,2);
							countyParentKey = code.substring(0,3);
							cityMap.put(countyParentKey, name);
							cityCodeList.add(countyParentKey);
							provincialCity.add(countyParentKey);
							provinceMap.put(cityParentKey, name);
							provinces_key.add(cityParentKey);
							countyType = 2;
						} else if (code.indexOf("0000") > -1 && !cityParentKey.equals("")) {//ʡ���ڵ� proviceMap
							city_countys_key.put(countyParentKey, new ArrayList<String>(countyCodeList));
							countyCodeList.clear();

							prov_citys_key.put(cityParentKey, new ArrayList<String>(cityCodeList));
							cityCodeList.clear();
								
							cityParentKey = code.substring(0,2);
							provinceMap.put(cityParentKey, name);
							provinces_key.add(cityParentKey);

							if (name.indexOf("��") > -1) {//ֱϽ��
								countyType = 2;
								countyParentKey = code.substring(0,3);
								provincialCity.add(countyParentKey);
								cityMap.put(countyParentKey, name);
								cityCodeList.add(countyParentKey);
							} else {
								countyType = 0;
								countyParentKey = code.substring(0,4);
							}
						} else if (name.indexOf("��������") > -1 ) {//��Ч���ݣ������������ݱ�ʾ���˽ڵ㵽��һʡ�ڵ�֮������ݶ�ΪnoChildCityMap��
							countyType = 1;
							continue;
						} else if (code.endsWith("00")) {//�м��ڵ� cityMap
							if (name.equals("��")) {
								continue;
							} else {
								if ( Jsoup.clean(elements.get(i+2).text(), Whitelist.simpleText()).endsWith("00") || 
										(Jsoup.clean(elements.get(i+3).text(), Whitelist.simpleText()).indexOf("��Ͻ��") > -1 && (Jsoup.clean(elements.get(i+4).text(), Whitelist.simpleText()).endsWith("00")))) {
									//���м��ڵ����м��ڵ�֮���������� ����ΪnoChildCityMap��
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
						//ǰ�������Ѿ����˵� �м��ڵ㡢ʡ���ڵ㣬��Ч�����ݣ�ʣ�µ�ֻ�е����ڵ� �� ����noChildCityMap�ڵ�����
						//��������ʾ��counyType)���벻ͬ����
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
			//��noChildCity ���뵽prov_citys_key��ȥ
			for (int i = 0; i < noChildCityCodeList.size(); i++) {
				List<String> codeList= prov_citys_key.get(noChildCityCodeList.get(i).substring(0,2));
					codeList.add(noChildCityCodeList.get(i));
			}
			//���ɶ�Ӧ��JS�����ļ� provinces��citys��noChildCitys��countys��prov_city_key��city_county_key��prov_key��provincialCity
			
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
