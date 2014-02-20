package net.ion.talk.handler.craken;

import java.io.IOException;
import java.util.Map;

import net.ion.craken.listener.WorkspaceListener;
import net.ion.craken.node.Workspace;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.craken.tree.TreeNodeKey;
import net.ion.message.push.sender.Sender;
import net.ion.talk.TalkEngine;
import net.ion.talk.bean.Const.User;
import net.ion.talk.responsebuilder.TalkResponse;
import net.ion.talk.responsebuilder.TalkResponseBuilder;

import org.infinispan.atomic.AtomicMap;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;

/**
 * Created with IntelliJ IDEA. User: Ryun Date: 2014. 2. 11. Time: 오후 2:08 To change this template use File | Settings | File Templates.
 */

@Listener
public class NotificationListener implements WorkspaceListener{

	private TalkEngine tengine;
	private Sender sender;
	private String memberId;

	public NotificationListener(TalkEngine tengine, Sender sender) throws IOException {
		this.tengine = tengine;
		this.sender = sender;
	}

	@CacheEntryModified
	public void modified(CacheEntryModifiedEvent<TreeNodeKey, AtomicMap<PropertyId, PropertyValue>> event) {

		if (event.isPre()) return ;
		if (! event.getKey().getType().isData()) return ;
		
		String pattern = "/notifies/{userId}/{notifyId}";
		if (event.getKey().getFqn().isPattern(pattern)){
			
			Map<String, String> resolveMap = event.getKey().getFqn().resolve(pattern) ;
			final String userId = resolveMap.get("userId");
			final String notifyId = resolveMap.get("notifyId");
			
			AtomicMap<PropertyId, PropertyValue> pmap = event.getValue() ;
			PropertyValue pvalue = pmap.get(PropertyId.fromIdString(User.DelegateServer)) ;
			
			if(pvalue != null && pvalue.stringValue().equals(this.memberId)){
				TalkResponse tresponse = TalkResponseBuilder.create().newInner().property("notifyId", notifyId).build() ;
				tengine.sendMessage(userId, sender, tresponse) ;
			}
		}
				
	}

	@Override
	public void registered(Workspace wspace) {
		this.memberId = wspace.repository().memberId() ;
	}

	@Override
	public void unRegistered(Workspace wspace) {
	}

}