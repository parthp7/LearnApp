package com.ms.learnapp.service;

import com.ms.learnapp.domain.SessionParticipation;
import com.ms.learnapp.repository.SessionParticipationRepository;
import com.ms.learnapp.service.dto.SessionParticipationDTO;
import com.ms.learnapp.service.mapper.SessionParticipationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link SessionParticipation}.
 */
@Service
public class SessionParticipationService {

    private final Logger log = LoggerFactory.getLogger(SessionParticipationService.class);

    private final SessionParticipationRepository sessionParticipationRepository;

    private final SessionParticipationMapper sessionParticipationMapper;

    public static final String CREATE = "create";
    public static final String UPDATE = "update";


    public SessionParticipationService(SessionParticipationRepository sessionParticipationRepository, SessionParticipationMapper sessionParticipationMapper) {
        this.sessionParticipationRepository = sessionParticipationRepository;
        this.sessionParticipationMapper = sessionParticipationMapper;
    }

    /**
     * Save a sessionParticipation.
     *
     * @param sessionParticipationDTO the entity to save.
     * @return the persisted entity.
     */
    public SessionParticipationDTO save(String createUpdateFlag, SessionParticipationDTO sessionParticipationDTO) {
        log.debug("Request to save SessionParticipation : {}", sessionParticipationDTO);
        SessionParticipation sessionParticipation = sessionParticipationMapper.toEntity(sessionParticipationDTO);
        boolean save = true;
        if(createUpdateFlag.equals(CREATE)) {
            List<SessionParticipationDTO> matchingSessions = findMatchingSessionParticipationsForUser(sessionParticipationDTO);
            if(matchingSessions != null && matchingSessions.size() > 0)
                save = false;
        }
        if(save)
            sessionParticipation = sessionParticipationRepository.save(sessionParticipation);
        return sessionParticipationMapper.toDto(sessionParticipation);
    }

    /**
     * Get all the sessionParticipations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<SessionParticipationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all SessionParticipations");
        return sessionParticipationRepository.findAll(pageable)
            .map(sessionParticipationMapper::toDto);
    }

    /**
     * Get all the matching sessionParticipations for a given user.
     *
     * @param sessionParticipationDTO dto containing user id and comma delimited session id list.
     * @return the list of entities.
     */
    public List<SessionParticipationDTO> findMatchingSessionParticipationsForUser(SessionParticipationDTO dto) {
        log.debug("Request to get all SessionParticipations");
        List<String> sessionIds = Arrays.asList(dto.getSessionId().split(","));
        String userId = dto.getUserId();
        return sessionParticipationRepository.findSessionParticipationByUserIdAndSessionIdContaining(userId, sessionIds)
        .stream()
        .map(sessionParticipationMapper::toDto)
        .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one sessionParticipation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<SessionParticipationDTO> findOne(String id) {
        log.debug("Request to get SessionParticipation : {}", id);
        return sessionParticipationRepository.findById(id)
            .map(sessionParticipationMapper::toDto);
    }

    /**
     * Delete the sessionParticipation by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete SessionParticipation : {}", id);
        sessionParticipationRepository.deleteById(id);
    }
}