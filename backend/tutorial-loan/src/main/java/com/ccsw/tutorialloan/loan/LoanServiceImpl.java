package com.ccsw.tutorialloan.loan;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccsw.tutorialloan.client.ClientClient;
import com.ccsw.tutorialloan.client.model.ClientDto;
import com.ccsw.tutorialloan.common.criteria.SearchCriteria;
import com.ccsw.tutorialloan.game.GameClient;
import com.ccsw.tutorialloan.game.model.GameDto;
import com.ccsw.tutorialloan.loan.exceptions.ApplicationException;
import com.ccsw.tutorialloan.loan.model.Loan;
import com.ccsw.tutorialloan.loan.model.LoanDto;
import com.ccsw.tutorialloan.loan.model.LoanSearchDto;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ClientClient clientClient;

    @Autowired
    private GameClient gameClient;

    @Autowired
    private ModelMapper mapper;

    @Override
    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    @Override
    public Page<LoanDto> findPage(LoanSearchDto dto) {
        Page<Loan> loansPage = loanRepository.findAll(dto.getPageable().getPageable());

        List<LoanDto> loanDtos = loansPage.stream().map(loan -> {
            LoanDto loanDto = mapper.map(loan, LoanDto.class);

            ClientDto client = findClientById(loan.getClientId());
            loanDto.setClient(client);

            GameDto game = findGameById(loan.getGameId());
            loanDto.setGame(game);

            return loanDto;
        }).collect(Collectors.toList());

        return new PageImpl<>(loanDtos, loansPage.getPageable(), loansPage.getTotalElements());
    }

    @Override
    public void save(Long id, LoanDto loanDto) {

        validateLoanDto(loanDto);

        Loan loan = (id == null) ? new Loan()
                : loanRepository.findById(id).orElseThrow(() -> new ApplicationException("Préstamo no encontrado"));

        loan.setClientId(loanDto.getClient().getId());
        loan.setGameId(loanDto.getGame().getId());
        loan.setStartDate(loanDto.getStartDate());
        loan.setEndDate(loanDto.getEndDate());

        loanRepository.save(loan);
    }

    @Override
    public void delete(Long id) {
        loanRepository.deleteById(id);
    }

    @Override
    public boolean validateLoan(LoanDto loanDto) {
        LocalDate startDate = loanDto.getStartDate();
        LocalDate endDate = loanDto.getEndDate();

        if (endDate.isBefore(startDate)) {
            throw new ApplicationException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        if (startDate.plusDays(14).isBefore(endDate)) {
            throw new ApplicationException("El periodo de préstamo no puede exceder los 14 días");
        }

        Specification<Loan> gameConflictSpec = Specification.where(new LoanSpecification(
                new SearchCriteria("gameId", "conflict", new LocalDate[] { startDate, endDate })));
        List<Loan> conflictingGameLoans = loanRepository.findAll(gameConflictSpec).stream()
                .filter(loan -> loan.getGameId().equals(loanDto.getGame().getId())).collect(Collectors.toList());
        if (!conflictingGameLoans.isEmpty()) {
            throw new ApplicationException("El juego ya está prestado en las fechas seleccionadas");
        }

        Specification<Loan> clientConflictSpec = Specification.where(new LoanSpecification(
                new SearchCriteria("clientId", "conflict", new LocalDate[] { startDate, endDate })));
        List<Loan> clientLoans = loanRepository.findAll(clientConflictSpec).stream()
                .filter(loan -> loan.getClientId().equals(loanDto.getClient().getId())).collect(Collectors.toList());
        if (clientLoans.size() >= 2) {
            throw new ApplicationException("El cliente ya tiene dos juegos prestados en las fechas seleccionadas");
        }

        return true;
    }

    @Override
    public Page<LoanDto> findLoansFiltered(LoanSearchDto dto) {

        Specification<Loan> spec = Specification.where(null);

        if (dto.getGameId() != null) {
            spec = spec.and(new LoanSpecification(new SearchCriteria("gameId", ":", dto.getGameId())));
        }
        if (dto.getClientId() != null) {
            spec = spec.and(new LoanSpecification(new SearchCriteria("clientId", ":", dto.getClientId())));
        }
        if (dto.getSearchDate() != null) {
            spec = spec.and(new LoanSpecification(new SearchCriteria("startDate", "dateRange", dto.getSearchDate())));
        }

        Page<Loan> loansPage = loanRepository.findAll(spec, dto.getPageable().getPageable());
        List<LoanDto> loanDtos = loansPage.getContent().stream().map(loan -> {
            LoanDto loanDto = mapper.map(loan, LoanDto.class);

            ClientDto client = findClientById(loan.getClientId());
            loanDto.setClient(client);

            GameDto game = findGameById(loan.getGameId());
            loanDto.setGame(game);

            return loanDto;
        }).collect(Collectors.toList());

        return new PageImpl<>(loanDtos, loansPage.getPageable(), loansPage.getTotalElements());
    }

    private void validateLoanDto(LoanDto loanDto) {

        if (loanDto.getClient() == null || loanDto.getClient().getId() == null) {
            throw new ApplicationException("El cliente es obligatorio");
        }

        if (loanDto.getGame() == null || loanDto.getGame().getId() == null) {
            throw new ApplicationException("El juego es obligatorio");
        }

        findClientById(loanDto.getClient().getId());
        findGameById(loanDto.getGame().getId());

        if (loanDto.getStartDate() == null) {
            throw new ApplicationException("La fecha de inicio es obligatoria");
        }

        if (loanDto.getEndDate() == null) {
            throw new ApplicationException("La fecha de fin es obligatoria");
        }

        if (loanDto.getEndDate().isBefore(loanDto.getStartDate())) {
            throw new ApplicationException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        if (loanDto.getStartDate().isBefore(LocalDate.now())) {
            throw new ApplicationException("No se puede crear un préstamo para una fecha pasada");
        }

        if (!validateLoan(loanDto)) {
            throw new ApplicationException("El juego ya está prestado en las fechas seleccionadas");
        }
    }

    private ClientDto findClientById(Long clientId) {
        return clientClient.findAll().stream().filter(c -> c.getId().equals(clientId)).findFirst()
                .orElseThrow(() -> new ApplicationException("Cliente no encontrado"));
    }

    private GameDto findGameById(Long gameId) {
        return gameClient.findAll().stream().filter(g -> g.getId().equals(gameId)).findFirst()
                .orElseThrow(() -> new ApplicationException("Juego no encontrado"));
    }
}