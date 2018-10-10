import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential.Builder;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetRefreshToken
{

	// SETTINGS
	private static final List<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/youtube",
			"https://www.googleapis.com/auth/youtubepartner",
			"https://www.googleapis.com/auth/yt-analytics-monetary.readonly",
			"https://www.googleapis.com/auth/yt-analytics.readonly"
	);

	// CONSTANTS
	private static final String CALLBACK_URL = "urn:ietf:wg:oauth:2.0:oob";


	public static void main(String[] args) throws IOException
	{
		GoogleClientSecrets clientSecrets = createClientSecrets();
		GoogleAuthorizationCodeFlow authorizationFlow = new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(),
				new JacksonFactory(),
				clientSecrets,
				SCOPES
		).setAccessType("offline").build();

		String authorizeUrl = authorizationFlow.newAuthorizationUrl().setRedirectUri(CALLBACK_URL).build();
		System.out.println("Open this url in your browser: \n" + authorizeUrl);
		String authorizationCode = readInputLine("Enter the code you received: ");

		GoogleAuthorizationCodeTokenRequest tokenRequest = authorizationFlow.newTokenRequest(authorizationCode);
		tokenRequest.setRedirectUri(CALLBACK_URL);
		GoogleTokenResponse tokenResponse = tokenRequest.execute();

		GoogleCredential credential = buildCredential(clientSecrets, tokenResponse);
		System.out.println("Your refresh token is: " + credential.getRefreshToken());
	}

	private static GoogleCredential buildCredential(GoogleClientSecrets clientSecrets, GoogleTokenResponse tokenResponse)
	{
		GoogleCredential credential = new Builder()
				.setTransport(new NetHttpTransport())
				.setJsonFactory(new JacksonFactory())
				.setClientSecrets(clientSecrets)
				.build();

		credential.setFromTokenResponse(tokenResponse);
		return credential;
	}

	private static GoogleClientSecrets createClientSecrets()
	{
		Details webSecrets = new Details();
		webSecrets.setClientId(readInputLine("enter clientId"));
		webSecrets.setClientSecret(readInputLine("enter clientSecret"));

		GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
		clientSecrets.setWeb(webSecrets);

		return clientSecrets;
	}

	private static String readInputLine(String prompt)
	{
		System.out.println(prompt);
		return new Scanner(System.in).nextLine();
	}

}
