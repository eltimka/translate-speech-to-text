package com.example.demo;

import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) throws URISyntaxException {
		SpringApplication.run(DemoApplication.class, args);

		Transcript transcript = new Transcript();
		transcript.setAudio_url("https:// audio path url ");
		Gson gson = new Gson();
		String jsonRequest = gson.toJson(transcript);

		System.out.println(jsonRequest);

		HttpRequest postRequest = HttpRequest.newBuilder()
				.uri(new URI("https://api.assemblyai.com/v2/transcript"))
				.header("Autorization", Constants.API_KEY)
				.POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
				.build();

		HttpClient httpClient = HttpClient.newHttpClient();

		HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

		System.out.println(postResponse.body());

		transcript = gson.fromJson(postResponse.body(), Transcript.class);
		System.out.println(transcript.getId());

		HttpRequest getRequest = HttpRequest.newBuilder()
				.uri(new URI("https://api.assemblyai.com/v2/transcript" + transcript.getId()))
				.header("Autorization", Constants.API_KEY)
				.build();

		while (true) {
			HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
			transcript = gson.fromJson(getResponse.body(), Transcript.class);

			System.out.println(transcript.getStatus());

			if ("completed".equals(transcript.getStatus()) || "error".equals(transcript.getStatus())){
				break;
		}
		Thread.sleep(1000);

	}
		System.out.println("Transcription completed!");
		System.out.println(transcript.getText());
	}

}
