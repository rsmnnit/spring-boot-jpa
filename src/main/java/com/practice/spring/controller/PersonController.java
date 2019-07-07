package com.practice.spring.controller;

import com.practice.spring.domain.Person;
import com.practice.spring.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/persons")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping(path = "/get")
    public Optional<Person> getPerson(@RequestParam final Integer id) {
        return personService.getPerson(id.longValue());
    }

    @PutMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Person savePerson(@RequestBody Person person) {
        return personService.insert(person);
    }

    @GetMapping(path = "/getAll")
    public Iterable<Person> getAll() {
        return personService.getAll();
    }

    @DeleteMapping
    public void delete(@RequestBody Person person){
        personService.delete(person);
    }
}
