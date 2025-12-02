package br.com.careermatcher.controllers;
import br.com.careermatcher.models.Vaga;
import br.com.careermatcher.repositories.CandidatoRepository;
import br.com.careermatcher.repositories.VagaRepository;
import br.com.careermatcher.services.CandidatoService;
import br.com.careermatcher.services.GaleShapley;
import br.com.careermatcher.services.VagaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/careermatcher/vagas")
@AllArgsConstructor
public class VagaController {

    private final VagaService vagaService;
    private final CandidatoService candidatoService;
    private final VagaRepository vagaRepository;
    private final CandidatoRepository candidatoRepository;
    private final GaleShapley galeShapley;

    @GetMapping("/")
    public ResponseEntity<List<Vaga>> findAllVagas(){
        galeShapley.galeShapleyAlgorithm(candidatoService.findAll(),  vagaService.findAll());
        return new ResponseEntity<>(vagaService.findAll(), HttpStatus.OK);
    }

}
