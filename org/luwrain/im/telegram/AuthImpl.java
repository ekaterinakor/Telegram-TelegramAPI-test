package org.luwrain.im.telegram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;import java.io.OutputStream;
import java.util.concurrent.TimeoutException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.luwrain.im.Auth;
import org.luwrain.im.Events;
import org.telegram.api.TLConfig;
import org.telegram.api.auth.TLAuthorization;
import org.telegram.api.auth.TLExportedAuthorization;
import org.telegram.api.auth.TLSentCode;
import org.telegram.api.engine.ApiCallback;
import org.telegram.api.engine.AppInfo;
import org.telegram.api.engine.RpcCallback;
import org.telegram.api.engine.RpcException;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.auth.TLRequestAuthBindTempAuthKey;
import org.telegram.api.functions.auth.TLRequestAuthExportAuthorization;
import org.telegram.api.functions.auth.TLRequestAuthImportAuthorization;
import org.telegram.api.functions.auth.TLRequestAuthSendCode;
import org.telegram.api.functions.auth.TLRequestAuthSignIn;
import org.telegram.api.functions.auth.TLRequestAuthSignUp;
import org.telegram.api.functions.help.TLRequestHelpGetConfig;
import org.telegram.api.updates.TLAbsUpdates;
import org.telegram.bot.kernel.engine.MemoryApiState;
import org.telegram.tl.TLBytes;

public class AuthImpl implements Auth {

	/** Timeout milliseconds */
	final int TIMEOUT=30000;
	/** Telegram Application hash */
	final String APIHASH="62155226f23b8565aa3aaa0fa68df878";
	/** Telegram Application id */
	final Integer APIID=97022;
		
	private Config config;
	private Events events;
	private TelegramApi api;
	private TLAuthorization auth;
	TLConfig tlconfig;
	
	private MemoryApiState state;
	TLSentCode sentCode = null;

