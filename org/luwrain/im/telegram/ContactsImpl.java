package org.luwrain.im.telegram;

import org.luwrain.im.Contacts;
import org.luwrain.im.Events;
import org.telegram.api.contacts.TLContactsFound;
import org.telegram.api.functions.contacts.TLRequestContactsSearch;

public class ContactsImpl implements Contacts {

	private AuthImpl auth;

	public ContactsImpl(AuthImpl auth) {
		this.auth = auth;
	}

	public void Seach(String q) {
		final ContactsImpl that=this;
		TLRequestContactsSearch cntcs = new TLRequestContactsSearch();
		cntcs.setQ(q);
		TLContactsFound rescnts;
		try {
			rescnts = auth.getApi().doRpcCallSide(cntcs);
		} catch (Exception e) {
			that.auth.getEvents().onError(e.getMessage());
        	return;
		} 
		System.out.println("contacts " + rescnts.getUsers().size());
	}

}
