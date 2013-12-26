/*
 * create By liuzhi at Dec 23, 2013
 * Copyright HiSupplier.com
 */
package com.sin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;


import com.alibaba.fastjson.JSON;
@SuppressWarnings("unchecked")
public class CityUtil {

	public final static Map cityDataMap = doth();
	public final static Map<String, String> provinceMap = (Map<String, String>) cityDataMap.get("province");;
	public final static Map<String, String> cityMap = (Map<String, String>) cityDataMap.get("city");
	public final static Map<String, String> noChildCityMap = (Map<String, String>) cityDataMap.get("noChildCity");
	public final static Map<String, String> countyMap = (Map<String, String>) cityDataMap.get("county");
	public final static List<String> provinces_key = (List<String>) cityDataMap.get("provinces_key");
	public final static Map<String, List<String>> prov_citys_key = (Map<String, List<String>>) cityDataMap.get("prov_citys_key");
	public final static Map<String, List<String>> city_countys_key = (Map<String, List<String>>) cityDataMap.get("city_countys_key");
	/**
	 * ��ȡ����json�ļ�
	 * @param path �ļ�·��
	 * @return Content �ļ�����
	 */
	public static String readFile(String Path) {
		File file = new File(Path);
		String fileContent = "";
		try {
			InputStreamReader in = new InputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader bf = new BufferedReader(in);
			String temp;
			while ((temp = bf.readLine()) != null) {
				if (!temp.contains("//")) {
					fileContent += temp + "\n";
				}
			}
			bf.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileContent;
	}

	public static Map doth() {
		String data = readFile(System.getProperty("user.dir") + "/WebRoot/cityData.txt");
		CityData cityData = JSON.parseObject(data, CityData.class);
		return cityData.getData();
	}
	
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

	public static void main(String[] args) {
		String code = "4290";
		//String code = "2302";

		System.out.println("ʡ:" + getProvinceName(code));
		System.out.println("��:" + getCityName(code));
		System.out.println("��:" + getCountyCode(code));
		System.out.println(getAddress(code));
	}
}
