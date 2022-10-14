package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleDto {
    private Integer id;
    private String roleNom;
    private UtilisateurDto utilisateur;

    public static RoleDto fromEntity(Role role){
        if (role == null){
            return null;
        }
        return RoleDto.builder()
                .id(role.getId())
                .roleNom(role.getRoleNom())
                .build();
    }

    public static Role toEntity(RoleDto roleDto){
        if (roleDto == null){
            return null;
        }
        Role role = new Role();
        role.setId(roleDto.getId());
        role.setRoleNom(roleDto.getRoleNom());
        role.setUtilisateur(UtilisateurDto.toEntity(roleDto.getUtilisateur()));
        return role;
    }

}
