package com.lucasjosino.hawapi.interfaces;

import com.lucasjosino.hawapi.models.base.BaseTranslationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

public interface TranslationInterface<D extends BaseTranslationDTO> {

    ResponseEntity<List<D>> findAllTranslationsBy(@PathVariable UUID uuid);

    ResponseEntity<D> findTranslationBy(@PathVariable UUID uuid, @PathVariable String language);

    ResponseEntity<D> saveTranslation(@PathVariable UUID uuid, @RequestBody D dto);

    ResponseEntity<D> patchTranslation(@PathVariable UUID uuid, @PathVariable String language, @RequestBody D dto);

    ResponseEntity<Void> deleteTranslation(@PathVariable UUID uuid, @PathVariable String language);
}