	public AuthImpl(Config config)
	{
		this.config=config;
	}
	public void go(final Events events) {
		final AuthImpl that=this;
		this.events=events;
		state = new MemoryApiState("qwsedrfgth");
//		getAuthKey 3cc1b1a3763c2cad6815a5de0ffc5208e8cb6b04917d3aa88901985537edfd5d06841111785cea72a65db78f753cfe4803d5a50880cf29dd83a4a40b69a3478fea7740fe1782a945a56a49e80a8be2fcb86ed6cecc32b6ca83d46001e8e6f8ea16806c87d5c793b3e3088c598b158abdca6123fe6a915e579dc834a608ddb25456542c1e8f3290d96c12adbea2adfe7812e68dd7c9a741a1111b7e8445abc5de822abdbd9665e1c869e3ec055dce0460917785d7f8464716a50ed9a25510f51980b5cda420847ee37d7df442901330e8a03f90cd10f49e5694a3da11ccb245ac669e8c9725baae6398d8a529043624c913c2b00bf60684337165e37c5cf8c994
//		getUserId 197321144
		api = new TelegramApi(state, new AppInfo(97022, "console", "1.0", "1.0", "en"), new ApiCallback() {

			public void onAuthCancelled(TelegramApi api) {
				//System.out.println("*** DEBUG onAuthCancelled:"+api);
			}

			public void onUpdatesInvalidated(TelegramApi api) {
				//System.out.println("*** DEBUG onUpdatesInvalidated:"+api);
			}

			public void onUpdate(TLAbsUpdates updates) {
				//System.out.println("*** DEBUG onUpdate:"+updates);
			}});
		
		
        
        
		try {
			tlconfig = api.doRpcCallNonAuth(new TLRequestHelpGetConfig());
	        state.updateSettings(tlconfig);
	        state.setPrimaryDc(tlconfig.getThisDc());
			api.switchToDc(tlconfig.getThisDc());
			System.out.println("1");
			// request twofactor auth
	        TLRequestAuthSendCode m= new TLRequestAuthSendCode();
	        m.setPhoneNumber(that.config.phone);
	        m.setApiHash(APIHASH);
	        m.setApiId(APIID);
	        that.sentCode = null;
	        try {
	        	sentCode=api.doRpcCall(m);
	        	System.out.println("authsendcode "+sentCode.getPhoneCodeHash());
	        } catch (RpcException e) {
	        	System.out.println("e.getErrorCode() "+e.getMessage());
	            if (e.getErrorCode() == 303) {
	                int destDC;
	                // TODO: использование регулярных выражений
	                if (e.getErrorTag().startsWith("NETWORK_MIGRATE_")) {
	                    destDC = Integer.parseInt(e.getErrorTag().substring("NETWORK_MIGRATE_".length()));
	                } else if (e.getErrorTag().startsWith("PHONE_MIGRATE_")) {
	                    destDC = Integer.parseInt(e.getErrorTag().substring("PHONE_MIGRATE_".length()));
	                } else if (e.getErrorTag().startsWith("USER_MIGRATE_")) {
	                    destDC = Integer.parseInt(e.getErrorTag().substring("USER_MIGRATE_".length()));
	                } else {
	                	that.events.onError(e.getMessage());
	                	return;
	                }
	                api.switchToDc(destDC);
	                //готовы общаться с датацентром                
	                try {
						sentCode = api.doRpcCallNonAuth(m);
					} catch (Exception e1) {
						that.events.onError(e.getMessage());
	                	return;
					} 
	                //phone = "99966"+destDC+"2345";
	                System.out.println("destDC="+destDC);
	            } else {
	            	that.events.onError(e.getMessage());
                	return;
	            }
	        }
	        catch (java.util.concurrent.TimeoutException e) {
	        	that.events.onError(e.getMessage());
	        	return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
	        // восстанавливаем авторизацию
	        System.out.println("DC "+that.tlconfig.getThisDc());

	        TLRequestAuthImportAuthorization impauth=new TLRequestAuthImportAuthorization();
	        impauth.setId(197321144);
	        try {
				impauth.setBytes(new TLBytes(Hex.decodeHex("3cc1b1a3763c2cad6815a5de0ffc5208e8cb6b04917d3aa88901985537edfd5d06841111785cea72a65db78f753cfe4803d5a50880cf29dd83a4a40b69a3478fea7740fe1782a945a56a49e80a8be2fcb86ed6cecc32b6ca83d46001e8e6f8ea16806c87d5c793b3e3088c598b158abdca6123fe6a915e579dc834a608ddb25456542c1e8f3290d96c12adbea2adfe7812e68dd7c9a741a1111b7e8445abc5de822abdbd9665e1c869e3ec055dce0460917785d7f8464716a50ed9a25510f51980b5cda420847ee37d7df442901330e8a03f90cd10f49e5694a3da11ccb245ac669e8c9725baae6398d8a529043624c913c2b00bf60684337165e37c5cf8c994".toCharArray())));
			} catch (DecoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
	        	System.out.println("1");
				auth=api.doRpcCall(impauth);
	        	System.out.println("2");
				state.doAuth(auth);
	        	System.out.println("3");
				that.events.onAuthFinish();
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
	        // Запрашиваем смс код
	        that.events.on2PassAuth(null);	
		} catch (RpcException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (TimeoutException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		
        //api.doRpcCallNonAuth(checkPhone,3000,callback);
		//System.out.println("*** DEBIG checkedPhone: "+checkedPhone.isPhoneRegistered());
//		api.doRpcCallNonAuth(new TLRequestHelpGetConfig(),TIMEOUT,new RpcCallback<TLConfig>() {
//			
//			public void onResult(TLConfig tlconfig) {
//				// request datacenter config
//				System.out.println("*** DEBUG onResult: thisDC="+tlconfig.getThisDc());
//		        state.updateSettings(tlconfig);
//		        state.setPrimaryDc(tlconfig.getThisDc());
//				api.switchToDc(tlconfig.getThisDc());
//				System.out.println("1");
//				// request twofactor auth
//		        TLRequestAuthSendCode m= new TLRequestAuthSendCode();
//		        m.setPhoneNumber(that.config.phone);
//		        m.setApiHash(APIHASH);
//		        m.setApiId(APIID);
//		        TLSentCode sentCode = null;
//		        try {
//		        	sentCode=api.doRpcCall(m);
//		        	System.out.println("authsendcode "+sentCode.getPhoneCodeHash());
//		        } catch (RpcException e) {
//		        	System.out.println("e.getErrorCode() "+e.getMessage());
//		            if (e.getErrorCode() == 303) {
//		                int destDC;
//		                // TODO: использование регулярных выражений
//		                if (e.getErrorTag().startsWith("NETWORK_MIGRATE_")) {
//		                    destDC = Integer.parseInt(e.getErrorTag().substring("NETWORK_MIGRATE_".length()));
//		                } else if (e.getErrorTag().startsWith("PHONE_MIGRATE_")) {
//		                    destDC = Integer.parseInt(e.getErrorTag().substring("PHONE_MIGRATE_".length()));
//		                } else if (e.getErrorTag().startsWith("USER_MIGRATE_")) {
//		                    destDC = Integer.parseInt(e.getErrorTag().substring("USER_MIGRATE_".length()));
//		                } else {
//		                	that.events.onError(e.getMessage());
//		                	return;
//		                }
//		                api.switchToDc(destDC);
//		                try {
//							sentCode = api.doRpcCallNonAuth(m);
//						} catch (Exception e1) {
//							that.events.onError(e.getMessage());
//		                	return;
//						} 
//		                //phone = "99966"+destDC+"2345";
//		                System.out.println("destDC="+destDC);
//		            } else {
//		            	that.events.onError(e.getMessage());
//	                	return;
//		            }
//		        }
//		        catch (java.util.concurrent.TimeoutException e) {
//		        	that.events.onError(e.getMessage());
//		        	return;
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					return;
//				}
//		        // Запрашиваем смс код
//		        that.events.on2PassAuth(null);	
//			}
//
//			public void onError(int errorCode, String message) {
//				events.onError(message);
//				
//			}
//		});
       

	}
	
	public TelegramApi getApi() {
		return api;
	}
	
	public Events getEvents() {
		return events;
	}
	public MemoryApiState getState() {
		return state;
	}
	public void twoPass(String code) {
		final AuthImpl that=this;
		auth=null;
		//TODO: первый вызов doRpcCallNonAuth сделать асинхронным
        try
        {  
        	System.out.println("signin");
        	TLRequestAuthSignIn sign = new TLRequestAuthSignIn();
            sign.setPhoneCode(code);
            sign.setPhoneCodeHash( that.sentCode.getPhoneCodeHash());
            sign.setPhoneNumber(that.config.phone);
            auth = api.doRpcCallNonAuth(sign);
            System.out.println("isTemporalSession "+auth.isTemporalSession()+" user "+auth.getUser().getId());
            
        }
        catch (Exception e)
        {     
        	e.printStackTrace();
        	System.out.println("signinerror "+e.getMessage());
        	System.out.println("signup");
        	TLRequestAuthSignUp sign = new TLRequestAuthSignUp();
        	sign.setFirstName("dgfgd");
        	sign.setLastName("dfgdfg");
        	sign.setPhoneCode(code);
        	sign.setPhoneCodeHash(sentCode.getPhoneCodeHash());
        	sign.setPhoneNumber(that.config.phone);
        	try {
				auth = api.doRpcCallNonAuth(sign);
			} catch (Exception e1) {
				that.events.onError(e1.getMessage());
            	return;
			} 
            System.out.println("isTemporalSession "+auth.isTemporalSession()+" user "+auth.getUser().getId());
        }
        //api.getState().doAuth(auth);        
        System.out.println("DC "+that.tlconfig.getThisDc());
        System.out.println("getAuthKey "+new String(Hex.encodeHex(state.getAuthKey(that.tlconfig.getThisDc()))));
        System.out.println("getUserId "+state.getUserId());
	}

}
