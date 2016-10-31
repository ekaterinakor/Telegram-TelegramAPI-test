import java.awt.SecondaryLoop;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import org.apache.commons.codec.binary.Hex;
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
	
	static final int timeout=30000;

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
		//Prjkej8_fCY
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
		final MemoryApiState state = new MemoryApiState("qwsedrfgth");
		TelegramApi api = new TelegramApi(state, new AppInfo(97022, "console", "1.0", "1.0", "en"), new ApiCallback() {

			public void onAuthCancelled(TelegramApi api) {
				//System.out.println("*** DEBUG onAuthCancelled:"+api);
			}

			public void onUpdatesInvalidated(TelegramApi api) {
				//System.out.println("*** DEBUG onUpdatesInvalidated:"+api);
			}

			public void onUpdate(TLAbsUpdates updates) {
				//System.out.println("*** DEBUG onUpdate:"+updates);
			}});
		

        //api.doRpcCallNonAuth(checkPhone,3000,callback);
		//System.out.println("*** DEBIG checkedPhone: "+checkedPhone.isPhoneRegistered());
		TLConfig config=api.doRpcCallNonAuth(new TLRequestHelpGetConfig());
		System.out.println("*** DEBUG onResult: thisDC="+config.getThisDc());
        state.updateSettings(config);
        state.setPrimaryDc(config.getThisDc());
		api.switchToDc(config.getThisDc());
		System.out.println("1");

        String phone = "79039502758"; // I have tested app with the phone without plus too
 
        TLRequestAuthSendCode m= new TLRequestAuthSendCode();
        m.setPhoneNumber(phone);
        m.setApiHash("62155226f23b8565aa3aaa0fa68df878");
        m.setApiId(97022);
        TLSentCode sentCode = null;
        try {
        	sentCode=api.doRpcCallNonAuth(m);
        	System.out.println("authsendcode "+sentCode.getPhoneCodeHash());
        } catch (RpcException e) {
        	System.out.println("e.getErrorCode() "+e.getMessage());
            if (e.getErrorCode() == 303) {
                int destDC;
                if (e.getErrorTag().startsWith("NETWORK_MIGRATE_")) {
                    destDC = Integer.parseInt(e.getErrorTag().substring("NETWORK_MIGRATE_".length()));
                } else if (e.getErrorTag().startsWith("PHONE_MIGRATE_")) {
                    destDC = Integer.parseInt(e.getErrorTag().substring("PHONE_MIGRATE_".length()));
                } else if (e.getErrorTag().startsWith("USER_MIGRATE_")) {
                    destDC = Integer.parseInt(e.getErrorTag().substring("USER_MIGRATE_".length()));
                } else {
                    throw e;
                }
                api.switchToDc(destDC);
                sentCode = api.doRpcCallNonAuth(m);
                //phone = "99966"+destDC+"2345";
                System.out.println("destDC="+destDC);
            } else {
                throw e;
            }
        }
        System.out.println("getPrimaryDc  "+api.getState().getPrimaryDc());
        
        System.out.print("input sms code: ");
		byte[] buf=new byte[10240];
		int cnt=System.in.read(buf, 0, 10240);
		String code=new String(buf,0,cnt).trim();
		System.out.println("My access code: '"+code+"'");

		
//		api.getState().doAuth(Integer.parseInt(code),phone);
//		System.out.println(state.isAuthenticated());
		

		TLAuthorization auth=null;
        try
        {  
        	System.out.println("signin");
        	TLRequestAuthSignIn sign = new TLRequestAuthSignIn();
            sign.setPhoneCode(code);
            sign.setPhoneCodeHash(sentCode.getPhoneCodeHash());
            sign.setPhoneNumber(phone);
            auth = api.doRpcCallNonAuth(sign);
            System.out.println("isTemporalSession "+auth.isTemporalSession()+" user "+auth.getUser().getId());
            
        }
        catch (Exception e)
        {     
        	System.out.println("e.getLocalizedMessage()"+e.getLocalizedMessage());
        	System.out.println("signup");
        	TLRequestAuthSignUp sign = new TLRequestAuthSignUp();
        	sign.setFirstName("dgfgd");
        	sign.setLastName("dfgdfg");
        	sign.setPhoneCode(code);
        	sign.setPhoneCodeHash(sentCode.getPhoneCodeHash());
        	sign.setPhoneNumber(phone);
        	auth = api.doRpcCallNonAuth(sign);
            System.out.println("isTemporalSession "+auth.isTemporalSession()+" user "+auth.getUser().getId());
        }
        api.getState().doAuth(auth);
        System.out.println("getAuthKey "+new String(Hex.encodeHex(state.getAuthKey(api.getState().getPrimaryDc()))));
        System.out.println("getUserId "+state.getUserId());
        //TLAuthorization auth = api.doRpcCallNonAuth(sign);
        //System.out.println(sign);
//        TLRequestAuthSendCode m= new TLRequestAuthSendCode();
//        m.setPhoneNumber("79528900423");
//        m.setApiHash("62155226f23b8565aa3aaa0fa68df878");
//        m.setApiId(97022);
//        System.out.println(m.getPhoneNumber());
//        TLSentCode sentCode = api.doRpcCall (m,15000);
		
/*		TLRequestAuthSendInvites invites=new TLRequestAuthSendInvites();
		TLStringVector users=new TLStringVector();
		users.add("79528900423");
		invites.setPhoneNumbers(users);
		invites.setMessage("invite");
		TLBool result=api.doRpcCall(invites);
		System.out.println(result);*/
		
        
        TLRequestContactsSearch cntcs=new TLRequestContactsSearch();
        cntcs.setQ("gdfg");
        TLContactsFound rescnts=api.doRpcCallSide(cntcs);
        System.out.println("contacts "+rescnts.getUsers().size());
        
//        TLRequestMessagesSendMessage msg=new TLRequestMessagesSendMessage();
//		msg.setMessage("fgdfgfd");
//		TLInputPeerUser user=new TLInputPeerUser();
//		user.setUserId();
//		msg.setPeer(new TLInputPeerUser()));


		System.out.println("*** DEBUG: exit?");
	}

}
