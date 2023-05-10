package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.MvtStk;
import com.devtech.gestiondestock.model.SourceMvtStk;
import com.devtech.gestiondestock.model.TypeMvt;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class MvtStkDto {
    private Integer id;
    private Instant dateMvt;
    private BigDecimal quantite;
    private ArticleDto article;
    private TypeMvt typeMvt;
    private Integer identreprise;
    private SourceMvtStk sourceMvt;

    public static MvtStkDto fromEntity(MvtStk mvtStk){
        if (mvtStk == null){
            return null;
        }
        return MvtStkDto.builder()
                .id(mvtStk.getId())
                .dateMvt(mvtStk.getDateMvt())
                .quantite(mvtStk.getQuantite())
                .article(ArticleDto.fromEntity(mvtStk.getArticle()))
                .typeMvt(mvtStk.getTypeMvt())
                .sourceMvt(mvtStk.getSourceMvt())
                .identreprise(mvtStk.getIdentreprise())
                .build();
    }

    public static MvtStk toEntity(MvtStkDto mvtStkDto){
        if (mvtStkDto == null){
            return null;
        }

        MvtStk mvtStk = new MvtStk();

        mvtStk.setId(mvtStkDto.getId());
        mvtStk.setDateMvt(mvtStkDto.getDateMvt());
        mvtStk.setQuantite(mvtStkDto.getQuantite());
        mvtStk.setArticle(ArticleDto.toEntity(mvtStkDto.getArticle()));
        mvtStk.setTypeMvt(mvtStkDto.getTypeMvt());
        mvtStk.setSourceMvt(mvtStkDto.getSourceMvt());
        mvtStk.setIdentreprise(mvtStkDto.getIdentreprise());

        return mvtStk;
    }
}
