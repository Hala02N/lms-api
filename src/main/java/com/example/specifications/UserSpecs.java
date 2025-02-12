package com.example.specifications;

import com.example.entities.UserEntity;
import com.example.events.FilterDTO;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class UserSpecs {
    // Specifications to filter users by major
    public static Specification<UserEntity> hasMajor(String major){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("major"), major);
    }
    // Specifications to filter users by first name
    public static Specification<UserEntity> hasfirstName(String firstName){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("first_name"), firstName);
    }

    public static Specification<UserEntity> dynamicFilter(List<FilterDTO> filterDTOList){
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            filterDTOList.forEach(filter -> {
                Predicate predicate = criteriaBuilder.equal(root.get(filter.getColumnName()), filter.getColumnValue());
                predicates.add(predicate);
            });
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        });
    }

    // Specifications to filter users by major
    public static Specification<UserEntity> hasMajorAndFirstName(String major, String firstName){
        Specification<UserEntity> userWithMajor = null;
        if(major != null){
            userWithMajor = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("major"), major);
        }
        if (firstName != null){
            userWithMajor.or((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("first_name"), firstName));
        }

        return userWithMajor;
    }

//    // Specification to filter freshmen
//    public static Specification<UserEntity> isFreshman(){
//        return ((root, query, criteriaBuilder) -> {
//            LocalDate currDate = LocalDate.now();
//            LocalDate currDateMinusYear = LocalDate.now().minusYears(1);
//            Date userRegistrationDate = (Date)root.get("date_of_registration");
//            // add a year for the user registration date to use it in the comparison
//            //Date addYearAfterRegistrationDate = Calendar.set(Calendar.YEAR, year + 1900)
//            criteriaBuilder.between(currDateMinusYear, (Date)root.get("date_of_registration"), currDate);
//        })
//    }
}
