package com.practice.spring.controller;

import com.practice.spring.domain.Person;
import com.practice.spring.service.PersonKafkaService;
import com.practice.spring.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/persons")
public class PersonController {

    @Autowired
    private PersonService personService;

    @Autowired
    private PersonKafkaService personKafkaService;

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

    @GetMapping(path = "/kafka")
    public void sendMessage(final String message){
        personKafkaService.sendMessage(message);
    }

    @GetMapping(path = "/getLastRecords")
    public void getLastRecords(@RequestParam final int numRecords){
        personKafkaService.getLastNRecords(numRecords);
    }

    @GetMapping(path = "/getLastMessages")
    public List<String> getLastMessages(@RequestParam final int numRecords){
        return personKafkaService.getLastNMessages(numRecords);
    }

    @GetMapping(path = "/getLastNDays")
    public void getLastNDays(@RequestParam final int days){
        personKafkaService.getLastNDays(days);
    }
}
