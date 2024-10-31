package com.ccsw.tutorialloan.loan;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ccsw.tutorialloan.loan.model.LoanDto;
import com.ccsw.tutorialloan.loan.model.LoanSearchDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Loan", description = "API of Loan")
@RequestMapping(value = "/loan")
@RestController
@CrossOrigin(origins = "*")
public class LoanController {

    @Autowired
    LoanService loanService;

    @Autowired
    ModelMapper mapper;

    @Operation(summary = "Find Page", description = "Method that return a page of Loans")
    @RequestMapping(path = "/paginated", method = RequestMethod.POST)
    public Page<LoanDto> findPage(@RequestBody LoanSearchDto dto) {
        System.out.println("Received LoanSearchDto for pagination: " + dto);
        Page<LoanDto> page = this.loanService.findPage(dto);
        System.out.println("Loan page content: " + page.getContent());
        return page;
    }

    @Operation(summary = "Save or Update Loan", description = "Method to save or update a Loan")
    @RequestMapping(path = { "", "/{id}" }, method = { RequestMethod.POST, RequestMethod.PUT })
    public void saveLoan(@PathVariable(name = "id", required = false) Long id, @RequestBody LoanDto loanDto) {
        loanService.save(id, loanDto);
    }

    @Operation(summary = "Delete Loan", description = "Method to delete a Loan")
    @DeleteMapping("/{id}")
    public void deleteLoan(@PathVariable Long id) {
        loanService.delete(id);
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateLoan(@RequestBody LoanDto loanDto) {
        System.out.println("Validating loan DTO: " + loanDto);
        boolean isValid = loanService.validateLoan(loanDto);
        System.out.println("Loan validation result: " + isValid);
        return ResponseEntity.ok(isValid);
    }

    @Operation(summary = "Find Loans by filters", description = "Method to get loans by filters")
    @PostMapping("/filtered")
    public Page<LoanDto> findLoansFiltered(@RequestBody LoanSearchDto dto) {
        Page<LoanDto> loans = loanService.findLoansFiltered(dto);
        return loans;
    }
}
