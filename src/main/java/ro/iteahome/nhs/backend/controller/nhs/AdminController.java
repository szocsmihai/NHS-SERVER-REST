package ro.iteahome.nhs.backend.controller.nhs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ro.iteahome.nhs.backend.exception.business.GlobalDatabaseException;
import ro.iteahome.nhs.backend.exception.business.GlobalNotFoundException;
import ro.iteahome.nhs.backend.model.nhs.dto.AdminDTO;
import ro.iteahome.nhs.backend.model.nhs.entity.Admin;
import ro.iteahome.nhs.backend.service.clientapp.RoleService;
import ro.iteahome.nhs.backend.service.nhs.AdminService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/admins")
public class AdminController {

// DEPENDENCIES: -------------------------------------------------------------------------------------------------------

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

// C.R.U.D. METHODS: ---------------------------------------------------------------------------------------------------

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntityModel<AdminDTO>> add(@RequestBody @Valid Admin admin) {
        try {
            AdminDTO savedAdminDTO = adminService.add(admin);
            EntityModel<AdminDTO> savedAdminDTOEntity = new EntityModel<>(
                    savedAdminDTO,
                    linkTo(methodOn(AdminController.class).findByEmail(savedAdminDTO.getEmail())).withSelfRel());
            return new ResponseEntity<>(savedAdminDTOEntity, HttpStatus.CREATED);
        } catch (Exception ex) {
            throw new GlobalDatabaseException("ADMIN", ex.getCause().getCause().getMessage());
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CollectionModel<AdminDTO>> findAll() {
        try {
            List<AdminDTO> adminDTOList = new ArrayList<>(adminService.findAll());
            adminDTOList.forEach(adminDTO ->
                    adminDTO.add(linkTo(methodOn(AdminController.class).findByEmail(adminDTO.getEmail())).withSelfRel()));
            CollectionModel<AdminDTO> adminDTOCollection = new CollectionModel<>(
                    adminDTOList,
                    linkTo(methodOn(AdminController.class).findAll()).withSelfRel());
            return new ResponseEntity<>(adminDTOCollection, HttpStatus.OK);
        } catch (Exception ex) {
            throw new GlobalDatabaseException("ADMIN", ex.getCause().getCause().getMessage());
        }
    }

    @GetMapping("/by-email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntityModel<AdminDTO>> findByEmail(@PathVariable String email) {
        try {
            AdminDTO adminDTO = adminService.findByEmail(email);
            EntityModel<AdminDTO> adminDTOEntity = new EntityModel<>(
                    adminDTO,
                    linkTo(methodOn(AdminController.class).findByEmail(email)).withSelfRel());
            return new ResponseEntity<>(adminDTOEntity, HttpStatus.OK);
        } catch (GlobalNotFoundException ex) {
            throw new GlobalNotFoundException(ex.getEntityName());
        } catch (Exception ex) {
            throw new GlobalDatabaseException("ADMIN", ex.getCause().getCause().getMessage());
        }
    }

    @GetMapping("/sensitive/by-email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntityModel<Admin>> findSensitiveByEmail(@PathVariable String email) {
        try {
            Admin admin = adminService.findSensitiveByEmail(email);
            EntityModel<Admin> adminEntity = new EntityModel<>(
                    admin,
                    linkTo(methodOn(AdminController.class).findSensitiveByEmail(email)).withSelfRel());
            return new ResponseEntity<>(adminEntity, HttpStatus.OK);
        } catch (GlobalNotFoundException ex) {
            throw new GlobalNotFoundException(ex.getEntityName());
        } catch (Exception ex) {
            throw new GlobalDatabaseException("ADMIN", ex.getCause().getCause().getMessage());
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntityModel<AdminDTO>> update(@RequestBody @Valid Admin admin) {
        try {
            AdminDTO updatedAdminDTO = adminService.update(admin);
            EntityModel<AdminDTO> updatedAdminDTOEntity = new EntityModel<>(
                    updatedAdminDTO,
                    linkTo(methodOn(AdminController.class).findByEmail(admin.getEmail())).withSelfRel());
            return new ResponseEntity<>(updatedAdminDTOEntity, HttpStatus.CREATED);
        } catch (GlobalNotFoundException ex) {
            throw new GlobalNotFoundException(ex.getEntityName());
        } catch (Exception ex) {
            throw new GlobalDatabaseException("ADMIN", ex.getCause().getCause().getMessage());
        }
    }

    @DeleteMapping("/by-email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntityModel<AdminDTO>> deleteByEmail(@PathVariable String email) {
        try {
            AdminDTO deletedAdminDTO = adminService.deleteByEmail(email);
            EntityModel<AdminDTO> deletedAdminDTOEntity = new EntityModel<>(deletedAdminDTO);
            return new ResponseEntity<>(deletedAdminDTOEntity, HttpStatus.OK);
        } catch (GlobalNotFoundException ex) {
            throw new GlobalNotFoundException(ex.getEntityName());
        } catch (Exception ex) {
            throw new GlobalDatabaseException("ADMIN", ex.getCause().getCause().getMessage());
        }
    }
}
