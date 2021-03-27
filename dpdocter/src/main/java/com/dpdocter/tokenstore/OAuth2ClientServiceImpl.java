package com.dpdocter.tokenstore;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.OAuth2ClientDetailsCollection;
import com.dpdocter.repository.OAuth2ClientDetailsRepository;

@Service
public class OAuth2ClientServiceImpl implements ClientDetailsService {

	@Autowired
	private OAuth2ClientDetailsRepository auth2ClientDetailsRepository;

	public BaseClientDetails getClient(String clientId) {

		OAuth2ClientDetailsCollection oAuth2ClientDetailsCollection = auth2ClientDetailsRepository
				.findByClientId(clientId);
		BaseClientDetails details = new BaseClientDetails(oAuth2ClientDetailsCollection.getClientId(),
				oAuth2ClientDetailsCollection.getResourceIds(), oAuth2ClientDetailsCollection.getScope(),
				oAuth2ClientDetailsCollection.getAuthorizedGrantTypes(), oAuth2ClientDetailsCollection.getAuthorities(),
				oAuth2ClientDetailsCollection.getRegisteredRedirectUris());

		details.setClientSecret(oAuth2ClientDetailsCollection.getClientSecret());
		if (oAuth2ClientDetailsCollection.getAccessTokenValiditySeconds() != null) {
			details.setAccessTokenValiditySeconds(oAuth2ClientDetailsCollection.getAccessTokenValiditySeconds());
		}

		if (oAuth2ClientDetailsCollection.getRefreshTokenValiditySeconds() != null) {
			details.setRefreshTokenValiditySeconds(
					Integer.valueOf(oAuth2ClientDetailsCollection.getRefreshTokenValiditySeconds()));
		}
		String scopes1 = oAuth2ClientDetailsCollection.getScope();
//details.setClientSecret("{bcrypt}");
		if (scopes1 != null) {
			Set<String> temp = new HashSet<String>();
			temp.add(scopes1);
			try {
				details.setAutoApproveScopes(temp);
			} catch (Exception e) {
				System.out.println("e");
				return details;
			}

		}
		return details;
	}

	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {

		return this.getClient(clientId);

	}
}
