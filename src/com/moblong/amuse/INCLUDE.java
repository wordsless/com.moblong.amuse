package com.moblong.amuse;

public final class INCLUDE implements IOperator<String> {

	class InterestingItem {
		private String title, relation;
	}
	
	private String[] items;
	
	@Override
	public boolean operat(final String data) {
		for(String item : items) {
			if(item.equals(data))
				return true;
		}
		return false;
	}
}
