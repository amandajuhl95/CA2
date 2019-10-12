/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Hobby;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 *
 * @author Amanda
 */
public class HobbyDTO {
    
    @Schema(required = true, example = "1")
    private long id;
    @Schema(required = true, example = "Programming")
    private String hobby;
    @Schema(required = true, example = "Programming is fun")
    private String description;
    
    public HobbyDTO(Hobby hobby)
    {
        this.id = hobby.getId();
        this.hobby = hobby.getName();
        this.description = hobby.getDescription();
    }

    public HobbyDTO(String hobby, String description) {
        this.hobby = hobby;
        this.description = description;
    }

    public HobbyDTO() {
    }

    public long getId() {
        return id;
    }

    public String getHobby() {
        return hobby;
    }

    public String getDescription() {
        return description;
    }
     
}
