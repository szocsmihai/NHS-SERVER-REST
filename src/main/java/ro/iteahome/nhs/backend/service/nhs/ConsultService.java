package ro.iteahome.nhs.backend.service.nhs;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.iteahome.nhs.backend.exception.business.GlobalNotFoundException;
import ro.iteahome.nhs.backend.model.nhs.dto.ConsultDTO;
import ro.iteahome.nhs.backend.model.nhs.entity.*;
import ro.iteahome.nhs.backend.repository.nhs.*;
import ro.iteahome.nhs.backend.utils.ConsultList;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;

@Service
public class ConsultService {

// DEPENDENCIES: -------------------------------------------------------------------------------------------------------

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private DiagnosticRepository diagnosticRepository;

    @Autowired
    private ConsultRepository consultRepository;

    @Autowired
    private TreatmentRepository treatmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    public ConsultDTO add(ConsultDTO consultDTO) {
        new Consult();
        Consult consult;
        Diagnostic diagnostic;
        Treatment treatment;

        consult = extractConsult(consultDTO);
        diagnostic = extractDiagnostic(consultDTO, consult);
        treatment = extractTreatment(consultDTO, consult);

        diagnosticRepository.save(diagnostic);
        treatmentRepository.save(treatment);
        consultDTO.setDate(consult.getDate());
        return consultDTO;
    }


    private Consult extractConsult(ConsultDTO consultDTO) {
        Consult consult = new Consult();
        LocalDate localDate = LocalDate.now();
        LocalTime time = LocalTime.now();
        Date date = Date.from(localDate.atTime(time).toInstant(ZoneOffset.MAX));

//        get Doctor by cnp
        Doctor doctor = doctorRepository.getByCnp(consultDTO.getDoctor_cnp());
//        get Patient by cnp
        Patient patient = patientRepository.getByCnp(consultDTO.getPatient_cnp());
//      get institution by cui
        Institution institution = institutionRepository.getByCui(consultDTO.getInstitution_cui());

        consult.setDoctor(doctor);
        consult.setInstitution(institution);
        consult.setPatient(patient);
        consult.setDate(date);
        String unique_fetcher = consultDTO.getDoctor_cnp() + consultDTO.getPatient_cnp() + consult.getDate().toString();
        consult.setFetcher(unique_fetcher);
        consultRepository.save(consult);

        consult = consultRepository.getByFetcher(unique_fetcher);
        return consult;
    }

    private Treatment extractTreatment(ConsultDTO consultDTO, Consult consult) {
        Treatment treatment = new Treatment();
        treatment.setDescription(consultDTO.getTreatment_desc());
        treatment.setMaxDays(consultDTO.getMax_days());
        treatment.setMinDays(consultDTO.getMin_days());
        treatment.setSchedule(consultDTO.getTreatment_schedule());
        treatment.setConsult(consult);
        return treatment;
    }

    private Diagnostic extractDiagnostic(ConsultDTO consultDTO, Consult consult) {
        Diagnostic diagnostic = new Diagnostic();
        diagnostic.setConsult(consult);
        diagnostic.setDescription(consultDTO.getDiagnostic_desc());
        return diagnostic;
    }


    public ConsultList findConsult(String patientCnp) {
        Patient patient;

        patient = patientRepository.getByCnp(patientCnp);

        ArrayList<Consult> consults = consultRepository.findByPatient(patient);

        ArrayList<ConsultDTO> consultDTOS = new ArrayList<>();

        if (!consults.isEmpty()) {
            for (Consult consult : consults) {
                ConsultDTO consultDTO = getConsultDTOs(consult);
                consultDTOS.add(consultDTO);
                System.out.println(consultDTOS.toString());
            }
        } else {
            throw new GlobalNotFoundException("Consult DTO");
        }
        System.out.println("Step1");
        ConsultList consultList = new ConsultList();
        consultList.setConsultDTOList(consultDTOS);

        return consultList;
    }

    private ConsultDTO getConsultDTOs(Consult consult) {
        Treatment treatment = new Treatment();
        Diagnostic diagnostic = new Diagnostic();

        ConsultDTO consultDTO = new ConsultDTO();

        treatment = treatmentRepository.getByConsult(consult);
        diagnostic = diagnosticRepository.getByConsult(consult);

        consultDTO.setDate(consult.getDate());
        consultDTO.setDiagnostic_desc(diagnostic.getDescription());
        consultDTO.setDoctor_cnp(consult.getDoctor().getCnp());
        consultDTO.setInstitution_cui(consult.getInstitution().getCui());
        consultDTO.setMax_days(treatment.getMaxDays());
        consultDTO.setMin_days(treatment.getMinDays());
        consultDTO.setPatient_cnp(consult.getPatient().getCnp());
        consultDTO.setTreatment_desc(treatment.getDescription());
        consultDTO.setTreatment_schedule(treatment.getSchedule());

        return consultDTO;
    }

}
