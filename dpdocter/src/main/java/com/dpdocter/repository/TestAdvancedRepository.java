package com.dpdocter.repository;

import java.io.Serializable;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.beans.User;

@NoRepositoryBean
public interface TestAdvancedRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {
    public User findById(ID id);
}
