package org.ironrhino.activiti.action;

public class Tuple<K, V> {

	private String id;

	private K key;

	private V value;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

}
