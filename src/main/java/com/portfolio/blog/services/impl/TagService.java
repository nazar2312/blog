package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.dto.post.PostRequest;
import com.portfolio.blog.domain.dto.tag.TagRequest;
import com.portfolio.blog.domain.dto.tag.TagResponse;
import com.portfolio.blog.domain.entities.TagEntity;
import com.portfolio.blog.exceptions.ResourceNotFoundException;
import com.portfolio.blog.mappers.TagMapper;
import com.portfolio.blog.repositories.TagRepository;
import com.portfolio.blog.services.AuthorizationServiceInterface;
import com.portfolio.blog.services.TagServiceInterface;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService implements TagServiceInterface {

    private final TagRepository repository;
    private final TagMapper mapper;
    private final AuthorizationServiceInterface authorizationService;

    @Override
    public TagResponse create(TagRequest request) {
        TagEntity entity = mapper.requestToEntity(request);
        return mapper.entityToResponse(repository.save(entity));
    }

    @Override
    public Set<TagResponse> findAll() {

        return repository.findAll().stream().map(
                        mapper::entityToResponse)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void deleteById(UUID uuid) {

        authorizationService.authorizeCategoryOrTagDeleting(); //Exception is thrown if not authorized;
        repository.deleteById(uuid);
    }

    @Override
    public Set<TagEntity> verifyTags(PostRequest request) {

        Set<TagRequest> tagsFromRequest = request.getTags();

        List<String> nameOfTagsFromRequest = tagsFromRequest.stream()
                .map(TagRequest::getName)
                .toList();

        Set<TagEntity> tags = repository.findTagEntitiesByNameIn(nameOfTagsFromRequest);

        if (tags.size() != tagsFromRequest.size())

            throw new ResourceNotFoundException("Only existing tags can be assigned");

        return tags;
    }
}
