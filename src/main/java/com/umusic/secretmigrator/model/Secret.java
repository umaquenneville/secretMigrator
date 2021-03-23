package com.umusic.secretmigrator.model;

import lombok.Data;

@Data
public class Secret {
    
    private String env;
    private String name;
    private String json;
    private String filename;
}
