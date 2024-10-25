package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.HelpRequest;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.HelpRequestRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * This is a REST controller for HelpRequest
 */

@Tag(name = "HelpRequest")
@RequestMapping("/api/helpRequest")
@RestController
@Slf4j
public class HelpRequestController extends ApiController {

    @Autowired
    HelpRequestRepository helpRequestRepository;

    /**
     * THis method returns a list of all helpRequest.
     * @return a list of all helpRequest
     */
    @Operation(summary= "List all ucsb dining commons")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<HelpRequest> allCommonss() {
        Iterable<HelpRequest> commons = helpRequestRepository.findAll();
        return commons;
    }

    /**
     * This method returns a single diningcommons.
     * @param code code of the diningcommons
     * @return a single diningcommons
     */
    @Operation(summary= "Get a single commons")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public HelpRequest getById(
            @Parameter(name="code") @RequestParam String code) {
        HelpRequest commons = helpRequestRepository.findById(code)
                .orElseThrow(() -> new EntityNotFoundException(HelpRequest.class, code));

        return commons;
    }

    /**
     * This method creates a new diningcommons. Accessible only to users with the role "ROLE_ADMIN".
     * @param code code of the diningcommons
     * @param name name of the diningcommons
     * @param hasSackMeal whether or not the commons has sack meals
     * @param hasTakeOutMeal whether or not the commons has take out meals
     * @param hasDiningCam whether or not the commons has a dining cam
     * @param latitude latitude of the commons
     * @param longitude logitude of the commons
     * @return the save diningcommons
     */
    @Operation(summary= "Create a new commons")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public HelpRequest postCommons(
        @Parameter(name="code") @RequestParam String code,
        @Parameter(name="name") @RequestParam String name,
        @Parameter(name="hasSackMeal") @RequestParam boolean hasSackMeal,
        @Parameter(name="hasTakeOutMeal") @RequestParam boolean hasTakeOutMeal,
        @Parameter(name="hasDiningCam") @RequestParam boolean hasDiningCam,
        @Parameter(name="latitude") @RequestParam double latitude,
        @Parameter(name="longitude") @RequestParam double longitude
        )
        {

        HelpRequest commons = new HelpRequest();
        commons.setCode(code);
        commons.setName(name);
        commons.setHasSackMeal(hasSackMeal);
        commons.setHasTakeOutMeal(hasTakeOutMeal);
        commons.setHasDiningCam(hasDiningCam);
        commons.setLatitude(latitude);
        commons.setLongitude(longitude);

        HelpRequest savedCommons = helpRequestRepository.save(commons);

        return savedCommons;
    }

    /**
     * Delete a diningcommons. Accessible only to users with the role "ROLE_ADMIN".
     * @param code code of the commons
     * @return a message indiciating the commons was deleted
     */
    @Operation(summary= "Delete a HelpRequest")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteCommons(
            @Parameter(name="code") @RequestParam String code) {
        HelpRequest commons = helpRequestRepository.findById(code)
                .orElseThrow(() -> new EntityNotFoundException(HelpRequest.class, code));

        helpRequestRepository.delete(commons);
        return genericMessage("HelpRequest with id %s deleted".formatted(code));
    }

    /**
     * Update a single diningcommons. Accessible only to users with the role "ROLE_ADMIN".
     * @param code code of the diningcommons
     * @param incoming the new commons contents
     * @return the updated commons object
     */
    @Operation(summary= "Update a single commons")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public HelpRequest updateCommons(
            @Parameter(name="code") @RequestParam String code,
            @RequestBody @Valid HelpRequest incoming) {

        HelpRequest commons = helpRequestRepository.findById(code)
                .orElseThrow(() -> new EntityNotFoundException(HelpRequest.class, code));


        commons.setName(incoming.getName());  
        commons.setHasSackMeal(incoming.getHasSackMeal());
        commons.setHasTakeOutMeal(incoming.getHasTakeOutMeal());
        commons.setHasDiningCam(incoming.getHasDiningCam());
        commons.setLatitude(incoming.getLatitude());
        commons.setLongitude(incoming.getLongitude());

        helpRequestRepository.save(commons);

        return commons;
    }
}
