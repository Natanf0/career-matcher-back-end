package br.com.careermatcher.controllers;


import br.com.careermatcher.models.Vaga;
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

    @GetMapping("/")
    public ResponseEntity<List<Vaga>> findAllVagas(){
        //System.out.println("--------------------------------------------------------------------------");
        //vagaService.createRankedListVagas();
        return new ResponseEntity<>(vagaService.findAll(), HttpStatus.OK);
    }

}
