package assemblyai.assemblyAi;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SpringBootApplication
public class AssemblyAiApplication {

	public AssemblyAiApplication() throws URISyntaxException {
	}

	@SneakyThrows
	public static void main(String[] args) throws URISyntaxException {
		SpringApplication.run(AssemblyAiApplication.class, args);

		// Create an instance of the assemblyai.assemblyAi.Transcript class
		Transcript transcript = new Transcript();
		transcript.setAudio_url("https://github.com/AssemblyAI-Examples/audio-examples/raw/main/20230607_me_canadian_wildfires.mp3");

		// Convert the assemblyai.assemblyAi.Transcript object to JSON
		Gson gson = new Gson();
		String jsonRequest = gson.toJson(transcript);
		System.out.println(jsonRequest);


		 HttpRequest postRequest = HttpRequest.newBuilder()
		        .uri(new URI("https://api.assemblyai.com/v2/transcript"))
		         .header("Authorization","YOURAPIKEY")
		         .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
				 .build();

		HttpClient httpClient = HttpClient.newHttpClient();
		HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

		System.out.println(postResponse.body());


		transcript = gson.fromJson(postResponse.body(),Transcript.class);

		System.out.println(transcript.getId());

		HttpRequest getRequest = HttpRequest.newBuilder()
				.uri(new URI("https://api.assemblyai.com/v2/transcript/" +transcript.getId()))
				.header("Authorization","2ee54ed06f9247a9af762c9f256aa4ff")

				.build();
		while (true) {
			HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
			transcript = gson.fromJson(getResponse.body(), Transcript.class);
			System.out.println(transcript.getStatus());

			if ("completed".equals(transcript.getStatus()) || "error".equals(transcript.getStatus()))
				break;

		}
		Thread.sleep(1000);
		System.out.println("Transcription completed successfully");
		System.out.println(transcript.getText());

	}

}
