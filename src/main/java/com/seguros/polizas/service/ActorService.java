package com.seguros.polizas.service;

import com.seguros.polizas.entity.Actor;
import com.seguros.polizas.repository.ActorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActorService {

    private final ActorRepository actorRepository;

    public List<Actor> listarActores() {
        return actorRepository.findAll();
    }
}