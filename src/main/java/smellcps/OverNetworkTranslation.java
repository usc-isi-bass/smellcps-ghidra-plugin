package smellcps;

import java.util.Properties;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URI;
import java.io.IOException;

public class OverNetworkTranslation {

	private final String hostUrl = "https://httpbin.org/anything";

	private final String translationTarget;

	public OverNetworkTranslation(String translationTarget) {
		
		this.translationTarget = translationTarget;
	}

	public String run() {
		System.out.println("OverNetworkTranslation.run() start");
		HttpClient client = HttpClient.newHttpClient();

		URI uri = URI.create(this.hostUrl);
		HttpRequest request = HttpRequest.newBuilder(uri).build();

		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

			return response.body();
		} catch (IOException e) {
			e.printStackTrace();
			return "Error IOException";
		} catch (InterruptedException e) {
			e.printStackTrace();
			return "Error InterruptedException";
		}
		
	}

}
