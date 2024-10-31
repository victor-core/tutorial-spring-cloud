package com.ccsw.tutorialloan.loan;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ccsw.tutorialloan.loan.model.Loan;
import com.ccsw.tutorialloan.loan.model.LoanDto;
import com.ccsw.tutorialloan.loan.model.LoanSearchDto;

/**
 * @author ccsw
 *
 */
public interface LoanService {

    List<Loan> findAll();

    void save(Long id, LoanDto loanDto);

    void delete(Long id);

    boolean validateLoan(LoanDto loanDto);

    Page<LoanDto> findPage(LoanSearchDto dto);

    Page<LoanDto> findLoansFiltered(LoanSearchDto dto);
}
