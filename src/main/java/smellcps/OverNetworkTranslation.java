package smellcps;

import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URI;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class OverNetworkTranslation {

	private final String translationTarget;
	private final String translationServerUri;

	public OverNetworkTranslation(String translationTarget) {
		Properties config = new Properties();
		String translationServerUri = null;
		try {
			FileInputStream propertiesInputStream = new FileInputStream(Paths.get(System.getProperty("user.home"), ".ghidra", "smellcps_plugin_config.properties").toString());
			config.load(propertiesInputStream);
			translationServerUri = config.getProperty("translation_server_uri");
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(-1);
		}
		this.translationServerUri = translationServerUri;
		this.translationTarget = translationTarget;
	}

	public String run() {
		System.out.println("OverNetworkTranslation.run() start");
		HttpClient client = HttpClient.newHttpClient();

		Map<String, String> postDataMap = new HashMap<String, String>();
		postDataMap.put("source", this.translationTarget);
		Gson gson = new Gson();
		String json = gson.toJson(postDataMap);

		URI uri = URI.create(this.translationServerUri);
		HttpRequest request = HttpRequest.newBuilder(uri).header("Content-Type", "application/json").POST(BodyPublishers.ofString(json)).build();

		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

			Type type = new TypeToken<Map<String, List<String>>>(){}.getType();
			Map<String, List<String>> myMap = gson.fromJson(response.body(), type);

			String translation = myMap.get("translation").get(0);


			return translation;
		} catch (IOException e) {
			e.printStackTrace();
			return "Error IOException";
		} catch (InterruptedException e) {
			e.printStackTrace();
			return "Error InterruptedException";
		}
		
	}

}
