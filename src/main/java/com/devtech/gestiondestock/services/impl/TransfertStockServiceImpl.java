package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.TransfertStockDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.model.Entrepot;
import com.devtech.gestiondestock.model.Entreprise;
import com.devtech.gestiondestock.model.TransfertStock;
import com.devtech.gestiondestock.repository.TransfertStockRepository;
import com.devtech.gestiondestock.services.TransfertStockService;
import com.devtech.gestiondestock.validator.TransfertStockValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransfertStockServiceImpl implements TransfertStockService {
    private static final Logger log = LoggerFactory.getLogger(TransfertStockServiceImpl.class);
    private final TransfertStockRepository transfertStockRepository;

    public TransfertStockServiceImpl(TransfertStockRepository transfertStockRepository) {
        this.transfertStockRepository = transfertStockRepository;
    }

    @Override
    public TransfertStockDto save(TransfertStockDto dto) {
        List<String> errors = TransfertStockValidator.validate(dto);
        if (!errors.isEmpty()) {
            log.error("TransfertStock is not valid {}", dto);
            throw new InvalidEntityException("Le transfert de stock n'est pas valide", ErrorsCode.TRANSFERT_STOCK_NOT_VALID, errors);
        }
        TransfertStock transfert = TransfertStockDto.toEntity(dto);
        if (dto.getEntrepriseId() != null) {
            Entreprise e = new Entreprise();
            e.setId(dto.getEntrepriseId());
            transfert.setEntreprise(e);
        }
        if (dto.getEntrepotSourceId() != null) {
            Entrepot e = new Entrepot();
            e.setId(dto.getEntrepotSourceId());
            transfert.setEntrepotSource(e);
        }
        if (dto.getEntrepotDestinationId() != null) {
            Entrepot e = new Entrepot();
            e.setId(dto.getEntrepotDestinationId());
            transfert.setEntrepotDestination(e);
        }
        return TransfertStockDto.fromEntity(transfertStockRepository.save(transfert));
    }

    @Override
    public TransfertStockDto findById(Integer id) {
        return transfertStockRepository.findById(id)
                .map(TransfertStockDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Transfert non trouvé avec l'ID " + id, ErrorsCode.TRANSFERT_STOCK_NOT_FOUND));
    }

    @Override
    public TransfertStockDto findByCode(String code) {
        TransfertStock ts = transfertStockRepository.findByCode(code);
        if (ts == null) throw new EntityNotFoundException("Transfert non trouvé avec le code " + code, ErrorsCode.TRANSFERT_STOCK_NOT_FOUND);
        return TransfertStockDto.fromEntity(ts);
    }

    @Override
    public List<TransfertStockDto> findAll() {
        return transfertStockRepository.findAll().stream().map(TransfertStockDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<TransfertStockDto> findByEntreprise(Integer entrepriseId) {
        return transfertStockRepository.findByEntrepriseId(entrepriseId).stream().map(TransfertStockDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<TransfertStockDto> findByStatut(TransfertStock.StatutTransfert statut) {
        return transfertStockRepository.findByStatut(statut).stream().map(TransfertStockDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<TransfertStockDto> findByEntrepotSource(Integer entrepotId) {
        return transfertStockRepository.findByEntrepotSourceId(entrepotId).stream().map(TransfertStockDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<TransfertStockDto> findByEntrepotDestination(Integer entrepotId) {
        return transfertStockRepository.findByEntrepotDestinationId(entrepotId).stream().map(TransfertStockDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        transfertStockRepository.deleteById(id);
    }
}
