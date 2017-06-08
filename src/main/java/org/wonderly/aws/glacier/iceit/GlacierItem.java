package org.wonderly.aws.glacier.iceit;

import java.io.Serializable;

import com.amazonaws.services.glacier.model.DescribeVaultOutput;

public class GlacierItem implements Serializable {
	private static final long serialVersionUID = 1L;
	DescribeVaultOutput out;
	public GlacierItem(DescribeVaultOutput out ) {
		this.out = out;
	}
	
	@Override
	public String toString() {
		return out.getVaultName()+" ("+out.getNumberOfArchives()+" items)";
	}
}
