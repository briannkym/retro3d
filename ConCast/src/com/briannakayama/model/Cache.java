package com.briannakayama.model;

public interface Cache<K> {

	public int register(K k);
	public boolean remove(K k);
	public boolean remove(int index);
}
