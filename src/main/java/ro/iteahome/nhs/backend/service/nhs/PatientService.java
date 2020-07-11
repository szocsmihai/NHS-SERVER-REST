package ro.iteahome.nhs.backend.service.nhs;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import ro.iteahome.nhs.backend.controller.nhs.PatientController;
import ro.iteahome.nhs.backend.exception.business.GlobalAlreadyExistsException;
import ro.iteahome.nhs.backend.exception.business.GlobalNotFoundException;
import ro.iteahome.nhs.backend.model.nhs.dto.PatientDTO;
import ro.iteahome.nhs.backend.model.nhs.entity.Patient;
import ro.iteahome.nhs.backend.repository.nhs.PatientRepository;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PatientService {

// DEPENDENCIES: -------------------------------------------------------------------------------------------------------

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ModelMapper modelMapper;

// C.R.U.D. METHODS: ---------------------------------------------------------------------------------------------------

    public EntityModel<PatientDTO> add(Patient patient) {
        if (!patientRepository.existsByCnp(patient.getCnp())) {
            patientRepository.save(patient);
            Patient savedPatient = patientRepository.getByCnp(patient.getCnp());
            PatientDTO savedPatientDTO = modelMapper.map(savedPatient, PatientDTO.class);
            return new EntityModel<>(
                    savedPatientDTO,
                    linkTo(methodOn(PatientController.class).findByCnp(savedPatient.getCnp())).withSelfRel());
        } else {
            throw new GlobalAlreadyExistsException("PATIENT");
        }
    }

    public EntityModel<PatientDTO> findByCnp(String cnp) {
        Optional<Patient> optionalPatient = patientRepository.findByCnp(cnp);
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            PatientDTO patientDTO = modelMapper.map(patient, PatientDTO.class);
            return new EntityModel<>(
                    patientDTO,
                    linkTo(methodOn(PatientController.class).findByCnp(patient.getCnp())).withSelfRel());
        } else {
            throw new GlobalNotFoundException("PATIENT");
        }
    }

    public EntityModel<PatientDTO> update(Patient patient) {
        if (patientRepository.existsById(patient.getId())) {
            Patient updatedPatient = patientRepository.save(patient);
            PatientDTO updatedPatientDTO = modelMapper.map(updatedPatient, PatientDTO.class);
            return new EntityModel<>(
                    updatedPatientDTO,
                    linkTo(methodOn(PatientController.class).findByCnp(updatedPatient.getCnp())).withSelfRel());
        } else {
            throw new GlobalNotFoundException("PATIENT");
        }
    }

    public EntityModel<PatientDTO> deleteByCnp(String cnp) {
        Optional<Patient> optionalPatient = patientRepository.findByCnp(cnp);
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            PatientDTO patientDTO = modelMapper.map(patient, PatientDTO.class);
            patientRepository.delete(patient);
            return new EntityModel<>(patientDTO);
        } else {
            throw new GlobalNotFoundException("PATIENT");
        }
    }

// OTHER METHODS: -----------------------------------------------------------------------------------------------------

}
