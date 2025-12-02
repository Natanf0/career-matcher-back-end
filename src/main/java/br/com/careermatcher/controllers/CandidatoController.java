package br.com.careermatcher.controllers;

import br.com.careermatcher.models.Candidato;
import br.com.careermatcher.services.CandidatoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/careermatcher/candidatos")
@AllArgsConstructor
public class CandidatoController {

    private final CandidatoService candidatoService;

    @GetMapping("/")
    public ResponseEntity<List<Candidato>> findAllCandidatos(){
        return new ResponseEntity<>(candidatoService.findAll(), HttpStatus.OK);
    }
}
