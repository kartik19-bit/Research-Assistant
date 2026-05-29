package com.research.assistant;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/research")
@CrossOrigin(origins = "*")
@AllArgsConstructor //for getting all constr of researchservice automatically
public class ResearchController {
    private final ResearchService researchService;

    @PostMapping("/process")
    public ResponseEntity<String> processRequest(@RequestBody ResearchRequest researchRequest){
           String result = researchService.processContent(researchRequest);
           return ResponseEntity.ok(result);
    }


}
