package com.example.fixtures;

import com.example.common.JteTemplateEngine;
import com.example.common.models.Person;

public class PersonFixture {

    private final JteTemplateEngine jte = new JteTemplateEngine();

    private String name;
    private String email;
    private int age;

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String render() throws Exception {
        var person = new Person(name, email, age);
        var json = jte.renderPerson(person);
        var tree = mapper.readTree(json);
        return mapper.writeValueAsString(tree);
    }
    private final com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
}
