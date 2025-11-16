package br.com.careermatcher.controllers;


import br.com.careermatcher.models.Vaga;
import br.com.careermatcher.repositories.VagaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/careermatcher")
public class VagaController {

    private final VagaRepository vagaRepository;
    public VagaController(VagaRepository vagaRepository) {
        this.vagaRepository = vagaRepository;
    }

    @GetMapping("/")
    public ResponseEntity<List<Vaga>> findAllVagas(){
        return new ResponseEntity<>(vagaRepository.findAll(), HttpStatus.OK);
    }
}
