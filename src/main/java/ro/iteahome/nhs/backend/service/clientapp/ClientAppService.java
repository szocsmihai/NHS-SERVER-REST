package ro.iteahome.nhs.backend.service.clientapp;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ro.iteahome.nhs.backend.controller.clientapp.ClientAppController;
import ro.iteahome.nhs.backend.exception.business.GlobalAlreadyExistsException;
import ro.iteahome.nhs.backend.exception.business.GlobalNotFoundException;
import ro.iteahome.nhs.backend.model.clientapp.dto.ClientAppCredentials;
import ro.iteahome.nhs.backend.model.clientapp.dto.ClientAppDTO;
import ro.iteahome.nhs.backend.model.clientapp.dto.RoleDTO;
import ro.iteahome.nhs.backend.model.clientapp.entity.ClientApp;
import ro.iteahome.nhs.backend.model.clientapp.entity.Role;
import ro.iteahome.nhs.backend.repository.clientapp.ClientAppRepository;
import ro.iteahome.nhs.backend.repository.clientapp.RoleRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ClientAppService implements UserDetailsService {

// DEPENDENCIES: -------------------------------------------------------------------------------------------------------

    @Autowired
    private ClientAppRepository clientAppRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

// METHODS: ------------------------------------------------------------------------------------------------------------

    public EntityModel<ClientAppDTO> add(ClientAppCredentials clientAppCredentials, RoleDTO initialRoleDTO) {
        if (!clientAppRepository.existsByName(clientAppCredentials.getName())) {

            ClientApp clientApp = modelMapper.map(clientAppCredentials, ClientApp.class);
            clientApp.setPassword(passwordEncoder.encode(clientAppCredentials.getPassword()));
            clientApp.setStatus(1);

            Role role = modelMapper.map(initialRoleDTO, Role.class);
            Set<Role> roles = new HashSet<>();
            roles.add(role);

            clientApp.setRoles(roles);
            ClientApp savedClientApp = clientAppRepository.save(clientApp);
            ClientAppDTO savedClientAppDTO = modelMapper.map(savedClientApp, ClientAppDTO.class);
            return new EntityModel<>(
                    savedClientAppDTO,
                    linkTo(methodOn(ClientAppController.class).findById(savedClientApp.getId())).withSelfRel());
        } else {
            throw new GlobalAlreadyExistsException("CLIENT APP");
        }
    }

    public EntityModel<ClientAppDTO> findById(int id) {
        Optional<ClientApp> optionalClientApp = clientAppRepository.findById(id);
        if (optionalClientApp.isPresent()) {
            ClientApp clientApp = optionalClientApp.get();
            ClientAppDTO clientAppDTO = modelMapper.map(clientApp, ClientAppDTO.class);
            return new EntityModel<>(
                    clientAppDTO,
                    linkTo(methodOn(ClientAppController.class).findById(id)).withSelfRel());
        } else {
            throw new GlobalNotFoundException("CLIENT APP");
        }
    }

    public EntityModel<ClientAppDTO> findByName(String name) {
        Optional<ClientApp> optionalClientApp = clientAppRepository.findByName(name);
        if (optionalClientApp.isPresent()) {
            ClientApp clientApp = optionalClientApp.get();
            ClientAppDTO clientAppDTO = modelMapper.map(clientApp, ClientAppDTO.class);
            return new EntityModel<>(
                    clientAppDTO,
                    linkTo(methodOn(ClientAppController.class).findById(clientAppDTO.getId())).withSelfRel());
        } else {
            throw new GlobalNotFoundException("CLIENT APP");
        }
    }

    public EntityModel<ClientAppDTO> update(ClientApp clientApp, String roleName) {
        if (clientAppRepository.existsById(clientApp.getId())) {
            Optional<Role> optionalRole = roleRepository.findByName(roleName);
            if (optionalRole.isPresent()) {
                Role role = optionalRole.get();
                clientApp.setPassword(passwordEncoder.encode(clientApp.getPassword()));
                clientApp.setRoles(new HashSet<>(Collections.singletonList(role)));
                ClientApp updatedClientApp = clientAppRepository.save(clientApp);
                ClientAppDTO updatedClientAppDTO = modelMapper.map(updatedClientApp, ClientAppDTO.class);
                return new EntityModel<>(
                        updatedClientAppDTO,
                        linkTo(methodOn(ClientAppController.class).findById(updatedClientApp.getId())).withSelfRel());
            } else {
                throw new GlobalNotFoundException("ROLE");
            }
        } else {
            throw new GlobalNotFoundException("CLIENT APP");
        }
    }


    public EntityModel<ClientAppDTO> deleteById(int id) {
        Optional<ClientApp> optionalClientApp = clientAppRepository.findById(id);
        if (optionalClientApp.isPresent()) {
            ClientApp clientApp = optionalClientApp.get();
            clientAppRepository.delete(clientApp);
            ClientAppDTO deletedClientAppDTO = modelMapper.map(clientApp, ClientAppDTO.class);
            return new EntityModel<>(deletedClientAppDTO);
        } else {
            throw new GlobalNotFoundException("CLIENT APP");
        }
    }

    public EntityModel<ClientAppDTO> deleteByName(String name) {
        Optional<ClientApp> optionalClientApp = clientAppRepository.findByName(name);
        if (optionalClientApp.isPresent()) {
            ClientApp clientApp = optionalClientApp.get();
            clientAppRepository.delete(clientApp);
            ClientAppDTO deletedClientAppDTO = modelMapper.map(clientApp, ClientAppDTO.class);
            return new EntityModel<>(deletedClientAppDTO);
        } else {
            throw new GlobalNotFoundException("CLIENT APP");
        }
    }

// OVERRIDDEN "UserDetailsService" METHODS: ----------------------------------------------------------------------------

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        return clientAppRepository.findOneByName(name)
                .orElseThrow(() -> new UsernameNotFoundException(name));
    }
}
