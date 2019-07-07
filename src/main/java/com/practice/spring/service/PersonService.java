package com.practice.spring.service;

import com.practice.spring.domain.Person;
import com.practice.spring.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public Iterable<Person> getAll() {
        return personRepository.findAll();
    }

    public Person insert(final Person person) {
        Person save = personRepository.save(person);
        return save;
    }

    public Optional<Person> getPerson(final Long personId){
        return personRepository.findById(personId);
    }

    public void delete(final Person person){
        personRepository.delete(person);
    }
}
