package com.xintu.smartcar.btphone.utils;

import java.util.Comparator;

import com.xintu.smartcar.btphone.bean.ContactItem;

public class PinyinComparator implements Comparator<ContactItem> {

	public int compare(ContactItem o1, ContactItem o2) {
		try {
			if (o1.getSortletter().equals("@")
					|| o2.getSortletter().equals("#")) {
				return -1;
			} else if (o1.getSortletter().equals("#")
					|| o2.getSortletter().equals("@")) {
				return 1;
			} else {
				return o1.getSortletter().compareTo(o2.getSortletter());
			}
		}
		catch (Exception e) {
			return 1;
		}
	}

}
