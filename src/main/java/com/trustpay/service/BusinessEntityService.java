package com.trustpay.service;
import com.trustpay.entity.BusinessEntity;
import com.trustpay.repository.BusinessEntityRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class BusinessEntityService {
    private final BusinessEntityRepository repo;
    public BusinessEntityService(BusinessEntityRepository repo) { this.repo = repo; }
    public BusinessEntity save(BusinessEntity b) { return repo.save(b); }
    public Optional<BusinessEntity> findByUpiId(String upi) { return repo.findByUpiId(upi); }
}