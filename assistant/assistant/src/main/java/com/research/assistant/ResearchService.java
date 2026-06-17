package com.research.assistant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
public class ResearchService {

       @Value("${gemini.api.url}")
       private String geminiApiUrl;

       @Value("${gemini.api.key}")
       private String geminiApiKey;

       private final WebClient webClient;
       private final ObjectMapper objectMapper;

       @Autowired
       public ResearchService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper){
              this.webClient = webClientBuilder.build();
              this.objectMapper = objectMapper;
       }
       public String processContent(ResearchRequest researchRequest){
              //create a prompt
              String prompt = buildPrompt(researchRequest);

              //fetch Gemini AI API
              //here this is the format which is used by the Gemini API during API call
              Map<String , Object> requestBody = Map.of(
                      "contents" , new Object[]{
                              Map.of("parts", new Object[]{
                                      Map.of("text", prompt)
                              })
                      });


              String response = webClient.post().
                      uri(geminiApiUrl + geminiApiKey).
                      bodyValue(requestBody)
                      .retrieve().
                      bodyToMono(String.class).
                      block();




              //parse the API response
              String resp = extractTextFromResponse(response);

              //return response
              return resp;


       }

       private String extractTextFromResponse(String response){
               try {
                    GeminiResponse geminiResponse =  objectMapper.readValue(response , GeminiResponse.class);
                    if(geminiResponse.getCandidates() != null && !geminiResponse.getCandidates().isEmpty()){
                        GeminiResponse.Candidate firstCandidate = geminiResponse.getCandidates().get(0);
                        if(firstCandidate.getContent() != null && firstCandidate.getContent().getParts() != null &&
                          !firstCandidate.getContent().getParts().isEmpty()){
                            return firstCandidate.getContent().getParts().get(0).getText();
                        }
                    }
                    return "No content found";
               }catch(Exception e){
                   return "Error Parsing:" + e.getMessage();
               }
       }
       @org.jetbrains.annotations.NotNull
       private String buildPrompt(ResearchRequest researchRequest){
              StringBuilder prompt = new StringBuilder(); //used over string as it is mutable
              switch(researchRequest.getOperation()){
                  case "summarize":
                      prompt.append("Provide a clear and concise summary of the following text in sentences given : \n\n");
                      break;
                      case "suggest":
                          prompt.append("Based on following content , suggest related topics for further referring to . Format the response with clear headings and bullet points : \n\n");
                          break;
                  default:
                      throw new IllegalArgumentException("Unkown Operation" +  researchRequest.getOperation());


              }
              prompt.append(researchRequest.getContent());
              return prompt.toString();
       }
}
