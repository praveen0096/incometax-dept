package io.mosip.credential.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Type {
    private String namespace;
    private String name;


    // No-argument constructor (Required for Jackson deserialization)
    public Type() {}

    // Constructor with parameters for deserialization
    @JsonCreator
    public Type(@JsonProperty("namespace") String namespace, @JsonProperty("name") String name) {
        this.namespace = namespace;
        this.name = name;
    }
   
   
    // Getter Methods 
   
    public String getNamespace() {
     return namespace;
    }
   
    public String getName() {
     return name;
    }
   
    // Setter Methods 
   
    public void setNamespace(String namespace) {
     this.namespace = namespace;
    }
   
    public void setName(String name) {
     this.name = name;
    }
   }