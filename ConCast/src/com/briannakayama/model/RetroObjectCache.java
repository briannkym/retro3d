package com.briannakayama.model;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class RetroObjectCache implements Cache<RetroObject>{

	public static final int NULL_POINTER = -1;
	
	private List<RetroObject> cache;
	private Deque<Integer> removed;
	
	public RetroObjectCache(int length){
		this.cache = new ArrayList<RetroObject>(length);
		this.removed = new ArrayDeque<Integer>();
	}

	@Override
	public int register(RetroObject k) {
		switch(k.cacheIndex){
		case -1:
			if (removed.isEmpty()){
				int index = cache.size();
				k.cacheIndex = index;
				cache.add(k);
				return index;
			} else {
				int index = removed.pop();
				k.cacheIndex = index;
				cache.set(index, k);
				return index;
			}
		default:
			if (k.equals(cache.get(k.cacheIndex))){
				return k.cacheIndex;
			}
		}
		return NULL_POINTER;
	}

	@Override
	public boolean remove(RetroObject k) {
		if (k.equals(cache.get(k.cacheIndex))){
			removed.add(k.cacheIndex);
			k.cacheIndex = NULL_POINTER;
			return true;
		}
		return false;
	}

	@Override
	public boolean remove(int index) {
		RetroObject k;
		if ((k = cache.get(index))!=null){
			k.cacheIndex = NULL_POINTER;
			removed.add(index);
			cache.set(index, null);
			return true;
		}
		return false;
	}
	
	public static RetroObject load(File f){
		
	}
	
	
	public boolean save(File f){
		
	}
	
	
}
