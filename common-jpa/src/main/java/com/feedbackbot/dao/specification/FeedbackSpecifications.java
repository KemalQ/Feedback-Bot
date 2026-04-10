package com.feedbackbot.dao.specification;

import com.feedbackbot.entity.FeedbackMessage;
import org.springframework.data.jpa.domain.Specification;

public class FeedbackSpecifications {

    public static Specification<FeedbackMessage> hasBranch(String branch) {
        return (root, query, cb) -> branch == null ? null :
                cb.equal(root.join("user").get("branch"), branch);
    }

    public static Specification<FeedbackMessage> hasRole(String role) {
        return (root, query, cb) -> role == null ? null :
                cb.equal(root.join("user").get("role"), role);
    }

    public static Specification<FeedbackMessage> hasCriticality(Integer criticality) {
        return (root, query, cb) -> criticality == null ? null :
                cb.equal(root.get("criticality"), criticality);
    }
}