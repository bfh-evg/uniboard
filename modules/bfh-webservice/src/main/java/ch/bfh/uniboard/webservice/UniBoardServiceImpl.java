/*
 * Copyright (c) 2013 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project UniBoard.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package ch.bfh.uniboard.webservice;

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.Between;
import ch.bfh.uniboard.service.Constraint;
import ch.bfh.uniboard.service.Equals;
import ch.bfh.uniboard.service.GetService;
import ch.bfh.uniboard.service.Greater;
import ch.bfh.uniboard.service.GreaterEquals;
import ch.bfh.uniboard.service.In;
import ch.bfh.uniboard.service.Less;
import ch.bfh.uniboard.service.LessEquals;
import ch.bfh.uniboard.service.PostElement;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.ResultContainer;
import ch.bfh.uniboard.webservice.data.AttributesDTO;
import ch.bfh.uniboard.webservice.data.AttributesDTO.EntryDTO;
import ch.bfh.uniboard.webservice.data.BetweenDTO;
import ch.bfh.uniboard.webservice.data.ConstraintDTO;
import ch.bfh.uniboard.webservice.data.EqualsDTO;
import ch.bfh.uniboard.webservice.data.GreaterDTO;
import ch.bfh.uniboard.webservice.data.GreaterEqualsDTO;
import ch.bfh.uniboard.webservice.data.InDTO;
import ch.bfh.uniboard.webservice.data.LessDTO;
import ch.bfh.uniboard.webservice.data.LessEqualsDTO;
import ch.bfh.uniboard.webservice.data.PostDTO;
import ch.bfh.uniboard.webservice.data.QueryDTO;
import ch.bfh.uniboard.webservice.data.ResultContainerDTO;
import ch.bfh.uniboard.webservice.data.ResultDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Stateless
public class UniBoardServiceImpl implements UniBoardService {

    @EJB
    private PostService postSuccessor;
    @EJB
    private GetService getSuccessor;

    @Override
    public ResultContainerDTO get(QueryDTO query) {

        List<Constraint> constraints = new ArrayList<>();
        for (ConstraintDTO cDTO : query.getBetweenOrInOrLess()) {
            if (cDTO instanceof BetweenDTO) {
                BetweenDTO cTmp = (BetweenDTO) cDTO;
                Between cNew = new Between(cTmp.getStart(), cTmp.getEnd(), cTmp.getKeys(),
                        PostElement.valueOf(cTmp.getPostElement().value()));
                constraints.add(cNew);
            } else if (cDTO instanceof EqualsDTO) {
                EqualsDTO cTmp = (EqualsDTO) cDTO;
                Equals cNew = new Equals(cTmp.getValue(), cTmp.getKeys(),
                        PostElement.valueOf(cTmp.getPostElement().value()));
                constraints.add(cNew);
            } else if (cDTO instanceof GreaterDTO) {
                GreaterDTO cTmp = (GreaterDTO) cDTO;
                Greater cNew = new Greater(cTmp.getValue(), cTmp.getKeys(),
                        PostElement.valueOf(cTmp.getPostElement().value()));
                constraints.add(cNew);
            } else if (cDTO instanceof GreaterEqualsDTO) {
                GreaterEqualsDTO cTmp = (GreaterEqualsDTO) cDTO;
                GreaterEquals cNew = new GreaterEquals(cTmp.getValue(), cTmp.getKeys(),
                        PostElement.valueOf(cTmp.getPostElement().value()));
                constraints.add(cNew);
            } else if (cDTO instanceof InDTO) {
                InDTO cTmp = (InDTO) cDTO;
                In cNew = new In(cTmp.getSet(), cTmp.getKeys(),
                        PostElement.valueOf(cTmp.getPostElement().value()));
                constraints.add(cNew);
            } else if (cDTO instanceof LessDTO) {
                LessDTO cTmp = (LessDTO) cDTO;
                Less cNew = new Less(cTmp.getValue(), cTmp.getKeys(),
                        PostElement.valueOf(cTmp.getPostElement().value()));
                constraints.add(cNew);
            } else if (cDTO instanceof LessEqualsDTO) {
                LessEqualsDTO cTmp = (LessEqualsDTO) cDTO;
                LessEquals cNew = new LessEquals(cTmp.getValue(), cTmp.getKeys(),
                        PostElement.valueOf(cTmp.getPostElement().value()));
                constraints.add(cNew);
            }
        }
        Query q = new Query(constraints);

        ResultContainer rContainer = this.getSuccessor.get(q);

        ResultDTO result = new ResultDTO();

        for (ch.bfh.uniboard.service.Post p : rContainer.getResult()) {
            PostDTO pNew = new PostDTO();
            pNew.setAlpha(this.convertAttributesToDTO(p.getAlpha()));
            pNew.setBeta(this.convertAttributesToDTO(p.getBeta()));
            pNew.setMessage(p.getMessage());
            result.getPost().add(pNew);
        }

        ResultContainerDTO resultContainer = new ResultContainerDTO();
        resultContainer.setResult(result);
        resultContainer.setGamma(this.convertAttributesToDTO(rContainer.getGamma()));
        return resultContainer;
    }

    @Override
    public AttributesDTO post(byte[] message, AttributesDTO alpha) {

        Attributes alphaIntern = new Attributes();
        for (EntryDTO e : alpha.getEntry()) {
            alphaIntern.add(e.getKey(), e.getValue());
        }

        Attributes betaIntern = new Attributes();

        betaIntern = this.postSuccessor.post(message, alphaIntern, betaIntern);

        return this.convertAttributesToDTO(betaIntern);
    }

    private AttributesDTO convertAttributesToDTO(Attributes attributes) {

        AttributesDTO aDTO = new AttributesDTO();
        for (Map.Entry<String, String> e : attributes.getEntries()) {
            EntryDTO ent = new EntryDTO();
            ent.setKey(e.getKey());
            ent.setValue(e.getValue());
            aDTO.getEntry().add(ent);
        }
        return aDTO;
    }

}
