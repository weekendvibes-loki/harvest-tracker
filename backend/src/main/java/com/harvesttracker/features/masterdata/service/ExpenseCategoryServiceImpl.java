package com.harvesttracker.features.masterdata.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.common.exception.DuplicateResourceException;
import com.harvesttracker.common.exception.ResourceNotFoundException;
import com.harvesttracker.features.masterdata.domain.ExpenseCategory;
import com.harvesttracker.features.masterdata.dto.ExpenseCategoryDto;
import com.harvesttracker.features.masterdata.mapper.ExpenseCategoryMapper;
import com.harvesttracker.features.masterdata.repository.ExpenseCategoryRepository;
import com.harvesttracker.features.masterdata.repository.spec.MasterDataSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

    private final ExpenseCategoryRepository repository;
    private final ExpenseCategoryMapper mapper;

    public ExpenseCategoryServiceImpl(ExpenseCategoryRepository repository, ExpenseCategoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public PagedResponse<ExpenseCategoryDto.ExpenseCategoryResponse> getAllExpenseCategories(
            int page, int size, String sort, String direction, String search, Boolean isActive) {

        Sort.Direction sortDirection = "DESC".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortProperty = (sort != null && !sort.trim().isEmpty()) ? sort.trim() : "sortOrder";
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortProperty));

        Specification<ExpenseCategory> spec = MasterDataSpecification.expenseCategorySpec(search, isActive);
        Page<ExpenseCategory> pageResult = repository.findAll(spec, pageable);

        List<ExpenseCategoryDto.ExpenseCategoryResponse> content = pageResult.getContent().stream()
                .map(mapper::toResponse)
                .toList();

        return PagedResponse.of(content, pageResult);
    }

    @Override
    public ExpenseCategoryDto.ExpenseCategoryResponse getExpenseCategoryById(Long id) {
        ExpenseCategory category = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense category not found with ID: " + id));
        return mapper.toResponse(category);
    }

    @Override
    @Transactional
    public ExpenseCategoryDto.ExpenseCategoryResponse createExpenseCategory(ExpenseCategoryDto.ExpenseCategoryRequest request) {
        validateUnique(request.getName(), request.getCode(), null);

        ExpenseCategory category = mapper.toEntity(request);
        ExpenseCategory saved = repository.save(category);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ExpenseCategoryDto.ExpenseCategoryResponse updateExpenseCategory(Long id, ExpenseCategoryDto.ExpenseCategoryRequest request) {
        ExpenseCategory category = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense category not found with ID: " + id));

        validateUnique(request.getName(), request.getCode(), id);

        mapper.updateEntity(category, request);
        ExpenseCategory updated = repository.save(category);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public ExpenseCategoryDto.ExpenseCategoryResponse toggleStatus(Long id, boolean isActive) {
        ExpenseCategory category = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense category not found with ID: " + id));

        category.setIsActive(isActive);
        ExpenseCategory updated = repository.save(category);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteExpenseCategory(Long id) {
        ExpenseCategory category = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense category not found with ID: " + id));

        category.setDeletedAt(OffsetDateTime.now());
        category.setIsActive(false);
        repository.save(category);
    }

    private void validateUnique(String name, String code, Long id) {
        if (id == null) {
            if (repository.existsByNameIgnoreCaseAndDeletedAtIsNull(name)) {
                throw new DuplicateResourceException("Expense category with name '" + name + "' already exists");
            }
            if (repository.existsByCodeIgnoreCaseAndDeletedAtIsNull(code)) {
                throw new DuplicateResourceException("Expense category with code '" + code + "' already exists");
            }
        } else {
            if (repository.existsByNameIgnoreCaseAndIdNotAndDeletedAtIsNull(name, id)) {
                throw new DuplicateResourceException("Expense category with name '" + name + "' already exists");
            }
            if (repository.existsByCodeIgnoreCaseAndIdNotAndDeletedAtIsNull(code, id)) {
                throw new DuplicateResourceException("Expense category with code '" + code + "' already exists");
            }
        }
    }
}
