import java.awt.SecondaryLoop;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import org.apache.commons.codec.binary.Hex;
import org.luwrain.im.Events;
import org.luwrain.im.telegram.AuthImpl;
import org.luwrain.im.telegram.Config;
import org.telegram.api.TLConfig;
import org.telegram.api.auth.TLAuthorization;
import org.telegram.api.auth.TLCheckedPhone;
import org.telegram.api.auth.TLSentCode;
import org.telegram.api.contacts.TLAbsContacts;
import org.telegram.api.contacts.TLContactsFound;
import org.telegram.api.engine.ApiCallback;
import org.telegram.api.engine.AppInfo;

import org.telegram.api.engine.RpcCallback;
import org.telegram.api.engine.RpcException;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.engine.storage.AbsApiState;
import org.telegram.api.functions.auth.TLRequestAuthCheckPhone;
import org.telegram.api.functions.auth.TLRequestAuthSendCode;
import org.telegram.api.functions.auth.TLRequestAuthSendInvites;
import org.telegram.api.functions.auth.TLRequestAuthSignIn;
import org.telegram.api.functions.auth.TLRequestAuthSignUp;
import org.telegram.api.functions.contacts.TLRequestContactsGetContacts;
import org.telegram.api.functions.contacts.TLRequestContactsSearch;
import org.telegram.api.functions.help.TLRequestHelpGetConfig;
import org.telegram.api.functions.messages.TLRequestMessagesSendMessage;
import org.telegram.api.input.peer.TLInputPeerUser;
import org.telegram.api.updates.TLAbsUpdates;
import org.telegram.bot.kernel.engine.MemoryApiState;
import org.telegram.bot.services.BotLogger;
import org.telegram.mtproto.log.LogInterface;
import org.telegram.tl.TLBool;
import org.telegram.tl.TLMethod;
import org.telegram.tl.TLObject;
import org.telegram.tl.TLStringVector;
import org.telegram.tl.TLVector;

public class Main {

	public static void main(String[] args) {
		try{
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void init() throws Exception
	{
		org.telegram.mtproto.log.Logger.registerInterface(new org.telegram.mtproto.log.LogInterface() {
            public void w(String tag, String message) {}
            public void d(String tag, String message) {}
            public void e(String tag, String message) {            }
            public void e(String tag, Throwable t) {}
        });
		org.telegram.api.engine.Logger.registerInterface(new org.telegram.api.engine.LoggerInterface() {
            public void w(String tag, String message) {
            }

            public void d(String tag, String message) {
            }

            public void e(String tag, String message) {
            }

            public void e(String tag, Throwable t) {
            }
});
		Config config=new Config();
		config.firstName="fhgfhfg";
		config.lastName="gfhgfh";
		config.phone="";//enter read phone
		
		final AuthImpl auth=new AuthImpl(config);
		auth.go(new Events() {
			
			public void onWarning(String message) {
				System.out.println("onWarning"+message);
			}
			
			public void onError(String message) {
				System.out.println("onError"+message);
			}
			
			public void onAuthFinish() {
				System.out.println("onAuthFinish");
			}
			
			public void on2PassAuth(String message) {
				System.out.println("on2PassAuth "+message);
				byte[] buf=new byte[10240];
				int cnt=0;
				try {
					cnt = System.in.read(buf, 0, 10240);
				} catch (IOException e) {
					e.printStackTrace();
				}
				String code=new String(buf,0,cnt).trim();
				System.out.println("My access code: '"+code+"'");
				auth.twoPass(code);
			}
		});
		
		System.out.println("*** DEBUG: exit?");
	}

}
