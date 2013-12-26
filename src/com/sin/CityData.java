/*
 * create By liuzhi at Dec 23, 2013
 * Copyright HiSupplier.com
 */
package com.sin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class CityData {
	private Map<String, String> province = new LinkedHashMap<String, String>();;
	private Map<String, String> city = new LinkedHashMap<String, String>();
	private Map<String, String> noChildCity = new LinkedHashMap<String, String>();
	private Map<String, String> county = new LinkedHashMap<String, String>();
	private List<String> provinces_key =  new ArrayList<String>();
	private Map<String, List<String>> city_countys_key = new LinkedHashMap<String, List<String>>();
	private Map<String, List<String>> prov_citys_key = new LinkedHashMap<String, List<String>>();

	@SuppressWarnings("unchecked")
	public Map getData (){
		Map map = new HashMap();
		map.put("province", province);
		map.put("city", city);
		map.put("county", county);
		map.put("noChildCity", noChildCity);
		map.put("provinces_key", provinces_key);
		map.put("city_countys_key", city_countys_key);
		map.put("prov_citys_key", prov_citys_key);
		return map;
	}

	public Map<String, String> getProvince() {
		return province;
	}
	public void setProvince(LinkedHashMap<String, String> province) {
		this.province = province;
	}
	public Map<String, String> getCity() {
		return city;
	}
	public void setCity(LinkedHashMap<String, String> city) {
		this.city = city;
	}
	public Map<String, String> getNoChildCity() {
		return noChildCity;
	}
	public void setNoChildCity(LinkedHashMap<String, String> noChildCity) {
		this.noChildCity = noChildCity;
	}
	public Map<String, String> getCounty() {
		return county;
	}
	public void setCounty(LinkedHashMap<String, String> county) {
		this.county = county;
	}
	public List<String> getProvinces_key() {
		return provinces_key;
	}
	public void setProvinces_key(List<String> provinces_key) {
		this.provinces_key = provinces_key;
	}
	public Map<String, List<String>> getCity_countys_key() {
		return city_countys_key;
	}
	public void setCity_countys_key(LinkedHashMap<String, List<String>> city_countys_key) {
		this.city_countys_key = city_countys_key;
	}
	public Map<String, List<String>> getProv_citys_key() {
		return prov_citys_key;
	}
	public void setProv_citys_key(LinkedHashMap<String, List<String>> prov_citys_key) {
		this.prov_citys_key = prov_citys_key;
	}
}
