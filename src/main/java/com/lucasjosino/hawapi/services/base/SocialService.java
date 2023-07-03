package com.lucasjosino.hawapi.services.base;

import com.lucasjosino.hawapi.models.dto.ActorSocialDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Base service interface that provides methods for manipulating (actor) social objects.
 *
 * @author Lucas Josino
 * @see Transactional
 * @see Cacheable
 * @see Pageable
 * @since 1.0.0
 */
public interface SocialService {

    @Cacheable(value = "findAllSocial")
    List<ActorSocialDTO> findAllSocials(UUID uuid);

    ActorSocialDTO findRandomSocial(UUID uuid);

    @Cacheable(value = "findSocialBy", key = "{ #uuid, #name }")
    ActorSocialDTO findSocialBy(UUID uuid, String name);

    @Transactional
    @CacheEvict(cacheNames = {"findAllSocial", "findSocialBy", "findAll", "findBy"}, allEntries = true)
    ActorSocialDTO saveSocial(UUID uuid, ActorSocialDTO dto);

    @Transactional
    @CacheEvict(cacheNames = {"findAllSocial", "findSocialBy", "findAll", "findBy"}, allEntries = true)
    void patchSocial(UUID uuid, String name, ActorSocialDTO patch) throws IOException;

    @Transactional
    @CacheEvict(cacheNames = {"findAllSocial", "findSocialBy", "findAll", "findBy"}, allEntries = true)
    void deleteSocial(UUID uuid, String name);
}
