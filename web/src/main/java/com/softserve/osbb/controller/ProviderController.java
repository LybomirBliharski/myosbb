package com.softserve.osbb.controller;

import com.softserve.osbb.dto.ProviderPageDTO;
import com.softserve.osbb.dto.mappers.ProviderPageDtoMapper;
import com.softserve.osbb.model.Provider;
import com.softserve.osbb.service.AttachmentService;
import com.softserve.osbb.service.ProviderService;
import com.softserve.osbb.service.ProviderTypeService;
import com.softserve.osbb.util.EntityNotFoundException;
import com.softserve.osbb.util.paging.PageDataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by Anastasiia Fedorak on 12.07.2016.
 */
@RestController
@RequestMapping(value = "/restful/provider")
@CrossOrigin
public class ProviderController {

    private static Logger logger = LoggerFactory.getLogger(ProviderController.class);

    @Autowired
    ProviderService providerService;

    @Autowired
    ProviderTypeService providerTypeService;

    @Autowired
    AttachmentService attachmentService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<Resource<ProviderPageDTO>>> listAllProviders() {
        List<Provider> providerList = providerService.findAllProviders();
        logger.info("getting all providers: " + providerList);

        List<ProviderPageDTO> providerPageDtoList = new ArrayList<>();

        final List<Resource<ProviderPageDTO>> resourceProviderList = new ArrayList<>();
        providerList.stream().forEach((provider) -> {
            ProviderPageDTO providerPageDTO =  ProviderPageDtoMapper.mapProviderEntityToDto(provider.getProviderId(), provider);
            try {
                resourceProviderList.add(addResourceLinkToProvider(providerPageDTO));
            } catch (EntityNotFoundException e) {
                logger.error(e.getMessage());
            }
        });
        return new ResponseEntity<>(resourceProviderList, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<PageDataObject<Resource<ProviderPageDTO>>> listAllProviders(
            @RequestParam(value = "pageNum", required = true) Integer pageNumber,
            @RequestParam(value = "sortedBy", required = false) String sortedBy,
            @RequestParam(value = "asc", required = false) Boolean ascOrder,
            @RequestParam(value = "actv", required = false) Boolean onlyActive) {
        logger.info("getting all providers by page number: " + pageNumber);
        Page<Provider> providersByPage = providerService.getProviders(pageNumber, sortedBy, ascOrder);
        if ((onlyActive == null) ? false : onlyActive) {
            logger.warn("getting only active providers");
            providersByPage = providerService.findByActiveTrue(pageNumber, sortedBy, ascOrder);
        }
        int currentPage = providersByPage.getNumber() + 1;
        logger.info("current page : " + currentPage);
        int begin = Math.max(1, currentPage - 5);
        logger.info("starts with: " + begin);
        int totalPages = providersByPage.getTotalPages();
        int end = Math.min(currentPage + 5, totalPages);
        logger.info("ends with: " + totalPages);

        List<Resource<ProviderPageDTO>> resourceList = new ArrayList<>();
        providersByPage.forEach((provider) -> {
            ProviderPageDTO providerPageDTO = ProviderPageDtoMapper.mapProviderEntityToDto(provider.getProviderId(), provider);
            try {
                resourceList.add(addResourceLinkToProvider(providerPageDTO));
            } catch (EntityNotFoundException e) {
                logger.error(e.getMessage());
            }
        });

        PageDataObject<Resource<ProviderPageDTO>> pageDataObject = new PageDataObject<>();
        pageDataObject.setRows(resourceList);
        pageDataObject.setCurrentPage(Integer.valueOf(currentPage).toString());
        pageDataObject.setBeginPage(Integer.valueOf(begin).toString());
        pageDataObject.setEndPage(Integer.valueOf(end).toString());
        pageDataObject.setTotalPages(Integer.valueOf(totalPages).toString());

        return new ResponseEntity<>(pageDataObject, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Resource<ProviderPageDTO>> getProviderById(@PathVariable("id") Integer providerId) {
        logger.info("fetching provider by id: " + providerId);
        Provider provider = providerService.findOneProviderById(providerId);
        Resource<ProviderPageDTO> providerResource = null;
        try {
            providerResource = addResourceLinkToProvider(
                    ProviderPageDtoMapper.mapProviderEntityToDto(providerId, provider));
        } catch (EntityNotFoundException e) {
            logger.error(e.getMessage());
        }
        return new ResponseEntity<>(providerResource, HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<Resource<ProviderPageDTO>> createProvider(@RequestBody ProviderPageDTO providerPageDTO) {
        Resource<ProviderPageDTO> providerPageDtoResource;
        Provider provider = ProviderPageDtoMapper.getProviderEntityFromDto(providerService, providerTypeService, attachmentService, providerPageDTO);
        try {
            providerService.saveProvider(provider);
            ProviderPageDTO providerPageDto =  ProviderPageDtoMapper.mapProviderEntityToDto(provider.getProviderId(), provider);
            logger.info("provider dto" + providerPageDto.getName());
            providerPageDtoResource = addResourceLinkToProvider(providerPageDto);
            logger.debug("added link");
        } catch (EntityNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(providerPageDtoResource, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Resource<ProviderPageDTO>> updateProvider(@PathVariable("id") Integer providerId,
                                                                    @RequestBody ProviderPageDTO providerPageDTO) {

        Resource<ProviderPageDTO> providerResource = null;
        Provider provider;
        try {
            if (providerService.existsProvider(providerId)){
                Provider provider1 = providerService.findOneProviderById(providerId);
                logger.info("updating provider by id: " + providerId);
                provider = ProviderPageDtoMapper.
                        getProviderEntityFromDto(providerService, providerTypeService, attachmentService, providerPageDTO);
                logger.info("id=" + provider.getProviderId() + "; name="+ provider.getName()
                +"; desc=" + provider.getDescription()+"; per="+provider.getPeriodicity()
                +"; add=" + provider.getAddress() + "; type=" + provider.getType()
                + "; actv=" + provider.isActive());
                providerService.saveProvider(provider);
                ProviderPageDTO providerPageDto = ProviderPageDtoMapper.
                        mapProviderEntityToDto(providerId, provider);
                providerResource = addResourceLinkToProvider(providerPageDto);
            } else logger.error("provider not found");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(providerResource, HttpStatus.OK);
    }

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public ResponseEntity<List<Resource<ProviderPageDTO>>> getProvidersByName(
            @RequestParam(value = "name", required = true) String name) {
        logger.info("fetching provider by search parameter: " + name);
        List<Provider> providersBySearchTerm = providerService.findProvidersByNameOrDescription(name);
        if (providersBySearchTerm.isEmpty()) {
            logger.warn("no providers were found");
        }
        List<Resource<ProviderPageDTO>> resourceProviderList = new ArrayList<>();
        providersBySearchTerm.stream().forEach((provider) -> {
            try {
                resourceProviderList.add(addResourceLinkToProvider(
                        ProviderPageDtoMapper.mapProviderEntityToDto(provider.getProviderId(), provider)));
            } catch (EntityNotFoundException e) {
                logger.error(e.getMessage());
            }
        });
        return new ResponseEntity<>(resourceProviderList, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<ProviderPageDTO> deleteProviderById(@PathVariable("id") Integer providerId){
        logger.info("removing provider by id: " + providerId);
       if (providerService.existsProvider(providerId)){
           providerService.deleteProviderById(providerId);
       }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public ResponseEntity<ProviderPageDTO> deleteAllProviders() {
        logger.info("removing all providers");
        providerService.deleteAllProviders();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Resource<ProviderPageDTO> addResourceLinkToProvider(ProviderPageDTO provider) throws EntityNotFoundException {
        logger.info("adding resource link");
        if (provider == null) throw new EntityNotFoundException();
        Resource<ProviderPageDTO> providerResource = new Resource<>(provider);
        providerResource.add(linkTo(methodOn(ProviderController.class)
                .getProviderById(provider.getProviderId()))
                .withSelfRel());
        return providerResource;
    }
}
