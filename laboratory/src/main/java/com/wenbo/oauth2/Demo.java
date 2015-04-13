package com.wenbo.oauth2;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.OAuthProviderType;

public class Demo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			OAuthClientRequest request = OAuthClientRequest
					   .authorizationProvider(OAuthProviderType.FACEBOOK)
					   .setClientId("your-facebook-application-client-id")
					   .setRedirectURI("http://www.example.com/redirect")
					   .buildQueryMessage();
		} catch (Exception e) {
			e.printStackTrace();		
		}
	}

}
