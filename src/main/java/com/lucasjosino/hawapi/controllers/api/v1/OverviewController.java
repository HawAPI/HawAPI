package com.lucasjosino.hawapi.controllers.api.v1;

import com.lucasjosino.hawapi.controllers.interfaces.BaseOverviewControllerInterface;
import com.lucasjosino.hawapi.controllers.utils.ResponseUtils;
import com.lucasjosino.hawapi.exceptions.BadRequestException;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.exceptions.SaveConflictException;
import com.lucasjosino.hawapi.models.dto.OverviewDTO;
import com.lucasjosino.hawapi.models.dto.translation.OverviewTranslationDTO;
import com.lucasjosino.hawapi.models.properties.OpenAPIProperty;
import com.lucasjosino.hawapi.services.impl.OverviewServiceImpl;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

@RestController
@RequestMapping("/api/v1")
@Tag(
        name = "Overview",
        description = "Endpoints for managing API overview",
        externalDocs = @ExternalDocumentation(
                url = "/docs"
        )
)
public class OverviewController implements BaseOverviewControllerInterface {

    private final OverviewServiceImpl overviewService;

    private final List<String> endpoints;

    private final OpenAPIProperty apiConfig;

    private final ResponseUtils responseUtils;

    private final RequestMappingHandlerMapping mappingHandler;

    @Autowired
    public OverviewController(
            OverviewServiceImpl overviewService,
            List<String> endpoints,
            OpenAPIProperty apiConfig,
            ResponseUtils responseUtils,
            RequestMappingHandlerMapping mappingHandler
    ) {
        this.overviewService = overviewService;
        this.endpoints = endpoints;
        this.apiConfig = apiConfig;
        this.responseUtils = responseUtils;
        this.mappingHandler = mappingHandler;
    }

    @Operation(summary = "Get all API endpoints")
    public ResponseEntity<List<String>> getEndpoints() {
        return ResponseEntity.ok(endpoints);
    }

    /**
     * Method that get all overview translations
     *
     * @return A {@link List} of {@link OverviewTranslationDTO} or empty
     * @since 1.0.0
     */
    @Operation(summary = "Get all API overview translations")
    public ResponseEntity<List<OverviewTranslationDTO>> findAllOverviewTranslations() {
        return ResponseEntity.ok(overviewService.findAllOverviewTranslations());
    }

    /**
     * Method that get API overview
     *
     * @param language An {@link String} that specify a language filter
     * @return An single {@link OverviewDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Get API overview")
    public ResponseEntity<OverviewDTO> findOverview(String language) {
        language = defaultIfEmpty(language, responseUtils.getDefaultLanguage());

        return ResponseEntity.ok(overviewService.findOverviewBy(language));
    }

    /**
     * Method that get a single overview translation
     *
     * @param language An {@link String} that specify a language filter
     * @return An single {@link OverviewTranslationDTO}
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Get API overview translation")
    public ResponseEntity<OverviewTranslationDTO> findOverviewTranslationBy(String language) {
        OverviewTranslationDTO translation = overviewService.findOverviewTranslationBy(language);
        HttpHeaders headers = responseUtils.getHeaders(translation.getLanguage());

        return ResponseEntity.ok().headers(headers).body(translation);
    }

    /**
     * Method that crates an overview
     *
     * @param dto An {@link OverviewDTO} with all overview fields
     * @return An {@link OverviewDTO} with the saved object
     * @throws BadRequestException   If dto validation fails
     * @throws SaveConflictException If language already exist
     * @since 1.0.0
     */
    @Operation(summary = "Save API overview", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<OverviewDTO> saveOverview(OverviewDTO dto) {
        OverviewDTO episode = overviewService.saveOverview(dto);
        HttpHeaders headers = responseUtils.getHeaders(episode.getLanguage());

        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(episode);
    }

    /**
     * Method that crates an overview translation
     *
     * @param dto An {@link OverviewTranslationDTO} with all overview fields
     * @return An {@link OverviewTranslationDTO} with the saved object
     * @throws BadRequestException   If dto validation fails
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Save API overview translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<OverviewTranslationDTO> saveOverviewTranslation(OverviewTranslationDTO dto) {
        OverviewTranslationDTO translation = overviewService.saveOverviewTranslation(
                responseUtils.getDefaultLanguage(),
                dto
        );
        HttpHeaders headers = responseUtils.getHeaders(translation.getLanguage());

        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(translation);
    }

    /**
     * Method that updates an overview
     *
     * @param patch An {@link OverviewDTO} with updated overview fields
     * @return An {@link OverviewDTO} with the updated object
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Patch API overview", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<OverviewDTO> patchOverview(OverviewDTO patch) throws IOException {
        // All translation models will be queried with 'eager' type.
        // If 'language' is not provided, query will return more than one result, resulting in an error.
        patch.setLanguage(responseUtils.getDefaultLanguage());

        overviewService.patchOverview(patch);
        return ResponseEntity.ok(patch);
    }

    /**
     * Method that updates an overview translation
     *
     * @param language An {@link String} that specify a language filter
     * @param patch    An {@link OverviewTranslationDTO} with updated overview fields
     * @return An {@link OverviewTranslationDTO} with the updated object
     * @throws ItemNotFoundException If no item was found
     * @throws SaveConflictException If language already exist
     * @since 1.0.0
     */
    @Operation(summary = "Patch API overview translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<OverviewTranslationDTO> patchOverviewTranslation(
            String language,
            OverviewTranslationDTO patch
    ) throws IOException {
        overviewService.patchOverviewTranslation(language, patch);
        HttpHeaders headers = responseUtils.getHeaders(language);

        return ResponseEntity.ok().headers(headers).body(patch);
    }

    /**
     * Method that delete an overview and all translations
     *
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Delete API overview", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<Void> deleteOverview() {
        overviewService.deleteOverview();
        return ResponseEntity.noContent().build();
    }

    /**
     * Method that delete an overview translation
     *
     * @param language An {@link String} that specify a language filter
     * @throws ItemNotFoundException If no item was found
     * @since 1.0.0
     */
    @Operation(summary = "Delete API overview translation", security = @SecurityRequirement(name = "Bearer"))
    public ResponseEntity<Void> deleteOverviewTranslation(String language) {
        overviewService.deleteOverviewTranslation(language);
        return ResponseEntity.noContent().build();
    }

    /**
     * Method that get all GET endpoints of the API
     */
    @PostConstruct
    public void findAllAPIEndpoints() {
        mappingHandler.getHandlerMethods().forEach((request, v) -> {
            // Check if the endpoint is a GET request.
            if (request.getMethodsCondition().toString().contains("GET")) {
                // Get the first item from Set.
                String value = request.getPatternValues().stream().findFirst().orElse(null);
                // Check if the endpoint is from the API.
                if (value != null && value.contains(apiConfig.getApiBaseUrl())) {
                    endpoints.add(value);
                }
            }
        });

        // Sort(alphabetically) this endpoint list.
        Collections.sort(endpoints);
    }
}
