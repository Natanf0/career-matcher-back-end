package br.com.careermatcher.controllers;

import br.com.careermatcher.models.Match;
import br.com.careermatcher.services.StableMarriageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/matching")
@AllArgsConstructor
public class MatchingController {
    
    private final StableMarriageService stableMarriageService;
    
    @GetMapping("/execute")
    public ResponseEntity<Map<String, Object>> executeStableMarriage() {
        List<Match> matches = stableMarriageService.executarCasamentoEstavel();
        
        Map<String, Object> response = new HashMap<>();
        response.put("matches", matches);
        response.put("totalMatches", matches.size());
        
        return ResponseEntity.ok(response);
    }
}
