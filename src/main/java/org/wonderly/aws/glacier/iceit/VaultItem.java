package org.wonderly.aws.glacier.iceit;

import java.io.Serializable;

public class VaultItem implements Serializable {
	String value;
	String key;

	public VaultItem(String k, String string) {
		key = k;
		value = string;
	}

	@Override
	public String toString() {
		return key+"="+value;
	}
	private static final long serialVersionUID = 1L;

}
