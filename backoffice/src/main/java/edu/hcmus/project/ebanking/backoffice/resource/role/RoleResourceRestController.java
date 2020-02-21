package edu.hcmus.project.ebanking.backoffice.resource.role;

import edu.hcmus.project.ebanking.backoffice.model.Role;
import edu.hcmus.project.ebanking.backoffice.repository.RoleRepository;
import edu.hcmus.project.ebanking.backoffice.resource.role.RoleDto;
import edu.hcmus.project.ebanking.backoffice.resource.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class RoleResourceRestController {
    @Autowired
    private RoleRepository RoleRepository;

    @GetMapping("/role")
    public List<Role> getAllRole() {
        return RoleRepository.findAll();
    }

    @GetMapping("/role/{id}")
    public RoleDto findBank(@PathVariable String id){
        Optional<Role> RoleOp = RoleRepository.findById(id);
        if(RoleOp.isPresent()){
            Role role = RoleOp.get();
            RoleDto dto = new RoleDto();
            dto.setRoleId(role.getRoleId());
            dto.setName(role.getName());
            return dto;
        }
        throw new ResourceNotFoundException("Role not found");
    }

    @PostMapping("/role/create")
    public ResponseEntity<RoleDto> createBank(@RequestBody RoleDto dto) {
        Optional<Role> RoleOp = RoleRepository.findById(dto.getRoleId());
        if(!RoleOp.isPresent()){
            Role newrole = new Role();
            newrole.setRoleId(dto.getRoleId());
            newrole.setName(dto.getName());
            newrole = RoleRepository.save(newrole);
            return new ResponseEntity<RoleDto>(dto, HttpStatus.OK);
        }
        throw new ResourceNotFoundException("Role have existed");
    }

    @PutMapping("/role/update/{id}")
    public ResponseEntity<RoleDto> updateBank(@RequestBody RoleDto dto, @PathVariable String id){
        Optional<Role> RoleOp = RoleRepository.findById(id);
        if(RoleOp.isPresent()) {
            Role upRole = RoleOp.get();
            upRole.setName(dto.getName());
            upRole = RoleRepository.save(upRole);
            return new ResponseEntity<RoleDto>(dto, HttpStatus.OK);
        }
        throw new ResourceNotFoundException("Role not found");
    }

    @DeleteMapping("/role/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        Optional<Role> deRole = RoleRepository.findById(id);
        if(deRole.isPresent())
        {
            RoleRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        throw new ResourceNotFoundException("Role not found");
    }
}
