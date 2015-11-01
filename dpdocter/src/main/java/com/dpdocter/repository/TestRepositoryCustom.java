package com.dpdocter.repository;

import com.dpdocter.beans.User;

public interface TestRepositoryCustom {
    public User findByUsername(String username);
}
