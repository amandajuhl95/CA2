/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Hobby;

/**
 *
 * @author Amanda
 */
public class HobbyDTO {
    
    private String hobby;
    private String description;
    
    public HobbyDTO(Hobby hobby)
    {
        this.hobby = hobby.getName();
        this.description = hobby.getDescription();
    }

    public HobbyDTO() {
    }

    public String getHobby() {
        return hobby;
    }

    public String getDescription() {
        return description;
    }
     
}
