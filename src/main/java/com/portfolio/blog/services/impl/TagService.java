package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.dto.post.PostRequest;
import com.portfolio.blog.domain.dto.tag.TagRequest;
import com.portfolio.blog.domain.dto.tag.TagResponse;
import com.portfolio.blog.domain.entities.TagEntity;
import com.portfolio.blog.mappers.TagMapper;
import com.portfolio.blog.repositories.TagRepository;
import com.portfolio.blog.services.TagServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TagService implements TagServiceInterface {

    private final TagRepository repository;
    private final TagMapper mapper;
    private final AuthorizationService authorizationService;

    @Override
    public TagResponse create(TagRequest request) {
        TagEntity entity = mapper.requestToEntity(request);
        return mapper.entityToResponse(repository.save(entity));
    }

    @Override
    public List<TagResponse> findAll() {
        return repository.findAll().stream().map(
                        mapper::entityToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID uuid) {

        try{
            authorizationService.authorizeCategoryOrTagDeleting();
            repository.deleteById(uuid);
        } catch (AuthorizationServiceException e) {
            throw new AccessDeniedException("");
        }
    }

    @Override
    public List<TagEntity> verifyTags(PostRequest request) {

        List<TagRequest> tagsFromRequest = request.getTags();

        List<String> nameOfTagsFromRequest = tagsFromRequest.stream()
                .map(TagRequest::getName)
                .toList();

        List<TagEntity> tags = repository.findTagEntitiesByNameIn(nameOfTagsFromRequest);

        if (tags.size() != tagsFromRequest.size())
            throw new EntityNotFoundException("Only existing tags can be assigned");

        return tags;
    }
}
