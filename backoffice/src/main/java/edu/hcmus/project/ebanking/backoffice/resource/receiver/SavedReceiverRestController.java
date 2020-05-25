package edu.hcmus.project.ebanking.backoffice.resource.receiver;

import edu.hcmus.project.ebanking.backoffice.resource.receiver.dto.CreateReceiverDto;
import edu.hcmus.project.ebanking.backoffice.resource.receiver.dto.ReceiverDto;
import edu.hcmus.project.ebanking.backoffice.resource.user.dto.CreateUserDto;
import edu.hcmus.project.ebanking.backoffice.security.jwt.JwtTokenUtil;
import edu.hcmus.project.ebanking.backoffice.service.SavedAccountService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/receivers")
public class SavedReceiverRestController {

    @Autowired
    private SavedAccountService service;

    @ApiOperation(value = "1.3 [User] Create A New Receiver Information ", response = CreateUserDto.class)
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public ResponseEntity<CreateReceiverDto> createReceiver(@Valid @RequestBody CreateReceiverDto dto) {
        service.createReceiver(JwtTokenUtil.getLoggedUser(), dto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
    @ApiOperation(value = "1.3_3 [User] Retrieve All Saved Receiver Information. ", response = List.class)
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public List<ReceiverDto> retrieveAllReceiver() {
        return service.findAll(JwtTokenUtil.getLoggedUser());
    }

    @ApiOperation(value = "1.3_3 [User] Update Receiver Information. ", response = CreateReceiverDto.class)
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<CreateReceiverDto> updateReceiver(@RequestBody CreateReceiverDto dto, @PathVariable Integer id){
        if(id == null) {
            return ResponseEntity.badRequest().build();
        }
        service.updateReceiver(JwtTokenUtil.getLoggedUser(), id, dto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @ApiOperation(value = "1.3_3 [User] Delete A Receiver Information. ")
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id, JwtTokenUtil.getLoggedUser());
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "1.3_3 [User] Search Receiver Information By Suggestion Name. ", response = List.class)
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/search/{name}")
    public List<ReceiverDto> search(@PathVariable String name) {
        return service.search(JwtTokenUtil.getLoggedUser(), name);
    }



}
