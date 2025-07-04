package com.project.expense_tracker.Service;

import com.project.expense_tracker.Entity.Label;
import com.project.expense_tracker.Repository.LabelRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class LabelService {
    private final LabelRepository labelRepository;

    public LabelService(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    public Label findLabelById(Long id) {
        return labelRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("No label found with id: " + id));
    }
}
