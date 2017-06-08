package org.wonderly.aws.glacier.iceit;

import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.wonderly.swing.*;

import com.amazonaws.services.glacier.*;
import com.amazonaws.services.glacier.model.*;

import org.wonderly.awt.Packer;

public class IceITMain extends JFrame {
	private static Logger log = Logger.getLogger( IceITMain.class.getName() );
	private static final long serialVersionUID = 1L;
	AmazonGlacier gl;
	private JList<GlacierItem> vaultsList;
	private ListListModel<GlacierItem> vaultsModel;
	private ListListModel<VaultItem> itemModel;
	private JList<VaultItem> itemList;
	public static void main( String args[] ) {
		new IceITMain(args);
	}
	
	public IceITMain( String args[] ) {
		super("IceIT - Glacier");
		gl = AmazonGlacierClientBuilder.defaultClient();
		ListVaultsRequest listVaultsRequest = new ListVaultsRequest();
		ListVaultsResult res = gl.listVaults(listVaultsRequest );
		Packer pk = new Packer( this );
		JPanel ip = new JPanel();
		Packer ipk = new Packer( ip );
		ip.setBorder( BorderFactory.createTitledBorder("Vault Items"));
		ipk.pack( new JScrollPane( itemList = new JList<VaultItem>( itemModel = new ListListModel<VaultItem>())));
		JPanel vlp = new JPanel();
		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT );
		sp.setLeftComponent(vlp);
		sp.setRightComponent(ip);
		pk.pack( sp ).fillboth();
		vlp.setBorder( BorderFactory.createTitledBorder("Vaults"));
		List<DescribeVaultOutput> l = res.getVaultList();
		List<GlacierItem> glst = new ArrayList<GlacierItem>();
		for( DescribeVaultOutput o : l ) {
			glst.add( new GlacierItem(o) );
		}
		Packer lpk = new Packer(vlp);
		lpk.pack( new JScrollPane( vaultsList = new JList<GlacierItem>( vaultsModel = new ListListModel<GlacierItem>(glst)))).gridx(0).gridy(0).fillboth();
		JPanel vbp = new JPanel();
		lpk.pack( vbp ).gridx(1).gridy(0).filly().inset(new Insets(0,3,3,3));
		Packer vbpk = new Packer( vbp );
		int y = -1;
		vbpk.pack( new JButton("Create...") ).gridx(0).gridy(++y).fillx(0).inset(0,0,2,0);
		vbpk.pack( new JButton("Edit...") ).gridx(0).gridy(++y).fillx(0).inset(2,0,2,0);
		vbpk.pack( new JButton("Remove") ).gridx(0).gridy(++y).fillx(0).inset(2,0,4,0);
		vbpk.pack( new JPanel() ).gridx(0).gridy(++y).filly();
		
		vaultsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if(arg0.getValueIsAdjusting())
					return;
				GlacierItem itm = vaultsList.getSelectedValue();
				log.info("See selected: "+itm);
				ListPartsRequest r = new ListPartsRequest();
				gl.
				r.setVaultName(itm.out.getVaultName());
				new SyncThread<ListPartsResult,ListPartsResult>(vaultsList, itemList) {
					public ListPartsResult run() {
						try {
							log.info("getting tags for vault with: "+r);
							return gl.listParts(r)
							//return gl.listTagsForVault(r);
						} catch( Exception ex) {
							reportException(ex);
						}
						return null;
					}
					public void done() {
						ListPartsResult res = getValue();
						log.info("Set vault result: "+res);
						List<VaultItem> vl = new ArrayList<VaultItem>();
						for( String k : res.().keySet() ) {
							vl.add(new VaultItem(k,res.getTags().get(k)));
						}
						itemModel.setContents(vl);
					}
				}.start();
			}
		});
		
		itemList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
			
			}	
		});
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void reportException(Throwable ex) {
		log.log( Level.SEVERE, ex.toString(), ex );
	}
}
