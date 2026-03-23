package com.portfolio.blog.services;

import java.util.UUID;

public interface AuthorizationServiceInterface {

    public boolean canDelete(UUID postToDelete);
    public boolean canUpdate(UUID postToUpdate);
}
